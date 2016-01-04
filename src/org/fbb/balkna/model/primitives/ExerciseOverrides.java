package org.fbb.balkna.model.primitives;

import java.util.List;
import org.fbb.balkna.model.utils.TimeUtils;
import org.fbb.balkna.model.utils.XmlConstants;
import static org.fbb.balkna.model.utils.XmlConstants.ITERATIONS;
import static org.fbb.balkna.model.utils.XmlConstants.PAUSE;
import static org.fbb.balkna.model.utils.XmlConstants.REST;
import static org.fbb.balkna.model.utils.XmlConstants.TIME;
import static org.fbb.balkna.model.utils.XmlUtils.getRealChilds;
import org.w3c.dom.Element;

/**
 *
 * @author jvanek
 */
public class ExerciseOverrides {

    private final String targetId;
    //overrides
    private final Integer time; //default time of exercise in seconds
    private final Integer pause; //default time of pause in seconds
    private final Integer iterations; //defualt number of iterations
    private final Integer rest; //defualt time of rest after all iterations

    public ExerciseOverrides(Integer time, Integer pause, Integer iterations, Integer rest, String targetId) {
        if (targetId == null) {
            throw new NullPointerException();
        }
        this.time = time;
        this.pause = pause;
        this.iterations = iterations;
        this.rest = rest;
        this.targetId = targetId;
    }

    static class Override {

        static Override read(Element idOrOVerrides) {
            Override o = new Override();
            List<Element> l = getRealChilds(idOrOVerrides);
            for (Element nn : l) {
                if (nn.getNodeName().equals(TIME)) {
                    o.time = Integer.valueOf(nn.getTextContent());
                } else if (nn.getNodeName().equals(PAUSE)) {
                    o.pause = Integer.valueOf(nn.getTextContent());
                } else if (nn.getNodeName().equals(ITERATIONS)) {
                    o.iterations = Integer.valueOf(nn.getTextContent());
                } else if (nn.getNodeName().equals(REST)) {
                    o.rest = Integer.valueOf(nn.getTextContent());
                }
            }
            return o;
        }

        Integer time = null;
        Integer pause = null;
        Integer iterations = null;
        Integer rest = null;

        public Override() {
        }

        private void copyApplicable(Override defaultOverride) {
            if (time == null){
                time = defaultOverride.time;
            }
            if (pause == null){
                pause = defaultOverride.pause;
            }
            if (iterations == null){
                iterations = defaultOverride.iterations;
            }
            if (rest == null){
                rest = defaultOverride.rest;
            }
        }

    }

    static ExerciseOverrides parse(Element excercise) {
        return parse(excercise, new Override());
    }
    static ExerciseOverrides parse(Element excercise, Override defaultOverride) {
        String targetId = null;
        Override o = new Override();
        //jdk6:(
        List<Element> idAndOverrides = getRealChilds(excercise);
        for (Element idOrOVerrides : idAndOverrides) {
            if (idOrOVerrides.getNodeName().equals(XmlConstants.ID)) {
                targetId = idOrOVerrides.getTextContent();
            } else if (idOrOVerrides.getNodeName().equals(XmlConstants.OVERRIDES)) {
                o = Override.read(idOrOVerrides);

            }
        }
        if (targetId == null) {
            System.err.println("no id for " + excercise.toString());
        }
        o.copyApplicable(defaultOverride);
        return new ExerciseOverrides(o.time, o.pause, o.iterations, o.rest, targetId);
    }

    public Integer getIterations() {
        return iterations;
    }

    public Integer getPause() {
        return pause;
    }

    public Integer getRest() {
        return rest;
    }

    public Integer getTime() {
        return time;
    }

    public String getTargetId() {
        return targetId;
    }

    ExerciseOverrides transform(TrainingOverrides override) {
        Exercise src = Exercises.getInstance().getExerciseById(targetId);
        Integer t = time;
        Integer p = pause;
        Integer it = iterations;
        Integer r = rest;
        String id = targetId;

        if (t == null) {
            t = src.getTime();
        }
        if (p == null) {
            p = src.getPause();
        }
        if (it == null) {
            it = src.getIterations();
        }
        if (r == null) {
            r = src.getRest();
        }
        double tt = (double) t * override.getTime();
        double pp = (double) p * override.getPause();
        double iitt = (double) it * override.getIterations();
        double rr = (double) r * override.getRest();
        return new ExerciseOverrides((int) tt, (int) pp, (int) iitt, (int) rr, targetId);
    }

    public static ExerciseOverrides fakeFromString(String s) {
        return fakeFromString(s, "TmpPreview");
    }

    public static ExerciseOverrides fakeFromString(String s, String id) {
        Integer time = null;
        Integer pause = null;
        Integer iterations = null;
        Integer rest = null;
        String[] ss = s.split("\\W+");
        if (ss.length == 4) {
            time = stringToIntSafe(ss[0]);
            pause = stringToIntSafe(ss[1]);
            iterations = stringToIntSafe(ss[2]);
            rest = stringToIntSafe(ss[3]);
        }
        return new ExerciseOverrides(time, pause, iterations, rest, id);

    }

    private static Integer stringToIntSafe(String s) {
        try {
            return Integer.valueOf(s.trim());
        } catch (Throwable t) {
            return null;
        }
    }

    public String format() {
        String s = "";
        if (time != null) {
            s += TimeUtils.secondsToMinutes(time);
        } else {
            s += " --";
        }

        if (pause != null) {
            s += " " + TimeUtils.secondsToMinutes(pause);
        } else {
            s += " --";
        }

        if (iterations != null) {
            s += " " + iterations + "x";
        } else {
            s += " --";
        }

        if (rest != null) {
            s += " " + TimeUtils.secondsToMinutes(rest);
        } else {
            s += " --";
        }

        return s;
    }

}

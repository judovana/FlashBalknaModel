/*
 * To change merged license header, choose License Headers in Project Properties.
 * To change merged template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fbb.balkna.model.merged;

import java.util.ArrayList;
import java.util.List;
import org.fbb.balkna.model.Translator;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BasicTime;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BigRestTime;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.PausaTime;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.TrainingTime;
import org.fbb.balkna.model.primitives.Exercise;
import org.fbb.balkna.model.primitives.ExerciseOverrides;
import org.fbb.balkna.model.primitives.Exercises;
import org.fbb.balkna.model.primitives.TimeShift;
import org.fbb.balkna.model.primitives.Training;
import org.fbb.balkna.model.utils.TimeUtils;

/**
 *
 * @author jvanek
 */
public class MergedExercise {

    //inherited
    private Exercise parent;
    //merged
    private Integer time; //default time of exercise in seconds
    private Integer iterations; //defualt number of iterations
    private Integer rest; //defualt time of rest after all iterations
    //merged and substituted if neededs
    private Integer pause; //default time of pause in seconds

  

    public static MergedExercise MergedExercise(ExerciseOverrides over, TimeShift t) {
        return MergedExercise(Exercises.getInstance().getExerciseById(over.getTargetId()), over, t);
    }

    private static MergedExercise MergedExercise(Exercise deflt, ExerciseOverrides over, TimeShift t) {
        MergedExercise merged = new MergedExercise();
        merged.parent = deflt;

        merged.time = over.getTime();
        merged.iterations = over.getIterations();
        merged.rest = over.getRest();
        merged.pause = over.getPause();

        if (merged.time == null) {
            merged.time = deflt.getTime();
        }
        if (merged.iterations == null) {
            merged.iterations = deflt.getIterations();
        }
        if (merged.rest == null) {
            merged.rest = deflt.getRest();
        }

        if (merged.pause == null) {
            merged.pause = deflt.getPause();
        }

        if (merged.rest == null) {
            merged.rest = merged.pause;
        }

        merged.time = (int) Math.round(t.getTraining() * (double) merged.time);
        merged.iterations = (int) Math.round(t.getIterations()* (double) merged.iterations);
        merged.rest = (int) Math.round(t.getRest() * (double) merged.rest);
        merged.pause = (int) Math.round(t.getPause() * (double) merged.pause);

        return merged;

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

    public Exercise getOriginal() {
        return parent;
    }

    public String getStory(int i, boolean html) {
        String n = i + ") " + getOriginal().getName() + ":";
        if (html) {
            n = "<h3>" + n + "</h3>";
        }
        n += breakLine(html) + toString(html);
        return n;
    }

    public String toString(boolean html) {
        String s = Translator.R("train", TimeUtils.secondsToMinutes(getTime())) + breakLine(html)
                + Translator.R("rest", TimeUtils.secondsToMinutes(getPause())) + breakLine(html)
                + Translator.R("iterations", getIterations()) + breakLine(html)
                + Translator.R("finalPause", TimeUtils.secondsToMinutes(getRest())) + breakLine(html)
                + getOriginal().getDescription() + breakLine(html)
                + getImageAsTExt(html);

        return s;
    }

    private String breakLine(boolean html) {
        if (html) {
            return "<br>\n";
        }
        return "\n";
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public List<BasicTime> decompress() {
        List<BasicTime> r = new ArrayList<BasicTime>();
        if (iterations == 0) {
            return r;
        }
        for (int i = 1; i < iterations; i++) {
            r.add(new TrainingTime(getTime(), this));
            r.add(new PausaTime(getPause(), this));
        }
        r.add(new TrainingTime(getTime(), this));
        r.add(new BigRestTime(getRest(), this));
        return r;
    }

    private String getImageAsTExt(boolean html) {
        StringBuilder sb = new StringBuilder();
        List<String> iims = getOriginal().getImages();
        for (String iim : iims) {
            if (html) {
                sb.append("<a href='" + Training.IMGS_SUBDIR + "/").append(iim)
                        .append("'>  <img src='" + Training.IMGS_SUBDIR + "/")
                        .append(iim)
                        .append("' width='100' height='100'>  </a>");
            } else {
                sb.append(iim).append(";");
            }
        }
        return sb.toString();
    }

}

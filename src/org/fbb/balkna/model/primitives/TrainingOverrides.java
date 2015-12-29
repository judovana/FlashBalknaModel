package org.fbb.balkna.model.primitives;

import java.util.List;
import org.fbb.balkna.model.Translator;
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
public class TrainingOverrides {

    private final String targetId;
    //overrides
    private final Double time; // time of exercise multiplyer
    private final Double pause; //time of pause multiplyer
    private final Double iterations; //number of iterations multiplyer
    private final Double rest; //time of rest after all iterations multiplyer
    
    private final boolean restday;

    public TrainingOverrides(Double time, Double pause, Double iterations, Double rest, String targetId, boolean sb) {
        if (targetId == null) {
            throw new NullPointerException();
        }
        this.time = time;
        this.pause = pause;
        this.iterations = iterations;
        this.rest = rest;
        this.targetId = targetId;
        this.restday = sb;
    }

    static TrainingOverrides parse(Element excercise) {
        String targetId = null;
        Double time = null;
        Double pause = null;
        Double iterations = null;
        Double rest = null;
        boolean restday = false;
        //jdk6:(
        List<Element> idAndOverrides = getRealChilds(excercise);
        for (Element idOrOVerrides : idAndOverrides) {
            if (idOrOVerrides.getNodeName().equals(XmlConstants.ID)) {
                targetId = idOrOVerrides.getTextContent();
            } else if (idOrOVerrides.getNodeName().equals(XmlConstants.SUGGESTBREAK)) {
                restday = Boolean.valueOf(idOrOVerrides.getTextContent().trim());
            } else if (idOrOVerrides.getNodeName().equals(XmlConstants.CHANGES)) {
                List<Element> l = getRealChilds(idOrOVerrides);
                for (Element nn : l) {
                    if (nn.getNodeName().equals(TIME)) {
                        time = Double.valueOf(nn.getTextContent());
                    } else if (nn.getNodeName().equals(PAUSE)) {
                        pause = Double.valueOf(nn.getTextContent());
                    } else if (nn.getNodeName().equals(ITERATIONS)) {
                        iterations = Double.valueOf(nn.getTextContent());
                    } else if (nn.getNodeName().equals(REST)) {
                        rest = Double.valueOf(nn.getTextContent());
                    }
                }
            }
        }
        if (targetId == null) {
            System.err.println("no id for " + excercise.toString());
        }
        if (time == null) {
            time = 1d;
        }
        if (pause == null) {
            pause = 1d;
        }
        if (iterations == null) {
            iterations = 1d;
        }
        if (rest == null) {
            rest = 1d;
        }
        return new TrainingOverrides(time, pause, iterations, rest, targetId, restday);
    }

    public Training getTraining() {
        Training base = Trainings.getInstance().getTrainingById(targetId);
        if (base == null) {
            return null;
        }
        return base.transform(this);

    }

    public Double getIterations() {
        return iterations;
    }

    public Double getPause() {
        return pause;
    }

    public Double getRest() {
        return rest;
    }

    public Double getTime() {
        return time;
    }

    public String getTargetId() {
        return targetId;
    }

    @Override
    public String toString() {
        return getHeader(0, false);
    }

    String getHeader(int i, boolean html) {

        String n = i + ") " + Trainings.getInstance().getTrainingById(targetId).getName();
        n += breakLine(html) + toString(html);
        return n;
    }

    public String toString(boolean html) {
        String s = Translator.R("train", (getTime())) + breakLine(html)
                + Translator.R("rest", (getPause())) + breakLine(html)
                + Translator.R("iterations", getIterations()) + breakLine(html)
                + Translator.R("finalPause", (getRest())) + breakLine(html);
        if (restday){
            if(html){
                s+="<i>";
            }
            s +=  " ** " + Translator.R("restDay") + " ** "+ breakLine(html);
            if(html){
                s+="</i>"+breakLine(html);
            }
        }
        return s;
    }

    private String breakLine(boolean html) {
        if (html) {
            return "<br>\n";
        }
        return "\n";
    }

}

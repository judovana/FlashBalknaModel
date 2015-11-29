package org.fbb.balkna.model.primitives;

import java.util.List;
import static org.fbb.balkna.model.utils.XmlConstants.ITERATIONS;
import static org.fbb.balkna.model.utils.XmlConstants.PAUSE;
import static org.fbb.balkna.model.utils.XmlConstants.REST;
import static org.fbb.balkna.model.utils.XmlConstants.TIME;
import org.fbb.balkna.model.utils.XmlUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author jvanek
 */
public class SetDefaults {

    private static final SetDefaults empty = new SetDefaults(null, null, null, null);

    private final Integer time; //default time of exercise in seconds
    private final Integer pause; //default time of pause in seconds
    private final Integer iterations; //defualt number of iterations
    private final Integer rest; //defualt time of rest after all iterations

    private SetDefaults(Integer t, Integer p, Integer i, Integer r) {
        time = t;
        pause = p;
        iterations = i;
        rest = r;
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

    static SetDefaults emptyDeafults() {
        return empty;
    }

    public static SetDefaults parse(final Node node) {
        Integer time = null;
        Integer pause = null;
        Integer iterations = null;
        Integer rest = null;
        List<Element> interlayer = XmlUtils.getRealChilds(node);
        for (Element nn : interlayer) {
            //jdk6 comaptible:(
            if (nn.getNodeName().equals(TIME)) {
                time = Integer.valueOf(nn.getTextContent());
            } else if (nn.getNodeName().equals(PAUSE)) {
                pause = Integer.valueOf(nn.getTextContent());
            } else if (nn.getNodeName().equals(ITERATIONS)) {
                iterations = Integer.valueOf(nn.getTextContent());
            } else if (nn.getNodeName().equals(REST)) {
                rest = Integer.valueOf(nn.getTextContent());
            }
        }
        return new SetDefaults(time, pause, iterations, rest);
    }

}

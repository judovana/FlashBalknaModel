package org.fbb.balkna.model.primitives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fbb.balkna.model.utils.XmlConstants;
import org.fbb.balkna.model.utils.XmlUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author jvanek
 */
public class Trainings {

    private static class TrainingsHolder {

        public static Trainings instance;

        public static Trainings getInstance() {
            if (instance == null) {
                instance = new Trainings();
            }
            return instance;
        }

        public static Trainings reloadInstance() {
            instance = new Trainings();
            return instance;
        }
    }

    public static Trainings getInstance() {
        return TrainingsHolder.getInstance();
    }

    public static Trainings reloadInstance() {
        return TrainingsHolder.reloadInstance();
    }

    private final List<Training> trainings = new ArrayList<Training>();

    public Trainings() {
        try {
            loadDefaults();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadDefaults() {
        try {
            List<Element> all = XmlUtils.getDataNodes("trainings", XmlConstants.TRAINING);
            for (Node node : all) {
                Training ex = Training.parse(node);
                trainings.add(ex);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public List<Training> getTrainings() {
        return Collections.unmodifiableList(trainings);
    }

}

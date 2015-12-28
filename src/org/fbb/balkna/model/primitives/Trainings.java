package org.fbb.balkna.model.primitives;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.primitives.history.Record;
import org.fbb.balkna.model.primitives.history.RecordWithOrigin;
import org.fbb.balkna.model.utils.XmlConstants;
import org.fbb.balkna.model.utils.XmlUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author jvanek
 */
public class Trainings {

      public List<RecordWithOrigin> gatherStatistics() {
        String[] names = getStatsDir().list();
        ArrayList<RecordWithOrigin> a = new ArrayList<RecordWithOrigin>(names.length);
        for (String name : names) {
            Training i = getTrainingById(name);
            if (i != null) {
                List<Record> rs = i.getRecords();
                for (Record r : rs) {
                    a.add(new RecordWithOrigin(i, r));
                }
            }
        }
        return a;
    }

    public List<RecordWithOrigin> gatherFakeTrainingsStatistics() {
        String[] names = getStatsDir().list();
        ArrayList<RecordWithOrigin> a = new ArrayList<RecordWithOrigin>(names.length);
        for (String name : names) {
            Exercise e = Exercises.getInstance().getExerciseById(name);
            if (e != null) {
                Training i = new Training(e);
                List<Record> rs = i.getRecords();
                for (Record r : rs) {
                    a.add(new RecordWithOrigin(i, r));
                }
            }
        }
        return a;
    }

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
    
    public Training getTrainingById(String id) {
        for (Training training : trainings) {
            if (training.getId().equals(id)){
                return  training;
            }
        }
        return null;
    }
    
      public static File getStatsDir() {
        File f = new File(Model.getModel().getStatsDir(), "trainings");
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

}

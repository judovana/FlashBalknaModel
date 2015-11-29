package org.fbb.balkna.model.primitives;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fbb.balkna.model.utils.XmlConstants;
import org.fbb.balkna.model.utils.XmlUtils;
import org.w3c.dom.Element;

/**
 *
 * @author jvanek
 */
public class Exercises {

    public static final String WARM_UP_ID = "internalWarmUp";

    private static class ExercisesHolder {

        public static Exercises instance;

        public static Exercises getInstance() {
            if (instance == null) {
                instance = new Exercises();
            }
            return instance;
        }

        public static Exercises reloadInstance() {
            instance = new Exercises();
            return instance;
        }
    }

    public static Exercises getInstance() {
        return ExercisesHolder.getInstance();
    }

    public static Exercises reloadInstance() {
        return ExercisesHolder.reloadInstance();
    }

    private final Map<String, Exercise> exercises = new HashMap<String, Exercise>();

    public Exercises() {
        try {
            loadDefaults();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Exercise getExerciseById(String id) {
        return getInstance().exercises.get(id);

    }

    public Exercise getWarmUpExercise() {
        return getExerciseById(WARM_UP_ID);

    }

    private void loadDefaults() {
        try {
            List<Element> sets = XmlUtils.getDataNodes("exercises", XmlConstants.EXERCISES_SET);
            for (Element set : sets) {
                List<Element> childs = XmlUtils.getRealChilds(set);
                SetDefaults currentDefaults = SetDefaults.emptyDeafults();
                //first found defaults
                for (Element child : childs) {
                    if (child.getNodeName().equals(XmlConstants.SET_DEFAULTS)) {
                        currentDefaults = SetDefaults.parse(child);
                    }
                }
                //now found all exercises, set defaults if exists
                for (Element child : childs) {
                    if (child.getNodeName().equals(XmlConstants.EXERCISE)) {

                        Exercise ex = Exercise.parse(child, currentDefaults);
                        Exercise old = exercises.put(ex.getId(), ex);
                        if (old != null) {
                        //throw new RuntimeException("Exercise id " + ex.getId() + " already presented. thats fatal");
                            //overwriting is ok. "update"
                        }
                    }
                }
            }
        }catch(Exception ex){
            throw  new RuntimeException(ex);
        }
    }

}
package org.fbb.balkna.model.primitives.history;

import org.fbb.balkna.model.Statisticable;
import org.fbb.balkna.model.Substituable;
import org.fbb.balkna.model.merged.uncompressed.MainTimer;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BasicTime;
import org.fbb.balkna.model.primitives.Cycle;
import org.fbb.balkna.model.primitives.Exercise;
import org.fbb.balkna.model.primitives.Training;
import org.fbb.balkna.model.settings.Settings;
import org.fbb.balkna.model.utils.TimeUtils;

/**
 *
 * @author jvanek
 */
public class StatisticHelper {


    private final Statisticable parent;

    public StatisticHelper(Statisticable parent) {
        this.parent = parent;
    }

    public void finishedWithSkips(String message) {
        parent.addRecord(Record.create(RecordType.FINISHED_WITH_SKIPPS, message));
    }

    public void finished(String message) {
        parent.addRecord(Record.create(RecordType.FINISHED, message));
    }

    public void canceled(String message) {
        parent.addRecord(Record.create(RecordType.CANCELED, message));
    }

    public void started(String message) {
        parent.addRecord(Record.create(RecordType.STARTED, message));
    }

    public void continued(String message) {
        parent.addRecord(Record.create(RecordType.CONTINUED, message));
    }

    public void modified(String message) {
        parent.addRecord(Record.create(RecordType.MODIFIED, message));
    }

    private static String substituableToString(Substituable src2) {
        return (src2 == null ? "" : " - " + src2.getIdAsMcro());
    }

    private static String generateTrainingsTitle(Cycle c, Training t, Exercise ex) {
        return "cycle: " + substituableToString(c)
                + "; training: " + substituableToString(t)
                + "; exercise: " + substituableToString(ex);

    }
    
     public static String generateMessage(Cycle c, Training t, Exercise ex) {
         return generateTrainingsTitle(c, t, ex)+"; "+generateModifiers();
     }

    private static String generateCurrentTime(BasicTime t) {
        if (t == null) {
            return "current: ";
        } else {
            return "current: " + TimeUtils.secondsToHours(t.getCurrentValue());
        }
    }

    public static String generateTime(BasicTime t, MainTimer model) {
        return generateCurrentTime(t) + "; " + generateTotalTime(t, model);
    }

    private static String generateTotalTime(BasicTime t, MainTimer model) {
        if (model == null) {
            return "totoal: ";
        } else {
            return "total: " + TimeUtils.getRemainingTime(t, model);
        }
    }

    public static String generateMessage(Cycle c, Training tr, MainTimer model) {
        try {
            return StatisticHelper.generateMessage(c, tr, model.getCurrent().getOriginator().getOriginal(), model.getCurrent(), model, model.getSkipps());
        } catch (Exception exx) {
            return exx.toString();
        }
    }

    public static String generateMessage(Cycle c, Training tr, Exercise ex, BasicTime ti, MainTimer model, Integer skipps) {
        try {
            return generateTrainingsTitle(c, tr, ex) + "; " + generateTime(ti, model) + "; skipps" + intToStrSafe(skipps) + "; " + generateModifiers();
        } catch (Exception exx) {
            return exx.toString();
        }
    }

    private static String intToStrSafe(Integer src2) {
        return (src2 == null ? "" : " - " + src2.toString());
    }
    
    private static String generateModifiers() {
        return "TimeShift: "+ Settings.getSettings().getTimeShift().statInfo()+"; "+Settings.getSettings().getSingleExerciseOverrideInfo().replaceFirst("=", ": ");
    }
}

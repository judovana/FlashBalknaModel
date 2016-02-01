package org.fbb.balkna.model.graphs;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.fbb.balkna.model.primitives.Exercise;
import org.fbb.balkna.model.primitives.Training;

/**
 *
 * @author jvanek
 */
public class DayTimes implements Steppable<DayTimes> {

    private final Date day;
    private int cyclesOperationms = 0;
    private int trainTime = 0;
    private int exercisesTimes = 0;
    private SimpleDateFormat easyFormater;
    private long step;

    public DayTimes(Date day) {
        this.day = day;
    }

    public static DayTimes fake(Date day) {
        DayTimes d = new DayTimes(day);
        d.detach();
        return d;
    }

    public boolean isNothing() {
        return isNothing(cyclesOperationms)
                && isNothing(trainTime)
                && isNothing(exercisesTimes);
    }

    private static boolean isNothing(int i) {
        return i < 0;
    }

    public void detach() {
        cyclesOperationms = -1;
        trainTime = -1;
        exercisesTimes = -1;
    }

    /**
     * @return the day
     */
    public Date getDay() {
        return day;
    }

    public void incCycles(int i) {
        cyclesOperationms += i;
    }

    public void incTrains(int c) {//seconds
        trainTime += c;
    }

    public void incExs(int c) {//seconds
        exercisesTimes += c;
    }

    public int getCyclesOperationms() {
        return cyclesOperationms;
    }

    public int getExercisesTimes() {//seconds
        return exercisesTimes;
    }

    public int getTrainTime() {//seconds
        return trainTime;
    }
    
    public int getExercisesTimesMinutes() {
        return exercisesTimes/60;
    }

    public int getTrainTimeMinutes() {
        return trainTime/60;
    }
    

    @Override
    public int compareTo(DayTimes o) {
        return (int) (this.day.getTime() - o.day.getTime());
    }

    public int getMaxTime() { //not exacly usefull to mix with cycles...
        return Math.max(trainTime, exercisesTimes);
    }

    @Override
    public long getStep() {
        return step;
    }

    @Override
    public void setStep(long step) {
        this.step = step;
    }

    @Override
    public void setEasyFormater(SimpleDateFormat easyFormater) {
        this.easyFormater = easyFormater;
    }

    @Override
    public SimpleDateFormat getEasyFormater() {
        return easyFormater;
    }

    public String getAdaptedDateTime() {
        return easyFormater.format(day);
    }

    void incExTr(Class clazz, long realTime) {
        if (clazz.equals(Training.class)) {
            incTrains((int) realTime);
        } else if (clazz.equals(Exercise.class)) {
            incExs((int) realTime);
        } else {
            throw new RuntimeException("Unsupported class " + clazz);
        }
    }

    void detachCy() {
        cyclesOperationms=-1;
    }

    void detachTr() {
        trainTime=-1;
    }

    void detachEx() {
        exercisesTimes=-1;
    }
    
     @Override
    public String toString() {
        String s = day.toString();
        if (getEasyFormater() != null) {
            s = getAdaptedDateTime();
        }
        s += ":  cycles: " + cyclesOperationms+"x, trainings: "+trainTime+"s, exercises: "+exercisesTimes+"s.";
        return s;
    }

    
}

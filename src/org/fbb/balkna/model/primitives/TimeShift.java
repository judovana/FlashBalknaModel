package org.fbb.balkna.model.primitives;

import static org.fbb.balkna.model.Translator.R;

/**
 *
 * @author jvanek
 */
public class TimeShift {

    private double training;
    private double pause;
    private double rest;
    private double iterations;

    public TimeShift() {
        this.training = 1d;
        this.pause = 1d;
        this.rest = 1d;
        this.iterations = 1d;
    }

    public TimeShift(TimeShift d) {
        this.training = d.training;
        this.pause = d.pause;
        this.rest = d.rest;
        this.iterations = d.iterations;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TimeShift)) {
            return false;
        }
        TimeShift o = (TimeShift) obj;
        return o.pause == pause
                && o.rest == rest
                && o.training == training
                && o.iterations == iterations;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.training) ^ (Double.doubleToLongBits(this.training) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.pause) ^ (Double.doubleToLongBits(this.pause) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.rest) ^ (Double.doubleToLongBits(this.rest) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.iterations) ^ (Double.doubleToLongBits(this.iterations) >>> 32));
        return hash;
    }

    /**
     * @return the training
     */
    public double getTraining() {
        return training;
    }

    /**
     * @param training the training to set
     */
    public void setTraining(double training) {
        this.training = training;
    }

    /**
     * @return the pause
     */
    public double getPause() {
        return pause;
    }

    /**
     * @param pause the pause to set
     */
    public void setPause(double pause) {
        this.pause = pause;
    }

    /**
     * @return the rest
     */
    public double getRest() {
        return rest;
    }

    /**
     * @param rest the rest to set
     */
    public void setRest(double rest) {
        this.rest = rest;
    }

    public double getIterations() {
        return iterations;
    }

    public void setIterations(double iterations) {
        this.iterations = iterations;
    }

    @Override
    public String toString() {
        return R("TrainingsShift") + ": " + training + ", "
                + R("PausesShift") + ": " + pause + ", "
                + R("IterationsShift") + ": " + iterations + ", "
                + R("RestsShift") + ": " + rest;
    }

    public String statInfo() {
        return "T:" + training + ","
                + "P:" + pause + ","
                + "I:" + iterations + ","
                + "R:" + rest;
    }

}

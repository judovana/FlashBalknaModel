package org.fbb.balkna.model.merged.uncompressed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fbb.balkna.model.Translator;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BasicTime;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BigRestTime;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.PausaTime;
import org.fbb.balkna.sun.Timeable;
import org.fbb.balkna.sun.Timer;

/**
 *
 * @author jvanek
 */
public class MainTimer implements Timeable {

    private final List<BasicTime> src;
    private int index;
    private Runnable exerciseShiftedListener;
    private Runnable oneTenthOfSecondListener;
    private Runnable secondListener;
    private int skipps;

    private int tenthOfSecond;

    private final Timer timer;

    public MainTimer(List<BasicTime> src) {
        this.skipps = 0;
        this.src = Collections.unmodifiableList(src);
        this.index = 0;
        initTenth();
        timer = new Timer(this, 100);
    }

    public BasicTime getCurrent() {
        return get(index);
    }

    public BasicTime get(int i) {
        return src.get(i);
    }

    public int getIndex() {
        return index;
    }

    private void skipForward() {
        skipForward(false);
    }

    public void skipForward(boolean forced) {
        if (index < src.size() - 1) {
            getCurrent().resetTime();
            initTenth();
            index++;
            if (forced) {
                if (!(getCurrent() instanceof PausaTime)) {
                    skipps++;
                }
            }
            doExerciseShifted();
        }
    }

    private void jumpBack() {
        jumpBack(false);
    }

    public void jumpBack(boolean forced) {
        if (index > 0) {
            getCurrent().resetTime();
            index--;
            if (forced) {
                if (!(getCurrent() instanceof PausaTime)) {
                    skipps--;
                }
            }
            doExerciseShifted();
        }
    }

    public List<BasicTime> getSrc() {
        return Collections.unmodifiableList(src);
    }

    public void setExerciseShifted(Runnable r) {
        this.exerciseShiftedListener = r;
    }

    private void doExerciseShifted() {
        if (exerciseShiftedListener != null) {
            exerciseShiftedListener.run();
        }
    }

    private void doSecondShifted() {
        getCurrent().currentMinusMinus();
        if (secondListener != null) {
            secondListener.run();
        }
        if (getCurrent().getCurrentValue() < 0) {
            skipForward();
        }
    }

    private void doOneTenthSecondShifted() {
        if (oneTenthOfSecondListener != null) {
            oneTenthOfSecondListener.run();
        }
    }

    public void setOneTenthOfSecondListener(Runnable oneTenthOfSecondListener) {
        this.oneTenthOfSecondListener = oneTenthOfSecondListener;
    }

    public void setSecondListener(Runnable secondListener) {
        this.secondListener = secondListener;
    }

    public boolean isEnded() {
        return (index >= src.size() - 1);
    }

    public BasicTime getNext() {
        if (isEnded()) {
            return getCurrent();
        } else {
            return src.get(index + 1);
        }
    }

    public int getTenthOfSecond() {
        return tenthOfSecond;
    }

    @Override
    public void tick(Timer timer) {
        tenthOfSecond--;
        if (tenthOfSecond < 0) {
            tenthOfSecond = 9;
        }
        if (tenthOfSecond == 0) {
            doSecondShifted();
        }
        doOneTenthSecondShifted();
    }

    public void go() {
        timer.cont();
    }

    public void stop() {
        timer.stop();
    }

    public boolean isStopped() {
        return timer.isStopped();
    }

    private void initTenth() {
        tenthOfSecond = 1;
    }

    public int getFutureTime() {
        int x = 0;
        for (int i = index + 1; i < src.size(); i++) {
            BasicTime get = src.get(i);
            x += get.getOriginalValue();

        }
        return x;
    }

    public int getTotalTime() {
        int i = 0;
        for (BasicTime src1 : src) {
            i += src1.getOriginalValue();
        }
        return i;
    }

    public String next() {
        return Translator.R("Next");
    }

    public String now() {
        return Translator.R("Now");
    }

    public boolean isHalfSerie() {
        List<BasicTime> serie = header1();
        if (serie == null) {
            return false;
        }
        int indexInSerie = findNewPosition(serie);

        return indexInSerie == serie.size() / 2;
    }

    public boolean isLastExercise() {
        List<BasicTime> serie = header1();
        if (serie == null) {
            return false;
        }
        int indexInSerie = findNewPosition(serie);

        return indexInSerie == serie.size() - 1;
    }

    public boolean isThreeQatsSerie() {
        List<BasicTime> serie = header1();
        if (serie == null) {
            return false;
        }
        int indexInSerie = findNewPosition(serie);

        return indexInSerie == (3 * serie.size()) / 4;
    }

    public boolean isHalfTraining() {
        List<BasicTime> training = header2();
        if (training == null) {
            return false;
        }
        int indexInSerie = findNewPosition(training);

        return indexInSerie == training.size() / 2;
    }

    public boolean isLastSerie() {
        List<BasicTime> training = header2();
        if (training == null) {
            return false;
        }
        int indexInSerie = findNewPosition(training);

        return indexInSerie == training.size() - 2;
    }

    public boolean isThreeQatsTraining() {
        List<BasicTime> training = header2();
        if (training == null) {
            return false;
        }
        int indexInSerie = findNewPosition(training);

        return indexInSerie == (3 * training.size()) / 4;
    }

    private List<BasicTime> getSerieFromPause() {
        //should be asked form pausa, so caunting only pausas
        List<BasicTime> serie = new ArrayList<BasicTime>();
        int i = getIndex() + 1;
        while (i < src.size() && (!(get(i) instanceof BigRestTime))) {
            if (get(i) instanceof PausaTime) {
                serie.add(get(i));
            }
            i++;
        }
        //0 warm up
        //1 first iteration
        i = getIndex();
        while (i > 0 && (!(get(i) instanceof BigRestTime))) {
            if (get(i) instanceof PausaTime) {
                serie.add(0, get(i));
            }
            i--;
        }
        return serie;
    }

    private List<BasicTime> getTrainingFromRest() {
        List<BasicTime> training = new ArrayList<BasicTime>();
        for (BasicTime b : src) {
            if (b instanceof BigRestTime) {
                training.add(b);
            }
        }
        return training;
    }

    private int findNewPosition(List<BasicTime> serie) {
        BasicTime cur = getCurrent();
        int indexInSerie = getIndex();
        for (int j = 0; j < serie.size(); j++) {
            BasicTime get = serie.get(j);
            if (get == cur) {
                indexInSerie = j;
                break;
            }

        }
        return indexInSerie;
    }

    private List<BasicTime> header1() {
        //0 warm up
        //1 first iteration
        if (getIndex() == 0) {
            return null;
        }
        if (getIndex() >= src.size() - 1) {
            return null;
        }
        List<BasicTime> serie = getSerieFromPause();
        if (serie.size() < 2) {
            return null;
        }
        return serie;
    }

    private List<BasicTime> header2() {
        //0 warm up
        //1 first iteration
        if (getIndex() == 0) {
            return null;
        }
        if (getIndex() >= src.size() - 1) {
            return null;
        }
        List<BasicTime> train = getTrainingFromRest();
        if (train.size() < 2) {
            return null;
        }
        return train;
    }

    public int getSkipps() {
        return skipps;
    }

    public boolean wasSkipped() {
        return skipps != 0;
    }

}

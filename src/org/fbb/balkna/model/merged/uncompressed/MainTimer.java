package org.fbb.balkna.model.merged.uncompressed;

import java.util.Collections;
import java.util.List;
import org.fbb.balkna.model.Translator;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BasicTime;
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

    private int tenthOfSecond;

    private final Timer timer;

    public MainTimer(List<BasicTime> src) {
        this.src = Collections.unmodifiableList(src);
        this.index = 0;
        initTenth();
        timer = new Timer(this, 100);
    }

    public BasicTime getCurrent() {
        return src.get(index);
    }

    public int getIndex() {
        return index;
    }

    public void skipForward() {
        if (index < src.size() - 1) {
            getCurrent().resetTime();
            initTenth();
            index++;
            doExerciseShifted();
        }
    }

    public void jumpBack() {
        if (index > 0) {
            getCurrent().resetTime();
            index--;
            doExerciseShifted();
        }
    }

    public List<BasicTime> getSrc() {
        return src;
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

}

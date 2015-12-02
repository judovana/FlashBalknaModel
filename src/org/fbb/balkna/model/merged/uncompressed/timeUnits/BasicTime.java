package org.fbb.balkna.model.merged.uncompressed.timeUnits;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.SoundProvider;
import org.fbb.balkna.model.Translator;
import org.fbb.balkna.model.merged.MergedExercise;
import org.fbb.balkna.model.utils.IoUtils;
import org.fbb.balkna.model.utils.TimeUtils;

/**
 *
 * @author jvanek
 */
public abstract class BasicTime {

    private final int originalValue;
    private int currentValue;
    private final MergedExercise originator;

    public BasicTime(int originalValue, MergedExercise originator) {
        this.originalValue = originalValue;
        resetTime();
        this.originator = originator;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public int getOriginalValue() {
        return originalValue;
    }

    public MergedExercise getOriginator() {
        return originator;
    }

    @Override
    public String toString() {
        return TimeUtils.secondsToMinutes(currentValue) + "/" + TimeUtils.secondsToMinutes(originalValue);
    }

    public String getHtmlPreview1() {
        StringBuilder sb = new StringBuilder();
        sb.append(getInformaiveTitle())
                .append("<br>")
                .append(TimeUtils.secondsToMinutes(originalValue));

        sb = IoUtils.htmlWrap(sb);
        return sb.toString();
    }

    public String getInformaiveTitle() {
        return originator.getOriginal().getName();
    }

    public String getEndMssage() {
        return Translator.R("Ende");
    }

    public void resetTime() {
        this.currentValue = originalValue;
    }

    public void currentMinusMinus() {
        currentValue--;
    }

    public  void soundLogicRuntime() {
        BasicTime c = this;
        if (!Model.getModel().isLaud()) {
            return;
        }
        if (c.getCurrentValue() - 2 == 0) {
            SoundProvider.getInstance().getPSthree().playAsync();
        } else if (c.getCurrentValue() - 1 == 0) {
            SoundProvider.getInstance().getPStwo().playAsync();
        } else if (c.getCurrentValue() == 0) {
            SoundProvider.getInstance().getPSone().playAsync();
        } else if (c.getCurrentValue() == c.getOriginalValue() / 2) {
            if (c instanceof PausaTime) {
                SoundProvider.getInstance().getPShalfPause().playAsync();
            } else {
                SoundProvider.getInstance().getPShalfRun().playAsync();
            }
        } else if (c.getCurrentValue() == (c.getOriginalValue()) / 4) {
            if (c instanceof PausaTime) {
                SoundProvider.getInstance().getPSthreeQatsPause().playAsync();
            } else {
                SoundProvider.getInstance().getPSthreeQuatsRun().playAsync();
            }
        }
    }
    
    public abstract  void play();

}

package org.fbb.balkna.model.merged.uncompressed.timeUnits;

import java.util.List;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.SoundProvider;
import org.fbb.balkna.model.Translator;
import org.fbb.balkna.model.merged.MergedExercise;
import org.fbb.balkna.model.merged.uncompressed.MainTimer;
import org.fbb.balkna.model.settings.Settings;
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

    public String getHtmlPreview1(boolean markup) {
        StringBuilder sb = new StringBuilder();
        sb.append(getInformaiveTitle());
        if (markup) {
            sb.append("<br>");
        }
        sb.append("\n");
        sb.append(TimeUtils.secondsToMinutes(originalValue));
        if (markup) {
            sb = IoUtils.htmlWrap(sb);
        }
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

    public void soundLogicRuntime(MainTimer model) {
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
        } else if (c.getCurrentValue() == (3 * (c.getOriginalValue())) / 4) {
            if (Settings.getSettings().isPlayLongTermSounds()) {
                if (c instanceof PausaTime) {
                    if (model.isLastExercise()) {
                        SoundProvider.getInstance().getPSlastExercise().playAsync();
                    } else if (model.isHalfSerie()) {
                        SoundProvider.getInstance().getPShalfSerie().playAsync();
                    } else if (model.isThreeQatsSerie()) {
                        SoundProvider.getInstance().getPSthreeQatsSerie().playAsync();
                    }
                }
                if (c instanceof BigRestTime) {
                    if (model.isLastSerie()) {
                        SoundProvider.getInstance().getPSlastSerie().playAsync();
                    } else if (model.isHalfTraining()) {
                        SoundProvider.getInstance().getPShalfTraining().playAsync();
                    } else if (model.isThreeQatsTraining()) {
                        SoundProvider.getInstance().getPSthreeQatsTraining().playAsync();
                    }
                }

            }

        }
    }

    public abstract void play();

    public static void payEnd() {
        if (!Model.getModel().isLaud()) {
            return;
        }
        SoundProvider.getInstance().getPStrainingEnd().playAsync();
    }

}

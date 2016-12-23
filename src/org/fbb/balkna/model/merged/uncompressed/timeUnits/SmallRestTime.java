package org.fbb.balkna.model.merged.uncompressed.timeUnits;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.SoundProvider;
import org.fbb.balkna.model.Translator;
import org.fbb.balkna.model.merged.MergedExercise;

/**
 * when BigRestime should be used, but ids are equals
 *
 * @author jvanek
 */
public class SmallRestTime extends BigRestTime {

    public SmallRestTime(int originalValue, MergedExercise originator) {
        super(originalValue, originator);
    }

    @Override
    public String getInformaiveTitle() {
        return Translator.R("PauseWillChangeSlightly");
    }

    @Override
    public void play() {
        if (Model.getModel().isLaud()) {
            SoundProvider.getInstance().getPSendChangeSmall().playAsync();
        }
    }

}

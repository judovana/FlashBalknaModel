package org.fbb.balkna.model.merged.uncompressed.timeUnits;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.SoundProvider;
import org.fbb.balkna.model.Translator;
import org.fbb.balkna.model.merged.MergedExercise;

/**
 *
 * @author jvanek
 */
public class PausaTime extends BasicTime {

    public PausaTime(int originalValue, MergedExercise originator) {
        super(originalValue, originator);
    }

    @Override
    public String getInformaiveTitle() {
        return Translator.R("pause");
    }

    @Override
    public void play() {
        if (Model.getModel().isLaud()) {
            SoundProvider.getInstance().getPSendRun().playAsync();
        }
    }

}

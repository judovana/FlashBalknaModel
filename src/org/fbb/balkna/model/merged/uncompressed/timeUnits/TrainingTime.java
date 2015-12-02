package org.fbb.balkna.model.merged.uncompressed.timeUnits;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.SoundProvider;
import org.fbb.balkna.model.merged.MergedExercise;

/**
 *
 * @author jvanek
 */
public class TrainingTime extends BasicTime {

    public TrainingTime(int originalValue, MergedExercise originator) {
        super(originalValue, originator);
    }

    @Override
    public void play() {
        if (Model.getModel().isLaud()) {
            SoundProvider.getInstance().getPSstart().playAsync();
        }
    }

}

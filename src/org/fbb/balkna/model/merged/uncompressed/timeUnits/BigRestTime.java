package org.fbb.balkna.model.merged.uncompressed.timeUnits;

import org.fbb.balkna.model.Translator;
import org.fbb.balkna.model.merged.MergedExercise;

/**
 *
 * @author jvanek
 */
public  class BigRestTime extends PausaTime{
        

    public BigRestTime(int originalValue,  MergedExercise originator) {
        super(originalValue, originator);
    }

    @Override
     public String getInformaiveTitle() {
        return Translator.R("PauseWillChange");
    }
        
}

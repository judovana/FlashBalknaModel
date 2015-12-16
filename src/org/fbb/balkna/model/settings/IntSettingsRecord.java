package org.fbb.balkna.model.settings;

/**
 *
 * @author jvanek
 */
public class IntSettingsRecord  extends SettingsRecord<Integer>{

    public IntSettingsRecord(Integer defaultValue, String key) {
        super(defaultValue, key);
    }
    
    
    @Override
    public  void fromString(String t){
        if (t.equals("null")){
            setValue(null);
        } else {
        setValue(Integer.valueOf(t));
        }
        
    }
    
    
}

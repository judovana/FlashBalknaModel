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
        setValue(Integer.valueOf(t));
        
    }
    
    
}

package org.fbb.balkna.model.settings;

/**
 *
 * @author jvanek
 */
public class StringSettingsRecord  extends SettingsRecord<String>{

    public StringSettingsRecord(String defaultValue, String key) {
        super(defaultValue, key);
    }
    
    
    @Override
    public  void fromString(String t){
        setValue(t);
    }
    
    @Override
    public  String writeString(){
        return value;
    }

    
}

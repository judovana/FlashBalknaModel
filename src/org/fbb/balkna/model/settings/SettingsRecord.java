package org.fbb.balkna.model.settings;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author jvanek
 */
public abstract class SettingsRecord<T> {

    T value;
    final T defaultValue;
    final String key;

    public SettingsRecord(T defaultValue, String key) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.key = key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    
    

    public abstract void fromString(String t);

    public String writeString() {
        return value.toString();
    }

    public void writeObjectAsPropertyToWriter(BufferedWriter fw) throws IOException {
        writeObjectAsProperty(key, value, defaultValue, fw);

    }

    public String defaultAsString() {
        return " Default: " + objectAsPropertyString(key, defaultValue);
    }


    public String valueAsString() {
        return objectAsPropertyString(key, value);
    }
    
    private static String objectAsPropertyString(String key, Object value) {
        if (value == null) {
            return key + "=null";
        }
        return (key + "=" + value.toString());
    }

    private void writeObjectAsProperty(String key, T value, T defult, BufferedWriter fw) throws IOException {
        if ((value == null && defult != null)) {
            fw.write(objectAsPropertyString(key, value));
            fw.newLine();
            return;
        }
        if ((value == null && defult == null) || value.equals(defult)) {
            //not saving ddefaults
        } else {
            fw.write(objectAsPropertyString(key, value));
            fw.newLine();
        }
    }

}

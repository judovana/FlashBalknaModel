package org.fbb.balkna.model.settings;

/**
 *
 * @author jvanek
 */
public class BoolSettingsRecord extends SettingsRecord<Boolean> {

    public BoolSettingsRecord(Boolean defaultValue, String key) {
        super(defaultValue, key);
    }

    @Override
    public void fromString(String t) {
        if (t.equals("null")) {
            setValue(null);
        } else {
            setValue(Boolean.valueOf(t));
        }

    }

}

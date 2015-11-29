package org.fbb.balkna.model.primitives;

import java.util.List;
import org.fbb.balkna.model.Translator;

public class LocalisedString {

    public static String getMainLocale() {
        return Translator.getLocale();
    }

    static String findLocalised(List<LocalisedString> localisedNames) {
        for (LocalisedString s : localisedNames) {
            if (s.locale.equals(getMainLocale())) {
                return s.string;
            }
        }
        for (LocalisedString s : localisedNames) {
            if (getMainLocale().matches(".*" + s.locale)) {
                return s.string;
            }
        }
        for (LocalisedString s : localisedNames) {
            if (getMainLocale().matches(s.locale + ".*")) {
                return s.string;
            }
        }
        for (LocalisedString s : localisedNames) {
            if (getMainLocale().matches(".*" + s.locale + ".*")) {
                return s.string;
            }
        }
        return null;
    }
    private final String locale;
    private final String string;

    public LocalisedString(String locale, String string) {
        this.locale = locale;
        this.string = string;
    }

    public String getLocale() {
        return locale;
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return locale+"@"+string;
    }
    
    

}

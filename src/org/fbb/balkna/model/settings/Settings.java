/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fbb.balkna.model.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import org.fbb.balkna.Packages;
import org.fbb.balkna.model.SoundProvider;
import org.fbb.balkna.model.Translator;
import org.fbb.balkna.model.primitives.TimeShift;
import org.fbb.balkna.swing.locales.SwingTranslator;

/**
 *
 * @author jvanek
 */
public class Settings {

    private static final TimeShift timeShiftDefualt = new TimeShift();

    private static final String settingsname = "fbb.properties";

    private final BoolSettingsRecord laud = new BoolSettingsRecord(true, "laud");
    private final BoolSettingsRecord allowSkipping = new BoolSettingsRecord(false, "allowSkipping");
    private final BoolSettingsRecord pauseOnChange = new BoolSettingsRecord(false, "pauseOnChange");
    private final BoolSettingsRecord pauseOnExercise = new BoolSettingsRecord(false, "pauseOnExercise");
    private final BoolSettingsRecord ratioForced = new BoolSettingsRecord(true, "ratioForced");
    private final BoolSettingsRecord invertScreenCompress = new BoolSettingsRecord(false, "invertScreenCompress");
    private final BoolSettingsRecord allowScreenChange = new BoolSettingsRecord(true, "allowScreenChange");
    private final IntSettingsRecord imagesOnTimerSpeed = new IntSettingsRecord(2, "imagesOnTimerSpeed");
    private final StringSettingsRecord forcedLanguage = new StringSettingsRecord(null, "forcedLanguage");
    private final StringSettingsRecord forcedSoundFont = new StringSettingsRecord(Packages.DEFAULT_SOUND_PACK, "forcedSoundFont");

    private final SettingsRecord[] ALL_SETTINGS = new SettingsRecord[]{
        laud, allowSkipping, pauseOnChange, pauseOnExercise, ratioForced, invertScreenCompress,
        allowScreenChange, imagesOnTimerSpeed, forcedLanguage, forcedSoundFont};

    private final TimeShift timeShift = new TimeShift(timeShiftDefualt);

    /**
     * @return the laud
     */
    public boolean isLaud() {
        return laud.getValue();
    }

    /**
     * @param laud the laud to set
     */
    public void setLaud(boolean laud) {
        this.laud.setValue(laud);
    }

    /**
     * @return the allowSkipping
     */
    public boolean isAllowSkipping() {
        return allowSkipping.getValue();
    }

    /**
     * @param allowSkipping the allowSkipping to set
     */
    public void setAllowSkipping(boolean allowSkipping) {
        this.allowSkipping.setValue(allowSkipping);
    }

    /**
     * @return the pauseOnChange
     */
    public boolean isPauseOnChange() {
        return pauseOnChange.getValue();
    }

    /**
     * @param pauseOnChange the pauseOnChange to set
     */
    public void setPauseOnChange(boolean pauseOnChange) {
        this.pauseOnChange.setValue(pauseOnChange);
    }

    /**
     * @return the pauseOnExercise
     */
    public boolean isPauseOnExercise() {
        return pauseOnExercise.getValue();
    }

    /**
     * @param pauseOnExercise the pauseOnExercise to set
     */
    public void setPauseOnExercise(boolean pauseOnExercise) {
        this.pauseOnExercise.setValue(pauseOnExercise);
    }

    /**
     * @return the ratioForced
     */
    public boolean isRatioForced() {
        return ratioForced.getValue();
    }

    /**
     * @param ratioForced the ratioForced to set
     */
    public void setRatioForced(boolean ratioForced) {
        this.ratioForced.setValue(ratioForced);
    }

    /**
     * @return the imagesOnTimerSpeed
     */
    public int getImagesOnTimerSpeed() {
        return imagesOnTimerSpeed.getValue();
    }

    /**
     * @param imagesOnTimerSpeed the imagesOnTimerSpeed to set
     */
    public void setImagesOnTimerSpeed(int imagesOnTimerSpeed) {
        this.imagesOnTimerSpeed.setValue(imagesOnTimerSpeed);
    }

    /**
     * @return the forcedLanguage
     */
    public String getForcedLanguage() {
        return forcedLanguage.getDefaultValue();
    }

    /**
     * @param forcedLanguage the forcedLanguage to set
     */
    public void setForcedLanguage(String forcedLanguage) {
        if (forcedLanguage == null || forcedLanguage.trim().isEmpty()) {
            this.forcedLanguage.setValue(null);
        } else {
            this.forcedLanguage.setValue(forcedLanguage);
        }
    }

    /**
     * @return the forcedSoundFont
     */
    public String getForcedSoundFont() {
        return forcedSoundFont.getValue();
    }

    /**
     * @param forcedSoundFont the forcedSoundFont to set
     */
    public void setForcedSoundFont(String forcedSoundFont) {
        this.forcedSoundFont.setValue(forcedSoundFont);
    }

    public void save(File dir) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        dir.mkdir();
        //jdk6:(
        BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, settingsname)), "utf-8"));
        try {
            for (int i = 0; i < ALL_SETTINGS.length; i++) {
                SettingsRecord s = ALL_SETTINGS[i];
                s.writeObjectAsPropertyToWriter(fw);
            }
        } finally {
            fw.close();
        }
    }

    private void readItem(String s) {
        String[] ss = s.split("\\s*=\\s*");
        readItem(ss[0], ss[1]);
    }

    public void readItem(String key, String value) {
        key = key.trim();
        if (value != null) {
            value = value.trim();
        }
        for (int i = 0; i < ALL_SETTINGS.length; i++) {
            SettingsRecord s = ALL_SETTINGS[i];
            if (key.equals(s.getKey())) {
                s.fromString(value);
            }
        }
        //again, jdk6 compatibility
        if (key.equals("forcedLanguage")) {
            Translator.load(value);
            SwingTranslator.load(value);
        } else if (key.equals("forcedSoundFont")) {
            SoundProvider.getInstance().load(value);
        }
    }

    public String[] listItems() {
        String[] r = new String[ALL_SETTINGS.length * 2];
        for (int i = 0; i < ALL_SETTINGS.length; i++) {
            SettingsRecord s = ALL_SETTINGS[i];
            r[i * 2] = s.valueAsString();
            r[i * 2 + 1] = s.defaultAsString();
        }

        return r;
    }

    public void load(File dir) throws FileNotFoundException, IOException {
        if (dir.exists()) {
            File f = new File(dir, settingsname);
            if (f.exists()) {
                BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                while (true) {
                    String s = fr.readLine();
                    if (s == null) {
                        break;
                    }
                    readItem(s);
                }
            }
        }
    }

    public TimeShift getTimeShift() {
        return timeShift;

    }

    public void resetDefaults() {
        for (int i = 0; i < ALL_SETTINGS.length; i++) {
            SettingsRecord s = ALL_SETTINGS[i];
            s.setValue(s.getDefaultValue());
            
        }
    }

    private static class SettingsHolder {

        //https://en.wikipedia.org/wiki/Double-checked_locking#Usage_in_Java
        //https://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom
        private static final Settings INSTANCE = new Settings();

        private static Settings getInstance() {
            return SettingsHolder.INSTANCE;
        }
    }

    public static Settings getSettings() {
        return SettingsHolder.getInstance();
    }

    public boolean isAllowScreenChange() {
        return allowScreenChange.getValue();
    }

    public boolean isInvertScreenCompress() {
        return invertScreenCompress.getValue();
    }

    public void setAllowScreenChange(boolean allowScreenChange) {
        this.allowScreenChange.setValue(allowScreenChange);
    }

    public void setInvertScreenCompress(boolean invertScreenCompress) {
        this.invertScreenCompress.setValue(invertScreenCompress);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fbb.balkna.model;

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
import org.fbb.balkna.model.primitives.TimeShift;

/**
 *
 * @author jvanek
 */
public class Settings {

    private static final boolean laudDefault = true;
    private static final boolean allowSkippingDefault = false;
    private static final boolean pauseOnChangeDefault = false;
    private static final boolean pauseOnExerciseDefault = false;
    private static final boolean ratioForcedDefault = true;
    private static final int imagesOnTimerSpeedDefault = 2;
    private static final String forcedLanguageDefault = null;
    private static final String forcedSoundFontDefault = Packages.DEFAULT_SOUND_PACK;
    private static final TimeShift timeShiftDefualt = new TimeShift();

    private static final String settingsname = "fbb.properties";

    private boolean laud = laudDefault;
    private boolean allowSkipping = allowSkippingDefault;
    private boolean pauseOnChange = pauseOnChangeDefault;
    private boolean pauseOnExercise = pauseOnExerciseDefault;
    private boolean ratioForced = ratioForcedDefault;
    private int imagesOnTimerSpeed = imagesOnTimerSpeedDefault;
    private String forcedLanguage = forcedLanguageDefault;
    private String forcedSoundFont = forcedSoundFontDefault;

    private final TimeShift timeShift = new TimeShift(timeShiftDefualt);

    /**
     * @return the laud
     */
    public boolean isLaud() {
        return laud;
    }

    /**
     * @param laud the laud to set
     */
    public void setLaud(boolean laud) {
        this.laud = laud;
    }

    /**
     * @return the allowSkipping
     */
    public boolean isAllowSkipping() {
        return allowSkipping;
    }

    /**
     * @param allowSkipping the allowSkipping to set
     */
    public void setAllowSkipping(boolean allowSkipping) {
        this.allowSkipping = allowSkipping;
    }

    /**
     * @return the pauseOnChange
     */
    public boolean isPauseOnChange() {
        return pauseOnChange;
    }

    /**
     * @param pauseOnChange the pauseOnChange to set
     */
    public void setPauseOnChange(boolean pauseOnChange) {
        this.pauseOnChange = pauseOnChange;
    }

    /**
     * @return the pauseOnExercise
     */
    public boolean isPauseOnExercise() {
        return pauseOnExercise;
    }

    /**
     * @param pauseOnExercise the pauseOnExercise to set
     */
    public void setPauseOnExercise(boolean pauseOnExercise) {
        this.pauseOnExercise = pauseOnExercise;
    }

    /**
     * @return the ratioForced
     */
    public boolean isRatioForced() {
        return ratioForced;
    }

    /**
     * @param ratioForced the ratioForced to set
     */
    public void setRatioForced(boolean ratioForced) {
        this.ratioForced = ratioForced;
    }

    /**
     * @return the imagesOnTimerSpeed
     */
    public int getImagesOnTimerSpeed() {
        return imagesOnTimerSpeed;
    }

    /**
     * @param imagesOnTimerSpeed the imagesOnTimerSpeed to set
     */
    public void setImagesOnTimerSpeed(int imagesOnTimerSpeed) {
        this.imagesOnTimerSpeed = imagesOnTimerSpeed;
    }

    /**
     * @return the forcedLanguage
     */
    public String getForcedLanguage() {
        return forcedLanguage;
    }

    /**
     * @param forcedLanguage the forcedLanguage to set
     */
    public void setForcedLanguage(String forcedLanguage) {
        if (forcedLanguage == null || forcedLanguage.trim().isEmpty()) {
            this.forcedLanguage = null;
        } else {
            this.forcedLanguage = forcedLanguage;
        }
    }

    /**
     * @return the forcedSoundFont
     */
    public String getForcedSoundFont() {
        return forcedSoundFont;
    }

    /**
     * @param forcedSoundFont the forcedSoundFont to set
     */
    public void setForcedSoundFont(String forcedSoundFont) {
        this.forcedSoundFont = forcedSoundFont;
    }

    void save(File dir) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        dir.mkdir();
        BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, settingsname)), "utf-8"));
        try {
            writeObjectAsProperty("laud", laud, laudDefault, fw);
            writeObjectAsProperty("allowSkipping", allowSkipping, allowSkippingDefault, fw);
            writeObjectAsProperty("pauseOnChange", pauseOnChange, pauseOnChangeDefault, fw);
            writeObjectAsProperty("pauseOnExercise", pauseOnExercise, pauseOnExerciseDefault, fw);
            writeObjectAsProperty("ratioForced", ratioForced, ratioForcedDefault, fw);
            writeObjectAsProperty("imagesOnTimerSpeed", imagesOnTimerSpeed, imagesOnTimerSpeedDefault, fw);
            writeObjectAsProperty("forcedLanguage", forcedLanguage, forcedLanguageDefault, fw);
            writeObjectAsProperty("forcedSoundFont", forcedSoundFont, forcedSoundFontDefault, fw);
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
        if (value!=null){
            value = value.trim();
        }
        //sory, jdk6 compatibility
        if (key.equals("laud")) {
            laud = Boolean.valueOf(value);
        } else if (key.equals("allowSkipping")) {
            allowSkipping = Boolean.valueOf(value);
        } else if (key.equals("pauseOnChange")) {
            pauseOnChange = Boolean.valueOf(value);
        } else if (key.equals("pauseOnExercise")) {
            pauseOnExercise = Boolean.valueOf(value);
        } else if (key.equals("ratioForced")) {
            ratioForced = Boolean.valueOf(value);
        } else if (key.equals("imagesOnTimerSpeed")) {
            imagesOnTimerSpeed = Integer.valueOf(value);
        } else if (key.equals("forcedLanguage")) {
            forcedLanguage = value;
            Translator.load(value);
        } else if (key.equals("forcedSoundFont")) {
            forcedSoundFont = value;
            SoundProvider.getInstance().load(value);
        }
    }

    public String[] listItems() {
        return new String[]{
            objectAsPropertyString("laud", laud),
            " Default: " + objectAsPropertyString("laud", laudDefault),
            objectAsPropertyString("allowSkipping", allowSkipping),
            " Default: " + objectAsPropertyString("allowSkipping", allowSkippingDefault),
            objectAsPropertyString("pauseOnChange", pauseOnChange),
            " Default: " + objectAsPropertyString("pauseOnChange", pauseOnChangeDefault),
            objectAsPropertyString("pauseOnExercise", pauseOnExercise),
            " Default: " + objectAsPropertyString("pauseOnExercise", pauseOnExerciseDefault),
            objectAsPropertyString("ratioForced", ratioForced),
            " Default: " + objectAsPropertyString("ratioForced", ratioForcedDefault),
            objectAsPropertyString("imagesOnTimerSpeed", imagesOnTimerSpeed),
            " Default: " + objectAsPropertyString("imagesOnTimerSpeed", imagesOnTimerSpeedDefault),
            objectAsPropertyString("forcedLanguage", forcedLanguage),
            " Default: " + objectAsPropertyString("forcedLanguage", forcedLanguageDefault),
            objectAsPropertyString("forcedSoundFont", forcedSoundFont),
            " Default: " + objectAsPropertyString("forcedSoundFont", forcedSoundFontDefault)};
    }

    void load(File dir) throws FileNotFoundException, IOException {
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

    private String objectAsPropertyString(String key, Object value)  {
        if (value == null) {
            return key + "=null";
        }
        return (key + "=" + value.toString());
    }

    private void writeObjectAsProperty(String key, Object value, Object defult, BufferedWriter fw) throws IOException {
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

    TimeShift getTimeShift() {
        return timeShift;

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

}

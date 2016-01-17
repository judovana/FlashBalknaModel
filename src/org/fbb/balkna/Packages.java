package org.fbb.balkna;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fbb.balkna.model.PluginFactoryProvider;

/**
 *
 * @author jvanek
 */
public class Packages {

    public static final Package PACKAGE_ROOT = Packages.class.getPackage();
    public static final String PACKAGE_ROOT_NAME = PACKAGE_ROOT.getName();
    public static final String DATA = PACKAGE_ROOT_NAME + ".data";
    public static final String IMAGES = DATA + ".imgs";
    public static final String IMAGES_APP = IMAGES + ".app";
    public static final String IMAGES_EXE = IMAGES + ".exercises";
    public static final String IMAGES_TRA = IMAGES + ".trainings";
    public static final String SOUND_PACK = DATA + ".soundpacks";
    private static final String[] INTERNAL_SOUND_PACKS = {"cs", "cs_female", "cs_male", "en", "en_female", "en_male", "piip-beep"};
    public static final String[] LANGUAGES = {" ", "cs", "en"};
    public static final String DEFAULT_SOUND_PACK = INTERNAL_SOUND_PACKS[0];

    public static String[] SOUND_PACKS() {
        Set<String> pluginPacks = new HashSet<String>(0);
        try {
            List<URL> allWavs = PluginFactoryProvider.getInstance().searchResourceInPlugins(".*\\.wav");
            for (URL wav : allWavs) {
                String file = wav.getFile();
                String[] soundPack = file.split("[/\\\\]+");
                pluginPacks.add(soundPack[soundPack.length - 2]);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String[] SOUND_PACKS = new String[INTERNAL_SOUND_PACKS.length + pluginPacks.size()];
        for (int i = 0; i < INTERNAL_SOUND_PACKS.length; i++) {
            String IS = INTERNAL_SOUND_PACKS[i];
            SOUND_PACKS[i] = IS;
        }
        int i =0;
        for (String pluginPack : pluginPacks) {
            SOUND_PACKS[i+INTERNAL_SOUND_PACKS.length] = pluginPack;
            i++;
        }
        return SOUND_PACKS;
    }

}

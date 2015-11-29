package org.fbb.balkna;

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
    public static final String[] SOUND_PACKS = {"fbbhorror", "cs",  "cs_female", "cs_male",  "en",  "en_female",  "en_male"};
    public static final String DEFAULT_SOUND_PACK = SOUND_PACKS[1];

}

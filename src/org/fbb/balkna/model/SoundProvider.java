package org.fbb.balkna.model;

import java.net.URL;
import java.util.Random;
import org.fbb.balkna.Packages;
import org.fbb.balkna.model.utils.IoUtils;

/**
 *
 * @author jvanek
 */
public class SoundProvider {

    private static final String one = "1";
    private static final String two = "2";
    private static final String three = "3";
    private static final String change = "change";
    private static final String endPause = "endPause";
    private static final String endRun = "endRun";
    private static final String halfPause = "halfPause";
    private static final String halfRun = "halfRun";
    private static final String start = "start";
    private static final String threeQatsPause = "threeQatsPause";
    private static final String threeQuatsRun = "threeQats";
    private static final String endChange = "endChange";
    private static final String halfSerie = "halfSerie";
    private static final String halfTraining = "halfTraining";
    private static final String lastExercise = "lastExercise";
    private static final String lastSerie = "lastSerie";
    private static final String threeQatsSerie = "threeQatsSerie";
    private static final String threeQatsTraining = "threeQatsTraining";
    private static final String trainingEnd = "trainingEnd";

    //for testing
    String[] all = {one, two, three, change, endPause, endRun, halfPause, halfRun, start, threeQatsPause, threeQuatsRun, endChange, halfSerie, halfTraining, lastExercise, lastSerie, threeQatsSerie, threeQatsTraining, trainingEnd};

    private static final String suffix = ".wav";

    private WavPlayer PSone;
    private WavPlayer PStwo;
    private WavPlayer PSthree;
    private WavPlayer PSchange;
    private WavPlayer PSendPause;
    private WavPlayer PSendRun;
    private WavPlayer PShalfPause;
    private WavPlayer PShalfRun;
    private WavPlayer PSstart;
    private WavPlayer PSthreeQatsPause;
    private WavPlayer PSthreeQuatsRun;
    private WavPlayer PSendChange;

    private WavPlayer PShalfSerie;
    private WavPlayer PShalfTraining;
    private WavPlayer PSlastExercise;
    private WavPlayer PSlastSerie;
    private WavPlayer PSthreeQatsSerie;
    private WavPlayer PSthreeQatsTraining;
    private WavPlayer PStrainingEnd;

    private String usedSoundPack;
    private final WavPlayerProvider provider;

    public final void load(String name) {
        usedSoundPack = name;
        String soundPack = getPackage(name);
        PSone = provider.createPlayer(getUrl(soundPack, one));
        PStwo = provider.createPlayer(getUrl(soundPack, two));
        PSthree = provider.createPlayer(getUrl(soundPack, three));
        PSchange = provider.createPlayer(getUrl(soundPack, change));
        PSendPause = provider.createPlayer(getUrl(soundPack, endPause));
        PSendRun = provider.createPlayer(getUrl(soundPack, endRun));
        PShalfPause = provider.createPlayer(getUrl(soundPack, halfPause));
        PShalfRun = provider.createPlayer(getUrl(soundPack, halfRun));
        PSstart = provider.createPlayer(getUrl(soundPack, start));
        PSthreeQatsPause = provider.createPlayer(getUrl(soundPack, threeQatsPause));
        PSthreeQuatsRun = provider.createPlayer(getUrl(soundPack, threeQuatsRun));
        PSendChange = provider.createPlayer(getUrl(soundPack, endChange));
        PShalfSerie = provider.createPlayer(getUrl(soundPack, halfSerie));
        PShalfTraining = provider.createPlayer(getUrl(soundPack, halfTraining));
        PSlastExercise = provider.createPlayer(getUrl(soundPack, lastExercise));
        PSlastSerie = provider.createPlayer(getUrl(soundPack, lastSerie));
        PSthreeQatsSerie = provider.createPlayer(getUrl(soundPack, threeQatsSerie));
        PSthreeQatsTraining = provider.createPlayer(getUrl(soundPack, threeQatsTraining));
        PStrainingEnd = provider.createPlayer(getUrl(soundPack, trainingEnd));
    }

    private URL getUrl(String soundPack, String f) {
        return IoUtils.getFile(soundPack, f + suffix);
    }

    /**
     * @return the PSone
     */
    public WavPlayer getPSone() {
        return PSone;
    }

    /**
     * @return the PStwo
     */
    public WavPlayer getPStwo() {
        return PStwo;
    }

    /**
     * @return the PSthree
     */
    public WavPlayer getPSthree() {
        return PSthree;
    }

    /**
     * @return the PSchange
     */
    public WavPlayer getPSchange() {
        return PSchange;
    }

    /**
     * @return the PSendPause
     */
    public WavPlayer getPSendPause() {
        return PSendPause;
    }

    /**
     * @return the PSendRun
     */
    public WavPlayer getPSendRun() {
        return PSendRun;
    }

    /**
     * @return the PShalfPause
     */
    public WavPlayer getPShalfPause() {
        return PShalfPause;
    }

    /**
     * @return the PShalfRun
     */
    public WavPlayer getPShalfRun() {
        return PShalfRun;
    }

    /**
     * @return the PSstart
     */
    public WavPlayer getPSstart() {
        return PSstart;
    }

    /**
     * @return the PSthreeQatsPause
     */
    public WavPlayer getPSthreeQatsPause() {
        return PSthreeQatsPause;
    }

    public WavPlayer getPSthreeQuatsRun() {
        return PSthreeQuatsRun;
    }

    public WavPlayer getPSendChange() {
        return PSendChange;
    }

    public void test(String name) {
        String soundPack = getPackage(name);
        WavPlayer test = provider.createPlayer(getUrl(soundPack, all[new Random().nextInt(all.length)]));
        test.playAsync();
    }

    public void test() {
        test(getUsedSoundPack());
    }

    /**
     * @return the PShalfSerie
     */
    public WavPlayer getPShalfSerie() {
        return PShalfSerie;
    }

    /**
     * @return the PShalfTraining
     */
    public WavPlayer getPShalfTraining() {
        return PShalfTraining;
    }

    /**
     * @return the PSlastExercise
     */
    public WavPlayer getPSlastExercise() {
        return PSlastExercise;
    }

    /**
     * @return the PSlastSerie
     */
    public WavPlayer getPSlastSerie() {
        return PSlastSerie;
    }

    /**
     * @return the PSthreeQatsSerie
     */
    public WavPlayer getPSthreeQatsSerie() {
        return PSthreeQatsSerie;
    }

    /**
     * @return the PSthreeQatsTraining
     */
    public WavPlayer getPSthreeQatsTraining() {
        return PSthreeQatsTraining;
    }

    /**
     * @return the PStrainingEnd
     */
    public WavPlayer getPStrainingEnd() {
        return PStrainingEnd;
    }

    private static class SoundProviderHolder {

        public static SoundProvider instance;

        public static SoundProvider getInstance() {
            if (instance == null) {
                throw new RuntimeException("SondProvider not yet initialised");
            }
            return instance;
        }

        public static void createInstance(WavPlayerProvider wpp) {
            if (instance != null) {
                throw new RuntimeException("SondProvider already initialised");
            } else {
                instance = new SoundProvider(wpp);
            }
        }
    }

    public static SoundProvider getInstance() {
        return SoundProviderHolder.getInstance();
    }

    static void createInstance(WavPlayerProvider javaxWawPlayerProvider) {
        SoundProviderHolder.createInstance(javaxWawPlayerProvider);
    }

    public SoundProvider(WavPlayerProvider proider) {
        this.provider = proider;
        load(Packages.DEFAULT_SOUND_PACK);
    }

    public static String getDefaultSoundPack() {
        return getPackage(Packages.DEFAULT_SOUND_PACK);
    }

    private static String getPackage(String name) {
        return Packages.SOUND_PACK + "." + name;
    }

    public String getUsedSoundPack() {
        return usedSoundPack;
    }

}

package org.fbb.balkna.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Random;
import org.fbb.balkna.model.merged.MergedExercise;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BasicTime;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BigRestTime;
import org.fbb.balkna.model.primitives.ExerciseOverrides;
import org.fbb.balkna.model.primitives.Exercises;
import org.fbb.balkna.model.primitives.Substituable;
import org.fbb.balkna.model.primitives.TimeShift;
import org.fbb.balkna.model.primitives.Training;
import org.fbb.balkna.model.primitives.Trainings;

/**
 *
 * @author jvanek
 */
public class Model {

    public static void substitute(List<String> images, Substituable a) {
        for (int i = 0; i < images.size(); i++) {
            String get = images.get(i);
            get = get.replace("%{id}", a.getId());
            images.set(i, get);

        }
    }

    public static String getDefaultImageName() {
        return "fbbTitle.jpg";
    }

    public int getImagesOnTimerSpeed() {
        return Settings.getSettings().getImagesOnTimerSpeed();
    }

    public String getLanguage() {
        return Settings.getSettings().getForcedLanguage();
    }

    public void setImagesOnTimerSpeed(int i) {
        Settings.getSettings().setImagesOnTimerSpeed(i);
    }

    public String getPauseTitle() {
        return Translator.R("Pause");
    }

    public String getContinueTitle() {
        return Translator.R("Continue");
    }
    private final File settingsDir;
    private final File pluginsDir;

    private Model(File settingsDir, WavPlayerProvider wavProvider) {
        this.settingsDir = settingsDir;
        this.pluginsDir = new File(settingsDir, "plugins");
        SoundProvider.createInstance(wavProvider);
        try {
            if (pluginsDir.exists()) {
                File[] plugins = pluginsDir.listFiles();
                for (File plugin : plugins) {
                    try {
                        reloadForJar(false, plugin.toURI().toURL());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            load();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Exercises.getInstance();
        Trainings.getInstance();
    }

    public void save() {
        try {
            Settings.getSettings().save(settingsDir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void load() throws IOException {
        Settings.getSettings().load(settingsDir);
    }

    public void reload(boolean save, URL... u) throws MalformedURLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
        reloadForJar(save, u);
        Exercises.reloadInstance();
        Trainings.reloadInstance();
    }
//quick tester
//    public void reloadForJar() throws MalformedURLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//        reloadForJar(false, new URL("file:///home/jvanek/Desktop/urlLoadingTest.jar"));
//    }

    private static final Class[] parameters = new Class[]{URL.class};

    private void reloadForJar(boolean save, URL... urls) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
        for (URL u : urls) {
            if (save) {
                pluginsDir.mkdirs();
                File savedAs = new File(pluginsDir, new File(u.getFile()).getName());
                ReadableByteChannel rbc = Channels.newChannel(u.openStream());
                FileOutputStream fos = new FileOutputStream(savedAs);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                u = savedAs.toURI().toURL();
            }
            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class sysclass = URLClassLoader.class;

            Method method = sysclass.getDeclaredMethod("addURL", parameters);

            method.setAccessible(
                    true);
            method.invoke(sysloader,
                    new Object[]{u});

        }

    }

    public List<Training> getTraingNames() {
        List<Training> l = Trainings.getInstance().getTrainings();

//        Collections.sort(l, new Comparator<Training>() {
//
//            @Override
//            public int compare(Training o1, Training o2) {
//                return o1.getName().compareTo(o2.getName());
//            }
//        });
        return l;
    }

    public static String getDefaultImage() {
        return "title" + (new Random().nextInt(2) + 1) + ".png";
    }

    public String getDefaultStory() {
        return Translator.R("TrainingMoreInfo");
    }

    public BasicTime getWarmUp() {
        ExerciseOverrides eov = new ExerciseOverrides(5, 5, 1, 5, Exercises.WARM_UP_ID);
        MergedExercise wamTme = MergedExercise.MergedExercise(eov, new TimeShift());
        return new BigRestTime(5, wamTme) {

            @Override
            public String getInformaiveTitle() {
                return Translator.R("GetWarm");
            }

        };
    }

    public void setLaud(boolean l) {
        Settings.getSettings().setLaud(l);
    }

    public boolean isLaud() {
        return Settings.getSettings().isLaud();
    }

    public boolean isAllowSkipping() {
        return Settings.getSettings().isAllowSkipping();
    }

    public boolean isPauseOnChange() {
        return Settings.getSettings().isPauseOnChange();
    }

    public boolean isPauseOnExercise() {
        return Settings.getSettings().isPauseOnExercise();
    }

    public void setAllowSkipping(boolean allowSkipping) {
        Settings.getSettings().setAllowSkipping(allowSkipping);
    }

    public void setPauseOnChange(boolean pauseOnChange) {
        Settings.getSettings().setPauseOnChange(pauseOnChange);
    }

    public void setPauseOnExercise(boolean pauseOnExercise) {
        Settings.getSettings().setPauseOnExercise(pauseOnExercise);
    }

    public boolean isRatioForced() {
        return Settings.getSettings().isRatioForced();
    }

    public void setRatioForced(boolean ratioForced) {
        Settings.getSettings().setRatioForced(ratioForced);
    }

    public void setSoundPack(String s) {
        Settings.getSettings().setForcedSoundFont(s);
        SoundProvider.getInstance().load(s);        // TODO add your handling code here:
    }

    public void setLanguage(String string) {
        Settings.getSettings().setForcedLanguage(string);
        Translator.load(string);
    }

    public String getTitle() {
        return Translator.R("AppTitle");
    }

    public String getExamplePluginUrl() {
        //return "file://";
        return "https://github.com/judovana/FlashBalknaTestPlugin/releases/download/FlashBalknaTestPlugin-1.0/FlashBalknaTestPlugin_1.0.jar";
    }

    public TimeShift getTimeShift() {
        return Settings.getSettings().getTimeShift();
    }

    private static class ModelHolder {

        //https://en.wikipedia.org/wiki/Double-checked_locking#Usage_in_Java
        //https://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom
        private static Model INSTANCE;

        private static Model getInstance() {
            if (INSTANCE == null) {
                throw new NullPointerException("Model not initialised");
            }
            return ModelHolder.INSTANCE;
        }

        private static void createInstance(File f, WavPlayerProvider wpp) {
            INSTANCE = new Model(f, wpp);
        }

    }

    public static Model getModel() {
        return ModelHolder.getInstance();
    }

    public static void createrModel(File f, WavPlayerProvider wpp) {
        ModelHolder.createInstance(f, wpp);
    }

    public File getPluginsDir() {
        return pluginsDir;
    }

}

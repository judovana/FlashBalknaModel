package org.fbb.balkna.model;

import org.fbb.balkna.model.settings.Settings;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.fbb.balkna.model.merged.MergedExercise;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BasicTime;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BigRestTime;
import org.fbb.balkna.model.primitives.Cycle;
import org.fbb.balkna.model.primitives.Cycles;
import org.fbb.balkna.model.primitives.Exercise;
import org.fbb.balkna.model.primitives.ExerciseOverrides;
import org.fbb.balkna.model.primitives.Exercises;
import org.fbb.balkna.model.primitives.TimeShift;
import org.fbb.balkna.model.primitives.Training;
import org.fbb.balkna.model.primitives.Trainings;
import org.fbb.balkna.model.primitives.history.RecordWithOrigin;
import org.fbb.balkna.model.utils.JavaPluginProvider;
import org.fbb.balkna.swing.locales.SwingTranslator;

/**
 *
 * @author jvanek
 */
public class Model {

    private final PluginFactoryProvider pfp;

    public static void substitute(List<String> images, Substituable a) {
        for (int i = 0; i < images.size(); i++) {
            String get = images.get(i);
            String get2 = get.replace("%{id}", a.getId());
            if (!get2.equals(get)) {
                images.set(i, get2);
            }

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
    private final File statsDir;

    public File getStatsDir() {
        return statsDir;
    }
    
    

    private Model(File settingsDir, WavPlayerProvider wavProvider, PluginFactoryProvider lpfp) {
        this.settingsDir = settingsDir;
        this.pluginsDir = new File(settingsDir, "plugins");
        this.statsDir = new File(settingsDir, "stats");
        this.pfp = lpfp;
        SoundProvider.createInstance(wavProvider);
        try {
            if (pluginsDir.exists()) {
                File[] plugins = pluginsDir.listFiles();
                for (File plugin : plugins) {
                    try {
                        reloadForJar(false, pfp, plugin.toURI().toURL());
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
        reload();
    }

    public void reload() {
        Exercises.reloadInstance();
        Trainings.reloadInstance();
        Cycles.reloadInstance();
    }

    public void save() {
        try {
            Settings.getSettings().save(settingsDir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    final public void load() throws IOException {
        Settings.getSettings().load(settingsDir);
    }

    public void reload(boolean save, URL... u) throws MalformedURLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
        reloadForJar(save, pfp, u);
        reload();
    }
//quick tester
//    public void reloadForJar() throws MalformedURLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//        reloadForJar(false, new URL("file:///home/jvanek/Desktop/urlLoadingTest.jar"));
//    }

    private void reloadForJar(boolean save, PluginFactoryProvider pfp, URL... urls) throws IOException {

        for (URL u : urls) {
            if (save) {
                pluginsDir.mkdirs();
                File savedAs = new File(pluginsDir, new File(u.getFile()).getName());
                //right thing to do but... :-/
//                ReadableByteChannel rbc = Channels.newChannel(u.openStream());
//                FileOutputStream fos = new FileOutputStream(savedAs);
//                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                saveUrl(u, savedAs);
                u = savedAs.toURI().toURL();
            } else if (!u.getProtocol().toLowerCase().startsWith("file")) {
                File savedAs = saveUrl(u);
                u = savedAs.toURI().toURL();
            }
            try {
                pfp.addResource(u);
            } catch (Exception ex) {
                throw new IOException(ex);
            }

        }

    }

    //more android comaptible garbage
    public static File saveUrl(URL u) throws IOException {
        File savedAs = File.createTempFile("tmpFbbBalkna", "tmpDownload");
        saveUrl(u, savedAs);
        return savedAs;
    }

    //android comaptible garbage
    public static void saveUrl(URL u, File savedAs) throws IOException {

        URLConnection connection = u.openConnection();
        InputStream inputStream = new BufferedInputStream(u.openStream(), 10240);
        FileOutputStream outputStream = new FileOutputStream(savedAs);

        byte buffer[] = new byte[1024];
        int dataSize;
        while ((dataSize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, dataSize);
        }

        outputStream.close();

    }

    public List<Training> getTraingNames() {
        List<Training> l = Trainings.getInstance().getTrainings();
        return l;
    }
    
       public List<Cycle> getCycles() {
        List<Cycle> l = Cycles.getInstance().getCycles();
        return l;
    }

    public List<Exercise> getExercises() {
        List<Exercise> l = Exercises.getInstance().getExercises();

        Collections.sort(l, new Comparator<Exercise>() {

            @Override
            public int compare(Exercise o1, Exercise o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
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
        SwingTranslator.load(string);
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

    public void resetDefaults() {
        Settings.getSettings().resetDefaults();
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
            INSTANCE = new Model(f, wpp, new JavaPluginProvider());
        }

    }

    public static Model getModel() {
        if (ModelHolder.INSTANCE == null) {
            throw new RuntimeException("Model not yet initialised");
        }
        return ModelHolder.getInstance();
    }

    public static void createrModel(File f, WavPlayerProvider wpp) {
        if (ModelHolder.INSTANCE != null) {
            //android.. so silently ignore
        } else {
            ModelHolder.createInstance(f, wpp);
        }
    }

    public File getPluginsDir() {
        return pluginsDir;
    }

    
    private boolean saveStats=true;

    public boolean isSaveStats() {
        return saveStats;
    }

    public void setSaveStats(boolean saveStats) {
        this.saveStats = saveStats;
    }
    
    
    public List<RecordWithOrigin> gatherStatistics(){
        List<RecordWithOrigin>  cycles = Cycles.getInstance().gatherStatistics();
        List<RecordWithOrigin>  exercise = Exercises.getInstance().gatherStatistics();
        List<RecordWithOrigin>  trainings = Trainings.getInstance().gatherStatistics();
        List<RecordWithOrigin>  exercisesLiketrainings = Trainings.getInstance().gatherFakeTrainingsStatistics();
        List<RecordWithOrigin>  r = new ArrayList<RecordWithOrigin>(cycles.size() + exercise.size()+trainings.size()+exercisesLiketrainings.size());
        r.addAll(cycles);
        r.addAll(exercise);
        r.addAll(trainings);
        r.addAll(exercisesLiketrainings);
        
        Collections.sort(r);
        Collections.reverse(r);
        
        return r;
        
    }
    
    
}

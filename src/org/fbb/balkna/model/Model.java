package org.fbb.balkna.model;

import org.fbb.balkna.model.graphs.DayStatst;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.fbb.balkna.model.primitives.history.RecordType;
import org.fbb.balkna.model.primitives.history.RecordWithOrigin;
import org.fbb.balkna.model.utils.JavaPluginProvider;
import org.fbb.balkna.swing.locales.SwingTranslator;

/**
 *
 * @author jvanek
 */
public class Model {

    public static void substituteImages(List<String> images, Substituable a) {
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

    private Model(File settingsDir, WavPlayerProvider wavProvider) {
        this.settingsDir = settingsDir;
        this.pluginsDir = new File(settingsDir, "plugins");
        this.statsDir = new File(settingsDir, "stats");
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
        reloadForJar(save, u);
        reload();
    }
//quick tester
//    public void reloadForJar() throws MalformedURLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//        reloadForJar(false, new URL("file:///home/jvanek/Desktop/urlLoadingTest.jar"));
//    }

    private void reloadForJar(boolean save, URL... urls) throws IOException {

        for (URL u : urls) {
            if (save) {
                pluginsDir.mkdirs();
                File savedAs = new File(pluginsDir, new File(u.getFile()).getName());
                if (savedAs.exists()) {
                    File tmp = saveUrl(u);
                    if (tmp.exists() && tmp.length() > 0) {
                        JavaPluginProvider.getPluginPaths().removePath(savedAs);
                        tmp.renameTo(savedAs);
                    }
                } else {
                    saveUrl(u, savedAs);
                }
                u = savedAs.toURI().toURL();
            } else if (!u.getProtocol().toLowerCase().startsWith("file")) {
                File savedAs = saveUrl(u);
                u = savedAs.toURI().toURL();
            }
            try {
                PluginFactoryProvider.getInstance().addResource(u);
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
        InputStream inputStream = new BufferedInputStream(u.openStream(), 1024);
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
            INSTANCE = new Model(f, wpp);
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

    private boolean saveStats = true;

    public boolean isSaveStats() {
        return saveStats;
    }

    public void setSaveStats(boolean saveStats) {
        this.saveStats = saveStats;
    }

    public List<RecordWithOrigin> gatherStatistics() {
        return gatherStatistics(false, true, true);
    }

    public List<RecordWithOrigin> gatherStatistics(boolean ex, boolean tr, boolean cy) {
        List<RecordWithOrigin> cycles = new ArrayList<RecordWithOrigin>(0);
        if (cy) {
            cycles = Cycles.getInstance().gatherStatistics();
        }
        List<RecordWithOrigin> exercise = new ArrayList<RecordWithOrigin>(0);
        if (ex) {
            exercise = Exercises.getInstance().gatherStatistics();
        }

        List<RecordWithOrigin> trainings = new ArrayList<RecordWithOrigin>(0);
        List<RecordWithOrigin> exercisesLiketrainings = new ArrayList<RecordWithOrigin>(0);
        if (tr) {
            trainings = Trainings.getInstance().gatherStatistics();
            exercisesLiketrainings = Trainings.getInstance().gatherFakeTrainingsStatistics();
        }

        List<RecordWithOrigin> r = new ArrayList<RecordWithOrigin>(cycles.size() + exercise.size() + trainings.size() + exercisesLiketrainings.size() + 1);
        r.addAll(cycles);
        r.addAll(exercise);
        r.addAll(trainings);
        r.addAll(exercisesLiketrainings);
        r.add(RecordWithOrigin.NOW());

        Collections.sort(r);
        Collections.reverse(r);

        RecordWithOrigin.SHOW_CLASS = !((ex && !tr && !cy)
                || (!ex && tr && !cy)
                || (!ex && !tr && cy));
        return r;

    }

    public Map<Date, DayStatst> getMonthData(boolean ex, boolean tr, boolean cy) {
        return getDayData(35, ex, tr, cy, day);
    }

    public Map<Date, DayStatst> getWeekData(boolean ex, boolean tr, boolean cy) {
        return getDayData(10, ex, tr, cy, day);
    }

    public Map<Date, DayStatst> getYearData1(boolean ex, boolean tr, boolean cy) {
        return getDayData(365, ex, tr, cy, month);
    }

    public Map<Date, DayStatst> getYearData2(boolean ex, boolean tr, boolean cy) {
        return getDayData(365, ex, tr, cy, week);
    }
//dont fill! or just fake?

    public Map<Date, DayStatst> getDayData(boolean ex, boolean tr, boolean cy) {
        return getDayData(1, ex, tr, cy, hour);
    }
    
    public Map<Date, DayStatst> getHourData(boolean ex, boolean tr, boolean cy) {
        return getDayData(1, ex, tr, cy, minut);
    }

    private static final long minut = 60 * 1000;
    private static final long hour = 60 * minut;
    private static final long day = hour * 24;
    private static final long week = 7 * day;
    private static final long month = 4 * week;

    /**
     *
     * @param period - days
     * @param ex
     * @param tr
     * @param cy
     * @return
     */
    private Map<Date, DayStatst> getDayData(int period, boolean ex, boolean tr, boolean cy, long step) {
        long p = period;
        p = p * day;
        return getDayData(p, ex, tr, cy, step);
    }

    /**
     *
     * @param period - in ms, and max 356day - see usage of DayOfYear
     * @param ex
     * @param tr
     * @param cy
     * @return
     */
    private Map<Date, DayStatst> getDayData(long period, boolean ex, boolean tr, boolean cy, long step) {
        List<RecordWithOrigin> gathereds = gatherStatistics(ex, tr, cy);
        Map<Date, DayStatst> result = new HashMap(gathereds.size() / 3);
        Date now = new Date();
        Calendar cl = new GregorianCalendar();
        //int day = cl.get(Calendar.DAY_OF_YEAR);
        for (RecordWithOrigin gathered : gathereds) {
            if (now.getTime() - gathered.getRecord().getWhen() > period) {
                break;
            }
            cl.setTime(new Date(gathered.getRecord().getWhen()));
            Calendar cl2 = new GregorianCalendar();
            cl2.setTime(new Date(0));
            cl2.set(Calendar.YEAR, cl.get(Calendar.YEAR));
            if (step == day) {
                cl2.set(Calendar.DAY_OF_YEAR, cl.get(Calendar.DAY_OF_YEAR));
            }
            if (step == week) {
                cl2.set(Calendar.WEEK_OF_YEAR, cl.get(Calendar.WEEK_OF_YEAR));
            }
            if (step == month) {
                cl2.set(Calendar.MONTH, cl.get(Calendar.MONTH));
            }
            if (step == hour) {
                cl2.set(Calendar.DAY_OF_YEAR, cl.get(Calendar.DAY_OF_YEAR));
                cl2.set(Calendar.HOUR_OF_DAY, cl.get(Calendar.HOUR_OF_DAY));
            }
            if (step == minut) {
                cl2.set(Calendar.DAY_OF_YEAR, cl.get(Calendar.DAY_OF_YEAR));
                cl2.set(Calendar.HOUR_OF_DAY, cl.get(Calendar.HOUR_OF_DAY));
                cl2.set(Calendar.MINUTE, cl.get(Calendar.MINUTE));
            }
            Date day = cl2.getTime();
            DayStatst get = result.get(day);
            if (get == null) {
                get = new DayStatst(day);
                get.setStep(step);
                if (step <= hour) {
                    get.setEasyFormater(DayStatst.sdfDay);
                } else {
                    get.setEasyFormater(DayStatst.sdfMore);
                }
                result.put(day, get);
            }
            if (gathered.getRecord().getWhat() == RecordType.STARTED
                    || gathered.getRecord().getWhat() == RecordType.CONTINUED) {
                get.incStart(gathered.getOrigin().getClass());
            } else if (gathered.getRecord().getWhat() == RecordType.FINISHED) {
                get.incPass(gathered.getOrigin().getClass());
            } else if (gathered.getRecord().getWhat() == RecordType.NOW) {
            } else {
                get.incFail(gathered.getOrigin().getClass());
            }

        }

        return result;

    }

}

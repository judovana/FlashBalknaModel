package org.fbb.balkna.model.primitives;

import org.fbb.balkna.model.Substituable;
import java.io.BufferedReader;
import org.fbb.balkna.model.ImagesSaver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.fbb.balkna.model.Translator.R;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.Statisticable;
import org.fbb.balkna.model.merged.MergedExercise;
import org.fbb.balkna.model.merged.MergedExerciseWrapper;
import org.fbb.balkna.model.primitives.history.NonRepeatedArrayList;
import org.fbb.balkna.model.primitives.history.Record;
import org.fbb.balkna.model.primitives.history.RecordType;
import org.fbb.balkna.model.utils.IoUtils;
import org.fbb.balkna.model.utils.TimeUtils;
import org.fbb.balkna.model.utils.XmlUtils;
import org.w3c.dom.Node;
import static org.fbb.balkna.model.utils.XmlConstants.*;
import org.w3c.dom.Element;

/**
 *
 * @author jvanek
 */
public class Training implements Substituable, Statisticable {

    private final String id;
    private final String name;
    private final List<LocalisedString> localisedNames;
    private final String description;
    private final List<LocalisedString> localisedDescriptions;
    private final List<String> images;
    private final List<ExerciseOverrides> exerciseOverrides;

    private final List<Record> statistics = new NonRepeatedArrayList<Record>();
    private boolean load = false;

    public Training(Exercise ex) {
        this(ex.getId(), ex.getName(), ex.getDescription(), ex.getLocalisedNames(), ex.getLocalisedDescriptions(), new ArrayList<String>(), convert(ex));
    }

    public Training transform(TrainingOverrides override) {
        final List<ExerciseOverrides> nwOverride = new ArrayList<ExerciseOverrides>(exerciseOverrides.size());
        for (ExerciseOverrides ow : exerciseOverrides) {
            nwOverride.add(ow.transform(override));
        }
        return new Training(id, name, id, localisedNames, localisedDescriptions, images, nwOverride);
    }

    static List<ExerciseOverrides> convert(Exercise ex) {
        ArrayList<ExerciseOverrides> l = new ArrayList<ExerciseOverrides>(1);
        l.add(new ExerciseOverrides(ex.getTime(), ex.getPause(), ex.getIterations(), ex.getRest(), ex.getId()));
        return l;
    }

    private Training(String id, String name, String des, List<LocalisedString> localisedNames, List<LocalisedString> localisedDescriptions, List<String> images, List<ExerciseOverrides> exerciseOverrides) {
        if (id == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.name = name;
        this.description = des;
        this.localisedNames = localisedNames;
        this.localisedDescriptions = localisedDescriptions;
        this.images = images;
        this.exerciseOverrides = exerciseOverrides;
        Model.substitute(images, this);
    }

    public static Training parse(final Node node) {

        String id = null;
        String name = null;
        List<LocalisedString> localisedNames = new ArrayList<LocalisedString>();
        String description = null;
        List<LocalisedString> localisedDescriptions = new ArrayList<LocalisedString>();
        List<String> images = new ArrayList<String>();
        List<ExerciseOverrides> exerciseOverrides = new ArrayList<ExerciseOverrides>();
        List<Element> interlayer = XmlUtils.getRealChilds(node);
        for (Element n : interlayer) {
            if (n.getNodeName().equals(ID)) {
                id = n.getTextContent();
            } else if (n.getNodeName().equals(NAMES)) {
                name = XmlUtils.getDefaultName(n);
                localisedNames = XmlUtils.getLocalisedNames(n);
            } else if (n.getNodeName().equals(DESCRIPTIONS)) {
                description = XmlUtils.getDefaultDescription(n);
                localisedDescriptions = XmlUtils.getLocalisedDescriptions(n);
            } else if (n.getNodeName().equals(IMAGES)) {
                images = XmlUtils.getImages(n);
            } else if (n.getNodeName().equals(EXERCISES)) {
                List<Element> exerciseElements = XmlUtils.getRealChilds(n);
                for (Element exerciseElement : exerciseElements) {
                    if (exerciseElement.getNodeName().equals(EXERCISE)) {
                        exerciseOverrides.add(ExerciseOverrides.parse(exerciseElement));
                    } else if (exerciseElement.getNodeName().equals(EXERCISES_SET)) {
                        int clone = 1;
                        String count = exerciseElement.getAttribute("count");
                        if (count != null) {
                            clone = Integer.valueOf(count);
                        }
                        List<Element> exerciseSetsElements = XmlUtils.getRealChilds(exerciseElement);
                        for (int x = 0; x < clone; x++) {
                            for (Element setExercise : exerciseSetsElements) {
                                exerciseOverrides.add(ExerciseOverrides.parse(setExercise));
                            }
                        }
                    }
                }
            }
        }
        return new Training(id, name, description, localisedNames, localisedDescriptions, images, exerciseOverrides);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        String s = LocalisedString.findLocalised(localisedNames);
        if (s != null) {
            return s;
        }
        return name;
    }

    public String getDescription() {
        String s = getDescriptionImpl();
        if (s == null) {
            return getName();
        }
        return s;
    }

    private String getDescriptionImpl() {
        String s = LocalisedString.findLocalised(localisedDescriptions);
        if (s != null) {
            return s;
        }
        return description;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String[] getImages() {
        String[] r = new String[images.size()];
        for (int i = 0; i < images.size(); i++) {
            String get = images.get(i);
            r[i] = get;
        }
        return r;
    }

    public List<ExerciseOverrides> getExerciseOverrides() {
        return Collections.unmodifiableList(exerciseOverrides);
    }

    public List<String> getExerciseImages() {
        List<String> r = new ArrayList<String>();
        List<ExerciseOverrides> l1 = getExerciseOverrides();
        for (ExerciseOverrides eov : l1) {
            r.addAll(Exercises.getInstance().getExerciseById(eov.getTargetId()).getImages());

        }
        return r;
    }

    public String getStory() {
        return getStory(false);
    }

    public String getStory(boolean html) {
        StringBuilder sb = new StringBuilder();
        if (html) {
            sb.append("<html><head>")
                    .append("<meta http-equiv='Content-Type' content='text/html;charset=utf-8'>")
                    .append("</head><body>");
        }
        sb.append(getStoryPart(html));
        if (html) {
            sb.append("</body></html>");
        }
        return sb.toString();
    }

    public String getStoryPart(boolean html) {
        StringBuilder sb = new StringBuilder();
        if (html) {
            sb.append("<div>");
            sb.append("<a href='http://flashbb.cz/aktualne'").append(Model.getDefaultImageName())
                    .append("'>  <img align='right' src='" + IMGS_SUBDIR + "/")
                    .append(Model.getDefaultImageName())
                    .append("' width='150' height='150'>  </a>");
            sb.append("</div>");
        }
        if (html) {
            sb.append("<h1>");
        }
        sb.append(getName());
        if (html) {
            sb.append("</h1>");
        }
        breakLine(html, sb);
        sb.append(getDescription());
        breakLine(html, sb);
        String[] iims = getImages();
        for (String iim : iims) {
            if (html) {
                sb.append("<a href='" + IMGS_SUBDIR + "/").append(iim)
                        .append("'>  <img src='" + IMGS_SUBDIR + "/")
                        .append(iim)
                        .append("' width='150' height='100'>  </a>");
            } else {
                sb.append(iim).append(";");
            }
        }
        breakLine(html, sb);
        if (!Model.getModel().getTimeShift().equals(new TimeShift())) {
            sb.append(R("WarningTimeShift"));
            breakLine(html, sb);
            sb.append(Model.getModel().getTimeShift().toString());
            breakLine(html, sb);
            breakLine(html, sb);
        }
        MergedExerciseWrapper m = getMergedExercises(Model.getModel().getTimeShift());
        sb.append(R("TotalTime", TimeUtils.secondsToHours(m.getTime())));
        breakLine(html, sb);
        sb.append(R("TotalTimeExercise", TimeUtils.secondsToHours(m.getActiveTime())));
        breakLine(html, sb);
        sb.append(R("TotalTimeResting", TimeUtils.secondsToHours(m.getRestTime())));
        breakLine(html, sb);
        sb.append(R("TotalExercises", m.getIterations()));
        breakLine(html, sb);
        sb.append(R("TotalDifferentExercises", m.getSize()));
        breakLine(html, sb);
        sb.append("----");
        breakLine(html, sb);
        sb.append(m.getStory(html));
        return sb.toString();
    }

    public MergedExerciseWrapper getMergedExercises(TimeShift t) {
        List<ExerciseOverrides> l = getExerciseOverrides();
        List<MergedExercise> r = new ArrayList<MergedExercise>(l.size());
        for (ExerciseOverrides get : l) {
            r.add(MergedExercise.MergedExercise(get, t));
        }
        return new MergedExerciseWrapper(r);
    }

    public static final String IMGS_SUBDIR = "images";

    public File export(File root, ImagesSaver im) throws FileNotFoundException, IOException {
        String dir = "exported.html";
        String index1 = "index.html";
        String index2 = "index.txt";
        File mainDir = new File(root, dir);
        mainDir.mkdir();
        File indexFile1 = new File(mainDir, index1);
        File indexFile2 = new File(mainDir, index2);
        File imgDir = new File(mainDir, IMGS_SUBDIR);
        imgDir.mkdir();
        //jd6 comaptible
        BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFile1)));
        try {
            bw1.write(getStory(true));
            bw1.flush();
        } finally {
            bw1.close();
        }
        //jd6 comaptible
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFile2)));
        try {
            bw2.write(getStory(false));
            bw2.flush();
        } finally {
            bw2.close();
        }
        String[] ti = getImages();
        List<String> ei = getExerciseImages();
        im.writeExercisesImagesToDir(imgDir, ei);
        im.writeTrainingsImagesToDir(imgDir, Arrays.asList(ti));
        return indexFile1;
    }

    private void breakLine(boolean html, StringBuilder sb) {
        if (html) {
            sb.append("<br>");
        }
        sb.append("\n");
    }

  
    @Override
    public File getFile() {
        return new File(Trainings.getStatsDir(), getId());
    }

    public void load() {
        try {
            IoUtils.loadStatisticable(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void load(BufferedReader fr) throws IOException {
        if (load) {
            return;
        }
        load = true;
        while (true) {
            String s = fr.readLine();
            if (s == null) {
                break;
            }
            if (s.trim().isEmpty()){
                continue;
                        
            }
            statistics.add(Record.fromString(s));
        }

    }

    public void save() {
        try {
            IoUtils.saveStatisticable(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void save(BufferedWriter fr) throws IOException {
        for (Record r : statistics) {
            fr.write(r.toString());
            fr.newLine();

        }

    }

    @Override
    public List<Record> getRecords() {
        load();
        return Collections.unmodifiableList(statistics);
    }

    private Record  lastRecord;
    @Override
    public synchronized void addRecord(Record r) {
        load();
        if (lastRecord == null) {
            statistics.add(r);
            save();
        } else {
            Record q = lastRecord;
            if (Math.abs(q.compareTo(r)) > Record.minTime ||q.getWhat()!=r.getWhat()) {//somebody clicking to fast?
                statistics.add(r);
                save();
            }
        }
        lastRecord = r;

    }

    public void started() {
        addRecord(Record.create(RecordType.STARTED));
    }

    public void finished() {
        addRecord(Record.create(RecordType.FINISHED));
    }
    public void finishedWithSkips() {
        addRecord(Record.create(RecordType.FINISHED_WITH_SKIPPS));
    }
    
    public void canceled() {
        addRecord(Record.create(RecordType.CANCELED));
    }

}

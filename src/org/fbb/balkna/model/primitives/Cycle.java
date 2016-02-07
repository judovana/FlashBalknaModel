package org.fbb.balkna.model.primitives;

import java.io.BufferedReader;
import org.fbb.balkna.model.ImagesSaver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.fbb.balkna.model.Translator.R;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.Trainable;
import org.fbb.balkna.model.Translator;
import org.fbb.balkna.model.primitives.history.NonRepeatedArrayList;
import org.fbb.balkna.model.primitives.history.Record;
import org.fbb.balkna.model.primitives.history.RecordType;
import org.fbb.balkna.model.primitives.history.StatisticHelper;
import org.fbb.balkna.model.utils.IoUtils;
import org.fbb.balkna.model.utils.XmlUtils;
import org.w3c.dom.Node;
import static org.fbb.balkna.model.utils.XmlConstants.*;
import org.w3c.dom.Element;

/**
 *
 * @author jvanek
 */
public class Cycle implements Trainable {

    private final String id;
    private final String name;
    private final List<LocalisedString> localisedNames;
    private final String description;
    private final List<LocalisedString> localisedDescriptions;
    private final List<String> images;
    private final List<TrainingOverrides> trainings;

    private int trainingPointer = 1;
    private final List<Record> statistics = new NonRepeatedArrayList<Record>();
    private boolean load = false;

    private Cycle(String id, String name, String des, List<LocalisedString> localisedNames, List<LocalisedString> localisedDescriptions, List<String> images, List<TrainingOverrides> trainings) {
        if (id == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.name = name;
        this.description = des;
        this.localisedNames = localisedNames;
        this.localisedDescriptions = localisedDescriptions;
        this.images = images;
        this.trainings = trainings;
        Model.substituteImages(images, this);
    }

    public static Cycle parse(final Node node) {

        String id = null;
        String name = null;
        List<LocalisedString> localisedNames = new ArrayList<LocalisedString>();
        String description = null;
        List<LocalisedString> localisedDescriptions = new ArrayList<LocalisedString>();
        List<String> images = new ArrayList<String>();
        List<TrainingOverrides> overrides = new ArrayList<TrainingOverrides>();
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
            } else if (n.getNodeName().equals(TRAININGS)) {
                List<Element> exerciseElements = XmlUtils.getRealChilds(n);
                for (Element exerciseElement : exerciseElements) {
                    if (exerciseElement.getNodeName().equals(TRAINING)) {
                        overrides.add(TrainingOverrides.parse(exerciseElement));
                    }
                }
            }
        }
        return new Cycle(id, name, description, localisedNames, localisedDescriptions, images, overrides);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIdAsMcro() {
        return "%{c-" + getId() + ";" + getName() + "}";
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

    @Override
    public String[] getImages() {
        String[] r = new String[images.size()];
        for (int i = 0; i < images.size(); i++) {
            String get = images.get(i);
            r[i] = get;
        }
        return r;
    }

    public List<TrainingOverrides> getTrainingOverrides() {
        return Collections.unmodifiableList(trainings);
    }

    @Override
    public Training getTraining() {
        return getTraining(getTrainingPointer());
    }

    public Training getTraining(int which) {
        return trainings.get(which - 1).getTraining();
    }

//    public List<String> getExerciseImages() {
//        List<String> r = new ArrayList<String>();
//        List<TrainingOverrides> l1 = getExerciseOverrides();
//        for (ExerciseOverrides eov : l1) {
//            r.addAll(Exercises.getInstance().getExerciseById(eov.getTargetId()).getImages());
//
//        }
//        return r;
//    }
    @Override
    public String getStory() {
        return getStory(false);
    }

    @Override
    public String getStory(boolean html) {
        StringBuilder sb = new StringBuilder();
        if (html) {
            sb.append("<html><head>")
                    .append("<meta http-equiv='Content-Type' content='text/html;charset=utf-8'>")
                    .append("</head><body>");
            sb.append("<div>");
            sb.append("<a href='http://flashbb.cz/aktualne'").append(Model.getDefaultImageName())
                    .append("'>  <img align='right' src='" + Training.IMGS_SUBDIR + "/")
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
                sb.append("<a href='" + Training.IMGS_SUBDIR + "/").append(iim)
                        .append("'>  <img src='" + Training.IMGS_SUBDIR + "/")
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
        int i = 0;
        if (html) {
            sb.append("<h3>");
        }
        sb.append(R("overview"));
        sb.append("\n");
        if (html) {
            sb.append("</h3>");
        }
        if (html) {
            sb.append("<blockquote>");
        }
        for (TrainingOverrides t : getTrainingOverrides()) {
            i++;
            sb.append(t.getHeader(i, html));
        }
        if (html) {
            sb.append("</blockquote>");
        }
        if (html) {
            sb.append("<h3>");
        }
        sb.append("\n");
        sb.append("\n");
        sb.append(R("details"));
        sb.append("\n");
        if (html) {
            sb.append("</h3>");
        }
        i = 0;
        for (TrainingOverrides t : getTrainingOverrides()) {
            i++;
            if (!html) {
                breakLine(html, sb);
                sb.append("***** ").append(i).append(" *****");
                breakLine(html, sb);
                sb.append("***** ").append(i).append(" *****");
                breakLine(html, sb);
            } else {
                breakLine(html, sb);
                sb.append("<h4>***** ").append(i).append(" *****</h4>");
                breakLine(html, sb);
            }
            String s = t.getTraining().getStoryPart(html);
            sb.append(s);

            if (t.getRestday() != null) {
                if (html) {
                    sb.append("<i>");
                }
                sb.append(" ** ").append(Translator.R("restDay")).append(" ** ").append(t.getRestday().getDescription());
                breakLine(html, sb);
                if (html) {
                    sb.append("</i>");
                    breakLine(html, sb);
                }
            }

        }
        if (html) {
            sb.append("</body></html>");
        }
        return sb.toString();
    }

//    public MergedExerciseWrapper getMergedExercises(TimeShift t) {
//        List<ExerciseOverrides> l = getExerciseOverrides();
//        List<MergedExercise> r = new ArrayList<MergedExercise>(l.size());
//        for (ExerciseOverrides get : l) {
//            r.add(MergedExercise.MergedExercise(get, t));
//        }
//        return new MergedExerciseWrapper(r);
//    }
    @Override
    public File export(File root, ImagesSaver im) throws IOException {
        String dir = "cycle-" + getId() + ".html";
        String index1 = "index.html";
        String index2 = "index.txt";
        File mainDir = new File(root, dir);
        mainDir.mkdir();
        File indexFile1 = new File(mainDir, index1);
        File indexFile2 = new File(mainDir, index2);
        File imgDir = new File(mainDir, Training.IMGS_SUBDIR);
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
        im.writeTrainingsImagesToDir(imgDir, Arrays.asList(ti));
        for (TrainingOverrides t : getTrainingOverrides()) {
            List<String> ei = t.getTraining().getExerciseImages();
            im.writeExercisesImagesToDir(imgDir, ei);
        }

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
        return new File(Cycles.getStatsDir(), getId());
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
        String counter = fr.readLine();
        if (counter == null) {
            return;
        }
        trainingPointer = Integer.valueOf(counter.trim());
        statistics.clear();
        while (true) {
            String s = fr.readLine();
            if (s == null) {
                break;
            }
            if (s.trim().isEmpty()) {
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
        fr.write(String.valueOf(trainingPointer));
        fr.newLine();
        for (Record r : statistics) {
            fr.write(r.toString());
            fr.newLine();

        }

    }

    /**
     * Pointer is 1-size (not 0-(size-1)).
     *
     * @return current training based on saved state
     */
    public int getTrainingPointer() {
        load();
        return trainingPointer;
    }

    public String getTrainingPointerToString() {
        load();
        int tp = trainingPointer - 1;
        if (tp <= 0 || tp >= trainings.size()) {
            return Translator.R("last");
        }
        return tp + "/"+trainings.size();
    }

    public void incTrainingPointer() {
        int i = getTrainingPointer();
        i++;
        if (i >= trainings.size()) {
            i = trainings.size();
        }
        setTrainingPointer(i);

    }

    public void decTrainingPointer() {
        int i = getTrainingPointer();
        i--;
        if (i < 1) {
            i = 1;
        }
        setTrainingPointer(i);
    }

    private void setTrainingPointer(int i) {
        trainingPointer = i;
        save();
    }

    @Override
    public List<Record> getRecords() {
        load();
        return Collections.unmodifiableList(statistics);
    }

    private Record lastRecord;

    @Override
    public synchronized void addRecord(Record r) {
        load();
        if (lastRecord == null) {
            statistics.add(r);
            save();
        } else {
            Record q = lastRecord;
            if (Math.abs(q.compareTo(r)) > Record.minTime || q.getWhat() != r.getWhat()) {//somebody clicking to fast?
                statistics.add(r);
                save();
            }
        }
        lastRecord = r;

    }

    public void startCyclesTraining() {
        int i = getTrainingPointer();
        if (getTrainingPointer() == 1) {
            getStatsHelper().started("training: " + i + " - " + getTraining().getIdAsMcro());
        } else if (getTrainingPointer() == getTrainingOverrides().size()) {
            getStatsHelper().finished(" at: " + i + " - " + getTraining().getIdAsMcro());
        } else {
            getStatsHelper().continued(" training: " + i + " - " + getTraining().getIdAsMcro());
        }
        incTrainingPointer();
        if (i == getTrainingPointer()) {//no change => i++ would cross max, but incTraining did not
            setTrainingPointer(1);
        }
    }

    @Override
    public StatisticHelper getStatsHelper() {
        return new StatisticHelper(this);
    }

    public void modified(String m) {
        addRecord(Record.create(RecordType.MODIFIED, m));
    }

    @Override
    public List<String> getExerciseImages() {
        //to much
        return new ArrayList<String>(0);
    }

    @Override
    public Cycle getCycle() {
        return this;
    }
}

package org.fbb.balkna.model.primitives;

import java.io.BufferedReader;
import org.fbb.balkna.model.ImagesSaver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.fbb.balkna.model.Translator.R;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.primitives.history.Record;
import org.fbb.balkna.model.utils.XmlUtils;
import org.w3c.dom.Node;
import static org.fbb.balkna.model.utils.XmlConstants.*;
import org.w3c.dom.Element;

/**
 *
 * @author jvanek
 */
public class Cycle implements Substituable {

    private final String id;
    private final String name;
    private final List<LocalisedString> localisedNames;
    private final String description;
    private final List<LocalisedString> localisedDescriptions;
    private final List<String> images;
    private final List<TrainingOverrides> trainings;
    
    private int trainingPointer = 1;
    private List<Record> statistics = new ArrayList<Record>();

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
        Model.substitute(images, this);
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

    public List<TrainingOverrides> getTrainingOverrides() {
        return Collections.unmodifiableList(trainings);
    }

    public Training getTraining(int which) {
        return trainings.get(which).getTraining();
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
    public String getStory() {
        return getStory(false);
    }

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
        for (TrainingOverrides t : getTrainingOverrides()) {
            i++;
            sb.append(t.getHeader(i, html));
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
        }
//        MergedExerciseWrapper m = getMergedExercises(Model.getModel().getTimeShift());
//        sb.append(R("TotalTime", TimeUtils.secondsToHours(m.getTime())));
//        breakLine(html, sb);
//        sb.append(R("TotalTimeExercise", TimeUtils.secondsToHours(m.getActiveTime())));
//        breakLine(html, sb);
//        sb.append(R("TotalTimeResting", TimeUtils.secondsToHours(m.getRestTime())));
//        breakLine(html, sb);
//        sb.append(R("TotalExercises", m.getIterations()));
//        breakLine(html, sb);
//        sb.append(R("TotalDifferentExercises", m.getSize()));
//        breakLine(html, sb);
//        sb.append("----");
//        breakLine(html, sb);
//        sb.append(m.getStory(html));
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
    public File export(File root, ImagesSaver im) throws FileNotFoundException, IOException {
        String dir = "exported.html";
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
//        List<String> ei = getExerciseImages();
//        im.writeExercisesImagesToDir(imgDir, ei);
        im.writeTrainingsImagesToDir(imgDir, Arrays.asList(ti));
        return indexFile1;
    }

    private void breakLine(boolean html, StringBuilder sb) {
        if (html) {
            sb.append("<br>");
        }
        sb.append("\n");
    }
    
    private static File getStatsFile(){
        File f =  new File(Model.getModel().getStatsDir(), "cycles");
        if (!f.exists()){
            f.mkdirs();
        }
        return f;
    }
    
     private File getFile(){
        return new File(getStatsFile(), getId());
    }

    public void load() {
        try{
            loadUncought();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void loadUncought() throws IOException {
        File f = getFile();
          if (f.exists()) {
                BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                String counter = fr.readLine();
                if (counter == null){
                    return;
                }
                trainingPointer= Integer.valueOf(counter.trim());
                statistics.clear();
                while (true) {
                    String s = fr.readLine();
                    if (s == null) {
                        break;
                    }
                    statistics.add(Record.fromString(s));
                }
            }
        
    }

}

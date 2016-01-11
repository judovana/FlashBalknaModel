package org.fbb.balkna.model.primitives;

import org.fbb.balkna.model.Substituable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.Statisticable;
import org.fbb.balkna.model.primitives.history.NonRepeatedArrayList;
import org.fbb.balkna.model.primitives.history.Record;
import org.fbb.balkna.model.primitives.history.RecordType;
import org.fbb.balkna.model.primitives.history.StatisticHelper;
import org.fbb.balkna.model.utils.IoUtils;
import org.fbb.balkna.model.utils.XmlUtils;
import static org.fbb.balkna.model.utils.XmlUtils.getRealChilds;
import org.w3c.dom.Node;
import static org.fbb.balkna.model.utils.XmlConstants.*;
import org.w3c.dom.Element;

/**
 *
 * @author jvanek
 */
public class Exercise implements Substituable, Statisticable {

    private final String id;
    private final String name;
    private final List<LocalisedString> localisedNames;
    private final String description;
    private final List<LocalisedString> localisedDescriptions;
    private final List<String> images;
    //defaults
    private final Integer time; //default time of exercise in seconds
    private final Integer pause; //default time of pause in seconds
    private final Integer iterations; //defualt number of iterations
    private final Integer rest; //defualt time of rest after all iterations

    private final List<Record> statistics = new NonRepeatedArrayList<Record>();
    private boolean load = false;

    private Exercise(String id, String name, String des, Integer time, Integer pause, Integer iters, Integer rest, List<LocalisedString> localisedNames, List<LocalisedString> localisedDescriptions, List<String> images) {
        if (id == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.name = name;
        this.description = des;
        this.time = time;
        this.pause = pause;
        this.iterations = iters;
        this.rest = rest;
        this.localisedNames = localisedNames;
        this.localisedDescriptions = localisedDescriptions;
        this.images = images;
        Model.substituteImages(images, this);
    }

    public static Exercise parse(final Node node, SetDefaults defaults) {

        String id = null;
        String name = null;
        List<LocalisedString> localisedNames = new ArrayList<LocalisedString>();
        String description = null;
        List<LocalisedString> localisedDescriptions = new ArrayList<LocalisedString>();
        List<String> images = new ArrayList<String>();
        Integer time = null;
        Integer pause = null;
        Integer iterations = null;
        Integer rest = null;
        List<Element> interlayer = XmlUtils.getRealChilds(node);
        for (Element n : interlayer) {
            //jdk6 comaptible:(
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
            } else if (n.getNodeName().equals(DEFAULTS)) {
                List<Element> l = getRealChilds(n);
                for (Element nn : l) {
                    if (nn.getNodeName().equals(TIME)) {
                        time = Integer.valueOf(nn.getTextContent());
                    } else if (nn.getNodeName().equals(PAUSE)) {
                        pause = Integer.valueOf(nn.getTextContent());
                    } else if (nn.getNodeName().equals(ITERATIONS)) {
                        iterations = Integer.valueOf(nn.getTextContent());
                    } else if (nn.getNodeName().equals(REST)) {
                        rest = Integer.valueOf(nn.getTextContent());
                        break;
                    }
                }
                break;

            }
        }
        if (time == null) {
            time = defaults.getTime();
        }
        if (pause == null) {
            pause = defaults.getPause();
        }
        if (iterations == null) {
            iterations = defaults.getIterations();
        }
        if (rest == null) {
            rest = defaults.getRest();
        }
        return new Exercise(id, name, description, time, pause, iterations, rest, localisedNames, localisedDescriptions, images);
    }

    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getIdAsMcro() {
        return "%{e-"+getId()+";"+getName()+"}";
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
        String s = getDescriptionIml();
        if (s == null) {
            return getName();
        }
        return s;
    }

    private String getDescriptionIml() {
        String s = LocalisedString.findLocalised(localisedDescriptions);
        if (s != null) {
            return s;
        }
        return description;
    }

    public Integer getTime() {
        return time;
    }

    public Integer getPause() {
        return pause;
    }

    public Integer getIterations() {
        return iterations;
    }

    public Integer getRest() {
        return rest;
    }

    public List<String> getImages() {
        return Collections.unmodifiableList(images);
    }

    @Override
    public String toString() {
        return getName();
    }

    List<LocalisedString> getLocalisedNames() {
        return localisedNames;
    }

    List<LocalisedString> getLocalisedDescriptions() {
        return localisedDescriptions;
    }


    @Override
    public File getFile() {
        return new File(Exercises.getStatsDir(), getId());
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
            if (Math.abs(q.compareTo(r)) > Record.minTime && q.getWhat()!=r.getWhat()) {//somebody clicking to fast?
                statistics.add(r);
                save();
            }
        }
        lastRecord = r;

    }
    
      @Override
    public StatisticHelper getStatsHelper() {
        return new StatisticHelper(this);
    }

}

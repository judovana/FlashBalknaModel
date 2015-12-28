package org.fbb.balkna.model.primitives;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.primitives.history.Record;
import org.fbb.balkna.model.primitives.history.RecordWithOrigin;
import org.fbb.balkna.model.utils.XmlConstants;
import org.fbb.balkna.model.utils.XmlUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author jvanek
 */
public class Cycles {

    public List<RecordWithOrigin> gatherStatistics() {
        String[] names = getStatsDir().list();
        ArrayList<RecordWithOrigin> a = new ArrayList<RecordWithOrigin>(names.length);
        for (String name : names) {
            Cycle i = getCycleById(name);
            if (i != null) {
                List<Record> rs = i.getRecords();
                for (Record r : rs) {
                    a.add(new RecordWithOrigin(i, r));
                }
            }
        }
        return a;
    }

    private static class CyclesHolder {

        public static Cycles instance;

        public static Cycles getInstance() {
            if (instance == null) {
                instance = new Cycles();
            }
            return instance;
        }

        public static Cycles reloadInstance() {
            instance = new Cycles();
            return instance;
        }
    }

    public static Cycles getInstance() {
        return CyclesHolder.getInstance();
    }

    public static Cycles reloadInstance() {
        return CyclesHolder.reloadInstance();
    }

    private final List<Cycle> cycles = new ArrayList<Cycle>();

    public Cycles() {
        try {
            loadDefaults();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadDefaults() {
        try {
            List<Element> all = XmlUtils.getDataNodes("cycles", XmlConstants.CYCLE);
            for (Node node : all) {
                Cycle ex = Cycle.parse(node);
                cycles.add(ex);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public List<Cycle> getCycles() {
        return Collections.unmodifiableList(cycles);
    }

    public static File getStatsDir() {
        File f = new File(Model.getModel().getStatsDir(), "cycles");
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    public Cycle getCycleById(String id) {
        for (Cycle training : cycles) {
            if (training.getId().equals(id)) {
                return training;
            }
        }
        return null;
    }
}

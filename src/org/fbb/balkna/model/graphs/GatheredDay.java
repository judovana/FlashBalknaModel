package org.fbb.balkna.model.graphs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.fbb.balkna.model.primitives.history.RecordWithOrigin;

/**
 *
 * @author jvanek
 */
public class GatheredDay implements Steppable<GatheredDay> {

    private final Date day;
    private final List<RecordWithOrigin> sources = new ArrayList<RecordWithOrigin>();
    private SimpleDateFormat easyFormater;
    private long step;

    public GatheredDay(Date day) {
        this.day = day;
    }

    public GatheredDay(Date day, RecordWithOrigin gathered) {
        this(day);
        sources.add(gathered);
    }

    /**
     * @return the day
     */
    public Date getDay() {
        return day;
    }

    @Override
    public int compareTo(GatheredDay o) {
        return (int) (this.day.getTime() - o.day.getTime());
    }

    @Override
    public long getStep() {
        return step;
    }

    @Override
    public void setStep(long step) {
        this.step = step;
    }

    @Override
    public void setEasyFormater(SimpleDateFormat easyFormater) {
        this.easyFormater = easyFormater;
    }

    @Override
    public SimpleDateFormat getEasyFormater() {
        return easyFormater;
    }

    public String getAdaptedDateTime() {
        return easyFormater.format(day);
    }

    public void addRecord(RecordWithOrigin source) {
        sources.add(source);
    }

    public List<RecordWithOrigin> getSources() {
        return sources;
    }

    @Override
    public String toString() {
        String s = day.toString();
        if (getEasyFormater() != null) {
            s = getAdaptedDateTime();
        }
        s += ":  " + clazzesToString(sources);
        return s;
    }

    private String clazzesToString(List<RecordWithOrigin> sources) {
        StringBuilder sb = new StringBuilder();
        for (RecordWithOrigin source : sources) {
            sb.append(source.toString()).append("; ");
        }
        return sb.toString();
    }

}

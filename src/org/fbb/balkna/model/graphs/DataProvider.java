package org.fbb.balkna.model.graphs;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.primitives.Cycle;
import org.fbb.balkna.model.primitives.Exercise;
import org.fbb.balkna.model.primitives.Training;
import org.fbb.balkna.model.primitives.history.RecordType;
import org.fbb.balkna.model.primitives.history.RecordWithOrigin;

/**
 *
 * @author jvanek
 */
public class DataProvider {

    private static final DataProvider instance = new DataProvider();

    public DataProvider() {
    }

    public static DataProvider getDataProvider() {
        return instance;
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
//fil cleverly!!! dok day, fake day, ... "dday" , fake day ... ok day

    public Map<Date, DayStatst> getDayData(boolean ex, boolean tr, boolean cy) {
        return getDayData(1, ex, tr, cy, hour);
    }

    public Map<Date, DayStatst> getHourData(boolean ex, boolean tr, boolean cy) {
        return getDayData(1, ex, tr, cy, minut);
    }

    public Map<Date, DayTimes> getMonthTimeData(boolean ex, boolean tr, boolean cy) {
        return getDayTimeData(35, ex, tr, cy, day);
    }

    public Map<Date, DayTimes> getWeekTimeData(boolean ex, boolean tr, boolean cy) {
        return getDayTimeData(10, ex, tr, cy, day);
    }

    public Map<Date, DayTimes> getYearTimeData1(boolean ex, boolean tr, boolean cy) {
        return getDayTimeData(365, ex, tr, cy, month);
    }

    public Map<Date, DayTimes> getYearTimeData2(boolean ex, boolean tr, boolean cy) {
        return getDayTimeData(365, ex, tr, cy, week);
    }
//fil cleverly!!! dok day, fake day, ... "dday" , fake day ... ok day

    public Map<Date, DayTimes> getDayTimeData(boolean ex, boolean tr, boolean cy) {
        return getDayTimeData(1, ex, tr, cy, hour);
    }

    public Map<Date, DayTimes> getHourTimeData(boolean ex, boolean tr, boolean cy) {
        return getDayTimeData(1, ex, tr, cy, minut);
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
     * @param period - days
     * @param ex
     * @param tr
     * @param cy
     * @return
     */
    private Map<Date, DayTimes> getDayTimeData(int period, boolean ex, boolean tr, boolean cy, long step) {
        long p = period;
        p = p * day;
        return getDayTimeData(p, ex, tr, cy, step);
    }

    /**
     *
     * @param period - in ms, and max 356day - see usage of DayOfYear
     * @param ex
     * @param tr
     * @param cy
     * @return
     */
    private Map<Date, DayTimes> getDayTimeData(long period, boolean ex, boolean tr, boolean cy, long step) {
        Map<Date, GatheredDay> raws = getRawData(period, ex, tr, cy, step);
        Map<Date, DayTimes> result = new HashMap(raws.size());

        Set<Map.Entry<Date, GatheredDay>> vs = raws.entrySet();
        for (Map.Entry<Date, GatheredDay> v : vs) {

            Date day = v.getKey();
            GatheredDay raw = v.getValue();
            List<RecordWithOrigin> gathereds = raw.getSources();
            Collections.sort(gathereds); //THE MUST!
            //the result should be get form set, so actually always null there, and never get again, but...
            DayTimes get = result.get(day);
            if (get == null) {
                get = new DayTimes(day);
                setStep(get, step);
                result.put(day, get);
            }
            for (int i = 0; i < gathereds.size(); i++) {
                // are we on cycle? add+1
                //are we on start of tr/ex - find end of tr ex. If os,, get time difference
                // if another start in meantime (of same class) then scratch as error
                RecordWithOrigin record = gathereds.get(i);
                if (record.getRecord().getWhat().equals(RecordType.NOW)) {
                    if (gathereds.size() == 1) {
                        get.detach();//only now... just info then, otherwise vauess will appear
                    }
                    continue;
                }
                if (record.getOrigin() instanceof Cycle) {
                    get.incCycles(1);
                    continue;
                }
                if (record.getOrigin() instanceof Exercise || record.getOrigin() instanceof Training) {
                    if (record.getRecord().getWhat().equals(RecordType.STARTED)) {
                        //modified and continued should be cycles only
                        Class clazz = record.getOrigin().getClass();
                        for (int ii = i + 1; ii < gathereds.size(); ii++) {
                            RecordWithOrigin record2 = gathereds.get(ii);
                             if (!record2.getOrigin().getClass().equals(clazz)){
                                continue;
                            }
                            if (record2.getRecord().getWhat().equals(RecordType.STARTED)
                                    || record2.getRecord().getWhat().equals(RecordType.MODIFIED)
                                    || record2.getRecord().getWhat().equals(RecordType.CONTINUED)
                                    || record2.getRecord().getWhat().equals(RecordType.NOW)) {
                                break;
                            }
                            //same class and finished/cNCELED/FINISHEDwithSkippsa
                            long realTime = Math.abs(record.getRecord().getWhen()-record2.getRecord().getWhen());
                            //ms to seconds
                            realTime = (realTime/1000l);///60l; minute is to much!
                            get.incExTr(clazz, realTime);
                         }
                    }
                }
            }
            if (!cy){
                get.detachCy();
            }
            if (!tr){
                get.detachTr();
            }
            if (!ex){
                get.detachEx();
            }
        }
        return result;
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
        Map<Date, GatheredDay> raws = getRawData(period, ex, tr, cy, step);
        Map<Date, DayStatst> result = new HashMap(raws.size());

        Set<Map.Entry<Date, GatheredDay>> vs = raws.entrySet();
        for (Map.Entry<Date, GatheredDay> v : vs) {

            Date day = v.getKey();
            GatheredDay raw = v.getValue();
            List<RecordWithOrigin> gathereds = raw.getSources();
            //the result should be get form set, so actually always null there, and never get again, but...
            DayStatst get = result.get(day);
            if (get == null) {
                get = new DayStatst(day);
                setStep(get, step);
                result.put(day, get);
            }

            for (RecordWithOrigin gathered : gathereds) {
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
        }
        return result;

    }

    /**
     *
     * @param period - in ms, and max 356day - see usage of DayOfYear
     * @param ex
     * @param tr
     * @param cy
     * @return
     */
    private Map<Date, GatheredDay> getRawData(long period, boolean ex, boolean tr, boolean cy, long step) {
        List<RecordWithOrigin> gathereds = Model.getModel().gatherStatistics(ex, tr, cy);
        Map<Date, GatheredDay> result = new HashMap(gathereds.size() / 3);
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
            GatheredDay get = result.get(day);
            if (get == null) {
                get = new GatheredDay(day);
                setStep(get, step);
                result.put(day, get);
            }
            get.addRecord(gathered);

        }

        return result;

    }

    private static void setStep(Steppable get, long step) {
        get.setStep(step);
        if (step <= hour) {
            get.setEasyFormater(DayStatst.sdfDay);
        } else {
            get.setEasyFormater(DayStatst.sdfMore);
        }
    }

}

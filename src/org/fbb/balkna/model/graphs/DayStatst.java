package org.fbb.balkna.model.graphs;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jvanek
 */
public class DayStatst implements Comparable<DayStatst> {

    private final Date day;
    private final Map<Class, Integer> passes = new HashMap<Class, Integer>();
    private final Map<Class, Integer> failes = new HashMap<Class, Integer>();
    private final Map<Class, Integer> startss = new HashMap<Class, Integer>();
    private SimpleDateFormat easyFormater;
    private long step;

    public DayStatst(Date day) {
        this.day = day;
    }

    public static DayStatst fake(Date day, Collection<Class> classes) {
        DayStatst d = new DayStatst(day);
        d.detach(classes);
        return d;
    }

    public boolean isNothing() {
        return isNothing(passes)
                && isNothing(failes)
                && isNothing(startss);
    }

    private static boolean isNothing(Map<Class, Integer> m) {
        Iterable<Class> classes = m.keySet();
        for (Class classe : classes) {
            if (getValue(m, classe) > 0) {
                return false;
            }
        }
        return true;
    }

    public void detach(Collection<Class> classes) {
        detachOne(passes, classes);
        detachOne(failes, classes);
        detachOne(startss, classes);
    }

    public void detach() {
        detachOne(passes);
        detachOne(failes);
        detachOne(startss);
    }

    private static void detachOne(Map<Class, Integer> map) {
        detachOne(map, map.keySet());
    }

    private static void detachOne(Map<Class, Integer> map, Collection<Class> classes) {
        for (Class classe : classes) {
            map.put(classe, -1);
        }
    }

    /**
     * @return the day
     */
    public Date getDay() {
        return day;
    }

    public void incPass(Class c) {
        incValue(passes, c);
    }

    public void incFail(Class c) {
        incValue(failes, c);
    }

    public void incStart(Class c) {
        incValue(startss, c);
    }

    private static int incValue(Map<Class, Integer> map, Class c) {
        Integer i = map.get(c);
        if (i == null) {
            map.put(c, 1);
            return 1;
        } else {
            map.put(c, i + 1);
            return i+1;
        }
    }

    public int getPasses(Class c) {
        return getValue(passes, c);

    }

    private static int getValue(Map<Class, Integer> map, Class c) {
        Integer i = map.get(c);
        if (i == null) {
            return 0;
        } else {
            return i;
        }
    }

    public int getFails(Class c) {
        return getValue(failes, c);

    }

    public int getStarts(Class c) {
        return getValue(startss, c);

    }

    @Override
    public int compareTo(DayStatst o) {
        return (int) (this.day.getTime() - o.day.getTime());
    }

    public int getMax() {
        return Math.max(Math.max(getMax(passes), getMax(failes)), getMax(startss));
    }

    private int getMaxi(Collection<Integer> c) {
        int max = 0;
        for (Integer c1 : c) {
            if (c1 == null) {
                continue;
            }
            max = Math.max(max, c1);
        }
        return max;
    }

    private int getMax(Map<Class, Integer> map) {
        return getMaxi(map.values());
    }

    public void fillClasses(Set<Class> classes) {
        fillClasses(classes, passes);
        fillClasses(classes, failes);
        fillClasses(classes, startss);
    }

    private void fillClasses(Set<Class> classes, Map<Class, Integer> passes) {
        Set<Class> k = passes.keySet();
        for (Class k1 : k) {
            classes.add(k1);

        }
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }

    public void setEasyFormater(SimpleDateFormat easyFormater) {
        this.easyFormater = easyFormater;
    }

    public static final SimpleDateFormat sdfMore = new SimpleDateFormat("d.M");
    public static final SimpleDateFormat sdfDay = new SimpleDateFormat("HH.mm");

    public String getAdaptedDateTime() {
        return easyFormater.format(day);
    }

}

package org.fbb.balkna.model.graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jvanek
 */
public abstract class AbstractGraph {

    protected static int CWIDTH = 40;

 
    

    protected static int getMaxRecord(List<DayStatst> list) {
        int max = 0;
        for (DayStatst list1 : list) {
            if (list1.getMax() > max) {
                max = list1.getMax();
            }
        }
        return max;
    }
    
    protected static int getMaxTimeInTimes(List<DayTimes> list) {
        int max = 0;
        for (DayTimes list1 : list) {
            if (list1.getMaxTime()> max) {
                max = list1.getMaxTime();//seconds
            }
        }
        return max/60;//minutes!
    }

    
    protected static int getMaxCyclesuseInTimes(List<DayTimes> list) {
        int max = 0;
        for (DayTimes list1 : list) {
            if (list1.getCyclesOperationms()> max) {
                max = list1.getCyclesOperationms();
            }
        }
        return max;
    }
    protected static List<Class> getClasses(List<DayStatst> list) {
        Set<Class> classes = new HashSet<Class>(4);
        int max = 0;
        for (DayStatst list1 : list) {
            list1.fillClasses(classes);
        }
        List<Class> l = new ArrayList<Class>(classes);
        Collections.sort(l, new Comparator<Class>() {

            @Override
            public int compare(Class o1, Class o2) {
                return o1.getSimpleName().compareTo(o2.getSimpleName());
            }
        });
        return l;
    }

    

    protected static List<DayStatst> getDataAsList(Map<Date, DayStatst> data) {
        Collection<DayStatst> vals = data.values();
        List<DayStatst> list = new ArrayList<DayStatst>(vals);
        Collections.sort(list);
        if (list.isEmpty()) {
            return list;
        }
        dealWithNow(list);
        return list;
    }
    
    protected static List<DayTimes> getTimeDataAsList(Map<Date, DayTimes> data) {
        Collection<DayTimes> vals = data.values();
        List<DayTimes> list = new ArrayList<DayTimes>(vals);
        Collections.sort(list);
        if (list.isEmpty()) {
            return list;
        }
        //dealWithNow(list);
        //now should not be already dealed earlier
        return list;
    }

    protected static void dealWithNow(List<DayStatst> list) {
        DayStatst l1 = list.get(0);
        DayStatst l2 = list.get(list.size() - 1);
        dealNow(l1, getClasses(list));
        dealNow(l2, getClasses(list));
    }

    protected static void dealNow(DayStatst l1, List<Class> classes) {
        if (l1.isNothing()) {
            l1.detach(classes);
        }
    }
}

package org.fbb.balkna.model.primitives.history;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author jvanek
 */
public class Record  implements Comparable<Record>{
    
    public static final int minTime = 500;

    final RecordType what;
    final long when;

    private Record(long when, RecordType what) {
        this.what = what;
        this.when = when;
    }

    @Override
    public String toString() {
        return when + " " + what;
    }

    public String toNiceString() {
        return format(when) + " " + what.toNiceString();
    }

    public static Record create(RecordType what) {
        return new Record(System.currentTimeMillis(), what);

    }

    public static Record fromString(String s) {
        String[] ss = s.trim().split("\\s+");
        return new Record(Long.valueOf(ss[0]), RecordType.valueOf(ss[1]));
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd EEEEEEEE HH:mm:ss");

    private static String format(long when) {
        return sdf.format(new Date(when));
    }
    
//    public static void main(String[] args) {
//        System.out.println(Record.create(Type.STARTED).toNiceString());
//    }

    @Override
    public int compareTo(Record t) {
        return (int) (when-t.when);
    }

    public RecordType getWhat() {
        return what;
    }

    public long getWhen() {
        return when;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Record)){
            return false;
        }
        Record r = (Record) obj;
        return  r.what == this.what && r.when == this.when;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.what != null ? this.what.hashCode() : 0);
        hash = 37 * hash + (int) (this.when ^ (this.when >>> 32));
        return hash;
    }
    
    
    
    
    
    
}

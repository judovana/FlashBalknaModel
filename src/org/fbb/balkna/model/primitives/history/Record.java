package org.fbb.balkna.model.primitives.history;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author jvanek
 */
public class Record {

    final Type what;
    final long when;

    private Record(long when, Type what) {
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

    public static Record create(Type what) {
        return new Record(System.currentTimeMillis(), what);

    }

    public static Record fromString(String s) {
        String[] ss = s.trim().split("\\s\\+");
        return new Record(Long.valueOf(ss[0]), Type.valueOf(ss[1]));
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String format(long when) {
        return sdf.format(new Date(when));
    }
    
    
}

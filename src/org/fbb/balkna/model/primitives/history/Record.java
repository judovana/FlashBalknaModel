package org.fbb.balkna.model.primitives.history;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.fbb.balkna.model.Substituable;
import org.fbb.balkna.model.primitives.Cycles;
import org.fbb.balkna.model.primitives.Exercises;
import org.fbb.balkna.model.primitives.Trainings;

/**
 *
 * @author jvanek
 */
public class Record implements Comparable<Record> {

    public static final int minTime = 500;

    final RecordType what;
    final long when;
    final String message;

    public static boolean SHOW_MESSAGE = false;

    private Record(long when, RecordType what, String message) {
        this.what = what;
        this.when = when;
        this.message = message;
    }

    @Override
    public String toString() {
        String s = when + " " + what;
        if (SHOW_MESSAGE && message != null) {
            s = s + " " + message;
        }
        return s;
    }

    public String toNiceString() {
        String s = format(when) + " " + what.toNiceString();
        if (SHOW_MESSAGE && message != null) {
            s = s + " - " + processIDs(message);
        }
        return s;
    }

    public static Record create(RecordType what, String message) {
        return new Record(System.currentTimeMillis(), what, message);

    }

    public static Record fromString(String s) {
        s = s.trim();
        String[] ss = s.split("\\s+");
        int i = s.indexOf(ss[1]);
        String message = s.substring(i + ss[1].length()).trim();
        return new Record(Long.valueOf(ss[0]), RecordType.valueOf(ss[1]), message);
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
        return (int) (when - t.when);
    }

    public RecordType getWhat() {
        return what;
    }

    public long getWhen() {
        return when;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Record)) {
            return false;
        }
        Record r = (Record) obj;
        return r.what == this.what && r.when == this.when;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.what != null ? this.what.hashCode() : 0);
        hash = 37 * hash + (int) (this.when ^ (this.when >>> 32));
        return hash;
    }

    private String processIDs(String message) {
        String orogonal = message;
        try {
            while (message.contains("%{")) {
                int start = message.indexOf("%{");
                int end = message.indexOf("}");
                String macroBody = message.substring(start + 2, end);
                String[] macroParts = macroBody.split(";");
                String[] keys = macroParts[0].split("-");
                String substitued;
                Substituable found = null;
                if (keys[0].equals("c")) {
                    found = Cycles.getInstance().getCycleById(keys[1]);
                } else if (keys[0].equals("t")) {
                    found = Trainings.getInstance().getTrainingById(keys[1]);
                } else if (keys[0].equals("e")) {
                    found = Exercises.getInstance().getExerciseById(keys[1]);
                };
                if (found != null) {
                    substitued = found.getName();
                } else {
                    substitued = macroParts[1];
                }
                message = message.substring(0, start) + substitued + message.substring(end + 1);
            }
            return message;
        } catch (Exception ex) {
            return orogonal + "(" + ex.toString() + ")";
        }
    }

}

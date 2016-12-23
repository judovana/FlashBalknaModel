package org.fbb.balkna.model.merged;

import java.util.ArrayList;
import java.util.List;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BasicTime;

/**
 *
 * @author jvanek
 */
public class MergedExerciseWrapper {

    private final List<MergedExercise> src;

    public MergedExerciseWrapper(List<MergedExercise> r) {
        this.src = r;
    }

    public int getSize() {
        return src.size();
    }

    public int getIterations() {
        int i = 0;
        for (MergedExercise src1 : src) {
            i += src1.getIterations();
        }
        return i;
    }

    public int getActiveTime() {
        int i = 0;
        for (MergedExercise src1 : src) {
            i += src1.getIterations() * src1.getTime();
        }
        return i;
    }

    public int getRestTime() {
        int i = 0;
        for (MergedExercise src1 : src) {
            i += (src1.getIterations() - 1) * src1.getPause() + src1.getRest();
        }
        return i;
    }

    public String getStory(boolean html) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (MergedExercise src1 : src) {
            i++;
            sb.append(src1.getStory(i, html));
            breakLine(html, sb);

        }
        return sb.toString();
    }

    private void breakLine(boolean html, StringBuilder sb) {
        if (html) {
            sb.append("<br>");
        }
        sb.append("\n");
    }

    public int getTime() {
        return getRestTime() + getActiveTime();
    }

    public List<BasicTime> decompress() {
        List<BasicTime> r = new ArrayList<BasicTime>();
        for (int i = 0; i < src.size(); i++) {
            MergedExercise src1 = src.get(i);
            List<BasicTime> decompressed = src1.decompress(null);
            if (i < src.size() - 1) {
                MergedExercise src2 = src.get(i + 1);
                List<BasicTime> decompressed2 = src2.decompress(null);
                if (decompressed2.size() > 0) {
                    decompressed = src1.decompress(decompressed2.get(0));
                }
            }
            r.addAll(decompressed);
        }
        return r;

    }

}

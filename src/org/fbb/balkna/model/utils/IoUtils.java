package org.fbb.balkna.model.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.Statisticable;

/**
 *
 * @author jvanek
 */
public class IoUtils {

    public static void saveStatisticable(Statisticable i) throws IOException {
        if (!Model.getModel().isSaveStats()) {
            return;
        }
        File f = i.getFile();
        BufferedWriter fr = null;
        try {
            fr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "utf-8"));
            i.save(fr);
        } finally {
            if (fr != null) {
                fr.close();
            }
        }
    }

    public static void loadStatisticable(Statisticable i) throws IOException {
        File f = i.getFile();
        if (f.exists()) {
            BufferedReader fr = null;
            try {
                fr = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                i.load(fr);
            } finally {
                if (fr != null) {
                    fr.close();
                }
            }
        }
    }

    public static URL getFile(String subPackage, String fileName) {
        String res = subPackage + ".";
        res = "/" + res.replace(".", "/") + fileName;
        URL found = IoUtils.class.getResource(res);
        //regular classapth
        if (found != null) {
            return found;
        }
        //plugins
        return JavaPluginProvider.getPluginPaths().findFile(res);

    }

    public static List<URL> getFiles(String subPackage, String fileRoot, String suffix) {
        List<URL> l = new ArrayList<URL>();
        int i = 0;
        while (true) {
            i++;
            URL in = getFile(subPackage, fileRoot + i + "." + suffix);
            if (i >= 1000) {
                break;
            }
            if (in == null) {
                //break; try all 1000
                continue;
            }
            l.add(in);
        }
        return l;

    }

    public static StringBuilder htmlWrap(StringBuilder s) {
        s.insert(0, "<html>").append("</html>");
        return s;
    }

}

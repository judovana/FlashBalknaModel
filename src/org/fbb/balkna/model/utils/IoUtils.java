package org.fbb.balkna.model.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jvanek
 */
public class IoUtils {
    
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

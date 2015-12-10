package org.fbb.balkna.model.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.fbb.balkna.model.PluginFactoryProvider;

/**
 *
 * @author jvanek
 */
public class JavaPluginProvider implements PluginFactoryProvider {

    public static class PluginsPaths {

        private static PluginsPaths instance;

        public static PluginsPaths getInstance() {
            if (instance == null) {
                instance = new PluginsPaths();
            }
            return instance;
        }

        private List<File> paths = new ArrayList<File>();

        public void addPath(String s) {
            if (s == null) {
                return;
            }
            paths.add(new File(s));
        }

        public void addPath(File s) {
            if (s == null) {
                return;
            }
            paths.add(s);
        }

        public void removePath(String s) {
            if (s == null) {
                return;
            }
            removePath(new File(s));
        }

        public void removePath(File s) {
            if (s == null) {
                return;
            }
            for (int i = 0; i < paths.size(); i++) {
                File get = paths.get(i);
                if (get.equals(s)) {
                    paths.remove(i);
                    i--;
                }

            }
        }

        private static final Map<String, URL> foundCache = new HashMap<String, URL>();
        private static final URL NOT_FOUND;
        private static final Map<File, List<String>> searchCache = new HashMap<File, List<String>>();

        static {
            try {
                NOT_FOUND = new URL("file", "NOT_FOUND", "NOT_FOUND");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        URL findFile(String res) {
            URL cached = foundCache.get(res);
            if (cached != null) {
                if (cached == NOT_FOUND) {
                    return null;
                }
                return cached;
            }
            //search for full path
            URL r1 = searchInZips(res);
            if (r1 != null) {
                foundCache.put(res, r1);
                return r1;
            } else {
                foundCache.put(res, NOT_FOUND);
            }
            //desperate attempt to find in fwrongly packed plugins            
            //remove path
            res = res.substring(res.lastIndexOf("/"), res.length());

            URL r2 = searchInZips(res);
            if (r1 != null) {
                foundCache.put(res, r2);
                return r2;
            } else {
                foundCache.put(res, NOT_FOUND);
            }
            return null;

        }

        private URL searchInZips(String res) {
            List<File> l = Collections.unmodifiableList(JavaPluginProvider.getPluginPaths().paths);
            return searchInZips(l, res);
        }

        private URL searchInZips(List<File> l, String res) {
            for (File f : l) {
                if (f != null && f.exists()) {
                    URL r = searchInFile(res, f);
                    if (r != null) {
                        return r;
                    }
                }
            }
            return null;
        }

        private URL searchInFile(String res, File f) {
            List<String> l = searchCache.get(f);
            URL result = null;
            if (l == null) {
                List<String> ccache = new ArrayList<String>();
                try {
                    InputStream theFile = new FileInputStream(f);
                    ZipInputStream stream = new ZipInputStream(theFile);
                    ZipEntry entry;
                    while ((entry = stream.getNextEntry()) != null) {
                        String zippath = "/" + entry.getName();
                        ccache.add(zippath);
                        if (zippath.equals(res)) {
                            //eg                            
//jar:file:/home/jvanek/git/FlashBalknaModel/dist/FlashBalknaModel.jar!/org/fbb/balkna/data/imgs/app/title1.png
                            if (result == null) {
                                result = new URL("jar:file:" + f.getAbsolutePath() + "!" + zippath);
                                //continue reading, fill searchCache
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                searchCache.put(f, ccache);
                return result;
            } else {
                if (l.contains(res)) {
                    try {
                        return new URL("jar:file:" + f.getAbsolutePath() + "!" + res);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return null;
            }
        }

    }

    public static PluginsPaths getPluginPaths() {
        return PluginsPaths.getInstance();
    }

    @Override
    public void addResource(URL u) {
        PluginsPaths.searchCache.clear();
        PluginsPaths.foundCache.clear();
        getPluginPaths().addPath(u.getFile());

    }

}

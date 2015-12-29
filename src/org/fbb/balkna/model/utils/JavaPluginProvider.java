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
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.PluginFactoryProvider;

/**
 *
 * @author jvanek
 */
public class JavaPluginProvider implements PluginFactoryProvider {

    private static void clearCache() {
        PluginsPaths.searchCache1.clear();
        PluginsPaths.foundCache1.clear();
        PluginsPaths.searchCache2.clear();
        PluginsPaths.foundCache2.clear();
    }

    public static class PluginsPaths {

        private static PluginsPaths instance;

        public static PluginsPaths getInstance() {
            if (instance == null) {
                instance = new PluginsPaths();
            }
            return instance;
        }

        private final List<File> paths = new ArrayList<File>();

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
            s.delete();
            clearCache();
            Model.getModel().reload();
        }

        private static final Map<String, URL> foundCache1 = new HashMap<String, URL>();
        private static final Map<String, URL> foundCache2 = new HashMap<String, URL>();
        private static final URL NOT_FOUND;
        private static final Map<File, List<String>> searchCache1 = new HashMap<File, List<String>>();
        private static final Map<File, List<String>> searchCache2 = new HashMap<File, List<String>>();

        static {
            try {
                NOT_FOUND = new URL("file", "NOT_FOUND", "NOT_FOUND");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        URL findFile(String resOrig) {
            URL cached1 = foundCache1.get(resOrig);
            String res = resOrig.substring(resOrig.lastIndexOf("/"), resOrig.length());
            URL cached2 = foundCache2.get(res);
            if (cached1 != null) {
                if (cached1 == NOT_FOUND) {
                    if (cached2 != null) {
                        if (cached2 == NOT_FOUND) {
                            return null;
                        }
                        return cached2;
                    }
                }
                return cached1;
            }
            //search for full path
            URL r1 = searchInZips(resOrig, searchCache1);
            if (r1 != null) {
                foundCache1.put(resOrig, r1);
                return r1;
            } else {
                foundCache1.put(resOrig, NOT_FOUND);
            }
            //desperate attempt to find in wrongly packed plugins            
            //remove path
            URL r2 = searchInZips(res, searchCache2);
            if (r2 != null) {
                foundCache2.put(res, r2);
                return r2;
            } else {
                foundCache2.put(res, NOT_FOUND);
            }
            return null;

        }

        private URL searchInZips(String res, Map<File, List<String>> searchCache) {
            List<File> l = Collections.unmodifiableList(JavaPluginProvider.getPluginPaths().paths);
            return searchInZips(l, res, searchCache);
        }

        private static URL searchInZips(List<File> l, String res, Map<File, List<String>> searchCache) {
            for (File f : l) {
                if (f != null && f.exists()) {
                    URL r = searchInFile(res, f, searchCache);
                    if (r != null) {
                        return r;
                    }
                }
            }
            return null;
        }

        private static URL searchInFile(String res, File f, Map<File, List<String>> searchCache) {
            List<String> l = searchCache.get(f);
            URL result = null;
            if (l == null) {
                List<String> ccache = new ArrayList<String>();
                InputStream theFile = null;
                ZipInputStream stream = null;
                try {
                    theFile = new FileInputStream(f);
                    stream = new ZipInputStream(theFile);
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
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
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
        clearCache();
        getPluginPaths().addPath(u.getFile());

    }

}

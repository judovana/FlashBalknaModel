package org.fbb.balkna.model.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.PluginFactoryProvider;

/**
 *
 * @author jvanek
 */
public class JavaPluginProvider extends PluginFactoryProvider {

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

        private static List<String> mapMathces(List<String> l, String res) {
            List<String> r = new ArrayList<String>();
            for (String s : l) {
                if (s.matches(res)) {
                    r.add(s);
                }
            }
            return r;
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
            URL r1 = searchInPlugins(resOrig, searchCache1);
            if (r1 != null) {
                foundCache1.put(resOrig, r1);
                return r1;
            } else {
                foundCache1.put(resOrig, NOT_FOUND);
            }
            //desperate attempt to find in wrongly packed plugins            
            //remove path
            URL r2 = searchInPlugins(res, searchCache2);
            if (r2 != null) {
                foundCache2.put(res, r2);
                return r2;
            } else {
                foundCache2.put(res, NOT_FOUND);
            }
            return null;

        }

        private URL searchInPlugins(String res, Map<File, List<String>> searchCache) {
            List<File> l = Collections.unmodifiableList(JavaPluginProvider.getPluginPaths().paths);
            return searchInPlugins(l, res, searchCache);
        }

        private static URL searchInPlugins(List<File> l, String res, Map<File, List<String>> searchCache) {
            for (File f : l) {
                if (f != null && f.exists()) {
                    if (f.isFile()) {
                        URL r = searchInFile(res, f, searchCache);
                        if (r != null) {
                            return r;
                        }
                    }
                    if (f.isDirectory()) {
                        URL r = searchInDir(res, f, searchCache);
                        if (r != null) {
                            return r;
                        }
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

        private static URL searchInDir(String res, File f, Map<File, List<String>> searchCache) {
            List<String> l = searchCache.get(f);
            URL result = null;
            if (l == null) {
                List<String> ccache = new ArrayList<String>();
                try {
                    List<File> files = listAllFiles(f);
                    for (File entry : files) {
                        String zippath = entry.getAbsolutePath().substring(f.getAbsolutePath().length());
                        // musi zacinat /
                        ccache.add(zippath);
                        if (zippath.equals(res)) {
                            //eg                            
//file:/home/jvanek/git/FlashBalknaModel/dist/FlashBalknaModel/org/fbb/balkna/data/imgs/app/title1.png
                            if (result == null) {
                                result = entry.toURI().toURL();
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
                        return new File(f.getAbsolutePath() + "/" + res).toURI().toURL();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return null;
            }
        }

        private List<URL> findFiles(String regex) {
            List<URL> r = new ArrayList<URL>();
            //search for full path
            searchInPlugins(regex, searchCache1, r);
            //desperate attempt to find in wrongly packed plugins
            if (regex.contains("/") && regex.lastIndexOf("/") != regex.indexOf("/")) {
                String res = regex.substring(regex.lastIndexOf("/"), regex.length());
                //remove path
                searchInPlugins(res, searchCache2, r);
            }
            return r;

        }

        private List<URL> searchInPlugins(String res, Map<File, List<String>> searchCache, List<URL> result) {
            List<File> l = Collections.unmodifiableList(JavaPluginProvider.getPluginPaths().paths);
            return searchInPlugins(l, res, searchCache, result);
        }

        private static List<URL> searchInPlugins(List<File> l, String res, Map<File, List<String>> searchCache, List<URL> results) {
            for (File f : l) {
                if (f != null && f.exists()) {
                    if (f.isFile()) {
                        List<URL> r = searchInFile(res, f, searchCache, results);
                        results.addAll(r);
                    }
                    if (f.isDirectory()) {
                        List<URL> r = searchInDir(res, f, searchCache, results);
                        results.addAll(r);
                    }
                }
            }
            return results;
        }

        private static List<URL> searchInFile(String res, File f, Map<File, List<String>> searchCache, List<URL> result) {
            List<String> l = searchCache.get(f);
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
                        if (zippath.matches(res)) {
                            //eg                            
//jar:file:/home/jvanek/git/FlashBalknaModel/dist/FlashBalknaModel.jar!/org/fbb/balkna/data/imgs/app/title1.png
                            result.add(new URL("jar:file:" + f.getAbsolutePath() + "!" + zippath));
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
                List<String> cachedMatches = mapMathces(l, res);
                for (String cachedMatche : cachedMatches) {
                    try {
                        result.add(new URL("jar:file:" + f.getAbsolutePath() + "!" + cachedMatche));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return result;
            }
        }

        private static List<URL> searchInDir(String res, File f, Map<File, List<String>> searchCache, List<URL> result) {
            List<String> l = searchCache.get(f);
            if (l == null) {
                List<String> ccache = new ArrayList<String>();
                try {
                    List<File> files = listAllFiles(f);
                    for (File entry : files) {
                        String zippath = entry.getAbsolutePath().substring(f.getAbsolutePath().length());
                        // musi zacinat /
                        ccache.add(zippath);
                        if (zippath.matches(res)) {
                            //eg                            
//file:/home/jvanek/git/FlashBalknaModel/dist/FlashBalknaModel/org/fbb/balkna/data/imgs/app/title1.png
                            result.add(entry.toURI().toURL());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                searchCache.put(f, ccache);
                return result;
            } else {
                List<String> cachedMatches = mapMathces(l, res);
                for (String cachedMatche : cachedMatches) {
                    try {
                        result.add(new File(f.getAbsolutePath() + "/" + cachedMatche).toURI().toURL());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return result;
            }
        }
    }

    @Override
    public List<URL> searchResourceInPlugins(String regex) throws Exception {
        List<URL> r = getPluginPaths().findFiles(regex);
        Set<URL> s = new HashSet<URL>(r.size());
        for (URL u : r) {
            s.add(u);
        }
        r.clear();
        r.addAll(s);
        return r;
    }

    public static PluginsPaths getPluginPaths() {
        return PluginsPaths.getInstance();
    }

    @Override
    public void addResource(URL u) {
        clearCache();
        getPluginPaths().addPath(u.getFile());

    }

    private static List<File> listAllFiles(File f) {
        List<File> result = new ArrayList<File>();
        File[] ff = f.listFiles();
        for (File file : ff) {
            if (file.isDirectory()) {
                result.addAll(listAllFiles(file));
            }
            if (file.isFile()) {
                result.add(file);
            }
        }
        return result;

    }

}

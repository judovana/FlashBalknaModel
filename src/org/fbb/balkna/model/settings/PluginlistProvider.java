package org.fbb.balkna.model.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jvanek
 */
public class PluginlistProvider {

    private static final String FILE_NAME = "known-plugins";
    private static final String exUrl = "https://raw.githubusercontent.com/judovana/FlashBalknaModel/master/src/org/fbb/balkna/" + FILE_NAME;
    private static final String inPath = "/org/fbb/balkna/" + FILE_NAME;

    private static URL getExURL() throws MalformedURLException {
        return new URL(exUrl);
    }

    private static URL getInURL() throws MalformedURLException {
        return PluginlistProvider.class.getResource(inPath);
    }

    private static URL getCachedURL() throws MalformedURLException {
        return getCacheFile().toURI().toURL();
    }

    private static File getCacheFile() throws MalformedURLException {
        return new File(System.getProperty("java.io.tmpdir"), FILE_NAME);
    }

    public static enum PluginState {

        STABLE, TESTING, UNKNOWN;

    }

    public static class ParsedLine {

        private final String stateOrig;
        private final PluginState state;
        private final String homePageOrig;
        private final URL homePage;
        private final String urlOrig;
        private final URL url;
        private final String description;

        public ParsedLine(String stateOrig, String homePageOrig, String urlOrig, String description) throws MalformedURLException {
            this.stateOrig = stateOrig;
            this.homePageOrig = homePageOrig;
            this.urlOrig = urlOrig;
            this.description = description;
            homePage = new URL(this.homePageOrig);
            url = new URL(this.urlOrig);
            if (this.stateOrig.equalsIgnoreCase("testing")) {
                state = PluginState.TESTING;
            } else if (this.stateOrig.equalsIgnoreCase("stable")) {
                state = PluginState.STABLE;
            } else {
                state = PluginState.UNKNOWN;
            }
        }

        public URL getHomePage() {
            return homePage;
        }

        public PluginState getState() {
            return state;
        }

        public String getDescription() {
            return description;
        }

        public URL getUrl() {
            return url;
        }

        private String getLine() {
            return stateOrig + ";;" + homePageOrig + ";;" + urlOrig + ";;" + description;
        }

    }

    private static List<ParsedLine> readFromUrl(URL u) throws IOException {
        List<ParsedLine> r = new ArrayList<ParsedLine>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(u.openStream(), StandardCharsets.UTF_8));
            while (true) {
                String s = in.readLine();
                if (s == null) {
                    break;
                }
                s = s.trim();
                if (s.startsWith("#")) {
                    continue;
                }
                String[] ss = s.split(";;+");
                r.add(new ParsedLine(ss[0].trim(), ss[1].trim(), ss[2].trim(), ss[3].trim()));
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return r;
    }

    private static void cache(List<ParsedLine> items) throws IOException {
        save(getCacheFile(), items);
    }

    private static void save(File f, List<ParsedLine> items) throws IOException {
        BufferedWriter in = null;
        try {
            in = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8));
            for (ParsedLine item : items) {
                in.write(item.getLine());
                in.newLine();
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }

    }

    public static enum LoadedPluginsState {

        NETWORK, CACHE, LOCAL, FATALTY;
    }

    public static class LoadedPlugins {

        private final List<ParsedLine> r;
        private final LoadedPluginsState w;

        public LoadedPlugins(List<ParsedLine> r, LoadedPluginsState w) {
            this.r = r;
            this.w = w;
        }

        @Override
        public String toString() {
            return super.toString() + " - " + r.size() + " - " + w;
        }

    }

    public static LoadedPlugins obtain() {
        List<ParsedLine> r = null;
        //network
        try {
            r = readFromUrl(getExURL());
            if (r != null && !r.isEmpty()) {
                try {
                    //save it
                    cache(r);
                    System.out.println("Cache updated sucesfully. " + getCacheFile());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                System.out.println("Loaded remote file. " + getExURL());
                return new LoadedPlugins(r, LoadedPluginsState.NETWORK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //cache
        try {
            r = readFromUrl(getCachedURL());
            if (r != null && !r.isEmpty()) {
                System.out.println("Read from cache: " + getCacheFile());
                return new LoadedPlugins(r, LoadedPluginsState.CACHE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            r = readFromUrl(getInURL());
            if (r != null && !r.isEmpty()) {
                System.out.println("Read from app: " + getInURL());
                return new LoadedPlugins(r, LoadedPluginsState.LOCAL);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("All ways failed");
        return new LoadedPlugins(new ArrayList<PluginlistProvider.ParsedLine>(0), LoadedPluginsState.FATALTY);
    }

    public static void main(String... args) {
        LoadedPlugins r = obtain();
        System.out.println("" + r);
    }

}

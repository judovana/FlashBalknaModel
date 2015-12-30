package org.fbb.balkna.model;

import java.net.URL;
import java.util.List;
import org.fbb.balkna.model.utils.JavaPluginProvider;

/**
 *
 * @author jvanek
 */
public abstract class  PluginFactoryProvider {

     private static class PluginFactoryProviderHolder {

        //https://en.wikipedia.org/wiki/Double-checked_locking#Usage_in_Java
        //https://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom
        private static PluginFactoryProvider INSTANCE;

        private static PluginFactoryProvider getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new JavaPluginProvider();
            }
            return PluginFactoryProviderHolder.INSTANCE;
        }

        
    }

    public static PluginFactoryProvider getInstance() {
        return PluginFactoryProviderHolder.getInstance();
    }

    
    
    public abstract void addResource(URL u) throws Exception ;
    public abstract List<URL> searchResourceInPlugins(String regex) throws Exception ;
    
}

package org.fbb.balkna.model.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.fbb.balkna.model.PluginFactoryProvider;

/**
 *
 * @author jvanek
 */
public class JavaPluginProvider implements PluginFactoryProvider {

    private static final Class[] parameters = new Class[]{URL.class};

    @Override
    public void addResource(URL u) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        //java
        if (ClassLoader.getSystemClassLoader() instanceof URLClassLoader) {
            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class sysclass = URLClassLoader.class;

            Method method = sysclass.getDeclaredMethod("addURL", parameters);

            method.setAccessible(
                    true);
            method.invoke(sysloader,
                    new Object[]{u});
        } else {
                //android and its http://developer.android.com/reference/dalvik/system/PathClassLoader.html
            //where one must add item to
            //structured lists of path elements */
            //private final DexPathList pathList;
        }
    }

}

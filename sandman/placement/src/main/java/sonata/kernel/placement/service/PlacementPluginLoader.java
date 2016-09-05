package sonata.kernel.placement.service;


import sonata.kernel.placement.service.DefaultPlacementPlugin;
import sonata.kernel.placement.service.PlacementPlugin;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;

public final class PlacementPluginLoader {
	
	final static Logger logger = Logger.getLogger(PlacementPluginLoader.class);
    public static PlacementPlugin placementPlugin;

    public static void loadPlacementPlugin(String path, String pluginName) {
    	logger.info("Loading Placement Plugin");
        placementPlugin = createPlacementPlugin(path, pluginName);
    }

    public static PlacementPlugin createPlacementPlugin(String path, String pluginName){
    	logger.info("Creating placement Plugin");
    	logger.debug("Plugin pathe: "+path);
    	logger.debug("Plugin Name: "+pluginName);
        try {
            // Get ClassLoader for plugin folder
            File pluginFolder = new File(path);

            if (!pluginFolder.exists())
                return new DefaultPlacementPlugin();

            URL[] urls = new URL[]{pluginFolder.toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);

            // Get specified Class
            Class cls = loader.loadClass(pluginName);

            // Check if Class implements PlacementPlugin interface
            if(!PlacementPlugin.class.isAssignableFrom(cls)) {
                System.out.println(cls.getName()+" is not an instance of the PlacementPlugin interface!");
                return new DefaultPlacementPlugin();
            }

            // Check if Class can be instantiated
            if(Modifier.isInterface(cls.getModifiers()) || Modifier.isAbstract(cls.getModifiers())){
                System.out.println(cls.getName()+" can not be instantiated!");
                return new DefaultPlacementPlugin();
            }

            // If the Class misses an empty constructor instantiation will fail.
            return (PlacementPlugin) cls.newInstance();

        } catch (MalformedURLException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
            return new DefaultPlacementPlugin();
        }
    }

}

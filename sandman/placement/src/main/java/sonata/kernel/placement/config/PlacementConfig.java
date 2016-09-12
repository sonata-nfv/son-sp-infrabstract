package sonata.kernel.placement.config;

import java.util.ArrayList;

public class PlacementConfig {

    public String pluginPath;
    public String placementPlugin;
    public ArrayList<PopResource> resources;

	public ArrayList<PopResource> getResources() {
        return resources;
    }

    public void setResources(ArrayList<PopResource> resources) {
        this.resources = resources;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public String getPlacementPlugin() {
        return placementPlugin;
    }

    public void setPlacementPlugin(String placementPlugin) {
        this.placementPlugin = placementPlugin;
    }

}

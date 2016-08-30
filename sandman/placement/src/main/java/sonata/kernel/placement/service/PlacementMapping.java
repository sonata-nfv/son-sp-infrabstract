package sonata.kernel.placement.service;

import sonata.kernel.placement.config.PopResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacementMapping {

    public final List<PopResource> resources;

    // Maps instance node names to resource node names
    public final Map<String,String> mapping;

    // Maps instance node names to pop
    // should be consistent to the "mapping" map
    public final Map<String,PopResource> popMapping;


    public PlacementMapping(){
        mapping = new HashMap<String,String>();
        resources = new ArrayList<PopResource>();
        popMapping = new HashMap<String,PopResource>();
    }

}

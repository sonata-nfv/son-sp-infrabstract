package sonata.kernel.placement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sun.xml.internal.ws.api.wsdl.parser.ServiceDescriptor;

import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.placement.pd.PackageContentObject;
import sonata.kernel.placement.pd.PackageDescriptor;
import sonata.kernel.placement.pd.SonataPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Catalogue {

    static public List<String> packages = new ArrayList<String>();

    // Maps Vnf name to VnfDescriptor
    static public Map<String,VnfDescriptor> functions = new HashMap<String,VnfDescriptor>();


    static public int addPackage(String sPackage){
        int newIndex = -1;
        int oldIndex = -1;
        for(int i=0; i<packages.size(); i++) {
        	String  existPackage = packages.get(i);
            if(existPackage.equals(sPackage)) {
                oldIndex = i;
                break;
            }
        }
        // If already existing, replace!
        if(oldIndex != -1) {
            packages.remove(oldIndex);
            packages.add(oldIndex, sPackage);
            newIndex = oldIndex;
        }
        else { // Add new package
            packages.add(sPackage);
            newIndex = packages.size()-1;
        }
        // Replace or add Vnfs
        /*for(VnfDescriptor newVnf:sPackage.functions) {
            functions.put(newVnf.getName(),newVnf);
        }*/
        return newIndex;
    }
    
    static public DeployServiceData getPackagetoDeploy(int index) {
    	    	
    	return null;
    }

    static public String getJsonPackageDescriptor(int index){
        String jsonData = null;
        ObjectMapper mapper = getJsonMapper();

       // String package_dir = packages.get(index);
        String pack_dir = packages.get(index);
        /*try {
            jsonData = mapper.writeValueAsString(desc);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/

        return pack_dir;
    }

    static public String getJsonPackageList(){
       /* String jsonList = null;

        List<PackageDescriptor> packageList = new ArrayList<PackageDescriptor>();
        for(String p:packages)
            packageList.add(p.descriptor);

        ObjectMapper mapper  = getJsonMapper();
        try {
            jsonList = mapper.writeValueAsString(packageList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/
		
        return null;
    }
    static protected ObjectMapper getJsonMapper(){
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Unit.class, new UnitDeserializer());
        mapper.registerModule(module);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return mapper;
    }
}

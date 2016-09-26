package sonata.kernel.placement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.junit.Assert;

import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.heat.HeatResource;
import sonata.kernel.VimAdaptor.commons.heat.HeatTemplate;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.VimAdaptor.wrapper.openstack.Flavor;
import sonata.kernel.VimAdaptor.wrapper.openstack.OpenStackHeatWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import sonata.kernel.placement.config.PlacementConfig;
import sonata.kernel.placement.service.*;

class DescriptorTranslator		
{
	final static Logger logger = Logger.getLogger(DescriptorTranslator.class);
    public static String process_descriptor(String base_dir) throws IOException
    {
    	logger.debug("Base Directory is : " + base_dir);
        ServiceDescriptor sd = null;
        List<VnfDescriptor> vnfd_list = new ArrayList<VnfDescriptor>();

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SimpleModule module = new SimpleModule();
 
        File dir_sd = new File(base_dir + "/sd/");
        if(dir_sd.isDirectory())
        {
            File[] sd_files =  new File(base_dir + "/sd/").listFiles();
            if(sd_files[0].isFile() && sd_files[0].canRead()){
                System.out.println("DescriptorTranslator::process_descriptor(): Processing file : "
                        + sd_files[0].getAbsolutePath());
                logger.debug("DescriptorTranslator::process_descriptor(): Processing file : "+ sd_files[0].getAbsolutePath());


                StringBuilder bodyBuilder = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        new FileInputStream(new File(sd_files[0].getAbsolutePath())), Charset.forName("UTF-8")));
                String line;
                while ((line = in.readLine()) != null)
                    bodyBuilder.append(line + "\n\r");

                module.addDeserializer(Unit.class, new UnitDeserializer());
                mapper.registerModule(module);
                mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
                sd = mapper.readValue(bodyBuilder.toString(), ServiceDescriptor.class);
            }

        } else {
            throw new RuntimeException("DescriptorTranslator::process_descriptor(): Error : Service Descriptor directory " +
                    "not found");
        }

        File dir_vnfd = new File(base_dir + "/vnfd/");
        if(dir_vnfd.isDirectory())
        {
            File[] sd_files =  new File(base_dir + "/vnfd/").listFiles();
            for (File file : sd_files) {
                if (file.isFile() && file.canRead()) {
                    System.out.println("DescriptorTranslator::process_descriptor(): Processing file : "
                            + file.getAbsolutePath());
                    logger.debug("DescriptorTranslator::process_descriptor(): Processing file :"+file.getAbsolutePath() );
                    VnfDescriptor vnfd;
                    StringBuilder bodyBuilder = new StringBuilder();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            new FileInputStream(new File(file.getAbsolutePath())), Charset.forName("UTF-8")));
                    String line = null;
                    while ((line = in.readLine()) != null)
                        bodyBuilder.append(line + "\n\r");
                    vnfd = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);
                    vnfd_list.add(vnfd);
                }
            }

        } else {
            throw new RuntimeException("DescriptorTranslator::process_descriptor(): Error : VNF Descriptor directory " +
                    "not found");
        }

        DeployServiceData data = new DeployServiceData();
        data.setServiceDescriptor(sd);

        for( VnfDescriptor vnfd : vnfd_list){
            data.addVnfDescriptor(vnfd);
        }

        PlacementConfig config = PlacementConfigLoader.loadPlacementConfig();
        PlacementPlugin plugin = new DefaultPlacementPlugin();
        ServiceInstance instance = plugin.initialScaling(data);
        PlacementMapping mapping = plugin.initialPlacement(data, instance, config.getResources());
        List<HeatTemplate> templates = ServiceHeatTranslator.translatePlacementMappingToHeat(instance, config.getResources(), mapping);

/*
        WrapperConfiguration config = new WrapperConfiguration();

        config.setTenantExtNet("decd89e2-1681-427e-ac24-6e9f1abb1715");
        config.setTenantExtRouter("20790da5-2dc1-4c7e-b9c3-a8d590517563");

        OpenStackHeatWrapper wrapper = new OpenStackHeatWrapper(config);

        ArrayList<Flavor> vimFlavors = new ArrayList<Flavor>();
        vimFlavors.add(new Flavor("m1.small", 2, 2048, 20));

        ObjectMapper mapper2 = new ObjectMapper(new JsonFactory());
        HeatTemplate template;
        HeatStackCreate createStack = new HeatStackCreate();
        createStack.stackName = "MyLittleStack";
        try {
            template = wrapper.getHeatTemplateFromSonataDescriptor(data, vimFlavors);
            mapper2.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
            mapper2.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            mapper2.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
            mapper2.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
            mapper2.setSerializationInclusion(Include.NON_NULL);
            createStack.template = template;

            System.out.println("+++++++++++++++++++++++++++++++");
            System.out.println(template.getResources());
            logger.debug("Template Resources: "+template.getResources());
            System.out.println("+++++++++++++++++++++++++++++++");
            String body = mapper2.writeValueAsString(template);
            logger.debug("Body: "+body);
            System.out.println(body);
            return body;

           // Assert.assertNotNull(body);
        } catch (Exception e) {
            System.out.println("Exception translating template.");
            e.printStackTrace();
        }
*/

        for(HeatTemplate template: templates) {
            HeatStackCreate createStack = new HeatStackCreate();
            createStack.stackName = "MyLittleStack";
            createStack.template = template;
            ObjectMapper mapper2 = new ObjectMapper(new JsonFactory());
            mapper2.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
            mapper2.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            mapper2.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
            mapper2.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
            mapper2.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            module.addDeserializer(Unit.class, new UnitDeserializer());
            mapper2.registerModule(module);
            mapper2.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            try {
                String body = mapper2.writeValueAsString(template);
                logger.debug("Template Resources: "+template.getResources());
                logger.info("Body: "+body);
                return body;
            } catch (JsonProcessingException e) {
                logger.error("Exception translating template.");
                e.printStackTrace();
            }
        }

        return null;
    }
}
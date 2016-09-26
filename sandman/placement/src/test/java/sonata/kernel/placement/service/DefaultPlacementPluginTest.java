package sonata.kernel.placement.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Test;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.heat.Stack;
import org.openstack4j.openstack.OSFactory;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.heat.HeatTemplate;
import sonata.kernel.VimAdaptor.commons.vnfd.ResourceRequirements;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.placement.HeatStackCreate;
import sonata.kernel.placement.PackageLoader;
import sonata.kernel.placement.PlacementConfigLoader;
import sonata.kernel.placement.config.PlacementConfig;
import sonata.kernel.placement.config.PopResource;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DefaultPlacementPluginTest {

    @Test
    public void testOne() {

        System.out.println(new File("").getAbsolutePath());

        PlacementConfig config = PlacementConfigLoader.loadPlacementConfig();

        DeployServiceData data = PackageLoader.loadPackageFromDisk(Paths.get("YAML", "test.son").toString());

        PlacementPlugin plugin = new DefaultPlacementPlugin();

        ServiceInstance instance = plugin.initialScaling(data);

        PlacementMapping mapping = plugin.initialPlacement(data, instance, config.getResources());

        List<HeatTemplate> templates = ServiceHeatTranslator.translatePlacementMappingToHeat(instance, config.getResources(), mapping);

        assert templates.size()==1;

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
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Unit.class, new UnitDeserializer());
            mapper2.registerModule(module);
            mapper2.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            try {
                String body = mapper2.writeValueAsString(template);
                System.out.println(body);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        /*
        List<PopResource> resources = config.getResources();
        for(int i=0; i<resources.size(); i++) {
            PopResource res = resources.get(i);
            HeatTemplate template = templates.get(i);
            String templateStr = null;
            try {
                templateStr = templateToJson(template);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            assert templateStr!=null;
            String endpoint = res.getEndpoint();
            String username = res.getUserName();
            String password = res.getPassword();
            String tenantName = res.getTenantName();


            OSClient.OSClientV2 os = OSFactory.builderV2()
                    .endpoint( res.getEndpoint() + "/v2.0")
                    .credentials(username,password)
                    .tenantName(tenantName)
                    .authenticate();

            Stack stack = os.heat().stacks().create(Builders.stack()
                    .name("XYZ")
                    .template(templateStr)
                    .timeoutMins(5L).build());
            List<? extends Stack> stackList = os.heat().stacks().list();
        }
        */




        List<Object> nodeList = new ArrayList<Object>();
        // add first node as example
        nodeList.add(mapping.mapping.keySet().iterator().next());

        ScaleMessage trigger = new ScaleMessage(ScaleMessage.SCALE_TYPE.SCALE_OUT, nodeList);

        ServiceInstance updatedInstance = plugin.updateScaling(data, instance, trigger);

        PlacementMapping updatedMapping = plugin.updatePlacement(data, updatedInstance, config.getResources(), mapping);

        // Remove Stacks
        /*
        for(int i=0; i<resources.size(); i++) {
            PopResource res = resources.get(i);
            HeatTemplate template = templates.get(i);
            String templateStr = null;
            try {
                templateStr = templateToJson(template);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            assert templateStr!=null;
            String endpoint = res.getEndpoint();
            String username = res.getUserName();
            String password = res.getPassword();
            String tenantName = res.getTenantName();


            OSClient.OSClientV2 os = OSFactory.builderV2()
                    .endpoint( res.getEndpoint() + "/v2.0")
                    .credentials(username,password)
                    .tenantName(tenantName)
                    .authenticate();
            List<? extends Stack> stackList = os.heat().stacks().list();
            for(Stack stack: stackList){
                os.heat().stacks().delete(stack.getName(),stack.getId());
            }
        }
        */

    }

    public static String templateToJson(HeatTemplate template) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        SimpleModule module = new SimpleModule();
        mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        module.addDeserializer(Unit.class, new UnitDeserializer());
        mapper.registerModule(module);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return mapper.writeValueAsString(template);
    }
}
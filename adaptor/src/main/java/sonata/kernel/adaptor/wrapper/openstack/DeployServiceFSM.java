package sonata.kernel.adaptor.wrapper.openstack;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import sonata.kernel.adaptor.commons.DeployServiceData;
import sonata.kernel.adaptor.commons.DeploymentResponse;
import sonata.kernel.adaptor.commons.heat.HeatModel;
import sonata.kernel.adaptor.wrapper.WrapperConfiguration;
import sonata.kernel.adaptor.wrapper.WrapperStatusUpdate;

public class DeployServiceFSM implements Runnable {

  private String sid;
  private DeployServiceData data;
  private OpenStackHeatWrapper wrapper;
  private OpenStackHeatClient client;
  private HeatModel stack;
  final private int maxCounter = 5;

  public DeployServiceFSM(OpenStackHeatWrapper wrapper, OpenStackHeatClient client, String sid,
      DeployServiceData data, HeatModel stack) {

    this.wrapper = wrapper;
    this.client = client;
    this.sid = sid;
    this.data = data;
    this.stack = stack;
  }

  @Override
  public void run() {
    DeploymentResponse response = new DeploymentResponse();
    try {
      System.out.println("[OS-Deploy-FSM] Deploying new stack");
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
      mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
      mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
      mapper.setSerializationInclusion(Include.NON_NULL);
      System.out.println("[OS-Deploy-FSM]   Serializing stack...");

      String stackString = mapper.writeValueAsString(stack);

      String stackName = data.getNsd().getName() + UUID.randomUUID().toString().substring(0, 8);
      System.out.println("[OS-Deploy-FSM]   Pushing stack to Heat...");
      String instanceUuid = client.createStack(stackName, stackString);

      int counter = 0;
      int wait = 1000;
      while (counter < maxCounter) {
        String status = client.getStackStatus(stackName, instanceUuid);

        try {
          Thread.sleep(wait);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        wait *= 2;
      }
      response.setInstanceName(stackName);
      response.setInstanceVimUuid(instanceUuid);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      response.setErrorCode("TranslationError");
    }

    WrapperStatusUpdate update = new WrapperStatusUpdate(this.sid, "success", response.toString());
    wrapper.notifyObservers(update);
  }

}

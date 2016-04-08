package sonata.kernel.adaptor;

import java.util.UUID;

import sonata.kernel.adaptor.messaging.ServicePlatformMessage;

public class HeartBeat implements Runnable {

  private AdaptorMux mux;
  private AdaptorCore core;
  private double rate; // measured in beat/s
  private boolean stop;

  public HeartBeat(AdaptorMux mux, double rate, AdaptorCore core) {
    this.mux = mux;
    this.rate = rate;
    this.core = core;
  }

  @Override
  public void run() {
    String uuid = core.getUUID();
    while (!stop) {
      try {
        Thread.sleep((int) ((1 / rate) * 1000));
        String body = "{\"uuid\":\"" + uuid + "\",\"state\":\"" + core.getState() + "\"}";
        ServicePlatformMessage message = new ServicePlatformMessage(body,
            "platform.management.plugin." + uuid + ".heartbeat", UUID.randomUUID().toString());
        mux.enqueue(message);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  public void stop() {
    this.stop = true;
  }

}

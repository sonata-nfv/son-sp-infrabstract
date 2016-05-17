package sonata.kernel.adaptor.commons.vnfd;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.adaptor.commons.vnfd.Unit.TimeUnit;

public class VduMonitoringRules {

  
  private String name;
  private String description;
  private String condition;
  private double duration;
  @JsonProperty("duration_unit")
  private TimeUnit durationUnit;
  private ArrayList<Notification> notification;
  
  public void setName(String name) {
    this.name = name;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public void setCondition(String condition) {
    this.condition = condition;
  }
  public void setDuration(double duration) {
    this.duration = duration;
  }
  public void setDurationUnit(TimeUnit durationUnit) {
    this.durationUnit = durationUnit;
  }
  public void setNotification(ArrayList<Notification> notification) {
    this.notification = notification;
  }
  public String getName() {
    return name;
  }
  public String getDescription() {
    return description;
  }
  public String getCondition() {
    return condition;
  }
  public double getDuration() {
    return duration;
  }
  public TimeUnit getDurationUnit() {
    return durationUnit;
  }
  public ArrayList<Notification> getNotification() {
    return notification;
  }
  
  
  
}

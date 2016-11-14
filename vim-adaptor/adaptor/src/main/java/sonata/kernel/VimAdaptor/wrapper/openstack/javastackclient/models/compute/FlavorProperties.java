package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.compute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlavorProperties {

    private String id;
    private String name;
    private String vcpus;
    private String disk;
    private String ram;

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public String getVcpus() {
        return vcpus;
    }

    public void setVcpus(String vcpus) {
        this.vcpus = vcpus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String toString(){
      String out="";
      
      out+=id+"   "+name+"   "+vcpus+" vcpus   "+ram+" mem   "+disk+" disk";
      
      return out;
    }
}

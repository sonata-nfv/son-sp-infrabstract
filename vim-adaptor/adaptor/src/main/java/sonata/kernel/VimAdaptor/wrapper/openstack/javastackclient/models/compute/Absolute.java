package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.compute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Absolute {

    private String totalRAMUsed;
    private String maxTotalRAMSize;
    private String totalCoresUsed;
    private String maxTotalCores;

    public String getTotalRAMUsed() {
        return totalRAMUsed;
    }

    public void setTotalRAMUsed(String totalRAMUsed) {
        this.totalRAMUsed = totalRAMUsed;
    }

    public String getMaxTotalRAMSize() {
        return maxTotalRAMSize;
    }

    public void setMaxTotalRAMSize(String maxTotalRAMSize) {
        this.maxTotalRAMSize = maxTotalRAMSize;
    }

    public String getTotalCoresUsed() {
        return totalCoresUsed;
    }

    public void setTotalCoresUsed(String totalCoresUsed) {
        this.totalCoresUsed = totalCoresUsed;
    }

    public String getMaxTotalCores() {
        return maxTotalCores;
    }

    public void setMaxTotalCores(String maxTotalCores) {
        this.maxTotalCores = maxTotalCores;
    }
}

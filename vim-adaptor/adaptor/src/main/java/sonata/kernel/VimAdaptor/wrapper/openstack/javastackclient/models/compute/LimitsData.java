package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.compute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LimitsData {
    private Limits limits;

    public Limits getLimits() {
        return limits;
    }

    public void setLimits(Limits limits) {
        this.limits = limits;
    }
}

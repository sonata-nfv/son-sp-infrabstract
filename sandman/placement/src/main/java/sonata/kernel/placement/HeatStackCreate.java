package sonata.kernel.placement;

import com.fasterxml.jackson.annotation.JsonProperty;
import sonata.kernel.VimAdaptor.commons.heat.HeatTemplate;

public class HeatStackCreate {

    @JsonProperty("stack_name")
    public String stackName;

    public HeatTemplate template;
}

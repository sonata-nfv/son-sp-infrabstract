package sonata.kernel.placement.service;

import java.util.List;

public class ScaleMessage {

    public enum SCALE_TYPE {
        SCALE_OUT,
        SCALE_IN
    }

    public final SCALE_TYPE type;
    // TODO: define type for scale subject
    public final List<Object> node_list;

    public ScaleMessage(SCALE_TYPE type, List<Object> node_list){
        this.type = type;
        this.node_list = node_list;
    }



}

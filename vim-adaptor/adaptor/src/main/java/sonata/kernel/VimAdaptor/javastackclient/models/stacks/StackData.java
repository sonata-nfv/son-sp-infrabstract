package sonata.kernel.VimAdaptor.javastackclient.models.stacks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StackData {

    private Stack stack;

    public Stack getStack() {
        return stack;
    }

    public void setStack (Stack stack) {
        this.stack = stack;
    }


}


package sonata.kernel.placement.net;

import sonata.kernel.placement.config.PopResource;

public class LinkChain {

    public PopResource popResource;
    public String stackName;

    // Names defined by Translator
    public String srcServer;
    public String srcPort;
    public String dstServer;
    public String dstPort;

    // Names used by Emulator
    public String srcNode;
    public String srcInterface;
    public String dstNode;
    public String dstInterface;

    public LinkChain(PopResource popResource, String stackName, String srcServer, String srcPort, String dstServer, String dstPort){
        this.popResource = popResource;
        this.stackName = stackName;
        this.srcServer = srcServer;
        this.srcPort = srcPort;
        this.dstServer = dstServer;
        this.dstPort = dstPort;
        this.srcNode = convertNodeName(this.srcServer);
        this.dstNode = convertNodeName(this.dstServer);
        this.srcInterface = convertInterfaceName(this.srcPort);
        this.dstInterface = convertInterfaceName(this.dstPort);
    }

    /*
     * ! Assume that serverName is unique in the stack!!!
     */
    protected String convertNodeName(String serverName){
        String nodeName;

        nodeName = serverName.split(":")[0];

        nodeName = nodeName.substring(0,12).replace("-","_");

        nodeName = popResource.getPopName() + "_" + this.stackName + "_" + nodeName;

        return nodeName;
    }

    protected String convertInterfaceName(String portName){
        String interfaceName;
        String[] portNameSplitted = portName.split(":");
        interfaceName = portNameSplitted[0] + "-" + portNameSplitted[1] + "-";

        if (portNameSplitted[2].equals("input") || portNameSplitted[2].equals("in"))
            interfaceName += "in";
        else if(portNameSplitted[2].equals("output") || portNameSplitted[2].equals("out"))
            interfaceName += "out";
        else
            interfaceName += portNameSplitted[2];

        return interfaceName;
    }

}

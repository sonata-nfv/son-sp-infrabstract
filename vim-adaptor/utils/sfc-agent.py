import socket
import sys
import os
import time
import json



def findPort(mac):
   mac = mac[9:17]
   #print mac
   helper3 = os.popen("ovs-ofctl dump-ports-desc br-int | grep "+mac).read()
   helping = ""
   for i in range(1,len(helper3)):
      #print helper3[i]
      if (helper3[i]=="("):
          break
      helping = helping+helper3[i]
   #print helping
   return helping
   #return "ok"


brexport = "2"
brintport = "2"
breth0port = "3"

print ""
print ""
print "===SONATA PROJECT==="
print "SFC-AGENT Initializing..."
print ""
print ""

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind the socket to the port
server_address = ('10.100.32.200', 55555)
print >>sys.stderr, 'starting up on %s port %s' % server_address
sock.bind(server_address)

while True:
    data, address = sock.recvfrom(4096)
    #print >>sys.stderr, data
    print "received from: "
    print address
    print ""
    print ""


    jsonResponse=json.loads(data)
    returnflag = "SUCCESS"
    jsonMANA = jsonResponse["action"] # Check json request type 
    if (jsonMANA=="add"):
        jsonData0 = jsonResponse["instance_id"]
        jsonData = jsonResponse["in_segment"]
        jsonData2 = jsonResponse["out_segment"]
        print "SOURCE SEGMENT -> "+jsonData
        print "DESTINATION SEGMENT -> "+jsonData2
        uuid = jsonData0
        fo = open(uuid, "w")
        src = jsonData
        dst = jsonData2
        #for the rules to be installed: print them, install and log them, ready to be deleted.
        print "PoP first-in rule:"
        print "ovs-ofctl add-flow br-eth0 priority=66,dl_type=0x0800,in_port=1,nw_src="+src+",nw_dst="+dst+",actions=output:"+breth0port
        os.system("ovs-ofctl add-flow br-eth0 priority=66,dl_type=0x0800,in_port=1,nw_src="+src+",nw_dst="+dst+",actions=output:"+breth0port)
        fo.write("ovs-ofctl add-flow br-eth0 priority=66,dl_type=0x0800,in_port=1,nw_src="+src+",nw_dst="+dst+"\n")

        print "PoP in rule:"
        print "ovs-ofctl add-flow br-ex priority=66,dl_type=0x800,in_port=1,nw_src="+src+",nw_dst="+dst+",actions=output:"+brexport
        os.system("ovs-ofctl add-flow br-ex priority=66,dl_type=0x800,in_port=1,nw_src="+src+",nw_dst="+dst+",actions=output:"+brexport)
        fo.write("ovs-ofctl --strict del-flows br-ex priority=66,dl_type=0x800,in_port=1,nw_src="+src+",nw_dst="+dst+"\n")

        print "PoP out rule:"
        print "ovs-ofctl add-flow br-ex priority=66,dl_type=0x800,in_port="+brexport+",nw_src="+src+",nw_dst="+dst+",actions=output:1"
        os.system("ovs-ofctl add-flow br-ex priority=66,dl_type=0x800,in_port="+brexport+",nw_src="+src+",nw_dst="+dst+",actions=output:1")
        fo.write("ovs-ofctl --strict del-flows br-ex priority=66,dl_type=0x800,in_port="+brexport+",nw_src="+src+",nw_dst="+dst+"\n")

        jsonData3 = jsonResponse["port_list"] #get the port list
        portlist = []
        for item in jsonData3:
            port = item.get("port")
            order = item.get("order")
            portlist.append(port)
        # Install the redirection rules
        print "Rule First: "
        firstport = findPort(portlist[0])
        if (firstport==""):
            returnflag = "ERROR"
        print "ovs-ofctl add-flow br-int priority=66,dl_type=0x800,in_port="+brintport+",nw_src="+src+",nw_dst="+dst+",actions=output:"+firstport
        os.system("ovs-ofctl add-flow br-int priority=66,dl_type=0x800,in_port="+brintport+",nw_src="+src+",nw_dst="+dst+",actions=output:"+firstport)
        fo.write("ovs-ofctl --strict del-flows br-int priority=66,dl_type=0x800,in_port="+brintport+",nw_src="+src+",nw_dst="+dst+"\n")

        for i in range (2,len(portlist)-1,2):
            #print "in-> "+portlist[i-1]+" - out-> "+portlist[i]
            inport = findPort(portlist[i-1])
            if (inport==""):
                returnflag = "ERROR"
            outport = findPort(portlist[i])
            if (outport==""):
                returnflag = "ERROR"
            print "ovs-ofctl add-flow br-int priority=66,dl_type=0x800,in_port="+inport+",nw_src="+src+",nw_dst="+dst+",actions=output:"+outport
            os.system("ovs-ofctl add-flow br-int priority=66,dl_type=0x800,in_port="+inport+",nw_src="+src+",nw_dst="+dst+",actions=output:"+outport)
            fo.write("ovs-ofctl --strict del-flows br-int priority=66,dl_type=0x800,in_port="+inport+",nw_src="+src+",nw_dst="+dst+"\n")


        print "Last Rule: "
        lastport = findPort(portlist[len(portlist)-1])
        if (lastport==""):
            returnflag = "ERROR"
        print "ovs-ofctl add-flow br-int priority=66,dl_type=0x800,in_port="+lastport+",nw_src="+src+",nw_dst="+dst+",actions=output:"+brintport
        os.system("ovs-ofctl add-flow br-int priority=66,dl_type=0x800,in_port="+lastport+",nw_src="+src+",nw_dst="+dst+",actions=output:"+brintport)
        fo.write("ovs-ofctl --strict del-flows br-int priority=66,dl_type=0x800,in_port="+lastport+",nw_src="+src+",nw_dst="+dst+"\n")

        # Reply Success or Error 
        print returnflag
        sock.sendto(returnflag, address)
        fo.close()
        #if request is to delete, then:
    elif (jsonMANA=="delete"):
        jsonData0 = jsonResponse["instance_id"]
        print "DELETING-> "+jsonData0
        #os.system("rm "+jsonData0)
        f = open(jsonData0, 'r')
        for line in f:
            print line
            os.system(line)
        sock.sendto("SUCCESS", address)


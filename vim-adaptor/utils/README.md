# OVS-SFC 
This software implements an experimental and test version of a Networking VIM, able to receive commands from the IA to enforce SFC on a Virtual Network Service deployment.


### Building


### Dependencies


### Contributing

You can contribute to this repository extending the set VIM supported by the adaptor.
The VIM Adaptor architecture is based on VIM wrappers that implement technology dependant processes for deploying and managing VNFs. 
You can extend the set of available VIM wrappers creating a subpackage of sonata.kernel.VimAdaptor.wrapper and extending the interfaces therein. 

## Usage

The agent can be run simply running the command:

   python sfc-agent.py ...

The SFC agent exposes two API call through a UDP socket listening on default port 55555
It expects payloads formatted as JSON string, encoded in UTF-8, based on the following JSON schema:

    {
      action : String,
      in_segment : String,
      out_segment : String,
      instance_id : String, 
      port_list : [
        {
          port : String,
          order : Integer
        }
      ]
    }

* action: this field can be "add" or "delete" and is used to indicate wether the following info are used to create a chain or to delete a chain.
* in_segment: CIDR(x.x.x.x/n) of the source address of the flow to be steered through the chain
* out_segment: CIDR(x.x.x.x/n) of the destination address of the flow to be steered through the chain
* instance_id: an identifier for the chain
* port: a String with the MAC address of a virtual interface
* order: an integer indicating the order of the port in the chain (starts with 0)

### Example

        ____    ____    ____
       |    |  |    |  |    |
       | F1 |  | F2 |  | F3 |
       |____|  |____|  |____|
        M1 M2   M3 M4   M5 M6
    ____|  |____|  |____|  |_____

In order to set up a chain for the function chain shown above, the payload will look like:

    {
      "action":"add", 
      "in_segment":"192.168.0.0/24", 
      "out_segment":"172.16.0.0/24", 
      "instance_id":"0000-00000000-00000000-0000", 
      "port_list":[
        {"port":"M1","order":0},
        {"port":"M2","order":1},
        {"port":"M3","order":2},
        {"port":"M4","order":3},
        {"port":"M5","order":4},
        {"port":"M6","order":5}
      ]
    }

In order to remove it:

    {
      "action":"delete",
      "instance_id":"0000-00000000-00000000-0000"
    }

## License

This Software is published under Apache 2.0 license. Please see the LICENSE file for more details.

## Useful Links

* https://www.openstack.org/ the OpenStack project homepage
* https://pypi.python.org/pypi/pip Python Package Index
* https://maven.apache.org/ Java Maven 
* https://www.docker.com/ The Docker project
* https://docs.docker.com/compose/ Docker-compose documentation

---
#### Lead Developers

The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.

* [Dario Valocchi](https://github.com/DarioValocchi) 

#### Feedback-Channel


* You may use the mailing list [sonata-dev@lists.atosresearch.eu](mailto:sonata-dev@lists.atosresearch.eu)
* [GitHub issues](https://github.com/sonata-nfv/son-mano-framework/issues)



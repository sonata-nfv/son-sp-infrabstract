# son-infrastructure-abstraction
[![Build Status](http://jenkins.sonata-nfv.eu/buildStatus/icon?job=son-sp-infrabstract)](http://jenkins.sonata-nfv.eu/job/son-sp-infrabstract)
SONATA's Service Platform Infrastructure Abstraction


## Folder structure
  
 * `VIM adaptor` contains the sonata Virtual Infrastructure Manager adaptor files.
 * `wIM adaptor` contains the sonata WAN Infrastructure Manager adaptor files.


### Requirements
 * A running SP Broker and MANO-pluginmanager
 * A postgresql container to store VIM and WIM related info

## Docker support
### Build Docker containers for each component

 * run mano framework
   * checkout son-mano-framework
   * `git clone https://github.com/sonata-nfv/son-mano-framework.git`
   * run the broker:
     * (do in son-mano-framework/)
     * `docker build -t broker -f son-mano-broker/Dockerfile .`
     * `docker run -d -p 5672:5672 --name broker broker`
   * run the pluginmanager:
     * (do in son-mano-framework/)
     * `docker build -t pluginmanager -f son-mano-pluginmanager/Dockerfile .`
     * `docker run -it --link broker:broker --name pluginmanager pluginmanager`
 * run it
    * (do in son-sp-infrabstract/)
    * `docker build -t infrastructure_adaptor .`
    * `docker run -it --link broker:broker --name infrastructure_adaptor infrastructure_adaptor`

## Automated Tests

 * Run unit test using docker-compose (do in son-sp-infrabstract/):
 * docker-compose -f docker-compose-test.yml up. It will run mvn tests which;
    * Register the Adaptor to the MANO-core-pluginmanager, sends 4 heartbeat and deregister (mano framework is mocked)
    * Send an `addVim` request coming from a mock-up MsgBus through the Adaptor components. The adaptor parses it and creates a Wrapper for a Mock VIM.
    * add 4 Vims, list them, and remove them
    * Parse SONATA VNFD
    * Parse SONATA NSD
    * Instantiate the SONATA example service on the Mock VIM
    * Use the OpenStack client to create, retrieve status, and delete, a small example stack
    * ...



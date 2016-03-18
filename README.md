# son-infrastructure-adaptor
SONATA's Service Platform Infrastructure Adaptor


## Folder structure

 * `adaptor` contains the src and test code of the adaptor.



## TODOs:
 * Design API and Develop API endpoint
 * Develop an OpenStack+Heat Wrapper
 * Write more tests 
    * Test


### Requirements
 * A running SP Broker and MANO-pluginmanager
 * Java maven: `sudo apt-get install maven`

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

 * Run unit test using maven (do in son-sp-infrabstract/adaptor/):
 * `mvn test`
    * Register the Adaptor to the MANO-core-pluginmanager, sends 4 heartbeat and deregister (mano framework is mocked)
    * Sends an `addVim` request coming from a mock-up of the MsgBus through the Adaptor components. The adaptor parse it and create an empty VLSP Wrapper.



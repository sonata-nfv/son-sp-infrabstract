# son-infrastructure-adaptor
SONATA's Service Platform MANO Framework


## Folder structure

* `plugins` contains MANO plugin implementations



## TODOs:
* Design API and Develop API endpoint
* Develop an OpenStack+Heat Wrapper
* Write more tests 
    * Tests


### Requirements
* Running RabbitMQ broker instance on local machine (localhost)
* Python maven: `sudo apt-get install maven`

## Docker support
### Build Docker containers for each component

* `docker build -t infrastructure_adaptor .`


## Automated Tests

* Run unit test using maven:
** `mvn test`
*** The very first test sends a puppet addVim request coming from a mock of the MsgBus through the Adaptor component. The adaptor parse it and create an empty VLSP Wrapper



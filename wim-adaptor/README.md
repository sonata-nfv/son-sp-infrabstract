[![Build Status](http://jenkins.sonata-nfv.eu/buildStatus/icon?job=son-sp-infrabstract)](http://jenkins.sonata-nfv.eu/job/son-sp-infrabstract)

# WIM Adaptor
This repository contains the VIM-Adaptor module of the SONATA Infrastructure Abstraction.


## Development
(if applicable)

### Building
* You can run './docker build -t wim-adaptor .' in this folder to build the selfcontained docker image of the VIM-adaptor 

If you prefer to manually build the source code, please consider the following:

* This software is mainly organised as a maven project, so you can run 'mvn build assembly:single' in ./adaptor.
* The VTN wrapper makes use of python clients, you can see ./Dockerfile or Dependencies section of this README for the needed dependencies.

### Dependencies
Name all the dependencies needed by the software, including version, license (!), and a link. For example
* [amqp-client](https://www.rabbitmq.com/java-client.html) >=3.6.1, "Apache 2.0"
* [commons-io](https://commons.apache.org/proper/commons-io/) >= 1.3.2, "Apache 2.0"
* [jackson-annotations](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations) >=  2.7.0, "Apache 2.0"
* [jackson-core](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core) >= 2.7.5	, "Apache 2.0"
* [jackson-databind](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind) >= 2.7.5, "Apache 2.0"
* [jackson-dataformat-yaml](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-yaml) >= 2.7.5, "Apache 2.0"
* [json](http://www.json.org/), 20160212, "The JSON License"
* [junit](https://mvnrepository.com/artifact/junit/junit/3.8.1) = 3.8.1, "Common Public License Version 1.0"
* [postgresql](https://mvnrepository.com/artifact/org.postgresql/postgresql), 9.4.1208.jre7, "The PostgreSQL License"
* [pyaml](https://pypi.python.org/pypi/pyaml) >=15.8.2 (WTFPL)

### Contributing
(if applicable) Description (encouraging) how to contribute to this project/repository.

## Installation
(if applicable) Describe briefly how to install the software. You may want to put a link to son-install instead:

The installation of this component can be done using the [son-install](https://github.com/sonata-nfv/son-install) script.

## Usage
(if applicable) Describe briefly how to use the software.

## License

This [SOFTWARE] is published under Apache 2.0 license. Please see the LICENSE file for more details.

## Useful Links

* https://wiki.opendaylight.org/view/OpenDaylight_Virtual_Tenant_Network_(VTN):Overview The VTN documentation page
* https://pypi.python.org/pypi/pip Python Package Index
* https://maven.apache.org/ Java Maven 
* https://www.docker.com/ The Docker project
* https://docs.docker.com/compose/ Docker-compose documentation

---
#### Lead Developers

The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.

* DarioValocchi

#### Feedback-Channel

* You may use the mailing list sonata-dev@lists.atosresearch.eu
* Please use the GitHub issues to report bugs.



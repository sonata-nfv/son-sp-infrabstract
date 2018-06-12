#!/bin/bash
set -e
set -x
cd vim-adaptor
export DOCKER_HOST="unix:///var/run/docker.sock"

#Clean the workspace
docker-compose -f docker-compose-test.yml down

if ! [[ "$(docker inspect -f {{.State.Running}} son-sp-infrabstract 2> /dev/null)" == "" ]]; then docker rm -fv son-sp-infrabstract ; fi
docker run --name son-sp-infrabstract -d -t registry.sonata-nfv.eu:5000/son-sp-infrabstract
docker cp son-sp-infrabstract:/adaptor/target/adaptor-0.0.1-SNAPSHOT-jar-with-dependencies.jar .
docker rm -fv son-sp-infrabstract
sudo chown jenkins: adaptor-0.0.1-SNAPSHOT-jar-with-dependencies.jar

#Start the container and run tests using the docker-compose-test file
docker-compose -f docker-compose-test.yml up --abort-on-container-exit
docker-compose -f docker-compose-test.yml ps
docker-compose -f docker-compose-test.yml down
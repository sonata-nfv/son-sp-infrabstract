#!/bin/bash
#TESTING PHASE
set -e
set -x
cd wim-adaptor
export DOCKER_HOST="unix:///var/run/docker.sock"
#Start the container and run the tests-Dcheckstyle.config.location=google_checks.xml checkstyle:checkstyle findbugs:findbugs
#docker run -i --rm=true -v "$(pwd)/reports:/adaptor/target" registry.sonata-nfv.eu:5000/son-sp-infrabstract mvn -Dcheckstyle.config.location=google_checks.xml checkstyle:checkstyle cobertura:cobertura findbugs:findbugs; 

#Clean the workspace
docker-compose -f docker-compose-test.yml down

if ! [[ "$(docker inspect -f {{.State.Running}} wim-adaptor 2> /dev/null)" == "" ]]; then docker rm -fv wim-adaptor ; fi
docker run --name wim-adaptor -d -t registry.sonata-nfv.eu:5000/wim-adaptor
docker cp wim-adaptor:/adaptor/target/adaptor-0.0.1-SNAPSHOT-jar-with-dependencies.jar wimAdaptor-0.0.1-SNAPSHOT-jar-with-dependencies.jar
docker rm -fv wim-adaptor
sudo chown jenkins: wimAdaptor-0.0.1-SNAPSHOT-jar-with-dependencies.jar

#Start the container and run tests using the docker-compose-test file
docker-compose -f docker-compose-test.yml up --abort-on-container-exit
docker-compose -f docker-compose-test.yml ps
docker-compose -f docker-compose-test.yml down

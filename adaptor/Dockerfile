FROM maven:3.3.9-jdk-7
MAINTAINER Dario Valocchi <d.valocchi@ucl.ac.uk>


#RUN apt-get update && apt-get install -y maven openjdk-7-jdk

ADD adaptor /adaptor
ADD broker.config /etc/son-mano/broker.config


WORKDIR /adaptor
RUN mvn clean install test assembly:single; 
CMD java -jar target/adaptor-0.0.1-SNAPSHOT-jar-with-dependencies.jar;


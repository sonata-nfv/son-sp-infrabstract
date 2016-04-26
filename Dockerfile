FROM maven:3.3.9-jdk-7
MAINTAINER Dario Valocchi <d.valocchi@ucl.ac.uk>


#RUN apt-get update && apt-get install -y maven openjdk-7-jdk

ADD adaptor /adaptor
ADD broker.config /etc/son-mano/broker.config


WORKDIR /adaptor

RUN apt-get update && apt-get install -y python-pip \
  python-dev \
  build-essential \
  libcurl4-gnutls-dev \
  librtmp-dev \
  python-setuptools \
  python-httplib2

RUN pip install --upgrade pip; \
  pip install --upgrade virtualenv; \
  pip install python-keystoneclient; \
  pip install python-heatclient \
  && rm -rf /var/lib/apt/lists/*

RUN mvn compile assembly:single
CMD java -jar target/adaptor-0.0.1-SNAPSHOT-jar-with-dependencies.jar;


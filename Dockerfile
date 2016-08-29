FROM ubuntu:trusty

# openjdk:7
RUN apt-get update && apt-get install -y --no-install-recommends \
        bzip2 \
        unzip \
        xz-utils \
    && rm -rf /var/lib/apt/lists/*

ENV LANG C.UTF-8

RUN { \
        echo '#!/bin/sh'; \
        echo 'set -e'; \
        echo; \
        echo 'dirname "$(dirname "$(readlink -f "$(which javac || which java)")")"'; \
    } > /usr/local/bin/docker-java-home \
    && chmod +x /usr/local/bin/docker-java-home

ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64

ENV JAVA_VERSION 7u111

RUN set -x \
    && apt-get update \
    && apt-get install -y \
        curl openjdk-7-jdk \
    && rm -rf /var/lib/apt/lists/*

# maven:3-jdk-7

ENV MAVEN_VERSION 3.3.9

RUN mkdir -p /usr/share/maven \
  && curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
    | tar -xzC /usr/share/maven --strip-components=1 \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

#VOLUME /root/.m2

CMD ["mvn"]


# our own part
#FROM maven:3.3.9-jdk-7
MAINTAINER Foo Bar <lol@example.com>

RUN apt-get update && apt-get install -y python-pip \
  python-dev \
  build-essential \
  libcurl4-gnutls-dev \
  librtmp-dev \
  python-setuptools \
  python-httplib2 \
  netcat

RUN pip install --upgrade pip; \
  pip install --upgrade virtualenv; \
  pip install python-keystoneclient; \
  pip install python-novaclient; \
  pip install python-heatclient \
  && rm -rf /var/lib/apt/lists/*

ADD vim-adaptor/adaptor /adaptor

ADD sandman/placement /placement

ADD sandman/sandman-placement* /usr/local/bin/
ADD sandman/sandman-placement /etc/init.d/
ADD sandman/docker-entrypoint.sh /
RUN chmod +x /usr/local/bin/sandman-placement-*
RUN chmod +x /etc/init.d/sandman-placement

# build and install vim-adaptor and build sandman-placement
# in one command because:
#   parent image maven:3.3.9-jdk-7 declares /root/.m2/ as VOLUME
#   changes to /root/.m2/ are reset after each RUN command
#   VOLUME command can not be reset
#   check this Docker issue https://github.com/docker/docker/issues/3465
RUN cd /adaptor; mvn -Dmaven.test.skip=true -q install; cd /placement; mvn -Dmaven.test.skip=true -q compile assembly:single;

CMD /docker-entrypoint.sh




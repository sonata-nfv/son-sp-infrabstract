FROM maven:3.3.9-jdk-7
MAINTAINER Dario Valocchi <d.valocchi@ucl.ac.uk>

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
  pip install python-heatclient \
  && rm -rf /var/lib/apt/lists/*

ADD adaptor /adaptor
ADD broker.config /etc/son-mano/broker.config
ADD postgres.config /etc/son-mano/postgres.config
ADD son-sp-infra-* /usr/local/bin/
ADD son-sp-infra /etc/init.d/
ADD ./setenv.sh /
ADD ./test.sh /
ADD ./docker-entrypoint.sh /
RUN chmod +x /setenv.sh
RUN chmod +x /test.sh
RUN chmod +x /usr/local/bin/son-sp-infra-*
RUN chmod +x /etc/init.d/son-sp-infra

WORKDIR /adaptor

ENV broker_host broker
ENV broker_port 5672
ENV broker_exchange son-kernel
ENV broker_uri amqp://guest:guest@broker:5672/%2F

ENV repo_host postgres
ENV repo_port 5432
ENV repo_user adaptor
ENV repo_database vimregistry
ENV repo_pass repotestadaptor

RUN mvn -q compile assembly:single;

CMD ["/docker-entrypoint.sh","/test.sh"]


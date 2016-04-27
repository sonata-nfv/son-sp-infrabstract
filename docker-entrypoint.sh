#!/bin/bash
echo "Waiting for rabbitmq on port" $broker_port

sed "s#BROKERURL#$broker_uri#" -i /etc/son-mano/broker.config
sed -i "s/BROKEREXCHANGE/$broker_exchange/" /etc/son-mano/broker.config

while ! nc -z $broker_host $broker_port; do
  sleep 1 && echo -n .; # waiting for rabbitmq
done;

service son-sp-infra start

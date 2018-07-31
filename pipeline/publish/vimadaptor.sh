#!/bin/bash
set -e

TAG=$1

docker tag registry.sonata-nfv.eu:5000/son-sp-infrabstract:latest registry.sonata-nfv.eu:5000/son-sp-infrabstract":$TAG"

docker push registry.sonata-nfv.eu:5000/son-sp-infrabstract":$TAG"

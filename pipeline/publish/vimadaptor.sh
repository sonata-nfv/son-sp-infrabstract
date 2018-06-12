#!/bin/bash
set -e

TAG=$1

if [ "$TAG" == "int" ]; then
	docker tag registry.sonata-nfv.eu:5000/son-sp-infrabstract:latest registry.sonata-nfv.eu:5000/son-sp-infrabstract:int
fi

docker push registry.sonata-nfv.eu:5000/son-sp-infrabstract":$TAG"

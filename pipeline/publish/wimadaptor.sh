#!/bin/bash
set -e

TAG=$1

docker tag registry.sonata-nfv.eu:5000/wim-adaptor:latest registry.sonata-nfv.eu:5000/wim-adaptor":$TAG"

docker push registry.sonata-nfv.eu:5000/wim-adaptor":$TAG"

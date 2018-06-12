#!/bin/bash
set -e
cd vim-adaptor/
docker build -t registry.sonata-nfv.eu:5000/son-sp-infrabstract .

#!/bin/bash
set -e
cd wim-adaptor/
docker build -t registry.sonata-nfv.eu:5000/wim-adaptor .

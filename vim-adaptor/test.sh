#!/bin/bash
echo "Setting environment"
/setenv.sh
echo "Doing maven test"
cd /adaptor
mvn -q test

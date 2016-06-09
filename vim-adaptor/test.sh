#!/bin/bash
set -e
echo "Setting environment"
/setenv.sh

echo "Creating user and database"

echo "Doing maven test"
cd /adaptor
mvn -Dcheckstyle.config.location=google_checks.xml checkstyle:checkstyle findbugs:findbugs cobertura:cobertura

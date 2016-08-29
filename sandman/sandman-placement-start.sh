#!/bin/bash
echo "Start placement at ""$(date)"" ..." >> /var/log/placement
java -jar /placement/target/placement-0.0.1-SNAPSHOT-jar-with-dependencies.jar >> /var/log/placement
echo "Stop placement at ""$(date)"" ..." >> /var/log/placement

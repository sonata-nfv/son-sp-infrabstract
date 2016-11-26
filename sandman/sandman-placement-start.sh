#!/usr/bin/env bash
# http://stackoverflow.com/questions/3173131/redirect-copy-of-stdout-to-log-file-from-within-bash-script-itself#
# Redirect stdout ( > ) into a named pipe ( >() ) running "tee"
exec > >(tee -i -a /var/log/placement)

# Without this, only stdout would be captured - i.e. your
# log file would not contain any error messages.
# SEE (and upvote) the answer by Adam Spiers, which keeps STDERR
# as a seperate stream - I did not want to steal from him by simply
# adding his answer to mine.
exec 2>&1

echo "Start placement at ""$(date)"" ..."
java -jar /placement/target/placement-0.0.1-SNAPSHOT-jar-with-dependencies.jar
echo "Stop placement at ""$(date)"" ..."

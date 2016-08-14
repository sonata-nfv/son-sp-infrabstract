#!/bin/bash

pid=`ps aux | grep placement | awk '{print $2}'`
kill -9 $pid


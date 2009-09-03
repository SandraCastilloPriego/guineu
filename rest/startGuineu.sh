#!/bin/sh

SCRIPTDIR=`dirname "$0"`
cd "$SCRIPTDIR"

java -Djava.util.logging.config.file=conf/logging.properties -Xms512m -Xmx2048m -XX:ThreadStackSize=1024 -cp Guineu.jar guineu.main.GuineuClient

#!/bin/sh

SCRIPTDIR=`dirname "$0"`
cd "$SCRIPTDIR"

java -Djava.util.logging.config.file=conf/logging.properties -Xdock:name="Guineu" -Dapple.laf.useScreenMenuBar=true -Xms512m -Xmx2048m -cp Guineu.jar guineu.main.GuineuClient
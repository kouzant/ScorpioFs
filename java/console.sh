#!/bin/sh


. ./config/build.conf

LD_LIBRARY_PATH=./jni:$FUSE_HOME/lib $JDK_HOME/bin/java \
   -classpath .:./config/:./dist/scorpioFS.jar \
   -Dorg.apache.commons.logging.Log=fuse.logging.FuseLog \
   -Djava.net.preferIPv4Stack=true \
    unipi.p2p.chord.util.console.ConsoleClient $*

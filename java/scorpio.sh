#!/bin/sh


. ./config/build.conf

LD_LIBRARY_PATH=./dist/jni:$FUSE_HOME/lib $JDK_HOME/bin/java \
   -classpath .:./config/:./dist/scorpioFS.jar \
   -Dorg.apache.commons.logging.Log=fuse.logging.FuseLog \
   -Djava.net.preferIPv4Stack=true \
   -Dfuse.logging.level=DEBUG \
   fuse.scorpiofs.ScorpioFS $*

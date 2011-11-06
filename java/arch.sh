#!/usr/bin/env bash

machine_arch=`uname -m`

files[0]="/home/antonis/ScorpioFs/java/config/jvm_ldpath.def"
files[1]="/home/antonis/ScorpioFs/java/src/jvm_ldpath.def"

function baha {
    COUNTER=0
    while [ $COUNTER -lt 2 ];
    do
        echo ${files[$COUNTER]}
        if [ "$1" == "i386" ]; then
            diff0=`grep i386 ${files[$COUNTER]}`
            if [ "$diff0" == "" ]; then
                sed "s/amd64/i386/g" ${files[$COUNTER]} > /tmp/scorpiofs.conf.file
                mv /tmp/scorpiofs.conf.file ${files[$COUNTER]}
            fi
        else
            diff1=`grep amd64 ${files[$COUNTER]}`
            if [ "$diff1" == "" ]; then
                sed 's/i386/amd64/g' ${files[$COUNTER]} > /tmp/scorpiofs.conf.file
                mv /tmp/scorpiofs.conf.file ${files[$COUNTER]}
            fi
        fi
        let COUNTER=COUNTER+1
    done
    exit 0
}

if [ "$machine_arch" == "i686" ]; then
    baha i386
    
else
    baha amd64
fi

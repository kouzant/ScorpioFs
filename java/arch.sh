#!/usr/bin/env bash

GREP=/bin/grep
SED=/bin/sed
MV=/bin/mv
UNAME=/bin/uname
AWK=/usr/bin/awk
ECHO=/bin/echo
CAT=/bin/cat

machine_arch=`$UNAME -m`
file_home=`$CAT config/chord.properties | $GREP -m 1 home | $AWK -F/ '{print $3}'`
user_home=`$ECHO $HOME | $AWK -F/ '{print $3}'`

jvmld_files[0]="/home/antonis/ScorpioFs/java/config/jvm_ldpath.def"
jvmld_files[1]="/home/antonis/ScorpioFs/java/src/jvm_ldpath.def"

conf_files[0]="config/chord.properties"
conf_files[1]="config/scorpiofs.properties"
conf_files[2]="config/chord.propertiesClient1"
conf_files[3]="config/chord.propertiesClient2"

# Change between i386 and amd64 architecture
function sed_arch {
    COUNTER=0
    while [ $COUNTER -lt 2 ];
    do
        if [ "$1" == "i386" ]; then
            diff0=`$GREP i386 ${jvmld_files[$COUNTER]}`
            if [ "$diff0" == "" ]; then
                $SED "s/amd64/i386/g" ${jvmld_files[$COUNTER]} > /tmp/scorpiofs.conf.file
                $MV /tmp/scorpiofs.conf.file ${jvmld_files[$COUNTER]}
            fi
        else
            diff1=`$GREP amd64 ${jvmld_files[$COUNTER]}`
            if [ "$diff1" == "" ]; then
                $SED 's/i386/amd64/g' ${jvmld_files[$COUNTER]} > /tmp/scorpiofs.conf.file
                $MV /tmp/scorpiofs.conf.file ${jvmld_files[$COUNTER]}
            fi
        fi
        let COUNTER=COUNTER+1
    done
    exit 0
}

# Change home directories
function sed_conf {
    COUNTER=0
    while [ $COUNTER -lt 4 ];
    do
        $SED "s/$file_home/$user_home/g" ${conf_files[$COUNTER]} > /tmp/scofile
        $MV /tmp/scofile ${conf_files[$COUNTER]}
        let COUNTER=COUNTER+1
    done
    exit 0
}

sed_conf

if [ "$machine_arch" == "i686" ]; then
    sed_arch i386
    
else
    sed_arch amd64
fi

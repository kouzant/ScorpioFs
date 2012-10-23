#!/bin/bash

IP_ADDR=`/sbin/ifconfig eth0 | grep 'inet addr' | awk '{print \$2}' | awk -F':' '{print \$2}'`

echo $IP_ADDR

CONF[0]="chord.properties0"
CONF[1]="chord.properties1"

OLD_ONE="localhost"

for i in {0..1}
do
    sed "s/$OLD_ONE/$IP_ADDR/g" ${CONF[$i]} > "${CONF[$i]}.tmp"
    mv "${CONF[$i]}.tmp" ${CONF[$i]}
done

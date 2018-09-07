#!/bin/sh

java -Dlog4j.configuration=file:conf/log4j.xml -Djava.security.egd=file:/dev/./urandom -DappDynamics.controller.multiTenant=false -jar snmp-trap-alert.jar "$@" &

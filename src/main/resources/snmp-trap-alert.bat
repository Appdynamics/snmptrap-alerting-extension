@echo off
java -Dlog4j.configuration=file:conf/log4j.xml -jar snmp-trap-alert.jar %*
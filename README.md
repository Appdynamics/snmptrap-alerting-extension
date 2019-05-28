# AppDynamics SNMP Trap Alerting Integration

##Use Case

Simple Network Management Protocol (SNMP) is a protocol for managing IP network devices such as routers, switches, 
servers, workstations, etc. A SNMP trap is an asynchronous notification between an SNMP agent to its SNMP manager.

With the SNMP Trap integration you can leverage your existing alerting infrastructure to notify your operations team
to resolve performance degradation issues. This tool sends all the events in AppDynamics as SNMP trap alerts to its receivers.



##Installation

1. Download the snmp-trap-alert-<version>.zip from [AppDynamics Exchange](http://community.appdynamics.com/t5/AppDynamics-eXchange/idb-p/extensions)

2. Unzip the snmp-trap-alert-<version>.zip file into <CONTROLLER_HOME_DIR>/custom/actions/ . You should have <CONTROLLER_HOME_DIR>/custom/actions/snmp-trap-alert created.

3. Check if you have custom.xml file in <CONTROLLER_HOME_DIR>/custom/actions/ directory. If yes, add the following xml to the <custom-actions> element.
    
			```
				<action>
					 <type>snmp-trap-alert</type>
					 <!-- For Linux/Unix *.sh -->
					 <executable>snmp-trap-alert.sh</executable>
					 <!-- For windows *.bat -->
					 <!--<executable>snmp-trap-alert.bat</executable>-->
				</action>

			```

   If you don't have custom.xml already, create one with the below xml content.

			 ```
					<custom-actions>
					<action>
						 <type>snmp-trap-alert</type>
						 <!-- For Linux/Unix *.sh -->
						 <executable>snmp-trap-alert.sh</executable>
						 <!-- For windows *.bat -->
						 <!--<executable>snmp-trap-alert.bat</executable>-->
					</action>
					</custom-actions>
			 ```
   Uncomment the appropriate executable tag based on windows or linux/unix machine.

4. SNMP extension has support for all three versions of SNMP i.e. v1,v2,v3. Please find the MIB files associated to each version in the
    zip file to interpret the traps at the trap receiver.

5. Below is the config.yaml which needs to be configured.

			 ```
					######SNMP Trap information start##############

					receivers:
						 #host name or ip address of the snmp trap receiver
					   - host: ""
						 #listener port of the snmp trap receiver
						 port:

					#community level
					community: ""

					#descriptive name of the snmp trap sender | host name or ip address are commonly used
					senderHost: ""


					#Indicates the version of the MIB file used from the files included in the distribution
					#there are 3 MIB file versions, each having their own level of OID segmentation
					#APPD-CTLR-MIB-v1.mib | 1 OID emitted
					#1.3.6.1.4.1.40684.1.1.1.500.1 | all notifications
					###################################################################################### -->
					#APPD-CTLR-MIB-v2.mib | 3 OID's emitted
					#1.3.6.1.4.1.40684.1.1.1.500.1 | Policy Open Notification
					#1.3.6.1.4.1.40684.1.1.1.500.2 | Policy Closed Notification
					#1.3.6.1.4.1.40684.1.1.1.500.3 | Informational Event Notification
					###################################################################################### -->
					#APPD-CTLR-MIB-v3.mib | 6 OID's emitted
					#1.3.6.1.4.1.40684.1.1.1.500.1| Policy Open Warning Notification
					#1.3.6.1.4.1.40684.1.1.1.500.2| Policy Open Critical Notification
					#1.3.6.1.4.1.40684.1.1.1.500.3| Policy Open Upgraded Notification
					#1.3.6.1.4.1.40684.1.1.1.500.4| Policy Open Downgraded Notification
					#1.3.6.1.4.1.40684.1.1.1.500.5| Policy Closed Warning Notification
					#1.3.6.1.4.1.40684.1.1.1.500.6| Policy Closed Critical Notification
					#1.3.6.1.4.1.40684.1.1.1.500.7| Informational Event Notification
					#1.3.6.1.4.1.40684.1.1.1.500.8| Policy Cancelled Warning
					#1.3.6.1.4.1.40684.1.1.1.500.9| Policy Cancelled Critical
					#1.3.6.1.4.1.40684.1.1.1.500.10| Policy Continues Warning
					#1.3.6.1.4.1.40684.1.1.1.500.11 | Policy Continues Critical
					###################################################################################### -->

					#mib version used to send the trap to the receiver | 1:2:3
					mibVersion:

					#snmp version used to send the trap to the receiver | 1:2:3
					snmpVersion:

					#Only required if snmp-version set to 3
					snmpV3Configuration:
					 securityLevel:
					 username: ""
					 password: ""
					 authProtocol: ""
					 privProtocol: ""
					 privProtocolPassword: ""

					######SNMP Trap information end##############



					######AppD information start##################
                    controller:
                      host: ""
                      port:
                      useSsl: true
                      userAccount: ""
                      password: ""
                      #encryptedPassword: ""
                      connectTimeoutInSeconds: 10
                      socketTimeoutInSeconds: 10

                    
                    eventTypes: ["APPLICATION_ERROR","DEADLOCK","APP_SERVER_RESTART","APPLICATION_CONFIG_CHANGE","DIAGNOSTIC_SESSION","STALL","APPLICATION_CRASH","APPLICATION_DEPLOYMENT","NODE_DISCOVERED","SLOW","TIER_DISCOVERED","VERY_SLOW"]
                    severities: ["INFO","WARN","ERROR"]
                    durationInMins: 10

                    ######AppD information end#################
	         ```

  Some things to note about the config.yaml file

  1. Please make sure to not use tab (\t) while editing yaml files. You may want to validate the yaml file using a yaml validator (www.yamllint.com).

  2. The extension has support to configure multiple trap receivers. However, it uses the same SNMP settings for all receivers.

			 ```
				receivers:
							 #host name or ip address of the snmp trap receiver
						   - host: "cisco.receiver.com"
							 #listener port of the snmp trap receiver
							 port: 162
						   - host: "ibm-netcool.receiver.com"
							 port: 163

			 ```


   3. Please configure the snmpV3Configuration field only for SNMP v3 version otherwise please remove it.


6. Now you are ready to use this extension as a custom action. In the AppDynamics UI, go to 'Alert & Respond' -> 'Actions'.
   Click on the 'Create Action' button. Select 'Custom Action' and click OK. In the drop-down menu you can find the action called 'snmp-trap-alert'.


##Encryption Support

This SNMP extension supports encrypted password. Below are the steps to generate encrypted passwords

1. To generate the encrypted password you will have to provide an encryption key. The same encryption key will be used
   to encrypt all the passwords in the config.yaml. You can use the below command to generate encrypted password for every password
   field mentioned in the config.yaml

   java -cp snmp-trap-alert.jar com.appdynamics.extensions.crypto.Encryptor <myEncryptionKey> <myPassword>

		where <myEncryptionKey> is the encryption key used by the customer.
               <myPassword> is the password that needs to be encrpyted.

2. After generating the encrypted fields, please specify these fields in the config.yaml along with the encryption key used.
   For eg. to configure the snmpV3Configuration with encryption support, please add the encryption key and the encryptedPassword,
   encryptedPrivProtocolPassword fields in the yaml file. Please note that the original clear text password and privProtocolPassword fields
   are removed.

   ```
   	   encrpytionKey: ""

	   #Only required if snmp-version set to 3
	   snmpV3Configuration:
		securityLevel:
		username: ""
		encryptedPassword: ""
		authProtocol: ""
		privProtocol: ""
		encryptedPrivProtocolPassword: ""

   ```

	

##Debugging

To debug the code:

1.  In <controller_dir>/custom/actions/snmp-trap-alert/conf/log4j.xml file change the logging level to debug.

	<logger name="com.appdynamics.extensions" additivity="false">
    	<level value="debug"/>
    	<appender-ref ref="RollingFileAppender"/>
    	<appender-ref ref="ConsoleAppender"/>
      </logger>


2.  Open <controller_dir>/custom/actions/snmp-trap-alert/logs/snmp-trap-alert.log

##Testing

If you'd like to send a test trap for debugging purpose please execute the test-health-rule-violation.sh script.
This script will send a simple Trap using the configured config.yaml file. You can use this to verify that the transmission works.



##Contributing

Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/snmptrap-alerting-extension).

##Community

Find out more in the [AppDynamics Exchange](http://appsphere.appdynamics.com/t5/Extensions/SNMP-Trap-Alerting-Extension/idi-p/825) community.


## Support ##

For any questions or feature request, please contact [AppDynamics Center of Excellence][].

**Version:** 6.0.0
**Controller Compatibility:** 4.3+
**SNMP version tested on:** v1,v2,v3.

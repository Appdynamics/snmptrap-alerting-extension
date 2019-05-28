/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.snmp;


import com.appdynamics.extensions.snmp.config.*;
import com.appdynamics.extensions.snmp.model.ADSnmpData;
import org.apache.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.appdynamics.extensions.snmp.CommonUtils.getSysUptime;

public class SNMPSender {

    private static Logger logger = Logger.getLogger(SNMPSender.class);

    public static final int SNMP_V1 = 1;
    public static final int SNMP_V2 = 2;
    public static final int SNMP_V3 = 3;

    public static String NO_AUTH_NO_PRIV = "1";
    public static String AUTH_NO_PRIV = "2";
    public static String AUTH_PRIV = "3";



    public void sendTrap(Configuration config, ADSnmpData snmpData, String trapOid) {

        ArrayList<String> exceptions = new ArrayList<String>();
        //sending SNMP traps to all registered receivers
        for(Receiver receiver : config.getReceivers()){
            logger.info("SNMP version " + config.getSnmpVersion());
            logger.debug("Sending trap to " + receiver.getHost() + ":" + receiver.getPort());
            try {
                if (config.getSnmpVersion() == SNMP_V1) {
                    sendV1Trap(receiver.getHost(), Integer.toString(receiver.getPort()), config.getCommunity(), config.getSenderHost(), snmpData,trapOid);
                } else if (config.getSnmpVersion() == SNMP_V2) {
                    sendV2Trap(receiver.getHost(), Integer.toString(receiver.getPort()), config.getCommunity(), config.getSenderHost(), snmpData,trapOid);
                } else if (config.getSnmpVersion() == SNMP_V3) {
                    String propertiesFile = ConfigLoader.getEngineConfig();
                    logger.info("Loading engine properties from " + propertiesFile);
                    EngineProperties engineProperties = new EngineProperties(propertiesFile);
                    //logger.debug("Loaded Engine Properties => " + engineProperties);
                    sendV3Trap(receiver.getHost(), Integer.toString(receiver.getPort()), config.getSenderHost(), snmpData, config.getSnmpV3Configuration(),trapOid,engineProperties);
                    //logger.debug("New Engine Properties => " + engineProperties);
                }
            } catch (IOException e) {
                logger.error("Something unforeseen has happened.",e);
                exceptions.add("Exception while sending trap to " + receiver.getHost() + ":" + receiver.getPort()+ e);
            } catch (IllegalAccessException e) {
                logger.error("Illegal access",e);
                exceptions.add("Exception while sending trap to " + receiver.getHost() + ":" + receiver.getPort()+ e);
            }
        }
        if(exceptions.size() > 0){
            throw new SNMPTrapException(exceptions.toString());
        }
    }



    /**
     * Sends v1 Traps
     * @param 	host 						Host to send trap to
     * @param 	port						Port location to send trap to
     * @param 	community					Community (Default: PUBLIC)
     * @param 	trapHost					Host of the source sending the trap
     * @param 	snmpData					Trap Data
     * @throws java.io.IOException                    Failed to send trap exception
     * @throws 	IllegalArgumentException 	Failed to access snmp trap variables
     * @throws 	IllegalAccessException 		Failed to access snmp trap variables
     */
    @SuppressWarnings("rawtypes")
    private void sendV1Trap(String host, String port, String community, String trapHost, ADSnmpData snmpData,String trapOid)
            throws IOException, IllegalArgumentException, IllegalAccessException
    {
        Lookup lookUp = new Lookup();

        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        CommunityTarget comTarget = new CommunityTarget();
        comTarget.setCommunity(new OctetString(community));
        comTarget.setVersion(SnmpConstants.version1);
        comTarget.setAddress(new UdpAddress(host + "/" + port));
        comTarget.setRetries(2);
        comTarget.setTimeout(5000);

        TimeTicks sysUpTime = getTimeTicks();

        PDUv1 pdu = new PDUv1();
        pdu.setType(PDU.V1TRAP);
        pdu.setEnterprise(new OID(trapOid));
        pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
        pdu.setSpecificTrap(1);
        pdu.setAgentAddress(new IpAddress(trapHost));

        pdu.add(new VariableBinding(SnmpConstants.sysUpTime,  sysUpTime));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(trapHost)));

        if (snmpData.getMachines() == null || "".equals(snmpData.getMachines()) || " ".equals(snmpData.getMachines())) {
            snmpData.setMachines(" ");
        }
        if (snmpData.getTiers() == null || "".equals(snmpData.getTiers()) || " ".equals(snmpData.getTiers())) {
            snmpData.setTiers(" ");
        }
        if (snmpData.getIpAddresses() == null || "".equals(snmpData.getIpAddresses()) || " ".equals(snmpData.getIpAddresses())) {
            snmpData.setIpAddresses(" ");
        }

        for (Field field : snmpData.getClass().getDeclaredFields())
        {
            if(field.get(snmpData) != null) {
                try {
                    Object snmpVal = new OctetString(field.get(snmpData).toString());

                    if (!(snmpVal.equals(" ") || snmpVal.equals(""))) {
                        pdu.add(new VariableBinding(new OID(lookUp.getOID(field.getName())), new OctetString(snmpVal.toString())));
                    }
                } catch (Throwable ex) {
                    logger.error("Error reading snmp data field:", ex);
                }
            }
        }

        Snmp snmp = new Snmp(transport);
        snmp.send(pdu, comTarget);
        snmp.close();
    }

    /**
     * Sends v2 Traps
     * @param 	host 						Host to send trap to
     * @param 	port						Port location to send trap to
     * @param 	community					Community (Default: PUBLIC)
     * @param 	trapHost					Host of the source sending the trap
     * @param 	snmpData					Trap Data
     * @throws 	java.io.IOException					Failed to send trap exception
     * @throws 	IllegalArgumentException 	Failed to access snmp trap variables
     * @throws 	IllegalAccessException 		Failed to access snmp trap variables
     */
    @SuppressWarnings("rawtypes")
    private void sendV2Trap(String host, String port, String community, String trapHost, ADSnmpData snmpData,String trapOid)
            throws IOException, IllegalArgumentException, IllegalAccessException
    {
        Lookup lookUp = new Lookup();

        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        CommunityTarget comTarget = new CommunityTarget();
        comTarget.setCommunity(new OctetString(community));
        comTarget.setVersion(SnmpConstants.version2c);
        comTarget.setAddress(new UdpAddress(host + '/' + port));
        comTarget.setRetries(2);
        comTarget.setTimeout(5000);

        TimeTicks sysUpTime = getTimeTicks();

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(SnmpConstants.sysUpTime,  sysUpTime));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(trapHost)));

        for (Field field : snmpData.getClass().getDeclaredFields())
        {
            if(field.get(snmpData) != null) {

                try {
                    Object snmpVal = new OctetString(field.get(snmpData).toString());

                    if (!(snmpVal.equals(" ") || snmpVal.equals(""))) {
                        pdu.add(new VariableBinding(new OID(lookUp.getOID(field.getName())), new OctetString(snmpVal.toString())));
                    }
                } catch (Throwable ex) {
                    logger.error("Error reading snmp data field:", ex);
                }
            }
        }

        pdu.setType(PDU.NOTIFICATION);

        Snmp snmp = new Snmp(transport);
        snmp.send(pdu, comTarget);
        snmp.close();
    }

    private TimeTicks getTimeTicks() {
        long upTimeInMs = getSysUptime();
        return CommonUtils.getTimeTicks(upTimeInMs);
    }


    /**
     * Sends v3 Traps
     * @param 	host 					Host to send trap to
     * @param 	port					Port location to send trap to
     * @param 	trapHost				Host of the source sending the trap
     * @param 	snmpData				Trap Data
     * @param 	config				V3 settings
     * @throws 	java.io.IOException					Failed to send trap exception
     * @throws 	IllegalArgumentException 	Failed to access snmp trap variables
     * @throws 	IllegalAccessException 		Failed to access snmp trap variables
     */
    @SuppressWarnings("rawtypes")
    private void sendV3Trap(String host, String port, String trapHost, ADSnmpData snmpData, SnmpV3Configuration config,String trapOid,EngineProperties engineProperties)
            throws IOException, IllegalArgumentException, IllegalAccessException
    {
        Lookup lookUp = new Lookup();

        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        byte[] defaultEngineId = MPv3.createLocalEngineID();

        OctetString os = new OctetString(defaultEngineId);
        logger.info("Local engine id =>" + os.toString());

        //USM usm = new USM(SecurityProtocols.getInstance(),os,engineProperties.getEngineBoots());
        USM usm = new USM(SecurityProtocols.getInstance(),os,engineProperties.getEngineBoots(),engineProperties.getEngineTime());
        UsmTimeEntry ute = new UsmTimeEntry(os,engineProperties.getEngineBoots(),engineProperties.getEngineTime());
        usm.getTimeTable().setLocalTime(ute);
        SecurityModels.getInstance().addSecurityModel(usm);

        Snmp snmp = new Snmp(transport);
        snmp.setLocalEngine(defaultEngineId,engineProperties.getEngineBoots(),engineProperties.getEngineTime());


        String securityLevel = Integer.toString(config.getSecurityLevel());
        logger.info("SNMP Engine time =>" + snmp.getUSM().getConfigurableEngineTime() + " and engineProperties time=> " + engineProperties.getEngineTime());

        if(securityLevel.equals(NO_AUTH_NO_PRIV))
        {
            snmp.getUSM().addUser
                    (
                            new OctetString(config.getUsername()),
                            new UsmUser
                                    (
                                            new OctetString(config.getUsername()),
                                            null,
                                            null,
                                            null,
                                            null
                                    )
                    );
        }
        else if(securityLevel.equals(AUTH_NO_PRIV))
        {
            snmp.getUSM().addUser
                    (
                            new OctetString(config.getUsername()),
                            new UsmUser
                                    (
                                            new OctetString(config.getUsername()),
                                            (config.getAuthProtocol().toUpperCase().contains("SHA")) ? AuthSHA.ID : AuthMD5.ID,
                                            new OctetString(config.getPassword()),
                                            null,
                                            null
                                    )
                    );
        }
        else if(securityLevel.equals(AUTH_PRIV))
        {
            OID privProtocol = PrivAES256.ID;

            String strPrivProtocol = config.getPrivProtocol();

            if (strPrivProtocol.toUpperCase().contains("3DES"))
                privProtocol = Priv3DES.ID;
            else if (strPrivProtocol.toUpperCase().contains("AES128"))
                privProtocol = PrivAES128.ID;
            else if (strPrivProtocol.toUpperCase().contains("AES192"))
                privProtocol = PrivAES192.ID;
            else if (strPrivProtocol.toUpperCase().contains("DES"))
                privProtocol = PrivDES.ID;

            snmp.getUSM().addUser
                    (
                            new OctetString(config.getUsername()),
                            new UsmUser
                                    (
                                            new OctetString(config.getUsername()),
                                            (config.getAuthProtocol().contains("SHA")) ? AuthSHA.ID : AuthMD5.ID,
                                            new OctetString(config.getPassword()),
                                            privProtocol,
                                            new OctetString(config.getPrivProtocolPassword())
                                    )
                    );
        }

        UserTarget usrTarget = new UserTarget();
        usrTarget.setVersion(SnmpConstants.version3);
        usrTarget.setAddress(new UdpAddress(host + '/' + port));
        usrTarget.setRetries(2);
        usrTarget.setSecurityLevel(Integer.valueOf(config.getSecurityLevel()));
        usrTarget.setSecurityName(new OctetString(config.getUsername()));
        usrTarget.setTimeout(5000);

        TimeTicks sysUpTime = getTimeTicks();

        PDU pdu = new ScopedPDU();
        pdu.setType(PDU.NOTIFICATION);
        pdu.add(new VariableBinding(SnmpConstants.sysUpTime,  sysUpTime));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(trapHost)));

        for (Field field : snmpData.getClass().getDeclaredFields())
        {
            if(field.get(snmpData) != null) {
                try {
                    Object snmpVal = new OctetString(field.get(snmpData).toString());

                    if (!(snmpVal.equals(" ") || snmpVal.equals(""))) {
                        pdu.add(new VariableBinding(new OID(lookUp.getOID(field.getName())), new OctetString(snmpVal.toString())));
                    }
                } catch (Throwable ex) {
                    logger.error("Error reading snmp data field:", ex);
                }
            }
        }

        snmp.send(pdu, usrTarget);
        snmp.close();
    }



    private static void addIPV4Address(List<String> ipList, String ip) {
        if (ip != null && (!ipList.contains(ip))) {
            ipList.add(ip);
        }
    }
    private static void addIPV4Addresses(List<String> source, List<String> destination) {
        if (source != null) {
            for (String ip : source) {
                if (!destination.contains(ip)) {
                    destination.add(ip);
                }
            }
        }
    }



}

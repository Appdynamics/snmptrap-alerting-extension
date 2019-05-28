/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.mp;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.VariableBinding;

import java.io.Serializable;

/**
 * The <code>StatusInformation</code> class represents status information
 * of a SNMPv3 message that is needed to return a report message.
 * @author Frank Fock
 * @version 1.0
 */
public class StatusInformation implements Serializable {

  private static final long serialVersionUID = 9053403603288070071L;

  private VariableBinding errorIndication;
  private byte[] contextName;
  private byte[] contextEngineID;
  private Integer32 securityLevel;

  public StatusInformation() {
  }

  public StatusInformation(VariableBinding errorIndication,
                           byte[] contextName,
                           byte[] contextEngineID,
                           Integer32 securityLevel) {
    this.errorIndication = errorIndication;
    this.contextName = contextName;
    this.contextEngineID = contextEngineID;
    this.securityLevel = securityLevel;
  }

  public VariableBinding getErrorIndication() {
    return errorIndication;
  }
  public void setErrorIndication(VariableBinding errorIndication) {
    this.errorIndication = errorIndication;
  }
  public void setContextName(byte[] contextName) {
    this.contextName = contextName;
  }
  public byte[] getContextName() {
    return contextName;
  }
  public void setContextEngineID(byte[] contextEngineID) {
    this.contextEngineID = contextEngineID;
  }
  public byte[] getContextEngineID() {
    return contextEngineID;
  }
  public void setSecurityLevel(Integer32 securityLevel) {
    this.securityLevel = securityLevel;
  }
  public Integer32 getSecurityLevel() {
    return securityLevel;
  }

  public String toString() {
    if (errorIndication == null) {
      return "noError";
    }
    return errorIndication.toString();
  }
}


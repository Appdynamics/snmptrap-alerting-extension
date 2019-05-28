/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */





package org.snmp4j;

import org.snmp4j.security.SecurityModel;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;

// for JavaDoc

/**
 * User based target for SNMPv3 or later.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class UserTarget extends SecureTarget {

  private static final long serialVersionUID = -1426511355567423746L;

  private OctetString authoritativeEngineID = new OctetString();

  /**
   * Creates a target for a user based security model target.
   */
  public UserTarget() {
  }

  /**
   * Creates a SNMPv3 USM target with security level noAuthNoPriv, one second
   * time-out without retries.
   * @param address
   *    the transport <code>Address</code> of the target.
   * @param securityName
   *    the USM security name to be used to access the target.
   * @param authoritativeEngineID
   *    the authoritative engine ID as a possibly zero length byte
   *    array which must not be <code>null</code>.
   */
  public UserTarget(Address address, OctetString securityName,
                    byte[] authoritativeEngineID) {
    super(address, securityName);
    setAuthoritativeEngineID(authoritativeEngineID);
  }

  /**
   * Creates a SNMPv3 USM target with the supplied security level, one second
   * time-out without retries.
   * @param address
   *    the transport <code>Address</code> of the target.
   * @param securityName
   *    the USM security name to be used to access the target.
   * @param authoritativeEngineID
   *    the authoritative engine ID as a possibly zero length byte
   *    array which must not be <code>null</code>.
   * @param securityLevel
   *    the {@link org.snmp4j.security.SecurityLevel} to use.
   * @since 1.1
   */
  public UserTarget(Address address, OctetString securityName,
                    byte[] authoritativeEngineID, int securityLevel) {
    super(address, securityName);
    setAuthoritativeEngineID(authoritativeEngineID);
    setSecurityLevel(securityLevel);
  }

  /**
   * Sets the authoritative engine ID of this target.
   * @param authoritativeEngineID
   *    a possibly zero length byte array (must not be <code>null</code>).
   */
  public void setAuthoritativeEngineID(byte[] authoritativeEngineID) {
    this.authoritativeEngineID.setValue(authoritativeEngineID);
  }

  /**
   * Gets the authoritative engine ID of this target.
   * @return
   *    a possibly zero length byte array.
   */
  public byte[] getAuthoritativeEngineID() {
    return authoritativeEngineID.getValue();
  }

  /**
   * Gets the security model for the user target.
   *
   * @return
   *    {@link org.snmp4j.security.SecurityModel#SECURITY_MODEL_USM}
   */
  public int getSecurityModel() {
    return SecurityModel.SECURITY_MODEL_USM;
  }

  /**
   * Sets the security model for the user target.
   *
   * @param securityModel
   *    {@link org.snmp4j.security.SecurityModel#SECURITY_MODEL_USM}, for any other value a
   *    <code>IllegalArgumentException</code> is thrown.
   */
  public void setSecurityModel(int securityModel) {
    if (securityModel != SecurityModel.SECURITY_MODEL_USM) {
      throw new IllegalArgumentException("The UserTarget target can only be " +
                                         "used with the User Based Security " +
                                         "Model (USM)");
    }
  }

}


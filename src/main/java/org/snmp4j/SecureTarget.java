/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j;

import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;

import java.io.Serializable;

/**
 * The <code>SecureTarget</code> is an security model independent abstract class
 * for all targets supporting secure SNMP communication.
 *
 * @author Jochen Katz & Frank Fock
 * @version 1.0
 */
public abstract class SecureTarget
    extends AbstractTarget implements Serializable {

  private static final long serialVersionUID = 3864834593299255038L;

  private int securityLevel = SecurityLevel.NOAUTH_NOPRIV;
  private int securityModel = SecurityModel.SECURITY_MODEL_ANY;
  private OctetString securityName = new OctetString();

  /**
   * Default constructor.
   */
  protected SecureTarget() {
  }

  /**
   * Creates a SNMPv3 secure target with an address and security name.
   * @param address
   *    an <code>Address</code> instance denoting the transport address of the
   *    target.
   * @param securityName
   *    a <code>OctetString</code> instance representing the security name
   *    of the USM user used to access the target.
   */
  protected SecureTarget(Address address, OctetString securityName) {
    super(address);
    setSecurityName(securityName);
  }

  /**
   * Gets the security model associated with this target.
   * @return
   *    an <code>int</code> value as defined in the {@link org.snmp4j.security.SecurityModel}
   *    interface or any third party subclass thereof.
   */
  public int getSecurityModel() {
    return securityModel;
  }

  /**
   * Gets the security name associated with this target. The security name
   * is used by the security model to lookup further parameters like
   * authentication and privacy protocol settings from the security model
   * dependent internal storage.
   * @return
   *   an <code>OctetString</code> instance (never <code>null</code>).
   */
  public final OctetString getSecurityName() {
    return securityName;
  }

  /**
   * Gets the security level associated with this target.
   * @return
   *   one of
   *   <P><UL>
   *   <LI>{@link org.snmp4j.security.SecurityLevel#NOAUTH_NOPRIV}
   *   <LI>{@link org.snmp4j.security.SecurityLevel#AUTH_NOPRIV}
   *   <LI>{@link org.snmp4j.security.SecurityLevel#AUTH_PRIV}
   *   </UL></P>
   */
  public int getSecurityLevel() {
    return securityLevel;
  }

  /**
   * Sets the security level for this target. The supplied security level must
   * be supported by the security model dependent information associated with
   * the security name set for this target.
   * @param securityLevel
   *   one of
   *   <P><UL>
   *   <LI>{@link org.snmp4j.security.SecurityLevel#NOAUTH_NOPRIV}
   *   <LI>{@link org.snmp4j.security.SecurityLevel#AUTH_NOPRIV}
   *   <LI>{@link org.snmp4j.security.SecurityLevel#AUTH_PRIV}
   *   </UL></P>
   */
  public void setSecurityLevel(int securityLevel) {
    this.securityLevel = securityLevel;
  }

  /**
   * Sets the security model for this target.
   * @param securityModel
   *    an <code>int</code> value as defined in the {@link org.snmp4j.security.SecurityModel}
   *    interface or any third party subclass thereof.
   */
  public void setSecurityModel(int securityModel) {
    this.securityModel = securityModel;
  }

  /**
   * Sets the security name to be used with this target.
   * @param securityName
   *    an <code>OctetString</code> instance (must not be <code>null</code>).
   * @see #getSecurityName()
   */
  public final void setSecurityName(OctetString securityName) {
    this.securityName = securityName;
  }

}

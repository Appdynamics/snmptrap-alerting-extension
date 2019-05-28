/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j;

import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;

/**
 * A <code>CommunityTarget</code> represents SNMP target properties for
 * community based message processing models (SNMPv1 and SNMPv2c).
 * @author Frank Fock
 * @version 1.1
 */
public class CommunityTarget extends AbstractTarget {

  static final long serialVersionUID = 147443821594052003L;

  private OctetString community = new OctetString();

  /**
   * Default constructor.
   */
  public CommunityTarget() {
    setVersion(SnmpConstants.version1);
  }

  /**
   * Creates a fully specified communtity target.
   * @param address
   *    the transport <code>Address</code> of the target.
   * @param community
   *    the community to be used for the target.
   */
  public CommunityTarget(Address address, OctetString community) {
    super(address);
    setVersion(SnmpConstants.version1);
    setCommunity(community);
  }

  /**
   * Gets the community octet string.
   * @return
   *    an <code>OctetString</code> instance, never <code>null</code>.
   */
  public OctetString getCommunity() {
    return community;
  }

  /**
   * Sets the community octet sting.
   * @param community
   *    an <code>OctetString</code> instance which must not be
   *    <code>null</code>.
   */
  public void setCommunity(OctetString community) {
    if (community == null) {
      throw new IllegalArgumentException("Community must not be null");
    }
    this.community = community;
  }

  public String toString() {
    return "CommunityTarget["+toStringAbstractTarget()+
        ", community="+community+"]";
  }

}

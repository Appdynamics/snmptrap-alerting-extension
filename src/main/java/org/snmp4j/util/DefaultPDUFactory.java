/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */


package org.snmp4j.util;

import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;

/**
 * The <code>DefaultPDUFactory</code> is a default implementation of the
 * <code>PDUFactory</code> interface. It creates PDUs depending on the
 * target's message processing model. That is, a {@link org.snmp4j.PDUv1} instance is
 * created for a SNMPv1 target whereas a {@link org.snmp4j.ScopedPDU} is created
 * for a SNMPv3 target. In all other cases a {@link org.snmp4j.PDU} instance is created.
 *
 * @author Frank Fock
 * @version 1.7.3
 * @since 1.0.4
 */
public class DefaultPDUFactory implements PDUFactory {

  private int pduType = PDU.GET;

  /**
   * Creates a PDU factory for the {@link org.snmp4j.PDU#GET} PDU type.
   */
  public DefaultPDUFactory() {
  }

  /**
   * Creates a PDU factory for the specified PDU type.
   * @param pduType
   *    a PDU type as specified by {@link org.snmp4j.PDU}.
   */
  public DefaultPDUFactory(int pduType) {
    setPduType(pduType);
  }

  public void setPduType(int pduType) {
    this.pduType = pduType;
  }

  public int getPduType() {
    return pduType;
  }

  /**
   * Create a <code>PDU</code> instance for the supplied target.
   *
   * @param target the <code>Target</code> where the PDU to be created will be
   *   sent.
   * @return PDU a PDU instance that is compatible with the supplied target.
   */
  public PDU createPDU(Target target) {
    return createPDU(target, pduType);
  }

  /**
   * Create a <code>PDU</code> instance for the supplied target.
   *
   * @param target the <code>Target</code> where the PDU to be created will be
   *    sent.
   * @param pduType
   *    a PDU type as specified by {@link org.snmp4j.PDU}.
   * @return PDU
   *    a PDU instance that is compatible with the supplied target.
   */
  public static PDU createPDU(Target target, int pduType) {
    PDU request = createPDU(target.getVersion());
    request.setType(pduType);
    return request;
  }

  /**
   * Create a <code>PDU</code> instance for the specified SNMP version.
   * @param targetVersion
   *    a SNMP version as defined by {@link org.snmp4j.mp.SnmpConstants}.
   * @return
   *    a PDU instance that is compatible with the supplied target SNMP version.
   * @since 1.7.3
   */
  public static PDU createPDU(int targetVersion) {
    PDU request;
    switch (targetVersion) {
      case SnmpConstants.version3: {
        request = new ScopedPDU();
        break;
      }
      case SnmpConstants.version1: {
        request = new PDUv1();
        break;
      }
      default:
        request = new PDU();
    }
    return request;
  }
}

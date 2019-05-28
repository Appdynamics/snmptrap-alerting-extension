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

/**
 * A <code>AbstratTarget</code> class is an abstract representation of a remote
 * SNMP entity. It represents a target with an Address object, as well protocol
 * parameters such as retransmission and timeout policy. Implementors of the
 * <code>Target</code> interface can subclass <code>AbstratTarget</code> to
 * take advantage of the implementation of common <code>Target</code>
 * properties.
 *
 * @author Frank Fock
 * @version 1.6
 * @since 1.2
 */
public abstract class AbstractTarget implements Target {

  private Address address;
  private int version = SnmpConstants.version3;
  private int retries = 0;
  private long timeout = 1000;
  private int maxSizeRequestPDU = 65535;

  /**
   * Default constructor
   */
  protected AbstractTarget() {
  }

  /**
   * Creates a SNMPv3 target with no retries and a timeout of one second.
   * @param address
   *    an <code>Address</code> instance.
   */
  protected AbstractTarget(Address address) {
    this.address = address;
  }

  /**
   * Gets the address of this target.
   * @return
   *    an Address instance.
   */
  public Address getAddress() {
    return address;
  }

  /**
   * Sets the address of the target.
   * @param address
   *    an Address instance.
   */
  public void setAddress(Address address) {
    this.address = address;
  }

  /**
   * Sets the SNMP version (thus the SNMP messagen processing model) of the
   * target.
   * @param version
   *    the message processing model ID.
   * @see org.snmp4j.mp.SnmpConstants#version1
   * @see org.snmp4j.mp.SnmpConstants#version2c
   * @see org.snmp4j.mp.SnmpConstants#version3
   */
  public void setVersion(int version) {
    this.version = version;
  }

  /**
   * Gets the SNMP version (NMP messagen processing model) of the target.
   * @return
   *    the message processing model ID.
   * @see org.snmp4j.mp.SnmpConstants#version1
   * @see org.snmp4j.mp.SnmpConstants#version2c
   * @see org.snmp4j.mp.SnmpConstants#version3
   */
  public int getVersion() {
    return version;
  }

  /**
   * Sets the number of retries to be performed before a request is timed out.
   * @param retries
   *    the number of retries. <em>Note: If the number of retries is set to
   *    0, then the request will be sent out exactly once.</em>
   */
  public void setRetries(int retries) {
    if (retries < 0) {
      throw new IllegalArgumentException("Number of retries < 0");
    }
    this.retries = retries;
  }

  /**
   * Gets the number of retries.
   * @return
   *    an integer >= 0.
   */
  public int getRetries() {
    return retries;
  }

  /**
   * Sets the timeout for a target.
   * @param timeout
   *    timeout in milliseconds before a confirmed request is resent or
   *    timed out.
   */
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  /**
   * Gets the timeout for a target.
   * @return
   *    the timeout in milliseconds.
   */
  public long getTimeout() {
    return timeout;
  }

  /**
   * Gets the maxmim size of request PDUs that this target is able to respond
   * to. The default is 65535.
   * @return
   *    the maximum PDU size of request PDUs for this target. Which is always
   *    greater than 484.
   */
  public int getMaxSizeRequestPDU() {
    return maxSizeRequestPDU;
  }

  /**
   * Sets the maximum size of request PDUs that this target is able to receive.
   * @param maxSizeRequestPDU
   *    the maximum PDU (SNMP message) size this session will be able to
   *    process.
   */
  public void setMaxSizeRequestPDU(int maxSizeRequestPDU) {
    if (maxSizeRequestPDU < SnmpConstants.MIN_PDU_LENGTH) {
      throw new IllegalArgumentException("The minimum PDU length is: "+
                                         SnmpConstants.MIN_PDU_LENGTH);
    }
    this.maxSizeRequestPDU = maxSizeRequestPDU;
  }

  protected String toStringAbstractTarget() {
    return "address="+getAddress()+", version="+version+
        ", timeout="+timeout+", retries="+retries;
  }

  public String toString() {
    return getClass().getName()+"["+toStringAbstractTarget()+"]";
  }

  public Object clone() {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException ex) {
      return null;
    }
  }
}


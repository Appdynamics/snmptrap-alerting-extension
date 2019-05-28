/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j;

import org.snmp4j.smi.Address;

import java.io.Serializable;

// for JavaDoc

/**
 * A <code>Target</code> interface defines an abstract representation of a
 * remote SNMP entity. It represents a target with an Address object, as well
 * protocol parameters such as retransmission and timeout policy.
 *
 * @author Frank Fock
 * @version 1.6
 */
public interface Target extends Serializable, Cloneable {

  /**
   * Gets the address of this target.
   * @return
   *    an Address instance.
   */
  Address getAddress();

  /**
   * Sets the address of the target.
   * @param address
   *    an Address instance.
   */
  void setAddress(Address address);

  /**
   * Sets the SNMP version (thus the SNMP messagen processing model) of the
   * target.
   * @param version
   *    the message processing model ID.
   * @see org.snmp4j.mp.SnmpConstants#version1
   * @see org.snmp4j.mp.SnmpConstants#version2c
   * @see org.snmp4j.mp.SnmpConstants#version3
   */
  void setVersion(int version);

  /**
   * Gets the SNMP version (NMP messagen processing model) of the target.
   * @return
   *    the message processing model ID.
   * @see org.snmp4j.mp.SnmpConstants#version1
   * @see org.snmp4j.mp.SnmpConstants#version2c
   * @see org.snmp4j.mp.SnmpConstants#version3
   */
  int getVersion();

  /**
   * Sets the number of retries to be performed before a request is timed out.
   * @param retries
   *    the number of retries. <em>Note: If the number of retries is set to
   *    0, then the request will be sent out exactly once.</em>
   */
  void setRetries(int retries);

  /**
   * Gets the number of retries.
   * @return
   *    an integer >= 0.
   */
  int getRetries();

  /**
   * Sets the timeout for a target.
   * @param timeout
   *    timeout in milliseconds before a confirmed request is resent or
   *    timed out.
   */
  void setTimeout(long timeout);

  /**
   * Gets the timeout for a target.
   * @return
   *    the timeout in milliseconds.
   */
  long getTimeout();

  /**
   * Gets the maxmim size of request PDUs that this target is able to respond
   * to. The default is 65535.
   * @return
   *    the maximum PDU size of request PDUs for this target. Which is always
   *    greater than 484.
   */
  int getMaxSizeRequestPDU();

  /**
   * Sets the maximum size of request PDUs that this target is able to receive.
   * @param maxSizeRequestPDU
   *    the maximum PDU (SNMP message) size this session will be able to
   *    process.
   */
  void setMaxSizeRequestPDU(int maxSizeRequestPDU);

  Object clone();
}


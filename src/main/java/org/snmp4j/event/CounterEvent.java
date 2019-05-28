/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.event;

import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.util.EventObject;

// for JavaDoc

/**
 * <code>CounterEvent</code> is an event object that indicates that a specific
 * counter needs to be incremented.
 * <p>
 * At the same time a <code>CounterEvent</code>
 * can be used by the event originator to retrieve the actual value of the
 * specified counter. Listeners that maintain the specified counter value,
 * must set the new value when receiving the <code>CounterEvent</code> by using
 * the {@link #setCurrentValue(org.snmp4j.smi.Variable currentValue)} method.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class CounterEvent extends EventObject {

  private static final long serialVersionUID = 7916507798848195425L;

  private OID oid;
  private Variable currentValue = new Counter32();

  /**
   * Creates a <code>CounterEvent</code> for the specified counter.
   * @param source
   *    the source of the event.
   * @param oid
   *    the OID of the counter instance (typically, the counter is a scalar and
   *    thus the OID has to end on zero).
   */
  public CounterEvent(Object source, OID oid) {
    super(source);
    this.oid = oid;
  }

  /**
   * Gets the instance object identifier of the counter.
   * @return
   *    an <code>OID</code>.
   */
  public OID getOid() {
    return oid;
  }

  /**
   * Gets the current value of the counter, as set by the maintainer of the
   * counter (one of the event listeners).
   * @return
   *    a {@link org.snmp4j.smi.Counter32} or {@link org.snmp4j.smi.Counter64} instance.
   */
  public Variable getCurrentValue() {
    return currentValue;
  }

  /**
   * Sets the current value of the counter. This method has to be called by
   * the maintainer of the counter's value.
   *
   * @param currentValue
   *    a {@link org.snmp4j.smi.Counter32} or {@link org.snmp4j.smi.Counter64} instance.
   */
  public void setCurrentValue(Variable currentValue) {
    this.currentValue = currentValue;
  }
}

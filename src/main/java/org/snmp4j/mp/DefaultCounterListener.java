/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */





package org.snmp4j.mp;

import org.snmp4j.event.CounterEvent;
import org.snmp4j.event.CounterListener;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import java.util.Hashtable;

/**
 * The <code>DefaultCounterListener</code> is the default implementation of
 * the <code>CounterListener</code> interface. For any counter incrementation
 * event it checks whether the referenced counter object already exists. If not,
 * it will be created and initialized with one. Otherwise, the current value
 * will be incremented by one. In either case, the current value will be
 * returned in the event object.
 * <p>
 * To use a <code>DefaultCounterListener</code> with SNMP4J, add it to the
 * default <code>CounterSupport</code> by:
 * <pre>
 *   CounterSupport.getInstance().addCounterListener(new DefaultCounterListener());
 * </pre>
 *
 * @author Frank Fock
 * @version 1.0.1
 */
public class DefaultCounterListener implements CounterListener {

  private Hashtable counters = new Hashtable(50);

  /**
   * Default constructor.
   */
  public DefaultCounterListener() {
  }

  public synchronized void incrementCounter(CounterEvent event) {
    OID id = event.getOid();
    VariableBinding counter = (VariableBinding) counters.get(id);
    if (counter == null) {
      counter = new VariableBinding(id, new Counter32(1));
      counters.put(id, counter);
    }
    else {
      ((Counter32)counter.getVariable()).increment();
    }
    // write back current value
    event.setCurrentValue((Variable)
                          ((VariableBinding)counter).getVariable().clone());
  }
}

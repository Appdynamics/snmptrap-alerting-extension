/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */





package org.snmp4j.event;

import java.util.EventListener;

// for JavaDoc

/**
 * The <code>CounterListener</code> interface has to be implemented by listener
 * for {@link org.snmp4j.event.CounterEvent} events. By implementing this method, an object is
 * able to be informed by a {@link org.snmp4j.mp.MessageProcessingModel},
 * {@link org.snmp4j.security.SecurityModel}, or other objects about conditions causing
 * certain counters to be incremented.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface CounterListener extends EventListener {

  /**
   * Increment the supplied counter instance and return the current value
   * (after incrementation) in the event object if the event receiver is the
   * maintainer of the counter value.
   * @param event
   *   a <code>CounterEvent</code> instance.
   */
  void incrementCounter(CounterEvent event);

}

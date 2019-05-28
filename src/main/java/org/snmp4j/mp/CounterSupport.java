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

import java.util.Vector;

/**
 * The <code>CounterSupport</code> class provides support to fire
 * {@link org.snmp4j.event.CounterEvent} to registered listeners.
 * @author Frank Fock
 * @version 1.0
 */
public class CounterSupport {

  protected static CounterSupport instance = null;
  private transient Vector counterListeners;

  protected CounterSupport() {
  }

  /**
   * Gets the counter support singleton.
   * @return
   *    the <code>CounterSupport</code> instance.
   */
  public static CounterSupport getInstance() {
    if (instance == null) {
      instance = new CounterSupport();
    }
    return instance;
  }

  /**
   * Adds a <code>CounterListener</code>.
   * @param listener
   *    a <code>CounterListener</code> instance that needs to be informed when
   *    a counter needs to be incremented.
   */
  public synchronized void addCounterListener(CounterListener listener) {
    Vector v = (counterListeners == null) ?
        new Vector(2) : (Vector) counterListeners.clone();
    if (!v.contains(listener)) {
      v.addElement(listener);
      counterListeners = v;
    }
  }

  /**
   * Removes a previously added <code>CounterListener</code>.
   * @param listener
   *    a <code>CounterListener</code> instance.
   */
  public synchronized void removeCounterListener(CounterListener listener) {
    if (counterListeners != null && counterListeners.contains(listener)) {
      Vector v = (Vector) counterListeners.clone();
      v.removeElement(listener);
      counterListeners = v;
    }
  }

  /**
   * Inform all registered listeners that the supplied counter needs to be
   * incremented.
   * @param event
   *    a <code>CounterEvent</code> containing information about the counter to
   *    be incremented.
   */
  public void fireIncrementCounter(CounterEvent event) {
    if (counterListeners != null) {
      Vector listeners = counterListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((CounterListener) listeners.elementAt(i)).incrementCounter(event);
      }
    }
  }
}

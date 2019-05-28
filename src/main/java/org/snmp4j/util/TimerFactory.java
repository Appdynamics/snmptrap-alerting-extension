/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.util;

/**
 * The <code>TimerFactory</code> describes a factory for
 * <code>CommonTimer</code> instances.
 *
 * @author Frank Fock
 * @version 1.9
 * @since 1.9
 */
public interface TimerFactory {

  /**
   * Creates a new timer instance.
   * @return
   *    a <code>Timer</code>.
   */
  CommonTimer createTimer();

}

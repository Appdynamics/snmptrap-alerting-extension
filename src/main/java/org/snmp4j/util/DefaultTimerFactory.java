/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This <code>DefaultTimerFactory</code> creates a new <code>Timer</code>
 * which is configured to run as daemon.
 *
 * @author Frank Fock
 * @version 1.9
 * @since 1.9
 */
public class DefaultTimerFactory implements TimerFactory {

  public DefaultTimerFactory() {
  }

  public CommonTimer createTimer() {
    return new TimerAdapter();
  }

  class TimerAdapter implements CommonTimer {

    private Timer timer = new Timer(true);

    public void schedule(TimerTask task, long delay) {
      timer.schedule(task, delay);
    }

    public void cancel() {
      timer.cancel();
    }

    public void schedule(TimerTask task, Date firstTime, long period) {
      timer.schedule(task, firstTime, period);
    }

    public void schedule(TimerTask task, long delay, long period) {
      timer.schedule(task, delay, period);
    }
  }
}

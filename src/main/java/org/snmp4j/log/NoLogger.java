/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.log;

import java.util.Collections;
import java.util.Iterator;

/**
 * The <code>NoLogger</code> implements a <code>LogAdapter</code> that does
 * not perform any logging.
 *
 * @author Frank Fock
 * @version 1.6.1
 * @since 1.2.1
 */
public class NoLogger implements LogAdapter {

  static final NoLogger instance = new NoLogger();

  private NoLogger() {
  }

  public void debug(Object message) {
  }

  public void error(Object message) {
  }

  public void error(Object message, Throwable t) {
  }

  public void info(Object message) {
  }

  public boolean isDebugEnabled() {
    return false;
  }

  public boolean isInfoEnabled() {
    return false;
  }

  public boolean isWarnEnabled() {
    return false;
  }

  public void warn(Object message) {
  }

  public void fatal(Object message) {
  }

  public void fatal(Object message, Throwable throwable) {
  }

  public void setLogLevel(LogLevel level) {
  }

  public String getName() {
    return "";
  }

  public LogLevel getLogLevel() {
    return LogLevel.OFF;
  }

  public LogLevel getEffectiveLogLevel() {
    return LogLevel.OFF;
  }

  public Iterator getLogHandler() {
    return Collections.EMPTY_LIST.iterator();
  }

}

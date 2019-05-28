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
 * A <code>LogProxy</code> can be used for late binding loggers. A proxy
 * does not log anything, if its logger member is <code>null</code>.
 * Otherwise it forwards all its messages to that logger. Even if the
 * proxy logger is not set, the <code>LogProxy</code> has a name.
 *
 * @author Frank Fock
 * @version 1.8
 */
public class LogProxy implements LogAdapter {

  private String name;
  private LogAdapter logger;

  public LogProxy(String name) {
    this.name = name;
  }

  public LogProxy(LogAdapter logger) {
    this.logger = logger;
  }

  public void debug(Object message) {
    if (logger != null) {
      logger.debug(message);
    }
  }

  public void error(Object message) {
    if (logger != null) {
      logger.error(message);
    }
  }

  public void error(Object message, Throwable throwable) {
    if (logger != null) {
      logger.error(message, throwable);
    }
  }

  public void fatal(Object message) {
    if (logger != null) {
      logger.fatal(message);
    }
  }

  public void fatal(Object message, Throwable throwable) {
    if (logger != null) {
      logger.fatal(message, throwable);
    }
  }

  public LogLevel getEffectiveLogLevel() {
    if (logger != null) {
      return logger.getEffectiveLogLevel();
    }
    return LogLevel.OFF;
  }

  public Iterator getLogHandler() {
    if (logger != null) {
      return logger.getLogHandler();
    }
    return Collections.EMPTY_LIST.iterator();
  }

  public LogLevel getLogLevel() {
    if (logger != null) {
      return logger.getLogLevel();
    }
    return LogLevel.OFF;
  }

  public String getName() {
    if (logger != null) {
      logger.getName();
    }
    return name;
  }

  public void info(Object message) {
    if (logger != null) {
      logger.info(message);
    }
  }

  public boolean isDebugEnabled() {
    if (logger != null) {
      return logger.isDebugEnabled();
    }
    return false;
  }

  public boolean isInfoEnabled() {
    if (logger != null) {
      return logger.isInfoEnabled();
    }
    return false;
  }

  public boolean isWarnEnabled() {
    if (logger != null) {
      return logger.isWarnEnabled();
    }
    return false;
  }

  public void setLogLevel(LogLevel level) {
    if (logger != null) {
      logger.setLogLevel(level);
    }
  }

  public void warn(Object message) {
    if (logger != null) {
      logger.warn(message);
    }
  }

  /**
   * Gets the proxied logger.
   * @return
   *    a LogAdapter the actually logs the messages.
   */
  public LogAdapter getLogger() {
    return logger;
  }

  /**
   * Sets the logger that logs the log messages logged with this proxy.
   * @param logger
   *    a LogAdapter.
   */
  public void setLogger(LogAdapter logger) {
    this.logger = logger;
  }
}

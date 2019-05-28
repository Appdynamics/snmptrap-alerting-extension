/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.log;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The <code>JavaLogAdapter</code> log adapter provides logging for SNMP4J
 * through the Java logging (<code>java.util.logging</code>).
 *
 * @author Frank Fock
 * @version 1.9.1
 * @since 1.7.2
 */
public class JavaLogAdapter implements LogAdapter {

  private final Logger logger;

  public JavaLogAdapter(Logger logger) {
    this.logger = logger;
  }

  // ---- Checking methods

  public boolean isDebugEnabled() {
    return isLoggable(LogLevel.DEBUG);
  }

  public boolean isInfoEnabled() {
    return isLoggable(LogLevel.INFO);
  }

  public boolean isWarnEnabled() {
    return isLoggable(LogLevel.WARN);
  }

  // ---- Logging methods

  public void debug(Object message) {
    log(LogLevel.DEBUG, message.toString(), null);
  }

  public void info(Object message) {
    log(LogLevel.INFO, message.toString(), null);
  }

  public void warn(Object message) {
    log(LogLevel.WARN, message.toString(), null);
  }

  public void error(Object message) {
    log(LogLevel.ERROR, message.toString(), null);
  }

  public void error(Object message, Throwable t) {
    log(LogLevel.ERROR, message.toString(), t);
  }

  public void fatal(Object message) {
    log(LogLevel.FATAL, message.toString(), null);
  }

  public void fatal(Object message, Throwable t) {
    log(LogLevel.FATAL, message.toString(), t);
  }

  // ---- Public methods

  public LogLevel getEffectiveLogLevel() {
    return fromJavaToSnmp4jLevel(logger.getLevel());
  }

  public Iterator getLogHandler() {
    return Arrays.asList(logger.getHandlers()).iterator();
  }

  public LogLevel getLogLevel() {
    return getEffectiveLogLevel();
  }

  public String getName() {
    return logger.getName();
  }

  public void setLogLevel(LogLevel logLevel) {
    logger.setLevel(fromSnmp4jToJdk(logLevel));
  }

  // ---- Private methods

  private boolean isLoggable(LogLevel logLevel) {
    return logger.isLoggable(fromSnmp4jToJdk(logLevel));
  }

  private void log(LogLevel logLevel, String msg, Throwable t) {
    logger.log(fromSnmp4jToJdk(logLevel), msg, t);
  }

  /**
   * Mapping from <code>org.snmp4j.log.LogLevel</code> to
   * <code>java.util.logging.Level</code>.
   *
   * @param logLevel
   *    The <code>LogLevel</code> to mapped
   * @return the <code>Level</code>
   *    mapped to or <code>null</code> if
   *    <code>null</code> was specified as the parameter.
   */
  private static Level fromSnmp4jToJdk(LogLevel logLevel) {
    if (logLevel == null) {
      return null;
    }
    switch (logLevel.getLevel()) {
      case LogLevel.LEVEL_ALL:
        return Level.ALL;
      case LogLevel.LEVEL_DEBUG:
        return Level.FINE;
      case LogLevel.LEVEL_TRACE:
        return Level.FINEST;
      case LogLevel.LEVEL_INFO:
        return Level.INFO;
      case LogLevel.LEVEL_WARN:
        return Level.WARNING;
      case LogLevel.LEVEL_ERROR:
        return Level.SEVERE;
      case LogLevel.LEVEL_FATAL:
        return Level.SEVERE;
      case LogLevel.LEVEL_OFF:
        return Level.OFF;
      case LogLevel.LEVEL_NONE:
        return Level.OFF;
      default:
        throw new IllegalArgumentException(
            "Mapping not defined from SNMP4J level " + logLevel
            + " to Java logging level");
    }
  }

  /**
   * Mapping from <code>java.util.logging.Level</code> to
   * <code>org.snmp4j.log.LogLevel</code>.
   *
   * @param level
   *    The <code>Level</code> to mapped
   * @return
   *    the <code>LogLevel</code> mapped to or {@link org.snmp4j.log.LogLevel#NONE} if
   *    <code>null</code> was specified as the parameter.
   */
  private static LogLevel fromJavaToSnmp4jLevel(Level level) {
    if (level == null) {
      return LogLevel.NONE;
    }
    else if (Level.ALL.equals(level)) {
      return LogLevel.ALL;
    }
    else if (Level.SEVERE.equals(level)) {
      return LogLevel.FATAL;
    }
    else if (Level.WARNING.equals(level)) {
      return LogLevel.WARN;
    }
    else if (Level.INFO.equals(level)) {
      return LogLevel.INFO;
    }
    else if (Level.CONFIG.equals(level)) {
      return LogLevel.DEBUG;
    }
    else if (Level.FINE.equals(level)) {
      return LogLevel.DEBUG;
    }
    else if (Level.FINER.equals(level)) {
      return LogLevel.TRACE;
    }
    else if (Level.FINEST.equals(level)) {
      return LogLevel.TRACE;
    }
    else if (Level.OFF.equals(level)) {
      return LogLevel.DEBUG;
    }
    else {
      throw new IllegalArgumentException("Mapping not defined from Java level "
                                         + level.getName() +
                                         " to SNMP4J logging level");
    }
  }
}

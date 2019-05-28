/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.log;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * The <code>JavaLogFactory</code> implements a SNMP4J LogFactory for
 * Java logging. In order to use Java's <code>java.util.logging</code>
 * for logging SNMP4J log messages the static {@link org.snmp4j.log.LogFactory#setLogFactory}
 * method has to be used before any SNMP4J class is referenced or instantiated.
 *
 * @author Frank Fock
 * @version 1.7.2
 */
public class JavaLogFactory extends LogFactory {

  public JavaLogFactory() {
  }

  protected LogAdapter createLogger(Class c) {
    return new JavaLogAdapter(Logger.getLogger(c.getName()));
  }

  protected LogAdapter createLogger(String className) {
    return new JavaLogAdapter(Logger.getLogger(className));
  }

  public LogAdapter getRootLogger() {
    return new JavaLogAdapter(Logger.getLogger(""));
  }

  public Iterator loggers() {
    Enumeration loggerNames = LogManager.getLogManager().getLoggerNames();
    return new JavaLogAdapterIterator(loggerNames);
  }

  public class JavaLogAdapterIterator implements Iterator {
    private Enumeration loggerNames;

    protected JavaLogAdapterIterator(Enumeration loggerNames) {
      this.loggerNames = loggerNames;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public final boolean hasNext() {
      return loggerNames.hasMoreElements();
    }

    public Object next() {
      String loggerName = (String) loggerNames.nextElement();
      Logger logger = Logger.getLogger(loggerName);
      return new JavaLogAdapter(logger);
    }
  }
}

/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.log;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * The <code>Log4jLogFactory</code> implements a SNMP4J LogFactory for
 * Log4J. In order to use Log4J for logging SNMP4J log messages the
 * static {@link org.snmp4j.log.LogFactory#setLogFactory} method has to be used before
 * any SNMP4J class is referenced or instantiated.
 *
 * @author Frank Fock
 * @version 1.6.1
 * @since 1.2.1
 */
public class Log4jLogFactory extends LogFactory {

  public Log4jLogFactory() {
  }

  protected LogAdapter createLogger(Class c) {
    return new Log4jLogAdapter(Logger.getLogger(c));
  }

  protected LogAdapter createLogger(String className) {
    return new Log4jLogAdapter(Logger.getLogger(className));
  }

  public LogAdapter getRootLogger() {
    return new Log4jLogAdapter(Logger.getRootLogger());
  }

  public Iterator loggers() {
    ArrayList l = Collections.list(Logger.getRootLogger().
                                   getLoggerRepository().
                                   getCurrentLoggers());
    for (int i=0; i < l.size(); i++) {
      l.set(i, new Log4jLogAdapter((Logger)l.get(i)));
    }
    Collections.sort(l);
    return l.iterator();
  }
}

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
 * The <code>LogFactory</code> singleton is used by SNMP4J to determine
 * the logging framework used to process SNMP4J log messages. By default
 * {@link org.snmp4j.log.NoLogger} instances are used.
 *
 * @author Frank Fock
 * @version 1.8
 * @since 1.2.1
 */
public class LogFactory {

  public static final String SNMP4J_LOG_FACTORY_SYSTEM_PROPERTY =
      "snmp4j.LogFactory";

  private static LogFactory snmp4jLogFactory = null;
  private static boolean configChecked = false;

  /**
   * Gets the logger for the supplied class.
   *
   * @param c
   *    the class for which a logger needs to be created.
   * @return
   *    the <code>LogAdapter</code> instance.
   */
  public static LogAdapter getLogger(Class c) {
    checkConfig();
    if (snmp4jLogFactory == null) {
      return NoLogger.instance;
    }
    else {
      return snmp4jLogFactory.createLogger(c.getName());
    }
  }

  private static void checkConfig() {
    if (!configChecked) {
      configChecked = true;
      getFactoryFromSystemProperty();
    }
  }

  private synchronized static void getFactoryFromSystemProperty() {
    try {
      String factory =
          System.getProperty(SNMP4J_LOG_FACTORY_SYSTEM_PROPERTY, null);
      if (factory != null) {
        try {
          Class c = Class.forName(factory);
          snmp4jLogFactory = (LogFactory) c.newInstance();
        }
        catch (ClassNotFoundException ex) {
        }
        catch (IllegalAccessException ex) {
        }
        catch (InstantiationException ex) {
        }
      }
    }
    catch (SecurityException sec) {
    }
  }

  /**
   * Returns the top level logger.
   * @return
   *    a LogAdapter instance.
   * @since 1.7
   */
  public LogAdapter getRootLogger() {
    return NoLogger.instance;
  }

  /**
   * Gets the logger for the supplied class name.
   *
   * @param className
   *    the class name for which a logger needs to be created.
   * @return
   *    the <code>LogAdapter</code> instance.
   * @since 1.7
   */
  public static LogAdapter getLogger(String className) {
    checkConfig();
    if (snmp4jLogFactory == null) {
      return NoLogger.instance;
    }
    else {
      return snmp4jLogFactory.createLogger(className);
    }
  }

  /**
   * Creates a Logger for the specified class. This method returns the
   * {@link org.snmp4j.log.NoLogger} logger instance which disables logging.
   * Overwrite this method the return a custom logger to enable logging for
   * SNMP4J.
   *
   * @param c
   *    the class for which a logger needs to be created.
   * @return
   *    the <code>LogAdapter</code> instance.
   */
  protected LogAdapter createLogger(Class c) {
    return NoLogger.instance;
  }

  /**
   * Creates a Logger for the specified class. This method returns the
   * {@link org.snmp4j.log.NoLogger} logger instance which disables logging.
   * Overwrite this method the return a custom logger to enable logging for
   * SNMP4J.
   *
   * @param className
   *    the class name for which a logger needs to be created.
   * @return
   *    the <code>LogAdapter</code> instance.
   * @since 1.7
   */
  protected LogAdapter createLogger(String className) {
    return NoLogger.instance;
  }

  /**
   * Sets the log factory to be used by SNMP4J. Call this method before
   * any other SNMP4J class is referenced or created to set and use a custom
   * log factory.
   *
   * @param factory
   *    a <code>LogFactory</code> instance.
   */
  public static void setLogFactory(LogFactory factory) {
    configChecked = true;
    snmp4jLogFactory = factory;
  }

  /**
   * Gets the log factory to be used by SNMP4J. If the log factory has not been
   * initialized by {@link #setLogFactory} a new instance of {@link org.snmp4j.log.LogFactory}
   * is returned.
   *
   * @return
   *    a <code>LogFactory</code> instance.
   * @since 1.7
   */
  public static LogFactory getLogFactory() {
    if (snmp4jLogFactory == null) {
      return new LogFactory();
    }
    return snmp4jLogFactory;
  }

  /**
   * Returns all available LogAdapters in depth first order.
   * @return
   *    a read-only Iterator.
   * @since 1.7
   */
  public Iterator loggers() {
    return Collections.singletonList(NoLogger.instance).iterator();
  }

}

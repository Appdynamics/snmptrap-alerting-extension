/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.util;

import java.text.ParseException;

/**
 * The <code>OIDTextFormat</code> provides a textual representation of a raw
 * object ID.
 *
 * @author Frank Fock
 * @version 1.10
 * @since 1.10
 */
public interface OIDTextFormat {

  /**
   * Returns a textual representation of a raw object ID, for example as
   * dotted string ("1.3.6.1.4") or object name ("ifDescr") depending on the
   * formats representation rules.
   *
   * @param value
   *    the OID value to format.
   * @return
   *    the textual representation.
   */
  String format(int[] value);

  /**
   * Parses a textual representation of an object ID and returns its raw value.
   * @param text
   *    a textual representation of an OID.
   * @return
   *    the raw OID value
   * @throws java.text.ParseException
   *    if the OID cannot be parsed successfully.
   */
  int[] parse(String text) throws ParseException;

}

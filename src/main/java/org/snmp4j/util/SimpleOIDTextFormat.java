/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.util;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

import java.text.ParseException;
import java.util.StringTokenizer;

/**
 * The <code>SimpleOIDTextFormat</code> implements a simple textual
 * representation for object IDs as dotted string.
 *
 * @author Frank Fock
 * @version 1.10
 * @since 1.10
 */
public class SimpleOIDTextFormat implements OIDTextFormat {

  /**
   * Creates a simple OID text format.
   */
  public SimpleOIDTextFormat() {
  }

  /**
   * Returns a textual representation of a raw object ID as dotted
   * string ("1.3.6.1.4").
   *
   * @param value
   *    the OID value to format.
   * @return
   *    the textual representation.
   */
  public String format(int[] value) {
    StringBuffer buf = new StringBuffer(10*value.length);
    for (int i=0; i<value.length; i++) {
      if (i != 0) {
        buf.append('.');
      }
      buf.append((value[i] & 0xFFFFFFFFL));
    }
    return buf.toString();
  }

  /**
   * Parses a textual representation of an object ID as dotted string
   * (e.g. "1.3.6.1.2.1.1") and returns its raw value.
   *
   * @param text
   *    a textual representation of an OID.
   * @return
   *    the raw OID value.
   * @throws java.text.ParseException
   *    if the OID cannot be parsed successfully.
   */
  public int[] parse(String text) throws ParseException {
    StringTokenizer st = new StringTokenizer(text, ".", true);
    int size = st.countTokens();
    int[] value = new int[size];
    size = 0;
    StringBuffer buf = null;
    while (st.hasMoreTokens()) {
      String t = st.nextToken();
      if ((buf == null) && t.startsWith("'")) {
        buf = new StringBuffer();
        t = t.substring(1);
      }
      if ((buf != null) && (t.endsWith("'"))) {
        buf.append(t.substring(0, t.length()-1));
        OID o = new OctetString(buf.toString()).toSubIndex(true);
        int[] h = value;
        value = new int[st.countTokens()+h.length+o.size()];
        System.arraycopy(h, 0, value, 0, size);
        System.arraycopy(o.getValue(), 0, value, size, o.size());
        size += o.size();
        buf = null;
      }
      else if (buf != null) {
        buf.append(t);
      }
      else if (!".".equals(t)) {
        value[size++] = (int) Long.parseLong(t.trim());
      }
    }
    if (size < value.length) {
      int[] h = value;
      value = new int[size];
      System.arraycopy(h, 0, value, 0, size);
    }
    return value;
  }
}

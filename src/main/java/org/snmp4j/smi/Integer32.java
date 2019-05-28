/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.smi;

import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The <code>Integer32</code> represents 32bit signed integer values for SNMP.
 *
 * @author Frank Fock
 * @version 1.8
 */
public class Integer32 extends AbstractVariable
    implements AssignableFromInteger, AssignableFromString {

  private static final long serialVersionUID = 5046132399890132416L;

  private int value = 0;

  /**
   * Creates an <code>Integer32</code> with a zero value.
   */
  public Integer32() {
  }

  /**
   * Creates an <code>Integer32</code> variable with the supplied value.
   * @param value
   *    an integer value.
   */
  public Integer32(int value) {
    setValue(value);
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeInteger(outputStream, BER.INTEGER, value);
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    int newValue = BER.decodeInteger(inputStream, type);
    if (type.getValue() != BER.INTEGER) {
      throw new IOException("Wrong type encountered when decoding Counter: "+type.getValue());
    }
    setValue(newValue);
  }

  public int getSyntax() {
    return SMIConstants.SYNTAX_INTEGER;
  }

  public int hashCode() {
    return value;
  }

  public int getBERLength() {
    if ((value <   0x80) &&
        (value >= -0x80)) {
      return 3;
    }
    else if ((value <   0x8000) &&
             (value >= -0x8000)) {
      return 4;
    }
    else if ((value <   0x800000) &&
             (value >= -0x800000)) {
      return 5;
    }
    return 6;
  }

  public boolean equals(Object o) {
    if (o instanceof Integer32) {
      return ((Integer32)o).value == value;
    }
    return false;
  }

  public int compareTo(Object o) {
    return value - ((Integer32)o).value;
  }

  public String toString() {
    return Integer.toString(value);
  }

  public final void setValue(String value) {
    this.value = Integer.parseInt(value);
  }

  /**
   * Sets the value of this integer.
   * @param value
   *    an integer value.
   */
  public final void setValue(int value) {
    this.value = value;
  }

  /**
   * Gets the value.
   * @return
   *    an integer.
   */
  public final int getValue() {
    return value;
  }

  public Object clone() {
    return new Integer32(value);
  }

  public final int toInt() {
    return getValue();
  }

  public final long toLong() {
    return getValue();
  }

  public OID toSubIndex(boolean impliedLength) {
    return new OID(new int[] { value });
  }

  public void fromSubIndex(OID subIndex, boolean impliedLength) {
    setValue(subIndex.get(0));
  }
}


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
 * UnsignedInteger32 type is an SNMP type that represents unsigned 32bit
 * integer values (0 to 4294967295).
 *
 * @author Frank Fock
 * @version 1.8
 */
public class UnsignedInteger32 extends AbstractVariable
    implements AssignableFromLong, AssignableFromString {

  private static final long serialVersionUID = -2155365655395258383L;

  protected long value = 0;

  public UnsignedInteger32() {
  }

  /**
   * Creates an <code>UnsignedInteger32</code> from a <code>long</code> value.
   * @param value
   *    a <code>long</code> value which must not be greater 2^32-1 and not less
   *    zero.
   * @throws IllegalArgumentException
   *    if <code>value</code> &lt; 0 or &gt; 2^32-1.
   */
  public UnsignedInteger32(long value) {
    setValue(value);
  }

  /**
   * Creates an unsigned integer from a signed int value. Negative values
   * will become 2^31-1 through 2^32-1.
   * @param signedIntValue
   *    a signed int value.
   * @since 1.7
   */
  public UnsignedInteger32(int signedIntValue) {
    setValue(signedIntValue & 0xFFFFFFFFL);
  }

  /**
   * Creates an unsigned integer from a signed byte value. Negative values
   * will become 2^7-1 through 2^8-1.
   * @param signedByteValue
   *    a signed byte value.
   * @since 1.7
   */
  public UnsignedInteger32(byte signedByteValue) {
    setValue(signedByteValue & 0xFF);
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeUnsignedInteger(outputStream, BER.GAUGE, value);
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    long newValue = BER.decodeUnsignedInteger(inputStream, type);
    if (type.getValue() != BER.GAUGE) {
      throw new IOException("Wrong type encountered when decoding Gauge: "+
                            type.getValue());
    }
    setValue(newValue);
  }

  public int getSyntax() {
    return SMIConstants.SYNTAX_UNSIGNED_INTEGER32;
  }

  public int hashCode() {
    return (int)value;
  }

  public int getBERLength() {
    if (value < 0x80L) {
      return 3;
    }
    else if (value < 0x8000L) {
      return 4;
    }
    else if (value < 0x800000L) {
      return 5;
    }
    else if (value < 0x80000000L) {
      return 6;
    }
    return 7;
  }

  public boolean equals(Object o) {
    if (o instanceof UnsignedInteger32) {
      return (((UnsignedInteger32)o).value == value);
    }
    return false;
  }

  public int compareTo(Object o) {
    long diff = (value - ((UnsignedInteger32)o).getValue());
    if (diff < 0) {
      return -1;
    }
    else if (diff > 0) {
      return 1;
    }
    return 0;
  }

  public String toString() {
    return Long.toString(value);
  }

  public final void setValue(String value) {
    setValue(Long.parseLong(value));
  }

  public void setValue(long value) {
    if ((value < 0) || (value > 4294967295L)) {
      throw new IllegalArgumentException(
          "Argument must be an unsigned 32bit value");
    }
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  public Object clone() {
    return new UnsignedInteger32(value);
  }

  public final int toInt() {
    return (int)getValue();
  }

  public final long toLong() {
    return getValue();
  }

  public OID toSubIndex(boolean impliedLength) {
    return new OID(new int[] { toInt() });
  }

  public void fromSubIndex(OID subIndex, boolean impliedLength) {
    setValue(subIndex.getUnsigned(0));
  }

}


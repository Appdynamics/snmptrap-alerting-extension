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
import java.math.BigInteger;

/**
 * The <code>Counter64</code> class represents a 64bit unsigned integer type.
 * It is used for monotonically increasing values that wrap around at
 * 2^64-1 (18446744073709551615).
 * <p>
 * The unsigned 64bit value is represented by a signed 64bit long value
 * internally.
 *
 * @author Frank Fock
 * @version 1.8
 */
public class Counter64 extends AbstractVariable
    implements AssignableFromLong, AssignableFromString {

  private static final long serialVersionUID = 8741539680564150071L;

  private long value = 0;

  public Counter64() {
  }

  public Counter64(long value) {
    setValue(value);
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeUnsignedInt64(outputStream, BER.COUNTER64, value);
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    long newValue = BER.decodeUnsignedInt64(inputStream, type);
    if (type.getValue() != BER.COUNTER64) {
      throw new IOException("Wrong type encountered when decoding Counter64: " +
                            type.getValue());
    }
    setValue(newValue);
  }

  public int getSyntax() {
    return SMIConstants.SYNTAX_COUNTER64;
  }

  public int hashCode() {
    return (int) value;
  }

  public int getBERLength() {
    if (value < 0L) {
      return 11;
    }
    if (value < 0x80000000L) {
      if (value < 0x8000L) {
          return (value < 0x80L) ? 3 : 4;
      }
      return (value < 0x800000L) ? 5 : 6;
    }
    if (value < 0x800000000000L) {
      return (value < 0x8000000000L) ? 7 : 8;
    }
    return (value < 0x80000000000000L) ? 9 : 10;
  }

  public boolean equals(Object o) {
    if (o instanceof Counter64) {
      return ((Counter64) o).value == value;
    }
    return false;
  }

  public int compareTo(Object o) {
    long other = ((Counter64) o).value;
    for (int i = 63; i >= 0; i--) {
      if (((value >> i) & 1) !=
          ((other >> i) & 1)) {
        if (((value >> i) & 1) != 0) {
          return 1;
        }
        else {
          return -1;
        }
      }
    }
    return 0;
  }

  public String toString() {
    if ((value > 0) && (value < Long.MAX_VALUE)) {
      return Long.toString(value);
    }
    byte[] bytes = new byte[8];
    for (int i = 0; i < 8; i++) {
      bytes[i] = (byte) ((value >> ((7 - i) * 8)) & 0xFF);
    }
    BigInteger i64 = new BigInteger(1, bytes);
    return i64.toString();
  }

  public void setValue(String value) {
    this.value = Long.parseLong(value);
  }

  public void setValue(long value) {
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  public Object clone() {
    return new Counter64(value);
  }

  /**
   * Increment the value of the counter by one. If the current value is
   * 2^63-1 (9223372036854775807) then value will be set to -2^63. Nevertheless,
   * the BER encoded value of this counter will always be unsigned!
   */
  public void increment() {
    value++;
  }

  public final int toInt() {
    return (int)getValue();
  }

  public final long toLong() {
    return getValue();
  }

  public OID toSubIndex(boolean impliedLength) {
    throw new UnsupportedOperationException();
  }

  public void fromSubIndex(OID subIndex, boolean impliedLength) {
    throw new UnsupportedOperationException();
  }

}

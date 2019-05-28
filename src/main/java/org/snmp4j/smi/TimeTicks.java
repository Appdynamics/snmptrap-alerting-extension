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
import java.text.MessageFormat;

/**
 * The <code>TimeTicks</code> class represents the time in 1/100 seconds
 * since some epoch (which should be have been defined in the
 * corresponding MIB specification).
 *
 * @author Frank Fock
 * @version 1.7
 */
public class TimeTicks extends UnsignedInteger32 {

  private static final long serialVersionUID = 8663761323061572311L;

  private static final String FORMAT_PATTERN =
      "{0,choice,0#|1#1 day, |1<{0,number,integer} days, }"+
      "{1,number,integer}:{2,number,00}:{3,number,00}.{4,number,00}";

  public TimeTicks() {
  }

  /**
   * Copy constructor.
   * @param other
   *    a TimeTicks instance.
   * @since 1.7
   */
  public TimeTicks(TimeTicks other) {
    this.value = other.value;
  }

  public TimeTicks(long value) {
    super(value);
  }

  public Object clone() {
    return new TimeTicks(value);
  }

  public int getSyntax() {
    return SMIConstants.SYNTAX_TIMETICKS;
  }

  public void encodeBER(OutputStream os) throws IOException {
    BER.encodeUnsignedInteger(os, BER.TIMETICKS, super.getValue());
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    long newValue = BER.decodeUnsignedInteger(inputStream, type);
    if (type.getValue() != BER.TIMETICKS) {
      throw new IOException("Wrong type encountered when decoding TimeTicks: "+type.getValue());
    }
    setValue(newValue);
  }

  /**
   * Returns string with the value of this <code>TimeTicks</code> object as
   * "[days,]hh:mm:ss.hh".
   *
   * @return
   *    a <code>String</code> representation of this object.
   */
  public String toString() {
    return toString(FORMAT_PATTERN);
  }

  /**
   * Formats the content of this <code>TimeTicks</code> object according to
   * a supplied <code>MessageFormat</code> pattern.
   * @param pattern
   *    a <code>MessageFormat</code> pattern that takes up to five parameters
   *    which are: days, hours, minutes, seconds, and 1/100 seconds.
   * @return
   *    the formatted string representation.
   */
  public String toString(String pattern) {
    long hseconds, seconds, minutes, hours, days;
    long tt = getValue();

    days = tt / 8640000;
    tt %= 8640000;

    hours = tt / 360000;
    tt %= 360000;

    minutes = tt / 6000;
    tt %= 6000;

    seconds = tt / 100;
    tt %= 100;

    hseconds = tt;

    Long[] values = new Long[5];
    values[0] = new Long(days);
    values[1] = new Long(hours);
    values[2] = new Long(minutes);
    values[3] = new Long(seconds);
    values[4] = new Long(hseconds);

    return MessageFormat.format(pattern, (Object[])values);
  }

  /**
   * Returns the timeticks value as milliseconds (instead 1/100 seconds).
   * @return
   *    <code>getValue()*10</code>.
   * @since 1.7
   */
  public long toMilliseconds() {
    return value*10;
  }

  /**
   * Sets the timeticks value by milliseconds.
   * @param millis
   *    sets the value as <code>setValue(millis/10)</code>.
   * @since 1.7
   */
  public void fromMilliseconds(long millis) {
    setValue(millis/10);
  }
}


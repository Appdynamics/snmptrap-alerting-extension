/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.smi;

import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BER.MutableByte;
import org.snmp4j.asn1.BERInputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The <code>Null</code> class represents SMI Null and the derived
 * SMIv2 exception syntaxes.
 *
 * @author Frank Fock
 * @version 1.8
 */
public class Null extends AbstractVariable {

  private static final long serialVersionUID = 6907924131098190092L;

  private int syntax = SMIConstants.SYNTAX_NULL;

  public static final Null noSuchObject =
      new Null(SMIConstants.EXCEPTION_NO_SUCH_OBJECT);
  public static final Null noSuchInstance =
      new Null(SMIConstants.EXCEPTION_NO_SUCH_INSTANCE);
  public static final Null endOfMibView =
      new Null(SMIConstants.EXCEPTION_END_OF_MIB_VIEW);
  public static final Null instance =
      new Null(SMIConstants.SYNTAX_NULL);

  public Null() {
  }

  public Null(int exceptionSyntax) {
   setSyntax(exceptionSyntax);
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    MutableByte type = new MutableByte();
    BER.decodeNull(inputStream, type);
    this.syntax = type.getValue() & 0xFF;
  }

  public int getSyntax() {
    return syntax;
  }

  public int hashCode() {
    return getSyntax();
  }

  public int getBERLength() {
    return 2;
  }

  public boolean equals(Object o) {
    if (o instanceof Null) {
      return (((Null) o).getSyntax() == getSyntax());
    }
    return false;
  }

  public int compareTo(Object o) {
    return (getSyntax() - ((Null)o).getSyntax());
  }

  public String toString() {
    switch (getSyntax()) {
      case SMIConstants.EXCEPTION_NO_SUCH_OBJECT:
        return "noSuchObject";
      case SMIConstants.EXCEPTION_NO_SUCH_INSTANCE:
        return "noSuchInstance";
      case SMIConstants.EXCEPTION_END_OF_MIB_VIEW:
        return "endOfMibView";
    }
    return "Null";
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeHeader(outputStream, (byte)getSyntax(), 0);
  }

  public void setSyntax(int syntax) {
    if ((syntax != SMIConstants.SYNTAX_NULL) &&  (!isExceptionSyntax(syntax))) {
      throw new IllegalArgumentException("Syntax " + syntax +
                                         " is incompatible with Null type");
    }
    this.syntax = syntax;
  }

  public Object clone() {
    return new Null(this.syntax);
  }

  public static boolean isExceptionSyntax(int syntax) {
    switch (syntax) {
      case SMIConstants.EXCEPTION_NO_SUCH_OBJECT:
      case SMIConstants.EXCEPTION_NO_SUCH_INSTANCE:
      case SMIConstants.EXCEPTION_END_OF_MIB_VIEW:
        return true;
    }
    return false;
  }

  /**
   * Returns the syntax of this Null variable.
   * @return
   *    {@link org.snmp4j.smi.SMIConstants#SYNTAX_NULL} or one of the exception syntaxes
   *    {@link org.snmp4j.smi.SMIConstants#EXCEPTION_NO_SUCH_OBJECT},
   *    {@link org.snmp4j.smi.SMIConstants#EXCEPTION_NO_SUCH_INSTANCE}, or
   *    {@link org.snmp4j.smi.SMIConstants#EXCEPTION_END_OF_MIB_VIEW}
   * @since 1.7
   */
  public final int toInt() {
    return getSyntax();
  }

  /**
   * Returns the syntax of this Null variable.
   * @return
   *    {@link org.snmp4j.smi.SMIConstants#SYNTAX_NULL} or one of the exception syntaxes
   *    {@link org.snmp4j.smi.SMIConstants#EXCEPTION_NO_SUCH_OBJECT},
   *    {@link org.snmp4j.smi.SMIConstants#EXCEPTION_NO_SUCH_INSTANCE}, or
   *    {@link org.snmp4j.smi.SMIConstants#EXCEPTION_END_OF_MIB_VIEW}
   * @since 1.7
   */
  public final long toLong() {
    return getSyntax();
  }

  public OID toSubIndex(boolean impliedLength) {
    throw new UnsupportedOperationException();
  }

  public void fromSubIndex(OID subIndex, boolean impliedLength) {
    throw new UnsupportedOperationException();
  }
}


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
 * The <code>Opaque</code> class represents the SMI type Opaque which is used
 * to transparently exchange BER encoded values.
 *
 * @author Frank Fock
 * @version 1.7.6
 */
public class Opaque extends OctetString {

  private static final long serialVersionUID = -17056771587100877L;

  public Opaque() {
    super();
  }

  public Opaque(byte[] bytes) {
    super(bytes);
  }

  public int getSyntax() {
    return SMIConstants.SYNTAX_OPAQUE;
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeString(outputStream, BER.OPAQUE, getValue());
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    byte[] v = BER.decodeString(inputStream, type);
    if (type.getValue() != (BER.ASN_APPLICATION | 0x04)) {
      throw new IOException("Wrong type encountered when decoding OctetString: "+
                            type.getValue());
    }
    setValue(v);
  }

  public void setValue(OctetString value) {
    this.setValue(new byte[0]);
    append(value);
  }

  public String toString() {
    return super.toHexString();
  }

  public Object clone() {
    return new Opaque(super.getValue());
  }

}


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
 * The <code>BitString</code> class represents the obsolete SMI type
 * BIT STRING which has been defined in RFC 1442 (an SNMPv2 draft) but
 * which has been obsoleteted by RFC 1902 and RFC 2578. This type is
 * provided for compatibility only and should not be used for new
 * applications.
 *
 * @author Frank Fock
 * @version 1.7.4
 * @since 1.7.4
 */
public class BitString extends OctetString {

  private static final long serialVersionUID = -8739361280962307248L;

  /**
   * Creates a BIT STRING value.
   * @deprecated
   *    The BIT STRING type has been temporarily defined in RFC 1442
   *    and obsoleted by RFC 2578. Use OctetString (i.e. BITS syntax)
   *    instead.
   */
  public BitString() {
  }

  public int getSyntax() {
    return BER.ASN_BIT_STR;
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeString(outputStream, BER.BITSTRING, getValue());
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    byte[] v = BER.decodeString(inputStream, type);
    if (type.getValue() != BER.BITSTRING) {
      throw new IOException("Wrong type encountered when decoding BitString: "+
                            type.getValue());
    }
    setValue(v);
  }

  public Object clone() {
    BitString clone = new BitString();
    clone.setValue(super.getValue());
    return clone;
  }
}

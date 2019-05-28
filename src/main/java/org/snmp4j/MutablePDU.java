/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j;

import java.io.Serializable;

/**
 * The <code>MutablePDU</code> is a container for a <code>PDU</code>
 * instance.
 * @author Frank Fock
 * @version 1.0
 */
public class MutablePDU implements Serializable {

  private static final long serialVersionUID = 2511133364341663087L;

  private PDU pdu;

  public MutablePDU() {
  }

  public PDU getPdu() {
    return pdu;
  }

  public void setPdu(PDU pdu) {
    this.pdu = pdu;
  }
}

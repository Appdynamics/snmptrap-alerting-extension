/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.transport;

import java.io.Serializable;

/**
 * The <code>MessageLength</code> object contains information about the
 * length of a message and the length of its header.
 *
 * @author Frank Fock
 * @version 1.7
 * @since 1.7
 */
public class MessageLength implements Serializable {

  private static final long serialVersionUID = -2722178759367760246L;

  private int payloadLength;
  private int headerLength;

  /**
   * Constructs a MessageLength object.
   * @param headerLength
   *    the length in bytes of the message header.
   * @param payloadLength
   *    the length of the payload.
   */
  public MessageLength(int headerLength, int payloadLength) {
    this.payloadLength = payloadLength;
    this.headerLength = headerLength;
  }

  /**
   * Returns the length of the payload.
   * @return
   *    the length in bytes.
   */
  public int getPayloadLength() {
    return payloadLength;
  }

  /**
   * Returns the length of the header.
   * @return
   *    the length in bytes.
   */
  public int getHeaderLength() {
    return headerLength;
  }

  /**
   * Returns the total message length (header+payload).
   * @return
   *    the sum of {@link #getHeaderLength()} and {@link #getPayloadLength()}.
   */
  public int getMessageLength() {
    return headerLength + payloadLength;
  }

  public String toString() {
    return MessageLength.class.getName()+
        "[headerLength="+headerLength+",payloadLength="+payloadLength+"]";
  }
}

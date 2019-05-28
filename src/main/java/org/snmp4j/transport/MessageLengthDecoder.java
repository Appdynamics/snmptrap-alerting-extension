/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */


package org.snmp4j.transport;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The <code>MessageLengthDecoder</code> needs to be implemented for connection
 * oriented transport mappings, because those transport mappings have no message
 * boundaries. To determine the message length, the message header is decoded
 * in a protocol specific way.
 *
 * @author Frank Fock
 * @version 1.7
 * @since 1.7
 */
public interface MessageLengthDecoder {

  /**
   * Returns the minimum length of the header to be decoded. Typically this
   * is a constant value.
   * @return
   *    the minimum length in bytes.
   */
  int getMinHeaderLength();

  /**
   * Returns the total message length to read (including header) and
   * the actual header length.
   * @param buf
   *    a ByteBuffer with a minimum of {@link #getMinHeaderLength()}.
   * @return
   *    the total message length in bytes and the actual header length in bytes.
   * @throws java.io.IOException
   *    if the header cannot be decoded.
   */
  MessageLength getMessageLength(ByteBuffer buf) throws IOException;

}

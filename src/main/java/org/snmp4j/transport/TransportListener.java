/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */



package org.snmp4j.transport;

import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;

import java.nio.ByteBuffer;

// JavaDoc import

/**
 * The <code>TransportListener</code> interface is implemented by objects
 * that process incoming messages from <code>TransportMapping</code>s, for
 * example {@link org.snmp4j.MessageDispatcher}.
 *
 * @author Frank Fock
 * @version 1.6
 * @since 1.6
 */
public interface TransportListener {

  /**
   * Processes an incoming message.
   * @param sourceTransport
   *    a <code>TransportMapping</code> instance denoting the transport that
   *    received the message and that will be used to send any responses to
   *    this message. The <code>sourceTransport</code> has to support the
   *    <code>incomingAddress</code>'s implementation class.
   * @param incomingAddress
   *    the <code>Address</code> from which the message has been received.
   * @param wholeMessage
   *    an <code>ByteBuffer</code> containing the received message.
   * @since 1.6
   */
  void processMessage(TransportMapping sourceTransport,
                      Address incomingAddress,
                      ByteBuffer wholeMessage);
}

/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */



package org.snmp4j;

import org.snmp4j.smi.Address;
import org.snmp4j.transport.TransportListener;

import java.io.IOException;

/**
 * The <code>TransportMapping</code> defines the common interface for SNMP
 * transport mappings. A transport mapping can only support a single
 * transport protocol.
 *
 * @author Frank Fock
 * @version 1.6
 */
public interface TransportMapping {

  /**
   * Gets the <code>Address</code> class that is this transport mapping
   * supports.
   * @return
   *    a subclass of {@link org.snmp4j.smi.Address}.
   */
  Class getSupportedAddressClass();

  /**
   * Returns the address that represents the incoming address this transport
   * mapping uses to listen for incoming packets.
   *
   * @return
   *    the address for incoming packets or <code>null</code> this transport
   *    mapping is not configured to listen for incoming packets.
   * @since 1.6
   */
  Address getListenAddress();

  /**
   * Sends a message to the supplied address using this transport.
   * @param address
   *    an <code>Address</code> instance denoting the target address.
   * @param message
   *    the whole message as an array of bytes.
   * @throws java.io.IOException
   */
  void sendMessage(Address address, byte[] message) throws IOException;

  /**
   * Adds a message dispatcher to the transport. Normally, at least one
   * message dispatcher needs to be added to process responses (or requests).
   * @param dispatcher
   *    a MessageDispatcher instance.
   * @see org.snmp4j.MessageDispatcherImpl
   * @deprecated
   *    Use {@link #addTransportListener} instead. This method has
   *    been deprecated because the direct coupling between MessageDispatcher
   *    and TransportMappings is not flexible enough and prevents reusing
   *    TransportMappings for other purposes. This method will be removed
   *    with SNMP4J 2.0.
   */
  void addMessageDispatcher(MessageDispatcher dispatcher);

  /**
   * Removes a message dispatcher. Incoming messages will no longer be
   * propagated to the supplied message dispatcher.
   * @param dispatcher
   *    a previously added MessageDispatcher instance.
   * @see #addMessageDispatcher
   * @deprecated
   *    Use {@link #removeTransportListener} instead.
   */
  void removeMessageDispatcher(MessageDispatcher dispatcher);

  /**
   * Adds a transport listener to the transport. Normally, at least one
   * transport listener needs to be added to process incoming messages.
   * @param transportListener
   *    a <code>TransportListener</code> instance.
   * @since 1.6
   */
  void addTransportListener(TransportListener transportListener);

  /**
   * Removes a transport listener. Incoming messages will no longer be
   * propagated to the supplied <code>TransportListener</code>.
   * @param transportListener
   *    a <code>TransportListener</code> instance.
   * @since 1.6
   */
  void removeTransportListener(TransportListener transportListener);

  /**
   * Closes the transport an releases all bound resources synchronously.
   * @throws java.io.IOException
   */
  void close() throws IOException;

  /**
   * Listen for incoming messages. For connection oriented transports, this
   * method needs to be called before {@link #sendMessage} is called for the
   * first time.
   * @throws java.io.IOException
   */
  void listen() throws IOException;

  /**
   * Returns <code>true</code> if the transport mapping is listening for
   * incoming messages. For connection oriented transport mappings this
   * is a prerequisite to be able to send SNMP messages. For connectionless
   * transport mappings it is a prerequisite to be able to receive responses.
   * @return
   *    <code>true</code> if this transport mapping is listening for messages.
   * @since 1.1
   */
  boolean isListening();

  /**
   * Gets the maximum length of an incoming message that can be successfully
   * processed by this transport mapping implementation.
   * @return
   *    an integer > 484.
   */
  int getMaxInboundMessageSize();
}


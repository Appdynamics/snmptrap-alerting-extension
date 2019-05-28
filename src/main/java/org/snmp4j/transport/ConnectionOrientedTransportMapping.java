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

import java.io.IOException;

/**
 * Transport mappings for connection oriented transport protocols have to
 * implement this interface.
 *
 * @author Frank Fock
 * @version 1.7.1
 * @since 1.7
 */
public interface ConnectionOrientedTransportMapping extends TransportMapping {

  /**
   * Returns the <code>MessageLengthDecoder</code> used by this transport
   * mapping.
   * @return
   *    a MessageLengthDecoder instance.
   */
  MessageLengthDecoder getMessageLengthDecoder();

  /**
   * Sets the <code>MessageLengthDecoder</code> that decodes the total
   * message length from the header of a message.
   *
   * @param messageLengthDecoder
   *    a MessageLengthDecoder instance.
   */
  void setMessageLengthDecoder(MessageLengthDecoder messageLengthDecoder);

  /**
   * Sets the connection timeout. This timeout specifies the time a connection
   * may be idle before it is closed.
   * @param connectionTimeout
   *    the idle timeout in milliseconds. A zero or negative value will disable
   *    any timeout and connections opened by this transport mapping will stay
   *    opened until they are explicitly closed.
   */
  void setConnectionTimeout(long connectionTimeout);

  /**
   * Adds a transport state listener that is to be informed about connection
   * state changes.
   * @param l
   *    a TransportStateListener.
   */
  void addTransportStateListener(TransportStateListener l);

  /**
   * Removes the supplied transport state listener.
   * @param l
   *    a TransportStateListener.
   */
  void removeTransportStateListener(TransportStateListener l);

  /**
   * Closes the connection to the given remote address (socket).
   * @param remoteAddress
   *    the address of the remote socket.
   * @return
   *    <code>true</code> if the connection could be closed and
   *    <code>false</code> if either the connection does not exists.
   * @since 1.7.1
   */
  boolean close(Address remoteAddress) throws IOException;

}

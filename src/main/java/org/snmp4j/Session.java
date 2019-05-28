/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j;

import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;

import java.io.IOException;

/**
 * <code>Session</code> defines a common interface for all classes that
 * implement SNMP protocol operations based on SNMP4J.
 *
 * @author Frank Fock
 * @version 1.2
 */
public interface Session {

  /**
   * Closes the session and frees any allocated resources, i.e. sockets.
   * After a <code>Session</code> has been closed it must
   * be used.
   * @throws java.io.IOException
   *    if the session could not free all resources.
   */
  public void close() throws IOException;

  /**
   * Sends a <code>PDU</code> to the given target and returns the received
   * response <code>PDU</code>.
   * @param pdu
   *    the <code>PDU</code> to send.
   * @param target
   *    the <code>Target</code> instance that specifies how and where to send
   *    the PDU.
   * @return
   *    the received response encapsulated in a <code>ResponseEvent</code>
   *    instance. To obtain the received response <code>PDU</code> call
   *    {@link org.snmp4j.event.ResponseEvent#getResponse()}. If the request timed out,
   *    that method will return <code>null</code>. If the sent <code>pdu</code>
   *    is an unconfirmed PDU (notification, response, or report), then
   *    <code>null</code> will be returned.
   * @throws java.io.IOException
   *    if the message could not be send.
   */
  public ResponseEvent send(PDU pdu, Target target) throws IOException;

  /**
   * Asynchronously sends a <code>PDU</code> to the given target. The response
   * is then returned by calling the supplied <code>ResponseListener</code>
   * instance.
   *
   * @param pdu
   *    the PDU instance to send.
   * @param target
   *    the Target instance representing the target SNMP engine where to send
   *    the <code>pdu</code>.
   * @param userHandle
   *    an user defined handle that is returned when the request is returned
   *    via the <code>listener</code> object.
   * @param listener
   *    a <code>ResponseListener</code> instance that is called when
   *    <code>pdu</code> is a confirmed PDU and the request is either answered
   *    or timed out.
   * @throws java.io.IOException
   *    if the message could not be send.
   */
  public void send(PDU pdu, Target target, Object userHandle,
                   ResponseListener listener) throws IOException;

  /**
   * Sends a <code>PDU</code> to the given target and returns the received
   * response <code>PDU</code> encapsulated in a <code>ResponseEvent</code>
   * object that also includes:
   * <ul>
   * <li>the transport address of the response sending peer,
   * <li>the <code>Target</code> information of the target,
   * <li>the request <code>PDU</code>,
   * <li>the response <code>PDU</code> (if any).
   * </ul>
   * @param pdu
   *    the PDU instance to send.
   * @param target
   *    the Target instance representing the target SNMP engine where to send
   *    the <code>pdu</code>.
   * @param transport
   *    specifies the <code>TransportMapping</code> to be used when sending
   *    the PDU. If <code>transport</code> is <code>null</code>, the associated
   *    message dispatcher will try to determine the transport mapping by the
   *    <code>target</code>'s address.
   * @return
   *    the received response encapsulated in a <code>ResponseEvent</code>
   *    instance. To obtain the received response <code>PDU</code> call
   *    {@link org.snmp4j.event.ResponseEvent#getResponse()}. If the request timed out,
   *    that method will return <code>null</code>. If the sent <code>pdu</code>
   *    is an unconfirmed PDU (notification, response, or report), then
   *    <code>null</code> will be returned.
   * @throws java.io.IOException
   *    if the message could not be send.
   */
  public ResponseEvent send(PDU pdu, Target target,
                            TransportMapping transport) throws IOException;

  /**
   * Asynchronously sends a <code>PDU</code> to the given target. The response
   * is then returned by calling the supplied <code>ResponseListener</code>
   * instance.
   *
   * @param pdu
   *    the PDU instance to send.
   * @param target
   *    the Target instance representing the target SNMP engine where to send
   *    the <code>pdu</code>.
   * @param transport
   *    specifies the <code>TransportMapping</code> to be used when sending
   *    the PDU. If <code>transport</code> is <code>null</code>, the associated
   *    message dispatcher will try to determine the transport mapping by the
   *    <code>target</code>'s address.
   * @param userHandle
   *    an user defined handle that is returned when the request is returned
   *    via the <code>listener</code> object.
   * @param listener
   *    a <code>ResponseListener</code> instance that is called when
   *    <code>pdu</code> is a confirmed PDU and the request is either answered
   *    or timed out.
   * @throws java.io.IOException
   *    if the message could not be send.
   */
  public void send(PDU pdu, Target target, TransportMapping transport,
                   Object userHandle,
                   ResponseListener listener) throws IOException;


  /**
   * Cancels an asynchronous request. Any asynchronous request must be canceled
   * when the supplied response listener is being called, even if the
   * <code>ResponseEvent</code> indicates an error.
   * @param request
   *    a request PDU as sent via {@link #send(org.snmp4j.PDU pdu, org.snmp4j.Target target,
   *    Object userHandle, org.snmp4j.event.ResponseListener listener)} or any .
   * @param listener
   *    a ResponseListener instance.
   */
  public void cancel(PDU request, ResponseListener listener);
}


/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.event;

import org.snmp4j.PDU;
import org.snmp4j.smi.Address;

import java.util.EventObject;

// Imports needed for JavaDoc

/**
 * <code>ResponseEvent</code> associates a request PDU with the corresponding
 * response and an optional user object.
 *
 * @author Frank Fock
 * @version 1.1
 */
public class ResponseEvent extends EventObject {

  private static final long serialVersionUID = 3966730838956160070L;

  private Address peerAddress;
  private PDU request;
  private PDU response;
  private Object userObject;
  private Exception error;

  /**
   * Creates an <code>ResponseEvent</code> instance.
   * @param source
   *    the event source.
   * @param peerAddress
   *    the transport address of the entity that send the response.
   * @param request
   *    the request PDU (must not be <code>null</code>).
   * @param response
   *    the response PDU or <code>null</code> if the request timed out.
   * @param userObject
   *    an optional user object.
   */
  public ResponseEvent(Object source, Address peerAddress,
                       PDU request, PDU response, Object userObject) {
    super(source);
    setPeerAddress(peerAddress);
    setRequest(request);
    setResponse(response);
    setUserObject(userObject);
  }

  /**
   * Creates an <code>ResponseEvent</code> instance with an exception object
   * indicating a message processing error.
   * @param source
   *    the event source.
   * @param peerAddress
   *    the transport address of the entity that send the response.
   * @param request
   *    the request PDU (must not be <code>null</code>).
   * @param response
   *    the response PDU or <code>null</code> if the request timed out.
   * @param userObject
   *    an optional user object.
   * @param error
   *    an <code>Exception</code>.
   */
  public ResponseEvent(Object source,
                       Address peerAddress,
                       PDU request, PDU response,
                       Object userObject,
                       Exception error) {
    this(source, peerAddress, request, response, userObject);
    this.error = error;
  }

  /**
   * Gets the request PDU.
   * @return
   *    a <code>PDU</code>.
   */
  public PDU getRequest() {
    return request;
  }

  protected final void setPeerAddress(Address peerAddress) {
    this.peerAddress = peerAddress;
  }

  protected final void setRequest(PDU request) {
    this.request = request;
  }

  protected final void setResponse(PDU response) {
    this.response = response;
  }

  /**
   * Gets the response PDU.
   * @return
   *    a PDU instance if a response has been received. If the request
   *    timed out then <code>null</code> will be returned.
   */
  public PDU getResponse() {
    return response;
  }

  protected final void setUserObject(Object userObject) {
    this.userObject = userObject;
  }

  /**
   * Gets the user object that has been supplied to the asynchronous request
   * {@link org.snmp4j.Session#send(org.snmp4j.PDU pdu, Target target, Object userHandle,
   * org.snmp4j.event.ResponseListener listener)}.
   * @return
   *    an Object.
   */
  public Object getUserObject() {
    return userObject;
  }

  /**
   * Gets the exception object from the exception that has been generated
   * when the request processing has failed due to an error.
   * @return
   *    an <code>Exception</code> instance.
   */
  public Exception getError() {
    return error;
  }

  /**
   * Gets the transport address of the response sender.
   * @return
   *    the transport <code>Address</code> of the command responder that send
   *    this response, or <code>null</code> if no response has been received
   *    within the time-out interval or if an error occured (see
   *    {@link #getError()}).
   */
  public Address getPeerAddress() {
    return peerAddress;
  }

}

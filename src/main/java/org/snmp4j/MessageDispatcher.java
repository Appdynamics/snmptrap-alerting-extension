/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j;

import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.mp.*;
import org.snmp4j.smi.Address;
import org.snmp4j.transport.TransportListener;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * The <code>MessageDispatcher</code> interface defines common services of
 * instances that process incoming SNMP messages and dispatch them to
 * interested {@link org.snmp4j.CommandResponder} instances. It also provides a service
 * to send out outgoing SNMP messages.
 * <p>
 * A <code>MessageDispatcher</code> needs at least one {@link org.snmp4j.TransportMapping}
 * and at least one {@link org.snmp4j.mp.MessageProcessingModel} in order to be able to
 * process any messages.
 *
 * @author Frank Fock
 * @version 1.6
 */
public interface MessageDispatcher extends TransportListener {

  /**
   * Gets the next unique request ID. The returned ID is unique across
   * the last 2^31-1 IDs generated by this message dispatcher.
   * @return
   *    an integer value in the range 1..2^31-1. The returned ID can be used
   *    to map responses to requests send through this message dispatcher.
   * @since 1.1
   */
  int getNextRequestID();

  /**
   * Adds a {@link org.snmp4j.mp.MessageProcessingModel} to the dispatcher. In order to
   * support a specific SNMP protocol version, the message dispatcher needs
   * a message processing model to process messages before they can be
   * dispatched.
   * @param model
   *    a <code>MessageProcessingModel</code> instance.
   */
  void addMessageProcessingModel(MessageProcessingModel model);

  /**
   * Removes a previously added {@link org.snmp4j.mp.MessageProcessingModel} from
   * the dispatcher.
   * @param model
   *    a <code>MessageProcessingModel</code> instance.
   */
  void removeMessageProcessingModel(MessageProcessingModel model);

  /**
   * Gets the <code>MessageProcessingModel</code> for the supplied message
   * processing model ID.
   *
   * @param messageProcessingModel
   *    a message processing model ID
   *    (see {@link org.snmp4j.mp.MessageProcessingModel#getID()}).
   * @return
   *    a MessageProcessingModel instance if the ID is known, otherwise
   *    <code>null</code>
   */
  MessageProcessingModel getMessageProcessingModel(int messageProcessingModel);

  /**
   * Adds a {@link org.snmp4j.TransportMapping} to the dispatcher. The transport mapping
   * is used to send and receive messages to/from the network.
   * @param transport
   *   a <code>TransportMapping</code> instance.
   */
  void addTransportMapping(TransportMapping transport);

  /**
   * Removes a previously added {@link org.snmp4j.TransportMapping} from
   * the dispatcher.
   * @param transport
   *    a <code>TransportMapping</code> instance.
   * @return
   *    the <code>TransportMapping</code> instance supplied if it
   * could be successfully removed, <code>null</code> otherwise.
   */
  TransportMapping removeTransportMapping(TransportMapping transport);

  /**
   * Gets the <code>Collection</code> of transport mappings in this message
   * dispatcher.
   * @return
   *    a Collection with the registered transport mappings.
   */
  Collection getTransportMappings();

  /**
   * Returns a transport mapping that can handle the supplied address.
   * @param destAddress
   *    an Address instance.
   * @return
   *    a <code>TransportMapping</code> instance that can be used to sent
   *    a SNMP message to <code>destAddress</code> or <code>null</code> if
   *    such a transport mapping does not exists.
   * @since 1.6
   */
  TransportMapping getTransport(Address destAddress);

  /**
   * Adds a {@link org.snmp4j.CommandResponder} instance to the message dispatcher.
   * Successfully processed SNMP messages will be presented to all command
   * responder (in the order in which they have been added) until a responder
   * uses the {@link org.snmp4j.CommandResponderEvent#setProcessed(boolean processed)}
   * to set the processed status of the event to <code>true</code>.
   * @param listener
   *    a <code>CommandResponder</code> instance.
   */
  void addCommandResponder(CommandResponder listener);

  /**
   * Removes a previously added {@link org.snmp4j.CommandResponder} instance from
   * the message dispatcher.
   * @param listener
  *    a <code>CommandResponder</code> instance.
   */
  void removeCommandResponder(CommandResponder listener);

  /**
   * Sends a PDU to the supplied transport address.
   * @param transportMapping
   *    the <code>TransportMapping</code> to be used to send the PDU. If
   *    <code>transportMapping</code> is <code>null</code> the message
   *    dispatcher will determine the appropriate transport mapping for the
   *    given transport address.
   * @param transportAddress
   *    the target transport address.
   * @param messageProcessingModel
   *    typically the SNMP version.
   * @param securityModel
   *    Security Model to use.
   * @param securityName
   *    on behalf of this principal.
   * @param securityLevel
   *    Level of Security requested.
   * @param pdu
   *    the SNMP Protocol Data Unit
   * @param expectResponse
   *    <code>true</code> if a response is expected and a state reference should
   *    be saved (if needed for the supplied message processing model).
   * @return
   *    an <code>PduHandle</code> that uniquely identifies this request.
   * @throws org.snmp4j.MessageException
   */
  PduHandle sendPdu(TransportMapping transportMapping,
                    Address transportAddress,
                    int messageProcessingModel,    // typically, SNMP version
                    int securityModel,             // Security Model to use
                    byte[] securityName,
                    int securityLevel,
                    /* the following parameters are given in ScopedPDU
                       byte[] contextEngineID,
                       byte[] contextName,
                     */
                    PDU pdu,
                    boolean expectResponse) throws MessageException;

  /**
   * Sends a PDU to the supplied transport address and returns the
   * <code>PduHandle</code> that uniquely identifies the request as response
   * <em>after<em> the request has been sent and otional, if a
   * {@link org.snmp4j.mp.PduHandleCallback} is given, it returns also the
   * <code>PduHandle</code> just <em>before<em> the request is sent through the
   * the callback interface.
   *
   * @param transportMapping
   *    the <code>TransportMapping</code> to be used to send the PDU. If
   *    <code>transportMapping</code> is <code>null</code> the message
   *    dispatcher will determine the appropriate transport mapping for the
   *    given transport address.
   * @param transportAddress
   *    the target transport address.
   * @param messageProcessingModel
   *    typically the SNMP version.
   * @param securityModel
   *    Security Model to use.
   * @param securityName
   *    on behalf of this principal.
   * @param securityLevel
   *    Level of Security requested.
   * @param pdu
   *    the SNMP Protocol Data Unit
   * @param expectResponse
   *    <code>true</code> if a response is expected and a state reference should
   *    be saved (if needed for the supplied message processing model).
   * @param callback
   *    an optional callback instance that is informed (if not
   *    <code>null</code>) about the newly assigned PduHandle just before the
   *    message is sent out.
   * @return
   *    an <code>PduHandle</code> that uniquely identifies this request.
   * @throws org.snmp4j.MessageException
   */
  PduHandle sendPdu(TransportMapping transportMapping,
                    Address transportAddress,
                    int messageProcessingModel,    // typically, SNMP version
                    int securityModel,             // Security Model to use
                    byte[] securityName,
                    int securityLevel,
                    /* the following parameters are given in ScopedPDU
                       byte[] contextEngineID,
                       byte[] contextName,
                     */
                    PDU pdu,
                    boolean expectResponse,
                    PduHandleCallback callback) throws MessageException;

  /**
   * Sends a PDU to the supplied transport address. This method behaves like
   * a call to {@link #sendPdu(org.snmp4j.TransportMapping transportMapping,
   * org.snmp4j.smi.Address transportAddress, int messageProcessingModel,
   * int securityModel, byte[] securityName, int securityLevel, org.snmp4j.PDU pdu,
   * boolean expectResponse)} with <code>transportMapping</code> set to
   * <code>null</code>.
   *
   * @param transportAddress
   *    the target transport address.
   * @param messageProcessingModel
   *    typically the SNMP version.
   * @param securityModel
   *    Security Model to use.
   * @param securityName
   *    on behalf of this principal.
   * @param securityLevel
   *    Level of Security requested.
   * @param pdu
   *    the SNMP Protocol Data Unit
   * @param expectResponse
   *    <code>true</code> if a response is expected and a state reference should
   *    be saved (if needed for the supplied message processing model).
   * @return
   *    an <code>PduHandle</code> that uniquely identifies this request.
   * @throws org.snmp4j.MessageException
   */
  PduHandle sendPdu(Address transportAddress,
                    int messageProcessingModel,    // typically, SNMP version
                    int securityModel,             // Security Model to use
                    byte[] securityName,
                    int securityLevel,
                    /* the following parameters are given in ScopedPDU
                       byte[] contextEngineID,
                       byte[] contextName,
                     */
                    PDU pdu,
                    boolean expectResponse) throws MessageException;

  /**
   * Returns a response PDU to the sender of the corresponding request PDU.
   * @param messageProcessingModel int
   * @param securityModel int
   * @param securityName byte[]
   * @param securityLevel int
   * @param pdu PDU
   * @param maxSizeResponseScopedPDU int
   * @param stateReference StateReference
   * @param statusInformation StatusInformation
   * @throws org.snmp4j.MessageException
   * @return int
   */
  int returnResponsePdu(int messageProcessingModel,
                        int securityModel,
                        byte[] securityName,
                        int securityLevel,
                        /* the following parameters are given in ScopedPDU
                           byte[] contextEngineID,
                           byte[] contextName,
                         */
                        PDU pdu,
                        int maxSizeResponseScopedPDU,
                        StateReference stateReference,
                        StatusInformation statusInformation)
       throws MessageException;

  /**
   * Process an incoming SNMP message. The message is processed and dispatched
   * according to the message's content, the message processing models, and the
   * command responder available to the dispatcher.
   * @param sourceTransport
   *    a <code>TransportMapping</code> instance denoting the transport that
   *    received the message and that will be used to send any responses to
   *    this message. The <code>sourceTransport</code> has to support the
   *    <code>incomingAddress</code>'s implementation class.
   * @param incomingAddress
   *    the <code>Address</code> from which the message has been received.
   * @param wholeMessage
   *    an <code>BERInputStream</code> containing the received SNMP message.
   *    The supplied input stream must support marks, otherwise an
   *    <code>IllegalArgumentException</code> is thrown.
   * @deprecated
   *    Use {@link #processMessage(org.snmp4j.TransportMapping, org.snmp4j.smi.Address, java.nio.ByteBuffer)}
   *    instead.
   */
  void processMessage(TransportMapping sourceTransport,
                      Address incomingAddress,
                      BERInputStream wholeMessage);

  /**
   * Process an incoming SNMP message. The message is processed and dispatched
   * according to the message's content, the message processing models, and the
   * command responder available to the dispatcher.
   * @param sourceTransport
   *    a <code>TransportMapping</code> instance denoting the transport that
   *    received the message and that will be used to send any responses to
   *    this message. The <code>sourceTransport</code> has to support the
   *    <code>incomingAddress</code>'s implementation class.
   * @param incomingAddress
   *    the <code>Address</code> from which the message has been received.
   * @param wholeMessage
   *    an <code>ByteBuffer</code> containing the received SNMP message.
   */
  void processMessage(TransportMapping sourceTransport,
                      Address incomingAddress,
                      ByteBuffer wholeMessage);


  /**
   * Release any state references associated with the supplied
   * <code>PduHandle</code> in the specified message processing model.
   * @param messageProcessingModel
   *    a message processing model ID.
   * @param pduHandle
   *    the <code>PduHandle</code> that identifies a confirmed class message.
   * @see org.snmp4j.mp.MessageProcessingModel
   */
  void releaseStateReference(int messageProcessingModel,
                             PduHandle pduHandle);
}


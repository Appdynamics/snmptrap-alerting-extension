/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.mp;

import org.snmp4j.MessageDispatcher;
import org.snmp4j.MutablePDU;
import org.snmp4j.PDU;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.asn1.BEROutputStream;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;

import java.io.IOException;

// needed by JavaDoc:

/**
 * The <code>MessageProcessingModel</code> interface defines common methods
 * to all SNMP message processing models.
 * <p>Note: The read counter of the {@link org.snmp4j.asn1.BERInputStream} parameters in this
 * interface should not be reset while those methods are executed.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface MessageProcessingModel {

  int MPv1  = 0;
  int MPv2c = 1;
  int MPv2u = 2;
  int MPv3  = 3;

  /**
   * Gets the numerical ID of the message processing model as defined by the
   * constants in this interface or by an appropriate constant in the
   * class implementing this interface.
   * @return
   *    a positive integer value.
   */
  int getID();

  /**
   * Prepares an outgoing message as defined in RFC3412 §7.1.
   *
   * @param transportAddress
   *    the destination transport <code>Address</code>.
   * @param maxMsgSize
   *    the maximum message size the transport mapping for the destination
   *    address is capable of.
   * @param messageProcessingModel
   *    the {@link org.snmp4j.mp.MessageProcessingModel} ID (typically, the SNMP version).
   * @param securityModel
   *    the security model ID (see {@link org.snmp4j.security.SecurityModel}) to use.
   * @param securityName
   *    the principal on behalf the message is to be sent.
   * @param securityLevel
   *    the level of security requested (see {@link org.snmp4j.security.SecurityLevel}).
   * @param pdu
   *    the <code>PDU</code> to send. For a SNMPv1 trap <code>pdu</code> has
   *    to be a {@link org.snmp4j.PDUv1} instance, for SNMPv3 messages it has to be a
   *    {@link org.snmp4j.ScopedPDU} instance.
   * @param expectResponse
   *    indicates if a message expects a response. This has to be
   *    <code>true</code> for confirmed class PDUs and <code>false</code>
   *    otherwise.
   * @param sendPduHandle
   *    the <code>PduHandle</code> that uniquely identifies the sent message.
   * @param destTransportAddress
   *    returns the destination transport address (currently set always set to
   *    <code>transportAddress</code>.
   * @param outgoingMessage
   *    returns the message to send.
   * @throws java.io.IOException if the supplied PDU could not be encoded to the
   *  <code>outgoingMessage</code>
   * @return
   *   the status of the message preparation. {@link org.snmp4j.mp.SnmpConstants#SNMP_MP_OK}
   *   is returned if on success, otherwise any of the
   *   <code>SnmpConstants.SNMP_MP_*</code> values may be returned.
   */
  int prepareOutgoingMessage(Address transportAddress,
                             int maxMsgSize,
                             int messageProcessingModel,
                             int securityModel,
                             byte[] securityName,
                             int securityLevel,
                             /* the following parameters are given in ScopedPDU
                                   byte[] contextEngineID,
                                   byte[] contextName,
                              */
                             PDU pdu,
                             boolean expectResponse,
                             PduHandle sendPduHandle,
                             Address destTransportAddress,
                             BEROutputStream outgoingMessage)
      throws IOException;

  /**
   * Prepares a response message as defined in RFC3412 §7.1.
   *
   * @param messageProcessingModel int
   *    the {@link org.snmp4j.mp.MessageProcessingModel} ID (typically, the SNMP version).
   * @param maxMsgSize
   *    the maximum message size the transport mapping for the destination
   *    address is capable of.
   * @param securityModel
   *    the security model ID (see {@link org.snmp4j.security.SecurityModel}) to use.
   * @param securityName
   *    the principal on behalf the message is to be sent.
   * @param securityLevel
   *    the level of security requested (see {@link org.snmp4j.security.SecurityLevel}).
   * @param pdu
   *    the <code>PDU</code> to send. For a SNMPv1 trap <code>pdu</code> has
   *    to be a {@link org.snmp4j.PDUv1} instance, for SNMPv3 messages it has to be a
   *    {@link org.snmp4j.ScopedPDU} instance.
   * @param maxSizeResponseScopedPDU
   *    the maximum size of the scoped PDU the sender (of the request) can
   *    accept.
   * @param stateReference
   *    reference to state information presented with the request.
   * @param statusInformation
   *    returns success or error indication. When an error occured, the error
   *    counter OID and value are included.
   * @param outgoingMessage
   *    returns the message to send.
   * @throws java.io.IOException
   *    if an internal error or a resource exception occured.
   * @return
   *   the status of the message preparation. {@link org.snmp4j.mp.SnmpConstants#SNMP_MP_OK}
   *   is returned if on success, otherwise any of the
   *   <code>SnmpConstants.SNMP_MP_*</code> values may be returned.
   */
  int prepareResponseMessage(int messageProcessingModel,
                             int maxMsgSize,
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
                             StatusInformation statusInformation,
                             BEROutputStream outgoingMessage)
      throws IOException;

  /**
   * Prepare data elements from an incoming SNMP message as described in
   * RFC3412 §7.2.
   *
   * @param messageDispatcher
   *    the <code>MessageDispatcher</code> instance to be used to send reports.
   *    Thus, <code>messageDispatcher</code> is typically the calling module.
   * @param transportAddress
   *    the origin transport address.
   * @param wholeMsg
   *    the whole message as received from the network.
   * @param messageProcessingModel
   *    returns the message processing model (typically the SNMP version).
   * @param securityModel
   *    returns the security model ID (see {@link org.snmp4j.security.SecurityModel}.
   * @param securityName
   *    returns the principal.
   * @param securityLevel
   *    returns the requested security level (see {@link org.snmp4j.security.SecurityLevel}).
   * @param pdu
   *    returns SNMP protocol data unit (the payload of the received message).
   * @param sendPduHandle
   *    returns the handle to match request.
   * @param maxSizeResponseScopedPDU
   *    returns the maximum size of the scoped PDU the sender can accept.
   * @param statusInformation
   *    returns success or error indication. When an error occured, the error
   *    counter OID and value are included.
   * @param stateReference
   *    returns the state reference to be used for a possible response. On input
   *    the stateReference may contain information about the transport mapping
   *    of the incoming request. This allows the
   *    <code>MessageProcessingModel</code> to send reports over the same
   *    transport as it received them.
   * @throws java.io.IOException
   *    if the decoding of the message failed.
   * @return int
   *   the status of the message preparation. {@link org.snmp4j.mp.SnmpConstants#SNMP_MP_OK}
   *   is returned on success, otherwise any of the
   *   <code>SnmpConstants.SNMP_MP_*</code> values may be returned.
   */
  int prepareDataElements(MessageDispatcher messageDispatcher,
                          Address transportAddress,
                          BERInputStream wholeMsg,
                          Integer32 messageProcessingModel,
                          Integer32 securityModel,
                          OctetString securityName,
                          Integer32 securityLevel,
                          /* the following parameters are given in ScopedPDU
                                byte[] contextEngineID,
                                byte[] contextName,
                           */
                          MutablePDU pdu,
                          PduHandle sendPduHandle,
                          Integer32 maxSizeResponseScopedPDU,
                          StatusInformation statusInformation,
                          MutableStateReference stateReference)
      throws IOException;

  /**
   * Checks whether the supplied SNMP protocol version is supported by this
   * message processing model.
   * @param snmpProtocolVersion
   *    the SNMP protocol version.
   * @return
   *    <code>true</code> if the supplied SNMP protocol is supported,
  *     <code>false</code> otherwise.
   */
  boolean isProtocolVersionSupported(int snmpProtocolVersion);

  /**
   * Release the state reference associated with the supplied
   * <code>PduHandle</code>.
   * @param pduHandle
   *    a <code>PduHandle</code>.
   */
  void releaseStateReference(PduHandle pduHandle);
}


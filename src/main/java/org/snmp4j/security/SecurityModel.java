/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.security;

import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.asn1.BEROutputStream;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;

import java.io.IOException;

// for JavaDoc

/**
 * The <code>SecurityModel</code> interface as described in RFC3411 section 4.4.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface SecurityModel {

  int SECURITY_MODEL_ANY = 0;
  int SECURITY_MODEL_SNMPv1 = 1;
  int SECURITY_MODEL_SNMPv2c = 2;
  int SECURITY_MODEL_USM = 3;

  /**
   * Gets the ID of the security model.
   * @return
   *    one of the integer constants defined in the {@link org.snmp4j.security.SecurityModel}
   *    interface.
   * @see org.snmp4j.security.SecurityModel#SECURITY_MODEL_ANY
   * @see org.snmp4j.security.SecurityModel#SECURITY_MODEL_SNMPv1
   * @see org.snmp4j.security.SecurityModel#SECURITY_MODEL_SNMPv2c
   * @see org.snmp4j.security.SecurityModel#SECURITY_MODEL_USM
   */
  int getID();

  /**
   * Creates a new {@link org.snmp4j.security.SecurityParameters} instance that corresponds to this
   * security model.
   * @return
   *    a new <code>SecurityParameters</code> instance.
   */
  SecurityParameters newSecurityParametersInstance();

  /**
   * Creates a new {@link org.snmp4j.security.SecurityStateReference} instance that corresponds to
   * this security model.
   * @return
   *    a new <code>SecurityStateReference</code> instance.
   */
  SecurityStateReference newSecurityStateReference();

  /**
   * Generate a request message.
   * @param messageProcessingModel
   *    the ID of the message processing model (SNMP version) to use.
   * @param globalData
   *    the message header and admin data.
   * @param maxMessageSize
   *    the maximum message size of the sending (this) SNMP entity for the
   *    selected transport mapping (determined by the message processing model).
   * @param securityModel
   *    the security model for the outgoing message.
   * @param securityEngineID
   *    the authoritative SNMP entity.
   * @param securityName
   *    the principal on behalf of this message is generated.
   * @param securityLevel
   *    the requested {@link org.snmp4j.security.SecurityLevel}.
   * @param scopedPDU
   *    a BERInputStream containing the message (plain text) payload.
   * @param securityParameters
   *    returns the {@link org.snmp4j.security.SecurityParameters} filled by the security model.
   * @param wholeMsg
   *    returns the complete generated message in a <code>BEROutputStream</code>.
   *    The buffer of <code>wholeMsg</code> is set to <code>null</code> by the
   *    caller and must be set by the implementation of this method.
   * @throws java.io.IOException
   *    if generation of the message fails because of an internal or an resource
   *    error.
   * @return
   *    the error status of the message generation. On success
   *    {@link org.snmp4j.mp.SnmpConstants#SNMPv3_USM_OK} is returned, otherwise one of the
   *    other <code>SnmpConstants.SNMPv3_USM_*</code> values is returned.
   */
  int generateRequestMessage(int messageProcessingModel,
                             byte[] globalData,
                             int maxMessageSize,
                             int securityModel,
                             byte[] securityEngineID,
                             byte[] securityName,
                             int securityLevel,
                             BERInputStream scopedPDU,
                             // out parameters
                             SecurityParameters securityParameters,
                             BEROutputStream wholeMsg) throws IOException;

  /**
   * Generates a response message.
   * @param messageProcessingModel
   *    the ID of the message processing model (SNMP version) to use.
   * @param globalData
   *    the message header and admin data.
   * @param maxMessageSize
   *    the maximum message size of the sending (this) SNMP entity for the
   *    selected transport mapping (determined by the message processing model).
   * @param securityModel
   *    the security model for the outgoing message.
   * @param securityEngineID
   *    the authoritative SNMP entity.
   * @param securityName
   *    the principal on behalf of this message is generated.
   * @param securityLevel
   *    the requested {@link org.snmp4j.security.SecurityLevel}.
   * @param scopedPDU
   *    a BERInputStream containing the message (plain text) payload.
   * @param securityStateReference
   *    a {@link org.snmp4j.security.SecurityStateReference} instance providing information from
   *    original request.
   * @param securityParameters
   *    returns the {@link org.snmp4j.security.SecurityParameters} filled by the security model.
   * @param wholeMsg
   *    returns the complete generated message in a <code>BEROutputStream</code>.
   *    The buffer of <code>wholeMsg</code> is set to <code>null</code> by the
   *    caller and must be set by the implementation of this method.
   * @throws java.io.IOException
   *    if generation of the message fails because of an internal or an resource
   *    error.
   * @return
   *    the error status of the message generation. On success
   *    {@link org.snmp4j.mp.SnmpConstants#SNMPv3_USM_OK} is returned, otherwise one of the
   *    other <code>SnmpConstants.SNMPv3_USM_*</code> values is returned.
   */
  int generateResponseMessage(int messageProcessingModel,
                              byte[] globalData,
                              int maxMessageSize,
                              int securityModel,
                              byte[] securityEngineID,
                              byte[] securityName,
                              int securityLevel,
                              BERInputStream scopedPDU,
                              SecurityStateReference securityStateReference,
                              // out parameters
                              SecurityParameters securityParameters,
                              BEROutputStream wholeMsg) throws IOException;

  /**
   * Processes an incoming message and returns its plaintext payload.
   * @param messageProcessingModel
   *    the ID of the message processing model (SNMP version) to use.
   * @param maxMessageSize
   *    the maximum message size of the message processing model for the
   *    transport mapping associated with this message's source address less
   *    the length of the maximum header length of the message processing model.
   *    This value is used by the security model to determine the
   *    <code>maxSizeResponseScopedPDU</code> value.
   * @param securityParameters
   *    the {@link org.snmp4j.security.SecurityParameters} for the received message.
   * @param securityModel
   *    the {@link org.snmp4j.security.SecurityModel} instance for the receied message.
   * @param securityLevel
   *    the {@link org.snmp4j.security.SecurityLevel} ID.
   * @param wholeMsg
   *    the <code>BERInputStream</code> containing the whole message as recieved
   *    on the wire.
   * @param securityEngineID
   *    the authoritative SNMP entity.
   * @param securityName
   *    the identification of the principal.
   * @param scopedPDU
   *    returns the message (plaintext) payload into the supplied
   *    <code>BEROutputStream</code>.
   *    The buffer of <code>scopedPDU</code> is set to <code>null</code> by the
   *    caller and must be set by the implementation of this method.
   * @param maxSizeResponseScopedPDU
   *    the determined maxmimum size for a response PDU.
   * @param securityStateReference
   *    the <code>SecurityStateReference</code> information needed for
   *    a response.
   * @param statusInfo
   *    the <code>StatusInformation</code> needed to generate reports if
   *    processing of the incoming message failed.
   * @throws java.io.IOException
   *    if an unexpected (internal) or an resource error occured.
   * @return
   *    the error status of the message processing. On success
   *    {@link org.snmp4j.mp.SnmpConstants#SNMPv3_USM_OK} is returned, otherwise one of the
   *    other <code>SnmpConstants.SNMPv3_USM_*</code> values is returned.
   */
  int processIncomingMsg(int messageProcessingModel,
                         int maxMessageSize,
                         SecurityParameters securityParameters,
                         SecurityModel securityModel,
                         int securityLevel,
                         BERInputStream wholeMsg,
                         OctetString securityEngineID,
                         OctetString securityName,
                         // out parameters
                         BEROutputStream scopedPDU,
                         Integer32 maxSizeResponseScopedPDU,
                         SecurityStateReference securityStateReference,
                         StatusInformation statusInfo) throws IOException;

}


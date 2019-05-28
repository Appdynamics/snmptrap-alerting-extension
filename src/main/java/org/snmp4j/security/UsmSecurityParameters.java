/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.security;

import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BER.MutableByte;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;

import java.io.IOException;
import java.io.OutputStream;

public class UsmSecurityParameters implements SecurityParameters {

  private static final LogAdapter logger =
      LogFactory.getLogger(UsmSecurityParameters.class);

  private static final int MAX_BER_LENGTH_WITHOU_SEC_PARAMS =
      32+2+ 6 + 6 + 32+2;

  private OctetString authoritativeEngineID = new OctetString();
  private Integer32 authoritativeEngineBoots = new Integer32();
  private Integer32 authoritativeEngineTime = new Integer32();
  private OctetString userName = new OctetString();
  private AuthenticationProtocol authenticationProtocol = null;
  private PrivacyProtocol privacyProtocol = null;
  private byte[] authenticationKey;
  private byte[] privacyKey;
  private OctetString privacyParameters = new OctetString();
  private OctetString authenticationParameters = new OctetString();
  private int securityParametersPosition = -1;
  private int authParametersPosition = -1;
  private int decodedLength = -1;

  public UsmSecurityParameters() {
  }

  public UsmSecurityParameters(OctetString authoritativeEngineID,
                               Integer32 authoritativeEngineBoots,
                               Integer32 authoritativeEngineTime,
                               OctetString userName,
                               AuthenticationProtocol authenticationProtocol,
                               PrivacyProtocol privacyProtocol) {
    this.authoritativeEngineID = authoritativeEngineID;
    this.authoritativeEngineBoots = authoritativeEngineBoots;
    this.authoritativeEngineTime = authoritativeEngineTime;
    this.privacyProtocol = privacyProtocol;
    this.userName = userName;
    this.authenticationProtocol = authenticationProtocol;
  }

  public byte[] getAuthoritativeEngineID() {
    return authoritativeEngineID.getValue();
  }

  public void setAuthoritativeEngineID(byte[] authoritativeEngineID) {
    if (authoritativeEngineID == null) {
      throw new NullPointerException("Authoritative engine ID must not be null");
    }
    this.authoritativeEngineID.setValue(authoritativeEngineID);
  }
  public void setAuthoritativeEngineBoots(int authoritativeEngineBoots) {
    this.authoritativeEngineBoots.setValue(authoritativeEngineBoots);
  }
  public int getAuthoritativeEngineBoots() {
    return authoritativeEngineBoots.getValue();
  }
  public void setAuthoritativeEngineTime(int authoritativeEngineTime) {
    this.authoritativeEngineTime.setValue(authoritativeEngineTime);
  }
  public int getAuthoritativeEngineTime() {
    return authoritativeEngineTime.getValue();
  }
  public void setUserName(OctetString userName) {
    this.userName = userName;
  }
  public OctetString getUserName() {
    return userName;
  }
  public void setAuthenticationProtocol(AuthenticationProtocol authenticationProtocol) {
    this.authenticationProtocol = authenticationProtocol;
  }
  public AuthenticationProtocol getAuthenticationProtocol() {
    return authenticationProtocol;
  }
  public void setPrivacyProtocol(PrivacyProtocol privacyProtocol) {
    this.privacyProtocol = privacyProtocol;
  }
  public PrivacyProtocol getPrivacyProtocol() {
    return privacyProtocol;
  }

  public int getBERLength() {
    int length = getBERPayloadLength();
    return length + BER.getBERLengthOfLength(length) + 1;
  }

  public int getBERPayloadLength() {
    int length = getBERUsmPayloadLength();
    length += BER.getBERLengthOfLength(length)+1;
    return length;
  }



  public void decodeBER(BERInputStream inputStream) throws IOException {
    int pos = (int)inputStream.getPosition();
    this.decodedLength = pos;
    MutableByte mutableByte = new MutableByte();
    int octetLength = BER.decodeHeader(inputStream, mutableByte);
    long startPos = inputStream.getPosition();
    if (mutableByte.getValue() != BER.OCTETSTRING) {
      String txt =
          "BER decoding error: Expected BER OCTETSTRING but found: " +
          mutableByte.getValue();
      logger.warn(txt);
      throw new IOException(txt);
    }
    int length = BER.decodeHeader(inputStream, mutableByte);
    long startPosSeq = inputStream.getPosition();
    if (mutableByte.getValue() != BER.SEQUENCE) {
      String txt =
          "BER decoding error: Expected BER SEQUENCE but found: " +
          mutableByte.getValue();
      logger.warn(txt);
      throw new IOException(txt);
    }
    authoritativeEngineID.decodeBER(inputStream);
    authoritativeEngineBoots.decodeBER(inputStream);
    authoritativeEngineTime.decodeBER(inputStream);
    userName.decodeBER(inputStream);
    this.authParametersPosition = (int)(inputStream.getPosition() - pos);
    pos = (int)inputStream.getPosition();
    authenticationParameters.decodeBER(inputStream);
    this.authParametersPosition +=
        (inputStream.getPosition() - pos) -
        authenticationParameters.getBERPayloadLength();

    privacyParameters.decodeBER(inputStream);
    this.decodedLength = (int) (inputStream.getPosition() - decodedLength);
    if (BER.isCheckSequenceLength()) {
      // check length
      BER.checkSequenceLength(length,
                              (int) (inputStream.getPosition() - startPosSeq),
                              this);
      BER.checkSequenceLength(octetLength,
                              (int) (inputStream.getPosition() - startPos),
                              this);
    }
  }

  private int getBEREncodedAuthParamsPosition() {
    int length = getBERLength()-
        (authenticationParameters.getBERPayloadLength() +
         privacyParameters.getBERLength());
    return length;
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeHeader(outputStream, BER.OCTETSTRING, getBERPayloadLength());
    BER.encodeHeader(outputStream, BER.SEQUENCE, getBERUsmPayloadLength());
    authoritativeEngineID.encodeBER(outputStream);
    authoritativeEngineBoots.encodeBER(outputStream);
    authoritativeEngineTime.encodeBER(outputStream);
    userName.encodeBER(outputStream);
    authenticationParameters.encodeBER(outputStream);
    privacyParameters.encodeBER(outputStream);
  }

  /**
   * getBERUsmPayloadLength
   *
   * @return int
   */
  public int getBERUsmPayloadLength() {
    int length = authoritativeEngineID.getBERLength();
    length += authoritativeEngineBoots.getBERLength();
    length += authoritativeEngineTime.getBERLength();
    length += userName.getBERLength();
    length += authenticationParameters.getBERLength();
    length += privacyParameters.getBERLength();
    return length;
  }

  public int getBERMaxLength(int securityLevel) {
    SecurityProtocols secProtocol = SecurityProtocols.getInstance();
    int securityParamsLength = 2;
    if (securityLevel > SecurityLevel.NOAUTH_NOPRIV) {
      securityParamsLength = secProtocol.getMaxAuthDigestLength() +
          BER.getBERLengthOfLength(secProtocol.getMaxAuthDigestLength()) + 1;

      if (securityLevel == SecurityLevel.AUTH_PRIV) {
        securityParamsLength += secProtocol.getMaxPrivDecryptParamsLength() +
            BER.getBERLengthOfLength(secProtocol.getMaxPrivDecryptParamsLength())
            + 1;
      }
    }
    return MAX_BER_LENGTH_WITHOU_SEC_PARAMS + securityParamsLength +
        BER.getBERLengthOfLength(MAX_BER_LENGTH_WITHOU_SEC_PARAMS +
                                 securityParamsLength) + 1;
  }

  public byte[] getAuthenticationKey() {
    return authenticationKey;
  }
  public void setAuthenticationKey(byte[] authenticationKey) {
    this.authenticationKey = authenticationKey;
  }
  public byte[] getPrivacyKey() {
    return privacyKey;
  }
  public void setPrivacyKey(byte[] privacyKey) {
    this.privacyKey = privacyKey;
  }
  public OctetString getPrivacyParameters() {
    return privacyParameters;
  }
  public void setPrivacyParameters(OctetString privacyParameters) {
    this.privacyParameters = privacyParameters;
  }
  public OctetString getAuthenticationParameters() {
    return authenticationParameters;
  }
  public void setAuthenticationParameters(OctetString authenticationParameters) {
    this.authenticationParameters = authenticationParameters;
  }
  public int getSecurityParametersPosition() {
    return securityParametersPosition;
  }
  public void setSecurityParametersPosition(int securityParametersPosition) {
    this.securityParametersPosition = securityParametersPosition;
  }
  public int getAuthParametersPosition() {
    if (authParametersPosition >= 0) {
      return authParametersPosition;
    }
    else {
      return getBEREncodedAuthParamsPosition();
    }
  }

  /**
   * getScopedPduPosition
   *
   * @return int
   */
  public int getScopedPduPosition() {
    if (decodedLength >= 0) {
      return decodedLength + getSecurityParametersPosition();
    }
    else {
      return getSecurityParametersPosition()+getBERLength();
    }
  }

}

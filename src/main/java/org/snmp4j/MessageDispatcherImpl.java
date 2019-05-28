/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j;

import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.asn1.BEROutputStream;
import org.snmp4j.event.AuthenticationFailureEvent;
import org.snmp4j.event.AuthenticationFailureListener;
import org.snmp4j.event.CounterEvent;
import org.snmp4j.event.CounterListener;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.*;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.UnsupportedAddressClassException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * The <code>MessageDispatcherImpl</code> decodes and dispatches incoming
 * messages using {@link org.snmp4j.mp.MessageProcessingModel} instances and encodes
 * and sends outgoing messages using an appropriate {@link org.snmp4j.TransportMapping}
 * instances.
 * <p>
 * The method {@link #processMessage} will be called from a
 * <code>TransportMapping</code> whereas the method {@link #sendPdu} will be
 * called by the application.
 *
 * @see org.snmp4j.Snmp
 * @see org.snmp4j.TransportMapping
 * @see org.snmp4j.mp.MessageProcessingModel
 * @see org.snmp4j.mp.MPv1
 * @see org.snmp4j.mp.MPv2c
 * @see org.snmp4j.mp.MPv3
 *
 * @author Frank Fock
 * @version 1.8
 */
public class MessageDispatcherImpl implements MessageDispatcher {

  private static final LogAdapter logger =
      LogFactory.getLogger(MessageDispatcherImpl.class);

  private Vector mpm = new Vector(3);
  private Map transportMappings = new Hashtable(5);

  private int nextTransactionID = new Random().nextInt(Integer.MAX_VALUE-2)+1;
  transient private Vector commandResponderListeners;
  private transient Vector counterListeners;
  private transient Vector authenticationFailureListeners;

  private boolean checkOutgoingMsg = true;

  /**
   * Default constructor creates a message dispatcher without any associated
   * message processing models.
   */
  public MessageDispatcherImpl() {
  }

  /**
   * Adds a message processing model to this message dispatcher. If a message
   * processing model with the same ID as the supplied one already exists it
   * will not be changed. Please call {@link #removeMessageProcessingModel}
   * before to replace a message processing model.
   * @param model
   *    a MessageProcessingModel instance.
   */
  public synchronized void addMessageProcessingModel(MessageProcessingModel model) {
    while (mpm.size() <= model.getID()) {
      mpm.add(null);
    }
    if (mpm.get(model.getID()) == null) {
      mpm.set(model.getID(), model);
    }
  }

  /**
   * Removes a message processing model from this message dispatcher.
   * @param model
   *    a previously added MessageProcessingModel instance.
   */
  public synchronized void removeMessageProcessingModel(MessageProcessingModel model) {
    mpm.set(model.getID(), null);
  }

  /**
   * Adds a transport mapping. When an outgoing message is processed where
   * no specific transport mapping has been specified, then the
   * message dispatcher will use the transport mapping
   * that supports the supplied address class of the target.
   * @param transport
   *    a TransportMapping instance. If there is already another transport
   *    mapping registered that supports the same address class, then
   *    <code>transport</code> will be registered but not used for messages
   *    without specific transport mapping.
   */
  public synchronized void addTransportMapping(TransportMapping transport) {
    List transports =
        (List) transportMappings.get(transport.getSupportedAddressClass());
    if (transports == null) {
      transports = new LinkedList();
      transportMappings.put(transport.getSupportedAddressClass(), transports);
    }
    transports.add(transport);
  }

  /**
   * Removes a transport mapping.
   * @param transport
   *    a previously added TransportMapping instance.
   * @return
   *    the supplied TransportMapping if it has been successfully removed,
   *    <code>null</code>otherwise.
   */
  public TransportMapping removeTransportMapping(TransportMapping transport) {
    List tm =
        (List) transportMappings.remove(transport.getSupportedAddressClass());
    if (tm != null) {
      if (tm.remove(transport)) {
        return transport;
      }
    }
    return null;
  }

  /**
   * Gets a collection of all registered transport mappings.
   * @return
   *    a Collection instance.
   */
  public Collection getTransportMappings() {
    ArrayList l = new ArrayList(transportMappings.size());
    synchronized (transportMappings) {
      for (Iterator it = transportMappings.values().iterator(); it.hasNext();) {
        List tm = (List) it.next();
        l.addAll(tm);
      }
    }
    return l;
  }

  public synchronized int getNextRequestID() {
    int nextID = nextTransactionID++;
    if (nextID <= 0) {
      nextID = 1;
      nextTransactionID = 2;
    }
    return nextID;
  }

  protected PduHandle createPduHandle() {
    return new PduHandle(getNextRequestID());
  }

  /**
   * Sends a message using the <code>TransportMapping</code> that has been
   * assigned for the supplied address type.
   *
   * @param transport
   *    the transport mapping to be used to send the message.
   * @param destAddress
   *    the transport address where to send the message. The
   *    <code>destAddress</code> must be compatible with the supplied
   *    <code>transport</code>.
   * @param message
   *    the SNMP message to send.
   * @throws java.io.IOException
   *    if an I/O error occured while sending the message or if there is
   *    no transport mapping defined for the supplied address type.
   */
  protected void sendMessage(TransportMapping transport,
                             Address destAddress, byte[] message)
      throws IOException
  {
    if (destAddress instanceof GenericAddress) {
      destAddress = ((GenericAddress)destAddress).getAddress();
    }
    if (transport != null) {
      transport.sendMessage(destAddress, message);
    }
    else {
      String txt = "No transport mapping for address class: "+
                   destAddress.getClass().getName()+"="+destAddress;
      logger.error(txt);
      throw new IOException(txt);
    }
  }

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
  public TransportMapping getTransport(Address destAddress) {
    Class addressClass = destAddress.getClass();
    List l = (List) transportMappings.get(addressClass);
    if ((l != null) && (l.size() > 0)) {
      TransportMapping transport = (TransportMapping)l.get(0);
      return transport;
    }
    return null;
  }

  /**
   * Actually decodes and dispatches an incoming SNMP message using the supplied
   * message processing model.
   *
   * @param sourceTransport
   *   a <code>TransportMapping</code> that matches the incomingAddress type.
   * @param mp
   *   a <code>MessageProcessingModel</code> to process the message.
   * @param incomingAddress
   *   the <code>Address</code> from the entity that sent this message.
   * @param wholeMessage
   *   the <code>BERInputStream</code> containing the SNMP message.
   * @throws java.io.IOException
   *   if the message cannot be decoded.
   */
  protected void dispatchMessage(TransportMapping sourceTransport,
                                 MessageProcessingModel mp,
                                 Address incomingAddress,
                                 BERInputStream wholeMessage) throws IOException {
    MutablePDU pdu = new MutablePDU();
    Integer32 messageProcessingModel = new Integer32();
    Integer32 securityModel = new Integer32();
    OctetString securityName = new OctetString();
    Integer32 securityLevel = new Integer32();

    PduHandle handle = createPduHandle();

    Integer32 maxSizeRespPDU =
        new Integer32(sourceTransport.getMaxInboundMessageSize());
    StatusInformation statusInfo = new StatusInformation();
    MutableStateReference mutableStateReference = new MutableStateReference();
    // add the transport mapping to the state reference to allow the MP to
    // return REPORTs on the same interface/port the message had been received.
    StateReference stateReference = new StateReference();
    stateReference.setTransportMapping(sourceTransport);
    stateReference.setAddress(incomingAddress);
    mutableStateReference.setStateReference(stateReference);

    int status = mp.prepareDataElements(this, incomingAddress, wholeMessage,
                                        messageProcessingModel, securityModel,
                                        securityName, securityLevel, pdu,
                                        handle, maxSizeRespPDU, statusInfo,
                                        mutableStateReference);
    if (mutableStateReference.getStateReference() != null) {
      // make sure transport mapping is set
      mutableStateReference.
          getStateReference().setTransportMapping(sourceTransport);
    }
    if (status == SnmpConstants.SNMP_ERROR_SUCCESS) {
      // dispatch it
      CommandResponderEvent e =
          new CommandResponderEvent(this,
                                    sourceTransport,
                                    incomingAddress,
                                    messageProcessingModel.getValue(),
                                    securityModel.getValue(),
                                    securityName.getValue(),
                                    securityLevel.getValue(),
                                    handle,
                                    pdu.getPdu(),
                                    maxSizeRespPDU.getValue(),
                                    mutableStateReference.getStateReference());
      fireProcessPdu(e);
    }
    else {
      switch (status) {
        case SnmpConstants.SNMP_MP_UNSUPPORTED_SECURITY_MODEL:
        case SnmpConstants.SNMP_MP_WRONG_USER_NAME:
        case SnmpConstants.SNMP_MP_USM_ERROR: {
          AuthenticationFailureEvent event =
              new AuthenticationFailureEvent(this, incomingAddress,
                                             sourceTransport, status,
                                             wholeMessage);
          fireAuthenticationFailure(event);
          break;
        }
      }
      logger.warn(statusInfo.toString());
    }
  }

  public void processMessage(TransportMapping sourceTransport,
                             Address incomingAddress,
                             ByteBuffer wholeMessage) {
    processMessage(sourceTransport, incomingAddress,
                   new BERInputStream(wholeMessage));
  }

  public void processMessage(TransportMapping sourceTransport,
                             Address incomingAddress,
                             BERInputStream wholeMessage) {
    fireIncrementCounter(new CounterEvent(this, SnmpConstants.snmpInPkts));
    if (!wholeMessage.markSupported()) {
      String txt = "Message stream must support marks";
      logger.error(txt);
      throw new IllegalArgumentException(txt);
    }
    try {
      wholeMessage.mark(16);
      BER.MutableByte type = new BER.MutableByte();
      // decode header but do not check length here, because we do only decode
      // the first 16 bytes.
      BER.decodeHeader(wholeMessage, type, false);
      if (type.getValue() != BER.SEQUENCE) {
        logger.error("ASN.1 parse error (message is not a sequence)");
        CounterEvent event = new CounterEvent(this,
                                              SnmpConstants.snmpInASNParseErrs);
        fireIncrementCounter(event);
      }
      Integer32 version = new Integer32();
      version.decodeBER(wholeMessage);
      MessageProcessingModel mp = getMessageProcessingModel(version.getValue());
      if (mp == null) {
        logger.warn("SNMP version "+version+" is not supported");
        CounterEvent event = new CounterEvent(this,
                                              SnmpConstants.snmpInBadVersions);
        fireIncrementCounter(event);
      }
      else {
        // reset it
        wholeMessage.reset();
        // dispatch it
        dispatchMessage(sourceTransport, mp, incomingAddress, wholeMessage);
      }
    }
    catch (IOException iox) {
      logger.warn(iox);
      CounterEvent event =
          new CounterEvent(this, SnmpConstants.snmpInvalidMsgs);
      fireIncrementCounter(event);
    }
    catch (Exception ex) {
      logger.error(ex);
      if (logger.isDebugEnabled()) {
        ex.printStackTrace();
      }
      if (SNMP4JSettings.isForwardRuntimeExceptions()) {
        throw new RuntimeException(ex);
      }
    }
    catch (OutOfMemoryError oex) {
      logger.error(oex);
      if (SNMP4JSettings.isForwardRuntimeExceptions()) {
        throw oex;
      }
    }
  }

  public PduHandle sendPdu(Address transportAddress,
                           int messageProcessingModel,
                           int securityModel,
                           byte[] securityName,
                           int securityLevel,
                           PDU pdu,
                           boolean expectResponse) throws MessageException {
    return sendPdu(null, transportAddress, messageProcessingModel,
                   securityModel, securityName, securityLevel,
                   pdu, expectResponse);
  }

  public PduHandle sendPdu(TransportMapping transport,
                           Address transportAddress,
                           int messageProcessingModel,
                           int securityModel,
                           byte[] securityName,
                           int securityLevel,
                           PDU pdu,
                           boolean expectResponse,
                           PduHandleCallback pduHandleCallback)
      throws MessageException
  {
    try {
      MessageProcessingModel mp =
          getMessageProcessingModel(messageProcessingModel);
      if (mp == null) {
        throw new MessageException("Unsupported message processing model: "
                                   + messageProcessingModel);
      }
      if (!mp.isProtocolVersionSupported(messageProcessingModel)) {
        throw new MessageException("SNMP version "+messageProcessingModel+
                                   " is not supported "+
                                   "by message processing model "+
                                   messageProcessingModel);
      }
      if (transport == null) {
        transport = getTransport(transportAddress);
      }
      if (transport == null) {
        throw new UnsupportedAddressClassException(
            "Unsupported address class (transport mapping): "+
            transportAddress.getClass().getName(),
            transportAddress.getClass());
      }
      else if (pdu.isConfirmedPdu()) {
        checkListening4ConfirmedPDU(pdu, transportAddress, transport);
      }

      // check PDU type
      checkOutgoingMsg(transportAddress, messageProcessingModel, pdu);

      // if request ID is == 0 then create one here, otherwise use the request
      // ID because it may be a resent request.
      PduHandle pduHandle;
      Integer32 reqID = pdu.getRequestID();
      if ((reqID == null) || (reqID.getValue() == 0) &&
          (pdu.getType() != PDU.RESPONSE)) {
        pduHandle = createPduHandle();
      }
      else {
        pduHandle = new PduHandle(pdu.getRequestID().getValue());
      }

      // assing request ID
      pdu.setRequestID(new Integer32(pduHandle.getTransactionID()));

      // parameters to receive
      GenericAddress destAddress = new GenericAddress();

      BEROutputStream outgoingMessage = new BEROutputStream();
      int status = mp.prepareOutgoingMessage(transportAddress,
                                             transport.getMaxInboundMessageSize(),
                                             messageProcessingModel,
                                             securityModel,
                                             securityName,
                                             securityLevel,
                                             pdu,
                                             expectResponse,
                                             pduHandle,
                                             destAddress,
                                             outgoingMessage);

      if (status == SnmpConstants.SNMP_ERROR_SUCCESS) {
        // inform callback about PDU new handle
        if (pduHandleCallback != null) {
          pduHandleCallback.pduHandleAssigned(pduHandle, pdu);
        }
        byte[] messageBytes = outgoingMessage.getBuffer().array();
        sendMessage(transport, transportAddress, messageBytes);
      }
      else {
        throw new MessageException("Message processing model "+
                                   mp.getID()+" returned error: "+
                                   SnmpConstants.mpErrorMessage(status));
      }
      return pduHandle;
    }
    catch (IndexOutOfBoundsException iobex) {
      throw new MessageException("Unsupported message processing model: "
                                 + messageProcessingModel);
    }
    catch (MessageException mex) {
      if (logger.isDebugEnabled()) {
        mex.printStackTrace();
      }
      throw mex;
    }
    catch (IOException iox) {
      if (logger.isDebugEnabled()) {
        iox.printStackTrace();
      }
      throw new MessageException(iox.getMessage());
    }
  }

  private static void checkListening4ConfirmedPDU(PDU pdu, Address target,
                                                  TransportMapping transport) {
    if ((transport != null) && (!transport.isListening())) {
      logger.warn("Sending confirmed PDU "+pdu+" to target "+target+
                  " although transport mapping "+transport+
                  " is not listening for a response");
    }
  }

  /**
   * Checks outgoing messages for consistency between PDU and target used.
   * @param transportAddress
   *    the target address.
   * @param messageProcessingModel
   *    the message processing model to be used.
   * @param pdu
   *    the PDU to be sent.
   * @throws org.snmp4j.MessageException
   *    if unrecoverable inconsistencies have been detected.
   */
  protected void checkOutgoingMsg(Address transportAddress,
                                  int messageProcessingModel, PDU pdu)
      throws MessageException
  {
    if (checkOutgoingMsg) {
      if (messageProcessingModel == MessageProcessingModel.MPv1) {
        if (pdu.getType() == PDU.GETBULK) {
          logger.warn("Converting GETBULK PDU to GETNEXT for SNMPv1 target: "+
                      transportAddress);
          pdu.setType(PDU.GETNEXT);
          if (!(pdu instanceof PDUv1)) {
            pdu.setMaxRepetitions(0);
          }
        }
      }
    }
  }

  public int returnResponsePdu(int messageProcessingModel,
                               int securityModel,
                               byte[] securityName,
                               int securityLevel,
                               PDU pdu,
                               int maxSizeResponseScopedPDU,
                               StateReference stateReference,
                               StatusInformation statusInformation)
      throws MessageException
  {
    try {
      MessageProcessingModel mp =
          getMessageProcessingModel(messageProcessingModel);
      if (mp == null) {
        throw new MessageException("Unsupported message processing model: "
                                   + messageProcessingModel);
      }
      TransportMapping transport = stateReference.getTransportMapping();
      if (transport == null) {
        transport = getTransport(stateReference.getAddress());
      }
      if (transport == null) {
        throw new MessageException("Unsupported address class (transport mapping): "+
                                   stateReference.getAddress().getClass().getName());
      }
      BEROutputStream outgoingMessage = new BEROutputStream();
      int status = mp.prepareResponseMessage(messageProcessingModel,
                                             transport.getMaxInboundMessageSize(),
                                             securityModel,
                                             securityName, securityLevel, pdu,
                                             maxSizeResponseScopedPDU,
                                             stateReference,
                                             statusInformation,
                                             outgoingMessage);
      if (status == SnmpConstants.SNMP_MP_OK) {
        sendMessage(transport,
                    stateReference.getAddress(),
                    outgoingMessage.getBuffer().array());
      }
      return status;
    }
    catch (ArrayIndexOutOfBoundsException aex) {
      throw new MessageException("Unsupported message processing model: "
                                 + messageProcessingModel);
    }
    catch (IOException iox) {
      throw new MessageException(iox.getMessage());
    }
  }

  public void releaseStateReference(int messageProcessingModel,
                                    PduHandle pduHandle) {
    MessageProcessingModel mp = getMessageProcessingModel(messageProcessingModel);
    if (mp == null) {
      throw new IllegalArgumentException("Unsupported message processing model: "+
                                         messageProcessingModel);
    }
    mp.releaseStateReference(pduHandle);
  }

  public synchronized void removeCommandResponder(CommandResponder l) {
    if (commandResponderListeners != null && commandResponderListeners.contains(l)) {
      Vector v = (Vector) commandResponderListeners.clone();
      v.removeElement(l);
      commandResponderListeners = v;
    }
  }

  public synchronized void addCommandResponder(CommandResponder l) {
    Vector v = (commandResponderListeners == null) ?
        new Vector(2) : (Vector) commandResponderListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      commandResponderListeners = v;
    }
  }

  /**
   * Fires a <code>CommandResponderEvent</code>. Listeners are called
   * in order of their registration  until a listener has processed the
   * PDU successfully.
   * @param e
   *   a <code>CommandResponderEvent</code> event.
   */
  protected void fireProcessPdu(CommandResponderEvent e) {
    if (commandResponderListeners != null) {
      Vector listeners = commandResponderListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((CommandResponder) listeners.elementAt(i)).processPdu(e);
        // if event is marked as processed the event is not forwarded to
        // remaining listeners
        if (e.isProcessed()) {
          return;
        }
      }
    }
  }

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
  public MessageProcessingModel getMessageProcessingModel(int messageProcessingModel) {
    try {
      return (MessageProcessingModel) mpm.get(messageProcessingModel);
    }
    catch (IndexOutOfBoundsException iobex) {
      return null;
    }
  }

  /**
   * Removes a <code>CounterListener</code>.
   * @param counterListener
   *    a previously added <code>CounterListener</code>.
   */
  public synchronized void removeCounterListener(CounterListener counterListener) {
    if (counterListeners != null && counterListeners.contains(counterListener)) {
      Vector v = (Vector) counterListeners.clone();
      v.removeElement(counterListener);
      counterListeners = v;
    }
  }

  /**
   * Adds a <code>CounterListener</code>.
   * @param counterListener
   *    a <code>CounterListener</code> that will be informed when a counter
   *    needs to incremented.
   */
  public synchronized void addCounterListener(CounterListener counterListener) {
    Vector v = (counterListeners == null) ?
        new Vector(2) : (Vector) counterListeners.clone();
    if (!v.contains(counterListener)) {
      v.addElement(counterListener);
      counterListeners = v;
    }
  }

  /**
   * Fires a counter incrementation event.
   * @param event
   *    the <code>CounterEvent</code> containing the OID of the counter
   *    that needs to be incremented.
   */
  protected void fireIncrementCounter(CounterEvent event) {
    if (counterListeners != null) {
      Vector listeners = counterListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((CounterListener) listeners.elementAt(i)).incrementCounter(event);
      }
    }
  }

  /**
   * Enables or disables the consistency checks for outgoing messages.
   * If the checks are enabled, then GETBULK messages sent to SNMPv1
   * targets will be converted to GETNEXT messages.
   * <p>
   * In general, if an automatically conversion is not possible, an
   * error is thrown when such a message is to be sent.
   * <p>
   * The default is consistency checks enabled.
   *
   * @param checkOutgoingMsg
   *    if <code>true</code> outgoing messages are checked for consistency.
   *    Currently, only the PDU type will be checked against the used SNMP
   *    version. If <code>false</code>, no checks will be performed.
   */
  public void setCheckOutgoingMsg(boolean checkOutgoingMsg) {
    this.checkOutgoingMsg = checkOutgoingMsg;
  }

  /**
   * Returns whether consistency checks for outgoing messages are activated.
   * @return
   *    if <code>true</code> outgoing messages are checked for consistency.
   *    If <code>false</code>, no checks are performed.
   */
  public boolean isCheckOutgoingMsg() {
    return checkOutgoingMsg;
  }

  /**
   * Adds a listener for authentication failure events caused by unauthenticated
   * incoming messages.
   * @param l
   *    the <code>AuthenticationFailureListener</code> to add.
   * @since 1.5
   */
  public synchronized void addAuthenticationFailureListener(
      AuthenticationFailureListener l) {
      Vector v = (authenticationFailureListeners == null) ?
          new Vector(2) : (Vector) authenticationFailureListeners.clone();
      if (!v.contains(l)) {
        v.addElement(l);
        authenticationFailureListeners = v;
      }
  }

  /**
   * Removes an <code>AuthenticationFailureListener</code>.
   * @param l
   *   the <code>AuthenticationFailureListener</code> to remove.
   */
  public synchronized void removeAuthenticationFailureListener(
      AuthenticationFailureListener l) {
      if (authenticationFailureListeners != null &&
          authenticationFailureListeners.contains(l)) {
        Vector v = (Vector) authenticationFailureListeners.clone();
        v.removeElement(l);
        authenticationFailureListeners = v;
      }
  }

  /**
   * Fires an <code>AuthenticationFailureEvent</code> to all registered
   * listeners.
   * @param event
   *    the event to fire.
   */
  protected void fireAuthenticationFailure(AuthenticationFailureEvent event) {
    if (authenticationFailureListeners != null) {
      Vector listeners = authenticationFailureListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((AuthenticationFailureListener) listeners.elementAt(i)).
            authenticationFailure(event);
      }
    }
  }

  public PduHandle sendPdu(TransportMapping transportMapping,
                           Address transportAddress,
                           int messageProcessingModel,
                           int securityModel, byte[] securityName,
                           int securityLevel, PDU pdu,
                           boolean expectResponse) throws MessageException {
    return sendPdu(transportMapping, transportAddress, messageProcessingModel,
                   securityModel,
                   securityName, securityLevel, pdu, expectResponse, null);
  }

}


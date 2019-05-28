/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.util;

import org.snmp4j.*;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.mp.*;
import org.snmp4j.smi.Address;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * The <code>MultiThreadedMessageDispatcher</code> class is a decorator
 * for any <code>MessageDispatcher</code> instances that processes incoming
 * message with a supplied <code>ThreadPool</code>. The processing is thus
 * parallelized on up to the size of the supplied thread pool threads.
 * <p>
 * In contrast to a {@link MessageDispatcherImpl} a
 * <code>MultiThreadedMessageDispatcher</code> copies the incoming
 * <code>ByteBuffer</code> for {@link #processMessage(org.snmp4j.TransportMapping
 * sourceTransport, org.snmp4j.smi.Address incomingAddress, java.nio.ByteBuffer wholeMessage)} to allow
 * parallel processing of the buffer.
 *
 * @author Frank Fock
 * @version 1.10
 * @since 1.0.2
 */
public class MultiThreadedMessageDispatcher implements MessageDispatcher {

  private MessageDispatcher dispatcher;
  private WorkerPool threadPool;

  /**
   * Creates a multi thread message dispatcher using the provided
   * <code>ThreadPool</code> to concurrently process incoming messages
   * that are forwarded to the supplied decorated
   * <code>MessageDispatcher</code>.
   *
   * @param workerPool
   *    a <code>WorkerPool</code> instance (that can be shared). <em>The worker
   *    pool has to be stopped externally.</em>
   * @param decoratedDispatcher
   *    the decorated <code>MessageDispatcher</code> that must be
   *    multi-threading safe.
   */
  public MultiThreadedMessageDispatcher(WorkerPool workerPool,
                                        MessageDispatcher decoratedDispatcher) {
    this.threadPool = workerPool;
    this.dispatcher = decoratedDispatcher;
  }

  public int getNextRequestID() {
    return dispatcher.getNextRequestID();
  }

  public void addMessageProcessingModel(MessageProcessingModel model) {
    dispatcher.addMessageProcessingModel(model);
  }

  public void removeMessageProcessingModel(MessageProcessingModel model) {
    dispatcher.removeMessageProcessingModel(model);
  }

  public MessageProcessingModel getMessageProcessingModel(int messageProcessingModel) {
    return dispatcher.getMessageProcessingModel(messageProcessingModel);
  }

  public void addTransportMapping(TransportMapping transport) {
    dispatcher.addTransportMapping(transport);
  }

  public TransportMapping removeTransportMapping(TransportMapping transport) {
    return dispatcher.removeTransportMapping(transport);
  }

  public Collection getTransportMappings() {
    return dispatcher.getTransportMappings();
  }

  public void addCommandResponder(CommandResponder listener) {
    dispatcher.addCommandResponder(listener);
  }

  public void removeCommandResponder(CommandResponder listener) {
    dispatcher.removeCommandResponder(listener);
  }

  public PduHandle sendPdu(Address transportAddress,
                           int messageProcessingModel,
                           int securityModel,
                           byte[] securityName,
                           int securityLevel,
                           PDU pdu,
                           boolean expectResponse) throws MessageException {
    return dispatcher.sendPdu(transportAddress, messageProcessingModel,
                              securityModel, securityName, securityLevel,
                              pdu, expectResponse);
  }

  public PduHandle sendPdu(TransportMapping transportMapping,
                           Address transportAddress,
                           int messageProcessingModel,
                           int securityModel,
                           byte[] securityName,
                           int securityLevel,
                           PDU pdu,
                           boolean expectResponse) throws MessageException {
    return dispatcher.sendPdu(transportMapping, transportAddress,
                              messageProcessingModel,
                              securityModel, securityName,
                              securityLevel, pdu, expectResponse);
  }

  public PduHandle sendPdu(TransportMapping transportMapping,
                           Address transportAddress,
                           int messageProcessingModel,
                           int securityModel, byte[] securityName,
                           int securityLevel, PDU pdu, boolean expectResponse,
                           PduHandleCallback callback) throws MessageException {
    return dispatcher.sendPdu(transportMapping, transportAddress,
                              messageProcessingModel,
                              securityModel, securityName,
                              securityLevel, pdu, expectResponse, callback);
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
    return dispatcher.returnResponsePdu(messageProcessingModel,
                                        securityModel, securityName,
                                        securityLevel, pdu,
                                        maxSizeResponseScopedPDU,
                                        stateReference,
                                        statusInformation);
  }

  public void processMessage(TransportMapping sourceTransport,
                             Address incomingAddress,
                             BERInputStream wholeMessage) {
    // OK, here wo do all that what this class is all about!
    MessageTask task = new MessageTask(sourceTransport,
                                       incomingAddress,
                                       wholeMessage);
    threadPool.execute(task);
  }

  public void processMessage(TransportMapping sourceTransport,
                             Address incomingAddress, ByteBuffer wholeMessage) {
    processMessage(sourceTransport, incomingAddress,
                   new BERInputStream(wholeMessage.duplicate()));
  }

  public void releaseStateReference(int messageProcessingModel,
                                    PduHandle pduHandle) {
    dispatcher.releaseStateReference(messageProcessingModel, pduHandle);
  }

  public TransportMapping getTransport(Address destAddress) {
    return dispatcher.getTransport(destAddress);
  }

  class MessageTask implements WorkerTask {
    private TransportMapping sourceTransport;
    private Address incomingAddress;
    private BERInputStream wholeMessage;

    public MessageTask(TransportMapping sourceTransport,
                       Address incomingAddress,
                       BERInputStream wholeMessage) {
      this.sourceTransport = sourceTransport;
      this.incomingAddress = incomingAddress;
      this.wholeMessage = wholeMessage;
    }

    public void run() {
      dispatcher.processMessage(sourceTransport, incomingAddress, wholeMessage);
    }

    public void terminate() {
    }

    public void join() throws InterruptedException {
    }

    public void interrupt() {
    }

  }
}

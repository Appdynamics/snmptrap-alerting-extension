/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */


package org.snmp4j.test;

import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;
import java.net.UnknownHostException;

public class MultiThreadedTrapReceiver implements CommandResponder {

  // initialize Log4J logging
/*
  static {
    LogFactory.setLogFactory(new Log4jLogFactory());
    BER.setCheckSequenceLength(false);
  }
*/
  private MultiThreadedMessageDispatcher dispatcher;
  private Snmp snmp = null;
  private Address listenAddress;
  private ThreadPool threadPool;

  private int n = 0;
  private long start = -1;


  public MultiThreadedTrapReceiver() {
//    BasicConfigurator.configure();
  }

  private void init() throws UnknownHostException, IOException {
    threadPool = ThreadPool.create("Trap", 2);
    dispatcher =
        new MultiThreadedMessageDispatcher(threadPool,
                                           new MessageDispatcherImpl());
    listenAddress =
        GenericAddress.parse(System.getProperty("snmp4j.listenAddress",
                                                "udp:0.0.0.0/162"));
    TransportMapping transport;
    if (listenAddress instanceof UdpAddress) {
      transport = new DefaultUdpTransportMapping((UdpAddress)listenAddress);
    }
    else {
      transport = new DefaultTcpTransportMapping((TcpAddress)listenAddress);
    }
    snmp = new Snmp(dispatcher, transport);
    snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
    snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
    snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
    USM usm = new USM(SecurityProtocols.getInstance(),
                      new OctetString(MPv3.createLocalEngineID()), 0);
    SecurityModels.getInstance().addSecurityModel(usm);
    snmp.listen();
  }

  public void run() {
    try {
      init();
      snmp.addCommandResponder(this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    MultiThreadedTrapReceiver multithreadedtrapreceiver = new
        MultiThreadedTrapReceiver();
    multithreadedtrapreceiver.run();
  }

  public void processPdu(CommandResponderEvent event) {
    if (start < 0) {
      start = System.currentTimeMillis()-1;
    }
//    System.out.println(event.toString());
    n++;
    if ((n % 100 == 1)) {
      System.out.println("Processed " +
                         (n / (double)(System.currentTimeMillis() - start)) * 1000 +
                         "/s, total="+n);
    }
  }
}

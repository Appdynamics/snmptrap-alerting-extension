/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */



package org.snmp4j.smi;

import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;

import java.net.InetAddress;

/**
 * The <code>TcpAddress</code> represents TCP/IP transport addresses.
 * @author Frank Fock
 * @version 1.0
 */

public class TcpAddress extends TransportIpAddress {

  static final long serialVersionUID = 1165319744164017388L;

  private static final LogAdapter logger = LogFactory.getLogger(TcpAddress.class);

  public TcpAddress() {
    super();
  }

  public TcpAddress(InetAddress inetAddress, int port) {
    setInetAddress(inetAddress);
    setPort(port);
  }

  public TcpAddress(int port) {
    super();
    setPort(port);
  }

  public TcpAddress(String address) {
    if (!parseAddress(address)) {
      throw new IllegalArgumentException(address);
    }
  }

  public static Address parse(String address) {
    try {
      TcpAddress a = new TcpAddress();
      if (a.parseAddress(address)) {
        return a;
      }
    }
    catch (Exception ex) {
      logger.error(ex);
    }
    return null;
  }

  public boolean equals(Object o) {
    if (o instanceof TcpAddress) {
      return super.equals(o);
    }
    return false;
  }

}

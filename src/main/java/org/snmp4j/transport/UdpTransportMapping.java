/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */





package org.snmp4j.transport;

import org.snmp4j.smi.Address;
import org.snmp4j.smi.UdpAddress;

import java.io.IOException;

/**
 * The <code>UdpTransportMapping</code> is the abstract base class for
 * UDP transport mappings.
 * @author Frank Fock
 * @version 1.0
 */

public abstract class UdpTransportMapping extends AbstractTransportMapping {

  protected UdpAddress udpAddress;


  public UdpTransportMapping(UdpAddress udpAddress) {
    this.udpAddress = udpAddress;
  }

  public Class getSupportedAddressClass() {
    return UdpAddress.class;
  }

  /**
   * Returns the transport address that is used by this transport mapping for
   * sending and receiving messages.
   * @return
   *    the <code>Address</code> used by this transport mapping. The returned
   *    instance must not be modified!
   */
  public UdpAddress getAddress() {
    return udpAddress;
  }

  public Address getListenAddress() {
    return udpAddress;
  }

  public abstract void listen() throws IOException;

  public abstract void close() throws IOException;

  public abstract void sendMessage(Address address, byte[] message)
      throws IOException;

}

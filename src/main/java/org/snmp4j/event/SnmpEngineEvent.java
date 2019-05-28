/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.event;

import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;

import java.util.EventObject;

/**
 * The <code>SnmpEngineEvent</code> describes events generated on behalf of
 * the engine ID cache of the SNMPv3 message processing model (MPv3).
 *
 * @author Frank Fock
 * @version 1.6
 * @since 1.6
 */
public class SnmpEngineEvent extends EventObject {

  private static final long serialVersionUID = -7287039511083410591L;

  public static final int ADDED_ENGINE_ID = 1;
  public static final int REMOVED_ENGINE_ID = 2;

  private OctetString engineID;
  private Address engineAddress;
  private int type;

  public SnmpEngineEvent(Object source, int type,
                         OctetString engineID, Address engineAddress) {
    super(source);
    this.engineID = engineID;
    this.type = type;
    this.engineAddress = engineAddress;
  }

  /**
   * Returns the type of the engine event.
   * @return
   *    one of the integer constants defined by this class.
   */
  public int getType() {
    return type;
  }

  /**
   * Returns the engine ID associated with this event.
   * @return
   *    the engine ID.
   */
  public OctetString getEngineID() {
    return engineID;
  }

  /**
   * Returns the transport address of the engine.
   * @return
   *    the transport address.
   */
  public Address getEngineAddress() {
    return engineAddress;
  }

}

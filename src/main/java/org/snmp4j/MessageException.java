/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j;

import org.snmp4j.mp.StatusInformation;

import java.io.IOException;

/**
 * The <code>MessageException</code> represents information about an exception
 * occured during message processing. The associated
 * <code>StatusInformation</code> object provides (if present) detailed
 * information about the error that occured and the status of the processed
 * message.
 * @author Frank Fock
 * @version 1.0.1
 */
public class MessageException extends IOException {

  private static final long serialVersionUID = 7129156393920783825L;

  private StatusInformation statusInformation;

  public MessageException() {
  }

  /**
   * Creates a <code>MessageException</code> from a
   * <code>StatusInformation</code> object.
   * @param status
   *   a <code>StatusInformation</code> instance.
   */
  public MessageException(StatusInformation status) {
    super(""+status.getErrorIndication());
    setStatusInformation(status);
  }

  public MessageException(String message) {
    super(message);
  }

  public StatusInformation getStatusInformation() {
    return statusInformation;
  }

  public void setStatusInformation(StatusInformation statusInformation) {
    this.statusInformation = statusInformation;
  }
}


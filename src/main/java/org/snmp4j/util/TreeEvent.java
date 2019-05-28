/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.util;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

/**
 * The <code>TreeEvent</code> class reports events in a tree retrieval
 * operation.
 *
 * @author Frank Fock
 * @version 1.8
 * @since 1.8
 * @see org.snmp4j.util.TreeUtils
 */
public class TreeEvent extends RetrievalEvent {

  private static final long serialVersionUID = 5660517240029018420L;

  public TreeEvent(Object source, Object userObject, VariableBinding[] vbs) {
    super(source, userObject, vbs);
  }

  public TreeEvent(Object source, Object userObject, int status) {
    super(source, userObject, status);
  }

  public TreeEvent(Object source, Object userObject, PDU report) {
    super(source, userObject, report);
  }

  public TreeEvent(Object source, Object userObject, Exception exception) {
    super(source, userObject, exception);
  }

  /**
   * Gets the variable bindings retrieved in depth first order from the
   * (sub-)tree.
   *
   * @return VariableBinding[]
   *    a possibly empty or <code>null</code> array of
   *    <code>VariableBindings</code>.
   */
  public VariableBinding[] getVariableBindings() {
    return vbs;
  }

}

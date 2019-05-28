/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.util;

import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.util.Arrays;

/**
 * The <code>TableEvent</code> class reports events in a table retrieval
 * operation.
 *
 * @author Frank Fock
 * @version 1.8
 * @since 1.0.2
 * @see org.snmp4j.util.TableUtils
 */
public class TableEvent extends RetrievalEvent {

  private static final long serialVersionUID = 3340523737695933621L;

  private OID index;

  protected TableEvent(Object source, Object userObject) {
    super(source, userObject);
    this.userObject = userObject;
  }

  /**
   * Creates a table event with a status.
   * @param source
   *    the source of the event.
   * @param userObject
   *    the user object or <code>null</code>.
   * @param status
   *    one of the status constants defined for this object.
   */
  public TableEvent(Object source, Object userObject, int status) {
    this(source, userObject);
    this.status = status;
  }

  /**
   * Creates a table event with an exception.
   * @param source
   *    the source of the event.
   * @param userObject
   *    the user object or <code>null</code>.
   * @param exception
   *    an exception instance.
   */
  public TableEvent(Object source, Object userObject, Exception exception) {
    this(source, userObject);
    this.exception = exception;
    this.status = STATUS_EXCEPTION;
  }

  /**
   * Creates a table event with a report PDU.
   * @param source
   *    the source of the event.
   * @param userObject
   *    the user object or <code>null</code>.
   * @param report
   *    a PDU of type {@link org.snmp4j.PDU#REPORT}.
   */
  public TableEvent(Object source, Object userObject, PDU report) {
    this(source, userObject);
    this.reportPDU = report;
    this.status = STATUS_REPORT;
  }

  /**
   * Creates a table event with row data.
   *
   * @param source
   *    the source of the event.
   * @param userObject
   *    the user object or <code>null</code>.
   * @param index
   *    the index OID of the row.
   * @param cols
   *    an array of <code>VariableBinding</code> instances containing the
   *    elements of the row. The array may contain <code>null</code> elements
   *    which indicates that the agent does not return an instance for that
   *    column and row index. If an element is not <code>null</code>, then
   *    the <code>OID</code> of the variable binding contains the full instance
   *    <code>OID</code> of the variable.
   */
  public TableEvent(Object source, Object userObject,
                    OID index, VariableBinding[] cols) {
    super(source, userObject, cols);
    this.index = index;
  }

  /**
   * Gets the row index OID.
   * @return
   *    the row's index OID or <code>null</code> if {@link #isError()} returns
   *    <code>true</code>.
   */
  public OID getIndex() {
    return index;
  }

  /**
   * Gets the columnar objects of the row.
   * @return
   *    an array of <code>VariableBinding</code> instances containing the
   *    elements of the row. The array may contain <code>null</code> elements
   *    which indicates that the agent does not return an instance for that
   *    column and row index. If an element is not <code>null</code>, then
   *    the <code>OID</code> of the variable binding contains the full instance
   *    <code>OID</code> of the variable.<p>
   *    If {@link #isError()} returns <code>true</code>, <code>null</code>
   *    will be returned.
   */
  public VariableBinding[] getColumns() {
    return vbs;
  }

  public String toString() {
    return getClass().getName()+"[index="+index+",vbs="+
        ((vbs == null) ? "null" : ""+Arrays.asList(vbs))+
        ",status="+status+",exception="+
        exception+",report="+reportPDU+"]";
  }
}

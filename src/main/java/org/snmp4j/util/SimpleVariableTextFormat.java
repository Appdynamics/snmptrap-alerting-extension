/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.util;

import org.snmp4j.SNMP4JSettings;
import org.snmp4j.smi.*;

import java.text.ParseException;

/**
 * The <code>SimpleVariableTextFormat</code> implements a simple textual
 * representation for SNMP variables based on their type only.
 * No MIB information is used (can be used).
 *
 * @author Frank Fock
 * @version 1.10
 * @since 1.10
 */
public class SimpleVariableTextFormat implements VariableTextFormat {

  /**
   * Creates a simple variable text format.
   */
  public SimpleVariableTextFormat() {
  }

  /**
   * Returns a textual representation of the supplied variable against the
   * optionally supplied instance OID.
   *
   * @param instanceOID the instance OID <code>variable</code> is associated
   *   with. If <code>null</code> the formatting cannot take any MIB
   *   specification of the variable into account and has to format it based
   *   on its type only.
   * @param variable
   *    the variable to format.
   * @param withOID
   *    if <code>true</code> the <code>instanceOID</code> should be included
   *    in the textual representation to form a {@link org.snmp4j.smi.VariableBinding}
   *    representation.
   * @return the textual representation.
   */
  public String format(OID instanceOID, Variable variable, boolean withOID) {
    return (withOID) ?
        SNMP4JSettings.getOIDTextFormat().format(instanceOID.getValue())+
        " = "+variable
        : variable.toString();
  }

  /**
   * This operation is not supported by {@link org.snmp4j.util.SimpleVariableTextFormat}.
   *
   * @param smiSyntax the SMI syntax identifier identifying the target
   *   <code>Variable</code>.
   * @param text a textual representation of the variable.
   * @return the new <code>Variable</code> instance.
   * @throws java.text.ParseException if the variable cannot be parsed successfully.
   */
  public Variable parse(int smiSyntax, String text) throws ParseException {
    Variable v = AbstractVariable.createFromSyntax(smiSyntax);
    if (v instanceof AssignableFromString) {
      ((AssignableFromString)v).setValue(text);
    }
    return v;
  }

  /**
   * This operation is not supported by {@link org.snmp4j.util.SimpleVariableTextFormat}.
   *
   * @param classOrInstanceOID
   *    the instance OID <code>variable</code> is associated with. Must not
   *    be <code>null</code>.
   * @param text
   *    a textual representation of the variable.
   * @return
   *    the new <code>Variable</code> instance.
   * @throws java.text.ParseException
   *    if the variable cannot be parsed successfully.
   */
  public Variable parse(OID classOrInstanceOID, String text) throws
      ParseException {
    throw new UnsupportedOperationException();
  }

  public VariableBinding parseVariableBinding(String text) throws ParseException {
    int assignmentPos = text.indexOf(" = ");
    if (assignmentPos <= 0) {
      throw new ParseException("Could not locate assignment ' = ' string in '"+
          text, 0);
    }
    OID oid = new OID(SNMP4JSettings.getOIDTextFormat().
        parse(text.substring(0, assignmentPos)));
    Variable var = parse(oid, text.substring(assignmentPos+3));
    return new VariableBinding(oid, var);
  }
}

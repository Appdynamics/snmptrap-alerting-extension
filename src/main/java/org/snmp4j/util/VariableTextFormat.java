/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.util;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import java.text.ParseException;

/**
 * The <code>VariableTextFormat</code> provides a textual representation
 * of SNMP {@link org.snmp4j.smi.Variable}s, in dependence of their associated (instance) OID.
 *
 * @author Frank Fock
 * @version 1.10
 * @since 1.10
 */
public interface VariableTextFormat {

  /**
   * Returns a textual representation of the supplied variable against the
   * optionally supplied instance OID.
   *
   * @param instanceOID
   *    the instance OID <code>variable</code> is associated with.
   *    If <code>null</code> the formatting cannot take any MIB specification
   *    of the variable into account and has to format it based on its type
   *    only.
   * @param variable
   *    the variable to format.
   * @param withOID
   *    if <code>true</code> the <code>instanceOID</code> should be included
   *    in the textual representation to form a {@link org.snmp4j.smi.VariableBinding}
   *    representation.
   * @return
   *    the textual representation.
   */
  String format(OID instanceOID, Variable variable, boolean withOID);

  /**
   * Parses a textual representation of a variable binding.
   *
   * @param text
   *    a textual representation of the variable binding.
   * @return
   *    the new <code>VariableBinding</code> instance.
   * @throws java.text.ParseException
   *    if the variable binding cannot be parsed successfully.
   */
  VariableBinding parseVariableBinding(String text) throws ParseException;

  /**
   * Parses a textual representation of a variable against its associated
   * OBJECT-TYPE OID.
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
  Variable parse(OID classOrInstanceOID, String text) throws ParseException;

  /**
   * Parses a textual representation of a variable against a SMI type.
   * @param smiSyntax
   *    the SMI syntax identifier identifying the target <code>Variable</code>.
   * @param text
   *    a textual representation of the variable.
   * @return
   *    the new <code>Variable</code> instance.
   * @throws java.text.ParseException
   *    if the variable cannot be parsed successfully.
   */
  Variable parse(int smiSyntax, String text) throws ParseException;
}

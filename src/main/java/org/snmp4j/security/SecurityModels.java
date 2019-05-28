/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.security;

import org.snmp4j.smi.Integer32;

import java.util.Hashtable;
import java.util.Map;

/**
 * The <code>SecurityModels</code> class is a collection of all
 * supported security models of a SNMP entity.
 *
 * @author Jochen Katz & Frank Fock
 * @version 1.0a
 */
public class SecurityModels {

  private Map securityModels = new Hashtable(3);

  private static SecurityModels instance = null;

  protected SecurityModels() {
  }

  /**
   * Gets the security singleton instance.
   * @return
   *    the <code>SecurityModels</code> instance.
   */
  public synchronized static SecurityModels getInstance() {
    if (instance == null) {
      instance = new SecurityModels();
    }
    return instance;
  }

  /**
   * Gets the SecurityModels collection instance that contains the supplied
   * {@link org.snmp4j.security.SecurityModel}s.
   * @param models
   *    an array of {@link org.snmp4j.security.SecurityModel} instances.
   * @return
   *    a new instance of SecurityModels that contains the supplied models.
   * @since 1.10
   */
  public static final SecurityModels getCollection(SecurityModel[] models) {
    SecurityModels smc = new SecurityModels();
    for (int i=0; i<models.length; i++) {
      smc.addSecurityModel(models[i]);
    }
    return smc;
  }

  /**
   * Adds a security model to the central repository of security models.
   * @param model
   *    a <code>SecurityModel</code>. If a security model with the same ID
   *    already
   */
  public void addSecurityModel(SecurityModel model) {
    securityModels.put(new Integer32(model.getID()), model);
  }

  /**
   * Removes a security model from the central repository of security models.
   * @param id
   *    the <code>Integer32</code> ID of the security model to remove.
   * @return
   *    the removed <code>SecurityModel</code> or <code>null</code> if
   *    <code>id</code> is not registered.
   */
  public SecurityModel removeSecurityModel(Integer32 id) {
    return (SecurityModel)securityModels.remove(id);
  }

  /**
   * Returns a security model from the central repository of security models.
   * @param id
   *    the <code>Integer32</code> ID of the security model to return.
   * @return
   *    the with <code>id</code> associated <code>SecurityModel</code> or
   *    <code>null</code> if no such model is registered.
   */
  public SecurityModel getSecurityModel(Integer32 id) {
    return (SecurityModel)securityModels.get(id);
  }
}


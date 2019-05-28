/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.security;

import org.snmp4j.SNMP4JSettings;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * The <code>SecurityProtocols</code> class holds all authentication and
 * privacy protocols for a SNMP entity.
 * <p>
 * To register security protocols other than the default, set the system
 * property {@link #SECURITY_PROTOCOLS_PROPERTIES} to a customized version
 * of the <code>SecurityProtocols.properties</code> file. The path has to
 * be specified relatively to this class.
 *
 * @author Jochen Katz & Frank Fock
 * @version 1.9
 */
public class SecurityProtocols implements Serializable {

  private static final long serialVersionUID = 3800474900139635836L;

  private Hashtable authProtocols;
  private Hashtable privProtocols;

  public static final String SECURITY_PROTOCOLS_PROPERTIES =
      "org.snmp4j.securityProtocols";
  private static final String SECURITY_PROTOCOLS_PROPERTIES_DEFAULT =
      "SecurityProtocols.properties";
  private static final LogAdapter logger = LogFactory.getLogger(SecurityProtocols.class);

  private static SecurityProtocols instance = null;
  private int maxAuthDigestLength = 0;
  private int maxPrivDecryptParamsLength = 0;

  protected SecurityProtocols() {
    authProtocols = new Hashtable(5);
    privProtocols = new Hashtable(5);
  }

  /**
   * Get an instance of class SecurityProtocols.
   *
   * @return the globally used SecurityProtocols object.
   */
  public static SecurityProtocols getInstance() {
    if (instance == null) {
      instance = new SecurityProtocols();
    }
    return instance;
  }

  /**
   * Set the <code>SecurityProtocols</code>
   * @param securityProtocols SecurityProtocols
   */
  public static void setSecurityProtocols(SecurityProtocols securityProtocols) {
    SecurityProtocols.instance = securityProtocols;
  }

  /**
   * Add the default SecurityProtocols.
   *
   * The names of the SecurityProtocols to add are read from a
   * properties file.
   *
   * @throws InternalError if the properties file cannot be opened/read.
   */
  public synchronized void addDefaultProtocols() {
    if (SNMP4JSettings.isExtensibilityEnabled()) {
      String secProtocols =
          System.getProperty(SECURITY_PROTOCOLS_PROPERTIES,
                             SECURITY_PROTOCOLS_PROPERTIES_DEFAULT);
      InputStream is =
          SecurityProtocols.class.getResourceAsStream(secProtocols);
      if (is == null) {
        throw new InternalError("Could not read '" + secProtocols +
                                "' from classpath!");
      }
      Properties props = new Properties();
      try {
        props.load(is);
        for (Enumeration en = props.propertyNames(); en.hasMoreElements(); ) {
          String className = (String) en.nextElement();
          try {
            Class c = Class.forName(className);
            Object proto = c.newInstance();
            if (proto instanceof AuthenticationProtocol) {
              addAuthenticationProtocol((AuthenticationProtocol) proto);
            }
            else if (proto instanceof PrivacyProtocol) {
              addPrivacyProtocol((PrivacyProtocol) proto);
            }
            else {
              logger.error(
                  "Failed to register security protocol because it does " +
                  "not implement required interfaces: " + className);
            }
          }
          catch (Exception cnfe) {
            logger.error(cnfe);
            throw new InternalError(cnfe.toString());
          }
        }
      }
      catch (IOException iox) {
        String txt = "Could not read '" + secProtocols + "': " +
            iox.getMessage();
        logger.error(txt);
        throw new InternalError(txt);
      }
      finally {
        try {
          is.close();
        }
        catch (IOException ex) {
          // ignore
          logger.warn(ex);
        }
      }
    }
    else {
      addAuthenticationProtocol(new AuthMD5());
      addAuthenticationProtocol(new AuthSHA());
      addPrivacyProtocol(new PrivDES());
      addPrivacyProtocol(new PrivAES128());
      addPrivacyProtocol(new PrivAES192());
      addPrivacyProtocol(new PrivAES256());
    }
  }

  /**
   * Add the given {@link org.snmp4j.security.AuthenticationProtocol}. If an authentication protocol
   * with the supplied ID already exists, the supplied authentication protocol
   * will not be added and the security protocols will not be unchang.
   *
   * @param auth
   *    the AuthenticationProtocol to add (an existing authentication protcol
   *    with <code>auth</code>'s ID remains unchanged).
   */
  public synchronized void addAuthenticationProtocol(AuthenticationProtocol auth) {
    if (authProtocols.get(auth.getID()) == null) {
      authProtocols.put(auth.getID(), auth);
      if (auth.getDigestLength() > maxAuthDigestLength) {
        maxAuthDigestLength = auth.getDigestLength();
      }
    }
  }

  /**
   * Get the {@link org.snmp4j.security.AuthenticationProtocol} with the given ID.
   *
   * @param id
   *    The unique ID (specified as {@link org.snmp4j.smi.OID}) of the AuthenticationProtocol.
   * @return
   *    the AuthenticationProtocol object if it was added before,
   *    or null if not.
   */
  public AuthenticationProtocol getAuthenticationProtocol(OID id) {
    if (id == null) {
      return null;
    }
    return (AuthenticationProtocol)authProtocols.get(id);
  }

  /**
   * Remove the given {@link org.snmp4j.security.AuthenticationProtocol}.
   *
   * @param auth The protocol to remove
   */
  public void removeAuthenticationProtocol(AuthenticationProtocol auth) {
    authProtocols.remove(auth.getID());
  }

  /**
   * Add the given {@link org.snmp4j.security.PrivacyProtocol}. If a privacy protocol
   * with the supplied ID already exists, the supplied privacy protocol
   * will not be added and the security protocols will not be changed.
   *
   * @param priv
   *    the PrivacyProtocol to add (an existing privacy protcol
   *    with <code>priv</code>'s ID remains unchanged).
   */
  public synchronized void addPrivacyProtocol(PrivacyProtocol priv) {
    if (privProtocols.get(priv.getID()) == null) {
      privProtocols.put(priv.getID(), priv);
      if (priv.getDecryptParamsLength() > maxPrivDecryptParamsLength) {
        maxPrivDecryptParamsLength = priv.getDecryptParamsLength();
      }
    }
  }

  /**
   * Get the PrivacyProtocol with the given ID.
   *
   * @param id
   *    The unique ID (specified as {@link org.snmp4j.smi.OID}) of the PrivacyProtocol.
   * @return
   *    the {@link org.snmp4j.security.PrivacyProtocol} object if it was added before,
   *    or null if not.
   */
  public PrivacyProtocol getPrivacyProtocol(OID id) {
    if (id == null) {
      return null;
    }
    return (PrivacyProtocol)privProtocols.get(id);
  }

  /**
   * Remove the given {@link org.snmp4j.security.PrivacyProtocol}.
   *
   * @param priv The protocol to remove
   */
  public void removePrivacyProtocol(PrivacyProtocol priv) {
    privProtocols.remove(priv.getID());
  }


  /**
   * Generates the localized key for the given password and engine id for the
   * authentication protocol specified by the supplied OID.
   *
   * @param authProtocolID
   *    an <code>OID</code> identifying the authentication protocol to
   *    use.
   * @param passwordString
   *    the authentication pass phrase.
   * @param engineID
   *    the engine ID of the authoritative engine.
   * @return
   *    the localized authentication key.
   */
  public byte[] passwordToKey(OID authProtocolID,
                              OctetString passwordString,
                              byte[] engineID) {

    AuthenticationProtocol protocol =
        (AuthenticationProtocol)authProtocols.get(authProtocolID);
    if (protocol == null) {
      return null;
    }
    return protocol.passwordToKey(passwordString, engineID);
  }

  /**
   * Generates the localized key for the given password and engine id for the
   * privacy protocol specified by the supplied OID.
   *
   * @param privProtocolID
   *    an <code>OID</code> identifying the privacy protocol the key should
   *    be created for.
   * @param authProtocolID
   *    an <code>OID</code> identifying the authentication protocol to use.
   * @param passwordString
   *    the authentication pass phrase.
   * @param engineID
   *    the engine ID of the authoritative engine.
   * @return
   *    the localized privacy key.
   */
  public byte[] passwordToKey(OID privProtocolID,
                              OID authProtocolID,
                              OctetString passwordString,
                              byte[] engineID) {

    AuthenticationProtocol authProtocol =
        (AuthenticationProtocol)authProtocols.get(authProtocolID);
    if (authProtocol == null) {
      return null;
    }
    PrivacyProtocol privProtocol =
        (PrivacyProtocol)privProtocols.get(privProtocolID);
    if (privProtocol == null) {
      return null;
    }
    byte[] key = authProtocol.passwordToKey(passwordString, engineID);

    if (key == null) {
      return null;
    }
    if (key.length >= privProtocol.getMinKeyLength()) {
      if (key.length > privProtocol.getMaxKeyLength()) {
        // truncate key
        byte[] truncatedKey = new byte[privProtocol.getMaxKeyLength()];
        System.arraycopy(key, 0, truncatedKey, 0, privProtocol.getMaxKeyLength());
        return truncatedKey;
      }
      return key;
    }
    // extend key if necessary
    byte[] extKey = privProtocol.extendShortKey(key, passwordString, engineID,
                                                authProtocol);
    return extKey;
  }

  /**
   * Gets the maximum authentication key length of the all known
   * authentication protocols.
   * @return
   *    the maximum authentication key length of all authentication protocols
   *    that have been added to this <code>SecurityProtocols</code>
   *    instance.
   */
  public int getMaxAuthDigestLength() {
    return maxAuthDigestLength;
  }

  /**
   * Gets the maximum privacy key length of the currently known
   * privacy protocols.
   * @return
   *    the maximum privacy key length of all privacy protocols
   *    that have been added to this <code>SecurityProtocols</code>
   *    instance.
   */
  public int getMaxPrivDecryptParamsLength() {
    return maxPrivDecryptParamsLength;
  }

  /**
   * Limits the supplied key value to the specified maximum length
   * @param key
   *    the key to truncate.
   * @param maxKeyLength
   *    the maximum length of the returned key.
   * @return
   *    the truncated key with a length of
   *    <code>min(key.length, maxKeyLength)</code>.
   * @since 1.9
   */
  public byte[] truncateKey(byte[] key, int maxKeyLength) {
    byte[] truncatedNewKey = new byte[Math.min(maxKeyLength, key.length)];
    System.arraycopy(key, 0, truncatedNewKey, 0, truncatedNewKey.length);
    return truncatedNewKey;
  }

}


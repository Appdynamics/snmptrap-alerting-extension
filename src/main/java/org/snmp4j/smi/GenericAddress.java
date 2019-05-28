/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.smi;

import org.snmp4j.SNMP4JSettings;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * The <code>GenericAddress</code> implements the decorator and factory
 * design pattern to provide a generic address type.
 * <p>
 * To register address types other than the default, set the system property
 * {@link #ADDRESS_TYPES_PROPERTIES} before calling the {@link #parse} method
 * for the first time.
 *
 * @author Frank Fock
 * @version 1.8
 */
public class GenericAddress extends SMIAddress {

  static final long serialVersionUID = -6102594326202231511L;

  /**
   * Default address type identifier for an UpdAddress.
   */
  public static final String TYPE_UDP = "udp";
  /**
   * Default address type identifier for a TcpAddress.
   */
  public static final String TYPE_TCP = "tcp";
  /**
   * Default address type identifier for an IpAddress.
   */
  public static final String TYPE_IP  = "ip";

  public static final String ADDRESS_TYPES_PROPERTIES =
      "org.snmp4j.addresses";
  private static final String ADDRESS_TYPES_PROPERTIES_DEFAULT =
      "address.properties";

  private static final LogAdapter logger = LogFactory.getLogger(GenericAddress.class);

  private SMIAddress address;
  private static Map knownAddressTypes = null;

  public GenericAddress() {
  }

  public GenericAddress(SMIAddress address) {
    this.address = address;
  }

  public int getSyntax() {
    return address.getSyntax();
  }

  public boolean isValid() {
    if (address == null) {
      return false;
    }
    return address.isValid();
  }

  public String toString() {
    return address.toString();
  }

  public int hashCode() {
    return address.hashCode();
  }

  public int compareTo(Object o) {
    return address.compareTo(o);
  }

  public boolean equals(Object o) {
    return address.equals(o);
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    throw new UnsupportedOperationException();
  }
  public void encodeBER(OutputStream outputStream) throws IOException {
    address.encodeBER(outputStream);
  }

  public int getBERLength() {
    return address.getBERLength();
  }

  public void setAddress(SMIAddress address) {
    this.address = address;
  }

  public Address getAddress() {
    return address;
  }

  /**
   * Register Address classes from a properties file. The registered
   * address types are used by the {@link parse} method to type-safe
   * instantiate sub-classes from <code>Address</code> from a
   * <code>String</code>.
   */
  private synchronized static void registerAddressTypes() {
    if (SNMP4JSettings.isExtensibilityEnabled()) {
      String addresses = System.getProperty(ADDRESS_TYPES_PROPERTIES,
                                            ADDRESS_TYPES_PROPERTIES_DEFAULT);
      InputStream is = Variable.class.getResourceAsStream(addresses);
      if (is == null) {
        throw new InternalError("Could not read '" + addresses +
                                "' from classpath!");
      }
      Properties props = new Properties();
      try {
        props.load(is);
        Map h = new TreeMap();
        for (Enumeration en = props.propertyNames(); en.hasMoreElements(); ) {
          String id = (String) en.nextElement();
          String className = props.getProperty(id);
          try {
            Class c = Class.forName(className);
            h.put(id, c);
          }
          catch (ClassNotFoundException cnfe) {
            logger.error(cnfe);
          }
        }
        knownAddressTypes = h;
      }
      catch (IOException iox) {
        String txt = "Could not read '" + addresses + "': " + iox.getMessage();
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
      Map h = new TreeMap();
      h.put(TYPE_UDP, UdpAddress.class);
      h.put(TYPE_TCP, TcpAddress.class);
      h.put(TYPE_IP, IpAddress.class);
      knownAddressTypes = h;
    }
  }

  /**
   * Parses a given transport protocol dependent address string into an
   * <code>Address</code> instance that is subsumed by this
   * <code>GenericAddress</code> object.
   *
   * @param address
   *    an address string with a leading type specifier as defined in the
   *    "address.properties". The format is <code>"type:address"</code> where
   *    the format of <code>address</code> depends on <code>type</code>.
   *    Valid values for <code>type</code> are, for example, "udp" and "tcp".
   * @return
   *    a <code>Address</code> instance of the address classes specified
   *    in "address.properties" whose type ID matched the specified ID in
   *    <code>address</code>. If <code>address</code> cannot be parsed,
   *    <code>null</code> is returned.
   * @throws IllegalArgumentException
   *    if the address type indicator supplied is not know.
   */
  public static Address parse(String address) {
    if (knownAddressTypes == null) {
      registerAddressTypes();
    }
    String type = TYPE_UDP;
    int sep = address.indexOf(':');
    if (sep > 0) {
      type = address.substring(0, sep);
      address = address.substring(sep+1);
    }
    type = type.toLowerCase();
    Class c = (Class)knownAddressTypes.get(type);
    if (c == null) {
      throw new IllegalArgumentException("Address type " + type + " unknown");
    }
    try {
      Address addr = (Address)c.newInstance();
      if (addr.parseAddress(address)) {
        return addr;
      }
      return null;
    }
    catch (Exception ex) {
      logger.warn(ex);
    }
    return null;
  }

  /**
   * Parse an address form the supplied string.
   * @param address
   *    an address string known by the GenericAddress.
   * @return boolean
   * @see #parse(String address)
   */
  public boolean parseAddress(String address) {
    Address addr = parse(address);
    if (addr instanceof SMIAddress) {
      setAddress((SMIAddress)addr);
      return true;
    }
    return false;
  }

  public Object clone() {
    return new GenericAddress(address);
  }

  public int toInt() {
    throw new UnsupportedOperationException();
  }

  public long toLong() {
    throw new UnsupportedOperationException();
  }

  public OID toSubIndex(boolean impliedLength) {
    throw new UnsupportedOperationException();
  }

  public void fromSubIndex(OID subIndex, boolean impliedLength) {
    throw new UnsupportedOperationException();
  }

  public void setValue(String value) {
    if (!parseAddress(value)) {
      throw new IllegalArgumentException(value+" cannot be parsed by "+
                                         getClass().getName());
    }
  }
}


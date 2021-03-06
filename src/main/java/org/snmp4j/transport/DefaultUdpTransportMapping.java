/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.transport;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

import org.snmp4j.log.*;
import org.snmp4j.smi.*;
import org.snmp4j.SNMP4JSettings;
import java.io.InterruptedIOException;
import org.snmp4j.util.WorkerTask;

/**
 * The <code>DefaultUdpTransportMapping</code> implements a UDP transport
 * mapping based on Java standard IO and using an internal thread for
 * listening on the inbound socket.
 *
 * @author Frank Fock
 * @version 1.9
 */
public class DefaultUdpTransportMapping extends UdpTransportMapping {

  private static final LogAdapter logger =
      LogFactory.getLogger(DefaultUdpTransportMapping.class);

  protected DatagramSocket socket = null;
  protected WorkerTask listener;
  protected ListenThread listenerThread;
  private int socketTimeout = 0;

  private int receiveBufferSize = 0; // not set by default

  /**
   * Creates a UDP transport with an arbitrary local port on all local
   * interfaces.
   *
   * @throws java.io.IOException
   *    if socket binding fails.
   */
  public DefaultUdpTransportMapping() throws IOException {
    super(new UdpAddress(InetAddress.getLocalHost(), 0));
    socket = new DatagramSocket(udpAddress.getPort());
  }

  /**
   * Creates a UDP transport with optional reusing the address if is currently
   * in timeout state (TIME_WAIT) after the connection is closed.
   *
   * @param udpAddress
   *    the local address for sending and receiving of UDP messages.
   * @param reuseAddress
   *    if <code>true</code> addresses are reused which provides faster socket
   *    binding if an application is restarted for instance.
   * @throws java.io.IOException
   *    if socket binding fails.
   * @since 1.7.3
   */
  public DefaultUdpTransportMapping(UdpAddress udpAddress,
                                    boolean reuseAddress) throws IOException {
    super(udpAddress);
    socket = new DatagramSocket(null);
    socket.setReuseAddress(reuseAddress);
    final SocketAddress addr =
        new InetSocketAddress(udpAddress.getInetAddress(),udpAddress.getPort());
    socket.bind(addr);
  }

  /**
   * Creates a UDP transport on the specified address. The address will not be
   * reused if it is currently in timeout state (TIME_WAIT).
   *
   * @param udpAddress
   *    the local address for sending and receiving of UDP messages.
   * @throws java.io.IOException
   *    if socket binding fails.
   */
  public DefaultUdpTransportMapping(UdpAddress udpAddress) throws IOException {
    super(udpAddress);
    socket = new DatagramSocket(udpAddress.getPort(),
                                udpAddress.getInetAddress());
  }

  public void sendMessage(Address targetAddress, byte[] message)
      throws IOException
  {
    InetSocketAddress targetSocketAddress =
        new InetSocketAddress(((UdpAddress)targetAddress).getInetAddress(),
                              ((UdpAddress)targetAddress).getPort());
    if (logger.isDebugEnabled()) {
      logger.debug("Sending message to "+targetAddress+" with length "+
                   message.length+": "+
                   new OctetString(message).toHexString());
    }
    DatagramSocket s = ensureSocket();
    s.send(new DatagramPacket(message, message.length, targetSocketAddress));
  }

  /**
   * Closes the socket and stops the listener thread.
   *
   * @throws java.io.IOException
   */
  public void close() throws IOException {
    boolean interrupted = false;
    WorkerTask l = listener;
    if (l != null) {
      l.terminate();
      l.interrupt();
      if (socketTimeout > 0) {
        try {
          l.join();
        }
        catch (InterruptedException ex) {
          interrupted = true;
          logger.warn(ex);
        }
      }
      listener = null;
    }
    DatagramSocket closingSocket = socket;
    if ((closingSocket != null) && (!closingSocket.isClosed())) {
      closingSocket.close();
    }
    socket = null;
    if (interrupted) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Starts the listener thread that accepts incoming messages. The thread is
   * started in daemon mode and thus it will not block application terminated.
   * Nevertheless, the {@link #close()} method should be called to stop the
   * listen thread gracefully and free associated ressources.
   *
   * @throws java.io.IOException
   */
  public synchronized void listen() throws IOException {
    if (listener != null) {
      throw new SocketException("Port already listening");
    }
    ensureSocket();
    listenerThread = new ListenThread();
    listener = SNMP4JSettings.getThreadFactory().createWorkerThread(
        "DefaultUDPTransportMapping_"+getAddress(), listenerThread, true);
    listener.run();
  }

  private synchronized DatagramSocket ensureSocket() throws SocketException {
    DatagramSocket s = socket;
    if (s == null) {
      s = new DatagramSocket(udpAddress.getPort());
      s.setSoTimeout(socketTimeout);
      this.socket = s;
    }
    return s;
  }

  /**
   * Changes the priority of the listen thread for this UDP transport mapping.
   * This method has no effect, if called before {@link #listen()} has been
   * called for this transport mapping.
   *
   * @param newPriority
   *    the new priority.
   * @see Thread#setPriority
   * @since 1.2.2
   */
  public void setPriority(int newPriority) {
    WorkerTask lt = listener;
    if (lt instanceof Thread) {
      ((Thread)lt).setPriority(newPriority);
    }
  }

  /**
   * Returns the priority of the internal listen thread.
   * @return
   *    a value between {@link Thread#MIN_PRIORITY} and
   *    {@link Thread#MAX_PRIORITY}.
   * @since 1.2.2
   */
  public int getPriority() {
    WorkerTask lt = listener;
    if (lt instanceof Thread) {
      return ((Thread)lt).getPriority();
    }
    else {
      return Thread.NORM_PRIORITY;
    }
  }

  /**
   * Sets the name of the listen thread for this UDP transport mapping.
   * This method has no effect, if called before {@link #listen()} has been
   * called for this transport mapping.
   *
   * @param name
   *    the new thread name.
   * @since 1.6
   */
  public void setThreadName(String name) {
    WorkerTask lt = listener;
    if (lt instanceof Thread) {
      ((Thread)lt).setName(name);
    }
  }

  /**
   * Returns the name of the listen thread.
   * @return
   *    the thread name if in listening mode, otherwise <code>null</code>.
   * @since 1.6
   */
  public String getThreadName() {
    WorkerTask lt = listener;
    if (lt instanceof Thread) {
      return ((Thread)lt).getName();
    }
    else {
      return null;
    }
  }

  public void setMaxInboundMessageSize(int maxInboundMessageSize) {
    this.maxInboundMessageSize = maxInboundMessageSize;
  }

  /**
   * Returns the socket timeout.
   * 0 returns implies that the option is disabled (i.e., timeout of infinity).
   * @return
   *    the socket timeout setting.
   */
  public int getSocketTimeout() {
    return socketTimeout;
  }

  /**
   * Gets the requested receive buffer size for the underlying UDP socket.
   * This size might not reflect the actual size of the receive buffer, which
   * is implementation specific.
   * @return
   *    <=0 if the default buffer size of the OS is used, or a value >0 if the
   *    user specified a buffer size.
   */
  public int getReceiveBufferSize() {
    return receiveBufferSize;
  }

  /**
   * Sets the receive buffer size, which should be > the maximum inbound message
   * size. This method has to be called before {@link #listen()} to be
   * effective.
   * @param receiveBufferSize
   *    an integer value >0 and > {@link #getMaxInboundMessageSize()}.
   */
  public void setReceiveBufferSize(int receiveBufferSize) {
    if (receiveBufferSize <= 0) {
      throw new IllegalArgumentException("Receive buffer size must be > 0");
    }
    this.receiveBufferSize = receiveBufferSize;
  }

  /**
   * Sets the socket timeout in milliseconds.
   * @param socketTimeout
   *    the socket timeout for incoming messages in milliseconds.
   *    A timeout of zero is interpreted as an infinite timeout.
   */
  public void setSocketTimeout(int socketTimeout) {
    this.socketTimeout = socketTimeout;
    if (socket != null) {
      try {
        socket.setSoTimeout(socketTimeout);
      }
      catch (SocketException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  public boolean isListening() {
    return (listener != null);
  }

  class ListenThread implements WorkerTask {

    private byte[] buf;
    private volatile boolean stop = false;


    public ListenThread() throws SocketException {
      buf = new byte[getMaxInboundMessageSize()];
    }

    public void run() {
      DatagramSocket socketCopy = socket;
      if (socketCopy != null) {
        try {
          socketCopy.setSoTimeout(getSocketTimeout());
          if (receiveBufferSize > 0) {
            socketCopy.setReceiveBufferSize(Math.max(receiveBufferSize,
                                                      maxInboundMessageSize));
          }
          if (logger.isDebugEnabled()) {
            logger.debug("UDP receive buffer size for socket " +
                             getAddress() + " is set to: " +
                             socketCopy.getReceiveBufferSize());
          }
        } catch (SocketException ex) {
          logger.error(ex);
          setSocketTimeout(0);
        }
      }
      while (!stop) {
        DatagramPacket packet = new DatagramPacket(buf, buf.length,
                                                   udpAddress.getInetAddress(),
                                                   udpAddress.getPort());
        try {
          try {
            socketCopy = socket;
            if (socketCopy == null) {
              stop = true;
              continue;
            }
            socketCopy.receive(packet);
          }
          catch (InterruptedIOException iiox) {
            if (iiox.bytesTransferred <= 0) {
              continue;
            }
          }
          if (logger.isDebugEnabled()) {
            logger.debug("Received message from "+packet.getAddress()+"/"+
                         packet.getPort()+
                         " with length "+packet.getLength()+": "+
                         new OctetString(packet.getData(), 0,
                                         packet.getLength()).toHexString());
          }
          ByteBuffer bis;
          // If messages are processed asynchronously (i.e. multi-threaded)
          // then we have to copy the buffer's content here!
          if (isAsyncMsgProcessingSupported()) {
            byte[] bytes = new byte[packet.getLength()];
            System.arraycopy(packet.getData(), 0, bytes, 0, bytes.length);
            bis = ByteBuffer.wrap(bytes);
          }
          else {
            bis = ByteBuffer.wrap(packet.getData());
          }
          fireProcessMessage(new UdpAddress(packet.getAddress(),
                                            packet.getPort()), bis);
        }
        catch (SocketTimeoutException stex) {
          // ignore
        }
        catch (PortUnreachableException purex) {
          synchronized (DefaultUdpTransportMapping.this) {
            listener = null;
          }
          logger.error(purex);
          if (logger.isDebugEnabled()) {
            purex.printStackTrace();
          }
          if (SNMP4JSettings.isForwardRuntimeExceptions()) {
            throw new RuntimeException(purex);
          }
          break;
        }
        catch (SocketException soex) {
          if (!stop) {
            logger.error("Socket for transport mapping " + toString() +
                         " error: " + soex.getMessage(), soex);
          }
          stop = true;
        }
        catch (IOException iox) {
          logger.warn(iox);
          if (logger.isDebugEnabled()) {
            iox.printStackTrace();
          }
          if (SNMP4JSettings.isForwardRuntimeExceptions()) {
            throw new RuntimeException(iox);
          }
        }
      }
      synchronized (DefaultUdpTransportMapping.this) {
        listener = null;
        stop = true;
        DatagramSocket closingSocket = socket;
        if ((closingSocket != null) && (!closingSocket.isClosed())) {
          closingSocket.close();
        }
      }
      if (logger.isDebugEnabled()) {
        logger.debug("Worker task stopped:" + getClass().getName());
      }
    }

    public void close() {
      stop = true;
    }

    public void terminate() {
      close();
      if (logger.isDebugEnabled()) {
        logger.debug("Terminated worker task: " + getClass().getName());
      }
    }

    public void join() throws InterruptedException {
      if (logger.isDebugEnabled()) {
        logger.debug("Joining worker task: " + getClass().getName());
      }
    }

    public void interrupt() {
      if (logger.isDebugEnabled()) {
        logger.debug("Interrupting worker task: " + getClass().getName());
      }
      close();
    }
  }
}

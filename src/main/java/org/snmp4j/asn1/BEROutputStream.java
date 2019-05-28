/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package org.snmp4j.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;


/**
 * The <code>BEROutputStream</code> class wraps a <code>ByteBuffer</code>
 * to support BER encoding. The backing buffer can be accessed directly to
 * optimize performance and memory usage.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class BEROutputStream extends OutputStream {

  private ByteBuffer buffer;
  private int offset = 0;

  /**
   * Creates a BER output stream without a backing buffer set. In order to
   * be able to write anything to the stream,
   * {@link #setBuffer(java.nio.ByteBuffer buffer)} has to be
   * called before. Otherwise a <code>NullPointerException</code> will be
   * thrown when calling one of the <code>write</code> operations.
   */
  public BEROutputStream() {
    this.buffer = null;
  }

  /**
   * Create a <code>BEROutputStream</code> that uses the supplied buffer
   * as backing buffer.
   * @param buffer
   *    a <code>ByteBuffer</code> whose limit and capacity must be greater or
   *    equal that the length of the encoded BER stream.
   */
  public BEROutputStream(ByteBuffer buffer) {
    this.buffer = buffer;
    this.offset = buffer.position();
  }

  public void write(int b) throws IOException {
    buffer.put((byte)b);
  }

  public void write(byte[] b) throws IOException {
    buffer.put(b);
  }

  public void write(byte[] b, int off, int len) throws IOException {
      buffer.put(b, off, len);
  }

  public void close() throws IOException {
  }

  public void flush() throws IOException {
  }

  /**
   * Rewinds backing buffer and returns it. In contrast to the backing buffer's
   * rewind method, this method sets the position of the buffer to the first
   * byte of this output stream rather than to the first byte of the underlying
   * byte array!
   * @return
   *    the ByteBuffer backing this output stream with its current position
   *    set to the begin of the output stream.
   */
  public ByteBuffer rewind() {
    return (ByteBuffer) buffer.position(offset);
  }

  /**
   * Gets the backing buffer.
   * @return
   *    the <code>ByteBuffer</code> backing this output stream.
   */
  public ByteBuffer getBuffer() {
    return buffer;
  }

  /**
   * Sets the backing buffer to the supplied one and sets the offset used by
   * {@link #rewind()} to the buffers current position.
   * @param buffer
   *    a <code>ByteBuffer</code> whose limit and capacity must be greater or
   *    equal that the length of the encoded BER stream.
   */
  public void setBuffer(ByteBuffer buffer) {
    this.buffer = buffer;
    this.offset = buffer.position();
  }

  /**
   * Sets the backing buffer and sets the current position of the stream to
   * the buffers limit (end).
   * @param buffer
   *    a <code>ByteBuffer</code> whose limit and capacity must be greater or
   *    equal that the length of the encoded BER stream.
   */
  public void setFilledBuffer(ByteBuffer buffer) {
    this.buffer = buffer;
    this.offset = buffer.position();
    buffer.position(buffer.limit());
  }

}

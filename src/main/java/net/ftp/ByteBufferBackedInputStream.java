package net.ftp;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferBackedInputStream extends InputStream {

    private ByteBuffer buffer;

    private int markPosition = -1;  // Keep track of the mark position

    public ByteBufferBackedInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    // Method to reset the ByteBuffer without recreating the stream
    public void setByteBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        return buffer.get() & 0xFF;
    }

    @Override
    public int read(@SuppressWarnings("NullableProblems") byte[] bytes, int off, int len) {
        if (!buffer.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buffer.remaining());
        buffer.get(bytes, off, len);
        return len;
    }

    @Override
    public boolean markSupported() {
        return true;  // Indicate that mark/reset are supported
    }

    @Override
    public synchronized void mark(int readLimit) {
        // Store the current position of the buffer
        markPosition = buffer.position();
    }

    @Override
    public synchronized void reset() throws IOException {
        if (markPosition == -1) {
            throw new IOException("Mark has not been set");
        }
        // Reset the position of the buffer to the marked position
        buffer.position(markPosition);
    }

}

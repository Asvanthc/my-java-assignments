package net.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class com {
    public static class BlockingDataTransfer implements DataTransfer {
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private static final int MARK_LIMIT = 1024;  // Limit for mark operation

        public BlockingDataTransfer(InputStream inputStream, OutputStream outputStream) {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        @Override
        public void write(byte[] data) throws IOException {
            outputStream.write(data);
            outputStream.flush();
        }

        @Override
        public void write(byte[] data, int offset, int length) throws IOException {
            outputStream.write(data, offset, length);
            outputStream.flush();
        }

        @Override
        public int read(byte[] buffer) throws IOException {
            // Mark the input stream
            inputStream.mark(MARK_LIMIT);
            int bytesRead = inputStream.read(buffer);
            if (bytesRead == -1) {
                return -1;  // End of stream
            }
            return bytesRead;
        }

        @Override
        public void mark() throws IOException {
            inputStream.mark(MARK_LIMIT); // Mark the stream
        }

        @Override
        public void reset() throws IOException {
            inputStream.reset(); // Reset to the marked position
        }

        @Override
        public void skip(int n) throws IOException {
            inputStream.skip(n); // Skip n bytes
        }
    }

    public static class NonBlockingDataTransfer implements DataTransfer {
        private final SocketChannel socketChannel;
        private final ByteBuffer readBuffer;  // Buffer to manage incoming data
        private final ByteBuffer markedBuffer; // Buffer for the marked position
        private static final int BUFFER_SIZE = 1024;

        public NonBlockingDataTransfer(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
            this.readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            this.markedBuffer = ByteBuffer.allocate(BUFFER_SIZE); // Allocate buffer for marking
        }

        @Override
        public void write(byte[] data) throws IOException {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
        }

        @Override
        public void write(byte[] data, int offset, int length) throws IOException {
            ByteBuffer buffer = ByteBuffer.wrap(data, offset, length);
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
        }

        @Override
        public int read(byte[] buffer) throws IOException {
            readBuffer.clear();  // Clear the buffer for new read
            int bytesRead = socketChannel.read(readBuffer);
            if (bytesRead == -1) {
                return -1;  // End of stream
            }
            readBuffer.flip();  // Prepare buffer for reading
            readBuffer.get(buffer, 0, bytesRead);  // Copy data to the provided buffer
            return bytesRead;
        }

        @Override
        public void mark() {
            markedBuffer.clear(); // Clear the marked buffer
            readBuffer.flip();    // Prepare the read buffer for reading
            markedBuffer.put(readBuffer); // Copy current read data to marked buffer
            markedBuffer.flip(); // Prepare for reading
        }

        @Override
        public void reset() {
            readBuffer.clear(); // Clear current read buffer
            markedBuffer.flip(); // Prepare marked buffer for writing
            readBuffer.put(markedBuffer); // Copy marked data back to read buffer
            readBuffer.flip(); // Prepare for reading
        }

        @Override
        public void skip(int n) throws IOException {
            // Read and discard n bytes from the socket channel
            ByteBuffer tempBuffer = ByteBuffer.allocate(n);
            while (n > 0) {
                int bytesRead = socketChannel.read(tempBuffer);
                if (bytesRead == -1) {
                    break;  // End of stream
                }
                n -= bytesRead;
            }
        }
    }

    public static interface DataTransfer {
        void write(byte[] data) throws IOException;
        void write(byte[] data, int offset, int length) throws IOException;
        int read(byte[] buffer) throws IOException;
        void mark() throws IOException;  // Method to mark the current position
        void reset() throws IOException; // Method to reset to the marked position
        void skip(int n) throws IOException; // Method to skip n bytes
    }
}

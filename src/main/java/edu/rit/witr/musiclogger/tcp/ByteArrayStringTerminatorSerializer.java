package edu.rit.witr.musiclogger.tcp;

import org.springframework.integration.ip.tcp.serializer.AbstractPooledBufferByteArraySerializer;
import org.springframework.integration.ip.tcp.serializer.ByteArraySingleTerminatorSerializer;
import org.springframework.integration.ip.tcp.serializer.SoftEndOfStreamException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Essentially a clone of {@link ByteArraySingleTerminatorSerializer} but with a full arbitrary String as the
 * terminator. The only major difference between the two classes is this includes the terminator in the deserialized
 * response byte array.
 */
public class ByteArrayStringTerminatorSerializer extends AbstractPooledBufferByteArraySerializer {

    private final String terminator;

    /**
     * Creates a {@link ByteArrayStringTerminatorSerializer} with a delimiter.
     *
     * @param delimiter The terminator, which is included in the byte array response
     */
    public ByteArrayStringTerminatorSerializer(String delimiter) {
        this.terminator = delimiter;
    }

    /**
     * Reads the data in the inputStream to a byte[]. Data must be terminated
     * by a single byte. Throws a {@link SoftEndOfStreamException} if the stream
     * is closed immediately after the terminator (i.e. no data is in the process of
     * being read).
     */
    @Override
    protected byte[] doDeserialize(InputStream inputStream, byte[] buffer) throws IOException {
        int n = 0;

        char[] termBuffer = new char[terminator.length()];

        int bite;
        int available = inputStream.available();
        logger.debug(() -> "Available to read: " + available);
        try {
            while (true) {
                bite = inputStream.read();
                if (bite < 0 && n == 0) {
                    throw new SoftEndOfStreamException("Stream closed between payloads");
                }

                checkClosure(bite);

                addBuffer(termBuffer, (char) bite);

                buffer[n++] = (byte) bite;
                if (shouldTerminate(termBuffer)) {
                    break;
                }

                int maxMessageSize = getMaxMessageSize();
                if (n >= maxMessageSize) {
                    throw new IOException("Terminator '" + terminator
                            + "' not found before max message length: "
                            + maxMessageSize);
                }
            }

            return copyToSizedArray(buffer, n);
        }
        catch (SoftEndOfStreamException e) { // NOSONAR catch and throw
            throw e; // it's an IO exception and we don't want an event for this
        }
        catch (IOException | RuntimeException ex) {
            publishEvent(ex, buffer, n);
            throw ex;
        }
    }

    /**
     * Writes the byte[] to the stream and appends the terminator.
     */
    @Override
    public void serialize(byte[] bytes, OutputStream outputStream) throws IOException {
        outputStream.write(bytes);
        outputStream.write(this.terminator.getBytes());
    }

    // remove 1st element of termBuffer, shift the array left, add `data` to the end

    /**
     * Shifts the array left, and adds the given data to the end. This creates a "pseudo-queue" but with a native array
     * for efficiency. This should be used to continuously check if the new buffered data array (which should be the
     * length of the terminating string) is indeed equal to the terminating string.
     *
     * @param termBuffer The array
     * @param data The data to add to the buffer
     */
    private void addBuffer(char[] termBuffer, char data) {

        // shift array left 1
        System.arraycopy(termBuffer, 1, termBuffer, 0, termBuffer.length - 1);

        // Add data at the end
        termBuffer[termBuffer.length - 1] = data;
    }

    private boolean shouldTerminate(char[] termBuffer) {
        return Arrays.equals(termBuffer, terminator.toCharArray());
    }

}

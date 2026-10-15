package org.cs2103t.marquee.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A {@code FilterInputStream} that keeps track of the current position in the stream.
 */
public final class InputStreamWithPosition extends FilterInputStream {
    private long pos = 0;
    private long mark = 0;

    public InputStreamWithPosition(InputStream in) {
        super(in);
    }

    /**
     * Gets the stream position.
     *
     * @return the current stream position.
     */
    public long getPosition() {
        return pos;
    }

    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b > 0) {
            pos += 1;
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = super.read(b, off, len);
        if (n > 0) {
            pos += 1;
        }
        return n;
    }

    @Override
    public long skip(long skip) throws IOException {
        long n = super.skip(skip);
        if (n > 0) {
            pos += 1;
        }
        return n;
    }

    @Override
    public void mark(int readlimit) {
        super.mark(readlimit);
        mark = pos;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        pos = mark;
    }

}

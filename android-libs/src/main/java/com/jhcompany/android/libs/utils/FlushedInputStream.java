package com.jhcompany.android.libs.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Many streams obtained over slow connection show <a
 * href="http://code.google.com/p/android/issues/detail?id=6066">this
 * problem</a>.
 */
public class FlushedInputStream extends FilterInputStream {

    public FlushedInputStream(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public long skip(long n) throws IOException {
        long totalBytesSkipped = 0L;
        while (totalBytesSkipped < n) {
            long bytesSkipped = in.skip(n - totalBytesSkipped);
            if (bytesSkipped == 0L) {
                int readCount = read();
                if (readCount < 0) {
                    break; // we reached EOF
                } else {
                    bytesSkipped = 1; // we read one byte
                }
            }
            totalBytesSkipped += bytesSkipped;
        }
        return totalBytesSkipped;
    }
}

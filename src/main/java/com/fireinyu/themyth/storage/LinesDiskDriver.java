package com.fireinyu.themyth.storage;

import java.util.stream.Stream;

/**
 * Driver interface for reading and writing to a storage on Disk.
 * File is read and written line-wise.
 */
public interface LinesDiskDriver {

    /**
     * Reads the lines in the disk storage associated with this LinesDiskDriver into a Stream buffer.
     *
     * @return Stream of lines in the disk storage.
     * @see Stream
     * @see String
     */
    Stream<String> readLines();

    /**
     * Writes all lines from a String Stream into the disk storage associated with this LinesDiskDriver.
     *
     * @param lines Stream of lines to be written.
     * @see Stream
     * @see String
     */
    void writeLines(Stream<String> lines);

    /**
     * Returns the descriptor associated with this LinesDiskDriver.
     *
     * @return the descriptor associated with this LinesDiskDriver.
     * @see String
     */
    String getDescriptor();
}

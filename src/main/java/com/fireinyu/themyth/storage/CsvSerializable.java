package com.fireinyu.themyth.storage;

import java.util.List;

/**
 * A class implements the CsvSerializable interface to indicate that it can be serialized to
 * and deserialized from a line in a CSV file on disk.
 *
 * @see FileLinesDisk
 */
public interface CsvSerializable {

    /**
     * Serializes this object into a List of String attributes.
     *
     * @return List of String attributes representing the serialized CsvSerializable object.
     * @see List
     * @see String
     */
    List<String> extract();
}

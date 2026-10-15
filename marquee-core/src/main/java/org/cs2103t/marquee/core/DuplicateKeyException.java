package org.cs2103t.marquee.core;

/**
 * Thrown to indicate a duplicate key being used in a unique key scenarios.
 */
public class DuplicateKeyException extends RuntimeException {
    private final String columnName;

    /**
     * Construct a {@code DuplicateKeyException} for a given key
     * with the given message.
     *
     * @param message the detail message
     * @param columnName the name of the duplicate key
     */
    public DuplicateKeyException(String message, String columnName) {
        super(message);
        this.columnName = columnName;
    }

    public String getKey() {
        return columnName;
    }
}

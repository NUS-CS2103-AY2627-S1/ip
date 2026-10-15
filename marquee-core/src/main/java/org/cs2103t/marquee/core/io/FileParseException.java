package org.cs2103t.marquee.core.io;

/**
 * Thrown during file parsing if the file doesn't have the right format.
 */
public class FileParseException extends Exception {
    private long errorOffset;
    private long line;
    private long column;

    /**
     * Contructs a {@code FileParseException} with the given detail message and error positions.
     *
     * @param message the detail message
     * @param errorOffset the position of the error
     * @param line the line number of the error position
     * @param column the column number of the error position
     */
    public FileParseException(String message, long errorOffset, long line, long column) {
        super(message);
        this.errorOffset = errorOffset;
        this.line = line;
        this.column = column;
    }

    public long getErrorOffset() {
        return errorOffset;
    }

    public long getLine() {
        return line;
    }

    public long getColumn() {
        return column;
    }
}

package org.cs2103t.marquee.core.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;

/**
 * Parser class for CSV files.
 */
public final class CsvParser {
    private final InputStreamWithPosition input;
    private final StringBuilder fieldBuilder = new StringBuilder();
    private boolean endOfStream = false;
    private int line = 0;
    private int column = 0;

    public CsvParser(InputStream input) {
        this.input = new InputStreamWithPosition(input);
    }

    /**
     * Checks if the end of stream has been reached.
     *
     * @return whether the end of stream has been reached
     */
    public boolean isEndOfStream() {
        return endOfStream;
    }

    public long getPos() {
        return input.getPosition();
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Parse the next row of the CSV file.
     *
     * @param fieldProcessor callback for every time a field is parsed, given the field index and field value
     * @param expectedFieldCount the expected number of fields, or negative if it is not known in advance
     * @throws FileParseException if the input doesn't follow the CSV format
     * @throws IOException if the input stream throws an {@link IOException}
     * @throws EOFException if the end of the stream has been reached
     */
    public void parseRow(BiConsumer<Integer, String> fieldProcessor, int expectedFieldCount)
            throws FileParseException, IOException, EOFException {
        boolean isEscaped = false;
        int fieldCount = 0;
        int thisChar = input.read();
        column++;
        if (thisChar == -1) {
            endOfStream = true;
            throw new EOFException("End of input stream");
        }
        int lastChar = -1;
        readLoop:
        for (; thisChar != -1; column++, thisChar = input.read()) {
            if (fieldCount == expectedFieldCount) {
                throw new FileParseException("More fields than expected", input.getPosition(), line, column);
            }
            switch (thisChar) {
                case '"' -> {
                    if (isEscaped) {
                        isEscaped = false;
                    } else {
                        if (lastChar == '"') {
                            fieldBuilder.append((char) thisChar);
                        }
                        isEscaped = true;
                    }
                }
                case ',' -> {
                    if (isEscaped) {
                        fieldBuilder.append((char) thisChar);
                    } else {
                        fieldProcessor.accept(fieldCount, fieldBuilder.toString());
                        fieldBuilder.setLength(0);
                        fieldCount++;
                    }
                }
                case '\r' -> {
                    if (isEscaped) {
                        line++;
                        column = 0;
                        fieldBuilder.append((char) thisChar);
                    } else {
                        line++;
                        column = 0;
                        if ((thisChar = input.read()) == '\n') {
                            if (fieldCount == 0 && fieldBuilder.isEmpty()) {
                                continue;
                            } else {
                                break readLoop;
                            }
                        } else {
                            throw new FileParseException("Stray carriage return", input.getPosition(), line, column);
                        }
                    }
                }
                case '\n' -> {
                    if (isEscaped) {
                        fieldBuilder.append((char) thisChar);
                        line++;
                        column = 0;
                    } else {
                        throw new FileParseException("Stray newline", input.getPosition(), line, column);
                    }
                }
                default -> fieldBuilder.append((char) thisChar);
            }
            lastChar = thisChar;
        }
        if (isEscaped) {
            throw new FileParseException("Escaped field not closed", input.getPosition(), line, column);
        }
        if (!(fieldCount == 0 && fieldBuilder.isEmpty())) {
            fieldProcessor.accept(fieldCount, fieldBuilder.toString());
            fieldBuilder.setLength(0);
            fieldCount++;
        }
        if (fieldCount < expectedFieldCount) {
            throw new FileParseException("Not enough fields", input.getPosition(), line, column);
        }
        if (thisChar == -1) {
            endOfStream = true;
        }
    }
}

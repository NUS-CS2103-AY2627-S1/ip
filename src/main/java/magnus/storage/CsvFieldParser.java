package magnus.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes fields from the comma-separated format used for persisted tasks.
 */
final class CsvFieldParser {
    private static final char FIELD_DELIMITER = ',';
    private static final char QUOTATION_MARK = '"';

    private final String serializedData;
    private final List<String> fields = new ArrayList<>();
    private final StringBuilder currentField = new StringBuilder();
    private FieldState fieldState = FieldState.UNQUOTED;
    private int currentPosition;

    private CsvFieldParser(String serializedData) {
        this.serializedData = serializedData;
    }

    /**
     * Parses comma-separated fields, including quoted fields and escaped quotation marks.
     *
     * @param serializedData The serialized fields to decode.
     * @return The decoded fields in their original order.
     * @throws IllegalArgumentException If quotation marks are malformed.
     */
    static List<String> parseFields(String serializedData) {
        return new CsvFieldParser(serializedData).parseFields();
    }

    /**
     * Consumes the serialized data and returns all decoded fields.
     *
     * @return The decoded fields.
     */
    private List<String> parseFields() {
        while (this.currentPosition < this.serializedData.length()) {
            consumeCurrentCharacter();
            this.currentPosition++;
        }

        if (this.fieldState == FieldState.QUOTED) {
            throw new IllegalArgumentException("unclosed quoted field");
        }

        finishCurrentField();
        return this.fields;
    }

    /**
     * Handles the current character according to the current field state.
     */
    private void consumeCurrentCharacter() {
        char currentCharacter = this.serializedData.charAt(this.currentPosition);
        switch (this.fieldState) {
            case UNQUOTED -> consumeUnquotedCharacter(currentCharacter);
            case QUOTED -> consumeQuotedCharacter(currentCharacter);
            case CLOSED_QUOTE -> consumeCharacterAfterClosedQuote(currentCharacter);
            default -> throw new AssertionError("Unexpected field state: " + this.fieldState);
        }
    }

    /**
     * Handles a character in an unquoted field.
     *
     * @param currentCharacter The character to consume.
     */
    private void consumeUnquotedCharacter(char currentCharacter) {
        if (currentCharacter == FIELD_DELIMITER) {
            finishCurrentField();
            return;
        }
        if (currentCharacter == QUOTATION_MARK) {
            if (!this.currentField.isEmpty()) {
                throw new IllegalArgumentException("unexpected quote in an unquoted field");
            }
            this.fieldState = FieldState.QUOTED;
            return;
        }
        this.currentField.append(currentCharacter);
    }

    /**
     * Handles a character in a quoted field.
     *
     * @param currentCharacter The character to consume.
     */
    private void consumeQuotedCharacter(char currentCharacter) {
        if (currentCharacter != QUOTATION_MARK) {
            this.currentField.append(currentCharacter);
            return;
        }
        if (isNextCharacterQuotationMark()) {
            this.currentField.append(QUOTATION_MARK);
            this.currentPosition++;
            return;
        }
        this.fieldState = FieldState.CLOSED_QUOTE;
    }

    /**
     * Handles the delimiter that must follow a closed quoted field.
     *
     * @param currentCharacter The character following the closing quotation mark.
     */
    private void consumeCharacterAfterClosedQuote(char currentCharacter) {
        if (currentCharacter != FIELD_DELIMITER) {
            throw new IllegalArgumentException("unexpected text after a quoted field");
        }
        finishCurrentField();
    }

    /**
     * Returns whether the next character escapes the current quotation mark.
     *
     * @return {@code true} if the current quotation mark is followed by another one.
     */
    private boolean isNextCharacterQuotationMark() {
        int nextPosition = this.currentPosition + 1;
        return nextPosition < this.serializedData.length()
                && this.serializedData.charAt(nextPosition) == QUOTATION_MARK;
    }

    /**
     * Stores the current field and prepares to read an unquoted field.
     */
    private void finishCurrentField() {
        this.fields.add(this.currentField.toString());
        this.currentField.setLength(0);
        this.fieldState = FieldState.UNQUOTED;
    }

    /**
     * Represents the valid parsing states for a serialized field.
     */
    private enum FieldState {
        UNQUOTED,
        QUOTED,
        CLOSED_QUOTE
    }
}

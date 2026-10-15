package cooper.note;

import cooper.exception.CooperException;

/** An immutable, single-line snippet independent of task completion state. */
public record Note(String text) {
    public static final String EMPTY_TEXT = "I need some text for your note!";
    public static final String LINE_BREAK = "I need each note command on a single line!";

    /** Rejects blank or multiline text and removes outer whitespace. */
    public Note {
        if (text.matches("(?s).*\\R.*")) {
            throw new CooperException(LINE_BREAK);
        }
        text = text.strip();
        if (text.isEmpty()) {
            throw new CooperException(EMPTY_TEXT);
        }
    }
}

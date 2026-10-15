package min.note;

/**
 * Represents a short snippet of text that the user wants to remember.
 */
public class Note {
    private static final String FILE_TYPE_VALUE = "N";
    private static final String FILE_FIELD_SEPARATOR = " | ";

    private final String text;

    /**
     * Creates a note holding the given text.
     *
     * @param text The text to remember. Must not contain the storage field separator.
     */
    public Note(String text) {
        assert text != null && !text.isBlank() : "Note text must not be blank.";
        assert !text.contains(FILE_FIELD_SEPARATOR)
                : "Note text must not contain the storage field separator.";

        this.text = text;
    }

    /** Returns this note's text. */
    public String getText() {
        return text;
    }

    /** Returns this note in the format used for persistent storage. */
    public String toFileString() {
        return FILE_TYPE_VALUE + FILE_FIELD_SEPARATOR + text;
    }

    /** Returns this note in a format suitable for display. */
    @Override
    public String toString() {
        return text;
    }
}

package habi;

/** Represents a short free-form note kept separately from actionable tasks. */
public class Note {
    private final String content;

    /**
     * Creates a note with the supplied text.
     *
     * @param content Text of the note.
     */
    public Note(String content) {
        this.content = content;
    }

    /**
     * Returns the note text.
     *
     * @return Note text.
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns this note in the persistent storage format.
     *
     * @return Tab-separated note type and text.
     */
    public String toDataString() {
        return "N\t" + content;
    }

    /**
     * Returns the note's user-facing representation.
     *
     * @return Note icon followed by its text.
     */
    @Override
    public String toString() {
        return "[N] " + content;
    }
}

package vega;

/** Represents a short piece of textual information recorded by the user. */
public class Note {
    private final String text;

    /**
     * Creates a note containing the given text.
     *
     * @param text Text recorded in the note.
     */
    public Note(String text) {
        assert text != null : "Note text must not be null";
        assert !text.isBlank() : "Note text must not be blank";
        this.text = text;
    }

    /**
     * Returns the text stored in this note.
     *
     * @return Note text.
     */
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}

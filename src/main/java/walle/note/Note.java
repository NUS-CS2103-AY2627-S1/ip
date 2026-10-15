package walle.note;

import java.util.Objects;

/**
 * A small snippet of text the user wants to record, e.g. their own waist
 * size, or a movie title they want to remember.
 */
public class Note {

    private final String text;

    /**
     * Creates a note with the given text.
     *
     * @param text the note's text.
     */
    public Note(String text) {
        assert text != null && !text.isBlank() : "Note text should never be null/blank; "
                + "callers (Parser, Storage) are expected to have already rejected that input.";
        this.text = text;
    }

    /**
     * Returns this note's line format for the memory file, e.g. {@code N;note text}.
     *
     * @return the note's line format for the memory file.
     */
    public String getMemoryFormat() {
        return "N;" + text;
    }

    @Override
    public String toString() {
        return "[N] " + text;
    }

    /**
     * Two Notes are equal if they have the same text -- used to detect
     * duplicate notes being added.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Note)) {
            return false;
        }
        return text.equals(((Note) obj).text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

}

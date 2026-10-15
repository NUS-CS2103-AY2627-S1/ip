package benbot;

import java.util.Arrays;

/** Stores a short piece of textual information for later reference. */
final class Note implements StorableEntity {
    /** The syntax accepted when adding a note. */
    static final String USAGE = "note TEXT";

    /** The text recorded by the user. */
    private final String text;

    /** Creates a note containing the supplied text. */
    Note(String text) {
        assert text != null && !text.isBlank() : "Note text must be provided";
        this.text = text;
    }

    /** Creates a note from a valid note command. */
    static Note createFromCommand(String[] words) throws InvalidCommandException {
        if (words.length < 2) {
            throw new InvalidCommandException("Use: " + USAGE);
        }
        return new Note(String.join(" ", Arrays.copyOfRange(words, 1, words.length)));
    }

    /** Returns the note in BenBot's display format. */
    @Override
    public String toString() {
        return "[N] " + text;
    }

    /** Returns the command used to recreate this note from stored data. */
    @Override
    public String toStorageString() {
        return "note " + text;
    }
}

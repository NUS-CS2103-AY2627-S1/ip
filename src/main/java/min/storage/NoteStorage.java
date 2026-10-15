package min.storage;

import java.nio.file.Path;

import min.note.Note;

/**
 * Saves and loads Min notes on the hard disk.
 */
public class NoteStorage extends Storage<Note> {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "notes.txt");
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private static final String NOTE_TYPE_VALUE = "N";
    private static final String DAMAGED_RECORD_MESSAGE = "Damaged note record.";
    private static final String UNKNOWN_TYPE_MESSAGE = "Unknown note type.";
    private static final String BLANK_TEXT_MESSAGE = "Note text cannot be blank.";
    private static final int TYPE_FIELD_INDEX = 0;
    private static final int TEXT_FIELD_INDEX = 1;
    private static final int FIELD_COUNT = 2;

    /** Creates storage that uses Min's default note data file. */
    public NoteStorage() {
        this(DEFAULT_FILE_PATH);
    }

    NoteStorage(Path filePath) {
        super(filePath);
    }

    @Override
    protected String encode(Note note) {
        return note.toFileString();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If the line has an invalid field count, type, or text.
     */
    @Override
    protected Note decode(String line) {
        String[] fields = line.split(FIELD_SEPARATOR_REGEX, -1);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException(DAMAGED_RECORD_MESSAGE);
        }
        if (!fields[TYPE_FIELD_INDEX].equals(NOTE_TYPE_VALUE)) {
            throw new IllegalArgumentException(UNKNOWN_TYPE_MESSAGE);
        }
        if (fields[TEXT_FIELD_INDEX].isBlank()) {
            throw new IllegalArgumentException(BLANK_TEXT_MESSAGE);
        }

        return new Note(fields[TEXT_FIELD_INDEX]);
    }
}

package samantha.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import samantha.exception.CorruptedNoteFileException;
import samantha.exception.NoteFileReadException;
import samantha.exception.NoteFileWriteException;
import samantha.exception.TaskValidationException;
import samantha.model.Note;

/**
 * Handles persistence of Samantha's notes.
 */
public class NoteStorage {
    private static final Path DEFAULT_FILE = Path.of("data", "notes.txt");
    private final Path file;

    /**
     * Creates note storage using Samantha's default note-file location.
     */
    public NoteStorage() {
        this(DEFAULT_FILE);
    }

    /**
     * Creates note storage backed by the supplied file.
     *
     * @param file location of the note data file
     */
    public NoteStorage(Path file) {
        assert file != null : "Note storage must have a data-file path";
        this.file = file;
    }

    /**
     * Loads all notes from the note data file.
     *
     * <p>A missing file represents a first run and therefore produces an empty
     * note list. Any existing malformed record is reported as a corrupted file.</p>
     *
     * @return the notes read from disk
     * @throws NoteFileReadException if the file cannot be read
     * @throws CorruptedNoteFileException if an existing record is malformed
     */
    public ArrayList<Note> load()
            throws NoteFileReadException, CorruptedNoteFileException {
        ArrayList<Note> notes = new ArrayList<>();
        if (!Files.exists(file)) {
            return notes;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new NoteFileReadException(e);
        }
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            try {
                notes.add(new Note(lines.get(lineNumber)));
            } catch (TaskValidationException e) {
                throw new CorruptedNoteFileException(lineNumber + 1, e);
            }
        }
        return notes;
    }

    /**
     * Saves the current notes, creating the relative data directory when necessary.
     *
     * @param notes notes to serialize
     * @throws NoteFileWriteException if the file cannot be created or written
     */
    public void save(List<Note> notes) throws NoteFileWriteException {
        assert notes != null : "Notes to save must not be null";
        assert notes.stream().noneMatch(note -> note == null)
                : "Notes to save must not contain null notes";
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.write(file, notes.stream()
                    .map(Note::toFileString)
                    .toList());
        } catch (IOException e) {
            throw new NoteFileWriteException(e);
        }
    }
}

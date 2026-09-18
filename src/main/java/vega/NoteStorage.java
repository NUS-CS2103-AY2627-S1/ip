package vega;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Saves Vega notes to, and loads them from, a separate text file. */
public class NoteStorage {
    private final Path filePath;

    /**
     * Creates note storage that uses the given file path.
     *
     * @param filePath Location of Vega's note data file.
     */
    public NoteStorage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads saved notes, or returns an empty list when no save file exists.
     *
     * @return Notes reconstructed from the data file.
     * @throws VegaException If the data file cannot be read.
     */
    public ArrayList<Note> loadNotes() throws VegaException {
        ArrayList<Note> notes = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return notes;
        }
        try {
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    notes.add(new Note(line));
                }
            }
            return notes;
        } catch (IOException e) {
            throw new VegaException("I could not load your saved notes.");
        }
    }

    /**
     * Saves all notes, creating the data directory when necessary.
     *
     * @param notes Notes to write to the data file.
     * @throws VegaException If the note data cannot be written.
     */
    public void saveNotes(List<Note> notes) throws VegaException {
        List<String> lines = notes.stream().map(Note::getText).toList();
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new VegaException("I could not save your notes.");
        }
    }
}

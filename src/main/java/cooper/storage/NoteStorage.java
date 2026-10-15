package cooper.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import cooper.exception.CooperException;
import cooper.note.Note;

/** Versioned UTF-8 note storage, isolated from task data and replaced atomically on save. */
public class NoteStorage {
    public static final String UNAVAILABLE = "I couldn't load your notes. Fix the notes file and restart me. "
            + "Your saved notes have not been changed.";
    public static final String SAVE_FAILED = "I couldn't save your notes. Your change has not been applied.";
    private static final String HEADER = "COOPER_NOTES_V1";
    private final Path path;

    /** Resolves notes beside the configured task file. */
    public NoteStorage(String taskFilePath) {
        path = Path.of(taskFilePath).toAbsolutePath().resolveSibling("notes.txt");
    }

    /** Loads the complete collection or fails without publishing partial data. */
    public List<Note> load() {
        try {
            if (Files.notExists(path)) {
                return List.of();
            }
            String data = Files.readString(path, StandardCharsets.UTF_8);
            String[] lines = data.split("\\r?\\n", -1);
            if (!lines[0].equals(HEADER)) {
                throw new CooperException(UNAVAILABLE);
            }
            List<Note> notes = new ArrayList<>();
            int end = lines.length;
            if (end > 1 && lines[end - 1].isEmpty()) {
                end--;
            }
            for (int i = 1; i < end; i++) {
                notes.add(decode(lines[i]));
            }
            return notes;
        } catch (IOException | CooperException | SecurityException e) {
            throw new CooperException(UNAVAILABLE);
        }
    }

    /** Decodes only escaped backslashes and pipes, rejecting noncanonical text. */
    private Note decode(String line) {
        if (!line.startsWith("N | ")) {
            throw new CooperException(UNAVAILABLE);
        }
        String encoded = line.substring(4);
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < encoded.length(); i++) {
            char character = encoded.charAt(i);
            if (character == '\\') {
                if (++i == encoded.length()) {
                    throw new CooperException(UNAVAILABLE);
                }
                character = encoded.charAt(i);
                if (character != '\\' && character != '|') {
                    throw new CooperException(UNAVAILABLE);
                }
            } else if (character == '|') {
                throw new CooperException(UNAVAILABLE);
            }
            text.append(character);
        }
        Note note = new Note(text.toString());
        if (!note.text().equals(text.toString())) {
            throw new CooperException(UNAVAILABLE);
        }
        return note;
    }

    /** Writes a candidate to a sibling temporary file, with no non-atomic fallback. */
    public void save(List<Note> notes) {
        Path temporary = null;
        try {
            Files.createDirectories(path.getParent());
            temporary = Files.createTempFile(path.getParent(), "notes-", ".tmp");
            StringBuilder data = new StringBuilder(HEADER).append('\n');
            for (Note note : notes) {
                data.append("N | ").append(note.text().replace("\\", "\\\\").replace("|", "\\|"))
                        .append('\n');
            }
            Files.writeString(temporary, data, StandardCharsets.UTF_8);
            replaceFile(temporary, path);
        } catch (IOException | SecurityException e) {
            throw new CooperException(SAVE_FAILED);
        } finally {
            cleanup(temporary);
        }
    }

    /** Atomic replacement boundary, overridable to exercise filesystem failures in tests. */
    protected void replaceFile(Path temporary, Path destination) throws IOException {
        Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    /** Best-effort cleanup must not turn a successful replacement into a reported failure. */
    private void cleanup(Path temporary) {
        if (temporary == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporary);
        } catch (IOException | SecurityException ignored) {
            // A leftover temporary file is never read as note data.
        }
    }
}

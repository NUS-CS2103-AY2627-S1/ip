package samantha.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import samantha.exception.CorruptedNoteFileException;
import samantha.exception.NoteFileReadException;
import samantha.exception.NoteFileWriteException;
import samantha.model.Note;

class NoteStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyNoteList() throws Exception {
        NoteStorage storage = new NoteStorage(temporaryDirectory.resolve("missing.txt"));

        assertEquals(List.of(), storage.load());
    }

    @Test
    void saveAndLoad_notesRoundTrip() throws Exception {
        Path file = temporaryDirectory.resolve("nested/notes.txt");
        NoteStorage storage = new NoteStorage(file);

        storage.save(List.of(new Note("waist size: 32 inches"), new Note("watch Inception")));

        assertEquals(List.of("waist size: 32 inches", "watch Inception"), Files.readAllLines(file));
        assertEquals("waist size: 32 inches", storage.load().get(0).getContent());
        assertEquals("watch Inception", storage.load().get(1).getContent());
    }

    @Test
    void load_blankRecord_corruptedNoteFileExceptionThrown() throws IOException {
        Path file = temporaryDirectory.resolve("notes.txt");
        Files.writeString(file, "\n");

        CorruptedNoteFileException exception = assertThrows(CorruptedNoteFileException.class, () ->
                new NoteStorage(file).load());

        assertEquals(1, exception.getLineNumber());
    }

    @Test
    void load_directoryInsteadOfFile_noteFileReadExceptionThrown() throws IOException {
        Path directory = Files.createDirectory(temporaryDirectory.resolve("note-directory"));

        assertThrows(NoteFileReadException.class, () -> new NoteStorage(directory).load());
    }

    @Test
    void save_directoryInsteadOfFile_noteFileWriteExceptionThrown() throws IOException {
        Path directory = Files.createDirectory(temporaryDirectory.resolve("note-directory"));

        assertThrows(NoteFileWriteException.class, () ->
                new NoteStorage(directory).save(List.of(new Note("note"))));
    }
}

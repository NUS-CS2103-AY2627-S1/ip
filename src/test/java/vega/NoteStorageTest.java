package vega;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class NoteStorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoadNotes_multipleNotes_preservesTextAndOrder() throws VegaException {
        NoteStorage storage = new NoteStorage(temporaryDirectory.resolve("notes.txt").toString());
        storage.saveNotes(List.of(new Note("first note"), new Note("second note")));

        List<Note> loadedNotes = storage.loadNotes();

        assertEquals(2, loadedNotes.size());
        assertEquals("first note", loadedNotes.get(0).getText());
        assertEquals("second note", loadedNotes.get(1).getText());
    }
}

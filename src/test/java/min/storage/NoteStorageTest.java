package min.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import min.note.Note;

class NoteStorageTest {
    private Path dataFile;
    private NoteStorage storage;

    @BeforeEach
    void setUp(@TempDir Path tempDirectory) {
        dataFile = tempDirectory.resolve("data/notes.txt");
        storage = new NoteStorage(dataFile);
    }

    @Test
    void load_missingDataFile_returnsEmptyList() throws IOException {
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_severalNotes_preservesTextAndOrder() throws IOException {
        storage.save(List.of(new Note("watch Dune"), new Note("read chapter 4")));

        List<Note> loadedNotes = storage.load();

        assertEquals(2, loadedNotes.size());
        assertEquals(List.of("watch Dune", "read chapter 4"),
                loadedNotes.stream().map(Note::getText).toList());
    }

    @Test
    void save_writesTypedRecords() throws IOException {
        storage.save(List.of(new Note("watch Dune")));

        assertEquals(List.of("N | watch Dune"), Files.readAllLines(dataFile));
    }

    @Test
    void save_existingData_replacesPreviousContents() throws IOException {
        storage.save(List.of(new Note("old note"), new Note("another old note")));

        storage.save(List.of(new Note("replacement note")));

        List<Note> loadedNotes = storage.load();
        assertEquals(1, loadedNotes.size());
        assertEquals("replacement note", loadedNotes.get(0).getText());
    }

    @Test
    void load_unknownNoteType_throwsIllegalArgumentException() throws IOException {
        writeDataFile("X | watch Dune");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Unknown note type.", exception.getMessage());
    }

    @Test
    void load_missingTextField_throwsIllegalArgumentException() throws IOException {
        writeDataFile("N");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Damaged note record.", exception.getMessage());
    }

    @Test
    void load_blankText_throwsIllegalArgumentException() throws IOException {
        writeDataFile("N |    ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Note text cannot be blank.", exception.getMessage());
    }

    @Test
    void load_extraField_throwsIllegalArgumentException() throws IOException {
        writeDataFile("N | watch | Dune");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Damaged note record.", exception.getMessage());
    }

    @Test
    void load_blankLines_ignoresThemAndPreservesNoteOrder() throws IOException {
        writeDataFile("N | watch Dune\n\n   \nN | read chapter 4");

        List<Note> loadedNotes = storage.load();

        assertEquals(List.of("watch Dune", "read chapter 4"),
                loadedNotes.stream().map(Note::getText).toList());
    }

    private void writeDataFile(String contents) throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, contents);
    }
}

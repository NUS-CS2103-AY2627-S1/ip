package cooper.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cooper.exception.CooperException;
import cooper.note.Note;

/** Verifies strict versioned storage and preservation of the last successful save. */
public class NoteStorageTest {
    @TempDir
    private Path directory;

    @Test
    public void saveAndLoad_roundTrip_preservesLiteralTextAndExactEncoding() throws IOException {
        NoteStorage storage = new NoteStorage(directory.resolve("cooper.txt").toString());
        List<Note> notes = List.of(new Note("Folder C:\\films | watch Arrival"), new Note("中文 😀  \t\"film\""));
        storage.save(notes);
        assertEquals("COOPER_NOTES_V1\nN | Folder C:\\\\films \\| watch Arrival\nN | 中文 😀  \t\"film\"\n",
                Files.readString(directory.resolve("notes.txt")));
        assertEquals(notes, storage.load());
        storage.save(List.of());
        assertEquals("COOPER_NOTES_V1\n", Files.readString(directory.resolve("notes.txt")));
        assertEquals(List.of(), storage.load());
    }

    @Test
    public void load_missingFile_isEmptyWithoutCreatingFile() {
        NoteStorage storage = new NoteStorage(directory.resolve("nested/cooper.txt").toString());
        assertEquals(List.of(), storage.load());
        assertFalse(Files.exists(directory.resolve("nested")));
    }

    @Test
    public void load_crlf_acceptsWindowsLineEndings() throws IOException {
        Files.writeString(directory.resolve("notes.txt"), "COOPER_NOTES_V1\r\nN | first\r\nN | second\r\n");
        assertEquals(List.of(new Note("first"), new Note("second")),
                new NoteStorage(directory.resolve("cooper.txt").toString()).load());
    }

    @Test
    public void load_unreadableFile_reportsUnavailable() throws IOException {
        Files.createDirectory(directory.resolve("notes.txt"));
        NoteStorage storage = new NoteStorage(directory.resolve("cooper.txt").toString());
        assertEquals(NoteStorage.UNAVAILABLE, assertThrows(CooperException.class, storage::load).getMessage());
    }

    @Test
    public void save_parentIsFile_reportsFailureWithoutOverwritingIt() throws IOException {
        Path blockedParent = directory.resolve("blocked");
        Files.writeString(blockedParent, "keep this file");
        NoteStorage storage = new NoteStorage(blockedParent.resolve("cooper.txt").toString());
        assertEquals(NoteStorage.SAVE_FAILED, assertThrows(CooperException.class, () ->
                storage.save(List.of(new Note("new")))).getMessage());
        assertEquals("keep this file", Files.readString(blockedParent));
    }

    @Test
    public void load_malformedFile_rejectsWithoutChangingBytes() throws IOException {
        Path path = directory.resolve("notes.txt");
        NoteStorage storage = new NoteStorage(directory.resolve("cooper.txt").toString());
        for (String data : new String[] {"", "COOPER_NOTES_V2\n", "N | old\n", "COOPER_NOTES_V1\n\n",
            "COOPER_NOTES_V1\nX | text\n", "COOPER_NOTES_V1\nN | \n", "COOPER_NOTES_V1\nN |  text\n",
            "COOPER_NOTES_V1\nN | text \n", "COOPER_NOTES_V1\nN | bad\\q\n",
            "COOPER_NOTES_V1\nN | bad\\\n", "COOPER_NOTES_V1\nN | bad|pipe\n",
            "COOPER_NOTES_V1\nN | first\nN | bad\u2028line\n"}) {
            Files.writeString(path, data);
            assertEquals(NoteStorage.UNAVAILABLE, assertThrows(CooperException.class, storage::load).getMessage());
            assertEquals(data, Files.readString(path));
        }
        Files.write(path, new byte[] {(byte) 0xc3, (byte) 0x28});
        assertThrows(CooperException.class, storage::load);
    }

    @Test
    public void save_atomicMoveUnsupported_preservesFileAndCleansTemporaryFile() throws IOException {
        String taskPath = directory.resolve("cooper.txt").toString();
        new NoteStorage(taskPath).save(List.of(new Note("original")));
        String original = Files.readString(directory.resolve("notes.txt"));
        NoteStorage failing = new NoteStorage(taskPath) {
            @Override
            protected void replaceFile(Path temporary, Path destination) throws IOException {
                throw new AtomicMoveNotSupportedException(temporary.toString(), destination.toString(), "test");
            }
        };
        assertEquals(NoteStorage.SAVE_FAILED, assertThrows(CooperException.class, () ->
                failing.save(List.of(new Note("replacement")))).getMessage());
        assertEquals(original, Files.readString(directory.resolve("notes.txt")));
        try (var paths = Files.list(directory)) {
            assertEquals(List.of("notes.txt"), paths.map(path -> path.getFileName().toString()).toList());
        }
    }
}

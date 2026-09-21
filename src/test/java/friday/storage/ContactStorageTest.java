package friday.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import friday.contact.Contact;

/** Tests contact snapshots, malformed data, and failure integrity in isolated directories. */
class ContactStorageTest {
    @TempDir
    Path directory;

    @Test
    void load_missingOrEmptyFile_returnsEmptyWithoutCreatingMissingParents() throws IOException {
        Path file = directory.resolve("nested/contacts.txt");
        ContactStorage storage = new ContactStorage(file);
        assertEquals(List.of(), storage.load());
        assertFalse(Files.exists(file.getParent()));
        Files.createDirectories(file.getParent());
        Files.writeString(file, "");
        assertEquals(List.of(), storage.load());
    }

    @Test
    void saveAndLoad_optionalFieldsAndEscapes_preservesSpecifiedRecords() throws IOException {
        Path file = directory.resolve("nested/contacts.txt");
        ContactStorage storage = new ContactStorage(file);
        storage.save(List.of(new Contact("Alice | \\ 陈", "91234567", "Alice@example.com"),
                new Contact("Bob", "", "bob@example.com"), new Contact("Carol", "81234567", "")));
        assertEquals(List.of("C|Alice \\| \\\\ 陈|91234567|Alice@example.com", "C|Bob||bob@example.com",
                "C|Carol|81234567|"), Files.readAllLines(file));
        List<Contact> loaded = storage.load();
        assertEquals(List.of("Alice | \\ 陈", "Bob", "Carol"), loaded.stream().map(Contact::getName).toList());
        assertEquals(List.of("91234567", "", "81234567"), loaded.stream().map(Contact::getPhone).toList());
        assertEquals(List.of("Alice@example.com", "bob@example.com", ""),
                loaded.stream().map(Contact::getEmail).toList());
        storage.save(List.of(loaded.get(1)));
        assertEquals(List.of("C|Bob||bob@example.com"), Files.readAllLines(file));
        storage.save(List.of());
        assertEquals("", Files.readString(file));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "T|Name|91234567|", "C|Name|91234567", "C|Name|91234567||extra",
            "C||91234567|", "C|Name||", "C|Name|12345678|", "C|Name||bad-email",
            "C|Name\\q|91234567|", "C|Name|91234567|trailing\\", "C|Name\t|91234567|",
            "C| ALICE |81234567|", "C|Name\\n|91234567|"})
    void load_invalidSecondRecord_rejectsWholeFileWithoutChangingBytes(String invalid) throws IOException {
        Path file = directory.resolve("contacts.txt");
        Files.writeString(file, "C|Alice|91234567|\n" + invalid + "\n");
        byte[] before = Files.readAllBytes(file);
        IOException error = assertThrows(IOException.class, () -> new ContactStorage(file).load());
        assertEquals("Invalid contact record on line 2.", error.getMessage());
        assertArrayEquals(before, Files.readAllBytes(file));
    }

    @Test
    void load_invalidUtf8_preservesOriginalBytes() throws IOException {
        Path file = directory.resolve("contacts.txt");
        byte[] bytes = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, bytes);
        assertThrows(IOException.class, () -> new ContactStorage(file).load());
        assertArrayEquals(bytes, Files.readAllBytes(file));
    }

    @Test
    void save_duplicateNames_preservesPreviousSnapshot() throws IOException {
        Path file = directory.resolve("contacts.txt");
        ContactStorage storage = new ContactStorage(file);
        Contact alice = new Contact("Alice", "91234567", "");
        storage.save(List.of(alice));
        byte[] before = Files.readAllBytes(file);
        assertThrows(IllegalArgumentException.class,
                () -> storage.save(List.of(alice, new Contact("ALICE", "81234567", ""))));
        assertArrayEquals(before, Files.readAllBytes(file));
        assertOnlyContactPathRemains();
    }

    @Test
    void save_nonemptyDestinationDirectory_preservesContentsCleansTemporaryFileAndAllowsRetry() throws IOException {
        Path file = directory.resolve("contacts.txt");
        Files.createDirectory(file);
        Path blocker = file.resolve("keep.txt");
        Files.writeString(blocker, "unchanged");
        ContactStorage storage = new ContactStorage(file);
        assertThrows(IOException.class, storage::load);
        assertThrows(IOException.class, () -> storage.save(List.of(new Contact("Alice", "91234567", ""))));
        assertEquals("unchanged", Files.readString(blocker));
        assertOnlyContactPathRemains();
        Files.delete(blocker);
        Files.delete(file);
        storage.save(List.of(new Contact("Bob", "", "bob@example.com")));
        assertEquals(List.of("C|Bob||bob@example.com"), Files.readAllLines(file));
        assertOnlyContactPathRemains();
    }

    @Test
    void save_parentIsFile_doesNotOverwriteIt() throws IOException {
        Path parent = directory.resolve("parent");
        Files.writeString(parent, "unchanged");
        ContactStorage storage = new ContactStorage(parent.resolve("contacts.txt"));
        assertThrows(IOException.class, () -> storage.save(List.of(new Contact("Alice", "91234567", ""))));
        assertEquals("unchanged", Files.readString(parent));
    }

    @Test
    void save_contactChanges_leaveTaskFileUntouched() throws IOException {
        Path tasks = directory.resolve("friday.txt");
        Files.writeString(tasks, "T|1|keep my task\n");
        byte[] before = Files.readAllBytes(tasks);
        ContactStorage storage = new ContactStorage(directory.resolve("contacts.txt"));
        storage.save(List.of(new Contact("Alice", "91234567", "")));
        assertEquals(1, storage.load().size());
        storage.save(List.of());
        assertArrayEquals(before, Files.readAllBytes(tasks));
        assertTrue(Files.exists(tasks));
    }

    /** Confirms that failed writes leave no temporary snapshots in the destination directory. */
    private void assertOnlyContactPathRemains() throws IOException {
        try (var files = Files.list(directory)) {
            assertEquals(List.of("contacts.txt"), files.map(path -> path.getFileName().toString()).toList());
        }
    }
}

package atlas.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import atlas.AtlasException;
import atlas.client.Client;
import atlas.task.Deadline;
import atlas.task.Event;
import atlas.task.Priority;
import atlas.task.Task;
import atlas.task.Todo;

/** Tests persistence of task and client data and recovery from malformed storage lines. */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    /**
     * Saves a task list that has no clients, for tests that only cover tasks.
     *
     * @param storage storage to write with.
     * @param tasks tasks to save.
     * @throws AtlasException if the file cannot be written.
     */
    private void saveTasks(Storage storage, ArrayList<Task> tasks) throws AtlasException {
        storage.save(tasks, new ArrayList<>());
    }

    @Test
    void missingFileLoadsAsAnEmptyList() throws AtlasException {
        Storage storage = new Storage(temporaryDirectory.resolve("atlas.txt").toString());

        AtlasData data = storage.load();

        assertTrue(data.getTasks().isEmpty());
        assertTrue(data.getClients().isEmpty());
    }

    @Test
    void savesAndLoadsAllTaskTypesAndTheirCompletionState() throws AtlasException {
        Path storagePath = temporaryDirectory.resolve("data/atlas.txt");
        Storage storage = new Storage(storagePath.toString());
        Todo todo = new Todo("read the Odyssey");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 1));
        Event event = new Event("project meeting", "2pm", "4pm");
        ArrayList<Task> tasks = new ArrayList<>(List.of(todo, deadline, event));

        saveTasks(storage, tasks);
        ArrayList<Task> loaded = storage.load().getTasks();

        assertEquals(3, loaded.size());
        assertEquals("read the Odyssey", loaded.get(0).getDescription());
        assertTrue(loaded.get(0).isDone());
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loaded.get(1));
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 1), loadedDeadline.getBy());
        Event loadedEvent = assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals("2pm", loadedEvent.getFrom());
        assertEquals("4pm", loadedEvent.getTo());
        assertFalse(loadedEvent.isDone());
    }

    @Test
    void preservesSpecialCharactersDuringRoundTrip() throws AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Storage storage = new Storage(storagePath.toString());
        Event event = new Event("review | notes \\ soon", "Mon | 2pm \\ start", "Mon 4pm | end \\");

        saveTasks(storage, new ArrayList<>(List.of(event)));
        Event loaded = assertInstanceOf(Event.class, storage.load().getTasks().get(0));

        assertEquals("review | notes \\ soon", loaded.getDescription());
        assertEquals("Mon | 2pm \\ start", loaded.getFrom());
        assertEquals("Mon 4pm | end \\", loaded.getTo());
    }

    @Test
    void skipsMalformedLinesAndLoadsTheValidLines() throws IOException, AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Files.writeString(storagePath, String.join(System.lineSeparator(),
                "T | 0 | keep",
                "not a valid task",
                "E | 1 | meeting | 10am | 11am"));

        ArrayList<Task> loaded = new Storage(storagePath.toString()).load().getTasks();

        assertEquals(2, loaded.size());
        assertEquals("keep", loaded.get(0).getDescription());
        Event event = assertInstanceOf(Event.class, loaded.get(1));
        assertTrue(event.isDone());
        assertEquals("meeting", event.getDescription());
    }

    @Test
    void savesAndLoadsClientsAlongsideTasks() throws AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Storage storage = new Storage(storagePath.toString());
        ArrayList<Task> tasks = new ArrayList<>(List.of(new Todo("buy milk")));
        ArrayList<Client> clients = new ArrayList<>(List.of(
                new Client("Bob", "91234567", ""),
                new Client("Chen Wei", "", "wei@example.com"),
                new Client("Dara")));

        storage.save(tasks, clients);
        AtlasData loaded = storage.load();

        assertEquals(1, loaded.getTasks().size());
        assertEquals("buy milk", loaded.getTasks().get(0).getDescription());
        assertEquals(3, loaded.getClients().size());
        assertEquals("Bob", loaded.getClients().get(0).getName());
        assertEquals("91234567", loaded.getClients().get(0).getPhone());
        assertEquals("", loaded.getClients().get(0).getEmail());
        assertEquals("wei@example.com", loaded.getClients().get(1).getEmail());
        assertEquals("", loaded.getClients().get(2).getPhone());
    }

    @Test
    void preservesClientDelimitersDuringRoundTrip() throws AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Storage storage = new Storage(storagePath.toString());
        Client client = new Client("Odd|Name", "91\\23", "odd|\\@example.com");

        storage.save(new ArrayList<>(), new ArrayList<>(List.of(client)));
        Client loaded = storage.load().getClients().get(0);

        assertEquals("Odd|Name", loaded.getName());
        assertEquals("91\\23", loaded.getPhone());
        assertEquals("odd|\\@example.com", loaded.getEmail());
    }

    @Test
    void skipsMalformedClientLinesAndKeepsValidOnes() throws IOException, AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Files.writeString(storagePath, String.join(System.lineSeparator(),
                "C | keep me | 91234567 | ",
                "C |  |  | ",
                "C | too | many | fields | here",
                "T | 0 | keep this task"));

        AtlasData loaded = new Storage(storagePath.toString()).load();

        assertEquals(1, loaded.getClients().size());
        assertEquals("keep me", loaded.getClients().get(0).getName());
        assertEquals(1, loaded.getTasks().size());
        assertEquals("keep this task", loaded.getTasks().get(0).getDescription());
    }

    @Test
    void loadsHandWrittenClientRecordsWithTwoOrThreeFields() throws IOException, AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Files.writeString(storagePath, String.join(System.lineSeparator(),
                "C | name only",
                "C | name and phone | 91234567",
                "C | full record | 91234567 | full@example.com"));

        ArrayList<Client> loaded = new Storage(storagePath.toString()).load().getClients();

        assertEquals(3, loaded.size());
        assertEquals("name only", loaded.get(0).getName());
        assertEquals("", loaded.get(0).getPhone());
        assertEquals("", loaded.get(0).getEmail());
        assertEquals("name and phone", loaded.get(1).getName());
        assertEquals("91234567", loaded.get(1).getPhone());
        assertEquals("", loaded.get(1).getEmail());
        assertEquals("full@example.com", loaded.get(2).getEmail());
    }

    @Test
    void writesTasksBeforeClientsInTheDocumentedLayout() throws IOException, AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Storage storage = new Storage(storagePath.toString());

        storage.save(new ArrayList<>(List.of(new Todo("buy milk"))),
                new ArrayList<>(List.of(new Client("Bob", "91234567", "bob@example.com"))));

        assertEquals(String.join(System.lineSeparator(),
                "T | 0 | buy milk",
                "C | Bob | 91234567 | bob@example.com") + System.lineSeparator(),
                Files.readString(storagePath));
    }

    @Test
    void savesAndLoadsPrioritiesForEveryTaskType() throws AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Storage storage = new Storage(storagePath.toString());
        Todo todo = new Todo("read the Odyssey");
        todo.setPriority(Priority.HIGH);
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 1));
        deadline.setPriority(Priority.LOW);
        Event event = new Event("project meeting", "2pm", "4pm");
        event.setPriority(Priority.MEDIUM);

        saveTasks(storage, new ArrayList<>(List.of(todo, deadline, event)));
        ArrayList<Task> loaded = storage.load().getTasks();

        assertEquals(3, loaded.size());
        assertEquals(Priority.HIGH, loaded.get(0).getPriority());
        assertEquals(Priority.LOW, loaded.get(1).getPriority());
        assertEquals(Priority.MEDIUM, loaded.get(2).getPriority());
        assertEquals("[T][ ][HIGH] read the Odyssey", loaded.get(0).toString());
    }

    @Test
    void writesThePriorityFieldOnlyWhenATaskHasOne() throws IOException, AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Storage storage = new Storage(storagePath.toString());
        Todo ranked = new Todo("ranked");
        ranked.setPriority(Priority.LOW);

        saveTasks(storage, new ArrayList<>(List.of(ranked, new Todo("plain"))));

        assertEquals(String.join(System.lineSeparator(),
                "T | 0 | ranked | low",
                "T | 0 | plain") + System.lineSeparator(),
                Files.readString(storagePath));
    }

    @Test
    void loadsHandWrittenTaskRecordsWithAndWithoutAPriorityField() throws IOException, AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Files.writeString(storagePath, String.join(System.lineSeparator(),
                "T | 0 | no priority",
                "T | 0 | ranked | low",
                "D | 1 | due | 2026-09-01 | medium",
                "E | 0 | meeting | 2pm | 4pm | high",
                "T | 0 | empty field | ",
                "T | 0 | bad word | urgent"));

        ArrayList<Task> loaded = new Storage(storagePath.toString()).load().getTasks();

        assertEquals(5, loaded.size());
        assertNull(loaded.get(0).getPriority());
        assertEquals(Priority.LOW, loaded.get(1).getPriority());
        assertEquals(Priority.MEDIUM, loaded.get(2).getPriority());
        assertTrue(loaded.get(2).isDone());
        assertEquals(Priority.HIGH, loaded.get(3).getPriority());
        assertNull(loaded.get(4).getPriority());
    }

    @Test
    void skipsTaskRecordsThatHoldMoreFieldsThanTheFormatAllows() throws IOException, AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Files.writeString(storagePath, String.join(System.lineSeparator(),
                "T | 0 | keep todo",
                "T | 0 | too many | high | junk",
                "D | 0 | keep deadline | 2026-09-01",
                "D | 0 | too many | 2026-09-01 | high | junk",
                "E | 0 | keep event | 1pm | 2pm",
                "E | 0 | too many | 1pm | 2pm | high | junk"));

        ArrayList<Task> loaded = new Storage(storagePath.toString()).load().getTasks();

        assertEquals(3, loaded.size());
        assertEquals("keep todo", loaded.get(0).getDescription());
        assertEquals("keep deadline", loaded.get(1).getDescription());
        assertEquals("keep event", loaded.get(2).getDescription());
    }

    /**
     * Returns the single backup file a test expects to find in the temporary
     * directory.
     *
     * @return path of the backup file.
     * @throws IOException if the directory cannot be listed.
     */
    private Path soleBackupFile() throws IOException {
        try (Stream<Path> entries = Files.list(temporaryDirectory)) {
            List<Path> backups = entries
                    .filter(path -> path.getFileName().toString().contains(".corrupted-"))
                    .toList();
            assertEquals(1, backups.size());
            return backups.get(0);
        }
    }

    @Test
    void describesSkippedRecordsAndKeepsTheFileBeforeTheNextSave() throws IOException, AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        String original = String.join(System.lineSeparator(),
                "T | 0 | keep",
                "not a valid task",
                "C |  |  | ") + System.lineSeparator();
        Files.writeString(storagePath, original);
        Storage storage = new Storage(storagePath.toString());

        AtlasData loaded = storage.load();

        assertEquals(1, loaded.getTasks().size());
        assertEquals("keep", loaded.getTasks().get(0).getDescription());
        assertEquals(3, loaded.getWarnings().size());
        assertTrue(loaded.getWarnings().get(0).startsWith("Atlas could not read 2 records from the data file"));
        assertEquals("Line 2: too few fields", loaded.getWarnings().get(1));
        assertEquals("Line 3: client name is empty", loaded.getWarnings().get(2));

        storage.save(new ArrayList<>(), new ArrayList<>());

        Path backup = soleBackupFile();
        assertTrue(loaded.getWarnings().get(0).contains(backup.getFileName().toString()));
        assertEquals(original, Files.readString(backup));
        assertEquals("", Files.readString(storagePath));
    }

    @Test
    void keepsAnUndecodableFileBeforeTheSaveThatReplacesIt() throws IOException, AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        byte[] undecodable = {(byte) 0xFF, (byte) 0xFE, 'T', ' ', '|'};
        Files.write(storagePath, undecodable);
        Storage storage = new Storage(storagePath.toString());

        AtlasException exception = assertThrows(AtlasException.class, storage::load);

        assertTrue(exception.getMessage().startsWith("The scroll of tasks could not be read:"));

        storage.save(new ArrayList<>(List.of(new Todo("survivor"))), new ArrayList<>());

        Path backup = soleBackupFile();
        assertTrue(exception.getMessage().contains(backup.getFileName().toString()));
        assertArrayEquals(undecodable, Files.readAllBytes(backup));
        assertEquals(String.join(System.lineSeparator(), "T | 0 | survivor") + System.lineSeparator(),
                Files.readString(storagePath));
    }

    @Test
    void refusesToSaveWhenTheBackupCannotBeWritten() throws IOException, AtlasException {
        // A folder that cannot be written to is how the copy is made to fail,
        // and that is a POSIX idea: on a platform without POSIX permissions the
        // situation cannot arise, so the test reports itself as skipped there
        // rather than failing the build.
        assumeTrue(Files.getFileStore(temporaryDirectory).supportsFileAttributeView("posix"),
                "this platform has no POSIX file permissions to deny");
        Path dataDirectory = Files.createDirectories(temporaryDirectory.resolve("data"));
        Path storagePath = dataDirectory.resolve("atlas.txt");
        String original = "C |  |  | " + System.lineSeparator();
        Files.writeString(storagePath, original);
        Storage storage = new Storage(storagePath.toString());
        AtlasData loaded = storage.load();
        assertFalse(loaded.getWarnings().isEmpty());

        Files.setPosixFilePermissions(dataDirectory, PosixFilePermissions.fromString("r-x------"));
        try {
            AtlasException exception = assertThrows(AtlasException.class, () ->
                    storage.save(new ArrayList<>(List.of(new Todo("survivor"))), new ArrayList<>()));

            assertTrue(exception.getMessage().startsWith("Atlas could not keep a copy of the data file"));
            assertEquals(original, Files.readString(storagePath));
        } finally {
            Files.setPosixFilePermissions(dataDirectory, PosixFilePermissions.fromString("rwx------"));
        }
    }

    @Test
    void roundTripsARankedTaskWhoseDescriptionHoldsDelimiters() throws AtlasException {
        Path storagePath = temporaryDirectory.resolve("atlas.txt");
        Storage storage = new Storage(storagePath.toString());
        Todo ranked = new Todo("buy | milk \\ now");
        ranked.setPriority(Priority.MEDIUM);

        saveTasks(storage, new ArrayList<>(List.of(ranked)));
        ArrayList<Task> loaded = storage.load().getTasks();

        assertEquals(1, loaded.size());
        assertEquals("buy | milk \\ now", loaded.get(0).getDescription());
        assertEquals(Priority.MEDIUM, loaded.get(0).getPriority());
    }
}

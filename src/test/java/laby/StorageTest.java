package laby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import laby.contact.Contact;
import laby.contact.ContactList;
import laby.task.Task;
import laby.task.TaskList;

/** Tests persistence of tasks and rejection of malformed storage data. */
class StorageTest {

    private static final LocalDateTime DEADLINE = LocalDateTime.of(2026, 8, 23, 10, 0);
    private static final LocalDateTime EVENT_START = LocalDateTime.of(2026, 8, 23, 11, 0);
    private static final LocalDateTime EVENT_END = LocalDateTime.of(2026, 8, 23, 12, 0);

    @Test
    void readTasksFromFile_missingFile_createsEmptyFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("nested").resolve("laby.txt");

        List<Task> tasks = new Storage(file.toString()).readTasks();

        assertEquals(0, tasks.size());
        assertFalse(Files.notExists(file));
    }

    @Test
    void writeTasksToFile_thenReadTasksFromFile_roundTripsAllTaskTypes(@TempDir Path tempDir)
            throws Exception {
        Path file = tempDir.resolve("laby.txt");
        ContactList contactList = new ContactList(new java.util.ArrayList<>());
        TaskList taskList = new TaskList(new java.util.ArrayList<>());
        taskList.addTodo("read book");
        taskList.addDeadline("return book", DEADLINE);
        taskList.addEvent("project meeting", EVENT_START, EVENT_END);
        taskList.modifyTaskStatus(0, true);

        Storage storage = new Storage(file.toString());
        storage.writeFile(taskList, contactList);
        List<Task> tasks = storage.readTasks();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Aug 23 2026 10:00)", tasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Aug 23 2026 11:00 to: Aug 23 2026 12:00)",
                tasks.get(2).toString());
    }

    @Test
    void writeTasksAndContactsToFile_thenReadBothLists_roundTripsData(@TempDir Path tempDir)
            throws Exception {
        Path file = tempDir.resolve("laby.txt");
        TaskList taskList = new TaskList(new java.util.ArrayList<>());
        taskList.addTodo("read book");
        ContactList contactList = new ContactList(new java.util.ArrayList<>());
        contactList.addContact("John Doe", "91234567", "john@example.com");

        Storage storage = new Storage(file.toString());
        storage.writeFile(taskList, contactList);

        List<Task> tasks = storage.readTasks();
        List<Contact> contacts = storage.readContacts();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.getFirst().toString());
        assertEquals(1, contacts.size());
        assertEquals("John Doe | Phone: 91234567 | Email: john@example.com",
                contacts.getFirst().toString());
    }

    @Test
    void readContactsFromFile_malformedContactRecord_exceptionThrown(@TempDir Path tempDir)
            throws Exception {
        Path file = tempDir.resolve("laby.txt");
        Files.writeString(file, "C|John Doe|91234567\n");

        LabyException exception = assertThrows(LabyException.class,
                () -> new Storage(file.toString()).readContacts());

        assertEquals("invalid file format.", exception.getMessage());
    }

    @Test
    void readTasksFromFile_unknownTaskType_exceptionThrown(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("laby.txt");
        Files.writeString(file, "X" + Parser.FIELD_SEPARATOR + "0" + Parser.FIELD_SEPARATOR + "unknown\n");

        LabyException exception = assertThrows(LabyException.class,
                () -> new Storage(file.toString()).readTasks());

        assertEquals("invalid file format.", exception.getMessage());
    }

    @Test
    void readTasksFromFile_invalidCompletionFlag_exceptionThrown(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("laby.txt");
        Files.writeString(file, "T" + Parser.FIELD_SEPARATOR + "2"
                + Parser.FIELD_SEPARATOR + "read book\n");

        LabyException exception = assertThrows(LabyException.class,
                () -> new Storage(file.toString()).readTasks());

        assertEquals("invalid file format.", exception.getMessage());
    }

    @Test
    void readTasksFromFile_missingTaskFields_exceptionThrown(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("laby.txt");
        Files.writeString(file, "D" + Parser.FIELD_SEPARATOR + "0"
                + Parser.FIELD_SEPARATOR + "return book\n");

        LabyException exception = assertThrows(LabyException.class,
                () -> new Storage(file.toString()).readTasks());

        assertEquals("invalid file format.", exception.getMessage());
    }
}

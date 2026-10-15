package otaku.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import otaku.exception.OtakuException;
import otaku.task.Deadline;
import otaku.task.Event;
import otaku.task.Task;
import otaku.task.Todo;

/** Tests persistence using an isolated temporary directory. */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void load_missingFile_returnsEmptyTaskList() throws OtakuException {
        Storage storage = new Storage(tempDirectory.resolve("data").resolve("tasks.txt"));

        assertEquals(List.of(), storage.load());
    }

    @Test
    public void saveAndLoad_allTaskTypesAndStatuses_restoresEquivalentTasks() throws OtakuException {
        Path dataFile = tempDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read | revise 日本語");
        Deadline deadline = new Deadline("submit work", LocalDate.of(2026, 9, 1));
        Event event = new Event("orientation", LocalDate.of(2026, 8, 20), LocalDate.of(2026, 8, 21));
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event));
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("[T][ ] read | revise 日本語", loadedTasks.get(0).toString());
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("[D][X] submit work (by: Sep 01 2026)", loadedTasks.get(1).toString());
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("[E][ ] orientation (from: Aug 20 2026 to: Aug 21 2026)", loadedTasks.get(2).toString());
    }

    @Test
    public void save_existingFile_replacesPreviousContents() throws OtakuException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Todo("old task"), new Todo("another old task")));

        storage.save(List.of(new Todo("replacement task")));

        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertEquals("replacement task", loadedTasks.get(0).getDescription());
    }

    @Test
    public void load_blankLines_ignoresBlankLines() throws IOException, OtakuException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "\nT|0|cmVhZCBib29r\n   \n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
    }

    @Test
    public void load_malformedRecord_throwsExceptionWithLineNumber() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T|0|dmFsaWQ=", "invalid record"), StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        OtakuException exception = assertThrows(OtakuException.class, storage::load);

        assertEquals("Saved task data is invalid on line 2.", exception.getMessage());
    }

    @Test
    public void load_eventWithEqualDates_throwsExceptionWithLineNumber() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        String invalidEvent = "E|0|c3luYw==|MjAyNi0wOS0yMA==|MjAyNi0wOS0yMA==";
        Files.writeString(dataFile, invalidEvent, StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        OtakuException exception = assertThrows(OtakuException.class, storage::load);

        assertEquals("Saved task data is invalid on line 1.", exception.getMessage());
    }

    @Test
    public void load_invalidStatus_throwsExceptionWithLineNumber() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T|2|cmVhZCBib29r", StandardCharsets.UTF_8);

        OtakuException exception = assertThrows(OtakuException.class,
                () -> new Storage(dataFile).load());

        assertEquals("Saved task data is invalid on line 1.", exception.getMessage());
    }

    @Test
    public void load_invalidBase64AndDate_throwsException() throws IOException {
        Path invalidBase64File = tempDirectory.resolve("base64.txt");
        Path invalidDateFile = tempDirectory.resolve("date.txt");
        Files.writeString(invalidBase64File, "T|0|%%%", StandardCharsets.UTF_8);
        Files.writeString(invalidDateFile, "D|0|cmVwb3J0|bm90LWEtZGF0ZQ==", StandardCharsets.UTF_8);

        assertThrows(OtakuException.class, () -> new Storage(invalidBase64File).load());
        assertThrows(OtakuException.class, () -> new Storage(invalidDateFile).load());
    }

    @Test
    public void load_directoryInsteadOfFile_throwsReadableException() {
        Storage storage = new Storage(tempDirectory);

        OtakuException exception = assertThrows(OtakuException.class, storage::load);

        assertTrue(exception.getMessage().contains("couldn't read saved tasks"));
    }

    @Test
    public void save_parentPathIsFile_throwsWritableException() throws IOException {
        Path parentFile = tempDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "blocking file", StandardCharsets.UTF_8);
        Storage storage = new Storage(parentFile.resolve("tasks.txt"));

        OtakuException exception = assertThrows(OtakuException.class,
                () -> storage.save(List.of(new Todo("read"))));

        assertTrue(exception.getMessage().contains("couldn't save tasks"));
    }

    @Test
    public void save_unsupportedTaskType_throwsException() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt"));
        Task unsupportedTask = new Task("unsupported") {
            @Override
            protected String getTypeIcon() {
                return "U";
            }
        };

        OtakuException exception = assertThrows(OtakuException.class,
                () -> storage.save(List.of(unsupportedTask)));

        assertEquals("I couldn't save an unsupported task type.", exception.getMessage());
    }
}

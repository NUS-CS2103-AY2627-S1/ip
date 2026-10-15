package nori.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nori.NoriException;
import nori.task.Deadline;
import nori.task.Event;
import nori.task.TaskList;
import nori.task.Todo;

class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void load_blankDescription_reportsInvalidData() throws IOException {
        Storage storage = new Storage(writeData("T | 0 | " + encode("   ")));

        NoriException exception = assertThrows(NoriException.class, storage::load);

        assertEquals("The task data on line 1 is invalid.", exception.getMessage());
    }

    @Test
    void load_blankEventEndpoint_reportsInvalidData() throws IOException {
        Storage storage = new Storage(writeData(String.join(" | ",
                "E", "0", encode("meeting"), encode(""), encode("Tuesday"))));

        assertThrows(NoriException.class, storage::load);
    }

    @Test
    void saveAndLoad_mixedTasks_preservesData() throws NoriException, IOException {
        Path dataFile = tempDirectory.resolve("data").resolve("nori.txt");
        Storage storage = new Storage(dataFile);
        TaskList originalTasks = new TaskList();
        originalTasks.add(new Todo("read book"));
        originalTasks.add(new Deadline(
                "submit report", LocalDate.of(2019, 12, 3), LocalTime.of(18, 0)));
        originalTasks.add(new Event("project meeting", "Monday", "Tuesday"));
        originalTasks.mark(1);

        storage.save(originalTasks);
        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(originalTasks.get(0).toString(), loadedTasks.get(0).toString());
        assertEquals(originalTasks.get(1).toString(), loadedTasks.get(1).toString());
        assertEquals(originalTasks.get(2).toString(), loadedTasks.get(2).toString());
        assertEquals(3, Files.readAllLines(dataFile).size());
    }

    @Test
    void load_missingFile_createsEmptyList() throws NoriException {
        Path dataFile = tempDirectory.resolve("data").resolve("nori.txt");
        Storage storage = new Storage(dataFile);

        TaskList loadedTasks = storage.load();

        assertEquals(0, loadedTasks.size());
        assertTrue(Files.exists(dataFile));
    }

    @Test
    void load_invalidData_throwsException() throws IOException {
        Path dataFile = writeData("D | 0");
        Storage storage = new Storage(dataFile);

        NoriException exception = assertThrows(NoriException.class, storage::load);

        assertEquals("The task data on line 1 is invalid.", exception.getMessage());
    }

    @Test
    void save_twice_replacesContents() throws NoriException, IOException {
        Path dataFile = tempDirectory.resolve("data").resolve("nori.txt");
        Storage storage = new Storage(dataFile);
        TaskList originalTasks = new TaskList();
        originalTasks.add(new Todo("first task"));
        originalTasks.add(new Todo("obsolete task"));
        storage.save(originalTasks);
        TaskList replacementTasks = new TaskList();
        replacementTasks.add(new Todo("replacement task"));

        storage.save(replacementTasks);
        TaskList loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("replacement task", loadedTasks.get(0).getDescription());
        assertEquals(1, Files.readAllLines(dataFile).size());
    }

    @Test
    void save_specialText_preservesDescription() throws NoriException {
        Path dataFile = tempDirectory.resolve("data").resolve("nori.txt");
        Storage storage = new Storage(dataFile);
        TaskList originalTasks = new TaskList();
        originalTasks.add(new Todo("read | résumé 📚"));

        storage.save(originalTasks);
        TaskList loadedTasks = storage.load();

        assertEquals("read | résumé 📚", loadedTasks.get(0).getDescription());
    }

    @Test
    void load_invalidState_throwsException() throws IOException {
        Path dataFile = writeData(String.join(" | ", "T", "2", encode("read book")));
        Storage storage = new Storage(dataFile);

        assertThrows(NoriException.class, storage::load);
    }

    @Test
    void load_unknownType_throwsException() throws IOException {
        Path dataFile = writeData(String.join(" | ", "X", "0", encode("read book")));
        Storage storage = new Storage(dataFile);

        assertThrows(NoriException.class, storage::load);
    }

    @Test
    void load_invalidEncoding_throwsException() throws IOException {
        Path dataFile = writeData(String.join(" | ", "T", "0", "%%%"));
        Storage storage = new Storage(dataFile);

        assertThrows(NoriException.class, storage::load);
    }

    @Test
    void load_invalidDate_throwsException() throws IOException {
        Path dataFile = writeData(String.join(" | ",
                "D", "0", encode("return book"), encode("2019-02-31"), encode("18:00")));
        Storage storage = new Storage(dataFile);

        assertThrows(NoriException.class, storage::load);
    }

    @Test
    void load_invalidTime_throwsException() throws IOException {
        Path dataFile = writeData(String.join(" | ",
                "D", "0", encode("return book"), encode("2019-12-03"), encode("25:00")));
        Storage storage = new Storage(dataFile);

        assertThrows(NoriException.class, storage::load);
    }

    @Test
    void load_invalidSecondLine_reportsLineNumber() throws IOException {
        String validTask = String.join(" | ", "T", "0", encode("read book"));
        Path dataFile = writeData(validTask + System.lineSeparator() + "D | 0");
        Storage storage = new Storage(dataFile);

        NoriException exception = assertThrows(NoriException.class, storage::load);

        assertEquals("The task data on line 2 is invalid.", exception.getMessage());
    }

    private Path writeData(String content) throws IOException {
        Path dataFile = tempDirectory.resolve("data").resolve("nori.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, content, StandardCharsets.UTF_8);
        return dataFile;
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}

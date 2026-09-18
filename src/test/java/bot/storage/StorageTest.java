package bot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bot.exception.BotException;
import bot.parser.TaskDateTime;
import bot.task.Deadline;
import bot.task.Task;
import bot.task.Todo;

class StorageTest {

    @TempDir
    private Path tempDir;

    @Test
    void load_missingFile_returnsEmptyList() throws BotException {
        Storage storage = new Storage(tempDir.resolve("bot.txt").toString());
        assertTrue(storage.load(new ArrayList<>()).isEmpty());
    }

    @Test
    void saveThenLoad_roundTripsTasks() throws BotException {
        String filePath = tempDir.resolve("bot.txt").toString();
        Storage storage = new Storage(filePath);

        List<Task> original = new ArrayList<>();
        original.add(new Todo("read book"));
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));
        deadline.markAsDone();
        original.add(deadline);

        storage.save(original);
        List<Task> loaded = storage.load(new ArrayList<>());

        assertEquals(2, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[D][X] return book (by: Oct 15 2019)", loaded.get(1).toString());
    }

    @Test
    void save_createsParentDirectoryIfMissing() throws BotException {
        String filePath = tempDir.resolve("nested/data/bot.txt").toString();
        Storage storage = new Storage(filePath);

        storage.save(List.of(new Todo("read book")));

        assertTrue(Files.exists(tempDir.resolve("nested/data/bot.txt")));
    }

    @Test
    void load_blankLines_areSkippedSilently() throws Exception {
        Path dataFile = tempDir.resolve("bot.txt");
        Files.writeString(dataFile, "T | 0 | read book\n\n   \n");
        Storage storage = new Storage(dataFile.toString());

        List<String> warnings = new ArrayList<>();
        List<Task> loaded = storage.load(warnings);

        assertEquals(1, loaded.size());
        assertTrue(warnings.isEmpty());
    }

    @Test
    void load_tooFewFields_recordsWarningWithLineNumber() throws Exception {
        Path dataFile = tempDir.resolve("bot.txt");
        Files.writeString(dataFile, "T | 0\n");
        Storage storage = new Storage(dataFile.toString());

        List<String> warnings = new ArrayList<>();
        List<Task> loaded = storage.load(warnings);

        assertTrue(loaded.isEmpty());
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).startsWith("line 1:"));
    }

    @Test
    void load_invalidDoneFlag_recordsWarning() throws Exception {
        Path dataFile = tempDir.resolve("bot.txt");
        Files.writeString(dataFile, "T | 2 | read book\n");
        Storage storage = new Storage(dataFile.toString());

        List<String> warnings = new ArrayList<>();
        storage.load(warnings);

        assertEquals(1, warnings.size());
    }

    @Test
    void load_deadlineMissingDateField_recordsWarning() throws Exception {
        Path dataFile = tempDir.resolve("bot.txt");
        Files.writeString(dataFile, "D | 0 | return book\n");
        Storage storage = new Storage(dataFile.toString());

        List<String> warnings = new ArrayList<>();
        storage.load(warnings);

        assertEquals(1, warnings.size());
    }

    @Test
    void load_eventMissingDateFields_recordsWarning() throws Exception {
        Path dataFile = tempDir.resolve("bot.txt");
        Files.writeString(dataFile, "E | 0 | project meeting | 2019-10-15 1400\n");
        Storage storage = new Storage(dataFile.toString());

        List<String> warnings = new ArrayList<>();
        storage.load(warnings);

        assertEquals(1, warnings.size());
    }

    @Test
    void load_unknownTaskType_recordsWarning() throws Exception {
        Path dataFile = tempDir.resolve("bot.txt");
        Files.writeString(dataFile, "X | 0 | mystery task\n");
        Storage storage = new Storage(dataFile.toString());

        List<String> warnings = new ArrayList<>();
        storage.load(warnings);

        assertEquals(1, warnings.size());
    }

    @Test
    void load_oneBadLineDoesNotAffectOtherLines() throws Exception {
        Path dataFile = tempDir.resolve("bot.txt");
        Files.writeString(dataFile, "T | 0 | read book\nX | 0 | mystery task\nT | 1 | return book\n");
        Storage storage = new Storage(dataFile.toString());

        List<String> warnings = new ArrayList<>();
        List<Task> loaded = storage.load(warnings);

        assertEquals(2, loaded.size());
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).startsWith("line 2:"));
    }
}

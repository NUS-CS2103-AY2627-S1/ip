package choo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import choo.exception.ChooException;
import choo.task.Deadline;
import choo.task.Event;
import choo.task.Task;
import choo.task.Todo;

/**
 * Tests saving and loading tasks using isolated temporary files.
 */
public class StorageTest {
    @TempDir
    Path testDirectory;

    @Test
    void load_missingDataFile_returnsEmptyList() throws ChooException {
        Storage storage = new Storage(testDirectory.resolve("data").resolve("choo.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void load_directoryInsteadOfFile_throwsReadableError() {
        Storage storage = new Storage(testDirectory);

        ChooException exception = assertThrows(ChooException.class, storage::load);

        assertEquals("I couldn't read the task data file.", exception.getMessage());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesTheirData() throws ChooException, IOException {
        Path dataFile = testDirectory.resolve("missing").resolve("folder").resolve("choo.txt");
        Storage storage = new Storage(dataFile);
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        Task returnBookDeadline = new Deadline("return book", "2019-12-06 1700");
        returnBookDeadline.markAsDone();
        tasks.add(returnBookDeadline);
        tasks.add(new Event("project meeting", "Monday 2pm", "4pm"));

        storage.save(tasks);
        List<Task> loadedTasks = storage.load();

        String lineSeparator = System.lineSeparator();
        assertEquals("T | 0 | read book" + lineSeparator
                + "D | 1 | return book | 2019-12-06 1700" + lineSeparator
                + "E | 0 | project meeting | Monday 2pm | 4pm" + lineSeparator,
                Files.readString(dataFile));
        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[D][X] return book (by: Dec 6 2019, 5:00PM)",
                loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Monday 2pm to: 4pm)",
                loadedTasks.get(2).toString());
    }

    @Test
    void saveAndLoad_dateOnlyAndMidnight_preservesWhetherTimeWasExplicit()
            throws ChooException, IOException {
        Path dataFile = testDirectory.resolve("midnight.txt");
        Storage storage = new Storage(dataFile);

        storage.save(List.of(
                new Deadline("date only", "2019-12-02"),
                new Deadline("midnight", "2019-12-02 0000")));
        List<Task> loadedTasks = storage.load();

        String lineSeparator = System.lineSeparator();
        assertEquals("D | 0 | date only | 2019-12-02" + lineSeparator
                + "D | 0 | midnight | 2019-12-02 0000" + lineSeparator,
                Files.readString(dataFile));
        assertEquals("[D][ ] date only (by: Dec 2 2019)", loadedTasks.get(0).toString());
        assertEquals("[D][ ] midnight (by: Dec 2 2019, 12:00AM)",
                loadedTasks.get(1).toString());
    }

    @Test
    void saveAndLoad_separatorsAndBackslashes_preservesTaskText()
            throws ChooException, IOException {
        Path dataFile = testDirectory.resolve("escaping.txt");
        Storage storage = new Storage(dataFile);

        storage.save(List.of(new Deadline("read A | B \\ notes", "2019-12-07")));
        List<Task> loadedTasks = storage.load();

        assertEquals("D | 0 | read A \\| B \\\\ notes | 2019-12-07"
                        + System.lineSeparator(), Files.readString(dataFile));
        assertEquals("[D][ ] read A | B \\ notes (by: Dec 7 2019)",
                loadedTasks.get(0).toString());
    }

    @Test
    void load_corruptedData_throwsErrorWithLineNumber() throws IOException {
        Path dataFile = testDirectory.resolve("corrupt.txt");
        Storage storage = new Storage(dataFile);
        String[] corruptedLines = {
            "D | 2 | report | 2019-12-02\n",
            "T | 0 | \n",
            "T | 0 | bad\\q\n",
            "T | 0 | trailing\\\n",
            "Q | 0 | unknown\n",
            "T | 0 | task | extra\n"
        };

        for (String data : corruptedLines) {
            Files.writeString(dataFile, data);
            ChooException exception = assertThrows(ChooException.class, storage::load);
            assertTrue(exception.getMessage().contains("line 1"));
        }
    }

    @Test
    void load_legacyDeadline_explainsSupportedFormatWithoutChangingFile() throws IOException {
        Path dataFile = testDirectory.resolve("legacy.txt");
        Storage storage = new Storage(dataFile);
        String legacyData = "D | 0 | report | Friday\n";
        Files.writeString(dataFile, legacyData);

        ChooException exception = assertThrows(ChooException.class, storage::load);

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("yyyy-MM-dd"));
        assertEquals(legacyData, Files.readString(dataFile));
    }
}

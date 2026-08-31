package bibi;

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

import bibi.task.Deadline;
import bibi.task.Event;
import bibi.task.Task;
import bibi.task.Todo;

/**
 * Tests writing tasks to the save file and reading them back, including the
 * handling of a missing file and of damaged lines.
 */
public class StorageTest {

    @Test
    public void save_allTaskTypes_writesTheDocumentedFormat(@TempDir Path tempDir)
            throws IOException, BibiException {
        Path file = tempDir.resolve("bibi.txt");
        Todo done = new Todo("read book");
        done.markComplete();

        new Storage(file).save(List.of(
                done,
                new Deadline("return book", "2019-06-06"),
                new Event("project meeting", "2019-08-06 1400", "2019-08-06 1600")));

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-06-06",
                "E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600"),
                Files.readAllLines(file));
    }

    @Test
    public void saveThenLoad_allTaskTypes_everyFieldSurvives(@TempDir Path tempDir)
            throws IOException, BibiException {
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        deadline.markComplete();
        List<Task> original = List.of(
                new Todo("read book"),
                deadline,
                new Event("camp", "2019-08-10", "2019-08-12"));

        storage.save(original);
        List<Task> restored = storage.load().tasks();

        assertEquals(original.size(), restored.size());
        for (int index = 0; index < original.size(); index++) {
            assertEquals(original.get(index).toString(), restored.get(index).toString());
        }
    }

    @Test
    public void save_missingParentFolder_folderCreated(@TempDir Path tempDir)
            throws IOException, BibiException {
        Path file = tempDir.resolve("data").resolve("nested").resolve("bibi.txt");
        assertFalse(Files.exists(file.getParent()));

        new Storage(file).save(List.of(new Todo("read book")));

        assertTrue(Files.exists(file));
    }

    @Test
    public void save_existingFile_previousContentsReplaced(@TempDir Path tempDir)
            throws IOException, BibiException {
        Path file = tempDir.resolve("bibi.txt");
        Storage storage = new Storage(file);

        storage.save(List.of(new Todo("first"), new Todo("second")));
        storage.save(List.of(new Todo("only")));

        assertEquals(List.of("T | 0 | only"), Files.readAllLines(file));
    }

    @Test
    public void save_emptyList_fileIsEmpty(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("bibi.txt");
        new Storage(file).save(List.of());
        assertEquals(List.of(), Files.readAllLines(file));
    }

    @Test
    public void load_missingFile_emptyResultWithoutWarnings(@TempDir Path tempDir)
            throws IOException {
        Storage.LoadReport report = new Storage(tempDir.resolve("nothing-here.txt")).load();

        assertTrue(report.tasks().isEmpty());
        assertTrue(report.warnings().isEmpty());
    }

    @Test
    public void load_blankLines_ignoredSilently(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("bibi.txt");
        Files.writeString(file, "T | 0 | read book\n\n   \nT | 1 | join club\n");

        Storage.LoadReport report = new Storage(file).load();

        assertEquals(2, report.tasks().size());
        assertTrue(report.warnings().isEmpty());
    }

    @Test
    public void load_damagedLines_goodTasksKeptAndEachProblemReported(@TempDir Path tempDir)
            throws IOException {
        Path file = tempDir.resolve("bibi.txt");
        Files.writeString(file, String.join("\n",
                "T | 1 | read book",
                "this line is not a task",
                "X | 0 | unknown type",
                "D | 2 | return book | 2019-06-06",
                "E | 0 | missing the end time",
                "D | 0 | return book | Sunday",
                "T | 0 | join sports club") + "\n");

        Storage.LoadReport report = new Storage(file).load();

        assertEquals(2, report.tasks().size());
        assertEquals(5, report.warnings().size());
        assertTrue(report.warnings().get(0).startsWith("Line 2:"));
        assertTrue(report.warnings().get(1).contains("unknown task type 'X'"));
        assertTrue(report.warnings().get(2).contains("status '2'"));
        assertTrue(report.warnings().get(3).contains("expected 5 fields"));
        assertTrue(report.warnings().get(4).contains("Sunday"));
    }

    @Test
    public void load_completedFlag_restoredAsCompleted(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("bibi.txt");
        Files.writeString(file, "T | 1 | read book\nT | 0 | join club\n");

        List<Task> tasks = new Storage(file).load().tasks();

        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[T][ ] join club", tasks.get(1).toString());
    }

    @Test
    public void load_lowerCaseTypeCode_stillUnderstood(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("bibi.txt");
        Files.writeString(file, "t | 0 | read book\n");

        Storage.LoadReport report = new Storage(file).load();

        assertEquals(1, report.tasks().size());
        assertTrue(report.warnings().isEmpty());
    }

    @Test
    public void load_pathIsAFolder_reportedAsAWarning(@TempDir Path tempDir) throws IOException {
        Path folder = tempDir.resolve("bibi.txt");
        Files.createDirectory(folder);

        Storage.LoadReport report = new Storage(folder).load();

        assertTrue(report.tasks().isEmpty());
        assertEquals(1, report.warnings().size());
        assertTrue(report.warnings().get(0).contains("is not a file"));
    }

    @Test
    public void save_pathIsAFolder_exceptionThrown(@TempDir Path tempDir) throws IOException {
        Path folder = tempDir.resolve("bibi.txt");
        Files.createDirectory(folder);

        assertThrows(IOException.class, () -> new Storage(folder).save(List.of()));
    }

    @Test
    public void getFilePath_afterConstruction_returnsTheSuppliedPath(@TempDir Path tempDir) {
        Path file = tempDir.resolve("bibi.txt");
        assertEquals(file, new Storage(file).getFilePath());
    }
}

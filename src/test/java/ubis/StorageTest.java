package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests storage recovery from missing paths, invalid paths, and malformed records.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_createsFileAndReturnsEmptyList() {
        Path savePath = temporaryDirectory.resolve("nested").resolve("data.txt");

        TaskList tasks = Storage.load(savePath);

        assertTrue(Files.isRegularFile(savePath));
        assertEquals("No tasks to show", tasks.listTasks());
    }

    @Test
    public void saveAndLoad_validTasks_preservesData() {
        Path savePath = temporaryDirectory.resolve("data.txt");
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo().initialise("read book"));

        assertTrue(Storage.save(tasks, savePath));
        assertEquals(tasks.toString(), Storage.load(savePath).toString());
    }

    @Test
    public void load_malformedRecords_skipsOnlyInvalidLines() throws IOException {
        Path savePath = temporaryDirectory.resolve("data.txt");
        String storedData = "{T}{0}{read book}\n"
                + "{T}{2}{invalid status}\n"
                + "{T}{0}{extra field}{unexpected}\n"
                + "{X}{0}{unknown type}\n"
                + "{D}{0}{invalid date}{2026-02-30}\n"
                + "prefix{T}{0}{stray text}\n"
                + "{T}{0}{}\n"
                + "{E}{1}{trip}{2026-09-01}{2026-09-03}\n";
        Files.writeString(savePath, storedData);

        TaskList tasks = Storage.load(savePath);

        assertEquals("{T}{0}{read book}\n{E}{1}{trip}{2026-09-01}{2026-09-03}\n", tasks.toString());
    }

    @Test
    public void loadWithReport_malformedRecords_returnsSpecificWarning() throws IOException {
        Path savePath = temporaryDirectory.resolve("data.txt");
        Files.writeString(savePath, "{T}{0}{read book}\n{T}{invalid}{broken task}\n");

        Storage.LoadResult result = Storage.loadWithReport(savePath);

        assertEquals("{T}{0}{read book}\n", result.getTaskList().toString());
        assertEquals("Ubis skipped 1 invalid saved task record. Valid tasks were loaded normally.",
                result.getWarning());
    }

    @Test
    public void loadWithReport_inaccessiblePath_returnsSpecificWarning() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "content");

        Storage.LoadResult result = Storage.loadWithReport(parentFile.resolve("data.txt"));

        assertEquals("Ubis could not access the task data file. It started with an empty task list.",
                result.getWarning());
    }

    @Test
    public void save_pathIsDirectory_returnsFalseWithoutDeletingDirectory() throws IOException {
        Path invalidSavePath = Files.createDirectory(temporaryDirectory.resolve("data.txt"));

        assertFalse(Storage.save(new TaskList(), invalidSavePath));
        assertTrue(Files.isDirectory(invalidSavePath));
    }

    @Test
    public void load_parentPathIsFile_returnsEmptyList() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "content");

        TaskList tasks = Storage.load(parentFile.resolve("data.txt"));

        assertEquals("No tasks to show", tasks.listTasks());
    }

    @Test
    public void saveAndLoad_mixedTasks_preservesUnicodeOrderAndCompletion() throws IOException {
        Path savePath = temporaryDirectory.resolve("nested/tasks.txt");
        TaskList tasks = new TaskList();
        tasks.addTask(Task.initialise("T", "1", "读书 📚"));
        tasks.addTask(Task.initialise("D", "0", "café report", "2024-02-29"));
        tasks.addTask(Task.initialise("E", "1", "trip", "2026-12-31", "2027-01-01"));
        String expected = "{T}{1}{读书 📚}\n{D}{0}{café report}{2024-02-29}\n"
                + "{E}{1}{trip}{2026-12-31}{2027-01-01}\n";
        assertTrue(Storage.save(tasks, savePath));
        assertEquals(expected, Files.readString(savePath));
        Storage.LoadResult result = Storage.loadWithReport(savePath);
        assertEquals(expected, result.getTaskList().toString());
        assertNull(result.getWarning());
        try (Stream<Path> files = Files.list(savePath.getParent())) {
            assertEquals(1, files.count(), "Successful saves must not leave temporary files behind");
        }
    }

    @Test
    public void save_shorterThenEmptyList_replacesOldContents() throws IOException {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(savePath, "{T}{0}{old task}\n{T}{1}{another old task}\n");
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo().initialise("new"));
        assertTrue(Storage.save(tasks, savePath));
        assertEquals("{T}{0}{new}\n", Files.readString(savePath));
        tasks.removeTask(1);
        assertTrue(Storage.save(tasks, savePath));
        assertEquals("", Files.readString(savePath));
        Storage.LoadResult result = Storage.loadWithReport(savePath);
        assertEquals("No tasks to show", result.getTaskList().listTasks());
        assertNull(result.getWarning());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "garbage", "{T}{0}{book", "{T}{0}{{book}}", "{T}{0}{book}suffix",
        "{T} {0}{book}", "{T}{0}{book}}", "{T}{0}", "{D}{0}{report}", "{E}{0}{trip}{2026-09-01}",
        "{E}{0}{trip}{2026-09-03}{2026-09-01}", "{T}{}{book}", "{T}{0}{   }"})
    void loadWithReport_malformedRecord_skipsRecordAndContinues(String record) throws IOException {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(savePath, "{T}{0}{before}\n" + record + "\n{T}{1}{after}\n");
        Storage.LoadResult result = Storage.loadWithReport(savePath);
        assertEquals("{T}{0}{before}\n{T}{1}{after}\n", result.getTaskList().toString());
        assertEquals("Ubis skipped 1 invalid saved task record. Valid tasks were loaded normally.",
                result.getWarning());
    }

    @Test
    public void loadWithReport_allRecordsInvalid_reportsPluralWarning() throws IOException {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(savePath, "bad\nworse\n");
        Storage.LoadResult result = Storage.loadWithReport(savePath);
        assertEquals("", result.getTaskList().toString());
        assertEquals("Ubis skipped 2 invalid saved task records. Valid tasks were loaded normally.",
                result.getWarning());
    }

    @Test
    public void loadWithReport_invalidUtf8_returnsReadWarning() throws IOException {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Files.write(savePath, new byte[] {(byte) 0xc3, (byte) 0x28});
        Storage.LoadResult result = Storage.loadWithReport(savePath);
        assertEquals("", result.getTaskList().toString());
        assertEquals("Ubis could not read the task data file. It started with an empty task list.",
                result.getWarning());
    }

    @Test
    public void load_windowsLineEndingsAndNoFinalNewline_loadsAllTasks() throws IOException {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(savePath, "{T}{0}{first}\r\n{T}{1}{last}");
        assertEquals("{T}{0}{first}\n{T}{1}{last}\n", Storage.load(savePath).toString());
    }

    @Test
    public void save_parentIsFile_preservesExistingContent() throws IOException {
        Path parentFile = temporaryDirectory.resolve("file");
        Files.writeString(parentFile, "keep me");
        assertFalse(Storage.save(new TaskList(), parentFile.resolve("tasks.txt")));
        assertEquals("keep me", Files.readString(parentFile));
    }

    @Test
    public void save_ancestorIsFile_reportsCreationFailure() throws IOException {
        Path parentFile = temporaryDirectory.resolve("file");
        Files.writeString(parentFile, "keep me");
        Path savePath = parentFile.resolve("nested/tasks.txt");
        assertFalse(Storage.save(new TaskList(), savePath));
        assertEquals("", Storage.load(savePath).toString());
        assertEquals("keep me", Files.readString(parentFile));
    }
}

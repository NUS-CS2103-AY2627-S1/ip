package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {

    // Every test writes to its own throwaway directory, so none of them can
    // reach the real data/nova.txt.
    @TempDir
    Path tempDir;

    private Path dataFile() {
        return tempDir.resolve("nova.txt");
    }

    private Path writeLines(String... lines) throws IOException {
        Path file = dataFile();
        Files.write(file, List.of(lines));
        return file;
    }

    private TaskList listOf(Task... tasks) {
        ArrayList<Task> contents = new ArrayList<>(List.of(tasks));
        return new TaskList(contents);
    }

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() throws FileNotFoundException {
        Storage storage = new Storage(dataFile().toString());

        assertTrue(storage.load().isEmpty());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_emptyFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(writeLines().toString());

        assertTrue(storage.load().isEmpty());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_validLines_returnsTasksInFileOrder() throws IOException {
        Path file = writeLines(
                "T | 1 | read book",
                "D | 0 | return book | 2019-10-15T18:00",
                "E | 0 | meeting | 2019-10-15T14:00 | 2019-10-15T16:00");

        ArrayList<Task> loaded = new Storage(file.toString()).load();

        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019, 6:00PM)", loaded.get(1).toString());
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)",
                loaded.get(2).toString());
    }

    @Test
    public void load_blankLines_areIgnoredAndNotCounted() throws IOException {
        Path file = writeLines("", "T | 0 | read book", "   ", "");

        Storage storage = new Storage(file.toString());

        assertEquals(1, storage.load().size());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_malformedLines_areSkippedAndCounted() throws IOException {
        Path file = writeLines(
                "T | 0 | read book",
                "garbage",
                "D | 0 | no date at all",
                "X | 0 | unknown type letter",
                "D | 0 | legacy free text date | 2pm",
                "T | notanumber | bad done flag",
                "T | 0 |");

        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals(6, storage.getSkippedLineCount());
    }

    @Test
    public void load_calledAgainOnACleanFile_resetsSkippedLineCount() throws IOException {
        Path file = writeLines("garbage", "more garbage");
        Storage storage = new Storage(file.toString());
        storage.load();
        assertEquals(2, storage.getSkippedLineCount());

        Files.write(file, List.of("T | 0 | read book"));
        storage.load();

        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_pathIsADirectory_throwsFileNotFoundException() throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("nova.txt.d"));

        Storage storage = new Storage(directory.toString());

        // The file "exists" but cannot be opened. This has to be distinguished
        // from a first run, because saving over it would destroy whatever is
        // there; Nova switches saving off when it sees this.
        assertThrows(FileNotFoundException.class, storage::load);
    }

    @Test
    public void save_missingParentDirectories_areCreated() throws IOException {
        Path nested = tempDir.resolve("a/b/c/nova.txt");
        Storage storage = new Storage(nested.toString());

        storage.save(listOf(new ToDo("read book", false)));

        assertTrue(Files.exists(nested));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(nested));
    }

    @Test
    public void save_existingFile_isRewrittenNotAppended() throws IOException {
        Path file = dataFile();
        Storage storage = new Storage(file.toString());
        storage.save(listOf(
                new ToDo("first", false),
                new ToDo("second", false),
                new ToDo("third", false)));

        // Appending cannot express a deletion, which is why the whole file is
        // rewritten. Saving a shorter list must shorten the file.
        storage.save(listOf(new ToDo("only one left", false)));

        assertEquals(List.of("T | 0 | only one left"), Files.readAllLines(file));
    }

    @Test
    public void save_emptyList_leavesAnEmptyFile() throws IOException {
        Path file = dataFile();
        Storage storage = new Storage(file.toString());
        storage.save(listOf(new ToDo("read book", false)));

        storage.save(listOf());

        assertTrue(Files.exists(file));
        assertTrue(Files.readAllLines(file).isEmpty());
    }

    @Test
    public void saveThenLoad_everyTaskType_roundTripsUnchanged() throws IOException {
        // Descriptions deliberately contain no "|": that is the save format's
        // field separator, and a description containing one is a known
        // limitation rather than something this test should pretend works.
        TaskList original = listOf(
                new ToDo("read book", false),
                new Deadline("return book", true, LocalDateTime.of(2019, 10, 15, 18, 0)),
                new Deadline("pay fees", false, LocalDateTime.of(2019, 10, 15, 0, 0)),
                new Event("meeting", false,
                        LocalDateTime.of(2019, 10, 15, 14, 0),
                        LocalDateTime.of(2019, 10, 15, 16, 0)));

        Storage storage = new Storage(dataFile().toString());
        storage.save(original);
        ArrayList<Task> reloaded = storage.load();

        assertEquals(original.size(), reloaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toDataString(), reloaded.get(i).toDataString());
            assertEquals(original.get(i).toString(), reloaded.get(i).toString());
        }
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void saveThenLoad_doneStatus_isPreserved() throws IOException {
        Storage storage = new Storage(dataFile().toString());
        storage.save(listOf(new ToDo("done one", true), new ToDo("not done", false)));

        ArrayList<Task> reloaded = storage.load();

        assertTrue(reloaded.get(0).isDone());
        assertFalse(reloaded.get(1).isDone());
    }

    @Test
    public void getFilePath_returnsThePathGivenToTheConstructor() {
        assertEquals("some/where/nova.txt", new Storage("some/where/nova.txt").getFilePath());
    }

    @Test
    public void getSkippedLineCount_beforeAnyLoad_isZero() {
        assertEquals(0, new Storage(dataFile().toString()).getSkippedLineCount());
    }
}

package minweeder.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import minweeder.exception.MinweederException;
import minweeder.task.Deadline;
import minweeder.task.Loan;
import minweeder.task.LoanType;
import minweeder.task.TaskList;
import minweeder.task.Todo;

public class StorageTest {
    private static final Path FILE_PATH = Paths.get("data", "minweeder.txt");
    private byte[] backup;
    private boolean isFileExisting;

    @BeforeEach
    public void backUpExistingSaveFile() throws IOException {
        isFileExisting = Files.exists(FILE_PATH);
        if (isFileExisting) {
            backup = Files.readAllBytes(FILE_PATH);
        }
    }

    @AfterEach
    public void restoreExistingSaveFile() throws IOException {
        if (isFileExisting) {
            Files.write(FILE_PATH, backup);
        } else {
            Files.deleteIfExists(FILE_PATH);
        }
    }

    @Test
    public void saveThenLoad_roundTrip_restoresEquivalentTasks() throws MinweederException {
        Storage storage = new Storage();
        TaskList original = new TaskList();
        original.add(new Todo("read book"));
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2024, 12, 2, 18, 0));
        deadline.mark();
        original.add(deadline);

        storage.save(original);
        TaskList loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals(original.get(0).toFileString(), loaded.get(0).toFileString());
        assertEquals(original.get(1).toFileString(), loaded.get(1).toFileString());
    }

    @Test
    public void saveThenLoad_loanRoundTrip_restoresEquivalentLoan() throws MinweederException {
        Storage storage = new Storage();
        TaskList original = new TaskList();
        original.add(new Loan("Alice", 50.5, LoanType.LENT));

        storage.save(original);
        TaskList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(original.get(0).toFileString(), loaded.get(0).toFileString());
    }

    @Test
    public void load_loanLineWithInvalidType_isSkippedAndCounted() throws IOException, MinweederException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, "L | 0 | Alice | GIFTED | 50.0\nT | 0 | read book\n".getBytes());

        Storage storage = new Storage();
        TaskList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toFileString());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_lineWithUnknownTaskType_isSkippedAndCounted() throws IOException, MinweederException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, "X | 0 | mystery task\nT | 0 | read book\n".getBytes());

        Storage storage = new Storage();
        TaskList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toFileString());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_missingFile_returnsEmptyTaskList() throws IOException, MinweederException {
        Files.deleteIfExists(FILE_PATH);

        Storage storage = new Storage();
        TaskList loaded = storage.load();

        assertEquals(0, loaded.size());
    }

    @Test
    public void load_blankLines_areIgnoredWithoutBeingCounted() throws IOException, MinweederException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, "\nT | 0 | read book\n   \n".getBytes());

        Storage storage = new Storage();
        TaskList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_lineWithTooFewFields_isSkippedAndCounted() throws IOException, MinweederException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, "T | 0\nT | 0 | read book\n".getBytes());

        Storage storage = new Storage();
        TaskList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_deadlineLineMissingByField_isSkippedAndCounted() throws IOException, MinweederException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, "D | 0 | submit report\nT | 0 | read book\n".getBytes());

        Storage storage = new Storage();
        TaskList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_deadlineLineWithUnparsableDate_isSkippedAndCounted() throws IOException, MinweederException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, "D | 0 | submit report | not-a-date\nT | 0 | read book\n".getBytes());

        Storage storage = new Storage();
        TaskList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_eventLineMissingToField_isSkippedAndCounted() throws IOException, MinweederException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, "E | 0 | project meeting | Mon 2pm\nT | 0 | read book\n".getBytes());

        Storage storage = new Storage();
        TaskList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_loanLineMissingAmountField_isSkippedAndCounted() throws IOException, MinweederException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, "L | 0 | Alice | LENT\nT | 0 | read book\n".getBytes());

        Storage storage = new Storage();
        TaskList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_lineWithInvalidMarkFlag_isSkippedAndCounted() throws IOException, MinweederException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, "T | 2 | read book\nT | 0 | return book\n".getBytes());

        Storage storage = new Storage();
        TaskList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("T | 0 | return book", loaded.get(0).toFileString());
        assertEquals(1, storage.getSkippedLineCount());
    }
}

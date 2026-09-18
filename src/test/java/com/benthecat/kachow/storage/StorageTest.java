package com.benthecat.kachow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.benthecat.kachow.exception.KachowException;
import com.benthecat.kachow.parser.DateTimeParser;
import com.benthecat.kachow.task.Deadline;
import com.benthecat.kachow.task.Event;
import com.benthecat.kachow.task.Task;
import com.benthecat.kachow.task.Todo;

/**
 * Tests persistence behavior represented by the UI startup and storage test cases.
 */
class StorageTest {
    @TempDir
    Path tempDirectory;

    /** Verifies that a missing data file is treated as an empty task list. */
    @Test
    void load_missingFile_returnsEmptyList() throws KachowException {
        Storage storage = new Storage(tempDirectory.resolve("data/kachow.txt"));

        assertTrue(storage.load().isEmpty());
    }

    /** Verifies round-trip persistence of task types, order, values, and completion state. */
    @Test
    void saveAndLoad_allTaskTypes_preserveOrderValuesAndCompletion() throws KachowException, IOException {
        Path dataFile = tempDirectory.resolve("data/kachow.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read book", true);
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        Event event = new Event(
                "project meeting",
                new DateTimeParser.ParsedDateTime(LocalDateTime.of(2019, 8, 6, 14, 0)),
                new DateTimeParser.ParsedDateTime(LocalDateTime.of(2019, 8, 6, 16, 0)));

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-06-06",
                "E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("[T][X] read book", loaded.get(0).getStatusText());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", loaded.get(1).getStatusText());
        assertEquals("[E][ ] project meeting (from: Aug 06 2019, 2:00 PM"
                + " to: Aug 06 2019, 4:00 PM)", loaded.get(2).getStatusText());
    }

    /** Verifies that blank records are ignored without affecting surrounding task data. */
    @Test
    void load_blankLines_ignoresThemAndKeepsPhysicalLineNumbers() throws IOException, KachowException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        Files.writeString(dataFile, "\nT | 1 | read book\n\n", StandardCharsets.UTF_8);

        List<Task> loaded = new Storage(dataFile).load();

        assertEquals(1, loaded.size());
        assertEquals("[T][X] read book", loaded.getFirst().getStatusText());
    }

    /** Verifies that a stored event with a reversed range reports its physical line. */
    @Test
    void load_invalidEventRange_throwsLineSpecificError() throws IOException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        Files.writeString(dataFile,
                "E | 0 | backwards | 2024-01-02T18:00 | 2024-01-02T17:00\n",
                StandardCharsets.UTF_8);

        KachowException exception = assertThrows(KachowException.class, () ->
                new Storage(dataFile).load());

        assertEquals("Task data on line 1 of " + dataFile + " is invalid.", exception.getMessage());
    }

    /** Verifies line-specific errors for invalid completion fields and record lengths. */
    @Test
    void load_malformedStatusAndFieldCount_throwLineSpecificErrors() throws IOException {
        Path invalidStatusFile = tempDirectory.resolve("invalid-status.txt");
        Path invalidFieldsFile = tempDirectory.resolve("invalid-fields.txt");
        Files.writeString(invalidStatusFile, "T | X | read book\n", StandardCharsets.UTF_8);
        Files.writeString(invalidFieldsFile, "D | 0 | return book\n", StandardCharsets.UTF_8);

        KachowException invalidStatus = assertThrows(KachowException.class, () ->
                new Storage(invalidStatusFile).load());
        KachowException invalidFields = assertThrows(KachowException.class, () ->
                new Storage(invalidFieldsFile).load());

        assertEquals("Task data on line 1 of " + invalidStatusFile + " is invalid.",
                invalidStatus.getMessage());
        assertEquals("Task data on line 1 of " + invalidFieldsFile + " is invalid.",
                invalidFields.getMessage());
    }
    @Test
    void load_invalidRecords_blocksSavingAndPreservesOriginalFile() throws IOException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        for (String record : List.of("T | 0 | ", "T | 0 | bad|description", "T | 0 | bad\u0000text",
                "X | 0 | unknown", "D | 0 | impossible | 2026-02-30",
                "E | 0 | equal | 2026-09-10 | 2026-09-10", "T | 0 | valid\nT | 1 | VALID")) {
            String contents = "T | 0 | first\n\n" + record + "\n";
            Files.writeString(dataFile, contents);
            Storage storage = new Storage(dataFile);
            assertThrows(KachowException.class, storage::load);
            assertThrows(KachowException.class, () -> storage.save(List.of(new Todo("replacement"))));
            assertEquals(contents, Files.readString(dataFile));
        }
    }

    @Test
    void load_invalidEncodingDirectoryAndBlockedParent_reportsReadErrors() throws IOException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        Files.write(dataFile, new byte[] {(byte) 0xc3, (byte) 0x28});
        assertThrows(KachowException.class, () -> new Storage(dataFile).load());
        assertThrows(KachowException.class, () -> new Storage(tempDirectory).load());
        assertThrows(KachowException.class, () -> new Storage(dataFile.resolve("child.txt")).load());
    }

    @Test
    void save_externalChangeOrDeletion_preservesOtherWritersData() throws IOException, KachowException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Todo("original")));
        Files.writeString(dataFile, "T | 0 | external\n");
        assertThrows(KachowException.class, () -> storage.save(List.of(new Todo("replacement"))));
        assertEquals("T | 0 | external\n", Files.readString(dataFile));
        Files.delete(dataFile);
        assertThrows(KachowException.class, () -> storage.save(List.of(new Todo("replacement"))));
        assertTrue(Files.notExists(dataFile));
    }

    @Test
    void save_failedWrite_cleansTemporaryFilesAndAllowsRetry() throws IOException, KachowException {
        Path dataFile = tempDirectory.resolve("data/kachow.txt");
        Storage storage = new Storage(dataFile);
        storage.load();
        Files.writeString(tempDirectory.resolve("data"), "blocked parent");
        assertThrows(KachowException.class, () -> storage.save(List.of(new Todo("replacement"))));
        assertEquals("blocked parent", Files.readString(tempDirectory.resolve("data")));
        Files.delete(tempDirectory.resolve("data"));
        storage.save(List.of(new Todo("retry")));
        assertEquals("T | 0 | retry\n", Files.readString(dataFile));
        try (var files = Files.list(dataFile.getParent())) {
            assertEquals(List.of(dataFile), files.toList());
        }
    }

    @Test
    void load_symbolicLink_reportsErrorWithoutChangingTarget() throws IOException {
        assumeTrue(Files.getFileStore(tempDirectory).supportsFileAttributeView("posix"));
        Path target = tempDirectory.resolve("original.txt");
        Files.writeString(target, "T | 0 | original\n");
        Path link = tempDirectory.resolve("kachow.txt");
        Files.createSymbolicLink(link, target);
        Storage storage = new Storage(link);
        assertThrows(KachowException.class, storage::load);
        assertThrows(KachowException.class, () -> storage.save(List.of(new Todo("replacement"))));
        assertEquals("T | 0 | original\n", Files.readString(target));
    }

    @Test
    void load_invalidRecordShapes_reportsPhysicalLineAndKeepsOriginalBytes() throws IOException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        for (String record : List.of("broken", "T | 0", "T | 0 | task | extra", "D | 0 | task",
                "D | 0 | task | 2026-09-11 | extra", "E | 0 | task | 2026-09-11",
                "E | 0 | task | 2026-09-11 | 2026-09-12 | extra", "T | 2 | task",
                "D | 0 | task | ", "E | 0 | task | tomorrow | 2026-09-12")) {
            String original = "T | 0 | valid\n\n" + record + "\n";
            Files.writeString(dataFile, original);
            KachowException exception = assertThrows(KachowException.class, () -> new Storage(dataFile).load());
            assertEquals("Task data on line 3 of " + dataFile + " is invalid.", exception.getMessage());
            assertEquals(original, Files.readString(dataFile));
        }
    }

    @Test
    void saveAndLoad_unicodeDateOnlyAndTimedTasks_preservesEveryDetail() throws IOException, KachowException {
        Path dataFile = tempDirectory.resolve("nested/data/kachow.txt");
        List<Task> originals = List.of(new Todo("阅读 📚 & C++/Java"),
                new Deadline("report", LocalDateTime.of(2026, 9, 11, 18, 30), true),
                new Event("conference", DateTimeParser.parse("2026-09-11"),
                        DateTimeParser.parse("2026-09-13"), true));
        new Storage(dataFile).save(originals);
        List<Task> loaded = new Storage(dataFile).load();
        assertEquals(originals.stream().map(Task::getStatusText).toList(),
                loaded.stream().map(Task::getStatusText).toList());
        assertEquals("T | 0 | 阅读 📚 & C++/Java\nD | 1 | report | 2026-09-11T18:30\n"
                + "E | 1 | conference | 2026-09-11 | 2026-09-13\n", Files.readString(dataFile));
    }

    @Test
    void save_emptyList_replacesExistingRecordsWithEmptyReadableFile() throws IOException, KachowException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Todo("original")));
        storage.save(List.of());
        assertEquals("", Files.readString(dataFile));
        assertTrue(new Storage(dataFile).load().isEmpty());
    }

    @Test
    void load_repairedFile_reenablesSavingAfterExplicitReload() throws IOException, KachowException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        Files.writeString(dataFile, "broken\n");
        Storage storage = new Storage(dataFile);
        assertThrows(KachowException.class, storage::load);
        Files.writeString(dataFile, "T | 1 | repaired\n");
        assertEquals("[T][X] repaired", storage.load().getFirst().getStatusText());
        storage.save(List.of(new Todo("new")));
        assertEquals("T | 0 | new\n", Files.readString(dataFile));
    }

    @Test
    void save_fileAppearsAfterEmptyLoad_preservesNewExternalData() throws IOException, KachowException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        Storage storage = new Storage(dataFile);
        assertTrue(storage.load().isEmpty());
        Files.writeString(dataFile, "T | 1 | external\n");
        assertThrows(KachowException.class, () -> storage.save(List.of(new Todo("new"))));
        assertEquals("T | 1 | external\n", Files.readString(dataFile));
    }

    @Test
    void load_crlfAndBlankLines_keepsRecordOrderAndNormalizesDescriptions() throws IOException, KachowException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        Files.writeString(dataFile, "\r\nT | 0 | first\r\n  \r\nT | 1 |  second   task \r\n");
        List<Task> tasks = new Storage(dataFile).load();
        assertEquals(List.of("[T][ ] first", "[T][X] second task"),
                tasks.stream().map(Task::getStatusText).toList());
    }

}

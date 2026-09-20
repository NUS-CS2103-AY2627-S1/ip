package bkxss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence of valid tasks and safe handling of invalid storage data. */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void storage_load_corruptRecords_recoversLaterTasksAndProtectsOriginal() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String contents = "T | 0 | first\n"
                + "D | 0 | impossible date | Feb 30 2026 09:00\n"
                + "T | 2 | invalid status\n"
                + "T | 0 | \n"
                + "E | 0 | invalid date | 2026-02-30 0900 to 2026-03-01 1000\n"
                + "E | 0 | reversed | 2026-09-12 1000 to 2026-09-12 0900\n"
                + "E | 0 | equal | 2026-09-12 0900 to 2026-09-12 0900\n"
                + "E | 0 | missing end | Mon 2pm to \n"
                + "T | 1 | first\n"
                + "T | 0 | extra | unexpected\n"
                + "T | 1 | last\n";
        Files.writeString(dataFile, contents);
        Storage storage = new Storage(dataFile.toString());

        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][ ] first", loaded.getFirst().toString());
        assertEquals("[T][X] last", loaded.getLast().toString());
        assertThrows(BkxssException.class, () -> storage.save(loaded));
        assertTrue(Bkxss.processCommandResult("delete 1", loaded, storage).isError());
        assertEquals(2, loaded.size());
        assertEquals(contents, Files.readString(dataFile));
        Files.writeString(dataFile, "T | 0 | repaired\n");
        ArrayList<Task> repairedTasks = storage.load();
        storage.save(repairedTasks);
        assertEquals(1, storage.load().size());
    }

    @Test
    void storage_load_invalidUtf8OrDirectory_blocksChanges() throws Exception {
        Path invalidEncoding = temporaryDirectory.resolve("invalid.txt");
        Files.write(invalidEncoding, new byte[]{(byte) 0xc3, (byte) 0x28});

        for (Path path : List.of(invalidEncoding, temporaryDirectory)) {
            Storage storage = new Storage(path.toString());
            assertTrue(storage.load().isEmpty());
            assertThrows(BkxssException.class, () -> storage.save(new ArrayList<>()));
        }
        assertEquals(2, Files.size(invalidEncoding));
    }

    @Test
    void storage_save_invalidRecord_doesNotTruncateExistingFileOrLeaveTemporaryFiles() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String contents = "T | 0 | original\n";
        Files.writeString(dataFile, contents);
        Storage storage = new Storage(dataFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("bad | record"));

        assertThrows(BkxssException.class, () -> storage.save(tasks));
        assertEquals(contents, Files.readString(dataFile));
        tasks.clear();
        tasks.add(new Todo("replacement"));
        storage.save(tasks);
        assertEquals("[T][ ] replacement", storage.load().getFirst().toString());
        try (var paths = Files.list(temporaryDirectory)) {
            assertEquals(1, paths.count());
        }
    }

    @Test
    void storage_save_readOnlyFile_preservesSavedTasks() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String contents = "T | 0 | original\n";
        Files.writeString(dataFile, contents);
        assumeTrue(Files.getFileStore(dataFile).supportsFileAttributeView(PosixFileAttributeView.class),
                "This permission test requires a POSIX file system");
        var originalPermissions = Files.getPosixFilePermissions(dataFile);
        Files.setPosixFilePermissions(dataFile, PosixFilePermissions.fromString("r--r--r--"));
        try {
            assumeTrue(!Files.isWritable(dataFile), "The current user can bypass read-only permissions");
            Storage storage = new Storage(dataFile.toString());
            assertEquals(1, storage.load().size());
            assertThrows(BkxssException.class, () -> storage.save(new ArrayList<>()));
            assertEquals(contents, Files.readString(dataFile));
        } finally {
            Files.setPosixFilePermissions(dataFile, originalPermissions);
        }
    }

    @Test
    void storage_save_bareRelativeFilename_doesNotRequireExplicitParent() throws Exception {
        Path dataFile = Files.createTempFile(Path.of("."), "bkxss-relative-", ".txt");
        try {
            Storage storage = new Storage(dataFile.getFileName().toString());
            storage.save(new ArrayList<>(List.of(new Todo("relative path"))));
            assertEquals(1, storage.load().size());
        } finally {
            Files.deleteIfExists(dataFile);
        }
    }

    @Test
    void storage_saveAndLoad_mixedTasks_preservesTypesValuesAndStatus() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        todo.markAsDone();
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertTrue(loaded.get(0) instanceof Todo);
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019 18:00)", loaded.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", loaded.get(2).toString());
        assertFalse(loaded.get(1).isDone());
    }

    @Test
    void storage_load_missingFile_returnsEmptyList() throws Exception {
        Path missingFile = temporaryDirectory.resolve("missing.txt");

        assertTrue(new Storage(missingFile.toString()).load().isEmpty());
    }

    @Test
    void storage_load_invalidLines_ignoresInvalidEntriesAndKeepsValidEntries() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | valid task\ninvalid line\nE | 0 | meeting | missing separator\n");

        ArrayList<Task> loaded = new Storage(dataFile.toString()).load();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] valid task", loaded.get(0).toString());
    }

    @Test
    void storage_saveAndLoad_datedEvent_preservesScheduleTimes() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Event("lecture", "2026-09-12 0900", "2026-09-12 1100"));

        storage.save(tasks);
        Event loadedEvent = (Event) storage.load().get(0);

        assertTrue(loadedEvent.hasScheduledTimes());
        assertEquals(LocalDateTime.of(2026, 9, 12, 9, 0), loadedEvent.getFromDateTime().orElseThrow());
        assertEquals(LocalDateTime.of(2026, 9, 12, 11, 0), loadedEvent.getToDateTime().orElseThrow());
    }

    @Test
    void storage_save_nestedMissingDirectory_createsParentsAndRoundTripsUnicode() throws Exception {
        Path dataFile = temporaryDirectory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        ArrayList<Task> tasks = new ArrayList<>(List.of(new Todo("买书 📚"),
                new Deadline("还书", LocalDateTime.of(2028, 2, 29, 18, 0)),
                new Event("讨论", "2028-02-29 2300", "2028-03-01 0100")));
        tasks.forEach(Task::markAsDone);

        storage.save(tasks);

        assertEquals("T | 1 | 买书 📚\nD | 1 | 还书 | Feb 29 2028 18:00\n"
                + "E | 1 | 讨论 | 2028-02-29 2300 to 2028-03-01 0100\n",
                Files.readString(dataFile).replace("\r\n", "\n"));
        assertEquals(tasks.stream().map(Task::toString).toList(),
                storage.load().stream().map(Task::toString).toList());
    }

    @Test
    void storage_save_emptyList_replacesExistingTasksWithEmptyFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | old\n");
        Storage storage = new Storage(dataFile.toString());

        storage.save(new ArrayList<>());

        assertEquals(0, Files.size(dataFile));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void storage_load_windowsLineEndingsAndCompactSeparators_readsEveryRecord() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T|1|read\r\nD|0|return|Feb 29 2028 18:00\r\n"
                + "E|1|meeting|2028-02-29 2300 to 2028-03-01 0100\r\n");

        ArrayList<Task> tasks = new Storage(dataFile.toString()).load();

        assertEquals(List.of("[T][X] read", "[D][ ] return (by: Feb 29 2028 18:00)",
                "[E][X] meeting (from: 2028-02-29 2300 to: 2028-03-01 0100)"),
                tasks.stream().map(Task::toString).toList());
    }

    @Test
    void storage_load_malformedFormats_warnsWithLineNumberAndPreservesLaterTasks() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        List<String> invalidRecords = List.of("", "T | 0", "X | 0 | unknown", "D | 0 | missing date",
                "E | 0 | missing times", "D | 0 | date | Feb 29 2028 18:00 | extra",
                "E | 0 | event | Mon 2pm to 4pm | extra", "T | done | bad status", "T | 0 | bad\ttext",
                "E | 0 | event | Mon 2pm to 4pm to 5pm", "E | 0 | event |  to 4pm",
                "E | 0 | event | Mon 2pm to 2026-09-12 1000",
                "E | 0 | event | 2026-09-12 0900 to 4pm");

        for (String invalid : invalidRecords) {
            String original = "T | 0 | first\n" + invalid + "\nT | 1 | last\n";
            Files.writeString(dataFile, original);
            Storage storage = new Storage(dataFile.toString());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream originalOutput = System.out;
            ArrayList<Task> tasks;
            try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
                System.setOut(capturedOutput);
                tasks = storage.load();
            } finally {
                System.setOut(originalOutput);
            }
            assertEquals(List.of("[T][ ] first", "[T][X] last"),
                    tasks.stream().map(Task::toString).toList(), invalid);
            assertEquals("     I skipped invalid saved task on line 2. "
                    + "Repair the data file and restart before making changes.\n",
                    output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n"), invalid);
            assertThrows(BkxssException.class, () -> storage.save(tasks), invalid);
            assertEquals(original, Files.readString(dataFile), invalid);
        }
    }

    @Test
    void storage_save_duplicateTasksOfEveryType_preservesOriginalAndAllowsRetry() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String original = "T | 0 | original\n";
        Files.writeString(dataFile, original);
        Storage storage = new Storage(dataFile.toString());
        List<Task> originals = List.of(new Todo("read"),
                new Deadline("return", LocalDateTime.of(2028, 2, 29, 18, 0)),
                new Event("meeting", "2028-02-29 2300", "2028-03-01 0100"));
        List<Task> duplicates = List.of(new Todo("read"),
                new Deadline("return", LocalDateTime.of(2028, 2, 29, 18, 0)),
                new Event("meeting", "2028-02-29 2300", "2028-03-01 0100"));

        for (int index = 0; index < originals.size(); index++) {
            Task duplicate = duplicates.get(index);
            duplicate.markAsDone();
            ArrayList<Task> tasks = new ArrayList<>(List.of(originals.get(index), duplicate));
            assertThrows(BkxssException.class, () -> storage.save(tasks));
            assertEquals(original, Files.readString(dataFile));
        }
        storage.save(new ArrayList<>(originals));
        assertEquals(3, storage.load().size());
        assertNoTemporaryFiles();
    }

    @Test
    void storage_save_unsupportedTaskOrInvalidEvent_preservesOriginalAndCleansUp() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String original = "T | 0 | original\n";
        Files.writeString(dataFile, original);
        Storage storage = new Storage(dataFile.toString());
        List<Task> invalidTasks = List.of(new UnsupportedTask(), new Todo("bad\nrecord"),
                new Todo(" "), new Event("meeting", "", "4pm"),
                new Event("meeting", "Mon 2pm", "4pm to 5pm"),
                new Event("meeting", "2028-02-29 2300", "2028-02-29 2200"));

        for (Task task : invalidTasks) {
            assertThrows(BkxssException.class, () -> storage.save(new ArrayList<>(List.of(task))));
            assertEquals(original, Files.readString(dataFile));
            assertNoTemporaryFiles();
        }
    }

    @Test
    void storage_save_directoryDestination_keepsContentsAndAllowsRetry() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.createDirectory(dataFile);
        Path marker = dataFile.resolve("marker.txt");
        Files.writeString(marker, "keep");
        Storage storage = new Storage(dataFile.toString());

        assertThrows(BkxssException.class, () -> storage.save(new ArrayList<>()));
        assertEquals("keep", Files.readString(marker));
        Files.delete(marker);
        Files.delete(dataFile);
        storage.save(new ArrayList<>(List.of(new Todo("retry"))));
        assertEquals("[T][ ] retry", storage.load().getFirst().toString());
    }

    @Test
    void storage_load_removedCorruptFile_resetsSaveBlock() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "broken\n");
        Storage storage = new Storage(dataFile.toString());
        assertTrue(storage.load().isEmpty());
        assertThrows(BkxssException.class, () -> storage.save(new ArrayList<>()));

        Files.delete(dataFile);

        assertTrue(storage.load().isEmpty());
        storage.save(new ArrayList<>(List.of(new Todo("recovered"))));
        assertEquals("[T][ ] recovered", storage.load().getFirst().toString());
    }

    /** Ensures successful and failed saves leave no temporary replacement records behind. */
    private void assertNoTemporaryFiles() throws Exception {
        try (var paths = Files.list(temporaryDirectory)) {
            assertEquals(List.of("tasks.txt"), paths.map(path -> path.getFileName().toString()).toList());
        }
    }

    /** Represents an unknown task subtype to exercise the persistence boundary. */
    private static class UnsupportedTask extends Task {
        private UnsupportedTask() {
            super("unsupported");
        }
    }
}

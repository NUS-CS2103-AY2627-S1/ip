import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import soar.exception.StorageException;
import soar.storage.Storage;
import soar.task.Deadline;
import soar.task.Event;
import soar.task.Task;
import soar.task.ToDo;

/**
 * Checks that valid persisted tasks can be reconstructed from disk.
 */
public class StorageTest {
    /**
     * Loads a representative storage file and checks its task types and states.
     *
     * @param args command-line arguments; they are not used
     * @throws Exception if the fixture cannot be written or read
     */
    public static void main(String[] args) throws Exception {
        Path dataFile = Path.of("_temp", "storage-test-data.txt");
        Files.createDirectories(dataFile.getParent());
        verifyValidRecords(dataFile);
        verifyMissingFile(Path.of("_temp", "missing-storage-test-data.txt"));
        verifyMissingFolderCreation();
        verifyAbsolutePathRejected(dataFile);
        verifyEscapedTextRoundTrip(Path.of("_temp", "a"));
        verifyMalformedRecords(dataFile);
        verifySaveFailure(Path.of("_temp", "storage-target-directory"));

        System.out.println("[PASS] Storage handled relative paths, missing files and folders, "
                + "valid, escaped, malformed, and unwritable data");
    }

    /** Verifies reconstruction of every supported task type and completion state. */
    private static void verifyValidRecords(Path dataFile) throws Exception {
        Files.write(dataFile, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-06-06",
                "E | 1 | project meeting | Aug 6th 2pm | 4pm"));

        List<Task> tasks = new Storage(dataFile).load();

        require(tasks.size() == 3, "Expected three loaded tasks");
        require(tasks.get(0) instanceof ToDo, "Expected the first task to be a todo");
        require(tasks.get(1) instanceof Deadline, "Expected the second task to be a deadline");
        require(tasks.get(2) instanceof Event, "Expected the third task to be an event");
        require(tasks.get(0).toString().equals("[T][X] read book"), "Todo data was not restored");
        require(tasks.get(1).toString().equals("[D][ ] return book (by: Jun 06 2019)"),
                "Deadline data was not restored");
        Deadline deadline = (Deadline) tasks.get(1);
        require(deadline.getBy().equals(LocalDate.of(2019, 6, 6).atStartOfDay()),
                "Deadline was not restored as a typed date");
        require(!deadline.hasTime(), "A date-only deadline should not gain a time");
        require(tasks.get(2).toString().equals(
                "[E][X] project meeting (from: Aug 6th 2pm to: 4pm)"),
                "Event data was not restored");
    }

    /** Verifies that a first run without a data file starts with an empty list. */
    private static void verifyMissingFile(Path dataFile) throws Exception {
        Files.deleteIfExists(dataFile);
        require(new Storage(dataFile).load().isEmpty(), "A missing file should load an empty list");
    }

    /** Verifies that saving creates a missing relative data-folder hierarchy. */
    private static void verifyMissingFolderCreation() throws Exception {
        Path testRoot = Files.createTempDirectory(Path.of("_temp"), "missing-folder-");
        Path dataFile = testRoot.resolve("data").resolve("nested").resolve("soar.txt");

        Storage storage = new Storage(dataFile);
        require(storage.load().isEmpty(), "A file in a missing folder should load an empty list");
        storage.save(List.of(new ToDo("created with its folders")));

        require(Files.isRegularFile(dataFile), "Saving did not create the missing data folders");
        require(storage.load().size() == 1, "The task saved in a new folder could not be loaded");
    }

    /** Verifies that storage cannot be configured with an OS-specific absolute path. */
    private static void verifyAbsolutePathRejected(Path relativePath) {
        try {
            new Storage(relativePath.toAbsolutePath());
            throw new AssertionError("An absolute data path should be rejected");
        } catch (IllegalArgumentException e) {
            require(e.getMessage().contains("relative"),
                    "Absolute-path rejection should explain the relative-path requirement");
        }
    }

    /** Verifies that structural and line-breaking characters survive save and load. */
    private static void verifyEscapedTextRoundTrip(Path dataFile) throws Exception {
        ArrayList<Task> original = new ArrayList<>();
        original.add(new ToDo("pipe | slash \\ newline\ncarriage\rreturn"));
        original.add(new Deadline("submit | report", LocalDateTime.of(2019, 12, 2, 18, 0)));
        original.add(new Event("team sync", "room | one", "room \\ two"));
        original.get(1).markAsDone();

        Storage storage = new Storage(dataFile);
        storage.save(original);
        List<Task> loaded = storage.load();

        require(loaded.size() == original.size(), "Round-trip changed the task count");
        for (int i = 0; i < original.size(); i++) {
            require(loaded.get(i).toString().equals(original.get(i).toString()),
                    "Round-trip changed task " + (i + 1));
        }
    }

    /** Verifies that malformed records fail with their exact line number. */
    private static void verifyMalformedRecords(Path dataFile) throws Exception {
        expectInvalid(dataFile, List.of(""), "blank records");
        expectInvalid(dataFile, List.of("X | 0 | unknown"), "unknown task type");
        expectInvalid(dataFile, List.of("T | 2 | bad status"), "completion status");
        expectInvalid(dataFile, List.of("D | 0 | missing date"), "requires 4 fields");
        expectInvalid(dataFile, List.of("D | 0 | bad date | 2019-02-29"), "deadline date");
        expectInvalid(dataFile, List.of("T | 0 | task | extra"), "requires 3 fields");
        expectInvalid(dataFile, List.of("T | 0 |  "), "must not be empty");
        expectInvalid(dataFile, List.of("T | 0 | bad\\qescape"), "unsupported escape");
        expectInvalid(dataFile, List.of("T | 0 | valid", "E | 0 | incomplete"), "line 2");
    }

    /** Verifies that a failed replacement is reported and its temporary file is cleaned up. */
    private static void verifySaveFailure(Path dataFile) throws Exception {
        Files.createDirectories(dataFile);
        try {
            new Storage(dataFile).save(List.of(new ToDo("cannot save here")));
            throw new AssertionError("Saving over a directory should fail");
        } catch (java.io.IOException expected) {
            try (var files = Files.list(dataFile.getParent())) {
                require(files.noneMatch(path -> path.getFileName().toString()
                                .startsWith(dataFile.getFileName().toString())
                                && path.getFileName().toString().endsWith(".tmp")),
                        "A failed save left a temporary file behind");
            }
        }
    }

    /** Writes malformed lines and verifies that loading rejects them clearly. */
    private static void expectInvalid(Path dataFile, List<String> lines, String expectedMessage)
            throws Exception {
        Files.write(dataFile, lines);
        try {
            new Storage(dataFile).load();
            throw new AssertionError("Expected malformed data to be rejected: " + lines);
        } catch (StorageException e) {
            require(e.getMessage().contains(expectedMessage),
                    "Unexpected malformed-data message: " + e.getMessage());
        }
    }

    /**
     * Fails the test with a useful message when a condition is false.
     *
     * @param condition condition that must hold
     * @param message explanation shown when the condition fails
     */
    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}

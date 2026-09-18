package lloyd.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lloyd.task.Deadline;
import lloyd.task.Event;
import lloyd.task.Task;
import lloyd.task.Todo;

/**
 * Provides a small console test driver for the {@link Storage} class.
 */
public class StorageTest {

    /**
     * Runs the storage checks and prints each successful result.
     *
     * @param args Command-line arguments, which are not used.
     * @throws IOException If the temporary test files cannot be accessed.
     */
    public static void main(String[] args) throws IOException {
        Path testDirectory = Files.createTempDirectory("lloyd-storage-test-");

        testSaveFormat(testDirectory.resolve("nested").resolve("tasks.txt"));
        testLoad(testDirectory.resolve("load.txt"));
        testMissingFile(testDirectory.resolve("missing").resolve("tasks.txt"));
        testInvalidData(testDirectory.resolve("invalid.txt"));
        testInvalidSavePreservesFile(testDirectory.resolve("preserved.txt"));
        testUnwritableLocation(testDirectory.resolve("blocked"));
        testReservedDelimiter(testDirectory.resolve("reserved.txt"));
    }

    /** Verifies the exact text format and automatic parent-directory creation. */
    private static void testSaveFormat(Path filePath) throws IOException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 6));
        deadline.mark();
        Event event = new Event("project meeting",
                LocalDateTime.of(2026, 9, 7, 14, 0),
                LocalDateTime.of(2026, 9, 7, 16, 0));

        new Storage(filePath).save(List.of(todo, deadline, event));

        List<String> expected = List.of(
                "T | 0 | read book",
                "D | 1 | return book | 2026-09-06",
                "E | 0 | project meeting | 2026-09-07T14:00 | 2026-09-07T16:00"
        );
        requireEquals(expected,
                Files.readAllLines(filePath, StandardCharsets.UTF_8),
                "saved file contents");
        System.out.println("Storage save format: PASSED");
    }

    /** Verifies task types, details, ordering, and completion state after loading. */
    private static void testLoad(Path filePath) throws IOException {
        Files.write(filePath, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-09-06",
                "E | 1 | project meeting | 2026-09-07T14:00 | 2026-09-07T16:00"
        ), StandardCharsets.UTF_8);

        ArrayList<Task> tasks = new Storage(filePath).load();

        requireEquals(3, tasks.size(), "loaded task count");
        requireEquals("[T][X] read book", tasks.get(0).toString(), "loaded todo");
        requireEquals("[D][ ] return book (by: Sep 6 2026)",
                tasks.get(1).toString(), "loaded deadline");
        requireEquals("[E][X] project meeting (from: Sep 7 2026, 2:00 PM"
                        + " to: Sep 7 2026, 4:00 PM)",
                tasks.get(2).toString(), "loaded event");
        System.out.println("Storage load: PASSED");
    }

    /** Verifies that first use creates the directory and an empty storage file. */
    private static void testMissingFile(Path filePath) throws IOException {
        ArrayList<Task> tasks = new Storage(filePath).load();

        requireEquals(0, tasks.size(), "new storage task count");
        requireEquals(true, Files.isRegularFile(filePath), "new storage file creation");
        System.out.println("Missing storage initialization: PASSED");
    }

    /** Verifies that malformed saved data is reported instead of silently ignored. */
    private static void testInvalidData(Path filePath) throws IOException {
        List<String> invalidLines = List.of(
                "T | 2 | invalid status",
                "X | 0 | unknown type",
                "D | 0 | missing deadline",
                "E | 0 | meeting | Monday",
                "D | 0 | task | not-a-date",
                "E | 0 | meeting | 2026-09-07T14:00 | not-a-date-time",
                "T | 0 |    ",
                "T | 0 | compare A|B"
        );

        for (String invalidLine : invalidLines) {
            Files.writeString(filePath, invalidLine, StandardCharsets.UTF_8);
            try {
                new Storage(filePath).load();
                throw new AssertionError(
                        "Invalid saved data should cause an IOException: " + invalidLine);
            } catch (IOException expected) {
                requireEquals(true, expected.getMessage().contains("line 1"),
                        "invalid-data error line number");
            }
        }
        System.out.println("Invalid storage data: PASSED");
    }

    /** Verifies validation occurs before an existing file can be replaced. */
    private static void testInvalidSavePreservesFile(Path filePath) throws IOException {
        Files.writeString(filePath, "T | 0 | original", StandardCharsets.UTF_8);

        try {
            new Storage(filePath).save(List.of(new Todo("compare A | B")));
            throw new AssertionError("Invalid task data should be rejected");
        } catch (IllegalArgumentException expected) {
            requireEquals("T | 0 | original",
                    Files.readString(filePath, StandardCharsets.UTF_8),
                    "storage contents after rejected save");
        }
        System.out.println("Rejected save preservation: PASSED");
    }

    /** Verifies that filesystem write failures are reported as IOExceptions. */
    private static void testUnwritableLocation(Path blockedPath) throws IOException {
        Files.writeString(blockedPath, "not a directory", StandardCharsets.UTF_8);
        Path filePath = blockedPath.resolve("tasks.txt");

        try {
            new Storage(filePath).save(List.of(new Todo("read book")));
            throw new AssertionError("An unusable directory should cause an IOException");
        } catch (IOException expected) {
            // Expected: the parent path is a regular file rather than a directory.
        }
        System.out.println("Storage write failure reporting: PASSED");
    }

    /** Verifies that a field cannot contain the reserved delimiter character. */
    private static void testReservedDelimiter(Path filePath) throws IOException {
        try {
            new Storage(filePath).save(List.of(new Todo("compare A | B")));
            throw new AssertionError("A reserved delimiter should be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected: accepting this character would make the saved line ambiguous.
        }
        System.out.println("Reserved delimiter validation: PASSED");
    }

    /** Fails the test immediately when expected and actual values differ. */
    private static void requireEquals(Object expected, Object actual, String checkName) {
        if (!expected.equals(actual)) {
            throw new AssertionError(checkName + ": expected " + expected + " but got " + actual);
        }
    }
}

package kia;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Black-box tests for Kia's command handling and in-memory task state.
 *
 * <p>Run this class with {@code java KiaTest}.</p>
 */
public class KiaTest {
    private static final Path TASK_FILE = Path.of("data", "kia.txt");
    /**
     * Runs all Kia command tests.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        testInterleavedValidAndInvalidCommands();
        testMalformedCommandsDoNotCreateTasks();
        testInvalidDatesDoNotCreateTasks();
        testFindCommand();
        testByeStopsCommandProcessing();
        testTaskChangesArePersisted();
        testTasksAreLoadedOnStartup();
        testMalformedRecordsAreSkipped();
        testDataDirectoryPathIsHandled();
        testMissingTaskFileStartsWithEmptyList();
        System.out.println("KiaTest passed.");
    }

    /**
     * Checks that invalid commands interleaved with valid ones do not affect
     * task numbering or completion state.
     */
    private static void testInterleavedValidAndInvalidCommands() {
        String input = String.join("\n",
                "todo read book",
                "blah",
                "deadline return book /by 2019-12-02",
                "deadline missing date",
                "event meeting /from Mon 2pm /to 4pm",
                "todo",
                "list",
                "mark 1",
                "list",
                "mark 99",
                "unmark 1",
                "list",
                "bye",
                "todo after bye") + "\n";

        String output = runKia(input);

        assertContains(output, "Hey!!! What did you do... >:[");
        assertContains(output, "Hey!!! A deadline must include a /by date or time. >:[");
        assertContains(output, "Hey!!! The description of a todo cannot be empty. >:[");
        assertContains(output, "Hey!!! The task number is invalid. >:[");
        assertContains(output, "1.[T][ ] read book");
        assertContains(output, "2.[D][ ] return book (by: Dec 02 2019)");
        assertContains(output, "3.[E][ ] meeting (from: Mon 2pm to: 4pm)");
        assertContains(output, "1.[T][X] read book");
        assertLastContains(output, "1.[T][ ] read book");
        assertNotContains(output, "4.[");
        assertNotContains(output, "after bye");
    }

    /**
     * Checks malformed commands and invalid task numbers when the list is empty.
     */
    private static void testMalformedCommandsDoNotCreateTasks() {
        String input = String.join("\n",
                "deadline",
                "event",
                "mark",
                "unmark",
                "todo   ",
                "list",
                "bye") + "\n";

        String output = runKia(input);

        assertContains(output, "Hey!!! A deadline must include a /by date or time. >:[");
        assertContains(output, "Hey!!! An event must include /from and /to date or time values. >:[");
        assertContains(output, "Hey!!! The task number is invalid. >:[");
        assertContains(output, "Hey!!! The description of a todo cannot be empty. >:[");
        assertNotContains(output, "1.[");
    }

    /**
     * Checks that commands after {@code bye} are not processed.
     */
    private static void testByeStopsCommandProcessing() {
        String output = runKia("bye\ntodo should not be added\n");

        assertContains(output, "Aww, goodbye. Hope to see you again soon!");
        assertNotContains(output, "should not be added");
    }

    /** Checks that malformed calendar dates are rejected without changing state. */
    private static void testInvalidDatesDoNotCreateTasks() {
        String output = runKia("deadline impossible /by 2019-02-30\nlist\nbye\n");

        assertContains(output, "Hey!!! The /by date or time must use yyyy-MM-dd format. >:[");
        assertNotContains(output, "1.[D]");
    }

    /** Checks matching, no-match, and missing-keyword find commands. */
    private static void testFindCommand() {
        String input = String.join("\n",
                "todo read book",
                "deadline return book /by 2019-12-02",
                "todo buy bread",
                "find BOOK",
                "find spaceship",
                "find",
                "bye") + "\n";

        String output = runKia(input);

        assertContains(output, "Here are the matching tasks in your list:");
        assertContains(output, "1.[T][ ] read book");
        assertContains(output, "2.[D][ ] return book (by: Dec 02 2019)");
        assertContains(output, "Hey!!! A find command must include a keyword. >:[");
        assertNotContains(output, "3.[T][ ] buy bread");
    }

    /**
     * Runs Kia with scripted standard input and captures standard output.
     *
     * @param input commands to provide to Kia
     * @return Kia's complete output
     */
    private static String runKia(String input) {
        clearTaskFile();
        try {
            return runKiaKeepingFile(input);
        } finally {
            clearTaskFile();
        }
    }

    /** Runs Kia while retaining its data file for persistence assertions. */
    private static String runKiaKeepingFile(String input) {
        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Kia.main(new String[0]);
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    /** Checks that add, completion, undo, and delete operations are persisted. */
    private static void testTaskChangesArePersisted() {
        clearTaskFile();
        try {
            runKiaKeepingFile(String.join("\n",
                    "todo write this",
                    "deadline finish this /by 2019-10-18",
                    "mark 1",
                    "unmark 1",
                    "delete 2",
                    "bye") + "\n");
            try {
                String data = Files.readString(TASK_FILE, StandardCharsets.UTF_8);
                require(data.contains("T | 0 | write this"), "Expected the remaining task to be persisted.");
                require(!data.contains("finish this"), "Deleted tasks must not remain in the data file.");
            } catch (java.io.IOException e) {
                throw new AssertionError("Could not read the data file", e);
            }
        } finally {
            clearTaskFile();
        }
    }

    /** Checks that valid records are loaded and displayed at startup. */
    private static void testTasksAreLoadedOnStartup() {
        clearTaskFile();
        try {
            try {
                Files.createDirectories(TASK_FILE.getParent());
                Files.writeString(TASK_FILE, String.join("\n",
                        "not a task record",
                        "T | 1 | loaded todo",
                        "D | 0 | loaded deadline | 2019-10-15",
                        "E | 0 | loaded event | 2pm | 4pm") + "\n", StandardCharsets.UTF_8);
            } catch (java.io.IOException e) {
                throw new AssertionError("Could not prepare the data file", e);
            }
            String output = runKiaKeepingFile("list\nbye\n");
            assertContains(output, "1.[T][X] loaded todo");
            assertContains(output, "2.[D][ ] loaded deadline (by: Oct 15 2019)");
            assertContains(output, "3.[E][ ] loaded event (from: 2pm to: 4pm)");
            assertNotContains(output, "4.[");
        } finally {
            clearTaskFile();
        }
    }

    /** Checks that malformed records do not prevent later valid records loading. */
    private static void testMalformedRecordsAreSkipped() {
        clearTaskFile();
        try {
            try {
                Files.createDirectories(TASK_FILE.getParent());
                Files.writeString(TASK_FILE, String.join("\n",
                        "\uFEFFT | 1 | bom todo",
                        "T | 2 | invalid status",
                        "T | 0 | ",
                        "X | 0 | unknown type",
                        "D | 0 | invalid date | 2019-02-30",
                        "D | 0 | extra field | 2019-10-15 | unexpected",
                        "E | 0 | missing end | 2pm",
                        "T | 0 | valid after errors") + "\n", StandardCharsets.UTF_8);
            } catch (java.io.IOException e) {
                throw new AssertionError("Could not prepare malformed task data", e);
            }
            String output = runKiaKeepingFile("list\nbye\n");
            assertContains(output, "1.[T][X] bom todo");
            assertContains(output, "2.[T][ ] valid after errors");
            assertNotContains(output, "3.[");
        } finally {
            clearTaskFile();
        }
    }

    /** Checks that a directory at the data-file path is reported without crashing. */
    private static void testDataDirectoryPathIsHandled() {
        clearTaskFile();
        try {
            try {
                Files.createDirectories(TASK_FILE.getParent());
                Files.createDirectory(TASK_FILE);
            } catch (java.io.IOException e) {
                throw new AssertionError("Could not prepare the invalid data path", e);
            }
            String output = runKiaKeepingFile("list\nbye\n");
            assertContains(output, "Hey!!! Unable to load tasks from disk. >:[");
            assertContains(output, "Here ya go! These are the tasks in your list:");
            assertNotContains(output, "1.[");
        } finally {
            clearTaskFile();
        }
    }

    /** Checks that a missing data file starts Kia with an empty list. */
    private static void testMissingTaskFileStartsWithEmptyList() {
        clearTaskFile();
        String output = runKiaKeepingFile("list\nbye\n");
        assertContains(output, "Here ya go! These are the tasks in your list:");
        assertNotContains(output, "1.[");
    }

    /** Removes the test data file so each scenario starts independently. */
    private static void clearTaskFile() {
        try {
            Files.deleteIfExists(TASK_FILE);
        } catch (java.io.IOException e) {
            throw new AssertionError("Could not clean up the data file", e);
        }
    }

    /** Asserts that captured output contains the expected text. */
    private static void assertContains(String output, String expected) {
        require(output.contains(expected), "Expected output to contain: " + expected);
    }

    /** Asserts that the expected text occurs after the final task-list heading. */
    private static void assertLastContains(String output, String expected) {
        int lastList = output.lastIndexOf("Here ya go! These are the tasks in your list:");
        int lastMatch = output.lastIndexOf(expected);
        require(lastMatch > lastList, "Expected the final list to contain: " + expected);
    }

    /** Asserts that captured output does not contain unexpected text. */
    private static void assertNotContains(String output, String unexpected) {
        require(!output.contains(unexpected), "Did not expect output to contain: " + unexpected);
    }

    /** Raises an assertion failure when a test condition is false. */
    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tardt.TardT;

/**
 * Runs the console UI cases described in {@code src/test/ui-test-plan.md}.
 *
 * <p>Each case is its own {@code @Test} method so a failed case is reported
 * independently instead of stopping all later cases from running.</p>
 */
class UiTest {
    /**
     * The project-relative data file used by the application and checked by the save test.
     */
    private static final Path SAVE_FILE = Path.of("data", "tasks.txt");

    private static final String WELCOME = "Hello! I'm TardT. \n"
            + "What can I do for you? \n"
            + "____________________________________________________________\n";
    private static final String SEPARATOR = "____________________________________________________________\n";
    private static final String GOODBYE = SEPARATOR + "Bye. Hope to see you again soon!\n" + SEPARATOR;

    /**
     * Whether a save file existed before this test session, so it can be restored afterward.
     */
    private boolean saveFileExisted;
    /**
     * The save file's original contents, if any, so they can be restored afterward.
     */
    private String originalSavedData;

    /**
     * Records any pre-existing save file, then removes it so each test starts with a clean slate.
     */
    @BeforeEach
    void setUp() throws IOException {
        saveFileExisted = Files.exists(SAVE_FILE);
        if (saveFileExisted) {
            originalSavedData = Files.readString(SAVE_FILE);
        }
        deleteSaveFile();
    }

    /**
     * Restores whatever save file existed (or its absence) before this test session ran.
     */
    @AfterEach
    void tearDown() {
        restoreSaveFile(saveFileExisted, originalSavedData);
    }

    @Test
    void bye_exitsApplication_showsWelcomeAndGoodbye() {
        String input = "bye\n";
        String expectedOutput = WELCOME + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void todo_addTask_savesTaskToFile() throws IOException {
        String input = "todo read book\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Got it. I've added this task:\n"
                + "  [T][ ] read book | Priority: LOW\n"
                + "Now you have 1 tasks in the list.\n"
                + SEPARATOR + GOODBYE;
        String expectedSavedData = "T | 0 | read book";

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
        assertEquals(expectedSavedData, Files.readString(SAVE_FILE));
    }

    @Test
    void list_startupWithSavedTasks_reconstructsAndListsTasks() throws IOException {
        String initialSavedData = "T | 1 | read book\n"
                + "D | 0 | return book | 2026-08-23T15:00\n"
                + "E | 0 | project meeting | 2026-10-20T15:00 | 2026-10-20T17:00";
        String input = "list\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "1. [T][X] read book | Priority: LOW\n"
                + "2. [D][ ] return book | Priority: LOW (by: Aug 23 2026 15:00)\n"
                + "3. [E][ ] project meeting | Priority: LOW (from: Oct 20 2026 15:00 to: Oct 20 2026 17:00)\n"
                + SEPARATOR + GOODBYE;

        writeInitialSavedData(initialSavedData);
        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void unknownCommand_isRejected_andSubsequentValidCommandStillWorks() {
        String input = "nonsense\ntodo read book\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "'nonsense' is not a valid input.\n"
                + "Valid input formats: \n"
                + "bye -> exits the interface\n"
                + "list -> lists all the tasks and their status\n"
                + "mark [task number] -> marks the task and show their status\n"
                + "unmark [task number] -> unmarks the task and show their status\n"
                + "Priority is optional; omit it to use low priority.\n"
                + "todo [task name] (optional: /priority low|medium|high) -> adds a todo task to taskList\n"
                + "deadline [task name] /by [deadline] (optional: /priority low|medium|high)"
                + " -> adds a deadline task to taskList\n"
                + "event [task name] /from [start time] /to [end time] (optional: /priority low|medium|high)"
                + " -> adds an event task to taskList\n"
                + "delete [task number] -> deletes a task from taskList\n"
                + "find [search string] -> finds a task consisting of the search string\n"
                + SEPARATOR + SEPARATOR
                + "Got it. I've added this task:\n"
                + "  [T][ ] read book | Priority: LOW\n"
                + "Now you have 1 tasks in the list.\n"
                + SEPARATOR + SEPARATOR
                + "1. [T][ ] read book | Priority: LOW\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void deadline_addValidDeadline_isStoredAndListedWithDeadlineValue() {
        String input = "deadline submit assignment /by 2026-10-20T17:00\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Got it. I've added this task:\n"
                + "  [D][ ] submit assignment | Priority: LOW (by: Oct 20 2026 17:00)\n"
                + "Now you have 1 tasks in the list.\n"
                + SEPARATOR + SEPARATOR
                + "1. [D][ ] submit assignment | Priority: LOW (by: Oct 20 2026 17:00)\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void deadline_malformedDeadline_isRejectedWithoutAddingSecondTask() {
        String input = "todo read book\ndeadline submit assignment\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Got it. I've added this task:\n"
                + "  [T][ ] read book | Priority: LOW\n"
                + "Now you have 1 tasks in the list.\n"
                + SEPARATOR + SEPARATOR
                + "Invalid format. Use: deadline [task name] /by [deadline]"
                + " (optional: /priority low|medium|high).\n"
                + SEPARATOR + SEPARATOR
                + "1. [T][ ] read book | Priority: LOW\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void event_addValidEvent_isStoredAndListedWithStartAndEndTimes() {
        String input = "event lecture /from 2026-10-20T15:00 /to 2026-10-20T16:00\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Got it. I've added this task:\n"
                + "  [E][ ] lecture | Priority: LOW (from: Oct 20 2026 15:00 to: Oct 20 2026 16:00)\n"
                + "Now you have 1 tasks in the list.\n"
                + SEPARATOR + SEPARATOR
                + "1. [E][ ] lecture | Priority: LOW (from: Oct 20 2026 15:00 to: Oct 20 2026 16:00)\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void mark_nonNumericIndex_isRejectedWithoutChangingTaskStatus() {
        String input = "todo read book\nmark one\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Got it. I've added this task:\n"
                + "  [T][ ] read book | Priority: LOW\n"
                + "Now you have 1 tasks in the list.\n"
                + SEPARATOR + SEPARATOR
                + "'one' is not a valid integer.\n"
                + SEPARATOR + SEPARATOR
                + "1. [T][ ] read book | Priority: LOW\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void delete_existingTask_removesTaskAndRenumbersRemaining() {
        String input = "todo read book\ndeadline submit assignment /by 2026-10-20T17:00\ndelete 1\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Got it. I've added this task:\n"
                + "  [T][ ] read book | Priority: LOW\n"
                + "Now you have 1 tasks in the list.\n"
                + SEPARATOR + SEPARATOR
                + "Got it. I've added this task:\n"
                + "  [D][ ] submit assignment | Priority: LOW (by: Oct 20 2026 17:00)\n"
                + "Now you have 2 tasks in the list.\n"
                + SEPARATOR + SEPARATOR
                + "Noted. I've removed this task:\n"
                + "  [T][ ] read book | Priority: LOW\n"
                + "Now you have 1 tasks in the list.\n"
                + SEPARATOR + SEPARATOR
                + "1. [D][ ] submit assignment | Priority: LOW (by: Oct 20 2026 17:00)\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void delete_nonNumericIndex_isRejectedWithoutChangingTasks() {
        String input = "todo read book\ndelete one\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Got it. I've added this task:\n"
                + "  [T][ ] read book | Priority: LOW\n"
                + "Now you have 1 tasks in the list.\n"
                + SEPARATOR + SEPARATOR
                + "'one' is not a valid integer.\n"
                + SEPARATOR + SEPARATOR
                + "1. [T][ ] read book | Priority: LOW\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void blankCommand_isRejected_andApplicationContinues() {
        String input = "\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Please enter a command.\n"
                + SEPARATOR + SEPARATOR
                + "Your task list is empty!\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void todo_invalidPriority_isRejectedWithoutAddingTask() {
        String input = "todo /priority urgent\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Invalid priority. Priority is optional; when included, use /priority low, /priority medium,"
                + " or /priority high.\n"
                + SEPARATOR + SEPARATOR
                + "Your task list is empty!\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void todo_repeatedPriority_isRejectedWithoutAddingTask() {
        String input = "todo read book /priority low /priority high\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Invalid priority. Priority is optional; when included, use /priority low, /priority medium,"
                + " or /priority high.\n"
                + SEPARATOR + SEPARATOR
                + "Your task list is empty!\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void event_endBeforeStart_isRejectedWithoutAddingTask() {
        String input = "event workshop /from 2026-10-20T16:00 /to 2026-10-20T15:00\nlist\nbye\n";
        String expectedOutput = WELCOME + SEPARATOR
                + "Event end time must be after its start time.\n"
                + SEPARATOR + SEPARATOR
                + "Your task list is empty!\n"
                + SEPARATOR + GOODBYE;

        String actualOutput = runApplication(input);

        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Runs the application once with the supplied console input and captures its output.
     */
    private String runApplication(String input) {
        PrintStream originalOutput = System.out;
        java.io.InputStream originalInput = System.in;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
            TardT.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }

    /**
     * Deletes the save file so each test starts with an isolated file state.
     */
    private void deleteSaveFile() {
        try {
            Files.deleteIfExists(SAVE_FILE);
        } catch (IOException exception) {
            throw new AssertionError("Unable to reset the test save file.", exception);
        }
    }

    /**
     * Writes a test's initial data so the application can load it at startup.
     */
    private void writeInitialSavedData(String initialSavedData) throws IOException {
        if (initialSavedData == null) {
            return;
        }
        Files.createDirectories(SAVE_FILE.getParent());
        Files.writeString(SAVE_FILE, initialSavedData);
    }

    /**
     * Restores any data that existed before this test ran.
     */
    private void restoreSaveFile(boolean saveFileExisted, String originalSavedData) {
        deleteSaveFile();
        if (!saveFileExisted) {
            return;
        }
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            Files.writeString(SAVE_FILE, originalSavedData);
        } catch (IOException exception) {
            throw new AssertionError("Unable to restore the original save file.", exception);
        }
    }
}

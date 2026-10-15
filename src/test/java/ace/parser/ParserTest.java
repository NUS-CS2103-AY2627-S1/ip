package ace.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ace.exception.AceException;
import ace.exception.MissingDescriptionException;
import ace.exception.WrongCommandException;
import ace.exception.WrongDateFormatException;
import ace.exception.WrongTaskNumberException;
import ace.task.TaskManager;
import ace.ui.Ui;

/**
 * Parser.parseCommand delegates to TaskManager, whose mutating methods call
 * Storage.save() against the real data/Ace.txt — back up/restore it around
 * each test, same reasoning as TaskManagerTest/StorageTest.
 */
public class ParserTest {
    private static final Path SAVE_FILE = Paths.get("data", "Ace.txt");
    private byte[] backup;
    private boolean wasFileExisting;
    private TaskManager taskManager;
    private Ui ui;

    @BeforeEach
    public void setUp() throws IOException {
        wasFileExisting = Files.exists(SAVE_FILE);
        if (wasFileExisting) {
            backup = Files.readAllBytes(SAVE_FILE);
        }
        taskManager = new TaskManager();
        ui = new Ui();
    }

    @AfterEach
    public void restoreRealSaveFile() throws IOException {
        if (wasFileExisting) {
            Files.write(SAVE_FILE, backup);
        } else {
            Files.deleteIfExists(SAVE_FILE);
        }
    }

    @Test
    public void parseCommand_validTodo_addsTaskAndReturnsFalse() throws Exception {
        boolean isExit = Parser.parseCommand("todo borrow book", taskManager, ui);

        assertFalse(isExit);
        assertEquals(1, taskManager.getCurrentNumberOfTasks());
    }

    @Test
    public void parseCommand_bye_returnsTrue() throws Exception {
        boolean isExit = Parser.parseCommand("bye", taskManager, ui);

        assertTrue(isExit);
    }

    @Test
    public void parseCommand_byeWithTrailingArgument_wrongCommandExceptionThrown() {
        // "bye" and "list" take no argument, so they're matched with plain
        // equality rather than isCommand's "starts with X " form — this
        // confirms that distinction actually holds, not just for "list".
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand("bye now", taskManager, ui));
    }

    @Test
    public void parseCommand_todoMissingDescription_missingDescriptionExceptionThrown() {
        assertThrows(MissingDescriptionException.class, () -> Parser.parseCommand("todo", taskManager, ui));
    }

    @Test
    public void parseCommand_unknownCommand_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand("gibberish", taskManager, ui));
    }

    @Test
    public void parseCommand_markValidNumber_marksTaskComplete() throws Exception {
        Parser.parseCommand("todo borrow book", taskManager, ui);

        Parser.parseCommand("mark 1", taskManager, ui);

        assertTrue(taskManager.getTask(0).isCompleted());
    }

    @Test
    public void parseCommand_markNonNumericArgument_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand("mark abc", taskManager, ui));
    }

    @Test
    public void parseCommand_markOutOfRangeNumber_wrongTaskNumberExceptionThrown() {
        assertThrows(WrongTaskNumberException.class, () -> Parser.parseCommand("mark 5", taskManager, ui));
    }

    @Test
    public void parseCommand_markBareNoArgument_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand("mark", taskManager, ui));
    }

    @Test
    public void parseCommand_validUnmark_unmarksTask() throws Exception {
        Parser.parseCommand("todo borrow book", taskManager, ui);
        Parser.parseCommand("mark 1", taskManager, ui);

        Parser.parseCommand("unmark 1", taskManager, ui);

        assertFalse(taskManager.getTask(0).isCompleted());
    }

    @Test
    public void parseCommand_validDelete_removesTask() throws Exception {
        Parser.parseCommand("todo borrow book", taskManager, ui);

        Parser.parseCommand("delete 1", taskManager, ui);

        assertEquals(0, taskManager.getCurrentNumberOfTasks());
    }

    @Test
    public void parseCommand_deleteMissingArgument_missingDescriptionExceptionThrown() {
        assertThrows(MissingDescriptionException.class, () -> Parser.parseCommand("delete", taskManager, ui));
    }

    @Test
    public void parseCommand_deleteOutOfRangeNumber_wrongTaskNumberExceptionThrown() {
        assertThrows(WrongTaskNumberException.class, () -> Parser.parseCommand("delete 1", taskManager, ui));
    }

    @Test
    public void parseCommand_validEvent_addsTask() throws Exception {
        boolean isExit = Parser.parseCommand("event meeting /from Mon 2pm /to 4pm", taskManager, ui);

        assertFalse(isExit);
        assertEquals(1, taskManager.getCurrentNumberOfTasks());
    }

    @Test
    public void parseCommand_eventMissingDescription_missingDescriptionExceptionThrown() {
        assertThrows(MissingDescriptionException.class, () -> Parser.parseCommand(
                "event /from Mon 2pm /to 4pm", taskManager, ui));
    }

    @Test
    public void parseCommand_eventMissingMarkers_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand("event meeting", taskManager, ui));
    }

    @Test
    public void parseCommand_eventMarkersOutOfOrder_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand(
                "event meeting /to 4pm /from Mon 2pm", taskManager, ui));
    }

    @Test
    public void parseCommand_eventEmptyStartTime_wrongCommandExceptionThrown() {
        // This is also a regression check: an empty start time between two
        // adjacent markers previously crashed the program with an uncaught
        // StringIndexOutOfBoundsException once whitespace normalization
        // collapsed the space between "/from" and "/to" into one.
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand(
                "event meeting /from  /to 4pm", taskManager, ui));
    }

    @Test
    public void parseCommand_eventEmptyEndTime_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand(
                "event meeting /from Mon 2pm /to ", taskManager, ui));
    }

    @Test
    public void parseCommand_validFind_doesNotThrow() {
        assertDoesNotThrowChecked(() -> {
            Parser.parseCommand("todo borrow book", taskManager, ui);
            Parser.parseCommand("find book", taskManager, ui);
        });
    }

    @Test
    public void parseCommand_findMissingKeyword_missingDescriptionExceptionThrown() {
        assertThrows(MissingDescriptionException.class, () -> Parser.parseCommand("find", taskManager, ui));
    }

    @Test
    public void parseCommand_findDifferentCase_stillMatches() throws Exception {
        Parser.parseCommand("todo read BOOK", taskManager, ui);

        String output = captureStdout(() -> Parser.parseCommand("find book", taskManager, ui));

        assertTrue(output.contains("read BOOK"));
    }

    @Test
    public void parseCommand_validDate_doesNotThrow() {
        assertDoesNotThrowChecked(() -> Parser.parseCommand("date 2/12/2019", taskManager, ui));
    }

    @Test
    public void parseCommand_dateMissingArgument_missingDescriptionExceptionThrown() {
        assertThrows(MissingDescriptionException.class, () -> Parser.parseCommand("date", taskManager, ui));
    }

    @Test
    public void parseCommand_dateUnparseableDate_wrongDateFormatExceptionThrown() {
        assertThrows(WrongDateFormatException.class, () -> Parser.parseCommand(
                "date not-a-date", taskManager, ui));
    }

    @Test
    public void parseCommand_validList_doesNotThrow() {
        assertDoesNotThrowChecked(() -> Parser.parseCommand("list", taskManager, ui));
    }

    @Test
    public void parseCommand_listWithTrailingArgument_wrongCommandExceptionThrown() {
        // "list" (like "bye") takes no argument, so "list foo" must not be
        // treated as a valid "list" that silently ignores "foo" — it should
        // fall through to the unrecognized-command case instead.
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand("list foo", taskManager, ui));
    }

    @Test
    public void parseCommand_deadlineMissingByClause_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand("deadline return book", taskManager, ui));
    }

    @Test
    public void parseCommand_deadlineUnparseableDate_wrongDateFormatExceptionThrown() {
        assertThrows(WrongDateFormatException.class, () -> Parser.parseCommand(
                "deadline return book /by not-a-date", taskManager, ui));
    }

    @Test
    public void parseCommand_deadlineMissingDescription_missingDescriptionExceptionThrown() {
        assertThrows(MissingDescriptionException.class, () -> Parser.parseCommand(
                "deadline /by 2/12/2019 1800", taskManager, ui));
    }

    @Test
    public void parseCommand_deadlineByMarkerWithNoDateAfter_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class, () -> Parser.parseCommand(
                "deadline return book /by ", taskManager, ui));
    }

    @Test
    public void parseCommand_repeatedInternalSpaces_collapsedToSingleSpace() throws Exception {
        Parser.parseCommand("todo   read   book", taskManager, ui);

        assertEquals("read book", taskManager.getTask(0).getDescription());
    }

    @Test
    public void parseCommand_doubleSpacesAroundByMarker_stillParsesCorrectly() throws Exception {
        // Before whitespace normalization was added, this would fail to find
        // the "/by" marker at all, since it required exactly one surrounding
        // space on each side.
        boolean isExit = Parser.parseCommand("deadline return book  /by  2/12/2019 1800", taskManager, ui);

        assertFalse(isExit);
        assertEquals(1, taskManager.getCurrentNumberOfTasks());
    }

    @Test
    public void parseCommand_errorMessage_includesUsageHint() {
        AceException exception = assertThrows(AceException.class, () -> Parser.parseCommand(
                "deadline return book", taskManager, ui));

        assertTrue(exception.getMessage().contains("Try: deadline <description> /by <date>"));
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    /**
     * Fails the test if action throws any exception (checked or unchecked) —
     * a checked-exception-friendly equivalent of
     * {@code org.junit.jupiter.api.Assertions#assertDoesNotThrow}, needed
     * since {@link Parser#parseCommand} declares a checked
     * {@link AceException}.
     *
     * @param action the code that should complete without throwing.
     */
    private static void assertDoesNotThrowChecked(ThrowingRunnable action) {
        try {
            action.run();
        } catch (Exception e) {
            throw new AssertionError("Expected no exception, but got: " + e, e);
        }
    }

    private static String captureStdout(ThrowingRunnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(originalOut);
        }
        return captured.toString();
    }
}

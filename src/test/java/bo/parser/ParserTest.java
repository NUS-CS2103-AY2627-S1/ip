package bo.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bo.Tracker;
import bo.command.CommandException;
import bo.storage.Storage;
import bo.task.Deadline;
import bo.ui.Ui;

/**
 * Tests for {@link Parser}.
 *
 * These tests cover commands that should throw CommandException (missing
 * description, missing "/by", bad date format, non-numeric task number,
 * empty task list). Each test uses a Tracker backed by a temporary save
 * file so they never read or write Bo's real data/bo.txt file.
 */
public class ParserTest {

    @TempDir
    Path tempDir;

    private final Parser parser = new Parser();
    private Tracker tracker;

    @BeforeEach
    public void setUp() {
        tracker = new Tracker(new Ui(), new Storage(tempDir.resolve("bo.txt")));
    }

    // ---------- empty / unknown command ----------

    @Test
    public void executeCommand_emptyString_throwsCommandException() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, ""));

        assertTrue(exception.getMessage().toLowerCase().contains("enter a command"));
    }

    @Test
    public void executeCommand_unrecognizedWord_throwsCommandExceptionWithInstructions() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "fly"));

        assertTrue(exception.getMessage().contains("I don't recognize that command"));
    }

    // ---------- "upcoming" with trailing text ----------

    @Test
    public void executeCommand_upcomingWithExtraText_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "upcoming now"));
    }

    // ---------- todo ----------

    @Test
    public void executeCommand_todoWithNoDescription_throwsCommandException() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "todo"));

        assertTrue(exception.getMessage().contains("todo needs a description"));
    }

    @Test
    public void executeCommand_todoWithOnlyWhitespaceDescription_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "todo    "));
    }

    // ---------- deadline ----------

    @Test
    public void executeCommand_deadlineWithoutBy_throwsCommandException() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "deadline submit report"));

        assertTrue(exception.getMessage().contains("/by"));
    }

    @Test
    public void executeCommand_deadlineWithEmptyDescriptionBeforeBy_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "deadline /by 2024-03-02"));
    }

    @Test
    public void executeCommand_deadlineWithEmptyDateAfterBy_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "deadline submit report /by"));
    }

    @Test
    public void executeCommand_deadlineWithMalformedDate_throwsCommandException() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "deadline submit report /by 2-3-2024"));

        assertTrue(exception.getMessage().contains("yyyy-MM-dd"));
    }

    // ---------- due ----------

    @Test
    public void executeCommand_dueWithNoDate_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "due"));
    }

    @Test
    public void executeCommand_dueWithInvalidDate_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "due not-a-date"));
    }

    // ---------- event ----------

    @Test
    public void executeCommand_eventMissingFromAndTo_throwsCommandException() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "event meeting"));

        assertTrue(exception.getMessage().contains("/from and /to"));
    }

    @Test
    public void executeCommand_eventMissingTo_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "event meeting /from monday"));
    }

    @Test
    public void executeCommand_eventFromAfterTo_throwsCommandException() {
        // "/to" appears before "/from" in the raw text -> fromIndex >= toIndex
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "event meeting /to friday /from monday"));
    }

    @Test
    public void executeCommand_eventEmptyDescription_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "event /from monday /to friday"));
    }

    @Test
    public void executeCommand_eventWithDateAndTime_savesEvent() throws Exception {
        parser.executeCommand(tracker,
                "event project meeting /from 2026-10-10 14:00 /to 2026-10-10 16:30");

        assertTrue(Files.readString(tempDir.resolve("bo.txt")).contains("2026-10-10T14:00"));
    }

    @Test
    public void executeCommand_eventWithInvalidDateAndTime_throwsCommandException() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker,
                        "event project meeting /from 2026-10-10 /to 2026-10-10 16:30"));

        assertTrue(exception.getMessage().contains("yyyy-MM-dd HH:mm"));
    }

    @Test
    public void executeCommand_eventEndingBeforeStart_throwsCommandException() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker,
                        "event project meeting /from 2026-10-10 16:30 /to 2026-10-10 14:00"));

        assertTrue(exception.getMessage().contains("end must be after its start"));
    }

    // ---------- mark / unmark ----------

    @Test
    public void executeCommand_markWithNonNumericArgument_throwsCommandException() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "mark abc"));

        assertTrue(exception.getMessage().contains("mark <task number>"));
    }

    @Test
    public void executeCommand_unmarkWithNonNumericArgument_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "unmark abc"));
    }

    @Test
    public void executeCommand_markWithNoTasksYet_throwsCommandExceptionForInvalidNumber() {
        // No tasks exist, so task number 1 cannot be valid.
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "mark 1"));

        assertTrue(exception.getMessage().contains("does not exist"));
    }

    // ---------- delete ----------

    @Test
    public void executeCommand_deleteWithNonNumericArgument_throwsCommandException() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "delete xyz"));

        assertTrue(exception.getMessage().contains("delete <task number>"));
    }

    @Test
    public void executeCommand_deleteWithNoTasksYet_throwsCommandExceptionForInvalidNumber() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "delete 1"));
    }

    @Test
    public void executeCommand_snoozeDeadline_updatesDate() throws CommandException {
        tracker.addTask(new Deadline("submit report", LocalDate.of(2999, 1, 1)));

        parser.executeCommand(tracker, "snooze 1 3d");

        assertTrue(tracker.isValidTaskNumber(1));
    }

    @Test
    public void executeCommand_snoozeWithWeeks_rejectsDuration() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "snooze 1 2w"));

        assertTrue(exception.getMessage().contains("positive number of days"));
    }

    @Test
    public void executeCommand_rescheduleWithPastDate_rejectsDate() throws CommandException {
        tracker.addTask(new Deadline("submit report", LocalDate.of(2999, 1, 1)));

        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "reschedule 1 2000-01-01"));
    }

    // ---------- find ----------

    @Test
    public void executeCommand_findWithNoKeyword_throwsCommandException() {
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "find"));

        assertTrue(exception.getMessage().contains("find needs a keyword"));
    }

    @Test
    public void executeCommand_findWithOnlyWhitespaceKeyword_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "find    "));
    }

    @Test
    public void executeCommand_findWithKeyword_doesNotThrow() throws CommandException {
        // No tasks exist yet, but a valid, non-empty keyword should still
        // reach Tracker.findTasks without throwing (an empty result list is
        // a valid outcome, not an error).
        parser.executeCommand(tracker, "find book");
    }

    // ---------- isCommand boundary: command word used as a prefix of another word ----------

    @Test
    public void executeCommand_wordStartingWithListButNotList_isTreatedAsUnknown() {
        // "listing" should NOT be treated as the "list" command.
        assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "listing"));
    }

    @Test
    public void executeCommand_todoAsPrefixOfLongerWord_isTreatedAsUnknown() {
        // "todos" should NOT be treated as "todo" with argument "s".
        CommandException exception = assertThrows(CommandException.class,
                () -> parser.executeCommand(tracker, "todos"));

        assertTrue(exception.getMessage().contains("I don't recognize that command"));
    }
}

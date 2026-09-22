package tony;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command processing through the entry point shared by the console and GUI.
 */
public class TonyTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-18T00:00:00Z"), ZoneOffset.UTC);

    @TempDir
    private Path temporaryDirectory;

    /** Verifies that a null command is reported without crashing an API caller. */
    @Test
    public void getResponse_nullCommand_returnsError() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("My apologies, Chief. Please enter a command.", tony.getResponse(null));
    }

    /** Verifies that core task commands update and display the same task list. */
    @Test
    public void getResponse_taskWorkflow_returnsCurrentTaskState() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Certainly, Chief. I've added this item to the agenda:\n"
                + "  [T][ ] read book\n"
                + "The agenda now contains 1 task.", tony.getResponse("todo read book"));
        assertEquals("Consider it scheduled, Chief. I'll keep watch over this deadline:\n"
                + "  [D][ ] submit report (by: Sep 20 2099)\n"
                + "The agenda now contains 2 tasks.",
                tony.getResponse("deadline submit report /by 2099-09-20"));
        assertEquals("Excellent, Chief. I've recorded this matter as complete:\n  [T][X] read book",
                tony.getResponse("mark 1"));
        assertEquals("Here is the current agenda, Chief:\n"
                + "1.[T][X] read book\n"
                + "2.[D][ ] submit report (by: Sep 20 2099)", tony.getResponse("list"));
        assertEquals("I found these matching matters, Chief:\n"
                + "1.[D][ ] submit report (by: Sep 20 2099)", tony.getResponse("find REPORT"));
        assertEquals("As requested, Chief. I've removed this matter:\n"
                + "  [T][X] read book\n"
                + "The agenda now contains 1 task.", tony.getResponse("delete 1"));
    }

    /** Verifies that one command deletes several original-list positions and reports them in list order. */
    @Test
    public void getResponse_deleteMultipleTasks_deletesOriginalPositionsInListOrder() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        tony.getResponse("todo read book");
        tony.getResponse("deadline submit report /by 2099-09-20");
        tony.getResponse("event orientation /from 2099-09-21 /to 2099-09-22");
        tony.getResponse("todo buy groceries");

        assertEquals("As requested, Chief. I've removed these matters:\n"
                + "  [D][ ] submit report (by: Sep 20 2099)\n"
                + "  [T][ ] buy groceries\n"
                + "The agenda now contains 2 tasks.", tony.getResponse("delete 4 2"));
        assertEquals("Here is the current agenda, Chief:\n"
                + "1.[T][ ] read book\n"
                + "2.[E][ ] orientation (from: Sep 21 2099 to: Sep 22 2099)",
                tony.getResponse("list"));
    }

    /** Verifies that any invalid selection prevents every deletion in a multi-delete command. */
    @Test
    public void getResponse_invalidMultiDelete_returnsErrorWithoutDeletingTasks() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        tony.getResponse("todo first task");
        tony.getResponse("todo second task");
        tony.getResponse("todo third task");

        assertEquals("My apologies, Chief. Please give me a whole-number task number to delete.",
                tony.getResponse("delete 1 two"));
        assertEquals("My apologies, Chief. That task number is not on the agenda.",
                tony.getResponse("delete 1 4"));
        assertEquals("My apologies, Chief. Please give me each task number only once.",
                tony.getResponse("delete 1 1"));
        assertEquals("Here is the current agenda, Chief:\n"
                + "1.[T][ ] first task\n"
                + "2.[T][ ] second task\n"
                + "3.[T][ ] third task", tony.getResponse("list"));
    }

    /** Verifies that the result of a multi-delete is available in a later session. */
    @Test
    public void constructor_tasksDeletedTogether_restoresRemainingTasks() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Tony firstSession = new Tony(dataFile);
        firstSession.getResponse("todo first task");
        firstSession.getResponse("todo second task");
        firstSession.getResponse("todo third task");
        firstSession.getResponse("delete 3 1");

        Tony secondSession = new Tony(dataFile);

        assertEquals("Here is the current agenda, Chief:\n1.[T][ ] second task",
                secondSession.getResponse("list"));
    }

    /** Verifies that invalid commands explain the problem without changing the task list. */
    @Test
    public void getResponse_invalidCommands_returnsErrorsWithoutChangingTasks() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("My apologies, Chief. I need a description for the to-do. "
                        + "For example: todo read chapter 3",
                tony.getResponse("todo"));
        assertEquals("My apologies, Chief. Please give me dates as yyyy-MM-dd, for example 2019-10-15.",
                tony.getResponse("deadline submit report /by tomorrow"));
        assertEquals("My apologies, Chief. I don't recognize that instruction. "
                + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.",
                tony.getResponse("hello"));
        assertEquals("Your agenda is clear, Chief. There are no matters on file.",
                tony.getResponse("list"));
    }

    /** Verifies that harmless surrounding and repeated whitespace does not invalidate commands. */
    @Test
    public void getResponse_irregularWhitespace_executesNormalizedCommand() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Certainly, Chief. I've added this item to the agenda:\n"
                + "  [T][ ] read book\n"
                + "The agenda now contains 1 task.", tony.getResponse("  todo   read   book  "));
        assertEquals("Consider it scheduled, Chief. I'll keep watch over this deadline:\n"
                + "  [D][ ] submit report (by: Sep 20 2099)\n"
                + "The agenda now contains 2 tasks.",
                tony.getResponse("deadline submit report   /by   2099-09-20"));
        assertEquals("The office is in order, Chief. Enjoy your evening.", tony.getResponse("  bye  "));
    }

    /** Verifies that malformed parameters and impossible dates are rejected without changing data. */
    @Test
    public void getResponse_invalidParametersAndDates_returnsSpecificErrors() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("My apologies, Chief. Please specify /by only once.",
                tony.getResponse("deadline report /by 2099-09-20 /by 2099-09-21"));
        assertEquals("My apologies, Chief. Please give me dates as yyyy-MM-dd, for example 2019-10-15.",
                tony.getResponse("deadline report /by 2099-02-30"));
        assertEquals("My apologies, Chief. I need the event's end date to be after its start date.",
                tony.getResponse("event meeting /from 2099-09-20 /to 2099-09-20"));
        assertEquals("My apologies, Chief. I need the event's end date to be after its start date.",
                tony.getResponse("event meeting /from 2099-09-21 /to 2099-09-20"));
        assertEquals("Your agenda is clear, Chief. There are no matters on file.", tony.getResponse("list"));
    }

    /** Verifies that deadlines before today are rejected while today's date remains valid. */
    @Test
    public void getResponse_deadlineBeforeToday_rejectsOnlyPastDate() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"), FIXED_CLOCK);

        assertEquals("My apologies, Chief. I cannot schedule a deadline before today.",
                tony.getResponse("deadline submit report /by 2026-09-17"));
        assertEquals("Consider it scheduled, Chief. I'll keep watch over this deadline:\n"
                + "  [D][ ] submit report (by: Sep 18 2026)\n"
                + "The agenda now contains 1 task.",
                tony.getResponse("deadline submit report /by 2026-09-18"));
    }

    /** Verifies that an event is rejected when either boundary date is before today. */
    @Test
    public void getResponse_eventDateBeforeToday_returnsErrorWithoutAddingEvent() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"), FIXED_CLOCK);
        String expected = "My apologies, Chief. I cannot schedule an event before today.";

        assertEquals(expected,
                tony.getResponse("event conference /from 2026-09-17 /to 2026-09-19"));
        assertEquals(expected,
                tony.getResponse("event conference /from 2026-09-18 /to 2026-09-17"));
        assertEquals(0, tony.getTaskCount());
    }

    /** Verifies that the same task cannot be added twice, including with different letter case. */
    @Test
    public void getResponse_duplicateTask_returnsErrorWithoutAddingDuplicate() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        tony.getResponse("todo Read Book");

        assertEquals("My apologies, Chief. That matter is already on the agenda.",
                tony.getResponse("todo read book"));
        assertEquals(1, tony.getTaskCount());
    }

    /** Verifies that blank, extraneous, and invalid-number commands are handled safely. */
    @Test
    public void getResponse_blankAndExtraneousInput_returnsErrors() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("My apologies, Chief. Please enter a command.", tony.getResponse("   "));
        assertEquals("My apologies, Chief. I don't recognize that instruction. "
                + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.",
                tony.getResponse("list now"));
        assertEquals("My apologies, Chief. Please give me a whole-number task number to mark.",
                tony.getResponse("mark 1!"));
    }

    /** Verifies that command-size and control-character limits reject unsafe input before parsing. */
    @Test
    public void getCommandResult_unsafeInput_returnsTypedErrors() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        Tony.CommandResult longCommandResult = tony.getCommandResult("a".repeat(1_001));
        Tony.CommandResult controlCharacterResult = tony.getCommandResult("todo read\u0000book");

        assertEquals(Tony.ResponseType.ERROR, longCommandResult.type());
        assertEquals("My apologies, Chief. That instruction is too long. "
                + "Please keep it under 1,000 characters.", longCommandResult.message());
        assertEquals(Tony.ResponseType.ERROR, controlCharacterResult.type());
        assertEquals("My apologies, Chief. That instruction contains unsupported control characters.",
                controlCharacterResult.message());
        assertEquals(0, tony.getTaskCount());
    }

    /** Verifies that whitespace control characters are normalized as ordinary command separators. */
    @Test
    public void getResponse_whitespaceControlCharacters_executesNormalizedCommand() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Certainly, Chief. I've added this item to the agenda:\n"
                + "  [T][ ] read book\n"
                + "The agenda now contains 1 task.", tony.getResponse("todo\tread\nbook"));
    }

    /** Verifies successful event creation and unmarking, including their persisted state. */
    @Test
    public void getResponse_eventAndUnmarkWorkflow_persistsUpdatedTask() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Tony tony = new Tony(dataFile);

        assertEquals("Your calendar is updated, Chief. I've arranged this event:\n"
                + "  [E][ ] orientation (from: Sep 21 2099 to: Sep 22 2099)\n"
                + "The agenda now contains 1 task.",
                tony.getResponse("event orientation /from 2099-09-21 /to 2099-09-22"));
        tony.getResponse("mark 1");
        assertEquals("Understood, Chief. I've returned this matter to the active agenda:\n"
                + "  [E][ ] orientation (from: Sep 21 2099 to: Sep 22 2099)",
                tony.getResponse("unmark 1"));

        Tony nextSession = new Tony(dataFile);
        assertEquals(0, nextSession.getCompletedTaskCount());
        assertEquals("Here is the current agenda, Chief:\n"
                + "1.[E][ ] orientation (from: Sep 21 2099 to: Sep 22 2099)",
                nextSession.getResponse("list"));
    }

    /** Verifies state-dependent errors for repeated mark and unmark operations. */
    @Test
    public void getResponse_repeatedCompletionChange_returnsSpecificError() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        tony.getResponse("todo read book");

        assertEquals("My apologies, Chief. That matter is already on the active agenda.",
                tony.getResponse("unmark 1"));
        tony.getResponse("mark 1");
        assertEquals("My apologies, Chief. That matter is already marked as complete.",
                tony.getResponse("mark 1"));
    }

    /** Verifies empty and unsuccessful searches without changing the agenda. */
    @Test
    public void getResponse_missingOrUnmatchedFindKeyword_returnsSpecificResponse() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        tony.getResponse("todo read book");

        assertEquals("My apologies, Chief. Please give me a keyword to search for.",
                tony.getResponse("find"));
        assertEquals("I found no matching matters, Chief.", tony.getResponse("find report"));
        assertEquals(1, tony.getTaskCount());
    }

    /** Verifies malformed deadline and event layouts report format-specific errors. */
    @Test
    public void getResponse_incompleteDatedTaskCommands_returnsFormatErrors() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        String deadlineError = "My apologies, Chief. I need a description and due date for the deadline. "
                + "Use: deadline <task> /by <yyyy-MM-dd>";
        String eventError = "My apologies, Chief. I need a description, start date, and end date for the event. "
                + "Use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>";

        assertEquals(deadlineError, tony.getResponse("deadline"));
        assertEquals(deadlineError, tony.getResponse("deadline report /by"));
        assertEquals(deadlineError, tony.getResponse("deadline /by 2099-09-20"));
        assertEquals(eventError, tony.getResponse("event"));
        assertEquals(eventError, tony.getResponse("event meeting /to 2099-09-22"));
        assertEquals(eventError, tony.getResponse("event meeting /from 2099-09-21"));
        assertEquals(eventError, tony.getResponse("event meeting /from /to 2099-09-22"));
    }

    /** Verifies duplicate event parameters and invalid event dates are rejected. */
    @Test
    public void getResponse_invalidEventParameters_returnsSpecificErrors() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("My apologies, Chief. Please specify /from and /to only once each.",
                tony.getResponse("event meeting /from 2099-09-20 /from 2099-09-21 /to 2099-09-22"));
        assertEquals("My apologies, Chief. Please specify /from and /to only once each.",
                tony.getResponse("event meeting /from 2099-09-20 /to 2099-09-21 /to 2099-09-22"));
        assertEquals("My apologies, Chief. Please give me dates as yyyy-MM-dd, for example 2019-10-15.",
                tony.getResponse("event meeting /from tomorrow /to 2099-09-22"));
        assertEquals("My apologies, Chief. Please give me dates as yyyy-MM-dd, for example 2019-10-15.",
                tony.getResponse("event meeting /from 2099-09-20 /to tomorrow"));
    }

    /** Verifies descriptions over the supported limit are rejected for every task command. */
    @Test
    public void getResponse_overlongTaskDescriptions_returnsErrorWithoutAddingTasks() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        String description = "a".repeat(501);
        String expected = "My apologies, Chief. Please keep task descriptions to 500 characters or fewer.";

        assertEquals(expected, tony.getResponse("todo " + description));
        assertEquals(expected, tony.getResponse("deadline " + description + " /by 2099-09-20"));
        assertEquals(expected, tony.getResponse(
                "event " + description + " /from 2099-09-20 /to 2099-09-21"));
        assertEquals(0, tony.getTaskCount());
    }

    /** Verifies every invalid task-number shape has a clear response and no side effects. */
    @Test
    public void getResponse_invalidTaskNumbers_returnsSpecificErrors() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        tony.getResponse("todo only task");

        assertEquals("My apologies, Chief. Please give me a task number to mark.",
                tony.getResponse("mark"));
        assertEquals("My apologies, Chief. That task number is not on the agenda.",
                tony.getResponse("mark 0"));
        assertEquals("My apologies, Chief. That task number is not on the agenda.",
                tony.getResponse("unmark 2"));
        assertEquals("My apologies, Chief. Please give me a whole-number task number to mark.",
                tony.getResponse("mark 999999999999999999999"));
        assertEquals("My apologies, Chief. Please give me a task number to delete.",
                tony.getResponse("delete"));
        assertEquals("My apologies, Chief. That task number is not on the agenda.",
                tony.getResponse("delete -1"));
        assertEquals(1, tony.getTaskCount());
    }

    /** Verifies exit-command recognition handles null, normalized, and unrelated input. */
    @Test
    public void isExitCommand_variedInput_recognizesOnlyNormalizedBye() {
        assertFalse(Tony.isExitCommand(null));
        assertFalse(Tony.isExitCommand("goodbye"));
        assertTrue(Tony.isExitCommand("  bye\t"));
    }

    /** Verifies that graphical interfaces receive error meaning separately from error wording. */
    @Test
    public void getCommandResult_invalidCommand_returnsTypedSecretaryError() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        Tony.CommandResult result = tony.getCommandResult("todo");

        assertEquals(Tony.ResponseType.ERROR, result.type());
        assertEquals("My apologies, Chief. I need a description for the to-do. "
                + "For example: todo read chapter 3", result.message());
    }

    /** Verifies that graphical interfaces receive normal response meaning for successful commands. */
    @Test
    public void getCommandResult_validCommand_returnsNormalResponse() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        Tony.CommandResult result = tony.getCommandResult("todo read book");

        assertEquals(Tony.ResponseType.NORMAL, result.type());
        assertEquals("Certainly, Chief. I've added this item to the agenda:\n"
                + "  [T][ ] read book\n"
                + "The agenda now contains 1 task.", result.message());
    }

    /** Verifies that task-summary counts follow task additions, completion, and deletion. */
    @Test
    public void taskCounts_taskWorkflow_returnsCurrentTotals() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals(0, tony.getTaskCount());
        assertEquals(0, tony.getCompletedTaskCount());

        tony.getResponse("todo first task");
        tony.getResponse("todo second task");
        tony.getResponse("mark 2");

        assertEquals(2, tony.getTaskCount());
        assertEquals(1, tony.getCompletedTaskCount());

        tony.getResponse("delete 2");
        assertEquals(1, tony.getTaskCount());
        assertEquals(0, tony.getCompletedTaskCount());
    }

    /** Verifies that the compact overview note responds to each meaningful workload state. */
    @Test
    public void getOverviewMessage_differentWorkloads_returnsSecretaryAdvice() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        assertEquals("Your desk is clear, Chief. I am ready when you are.", tony.getOverviewMessage());

        tony.getResponse("todo first task");
        assertEquals("One matter awaits your attention, Chief. I will keep it on our radar.",
                tony.getOverviewMessage());

        for (int taskNumber = 2; taskNumber <= 5; taskNumber++) {
            tony.getResponse("todo task " + taskNumber);
        }
        assertEquals("The agenda is rather full, Chief. Please remember to take a proper rest.",
                tony.getOverviewMessage());

        for (int taskNumber = 1; taskNumber <= 5; taskNumber++) {
            tony.getResponse("mark " + taskNumber);
        }
        assertEquals("Everything is in order, Chief. Shall we call it a day and have a drink?",
                tony.getOverviewMessage());
    }

    /** Verifies the overview's plural wording for a small nontrivial workload. */
    @Test
    public void getOverviewMessage_twoIncompleteTasks_returnsPluralAdvice() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        tony.getResponse("todo first task");
        tony.getResponse("todo second task");

        assertEquals("2 matters await your attention, Chief. I will keep them in order.",
                tony.getOverviewMessage());
    }

    /** Verifies that a new chatbot instance loads tasks saved by an earlier instance. */
    @Test
    public void constructor_savedTasksExist_restoresTasks() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Tony firstSession = new Tony(dataFile);
        firstSession.getResponse("todo keep this task");

        Tony secondSession = new Tony(dataFile);

        assertEquals("", secondSession.getStartupMessage());
        assertEquals("Here is the current agenda, Chief:\n1.[T][ ] keep this task",
                secondSession.getResponse("list"));
    }

    /** Verifies that malformed saved records produce a warning while valid records remain usable. */
    @Test
    public void constructor_malformedSavedRecord_warnsAndKeepsValidTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T | 0 | valid task", "T | 2 | invalid status"));

        Tony tony = new Tony(dataFile);

        assertEquals("Chief, I set aside 1 line from our records because the data was invalid.",
                tony.getStartupMessage());
        assertEquals("Here is the current agenda, Chief:\n1.[T][ ] valid task", tony.getResponse("list"));
    }

    /** Verifies that startup warnings use plural wording for multiple invalid records. */
    @Test
    public void constructor_multipleMalformedSavedRecords_usesPluralWarning() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T | 2 | invalid status", "X | 0 | invalid type"));

        Tony tony = new Tony(dataFile);

        assertEquals("Chief, I set aside 2 lines from our records because the data was invalid.",
                tony.getStartupMessage());
    }

    /** Verifies that an unreadable data path starts safely with an empty in-memory agenda. */
    @Test
    public void constructor_dataFileIsDirectory_warnsAndStartsWithEmptyAgenda() {
        Tony tony = new Tony(temporaryDirectory);

        assertEquals("Chief, I couldn't read our records, so I have opened a fresh agenda for this session.",
                tony.getStartupMessage());
        assertEquals(0, tony.getTaskCount());
    }

    /** Verifies that a file which failed to load is not overwritten by later session changes. */
    @Test
    public void getResponse_dataFileHasInvalidEncoding_warnsAndPreservesOriginalFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        byte[] invalidUtf8 = {(byte) 0xC3, (byte) 0x28};
        Files.write(dataFile, invalidUtf8);
        Tony tony = new Tony(dataFile);

        String response = tony.getResponse("todo session-only task");

        assertEquals("Chief, I couldn't read our records, so I have opened a fresh agenda for this session.",
                tony.getStartupMessage());
        assertEquals("Certainly, Chief. I've added this item to the agenda:\n"
                + "  [T][ ] session-only task\n"
                + "The agenda now contains 1 task.\n"
                + "Chief, I couldn't file that change. It will remain available only for this session.", response);
        assertArrayEquals(invalidUtf8, Files.readAllBytes(dataFile));
    }

    /** Verifies that a save failure is reported while the new task remains usable in the session. */
    @Test
    public void getCommandResult_dataParentIsFile_returnsWarningAndKeepsSessionTask() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "content");
        Tony tony = new Tony(parentFile.resolve("tasks.txt"));

        Tony.CommandResult result = tony.getCommandResult("todo prepare notes");

        assertEquals(Tony.ResponseType.WARNING, result.type());
        assertEquals("Certainly, Chief. I've added this item to the agenda:\n"
                + "  [T][ ] prepare notes\n"
                + "The agenda now contains 1 task.\n"
                + "Chief, I couldn't file that change. It will remain available only for this session.",
                result.message());
        assertEquals(1, tony.getTaskCount());

        Tony.CommandResult secondResult = tony.getCommandResult("todo another task");
        assertEquals(Tony.ResponseType.WARNING, secondResult.type());
        assertTrue(secondResult.message().endsWith(
                "Chief, I couldn't file that change. It will remain available only for this session."));
        assertEquals(2, tony.getTaskCount());
    }

    /** Verifies that the exit command returns Tony's farewell. */
    @Test
    public void getResponse_bye_returnsFarewell() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("The office is in order, Chief. Enjoy your evening.", tony.getResponse("bye"));
    }
}

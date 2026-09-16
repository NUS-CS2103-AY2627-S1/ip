package miku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import miku.command.CommandContext;
import miku.command.RescheduleCommand;
import miku.parser.DateTimeParser;
import miku.storage.Storage;
import miku.task.Deadline;
import miku.task.Task;
import miku.task.TaskList;
import miku.ui.ResponseFormatter;

/** Tests Miku's response API that connects command processing to the GUI. */
class MikuTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_addAndListCommands_returnsResponsesAndPersistsTask() throws IOException {
        Path saveFile = temporaryDirectory.resolve("miku.json");
        Miku miku = new Miku(new Storage(saveFile));

        String addResponse = miku.getResponse("todo read book");
        String listResponse = miku.getResponse("list");

        assertEquals("Got it! I've added this task for you ✨\n[T][ ] read book\n"
                + "Now you have 1 task(s) in the list! ☆", addResponse);
        assertEquals("Here are the tasks in your list ♫\n1.[T][ ] read book", listResponse);
        assertTrue(Files.readString(saveFile).contains("read book"));
    }

    @Test
    void getResponse_blankInput_returnsErrorWithoutChangingTasks() {
        Miku miku = new Miku(new Storage(temporaryDirectory.resolve("miku.json")));

        String errorResponse = miku.getResponse("   ");
        String listResponse = miku.getResponse("list");

        assertEquals("OOPS!!! The command cannot be empty. Please tell Miku what to do ♪", errorResponse);
        assertEquals("Here are the tasks in your list ♫", listResponse);
    }

    @Test
    void processCommand_validAndInvalidCommands_identifiesErrors() {
        Miku miku = new Miku(new Storage(temporaryDirectory.resolve("miku.json")));

        MikuResponse success = miku.processCommand("todo read book");
        MikuResponse failure = miku.processCommand("mark 99");

        assertFalse(success.isError());
        assertTrue(success.text().contains("read book"));
        assertTrue(failure.isError());
        assertTrue(failure.text().startsWith("OOPS!!!"));
    }

    @Test
    void getResponse_byeCommand_returnsFarewellAndRequestsExit() {
        Miku miku = new Miku(new Storage(temporaryDirectory.resolve("miku.json")));

        String response = miku.getResponse("bye");

        assertEquals("Bye bye! Miku hopes to see you again soon! ✨", response);
        assertTrue(miku.isExitRequested());
    }

    @Test
    void getResponse_markAndUnmarkCommands_updatesTaskStatus() {
        Miku miku = new Miku(new Storage(temporaryDirectory.resolve("miku.json")));
        miku.getResponse("todo read book");

        String markResponse = miku.getResponse("mark 1");
        String unmarkResponse = miku.getResponse("unmark 1");

        assertEquals("Okay ★ I've marked this task as done!\n[T][★] read book", markResponse);
        assertEquals("Oops... I've marked this task as not done yet ♪\n[T][ ] read book", unmarkResponse);
    }

    @Test
    void getResponse_rescheduleDeadline_preservesCompletionAndPersistsSchedule() throws IOException {
        Path saveFile = temporaryDirectory.resolve("miku.json");
        Miku miku = new Miku(new Storage(saveFile));
        miku.getResponse("deadline submit report /by 2026-09-08");
        miku.getResponse("mark 1");

        String response = miku.getResponse("reschedule 1 /by 2026-09-10 0930");

        assertEquals("Got it! I've rescheduled this task for you \u2728\n"
                + "[D][\u2605] submit report (by: Sep 10 2026 9:30 AM)", response);
        String savedTasks = Files.readString(saveFile);
        assertTrue(savedTasks.contains("\"datetime\": \"2026-09-10T09:30\""));
        assertTrue(savedTasks.contains("\"includesTime\": true"));
        Miku restoredMiku = new Miku(new Storage(saveFile));
        assertEquals("Here are the tasks in your list \u266b\n"
                + "1.[D][\u2605] submit report (by: Sep 10 2026 9:30 AM)", restoredMiku.getResponse("list"));
    }

    @Test
    void getResponse_rescheduleEvent_updatesBothScheduleEndpoints() throws IOException {
        Path saveFile = temporaryDirectory.resolve("miku.json");
        Miku miku = new Miku(new Storage(saveFile));
        miku.getResponse("event rehearsal /from 2026-09-08 /to 2026-09-09");

        String response = miku.getResponse("reschedule 1 /from 2026-09-10 /to 2026-09-11 1930");

        assertEquals("Got it! I've rescheduled this task for you \u2728\n"
                + "[E][ ] rehearsal (from: Sep 10 2026 to: Sep 11 2026 7:30 PM)", response);
        String savedTasks = Files.readString(saveFile);
        assertTrue(savedTasks.contains("\"from\": \"2026-09-10T00:00\""));
        assertTrue(savedTasks.contains("\"to\": \"2026-09-11T19:30\""));
        assertTrue(savedTasks.contains("\"fromIncludesTime\": false"));
        assertTrue(savedTasks.contains("\"toIncludesTime\": true"));
    }

    @Test
    void getResponse_invalidReschedule_leavesTaskListUnchanged() {
        Miku miku = new Miku(new Storage(temporaryDirectory.resolve("miku.json")));
        miku.getResponse("todo read notes");
        miku.getResponse("deadline submit report /by 2026-09-08");
        miku.getResponse("event rehearsal /from 2026-09-09 /to 2026-09-10");
        String expectedList = miku.getResponse("list");

        assertTrue(miku.getResponse("reschedule 1 /by 2026-09-11").contains("Only deadlines and events"));
        assertTrue(miku.getResponse("reschedule 2 /from 2026-09-11 /to 2026-09-12").contains("deadline needs"));
        assertTrue(miku.getResponse("reschedule 3 /by 2026-09-11").contains("event needs"));
        assertTrue(miku.getResponse("reschedule 3 /from 2026-09-11 /to 2026-09-10").contains("cannot be earlier"));
        assertEquals(expectedList, miku.getResponse("list"));
    }

    @Test
    void execute_rescheduleSaveFailure_restoresOriginalTask() throws MikuException {
        DateTimeParser.ParsedDateTime originalDateTime = DateTimeParser.parse("2026-09-08");
        Deadline originalTask = new Deadline("submit report", originalDateTime.dateTime(), false);
        TaskList tasks = new TaskList(List.of(originalTask));
        RescheduleCommand command = new RescheduleCommand(1, DateTimeParser.parse("2026-09-10"));
        CommandContext commandContext = new CommandContext(tasks, new ResponseFormatter(), new FailingStorage());

        assertThrows(MikuException.class, () -> command.execute(commandContext));
        assertSame(originalTask, tasks.get(0));
    }

    @Test
    void getWelcomeMessage_invalidSavedTasks_includesLoadingWarning() throws IOException {
        Path saveFile = temporaryDirectory.resolve("miku.json");
        Files.writeString(saveFile, "not json");

        Miku miku = new Miku(new Storage(saveFile));

        assertTrue(miku.getWelcomeMessage().contains("could not load saved tasks"));
        assertEquals(2, miku.getStartupResponses().size());
        assertFalse(miku.getStartupResponses().get(0).isError());
        assertTrue(miku.getStartupResponses().get(1).isError());
        assertFalse(miku.isExitRequested());
    }

    /** Storage double that always fails writes to verify command rollback behaviour. */
    private static class FailingStorage extends Storage {
        @Override
        public void saveTasks(List<Task> tasks) throws IOException {
            throw new IOException("Simulated storage failure");
        }
    }
}

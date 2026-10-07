package drax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandExecutorTest {
    private TaskList tasks;
    private RecordingStorage storage;
    private CommandExecutor executor;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        storage = new RecordingStorage();
        executor = new CommandExecutor(tasks, storage);
    }

    @Test
    public void executeBye_returnsFarewellAndExitOutcome() {
        CommandExecutor.ExecutionResult result = execute(Parser.Type.BYE);

        assertEquals("Godspeed. Hope to see ya again soon!", result.response());
        assertEquals(CommandExecutor.Outcome.EXIT, result.outcome());
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeList_emptyList_returnsEmptyListMessage() {
        CommandExecutor.ExecutionResult result = execute(Parser.Type.LIST);

        assertContinueResponse("Oops! You currently have no tasks.\n"
                + "Here are the tasks in your list!", result);
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeList_multipleTaskTypes_returnsNumberedTasksInOrder() {
        executeTodo("read book");
        executeDeadline("submit report", "2019-12-02T18:00");
        executeEvent("meeting", "2019-12-03T09:00", "2019-12-03T10:30");

        CommandExecutor.ExecutionResult result = execute(Parser.Type.LIST);

        assertContinueResponse("""
                Here are the tasks in your list!
                1.[T][ ] read book
                2.[D][ ] submit report (by: Dec 02 2019 6:00 PM)
                3.[E][ ] meeting (from: Dec 03 2019 9:00 AM to: Dec 03 2019 10:30 AM)""", result);
    }

    @Test
    public void executeTodo_validDescriptions_addsTasksAndUsesCorrectCounts() {
        CommandExecutor.ExecutionResult firstResult = executeTodo("read book");
        CommandExecutor.ExecutionResult secondResult = executeTodo("write report");

        assertContinueResponse("""
                I've added this task
                [T][ ] read book
                Now you have 1 task!""", firstResult);
        assertContinueResponse("""
                I've added this task
                [T][ ] write report
                Now you have 2 tasks!""", secondResult);
        assertTaskDisplays("[T][ ] read book", "[T][ ] write report");
        assertEquals(getTaskDisplays(tasks), getTaskDisplays(storage.getLastSavedTasks()));
        assertEquals(2, storage.getSaveCount());
    }

    @Test
    public void executeTodo_emptyDescription_returnsValidationMessageWithoutSaving() {
        CommandExecutor.ExecutionResult result = executeTodo("");

        assertContinueResponse("You didn't provide a task!?", result);
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
        assertContinueResponse("There's nothin' to undo!", executeUndo());
    }

    @Test
    public void executeDeadline_validDetails_addsAndSavesDeadline() {
        CommandExecutor.ExecutionResult result = executeDeadline("submit report", "2/12/2019 1800");

        assertContinueResponse("""
                I've added this task
                [D][ ] submit report (by: Dec 02 2019 6:00 PM)
                Now you have 1 task!""", result);
        assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals(getTaskDisplays(tasks), getTaskDisplays(storage.getLastSavedTasks()));
    }

    @Test
    public void executeDeadline_emptyDescription_returnsValidationMessageWithoutSaving() {
        CommandExecutor.ExecutionResult result = executeDeadline("", "2019-12-02");

        assertContinueResponse("You didn't provide a task!?", result);
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeDeadline_emptyDate_returnsValidationMessageWithoutSaving() {
        CommandExecutor.ExecutionResult result = executeDeadline("submit report", "");

        assertContinueResponse("You didn't provide a end date! Use /by [deadline]", result);
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeDeadline_invalidDate_returnsValidationMessageWithoutSaving() {
        CommandExecutor.ExecutionResult result = executeDeadline("submit report", "next Friday");

        assertContinueResponse(invalidDateMessage(), result);
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeEvent_validDetails_addsAndSavesEvent() {
        CommandExecutor.ExecutionResult result = executeEvent(
                "meeting", "2019-12-03T09:00", "2019-12-03T10:30");

        assertContinueResponse("""
                I've added this task
                [E][ ] meeting (from: Dec 03 2019 9:00 AM to: Dec 03 2019 10:30 AM)
                Now you have 1 task!""", result);
        assertInstanceOf(Event.class, tasks.get(0));
        assertEquals(getTaskDisplays(tasks), getTaskDisplays(storage.getLastSavedTasks()));
    }

    @Test
    public void executeEvent_emptyDescription_returnsValidationMessageWithoutSaving() {
        CommandExecutor.ExecutionResult result = executeEvent(
                "", "2019-12-03T09:00", "2019-12-03T10:30");

        assertContinueResponse("You didn't provide a task!?", result);
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeEvent_missingStartOrEnd_returnsValidationMessageWithoutSaving() {
        CommandExecutor.ExecutionResult missingStart =
                executeEvent("meeting", "", "2019-12-03T10:30");
        CommandExecutor.ExecutionResult missingEnd =
                executeEvent("meeting", "2019-12-03T09:00", "");

        String expected = "You didn't provide when this event is happening! Use /from [date] /to [date]";
        assertContinueResponse(expected, missingStart);
        assertContinueResponse(expected, missingEnd);
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeEvent_invalidStartOrEnd_returnsValidationMessageWithoutSaving() {
        CommandExecutor.ExecutionResult invalidStart = executeEvent(
                "meeting", "tomorrow", "2019-12-03T10:30");
        CommandExecutor.ExecutionResult invalidEnd = executeEvent(
                "meeting", "2019-12-03T09:00", "tomorrow");

        assertContinueResponse(invalidDateMessage(), invalidStart);
        assertContinueResponse(invalidDateMessage(), invalidEnd);
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeMark_validIndex_marksAndSavesTask() {
        executeTodo("read book");
        int savesBeforeMark = storage.getSaveCount();

        CommandExecutor.ExecutionResult result = executeMark("1");

        assertContinueResponse("I've marked this task as done:\n[T][X] read book", result);
        assertTrue(tasks.get(0).isDone());
        assertTrue(storage.getLastSavedTasks().get(0).isDone());
        assertEquals(savesBeforeMark + 1, storage.getSaveCount());
    }

    @Test
    public void executeUnmark_validIndex_unmarksAndSavesTask() {
        executeTodo("read book");
        executeMark("1");
        int savesBeforeUnmark = storage.getSaveCount();

        CommandExecutor.ExecutionResult result = executeUnmark("1");

        assertContinueResponse("I've marked this task as not done:\n[T][ ] read book", result);
        assertFalse(tasks.get(0).isDone());
        assertFalse(storage.getLastSavedTasks().get(0).isDone());
        assertEquals(savesBeforeUnmark + 1, storage.getSaveCount());
    }

    @Test
    public void executeMark_invalidTaskNumbers_returnValidationMessagesWithoutSaving() {
        executeTodo("read book");
        int savesBeforeInvalidCommands = storage.getSaveCount();

        assertContinueResponse("Please enter a valid number!", executeMark("abc"));
        assertContinueResponse(invalidTaskNumberMessage(), executeMark("0"));
        assertContinueResponse(invalidTaskNumberMessage(), executeMark("-1"));
        assertContinueResponse(invalidTaskNumberMessage(), executeMark("2"));
        assertFalse(tasks.get(0).isDone());
        assertEquals(savesBeforeInvalidCommands, storage.getSaveCount());
    }

    @Test
    public void executeUnmark_invalidTaskNumbers_returnValidationMessagesWithoutSaving() {
        executeTodo("read book");
        executeMark("1");
        int savesBeforeInvalidCommands = storage.getSaveCount();

        assertContinueResponse("Please enter a valid number!", executeUnmark("abc"));
        assertContinueResponse(invalidTaskNumberMessage(), executeUnmark("0"));
        assertContinueResponse(invalidTaskNumberMessage(), executeUnmark("2"));
        assertTrue(tasks.get(0).isDone());
        assertEquals(savesBeforeInvalidCommands, storage.getSaveCount());
    }

    @Test
    public void executeDelete_validIndex_removesTaskAndSavesRemainingTasks() {
        executeTodo("first");
        executeTodo("second");
        int savesBeforeDelete = storage.getSaveCount();

        CommandExecutor.ExecutionResult result = executeDelete("1");

        assertContinueResponse("""
                I've deleted this task
                [T][ ] first
                Now you have 1 task!""", result);
        assertTaskDisplays("[T][ ] second");
        assertEquals(getTaskDisplays(tasks), getTaskDisplays(storage.getLastSavedTasks()));
        assertEquals(savesBeforeDelete + 1, storage.getSaveCount());
    }

    @Test
    public void executeDelete_onlyTask_reportsZeroTasks() {
        executeTodo("only task");

        CommandExecutor.ExecutionResult result = executeDelete("1");

        assertContinueResponse("""
                I've deleted this task
                [T][ ] only task
                Now you have 0 tasks!""", result);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void executeDelete_invalidTaskNumbers_returnValidationMessagesWithoutSaving() {
        executeTodo("read book");
        int savesBeforeInvalidCommands = storage.getSaveCount();

        assertContinueResponse("Please enter a valid number!", executeDelete("abc"));
        assertContinueResponse(invalidTaskNumberMessage(), executeDelete("0"));
        assertContinueResponse(invalidTaskNumberMessage(), executeDelete("2"));
        assertTaskDisplays("[T][ ] read book");
        assertEquals(savesBeforeInvalidCommands, storage.getSaveCount());
    }

    @Test
    public void executeFind_caseInsensitiveKeyword_returnsOnlyMatchingTasksRenumbered() {
        executeTodo("Read Book");
        executeTodo("write report");
        executeTodo("book flight");
        int savesBeforeFind = storage.getSaveCount();

        CommandExecutor.ExecutionResult result = executeFind("BOOK");

        assertContinueResponse("""
                Here are the matching tasks in your list:
                1.[T][ ] Read Book
                2.[T][ ] book flight""", result);
        assertEquals(savesBeforeFind, storage.getSaveCount());
        assertEquals(3, tasks.getSize());
    }

    @Test
    public void executeFind_noMatches_returnsValidationMessage() {
        executeTodo("read book");
        int savesBeforeFind = storage.getSaveCount();

        CommandExecutor.ExecutionResult result = executeFind("exercise");

        assertContinueResponse("Oops! No matching tasks found!", result);
        assertEquals(savesBeforeFind, storage.getSaveCount());
    }

    @Test
    public void executeFind_emptyKeyword_returnsValidationMessage() {
        CommandExecutor.ExecutionResult result = executeFind("");

        assertContinueResponse("You didn't provide a keyword!?", result);
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeUnknown_returnsUnsupportedCommandMessage() {
        CommandExecutor.ExecutionResult result = execute(Parser.Type.UNKNOWN);

        assertContinueResponse("Sorry! But that's not a function I can perform. :(", result);
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeMutation_whenSaveFails_keepsInMemoryChangeAndReportsWarning() {
        storage.setShouldFail(true);

        CommandExecutor.ExecutionResult result = executeTodo("read book");

        assertContinueResponse("""
                Sorry! I couldn't save your tasks :(. \
                They are available till you exit the program!
                I've added this task
                [T][ ] read book
                Now you have 1 task!""", result);
        assertTaskDisplays("[T][ ] read book");
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    public void executeUndo_withoutEarlierState_returnsBoundaryMessage() {
        CommandExecutor.ExecutionResult result = executeUndo();

        assertEquals("There's nothin' to undo!", result.response());
        assertEquals(CommandExecutor.Outcome.CONTINUE, result.outcome());
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeRedo_withoutUndoneState_returnsBoundaryMessage() {
        CommandExecutor.ExecutionResult result = executeRedo();

        assertEquals("There's nothin' to redo!", result.response());
        assertEquals(CommandExecutor.Outcome.CONTINUE, result.outcome());
        assertTrue(tasks.isEmpty());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void executeUndoAndRedo_fiveTodos_restoresEachListState() {
        for (int taskNumber = 1; taskNumber <= 5; taskNumber++) {
            executeTodo("task " + taskNumber);
        }

        for (int expectedSize = 4; expectedSize >= 0; expectedSize--) {
            executeUndo();

            assertEquals(expectedSize, tasks.getSize(),
                    "Undo should restore a list with " + expectedSize + " tasks");
            if (expectedSize > 0) {
                assertEquals("task " + expectedSize, tasks.get(expectedSize - 1).getTask(),
                        "Undo should restore the correct previous task");
            }
        }

        for (int expectedSize = 1; expectedSize <= 5; expectedSize++) {
            executeRedo();

            assertEquals(expectedSize, tasks.getSize(),
                    "Redo should restore a list with " + expectedSize + " tasks");
            assertEquals("task " + expectedSize, tasks.get(expectedSize - 1).getTask(),
                    "Redo should restore the correct next task");
        }
    }

    @Test
    public void executeUndoAndRedo_deadline_restoresDeadlineDetails() {
        executeDeadline("B", "2019-12-02T18:00");

        executeUndo();
        assertTrue(tasks.isEmpty(), "Undo should remove the deadline");

        executeRedo();
        assertInstanceOf(Deadline.class, tasks.get(0));
        assertTaskDisplays("[D][ ] B (by: Dec 02 2019 6:00 PM)");
    }

    @Test
    public void executeUndoAndRedo_event_restoresEventDetails() {
        executeEvent("C", "2019-12-03T09:00", "2019-12-03T10:30");

        executeUndo();
        assertTrue(tasks.isEmpty(), "Undo should remove the event");

        executeRedo();
        assertInstanceOf(Event.class, tasks.get(0));
        assertTaskDisplays("[E][ ] C (from: Dec 03 2019 9:00 AM to: Dec 03 2019 10:30 AM)");
    }

    @Test
    public void executeUndoAndRedo_mark_restoresCompletionState() {
        executeTodo("A");
        executeMark("1");

        executeUndo();
        assertFalse(tasks.get(0).isDone(), "Undoing mark should restore the incomplete state");

        executeRedo();
        assertTrue(tasks.get(0).isDone(), "Redoing mark should restore the completed state");
    }

    @Test
    public void executeUndoAndRedo_unmark_restoresCompletionState() {
        executeTodo("A");
        executeMark("1");
        executeUnmark("1");

        executeUndo();
        assertTrue(tasks.get(0).isDone(), "Undoing unmark should restore the completed state");

        executeRedo();
        assertFalse(tasks.get(0).isDone(), "Redoing unmark should restore the incomplete state");
    }

    @Test
    public void executeUndoAndRedo_delete_restoresDeletedTaskAndOrder() {
        executeTodo("A");
        executeTodo("B");
        executeTodo("C");
        executeDelete("2");

        assertTaskDisplays("[T][ ] A", "[T][ ] C");

        executeUndo();
        assertTaskDisplays("[T][ ] A", "[T][ ] B", "[T][ ] C");

        executeRedo();
        assertTaskDisplays("[T][ ] A", "[T][ ] C");
    }

    @Test
    public void executeUndoAndRedo_successfulTransition_savesRestoredState() {
        executeTodo("A");
        int savesBeforeUndo = storage.getSaveCount();

        executeUndo();
        assertEquals(savesBeforeUndo + 1, storage.getSaveCount());
        assertTrue(storage.getLastSavedTasks().isEmpty());

        executeRedo();
        assertEquals(savesBeforeUndo + 2, storage.getSaveCount());
        assertEquals(getTaskDisplays(tasks), getTaskDisplays(storage.getLastSavedTasks()));
    }

    @Test
    public void executeNewCommand_afterUndo_discardsRedoHistory() {
        executeTodo("original");
        executeUndo();

        executeTodo("replacement");
        CommandExecutor.ExecutionResult redoResult = executeRedo();

        assertEquals("There's nothin' to redo!", redoResult.response(),
                "Executing a new command should clear the redo stack");
        assertTaskDisplays("[T][ ] replacement");
    }

    @Test
    public void executeInvalidMutation_afterSuccessfulMutation_doesNotAddHistoryState() {
        executeTodo("A");
        CommandExecutor.ExecutionResult invalidResult = executeMark("2");

        CommandExecutor.ExecutionResult undoResult = executeUndo();

        assertEquals("This task doesn't exist. You don't have that many tasks!", invalidResult.response(),
                "Executing mark 2 shouldn't be allowed as there is only one task in the list");
        assertEquals("""
                        Your last command was undone!
                        Oops! You currently have no tasks.
                        Here are the tasks in your list!""", undoResult.response(),
                "Executing undo should remove todo A");
        assertTrue(tasks.isEmpty(), "TaskList should be empty by the end of execution");
    }

    @Test
    public void executeUndoAndRedo_whenSaveFails_restoresStateAndReportsFailure() {
        storage.setShouldFail(true);
        executeTodo("A");

        CommandExecutor.ExecutionResult undoResult = executeUndo();
        CommandExecutor.ExecutionResult redoResult = executeRedo();

        String warning = "Sorry! I couldn't save your tasks :(. They are available till you exit the program!";
        assertTrue(undoResult.response().startsWith(warning + "\nYour last command was undone!"));
        assertTrue(redoResult.response().startsWith(warning + "\nYour last command was redone!"));
        assertTaskDisplays("[T][ ] A");
    }

    private CommandExecutor.ExecutionResult executeTodo(String description) {
        return execute(new Parser.Command(Parser.Type.TODO, "", description, "", ""));
    }

    private CommandExecutor.ExecutionResult executeDeadline(String description, String deadline) {
        return execute(new Parser.Command(Parser.Type.DEADLINE, "", description, "", deadline));
    }

    private CommandExecutor.ExecutionResult executeEvent(String description, String start, String end) {
        return execute(new Parser.Command(Parser.Type.EVENT, "", description, start, end));
    }

    private CommandExecutor.ExecutionResult executeMark(String taskNumber) {
        return execute(new Parser.Command(Parser.Type.MARK, taskNumber, "", "", ""));
    }

    private CommandExecutor.ExecutionResult executeUnmark(String taskNumber) {
        return execute(new Parser.Command(Parser.Type.UNMARK, taskNumber, "", "", ""));
    }

    private CommandExecutor.ExecutionResult executeDelete(String taskNumber) {
        return execute(new Parser.Command(Parser.Type.DELETE, taskNumber, "", "", ""));
    }

    private CommandExecutor.ExecutionResult executeFind(String keyword) {
        return execute(new Parser.Command(Parser.Type.FIND, keyword, "", "", ""));
    }

    private CommandExecutor.ExecutionResult executeUndo() {
        return execute(new Parser.Command(Parser.Type.UNDO, "", "", "", ""));
    }

    private CommandExecutor.ExecutionResult executeRedo() {
        return execute(new Parser.Command(Parser.Type.REDO, "", "", "", ""));
    }

    private CommandExecutor.ExecutionResult execute(Parser.Type type) {
        return execute(new Parser.Command(type, "", "", "", ""));
    }

    private CommandExecutor.ExecutionResult execute(Parser.Command command) {
        return executor.execute(command);
    }

    private void assertTaskDisplays(String... expectedDisplays) {
        assertEquals(List.of(expectedDisplays), getTaskDisplays(tasks));
    }

    private static void assertContinueResponse(String expectedResponse, CommandExecutor.ExecutionResult result) {
        assertEquals(expectedResponse, result.response());
        assertEquals(CommandExecutor.Outcome.CONTINUE, result.outcome());
    }

    private static String invalidTaskNumberMessage() {
        return "This task doesn't exist. You don't have that many tasks!";
    }

    private static String invalidDateMessage() {
        return "Please use a valid date and time: "
                + "yyyy-MM-dd, yyyy-MM-ddTHH:mm, or d/M/yyyy HHmm!";
    }

    private static List<String> getTaskDisplays(TaskList taskList) {
        return taskList.asList().stream()
                .map(Task::toString)
                .toList();
    }

    /** Records saved state in memory so unit tests never modify the user's save file. */
    private static class RecordingStorage extends Storage {
        private TaskList lastSavedTasks;
        private int saveCount;
        private boolean shouldFail;

        @Override
        public void save(TaskList tasks) throws IOException {
            saveCount++;
            if (shouldFail) {
                throw new IOException("Simulated write failure");
            }
            this.lastSavedTasks = new TaskList(tasks.asList());
        }

        public TaskList getLastSavedTasks() {
            return this.lastSavedTasks;
        }

        public int getSaveCount() {
            return this.saveCount;
        }

        public void setShouldFail(boolean shouldFail) {
            this.shouldFail = shouldFail;
        }
    }
}

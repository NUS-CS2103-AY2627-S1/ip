package monday.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import monday.exception.MondayException;
import monday.task.Deadline;
import monday.task.Event;
import monday.task.TaskList;
import monday.task.Todo;
import monday.ui.Ui;

/**
 * Tests for Parser.
 */
class ParserTest {
    private static final Path DATA_FILE = Path.of("data", "monday.txt");

    private Parser parser;
    private TaskList tasks;
    private Ui ui;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(DATA_FILE);
        parser = new Parser();
        tasks = new TaskList();
        ui = new Ui();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(DATA_FILE);
    }

    @Test
    void executeCommand_todoCommand_addsTodoAndShowsResponse() throws MondayException {
        boolean shouldContinue = parser.executeCommand("todo buy milk", tasks, ui);

        Todo todo = assertInstanceOf(Todo.class, tasks.get(0));
        assertTrue(shouldContinue);
        assertEquals(1, tasks.size());
        assertEquals("buy milk", todo.getDescription());
        assertFalse(todo.isDone());
        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] buy milk\n"
                + "Now you have 1 tasks in the list.", ui.getResponse());
    }

    @Test
    void executeCommand_todoCommandWithNotes_addsTodoWithNotes() throws MondayException {
        parser.executeCommand("todo buy milk /notes Use the voucher", tasks, ui);

        Todo todo = assertInstanceOf(Todo.class, tasks.get(0));
        assertEquals("buy milk", todo.getDescription());
        assertEquals("Use the voucher", todo.getNotes());
        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] buy milk Use the voucher\n"
                + "Now you have 1 tasks in the list.", ui.getResponse());
    }

    @Test
    void executeCommand_deadlineCommandWithTime_addsDeadline() throws MondayException {
        boolean shouldContinue = parser.executeCommand(
                "deadline submit report /by 30/08/2026 1430", tasks, ui);

        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertTrue(shouldContinue);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 30), deadline.getDeadlineDate());
        assertEquals(LocalTime.of(14, 30), deadline.getDeadlineTime());
    }

    @Test
    void executeCommand_deadlineCommandWithoutTime_addsDeadlineWithoutTime() throws MondayException {
        parser.executeCommand("deadline renew passport /by 01/09/2026", tasks, ui);

        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals(LocalDate.of(2026, 9, 1), deadline.getDeadlineDate());
        assertNull(deadline.getDeadlineTime());
    }

    @Test
    void executeCommand_deadlineCommandWithNotes_addsDeadlineWithNotes() throws MondayException {
        parser.executeCommand(
                "deadline submit report /by 30/08/2026 1430 /notes Attach the appendix", tasks, ui);

        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals("Attach the appendix", deadline.getNotes());
    }

    @Test
    void executeCommand_eventCommandWithTimes_addsEvent() throws MondayException {
        boolean shouldContinue = parser.executeCommand(
                "event team retreat /from 10/09/2026 0900 /to 11/09/2026 1700", tasks, ui);

        Event event = assertInstanceOf(Event.class, tasks.get(0));
        assertTrue(shouldContinue);
        assertEquals("team retreat", event.getDescription());
        assertEquals(LocalDate.of(2026, 9, 10), event.getStartDate());
        assertEquals(LocalTime.of(9, 0), event.getStartTime());
        assertEquals(LocalDate.of(2026, 9, 11), event.getEndDate());
        assertEquals(LocalTime.of(17, 0), event.getEndTime());
    }

    @Test
    void executeCommand_eventCommandWithoutTimes_addsEventWithoutTimes() throws MondayException {
        parser.executeCommand(
                "event team retreat /from 10/09/2026 /to 11/09/2026", tasks, ui);

        Event event = assertInstanceOf(Event.class, tasks.get(0));
        assertNull(event.getStartTime());
        assertNull(event.getEndTime());
    }

    @Test
    void executeCommand_eventCommandWithNotes_addsEventWithNotes() throws MondayException {
        parser.executeCommand(
                "event team retreat /from 10/09/2026 /to 11/09/2026 /notes Bring a jacket", tasks, ui);

        Event event = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("Bring a jacket", event.getNotes());
    }

    @Test
    void executeCommand_markCommand_marksTaskUsingOneBasedTaskNumber() throws MondayException {
        tasks.add(new Todo("first task"));
        tasks.add(new Todo("second task"));

        boolean shouldContinue = parser.executeCommand("mark 2", tasks, ui);

        assertTrue(shouldContinue);
        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
        assertEquals("Nice! I've marked this task as done:\n" + "  [T][X] second task", ui.getResponse());
    }

    @Test
    void executeCommand_unmarkCommand_unmarksTask() throws MondayException {
        tasks.add(new Todo("completed task", true));

        boolean shouldContinue = parser.executeCommand("unmark 1", tasks, ui);

        assertTrue(shouldContinue);
        assertFalse(tasks.get(0).isDone());
        assertEquals("OK, I've marked this task as not done yet:\n" + "  [T][ ] completed task", ui.getResponse());
    }

    @Test
    void executeCommand_deleteCommand_removesRequestedTask() throws MondayException {
        tasks.add(new Todo("first task"));
        tasks.add(new Todo("second task"));

        boolean shouldContinue = parser.executeCommand("delete 1", tasks, ui);

        assertTrue(shouldContinue);
        assertEquals(1, tasks.size());
        assertEquals("second task", tasks.get(0).getDescription());
        assertEquals("Noted. I've removed this task:\n"
                + "  [T][ ] first task\n"
                + "Now you have 1 tasks in the list.", ui.getResponse());
    }

    @Test
    void executeCommand_listCommand_showsAllTasks() throws MondayException {
        tasks.add(new Todo("buy milk"));
        tasks.add(new Todo("submit report", true));

        boolean shouldContinue = parser.executeCommand("list", tasks, ui);

        assertTrue(shouldContinue);
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] buy milk\n"
                + "2.[T][X] submit report", ui.getResponse());
    }

    @Test
    void executeCommand_findCommand_showsMatchingTasksIgnoringCase() throws MondayException {
        tasks.add(new Todo("Read Java chapter"));
        tasks.add(new Todo("buy milk"));
        tasks.add(new Todo("review java notes"));

        boolean shouldContinue = parser.executeCommand("find jAvA", tasks, ui);

        assertTrue(shouldContinue);
        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] Read Java chapter\n"
                + "2.[T][ ] review java notes", ui.getResponse());
    }

    @Test
    void executeCommand_findCommandWithoutMatches_showsNoMatchesMessage() throws MondayException {
        tasks.add(new Todo("buy milk"));

        parser.executeCommand("find report", tasks, ui);

        assertEquals("No matching tasks found.", ui.getResponse());
    }

    @Test
    void executeCommand_byeCommand_returnsFalseAndShowsFarewell() throws MondayException {
        boolean shouldContinue = parser.executeCommand("bye", tasks, ui);

        assertFalse(shouldContinue);
        assertEquals("Bye. Hope to see you again soon!", ui.getResponse());
    }

    @Test
    void executeCommand_unknownCommand_throwsMondayException() {
        assertInvalidCommand("remind me tomorrow", "Please tell me your task or which task to mark/unmark.");
    }

    @Test
    void executeCommand_todoCommandWithoutDescription_throwsMondayException() {
        assertInvalidCommand("todo", "Please tell me your todo task.");
        assertInvalidCommand("todo   ", "Please tell me your todo task.");
    }

    @Test
    void executeCommand_deadlineCommandWithMissingParts_throwsMondayException() {
        assertInvalidCommand("deadline", "Please tell me your task.");
        assertInvalidCommand("deadline submit report", "Please use the '/by' command.");
        assertInvalidCommand("deadline /by 30/08/2026", "Please tell me your task.");
        assertInvalidCommand("deadline submit report /by", "Please tell me your deadline.");
    }

    @Test
    void executeCommand_deadlineCommandWithInvalidDateTime_throwsMondayException() {
        assertInvalidCommand("deadline submit report /by 31/02/2026",
                "Please use the format dd/MM/yyyy or dd/MM/yyyy HHmm.");
        assertInvalidCommand("deadline submit report /by 30/08/2026 1430 extra",
                "Please use the format dd/MM/yyyy or dd/MM/yyyy HHmm.");
    }

    @Test
    void executeCommand_eventCommandWithMissingParts_throwsMondayException() {
        assertInvalidCommand("event", "Please tell me your task.");
        assertInvalidCommand("event team retreat /from 10/09/2026", "Please use the '/from' and '/to' commands.");
        assertInvalidCommand("event team retreat /to 11/09/2026 /from 10/09/2026",
                "The '/from' command needs to come before the '/to' command.");
        assertInvalidCommand("event /from 10/09/2026 /to 11/09/2026", "Please tell me your task.");
        assertInvalidCommand("event team retreat /from /to 11/09/2026", "Please tell me your start time.");
        assertInvalidCommand("event team retreat /from 10/09/2026 /to", "Please tell me your end time.");
    }

    @Test
    void executeCommand_eventCommandWithInvalidDateTime_throwsMondayException() {
        assertInvalidCommand("event team retreat /from 31/02/2026 /to 11/09/2026",
                "Please use the format dd/MM/yyyy or dd/MM/yyyy HHmm.");
        assertInvalidCommand("event team retreat /from 10/09/2026 0900 extra /to 11/09/2026",
                "Please use the format dd/MM/yyyy or dd/MM/yyyy HHmm.");
    }

    @Test
    void executeCommand_eventCommandWithInvalidRange_throwsMondayException() {
        assertInvalidCommand("event meeting /from 11/09/2026 /to 10/09/2026",
                "The event end must be after its start.");
        assertInvalidCommand("event meeting /from 10/09/2026 1700 /to 10/09/2026 0900",
                "The event end must be after its start.");
        assertInvalidCommand("event meeting /from 10/09/2026 /to 10/09/2026",
                "The event end must be after its start.");
    }

    @Test
    void executeCommand_repeatedDirectiveOrStorageDelimiter_throwsMondayException() {
        assertInvalidCommand("deadline submit report /by 30/08/2026 /by 31/08/2026",
                "Please specify '/by' only once.");
        assertInvalidCommand("event meeting /from 10/09/2026 /from 11/09/2026 /to 12/09/2026",
                "Please specify '/from' and '/to' only once each.");
        assertInvalidCommand("todo buy | milk", "Task descriptions cannot contain '|', newlines, or carriage returns.");
        assertInvalidCommand("todo buy milk /notes first /notes second", "Please specify '/notes' only once.");
        assertInvalidCommand("todo buy milk /notes", "Please tell me your notes.");
        assertInvalidCommand("todo buy milk /notes use | voucher",
                "Task notes cannot contain '|', newlines, or carriage returns.");
    }

    @Test
    void executeCommand_commandWithSurroundingWhitespace_isAccepted() throws MondayException {
        boolean shouldContinue = parser.executeCommand("  todo\tbuy milk  ", tasks, ui);

        assertTrue(shouldContinue);
        assertEquals("buy milk", tasks.get(0).getDescription());
    }

    @Test
    void executeCommand_taskCommandWithMissingNumber_throwsMondayException() {
        assertInvalidCommand("mark", "Please tell me which task number to mark.");
        assertInvalidCommand("unmark", "Please tell me which task number to unmark.");
        assertInvalidCommand("delete", "Please tell me which task number to delete.");
    }

    @Test
    void executeCommand_taskCommandWithInvalidNumber_throwsMondayException() {
        tasks.add(new Todo("buy milk"));

        assertInvalidCommand("mark zero", "Please enter a valid task number.");
        assertInvalidCommand("unmark 0", "Please tell me a valid task number to unmark.");
        assertInvalidCommand("delete 2", "Please tell me a valid task number to delete.");
    }

    @Test
    void executeCommand_findCommandWithoutKeyword_throwsMondayException() {
        assertInvalidCommand("find", "Please tell me what to find.");
        assertInvalidCommand("find   ", "Please tell me what to find.");
    }

    private void assertInvalidCommand(String command, String expectedMessage) {
        MondayException exception = assertThrows(
                MondayException.class, () -> parser.executeCommand(command, tasks, ui));

        assertEquals(expectedMessage, exception.getMessage());
    }
}

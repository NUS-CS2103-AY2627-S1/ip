package chattingheads.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import chattingheads.exception.ChattingHeadsException;
import chattingheads.exception.InvalidInputException;
import chattingheads.exception.InvalidTaskNumberException;
import chattingheads.exception.InvalidTaskTypeException;
import chattingheads.task.Deadline;
import chattingheads.task.Event;
import chattingheads.task.TaskList;
import chattingheads.task.Todo;
import chattingheads.ui.Ui;

public class CommandTest {

    private final Ui ui = new Ui();

    @Test
    public void addTodoCommand_execute_addsTodo() throws ChattingHeadsException {
        TaskList taskList = new TaskList();
        Command command = new AddTodoCommand("read book");

        command.execute(taskList, ui);

        assertEquals(1, taskList.size());
        assertEquals("T,read book,false", taskList.get(0).toCsv());
    }

    @Test
    public void addDeadlineCommand_execute_addsDeadline() throws ChattingHeadsException {
        TaskList taskList = new TaskList();
        Command command = new AddDeadlineCommand(
                "submit report",
                LocalDateTime.of(2026, 8, 28, 18, 0));

        command.execute(taskList, ui);

        assertEquals("D,submit report,false,2026-08-28T18:00", taskList.get(0).toCsv());
    }

    @Test
    public void addEventCommand_execute_addsEvent() throws ChattingHeadsException {
        TaskList taskList = new TaskList();
        Command command = new AddEventCommand(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 18, 0),
                LocalDateTime.of(2026, 8, 28, 20, 0));

        command.execute(taskList, ui);

        assertEquals("E,meeting,false,2026-08-28T18:00,2026-08-28T20:00", taskList.get(0).toCsv());
    }

    @Test
    public void addEventCommand_invalidTimeRange_throwsAndDoesNotAddTask() {
        TaskList taskList = new TaskList();
        Command command = new AddEventCommand(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 20, 0),
                LocalDateTime.of(2026, 8, 28, 18, 0));

        assertThrows(InvalidInputException.class, () -> command.execute(taskList, ui));
        assertTrue(taskList.isEmpty());
    }

    @Test
    public void deleteCommand_execute_usesOneBasedTaskNumber() throws InvalidTaskNumberException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("first"));
        taskList.add(new Todo("second"));

        new DeleteCommand(1).execute(taskList, ui);

        assertEquals(1, taskList.size());
        assertEquals("second", taskList.get(0).getDescription());
    }

    @Test
    public void markCommand_execute_usesOneBasedTaskNumber() throws InvalidTaskNumberException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("first"));
        taskList.add(new Todo("second"));

        new MarkCommand(2).execute(taskList, ui);

        assertFalse(taskList.get(0).isDone());
        assertTrue(taskList.get(1).isDone());
    }

    @Test
    public void unmarkCommand_execute_usesOneBasedTaskNumber() throws InvalidTaskNumberException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("first", true));
        taskList.add(new Todo("second", true));

        new UnmarkCommand(2).execute(taskList, ui);

        assertTrue(taskList.get(0).isDone());
        assertFalse(taskList.get(1).isDone());
    }

    @Test
    public void listCommand_execute_returnsTaskListMessage() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        String actual = new ListCommand().execute(taskList, ui);

        assertEquals(ui.getTaskListMessage(taskList), actual);
    }

    @Test
    public void findCommand_execute_returnsOnlyMatchingTasks() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("buy milk"));

        String actual = new FindCommand("read").execute(taskList, ui);

        assertTrue(actual.contains("1. [T][ ] read book"));
        assertFalse(actual.contains("2. [T][ ] buy milk"));
    }

    @Test
    public void rescheduleDeadlineCommand_execute_changesSelectedDeadline()
            throws InvalidTaskNumberException, InvalidTaskTypeException {
        TaskList taskList = new TaskList();
        taskList.add(new Deadline("first", LocalDateTime.of(2026, 8, 28, 18, 0)));
        taskList.add(new Deadline("second", LocalDateTime.of(2026, 8, 29, 18, 0)));

        new RescheduleDeadlineCommand(
                2,
                LocalDateTime.of(2026, 9, 1, 12, 0)).execute(taskList, ui);

        assertEquals("D,first,false,2026-08-28T18:00", taskList.get(0).toCsv());
        assertEquals("D,second,false,2026-09-01T12:00", taskList.get(1).toCsv());
    }

    @Test
    public void rescheduleDeadlineCommand_wrongTaskType_throwsInvalidTaskTypeException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("test"));

        assertThrows(
                InvalidTaskTypeException.class, () -> new RescheduleDeadlineCommand(
                        1,
                        LocalDateTime.of(2026, 9, 1, 12, 0)
                ).execute(taskList, ui)
        );
    }

    @Test
    public void rescheduleEventCommand_execute_changesSelectedEvent()
            throws InvalidInputException, InvalidTaskNumberException, InvalidTaskTypeException {
        TaskList taskList = new TaskList();
        taskList.add(new Event(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 18, 0),
                LocalDateTime.of(2026, 8, 28, 20, 0)));

        new RescheduleEventCommand(
                1,
                LocalDateTime.of(2026, 8, 30, 10, 0),
                LocalDateTime.of(2026, 8, 30, 12, 0)).execute(taskList, ui);

        assertEquals("E,meeting,false,2026-08-30T10:00,2026-08-30T12:00", taskList.get(0).toCsv());
    }

    @Test
    public void rescheduleEventCommand_wrongTaskType_throwsInvalidTaskTypeException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("test"));

        assertThrows(
                InvalidTaskTypeException.class, () -> new RescheduleEventCommand(
                        1,
                        LocalDateTime.of(2026, 8, 30, 10, 0),
                        LocalDateTime.of(2026, 8, 30, 12, 0)
                ).execute(taskList, ui)
        );
    }

    @Test
    public void rescheduleEventCommand_invalidRange_throwsInvalidInputExceptionAndKeepsOldTimes()
            throws InvalidInputException {
        TaskList taskList = new TaskList();
        taskList.add(new Event(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 18, 0),
                LocalDateTime.of(2026, 8, 28, 20, 0)));

        assertThrows(
                InvalidInputException.class, () -> new RescheduleEventCommand(
                        1,
                        LocalDateTime.of(2026, 8, 30, 12, 0),
                        LocalDateTime.of(2026, 8, 30, 10, 0)
                ).execute(taskList, ui)
        );
        assertEquals("E,meeting,false,2026-08-28T18:00,2026-08-28T20:00", taskList.get(0).toCsv());
    }

    @Test
    public void exitCommand_execute_returnsShutdownMessage() {
        String actual = new ExitCommand().execute(new TaskList(), ui);
        assertEquals(ui.getShutdownMessage(), actual);
    }

    @Test
    public void shouldExit_exitCommandTrue_otherCommandFalse() {
        assertTrue(new ExitCommand().shouldExit());
        assertFalse(new ListCommand().shouldExit());
    }

    @Test
    public void shouldSave_mutatingCommandsTrue_nonMutatingCommandsFalse() throws InvalidInputException {
        assertTrue(new AddTodoCommand("test").shouldSave());
        assertTrue(new AddDeadlineCommand(
                "test", LocalDateTime.of(2026, 8, 28, 18, 0)).shouldSave());
        assertTrue(new AddEventCommand(
                "test",
                LocalDateTime.of(2026, 8, 28, 18, 0),
                LocalDateTime.of(2026, 8, 28, 20, 0)).shouldSave());
        assertTrue(new DeleteCommand(1).shouldSave());
        assertTrue(new MarkCommand(1).shouldSave());
        assertTrue(new UnmarkCommand(1).shouldSave());
        assertTrue(new RescheduleDeadlineCommand(
                1, LocalDateTime.of(2026, 8, 30, 18, 0)).shouldSave());
        assertTrue(new RescheduleEventCommand(
                1,
                LocalDateTime.of(2026, 8, 30, 18, 0),
                LocalDateTime.of(2026, 8, 30, 20, 0)).shouldSave());

        assertFalse(new ListCommand().shouldSave());
        assertFalse(new FindCommand("test").shouldSave());
        assertFalse(new ExitCommand().shouldSave());
    }

    @Test
    public void taskNumberCommands_zeroTaskNumber_throwInvalidTaskNumberException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("test"));

        assertThrows(
                InvalidTaskNumberException.class, () -> new MarkCommand(0).execute(taskList, ui)
        );
        assertThrows(
                InvalidTaskNumberException.class, () -> new UnmarkCommand(0).execute(taskList, ui)
        );
        assertThrows(
                InvalidTaskNumberException.class, () -> new DeleteCommand(0).execute(taskList, ui)
        );
        assertThrows(
                InvalidTaskNumberException.class, () -> new RescheduleDeadlineCommand(
                        0,
                        LocalDateTime.of(2026, 8, 30, 18, 0)
                ).execute(taskList, ui)
        );
        assertThrows(
                InvalidTaskNumberException.class, () -> new RescheduleEventCommand(
                        0,
                        LocalDateTime.of(2026, 8, 30, 18, 0),
                        LocalDateTime.of(2026, 8, 30, 20, 0)
                ).execute(taskList, ui)
        );
    }
}

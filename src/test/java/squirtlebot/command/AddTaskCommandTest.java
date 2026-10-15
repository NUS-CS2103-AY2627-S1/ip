package squirtlebot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import squirtlebot.exception.CommandException;
import squirtlebot.storage.Storage;
import squirtlebot.task.Deadline;
import squirtlebot.task.TaskList;
import squirtlebot.task.ToDo;
import squirtlebot.ui.Ui;

/**
 * Tests commands that add todo and deadline tasks.
 */
public class AddTaskCommandTest {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        storage = new Storage();
        storage.disable();
        tasks = new TaskList();
        ui = new Ui();
    }

    @Test
    public void addToDo_execute_addsTaskAndUpdatesMessage() {
        AddToDoCommand command = new AddToDoCommand("todo read chapter 2".split(" "));

        command.execute(tasks, ui, storage);

        assertEquals(new ToDo("read chapter 2"), tasks.get(0));
        assertEquals("added: [T] [ ] read chapter 2 to your list of tasks\nYou now have 1 tasks",
                ui.getSavedMessage());
    }

    @Test
    public void addToDo_emptyDescription_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new AddToDoCommand(new String[]{"todo"}));

        assertEquals("Please provide the correct arguments for ToDo!", exception.getMessage());
    }

    @Test
    public void addDeadline_execute_addsTaskWithParsedDate() {
        AddDeadlineCommand command = new AddDeadlineCommand(
                "deadline submit report /by 30-09-2026".split(" "));

        command.execute(tasks, ui, storage);

        assertEquals(new Deadline("submit report", LocalDate.of(2026, 9, 30)), tasks.get(0));
        assertEquals("added: [D] [ ] submit report (by: 2026-09-30) to your list of tasks\n"
                + "You now have 1 tasks", ui.getSavedMessage());
    }

    @Test
    public void addDeadline_missingDueDate_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new AddDeadlineCommand("deadline submit report".split(" ")));

        assertEquals("Please provide the correct arguments for Deadline!", exception.getMessage());
    }

    @Test
    public void addDeadline_invalidDate_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new AddDeadlineCommand(
                        "deadline submit report /by tomorrow".split(" ")));

        assertEquals("Please enter a valid due date/datetime!", exception.getMessage());
    }
}

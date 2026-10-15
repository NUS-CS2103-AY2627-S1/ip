package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import magnus.exception.CommandSyntaxException;
import magnus.exception.DuplicateTaskException;
import magnus.exception.MagnusException;
import magnus.task.TaskList;
import magnus.task.ToDoTask;

/**
 * Tests validation and task creation for {@link ToDoCommand}.
 */
public class ToDoCommandTest {
    @Test
    public void execute_validDescription_addsTodoTask() throws MagnusException {
        TaskList tasks = new TaskList();
        ToDoCommand command = new ToDoCommand(tasks);

        String response = command.execute("  read a book  ");

        assertEquals("\tOpening move complete - I've added this To-Do task:\n\n\t[T][ ] read a book\n\n"
                + "\tYou now have 1 task in the list.", response);
        assertEquals(1, tasks.getTaskCount());
        assertEquals("read a book", tasks.getTask(0).getDescription());
    }

    @Test
    public void execute_withExistingTask_reportsPluralTaskCount() throws MagnusException {
        TaskList tasks = new TaskList();
        tasks.addTask(new ToDoTask("write notes"));

        String response = new ToDoCommand(tasks).execute("read a book");

        assertEquals("\tOpening move complete - I've added this To-Do task:\n\n\t[T][ ] read a book\n\n"
                + "\tYou now have 2 tasks in the list.", response);
    }

    @Test
    public void execute_missingDescription_throwsCommandSyntaxException() {
        ToDoCommand command = new ToDoCommand(new TaskList());

        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> command.execute(new String[0]));

        assertEquals("\tThat move is incomplete - please give me the task description.\n"
                + "\tUsage: todo <task description>", exception.getMessage());
    }

    @Test
    public void execute_blankDescription_throwsCommandSyntaxException() {
        ToDoCommand command = new ToDoCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () -> command.execute("   "));
    }

    @Test
    public void execute_multipleArguments_throwsCommandSyntaxExceptionWithoutAddingTask() {
        TaskList tasks = new TaskList();
        ToDoCommand command = new ToDoCommand(tasks);

        assertThrows(CommandSyntaxException.class, () -> command.execute("read", "book"));

        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void execute_reservedDateField_throwsCommandSyntaxExceptionWithoutAddingTask() {
        TaskList tasks = new TaskList();
        ToDoCommand command = new ToDoCommand(tasks);

        assertThrows(CommandSyntaxException.class, () -> command.execute("read /by Friday"));

        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void execute_duplicateDescription_throwsDuplicateTaskException() {
        TaskList tasks = new TaskList();
        tasks.addTask(new ToDoTask("read a book"));
        ToDoCommand command = new ToDoCommand(tasks);

        DuplicateTaskException exception = assertThrows(
                DuplicateTaskException.class, () -> command.execute("read a book"));

        assertEquals("\tThat piece is already on the board - an identical task already exists.",
                exception.getMessage());
        assertEquals(1, tasks.getTaskCount());
    }
}

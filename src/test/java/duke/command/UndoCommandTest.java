package duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import duke.exception.GaryException;
import duke.task.TaskList;
import duke.ui.CapturingUi;

public class UndoCommandTest {

    @Test
    public void execute_afterTaskChange_undoesChangeAndShowsConfirmation() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        CapturingUi ui = new CapturingUi();

        new UndoCommand("").execute(tasks, ui);

        assertEquals(0, tasks.size());
        assertEquals("Undid the last change.", ui.getLastMessage());
    }

    @Test
    public void execute_withArguments_throwsGaryExceptionWithoutConsumingHistory() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        CapturingUi ui = new CapturingUi();

        GaryException exception = assertThrows(GaryException.class, () ->
                new UndoCommand("2").execute(tasks, ui));

        assertEquals("The undo command does not take any arguments.", exception.getMessage());
        new UndoCommand("").execute(tasks, ui);
        assertEquals(0, tasks.size());
    }

    @Test
    public void execute_withoutHistory_throwsGaryException() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        GaryException exception = assertThrows(GaryException.class, () ->
                new UndoCommand("").execute(tasks, ui));

        assertEquals("There is no change to undo.", exception.getMessage());
    }
}

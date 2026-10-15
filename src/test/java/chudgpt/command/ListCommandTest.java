package chudgpt.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import chudgpt.task.Priority;
import chudgpt.task.TaskList;
import chudgpt.task.ToDo;
import chudgpt.ui.Ui;

/** Tests read-only sorted listing and unchanged empty-list responses. */
public class ListCommandTest {
    @Test
    public void execute_sortedView_usesUnderlyingNumbersWithoutStorage() {
        TaskList tasks = new TaskList(List.of(new ToDo("first"), new ToDo("second").setPriority(Priority.HIGH)));
        Ui ui = new Ui();

        // No storage is supplied: listing must not attempt to save.
        assertEquals("Here are the tasks in your list:\n2. [T][ ][P:HIGH   ] second\n1. [T][ ][P:NONE   ] first",
                new ListCommand(true).execute(tasks, ui, null));
        assertEquals("Here are the tasks in your list:\n1. [T][ ][P:NONE   ] first\n2. [T][ ][P:HIGH   ] second",
                new ListCommand().execute(tasks, ui, null));
    }

    @Test
    public void execute_emptySortedView_usesExistingMessage() {
        assertEquals("You have no tasks in your list! Try adding some",
                new ListCommand(true).execute(new TaskList(), new Ui(), null));
    }
}

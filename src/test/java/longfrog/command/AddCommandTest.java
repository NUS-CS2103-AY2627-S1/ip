package longfrog.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import longfrog.task.Task;
import longfrog.task.TaskList;
import longfrog.task.Todo;
import longfrog.ui.Ui;

class AddCommandTest {
    @Test
    void execute_uniqueTask_addsTaskAndShowsConfirmation() {
        TaskList taskList = new TaskList();
        Todo task = new Todo("read book");
        List<String> messages = new ArrayList<>();

        boolean shouldExit = new AddCommand(taskList, task).execute(new Ui(messages::add));

        assertFalse(shouldExit);
        assertEquals(1, taskList.getCount());
        assertSame(task, taskList.getTask(0));
        assertEquals(List.of("Task secured on the lily pad: [T][ ] read book"), messages);
    }

    @Test
    void execute_duplicateTask_rejectsTaskAndShowsFirstMatch() {
        Todo firstMatch = new Todo("Read Book");
        firstMatch.markAsDone();
        Todo secondMatch = new Todo("read   book");
        TaskList taskList = new TaskList(new ArrayList<>(List.of(new Todo("first"), firstMatch, secondMatch)));
        List<Task> originalTasks = taskList.getTasks();
        List<String> messages = new ArrayList<>();

        boolean shouldExit = new AddCommand(taskList, new Todo("READ BOOK"))
                .execute(new Ui(messages::add));

        assertFalse(shouldExit);
        assertEquals(originalTasks, taskList.getTasks());
        assertEquals(List.of("Duplicate detected; task already exists at position 2: [T][X] Read Book"),
                messages);
    }
}

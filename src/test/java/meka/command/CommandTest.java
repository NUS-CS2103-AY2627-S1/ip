package meka.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import meka.storage.Storage;
import meka.task.Task;
import meka.task.TaskList;
import meka.task.Todo;
import meka.ui.Ui;

public class CommandTest {
    @TempDir
    private Path directory;

    @Test
    public void execute_addCommand_addsPersistsAndReportsTask() throws Exception {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        Storage storage = new Storage(directory.resolve("meka.txt"));
        CapturingUi ui = new CapturingUi();

        new AddCommand(task).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertSame(task, ui.changedTask);
        assertEquals(1, ui.taskCount);
        assertEquals("T | 0 | read book", storage.load().get(1).toDataString());
    }

    @Test
    public void execute_listCommand_reportsTasksWithoutSaving() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Path file = directory.resolve("meka.txt");
        CapturingUi ui = new CapturingUi();
        ListCommand command = new ListCommand();

        command.execute(tasks, ui, new Storage(file));

        assertSame(tasks, ui.displayedTasks);
        assertFalse(Files.exists(file));
        assertFalse(command.isExit());
    }

    @Test
    public void execute_markCommand_marksPersistsAndReportsTask() throws Exception {
        Task task = new Todo("read book");
        TaskList tasks = taskListWith(task);
        Storage storage = new Storage(directory.resolve("meka.txt"));
        CapturingUi ui = new CapturingUi();

        new MarkCommand(1).execute(tasks, ui, storage);

        assertEquals("X", task.getStatusIcon());
        assertSame(task, ui.changedTask);
        assertEquals("X", storage.load().get(1).getStatusIcon());
    }

    @Test
    public void execute_unmarkCommand_unmarksPersistsAndReportsTask() throws Exception {
        Task task = new Todo("read book");
        task.markAsDone();
        TaskList tasks = taskListWith(task);
        Storage storage = new Storage(directory.resolve("meka.txt"));
        CapturingUi ui = new CapturingUi();

        new UnmarkCommand(1).execute(tasks, ui, storage);

        assertEquals(" ", task.getStatusIcon());
        assertSame(task, ui.changedTask);
        assertEquals(" ", storage.load().get(1).getStatusIcon());
    }

    @Test
    public void execute_deleteCommand_deletesPersistsAndReportsCount() throws Exception {
        Task removedTask = new Todo("first");
        Task remainingTask = new Todo("second");
        TaskList tasks = taskListWith(removedTask);
        tasks.add(remainingTask);
        Storage storage = new Storage(directory.resolve("meka.txt"));
        CapturingUi ui = new CapturingUi();

        new DeleteCommand(1).execute(tasks, ui, storage);

        assertSame(removedTask, ui.changedTask);
        assertEquals(1, ui.taskCount);
        assertSame(remainingTask, tasks.get(1));
        assertEquals("T | 0 | second", storage.load().get(1).toDataString());
    }

    @Test
    public void execute_exitCommand_reportsGoodbyeAndRequestsExit() {
        CapturingUi ui = new CapturingUi();
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), ui, new Storage(directory.resolve("meka.txt")));

        assertTrue(ui.isGoodbyeShown);
        assertTrue(command.isExit());
    }

    private TaskList taskListWith(Task task) {
        TaskList tasks = new TaskList();
        tasks.add(task);
        return tasks;
    }

    private static class CapturingUi extends Ui {
        private Task changedTask;
        private TaskList displayedTasks;
        private int taskCount;
        private boolean isGoodbyeShown;

        @Override
        public void showTaskAdded(Task task, int taskCount) {
            this.changedTask = task;
            this.taskCount = taskCount;
        }

        @Override
        public void showTaskList(TaskList tasks) {
            displayedTasks = tasks;
        }

        @Override
        public void showTaskMarked(Task task) {
            changedTask = task;
        }

        @Override
        public void showTaskUnmarked(Task task) {
            changedTask = task;
        }

        @Override
        public void showTaskDeleted(Task task, int taskCount) {
            this.changedTask = task;
            this.taskCount = taskCount;
        }

        @Override
        public void showGoodbye() {
            isGoodbyeShown = true;
        }
    }
}

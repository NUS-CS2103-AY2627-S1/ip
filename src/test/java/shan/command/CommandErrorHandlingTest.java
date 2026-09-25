package shan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shan.exception.InvalidArgumentException;
import shan.storage.Storage;
import shan.task.Task;
import shan.task.TaskList;
import shan.task.ToDo;

class CommandErrorHandlingTest {
    @TempDir
    Path tempDirectory;

    @Test
    void mark_alreadyCompletedTask_exceptionThrownWithoutSaving() {
        Task task = new ToDo("read book");
        task.markDone();
        TaskList tasks = taskListOf(task);
        Path dataFile = this.tempDirectory.resolve("shan.txt");

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () ->
                new MarkCommand(1).execute(tasks, new Storage(dataFile)));

        assertEquals("Task 1 is already marked as done.", exception.getMessage());
        assertTrue(task.isDone());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void unmark_incompleteTask_exceptionThrownWithoutSaving() {
        Task task = new ToDo("read book");
        TaskList tasks = taskListOf(task);
        Path dataFile = this.tempDirectory.resolve("shan.txt");

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () ->
                new UnmarkCommand(1).execute(tasks, new Storage(dataFile)));

        assertEquals("Task 1 is already marked as not done.", exception.getMessage());
        assertFalse(task.isDone());
        assertFalse(Files.exists(dataFile));
    }

    private TaskList taskListOf(Task task) {
        TaskList tasks = new TaskList();
        tasks.add(task);
        return tasks;
    }
}

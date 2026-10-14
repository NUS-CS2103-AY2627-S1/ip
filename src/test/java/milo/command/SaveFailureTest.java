package milo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.Task;
import milo.task.TaskList;
import milo.task.Todo;
import milo.ui.Ui;

/** Verifies that real storage failures preserve tasks and never announce success. */
public class SaveFailureTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_markPendingTask_saveFailurePreservesPendingStatus() throws IOException {
        assertStatusPreserved(new MarkCommand(1), false);
    }

    @Test
    public void execute_markCompletedTask_saveFailurePreservesCompletedStatus() throws IOException {
        assertStatusPreserved(new MarkCommand(1), true);
    }

    @Test
    public void execute_unmarkPendingTask_saveFailurePreservesPendingStatus() throws IOException {
        assertStatusPreserved(new UnmarkCommand(1), false);
    }

    @Test
    public void execute_unmarkCompletedTask_saveFailurePreservesCompletedStatus() throws IOException {
        assertStatusPreserved(new UnmarkCommand(1), true);
    }

    @Test
    public void execute_deleteTask_saveFailureRestoresOrderAndStatus() throws IOException {
        Task firstTask = new Todo("first");
        Task middleTask = new Todo("middle");
        Task lastTask = new Todo("last");
        middleTask.markAsDone();
        TaskList tasks = new TaskList(List.of(firstTask, middleTask, lastTask));

        assertSaveFailsWithoutSuccessOutput(new DeleteCommand(2), tasks);

        assertEquals(List.of(firstTask, middleTask, lastTask), tasks.getTasks());
        assertEquals(true, middleTask.isDone());
    }

    @Test
    public void execute_addTask_saveFailureRestoresOriginalList() throws IOException {
        Task originalTask = new Todo("original");
        TaskList tasks = new TaskList(List.of(originalTask));

        assertSaveFailsWithoutSuccessOutput(new AddCommand(new Todo("new")), tasks);

        assertEquals(List.of(originalTask), tasks.getTasks());
    }

    /** Checks both the selected task's identity and its original completion state. */
    private void assertStatusPreserved(Command command, boolean isDone) throws IOException {
        Task task = new Todo("read book");
        if (isDone) {
            task.markAsDone();
        }
        TaskList tasks = new TaskList(List.of(task));

        assertSaveFailsWithoutSuccessOutput(command, tasks);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
        assertEquals(isDone, task.isDone());
    }

    /** A file in place of a directory forces a portable, deterministic save failure. */
    private void assertSaveFailsWithoutSuccessOutput(Command command, TaskList tasks) throws IOException {
        Path blockedDirectory = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockedDirectory, "Keep this file intact.");
        Storage storage = new Storage(blockedDirectory.resolve("tasks.txt").toString());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8);
                Ui ui = new Ui(output)) {
            MiloException exception = assertThrows(MiloException.class, () -> command.execute(tasks, ui, storage));
            assertEquals("I couldn't save your tasks.", exception.getMessage());
            assertInstanceOf(IOException.class, exception.getCause());
        }
        assertEquals("", buffer.toString(StandardCharsets.UTF_8));
        assertEquals("Keep this file intact.", Files.readString(blockedDirectory));
    }
}

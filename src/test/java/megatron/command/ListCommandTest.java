package megatron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.exception.MegatronException;
import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.task.Todo;
import megatron.ui.Ui;

/**
 * Tests task display behavior by {@link ListCommand}.
 */
class ListCommandTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void execute_populatedList_displaysHeadingAndTasksWithoutChangingState() throws MegatronException {
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        secondTask.markAsDone();
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new ListCommand().execute(tasks, createUi(output), new TaskStorage(storageFile.toString()));

        assertEquals(expectedListOutput(), output.toString(StandardCharsets.UTF_8));
        assertEquals(2, tasks.size());
        assertFalse(tasks.getTask(1).isDone());
        assertTrue(tasks.getTask(2).isDone());
        assertFalse(Files.exists(storageFile));
    }

    @Test
    void execute_emptyList_displaysEmptyStateAndDoesNotSave() throws MegatronException {
        TaskList tasks = new TaskList();
        Path storageFile = tempDirectory.resolve("tasks.csv");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new ListCommand().execute(tasks, createUi(output), new TaskStorage(storageFile.toString()));

        assertEquals("Your task empire is empty. Add a task before it gets awkward."
                + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
        assertEquals(0, tasks.size());
        assertFalse(Files.exists(storageFile));
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private static String expectedListOutput() {
        return "Behold! The current state of your task empire:" + System.lineSeparator()
                + "1.[T][ ] first" + System.lineSeparator()
                + "2.[T][X] second" + System.lineSeparator();
    }
}

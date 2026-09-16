package esther.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import esther.exception.EstherException;
import esther.storage.Storage;
import esther.task.Task;
import esther.task.TaskList;
import esther.task.Todo;

/**
 * Tests shared command processing behavior.
 */
public class CommandProcessorTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void processTodo_addsTaskAndReturnsConfirmation()
            throws IOException, EstherException {
        CommandProcessor processor = newProcessor();

        CommandResult result = processor.process("todo read book");

        assertFalse(result.shouldExit());
        assertFalse(result.isError());
        assertTrue(result.messages().stream().anyMatch(
                message -> message.contains("consider it added")));
    }

    @Test
    public void processInvalidCommand_returnsErrorMessage()
            throws IOException, EstherException {
        CommandResult result = newProcessor().process("todo");

        assertFalse(result.shouldExit());
        assertTrue(result.isError());
        assertTrue(result.messages().stream().anyMatch(
                message -> message.contains("want to add a task")));
    }

    @Test
    public void processBye_returnsExitResult()
            throws IOException, EstherException {
        CommandResult result = newProcessor().process("bye");

        assertTrue(result.shouldExit());
        assertEquals(
                List.of("Byeee! Hope to see you soooon!"),
                result.messages());
    }

    @Test
    public void processTodo_savesTaskToStorage()
            throws IOException, EstherException {
        Storage storage = new Storage(
                temporaryDirectory.resolve("tasks.txt"));
        CommandProcessor processor = new CommandProcessor(
                storage, new TaskList());

        processor.process("todo read book");

        assertEquals(1, storage.load().size());
        assertEquals("[T][ ] read book", storage.load().get(0).toString());
    }

    @Test
    public void processCommand_withSurroundingWhitespace_processesCommand()
            throws IOException, EstherException {
        Storage storage = new Storage(
                temporaryDirectory.resolve("tasks.txt"));
        CommandProcessor processor = new CommandProcessor(
                storage, new TaskList());

        CommandResult result = processor.process("  todo read book  ");

        assertFalse(result.isError());
        assertEquals("[T][ ] read book", storage.load().get(0).toString());
    }

    @Test
    public void processSort_mixedStatuses_reordersTasksStably()
            throws IOException, EstherException {
        Task completedFirst = new Todo("submit form");
        completedFirst.markAsDone();
        Task incompleteFirst = new Todo("buy milk");
        Task completedSecond = new Todo("return book");
        completedSecond.markAsDone();
        Task incompleteSecond = new Todo("read notes");
        TaskList tasks = new TaskList(List.of(
                completedFirst,
                incompleteFirst,
                completedSecond,
                incompleteSecond));
        CommandProcessor processor = new CommandProcessor(
                new Storage(temporaryDirectory.resolve("tasks.txt")), tasks);

        CommandResult result = processor.process("sort");

        assertEquals(
                "Tadaa! Unfinished business first, completed victories after:",
                result.messages().get(0));
        assertEquals(
                List.of(incompleteFirst, incompleteSecond,
                        completedFirst, completedSecond),
                tasks.asList());
    }

    @Test
    public void processSort_reorderedTasks_persistsOrder()
            throws IOException, EstherException {
        Task completedTask = new Todo("submit form");
        completedTask.markAsDone();
        Task incompleteTask = new Todo("buy milk");
        Storage storage = new Storage(
                temporaryDirectory.resolve("tasks.txt"));
        CommandProcessor processor = new CommandProcessor(
                storage, new TaskList(List.of(completedTask, incompleteTask)));

        processor.process("sort");

        TaskList loadedTasks = storage.load();
        assertEquals("[T][ ] buy milk", loadedTasks.get(0).toString());
        assertEquals("[T][X] submit form", loadedTasks.get(1).toString());
    }

    @Test
    public void processMark_marksTaskAndPersistsStatus()
            throws IOException, EstherException {
        Todo task = new Todo("submit form");
        Storage storage = new Storage(
                temporaryDirectory.resolve("tasks.txt"));
        CommandProcessor processor = new CommandProcessor(
                storage, new TaskList(List.of(task)));

        CommandResult result = processor.process("mark 1");

        assertFalse(result.isError());
        assertTrue(result.messages().get(0).contains("marked this task as done"));
        assertTrue(task.isDone());
        assertTrue(storage.load().get(0).isDone());
    }

    @Test
    public void processUnmark_unmarksTaskAndPersistsStatus()
            throws IOException, EstherException {
        Todo task = new Todo("submit form");
        task.markAsDone();
        Storage storage = new Storage(
                temporaryDirectory.resolve("tasks.txt"));
        CommandProcessor processor = new CommandProcessor(
                storage, new TaskList(List.of(task)));

        CommandResult result = processor.process("unmark 1");

        assertFalse(result.isError());
        assertEquals(
                "Oops, back onto the unfinished pile it goes:",
                result.messages().get(0));
        assertFalse(task.isDone());
        assertFalse(storage.load().get(0).isDone());
    }

    @Test
    public void processDelete_removesTaskAndPersistsRemoval()
            throws IOException, EstherException {
        Storage storage = new Storage(
                temporaryDirectory.resolve("tasks.txt"));
        CommandProcessor processor = new CommandProcessor(
                storage, new TaskList(List.of(new Todo("submit form"))));

        CommandResult result = processor.process("delete 1");

        assertFalse(result.isError());
        assertEquals(
                "Poof! This task is officially gone:",
                result.messages().get(0));
        assertEquals(0, storage.load().size());
    }

    @Test
    public void processFind_returnsMatchingTasks() throws IOException, EstherException {
        Storage storage = new Storage(
                temporaryDirectory.resolve("tasks.txt"));
        CommandProcessor processor = new CommandProcessor(
                storage,
                new TaskList(List.of(
                        new Todo("read book"), new Todo("buy milk"))));

        CommandResult result = processor.process("find book");

        assertFalse(result.isError());
        assertEquals(
                List.of("Found them! Here's what matched:", "1.[T][ ] read book"),
                result.messages());
    }

    private CommandProcessor newProcessor() {
        Storage storage = new Storage(
                temporaryDirectory.resolve("tasks.txt"));
        return new CommandProcessor(storage, new TaskList());
    }
}

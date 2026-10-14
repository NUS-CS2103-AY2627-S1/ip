package dude.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import dude.command.core.Command;
import dude.command.core.CommandCreator;
import dude.command.core.CommandQueue;
import dude.command.core.CommandType;
import dude.exception.CommandExecutionException;
import dude.exception.UsageException;
import dude.parser.CommandRequest;
import dude.storage.Storage;
import dude.task.Task;
import dude.task.TaskList;
import dude.task.TaskService;
import dude.task.Todo;
import dude.ui.Ui;

/**
 * Tests the command queue's invocation and session-based undo behavior.
 */
public class CommandQueueTest {
    @Test
    public void addCommand_undoRemovesTask() throws Exception {
        TestContext context = context(new RecordingStorage());

        execute(context, CommandType.TODO, "read book");
        context.queue().undo();

        assertEquals(0, context.receiver().tasks().size());
    }

    @Test
    public void deadlineAndEventCommands_undoRemoveTasks() throws Exception {
        TestContext context = context(new RecordingStorage());

        execute(context, CommandType.DEADLINE, "return book /by 2019-12-02");
        execute(context, CommandType.UNDO, null);
        execute(context, CommandType.EVENT, "project /from 2019-12-01 /to 2019-12-03");
        execute(context, CommandType.UNDO, null);

        assertEquals(0, context.receiver().tasks().size());
    }

    @Test
    public void deleteCommand_undoRestoresOriginalPosition() throws Exception {
        TestContext context = context(new RecordingStorage());
        Task first = new Todo("first");
        Task deleted = new Todo("deleted");
        Task last = new Todo("last");
        context.receiver().add(first);
        context.receiver().add(deleted);
        context.receiver().add(last);

        execute(context, CommandType.DELETE, "2");
        context.queue().undo();

        assertEquals(List.of(first, deleted, last), context.receiver().tasks());
    }

    @Test
    public void markAndUnmark_undoRestoresPreviousState() throws Exception {
        TestContext context = context(new RecordingStorage());
        context.receiver().add(new Todo("read book"));

        execute(context, CommandType.MARK, "1");
        context.queue().undo();
        assertFalse(context.receiver().tasks().get(0).isDone());

        execute(context, CommandType.UNMARK, "1");
        context.queue().undo();
        assertFalse(context.receiver().tasks().get(0).isDone());
    }

    @Test
    public void undo_isLastInFirstOut() throws Exception {
        TestContext context = context(new RecordingStorage());

        execute(context, CommandType.TODO, "first");
        execute(context, CommandType.TODO, "second");
        context.queue().undo();
        assertEquals(List.of("first"), descriptions(context.receiver().tasks()));
        context.queue().undo();
        assertEquals(List.of(), descriptions(context.receiver().tasks()));
    }

    @Test
    public void readOnlyCommands_doNotChangeHistory() throws Exception {
        TestContext context = context(new RecordingStorage());
        context.receiver().add(new Todo("read book"));

        execute(context, CommandType.LIST, null);
        execute(context, CommandType.FIND, "book");

        assertEquals(0, context.queue().size());
    }

    @Test
    public void emptyHistory_doesNotChangeTasks() throws CommandExecutionException {
        TestContext context = context(new RecordingStorage());
        context.receiver().add(new Todo("read book"));

        context.queue().undo();

        assertEquals(List.of("read book"), descriptions(context.receiver().tasks()));
    }

    @Test
    public void failedUndoSave_restoresTaskAndKeepsHistory() throws Exception {
        RecordingStorage storage = new RecordingStorage();
        TestContext context = context(storage);

        execute(context, CommandType.TODO, "read book");
        storage.failNextSave();
        assertThrows(CommandExecutionException.class, () -> context.queue().undo());

        assertEquals(List.of("read book"), descriptions(context.receiver().tasks()));
        assertEquals(1, context.queue().size());
    }

    private static TestContext context(RecordingStorage storage) {
        TaskService receiver = new TaskService(storage);
        CommandQueue queue = new CommandQueue();
        Ui ui = new Ui(new Scanner(""), new PrintStream(new ByteArrayOutputStream()));
        CommandCreator creator = new CommandCreator(receiver,
                conflict -> dude.command.duplicate.DuplicateResolution.add(), queue);
        return new TestContext(receiver, queue, creator, ui);
    }

    private static void execute(TestContext context, CommandType type, String argument)
            throws UsageException, CommandExecutionException {
        Command command = context.creator().create(new CommandRequest(type, argument), context.ui());
        context.queue().execute(command);
    }

    private static List<String> descriptions(List<Task> tasks) {
        return tasks.stream().map(Task::getDescription).toList();
    }

    private record TestContext(TaskService receiver, CommandQueue queue, CommandCreator creator,
            Ui ui) {
    }

    private static class RecordingStorage extends Storage {
        private boolean shouldFailNextSave;

        private void failNextSave() {
            shouldFailNextSave = true;
        }

        @Override
        public void saveTasks(TaskList tasks) throws IOException {
            if (shouldFailNextSave) {
                shouldFailNextSave = false;
                throw new IOException("test failure");
            }
            assertTrue(tasks != null);
        }
    }
}

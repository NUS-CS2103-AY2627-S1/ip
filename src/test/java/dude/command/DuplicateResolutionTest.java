package dude.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import dude.command.core.Command;
import dude.command.core.CommandCreator;
import dude.command.core.CommandQueue;
import dude.command.core.CommandType;
import dude.command.duplicate.DuplicateResolution;
import dude.command.duplicate.DuplicateTaskConflict;
import dude.storage.Storage;
import dude.task.TaskService;
import dude.task.Todo;
import dude.ui.ConsoleDuplicateResolutionHandler;
import dude.ui.Ui;

/**
 * Tests shared duplicate-resolution behaviour and its console adapter.
 */
public class DuplicateResolutionTest {
    @Test
    public void cancelDuplicate_doesNotChangeTaskList() throws Exception {
        TestContext context = context(conflict -> DuplicateResolution.cancel());
        context.receiver().add(new Todo("read book"));

        execute(context, CommandType.TODO, "  READ   BOOK  ");

        assertEquals(1, context.receiver().tasks().size());
        assertEquals("read book", context.receiver().tasks().get(0).getDescription());
    }

    @Test
    public void editDuplicate_preservesDoneStateAndCanBeUndone() throws Exception {
        Todo existing = new Todo("read book");
        existing.markAsDone();
        TestContext context = context(conflict -> DuplicateResolution.edit(0));
        context.receiver().add(existing);

        execute(context, CommandType.TODO, "read   book");

        assertTrue(context.receiver().tasks().get(0).isDone());
        context.queue().undo();
        assertEquals("read book", context.receiver().tasks().get(0).getDescription());
        assertTrue(context.receiver().tasks().get(0).isDone());
    }

    @Test
    public void consoleHandler_acceptsPartialAddChoice() {
        Ui ui = new Ui(new Scanner("AD\n"), new PrintStream(new ByteArrayOutputStream()));
        DuplicateTaskConflict conflict = new DuplicateTaskConflict(new Todo("read book"),
                List.of(new DuplicateTaskConflict.DuplicateMatch(0, new Todo("read book"))));

        DuplicateResolution resolution = new ConsoleDuplicateResolutionHandler(ui).resolve(conflict);

        assertEquals(DuplicateResolution.Action.ADD, resolution.action());
        assertFalse(resolution.action() == DuplicateResolution.Action.CANCEL);
    }

    private static TestContext context(dude.command.duplicate.DuplicateResolutionHandler handler) {
        TaskService receiver = new TaskService(new RecordingStorage());
        CommandQueue queue = new CommandQueue();
        Ui ui = testUi();
        CommandCreator creator = new CommandCreator(receiver, handler, queue);
        return new TestContext(receiver, queue, creator, ui);
    }

    private static void execute(TestContext context, CommandType type, String argument)
            throws Exception {
        Command command = context.creator().create(new dude.parser.CommandRequest(type, argument),
                context.ui());
        context.queue().execute(command);
    }

    private static Ui testUi() {
        return new Ui(new Scanner(""), new PrintStream(new ByteArrayOutputStream()));
    }

    private record TestContext(TaskService receiver, CommandQueue queue, CommandCreator creator,
            Ui ui) {
    }

    private static class RecordingStorage extends Storage {
        @Override
        public void saveTasks(dude.task.TaskList tasks) {
            // Persistence is not part of these in-memory interaction tests.
        }
    }
}

package probe.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import probe.ProbeException;
import probe.parser.Parser;
import probe.storage.Storage;
import probe.task.TaskList;

class CommandHandlerTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void executesTagAndUntagCommands() throws ProbeException {
        CommandHandler handler = createHandler();

        handler.execute("todo read a book");
        handler.execute("tag 1 #fun #weekend");
        String response = handler.execute("untag 1 #fun");

        assertEquals("Removed tags: [T][ ] read a book #weekend", response);
    }

    @Test
    void clearsTagsFromAllTasks() throws ProbeException {
        CommandHandler handler = createHandler();

        handler.execute("todo read a book");
        handler.execute("tag 1 #fun");

        assertEquals("Cleared all tags.", handler.execute("tag clear"));
        assertEquals("1. [T][ ] read a book", handler.execute("list"));
    }

    @Test
    void rejectsTagCommandWithoutTags() throws ProbeException {
        CommandHandler handler = createHandler();

        handler.execute("todo read a book");

        assertThrows(ProbeException.class, () -> handler.execute("tag 1"));
    }

    @Test
    void executesFindCommand() throws ProbeException {
        CommandHandler handler = createHandler();

        handler.execute("todo read a book");

        assertEquals("1. [T][ ] read a book", handler.execute("find BOOK"));
    }

    private CommandHandler createHandler() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        return new CommandHandler(storage, new TaskList(), new Parser());
    }
}

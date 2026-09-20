package bkxss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests GUI response status, state preservation, and compatibility with plain-text clients. */
class CommandResultTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void processCommandResult_validInvalidValid_preservesTasksAndResetsErrorStatus() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        PrintStream originalOutput = System.out;

        CommandResult added = Bkxss.processCommandResult("todo borrow book", tasks, storage);
        assertFalse(added.isError());
        assertTrue(added.message().contains("[T][ ] borrow book"));
        CommandResult invalid = Bkxss.processCommandResult("mark 2", tasks, storage);
        assertTrue(invalid.isError());
        assertEquals("OhNo!! ERROR :( --> there is no task numbered 2.", invalid.message());
        assertEquals(1, tasks.size());
        assertFalse(tasks.getFirst().isDone());

        CommandResult marked = Bkxss.processCommandResult("mark 1", tasks, storage);
        assertFalse(marked.isError());
        assertTrue(tasks.getFirst().isDone());
        assertTrue(storage.load().getFirst().isDone());
        assertSame(originalOutput, System.out);
    }

    @Test
    void processCommandResult_errorWordsInDescription_doesNotReportError() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();

        CommandResult result = Bkxss.processCommandResult("todo OhNo!! ERROR :( --> investigate", tasks, storage);

        assertFalse(result.isError());
        assertEquals(1, tasks.size());
        assertFalse(Bkxss.processCommandResult("list", tasks, storage).isError());
    }

    @Test
    void processCommandResult_bye_restoresOutputAndKeepsTasks() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("borrow book"));
        PrintStream originalOutput = System.out;

        CommandResult result = Bkxss.processCommandResult("bye", tasks, storage);

        assertFalse(result.isError());
        assertEquals("Bye. Hope to see you again soon!", result.message());
        assertEquals(1, tasks.size());
        assertSame(originalOutput, System.out);
    }

    @Test
    void processCommand_plainTextClient_keepsResponseFormat() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("OhNo!! ERROR :( --> The description of a todo cannot be empty.",
                Bkxss.processCommand("todo", new ArrayList<>(), storage));
    }
}

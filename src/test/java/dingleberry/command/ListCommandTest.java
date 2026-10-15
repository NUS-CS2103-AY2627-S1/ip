package dingleberry.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dingleberry.model.TaskList;
import dingleberry.model.Todo;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Tests rendering the current task list. */
class ListCommandTest {
    /** Redirects storage writes to a temporary folder during tests. */
    @TempDir
    private Path tempDir;

    @Test
    void executeTasksArePrintedInNumberedOrder() throws Exception {
        TaskList tasks = new TaskList(new Todo("read lecture notes"),
                new Todo("submit assignment"));
        Storage storage = new Storage(tempDir.resolve("dingleberry.txt")
                .toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            new ListCommand().execute(tasks, new Ui(), storage);
        } finally {
            System.setOut(originalOutput);
        }

        String renderedTasks = output.toString();
        assertTrue(renderedTasks.contains("1.[T][ ] read lecture notes"));
        assertTrue(renderedTasks.contains("2.[T][ ] submit assignment"));
    }
}

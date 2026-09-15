package monkey.command;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import monkey.model.TaskList;
import monkey.model.ToDos;
import monkey.storage.Storage;
import monkey.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_markAndUnmarkValidTask_updatesStateAndStorage() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("read"));
        Path saveFile = temporaryDirectory.resolve("monkey.txt");
        Storage storage = new Storage(saveFile.toString());
        StringBuilder output = new StringBuilder();

        new MarkCommand("1", true).execute(tasks, createUi(output), storage);
        assertTrue(tasks.get(0).isDone());
        assertEquals("T | 1 | read", Files.readString(saveFile).trim());

        new MarkCommand("1", false).execute(tasks, createUi(output), storage);
        assertFalse(tasks.get(0).isDone());
        assertEquals("T | 0 | read", Files.readString(saveFile).trim());
        assertEquals("Nice! I've marked this task as done:\n  [T][X] read\n"
                + "OK, I've marked this task as not done yet:\n  [T][ ] read", output.toString());
    }

    @Test
    void execute_nonNumericMarkAndUnmark_reportSpecificErrors() {
        StringBuilder output = new StringBuilder();
        TaskList tasks = new TaskList();

        new MarkCommand("abc", true).execute(tasks, createUi(output), createStorage());
        new MarkCommand("abc", false).execute(tasks, createUi(output), createStorage());

        assertEquals("OOPS! Monkey says: That banana-shaped task number does not look right. "
                + "Use a number after 'mark'.\n"
                + "OOPS! Monkey says: This monkey needs a valid task number after 'unmark'.", output.toString());
    }

    @Test
    void execute_outOfRangeIndex_reportsError() {
        StringBuilder output = new StringBuilder();

        new MarkCommand("1", true).execute(new TaskList(), createUi(output), createStorage());

        assertEquals("That task number does not exist.", output.toString());
    }

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("monkey.txt").toString());
    }

    private static Ui createUi(StringBuilder output) {
        return new Ui(message -> {
            if (!output.isEmpty()) {
                output.append('\n');
            }
            output.append(message);
        });
    }
}

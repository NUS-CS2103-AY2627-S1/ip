package pdd.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pdd.PDDException;
import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.task.Todo;
import pdd.ui.Ui;

public class MarkStatusCommandTest {
    @TempDir
    Path tempDir;

    @Test
    public void execute_markCommand_marksTaskDoneShowsAndSaves() throws PDDException {
        List<String> lines = new ArrayList<>();
        Ui ui = new Ui(lines::add);
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(new ArrayList<>(List.of(todo)));
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        new MarkCommand("1").execute(tasks, ui, storage);

        assertEquals("[T][X] read book", todo.toString());
        assertEquals("Nice! I've marked this task as done:\n  [T][X] read book", String.join("\n", lines));
        assertEquals("[T][X] read book", storage.load().get(0).toString());
    }

    @Test
    public void execute_unmarkCommand_marksTaskNotDoneShowsAndSaves() throws PDDException {
        List<String> lines = new ArrayList<>();
        Ui ui = new Ui(lines::add);
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(new ArrayList<>(List.of(todo)));
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        new UnmarkCommand("1").execute(tasks, ui, storage);

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("OK, I've marked this task as not done yet:\n  [T][ ] read book", String.join("\n", lines));
        assertEquals("[T][ ] read book", storage.load().get(0).toString());
    }

    @Test
    public void execute_outOfRangeTaskNumber_throwsAndLeavesListUnchanged() {
        Ui ui = new Ui(line -> { });
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(new ArrayList<>(List.of(todo)));
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        assertThrows(PDDException.class, () -> new MarkCommand("5").execute(tasks, ui, storage));
        assertEquals("[T][ ] read book", todo.toString());
    }
}

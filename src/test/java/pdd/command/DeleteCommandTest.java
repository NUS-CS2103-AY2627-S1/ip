package pdd.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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

public class DeleteCommandTest {
    @TempDir
    Path tempDir;

    @Test
    public void execute_validIndex_removesTaskShowsAndSaves() throws PDDException {
        List<String> lines = new ArrayList<>();
        Ui ui = new Ui(lines::add);
        Todo first = new Todo("read book");
        Todo second = new Todo("return book");
        TaskList tasks = new TaskList(new ArrayList<>(List.of(first, second)));
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        new DeleteCommand("1").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
        assertEquals("Noted. I've removed this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
                String.join("\n", lines));
        assertEquals("[T][ ] return book", storage.load().get(0).toString());
    }

    @Test
    public void execute_outOfRangeIndex_throwsAndLeavesListUnchanged() {
        Ui ui = new Ui(line -> { });
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(new ArrayList<>(List.of(todo)));
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        assertThrows(PDDException.class, () -> new DeleteCommand("9").execute(tasks, ui, storage));
        assertEquals(1, tasks.size());
        assertSame(todo, tasks.get(0));
    }
}

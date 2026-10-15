package anders.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import anders.AndersException;
import anders.collection.TaskList;
import anders.parser.Parser;
import anders.storage.Storage;
import anders.task.Task;
import anders.task.Todo;
import anders.ui.Ui;

/** Tests command flags through parsing, task updates, and persistence. */
public class CommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_markAndUnmark_updatesAndPersistsSelectedTask() throws AndersException {
        Task firstTask = new Todo("read book");
        Task selectedTask = new Todo("finish report");
        TaskList tasks = new TaskList(List.of(firstTask, selectedTask));
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Ui ui = new Ui();

        Parser.parse("mark 2").execute(tasks, ui, storage);

        assertTrue(selectedTask.isDone());
        assertTrue(storage.load().get(1).isDone());
        assertFalse(firstTask.isDone());

        Parser.parse("unmark 2").execute(tasks, ui, storage);

        assertFalse(selectedTask.isDone());
        assertFalse(storage.load().get(1).isDone());
        assertFalse(firstTask.isDone());
    }

    @Test
    public void execute_tagAndUntag_updatesAndPersistsSelectedTask() throws AndersException {
        Task firstTask = new Todo("read book");
        Task selectedTask = new Todo("finish report");
        TaskList tasks = new TaskList(List.of(firstTask, selectedTask));
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Ui ui = new Ui();

        Parser.parse("tag 2 #School #fun").execute(tasks, ui, storage);

        assertEquals(List.of("#school", "#fun"), selectedTask.getTags());
        assertEquals(selectedTask.getTags(), storage.load().get(1).getTags());

        Parser.parse("tag 2 #SCHOOL").execute(tasks, ui, storage);
        assertEquals(List.of("#school", "#fun"), selectedTask.getTags());

        Parser.parse("untag 2 #FUN").execute(tasks, ui, storage);

        assertEquals(List.of("#school"), selectedTask.getTags());
        assertEquals(selectedTask.getTags(), storage.load().get(1).getTags());

        Parser.parse("untag 2 #missing").execute(tasks, ui, storage);

        assertEquals(List.of("#school"), selectedTask.getTags());
        assertEquals(selectedTask.getTags(), storage.load().get(1).getTags());
        assertTrue(firstTask.getTags().isEmpty());
        assertTrue(storage.load().get(0).getTags().isEmpty());
    }
}

package ev.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ev.EvException;
import ev.storage.Storage;
import ev.task.TaskList;
import ev.task.Todo;
import ev.ui.Ui;

public class FindCommandTest {

    @TempDir
    Path workFolder;

    private TaskList threeTasks() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("water plants"));
        tasks.add(new Todo("return book"));
        return tasks;
    }

    @Test
    public void execute_matchingKeyword_matchesKeepTheirNumbers() throws EvException {
        Ui ui = new Ui();
        new FindCommand("book").execute(threeTasks(), ui, new Storage(workFolder.resolve("ev.txt")));
        assertEquals("Matches:\n1.[T][ ] read book\n3.[T][ ] return book", ui.takeResponse());
    }

    @Test
    public void execute_differentCase_stillMatches() throws EvException {
        Ui ui = new Ui();
        new FindCommand("BOOK").execute(threeTasks(), ui, new Storage(workFolder.resolve("ev.txt")));
        assertEquals("Matches:\n1.[T][ ] read book\n3.[T][ ] return book", ui.takeResponse());
    }

    @Test
    public void execute_noMatch_saysSo() throws EvException {
        Ui ui = new Ui();
        new FindCommand("homework").execute(threeTasks(), ui, new Storage(workFolder.resolve("ev.txt")));
        assertEquals("No match for \"homework\".", ui.takeResponse());
    }
}

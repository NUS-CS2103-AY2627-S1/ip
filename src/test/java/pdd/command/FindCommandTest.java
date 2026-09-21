package pdd.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.task.Todo;
import pdd.ui.Ui;

public class FindCommandTest {
    @TempDir
    Path tempDir;

    @Test
    public void execute_keywordMatchesSomeTasks_showsOnlyMatches() {
        List<String> lines = new ArrayList<>();
        Ui ui = new Ui(lines::add);
        TaskList tasks = new TaskList(new ArrayList<>(
                List.of(new Todo("read book"), new Todo("join sports club"))));
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        new FindCommand("book").execute(tasks, ui, storage);

        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book", String.join("\n", lines));
    }

    @Test
    public void execute_keywordMatchesNothing_showsHeaderOnly() {
        List<String> lines = new ArrayList<>();
        Ui ui = new Ui(lines::add);
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("read book"))));
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        new FindCommand("xyz").execute(tasks, ui, storage);

        assertEquals("Here are the matching tasks in your list:", String.join("\n", lines));
    }
}

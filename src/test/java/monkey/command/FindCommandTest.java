package monkey.command;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import monkey.model.TaskList;
import monkey.model.ToDos;
import monkey.storage.Storage;
import monkey.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_emptyKeyword_rejectsInput() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("buy milk"));
        StringBuilder output = new StringBuilder();

        new FindCommand("").execute(tasks, new Ui(output::append),
                new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("OOPS! Monkey says: A find command needs a keyword.", output.toString());
    }

    @Test
    void execute_matchesKeywordCaseInsensitivelyAndKeepsOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("read book"));
        tasks.add(new ToDos("buy milk"));
        tasks.add(new ToDos("return BOOK"));

        new FindCommand("book").execute(tasks, new Ui(),
                new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals(3, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("buy milk", tasks.get(1).getDescription());
    }

    @Test
    void execute_noMatchingTasks_displaysHeaderWithoutEntries() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("buy milk"));
        StringBuilder output = new StringBuilder();

        new FindCommand("book").execute(tasks, new Ui(message -> output.append(message).append('\n')),
                new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("Here are the matching tasks in your list:\n", output.toString());
    }
}

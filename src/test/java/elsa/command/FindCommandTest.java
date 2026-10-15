package elsa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.task.Todo;
import elsa.ui.Ui;

/** Tests {@link FindCommand}, which shows the tasks a keyword appears in. */
public class FindCommandTest {

    private static TaskList threeTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        tasks.add(new Todo("return book"));
        return tasks;
    }

    private static Storage storeIn(Path folder) {
        return new Storage(folder.resolve("tasks.txt").toString());
    }

    /** A found task keeps its number from the full list, so it can be marked straight away. */
    @Test
    public void execute_aKeywordInTwoTasks_showsBothWithTheirOwnNumbers(@TempDir Path folder)
            throws ElsaException {
        assertEquals("Here are the matching tasks in your list:\n"
                        + "1.[T][ ] read book\n"
                        + "3.[T][ ] return book",
                new FindCommand("book").execute(threeTasks(), new Ui(), storeIn(folder)));
    }

    /** Several keywords find a task matching any of them, not only all of them. */
    @Test
    public void execute_severalKeywords_showsTasksMatchingAnyOfThem(@TempDir Path folder)
            throws ElsaException {
        assertEquals("Here are the matching tasks in your list:\n"
                        + "1.[T][ ] read book\n"
                        + "2.[T][ ] buy milk\n"
                        + "3.[T][ ] return book",
                new FindCommand("book", "milk").execute(threeTasks(), new Ui(), storeIn(folder)));
    }

    @Test
    public void execute_aKeywordInNothing_namesWhatWasLookedFor(@TempDir Path folder)
            throws ElsaException {
        assertEquals("Nothing matching \"socks\".",
                new FindCommand("socks").execute(threeTasks(), new Ui(), storeIn(folder)));
    }

    @Test
    public void execute_always_leavesTheStoreAlone(@TempDir Path folder) throws ElsaException {
        Path file = folder.resolve("tasks.txt");

        new FindCommand("book").execute(threeTasks(), new Ui(), new Storage(file.toString()));

        assertFalse(Files.exists(file), "finding wrote to the disk");
    }
}

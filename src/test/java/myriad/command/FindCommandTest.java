package myriad.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.task.ToDo;
import myriad.ui.Ui;

/**
 * Tests for {@link FindCommand#execute}: that it lists the tasks whose
 * descriptions contain the keyword, numbered afresh from 1, or says that
 * nothing matched. How a description is matched is tested in TaskTest; here
 * it only matters that the command shows that selection and changes nothing.
 */
public class FindCommandTest {

    /** What a recorded reply puts between its lines, on every platform. */
    private static final String NEWLINE = "\n";

    /** JUnit creates this per test and deletes it afterwards. */
    @TempDir
    private Path tempDir;

    /** Builds a list of not-done ToDos with the given descriptions, in order. */
    private static TaskList taskListOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new ToDo(description));
        }
        return tasks;
    }

    /** Runs a find command for {@code keyword} on {@code tasks} and returns the reply. */
    private String findAndGetReply(TaskList tasks, String keyword) {
        Ui ui = new Ui(false);
        new FindCommand(keyword).execute(tasks, ui, new Storage(tempDir.resolve("myriad.txt").toString()));
        return ui.getResponse();
    }

    @Test
    public void execute_matchingTasks_listedInOrderAndRenumbered() {
        assertEquals("Strokes matching your search:" + NEWLINE
                + "1.[T][ ] read book" + NEWLINE
                + "2.[T][ ] return Book",
                findAndGetReply(taskListOf("read book", "join club", "return Book"), "book"));
    }

    @Test
    public void execute_noMatchingTasks_noneFoundMessageQuotesKeyword() {
        assertEquals("No strokes match \"exam\".",
                findAndGetReply(taskListOf("read book", "join club"), "exam"));
    }

    @Test
    public void execute_emptyList_noneFoundMessage() {
        assertEquals("No strokes match \"book\".", findAndGetReply(new TaskList(), "book"));
    }

    @Test
    public void execute_anyKeyword_listUnchangedAndNothingSaved() {
        TaskList tasks = taskListOf("read book", "join club");
        findAndGetReply(tasks, "book");

        assertEquals(2, tasks.size());
        assertFalse(Files.exists(tempDir.resolve("myriad.txt")));
    }

    @Test
    public void isExit_findCommand_false() {
        assertFalse(new FindCommand("book").isExit());
    }
}

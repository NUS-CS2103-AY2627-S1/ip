package zhangwei.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zhangwei.ZhangWeiException;
import zhangwei.storage.Storage;
import zhangwei.task.Deadline;
import zhangwei.task.SortCriterion;
import zhangwei.task.TaskList;
import zhangwei.task.Todo;
import zhangwei.ui.Ui;

/**
 * Tests {@link SortCommand}. Sorting keeps its new order rather than showing
 * it once, so these tests check the list itself and the save file, not only
 * what was printed.
 */
public class SortCommandTest {

    // JUnit injects a fresh temporary folder here; it must not be private.
    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;
    private Path saveFile;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        saveFile = tempDir.resolve("tasks.txt");
        storage = new Storage(saveFile.toString());
    }

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOut);
    }

    /** Returns a list of three tasks that no two criteria order the same way. */
    private TaskList mixedTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("zebra"));
        tasks.add(new Deadline("apple", LocalDate.of(2019, 12, 2)));
        tasks.add(new Deadline("mango", LocalDate.of(2018, 1, 1)));
        return tasks;
    }

    @Test
    public void execute_sortByDate_listReorderedEarliestFirst() throws ZhangWeiException {
        TaskList tasks = mixedTasks();

        new SortCommand(SortCriterion.DATE).execute(tasks, new Ui(), storage);

        assertEquals("mango", tasks.get(1).getDescription());
        assertEquals("apple", tasks.get(2).getDescription());
        assertEquals("zebra", tasks.get(3).getDescription());
    }

    @Test
    public void execute_sortByDescription_listReorderedAlphabetically()
            throws ZhangWeiException {
        TaskList tasks = mixedTasks();

        new SortCommand(SortCriterion.DESCRIPTION).execute(tasks, new Ui(), storage);

        assertEquals("apple", tasks.get(1).getDescription());
        assertEquals("mango", tasks.get(2).getDescription());
        assertEquals("zebra", tasks.get(3).getDescription());
    }

    @Test
    public void execute_anySort_confirmationAndListShown() throws ZhangWeiException {
        new SortCommand(SortCriterion.DATE).execute(mixedTasks(), new Ui(), storage);

        String output = captured.toString();
        assertTrue(output.contains("Sorted your tasks by date, neat as a row of bamboo."));
        assertTrue(output.contains("Here are the tasks growing in your grove:"));
        assertTrue(output.contains("1.[D][ ] mango (by: Jan 1 2018)"));
    }

    @Test
    public void execute_anySort_newOrderSaved() throws Exception {
        new SortCommand(SortCriterion.DESCRIPTION).execute(mixedTasks(), new Ui(), storage);

        List<String> lines = Files.readAllLines(saveFile);
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).contains("apple"));
        assertTrue(lines.get(1).contains("mango"));
        assertTrue(lines.get(2).contains("zebra"));
    }

    @Test
    public void execute_emptyList_nothingToSortMessageShown() throws ZhangWeiException {
        TaskList tasks = new TaskList();

        new SortCommand(SortCriterion.DATE).execute(tasks, new Ui(), storage);

        String output = captured.toString();
        assertTrue(output.contains("You have no tasks to sort."));
        assertFalse(output.contains("Here are the tasks growing in your grove:"));
    }

    @Test
    public void isExit_sortCommand_falseReturned() {
        assertFalse(new SortCommand(SortCriterion.DATE).isExit());
    }
}

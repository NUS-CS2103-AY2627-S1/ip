package myriad.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import myriad.MyriadException;
import myriad.storage.Storage;
import myriad.task.Deadline;
import myriad.task.Event;
import myriad.task.TaskDateTime;
import myriad.task.TaskList;
import myriad.task.ToDo;
import myriad.ui.Ui;

/**
 * Tests for {@link ShowCommand#execute}: that it lists the tasks taking
 * place at the queried date/time, numbered afresh from 1, or says that there
 * are none. Which tasks count as taking place is tested in TaskListTest,
 * DeadlineTest and EventTest; here it only matters that the command shows
 * that selection and changes nothing.
 */
public class ShowCommandTest {

    /** What a recorded reply puts between its lines, on every platform. */
    private static final String NEWLINE = "\n";

    /** JUnit creates this per test and deletes it afterwards. */
    @TempDir
    private Path tempDir;

    /** Builds a TaskDateTime, unwrapping the checked exception for brevity. */
    private static TaskDateTime at(String raw) {
        try {
            return TaskDateTime.parse(raw);
        } catch (MyriadException e) {
            throw new AssertionError("test fixture should parse: " + raw, e);
        }
    }

    /** A list holding a to-do, a deadline on 2 Dec and an event on 5 Dec 2019. */
    private static TaskList mixedTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", at("2019-12-02 1800")));
        tasks.add(new Event("party", at("2019-12-05 1400"), at("2019-12-05 1600")));
        return tasks;
    }

    /** Runs a show command for {@code query} on {@code tasks} and returns the reply. */
    private String showAndGetReply(TaskList tasks, String query) {
        Ui ui = new Ui(false);
        new ShowCommand(at(query)).execute(tasks, ui, new Storage(tempDir.resolve("myriad.txt").toString()));
        return ui.getResponse();
    }

    @Test
    public void execute_matchingTasks_listedAndRenumbered() {
        // The event is task 3 in the list but the only match, so it is shown as 1.
        assertEquals("Painted on Dec 05 2019:" + NEWLINE
                + "1.[E][ ] party (from: Dec 05 2019 1400 to: Dec 05 2019 1600)",
                showAndGetReply(mixedTasks(), "2019-12-05"));
    }

    @Test
    public void execute_noMatchingTasks_noneFoundMessage() {
        assertEquals("Nothing is painted on Dec 03 2019 yet.", showAndGetReply(mixedTasks(), "2019-12-03"));
    }

    @Test
    public void execute_emptyList_noneFoundMessage() {
        assertEquals("Nothing is painted on Dec 02 2019 1800 yet.",
                showAndGetReply(new TaskList(), "2019-12-02 1800"));
    }

    @Test
    public void execute_anyQuery_listUnchangedAndNothingSaved() {
        TaskList tasks = mixedTasks();
        showAndGetReply(tasks, "2019-12-02");

        assertEquals(3, tasks.size());
        assertFalse(Files.exists(tempDir.resolve("myriad.txt")));
    }

    @Test
    public void isExit_showCommand_false() {
        assertFalse(new ShowCommand(at("2019-12-02")).isExit());
    }
}

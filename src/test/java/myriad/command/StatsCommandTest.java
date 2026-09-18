package myriad.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

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
 * Tests for {@link StatsCommand#execute}, which gathers three separate
 * TaskList queries into one report. The queries themselves are tested in
 * TaskListTest; what is tested here is that the command asks the right
 * questions (the right window, the right limit) and that the report puts
 * each answer in the right section.
 * <p>
 * Every command is built with a fixed date, so the "due soon" window does
 * not move depending on the day the suite is run.
 */
public class StatsCommandTest {

    /** The date every command here reports as of. */
    private static final LocalDate TODAY = LocalDate.parse("2019-12-02");

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

    /** Path to a data file inside the temp directory, not yet created. */
    private Path dataFile() {
        return tempDir.resolve("myriad.txt");
    }

    /** Runs a stats command dated TODAY on tasks, and returns the recorded reply. */
    private String runStats(TaskList tasks) {
        Ui ui = new Ui(false);
        new StatsCommand(TODAY).execute(tasks, ui, new Storage(dataFile().toString()));
        return ui.getResponse();
    }

    @Test
    public void execute_emptyList_everySectionEmpty() {
        assertEquals("Stepping back to admire your canvas:" + NEWLINE
                + "Total tasks: 0 (ToDo: 0, Deadline: 0, Event: 0)" + NEWLINE
                + "Due in the next 7 days: none" + NEWLINE
                + "Oldest not done: none",
                runStats(new TaskList()));
    }

    @Test
    public void execute_mixedList_fullReportShown() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", at("2019-12-05")));
        tasks.add(new Event("conference", at("2020-01-10"), at("2020-01-12")));

        assertEquals("Stepping back to admire your canvas:" + NEWLINE
                + "Total tasks: 3 (ToDo: 1, Deadline: 1, Event: 1)" + NEWLINE
                + "Due in the next 7 days:" + NEWLINE
                + "1.[D][ ] return book (by: Dec 05 2019)" + NEWLINE
                + "Oldest not done:" + NEWLINE
                + "1.[T][ ] read book" + NEWLINE
                + "2.[D][ ] return book (by: Dec 05 2019)" + NEWLINE
                + "3.[E][ ] conference (from: Jan 10 2020 to: Jan 12 2020)",
                runStats(tasks));
    }

    @Test
    public void execute_deadlinesAroundWindowEnd_onlyThoseInsideWindowDueSoon() {
        // The window is measured from the date given to the constructor, and
        // runs to the end of its seventh day.
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("last day", at("2019-12-09 2359")));
        tasks.add(new Deadline("day after", at("2019-12-10")));

        String response = runStats(tasks);

        assertTrue(response.contains("Due in the next 7 days:" + NEWLINE
                + "1.[D][ ] last day (by: Dec 09 2019 2359)" + NEWLINE
                + "Oldest not done:"), response);
    }

    @Test
    public void execute_sixUndoneTasks_onlyOldestFiveListed() {
        TaskList tasks = new TaskList();
        for (int i = 1; i <= 6; i++) {
            tasks.add(new ToDo("task " + i));
        }

        String response = runStats(tasks);

        assertTrue(response.endsWith("5.[T][ ] task 5"), response);
        assertFalse(response.contains("task 6"), response);
    }

    @Test
    public void execute_doneTask_countedButNotListedAsUndone() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.markDone(0);

        String response = runStats(tasks);

        assertTrue(response.contains("Total tasks: 1 (ToDo: 1, Deadline: 0, Event: 0)"), response);
        assertTrue(response.endsWith("Oldest not done: none"), response);
    }

    @Test
    public void execute_anyList_dataFileNotWritten() {
        // Stats only reads the list, so it must never trigger a save.
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        runStats(tasks);

        assertFalse(Files.exists(dataFile()));
    }
}

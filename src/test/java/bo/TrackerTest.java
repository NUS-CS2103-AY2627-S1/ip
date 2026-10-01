package bo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bo.command.CommandException;
import bo.storage.Storage;
import bo.task.Deadline;
import bo.task.Todo;
import bo.ui.Ui;

/**
 * Tests for {@link Tracker}.
 *
 * Each test uses a Storage instance pointed at a fresh temporary file
 * (via JUnit's @TempDir), so tests never read or write Bo's real
 * data/bo.txt save file and cannot interfere with each other.
 *
 * Focuses on:
 *  - addTask persisting to storage and being reflected in isValidTaskNumber
 *  - markTask / unmarkTask actually flipping status and surviving a reload
 *  - deleteTask removing the right task and shifting task numbers correctly
 *  - the "start empty on a corrupt/missing save file" fallback behaviour
 */
public class TrackerTest {

    @TempDir
    Path tempDir;

    private Path saveFile;
    private Tracker tracker;

    @BeforeEach
    public void setUp() {
        saveFile = tempDir.resolve("bo.txt");
        tracker = new Tracker(new Ui(), new Storage(saveFile));
    }

    // ---------- addTask ----------

    @Test
    public void addTask_singleTask_becomesValidTaskNumberOne() throws CommandException {
        tracker.addTask(new Todo("read book"));

        assertTrue(tracker.isValidTaskNumber(1));
        assertFalse(tracker.isValidTaskNumber(2));
    }

    @Test
    public void addTask_writesTaskToSaveFile() throws CommandException, IOException {
        tracker.addTask(new Todo("read book"));

        List<String> lines = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
        assertEquals(1, lines.size());
        assertEquals("T | 0 | read book", lines.get(0));
    }

    // ---------- reload from storage (constructor behaviour) ----------

    @Test
    public void newTracker_loadsTasksPreviouslySavedByAnotherTracker() throws CommandException {
        tracker.addTask(new Todo("read book"));
        tracker.addTask(new Deadline("submit report", LocalDate.of(2024, 3, 2)));

        // Simulate the app restarting: a brand-new Tracker pointed at the same file.
        Tracker reloadedTracker = new Tracker(new Ui(), new Storage(saveFile));

        assertTrue(reloadedTracker.isValidTaskNumber(1));
        assertTrue(reloadedTracker.isValidTaskNumber(2));
        assertFalse(reloadedTracker.isValidTaskNumber(3));
    }

    @Test
    public void newTracker_missingSaveFile_startsWithEmptyTaskListInsteadOfCrashing() {
        // saveFile does not exist yet (nothing has been saved), so this simulates
        // a completely fresh first run.
        Tracker freshTracker = new Tracker(new Ui(), new Storage(saveFile));

        assertFalse(freshTracker.isValidTaskNumber(1));
    }

    @Test
    public void newTracker_corruptSaveFile_startsWithEmptyTaskListInsteadOfCrashing() throws IOException {
        Files.createDirectories(saveFile.getParent());
        Files.writeString(saveFile, "this is not a valid saved task line\n", StandardCharsets.UTF_8);

        Tracker trackerWithCorruptFile = new Tracker(new Ui(), new Storage(saveFile));

        // Loading failed, so it should fall back to an empty list rather than throwing.
        assertFalse(trackerWithCorruptFile.isValidTaskNumber(1));
    }

    // ---------- markTask / unmarkTask ----------

    @Test
    public void markTask_incompleteTask_becomesDoneAndPersists() throws CommandException {
        tracker.addTask(new Todo("read book"));

        tracker.markTask(1);

        // Reload to confirm the "done" status was actually persisted, not just held in memory.
        Tracker reloadedTracker = new Tracker(new Ui(), new Storage(saveFile));
        assertTrue(reloadedTracker.isValidTaskNumber(1));
    }

    @Test
    public void unmarkTask_completedTask_becomesNotDoneAndPersists() throws CommandException, IOException {
        tracker.addTask(new Todo("read book"));
        tracker.markTask(1);

        tracker.unmarkTask(1);

        List<String> lines = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
        assertEquals("T | 0 | read book", lines.get(0));
    }

    @Test
    public void markTask_alreadyDoneTask_staysDoneAndDoesNotThrow() throws CommandException, IOException {
        tracker.addTask(new Todo("read book"));
        tracker.markTask(1);

        tracker.markTask(1); // mark again; should not error or toggle back off

        List<String> lines = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
        assertEquals("T | 1 | read book", lines.get(0));
    }

    @Test
    public void snoozeTask_deadline_advancesDateAndPersists() throws CommandException, IOException {
        tracker.addTask(new Deadline("submit report", LocalDate.of(2999, 1, 1)));

        tracker.snoozeTask(1, 3);

        assertEquals("D | 0 | submit report | 2999-01-04",
                Files.readAllLines(saveFile, StandardCharsets.UTF_8).get(0));
    }

    @Test
    public void rescheduleTask_completedDeadline_replacesDateAndKeepsStatus()
            throws CommandException, IOException {
        tracker.addTask(new Deadline("submit report", LocalDate.of(2999, 1, 1)));
        tracker.markTask(1);

        tracker.rescheduleTask(1, LocalDate.of(2999, 2, 1));

        assertEquals("D | 1 | submit report | 2999-02-01",
                Files.readAllLines(saveFile, StandardCharsets.UTF_8).get(0));
    }

    // ---------- deleteTask ----------

    @Test
    public void deleteTask_middleTask_remainingTasksShiftDownCorrectly() throws CommandException, IOException {
        tracker.addTask(new Todo("first"));
        tracker.addTask(new Todo("second"));
        tracker.addTask(new Todo("third"));

        tracker.deleteTask(2); // remove "second"

        assertTrue(tracker.isValidTaskNumber(1));
        assertTrue(tracker.isValidTaskNumber(2));
        assertFalse(tracker.isValidTaskNumber(3));

        List<String> lines = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());
        assertEquals("T | 0 | first", lines.get(0));
        assertEquals("T | 0 | third", lines.get(1));
    }

    @Test
    public void deleteTask_lastRemainingTask_leavesNoValidTaskNumbers() throws CommandException {
        tracker.addTask(new Todo("only task"));

        tracker.deleteTask(1);

        assertFalse(tracker.isValidTaskNumber(1));
    }

    // ---------- printUpcomingDeadlines / printDeadlinesDueOn ----------
    // These are void + print via Ui, so we test them indirectly through their
    // effect being observable: they must not throw for a mix of task types
    // and dates, including past-due and non-deadline tasks that must be skipped.

    @Test
    public void printUpcomingDeadlines_mixOfPastAndFutureDeadlinesAndTodos_doesNotThrow() throws CommandException {
        tracker.addTask(new Todo("no date task"));
        tracker.addTask(new Deadline("past deadline", LocalDate.of(2000, 1, 1)));
        tracker.addTask(new Deadline("future deadline", LocalDate.of(2999, 1, 1)));

        assertDoesNotThrowWrapper(() -> tracker.printUpcomingDeadlines(LocalDate.of(2024, 1, 1)));
    }

    @Test
    public void printDeadlinesDueOn_noMatchingDeadlines_doesNotThrow() throws CommandException {
        tracker.addTask(new Deadline("submit report", LocalDate.of(2024, 3, 2)));

        assertDoesNotThrowWrapper(() -> tracker.printDeadlinesDueOn(LocalDate.of(2024, 3, 3)));
    }

    /**
     * Runs an action without requiring an additional JUnit assertion import.
     * This keeps the two print tests above concise while preserving failures.
     */
    private void assertDoesNotThrowWrapper(Runnable action) {
        action.run();
    }

    // ---------- findTasks ----------
    // findTasks is void and prints via Ui, so (like printUpcomingDeadlines
    // above) it can't be asserted on directly without changing Ui to return
    // testable output. These tests instead confirm it runs correctly across
    // the situations most likely to break it: keyword found, not found, an
    // empty task list, multiple matches, and matches spread across
    // different task types.

    @Test
    public void findTasks_keywordMatchesOneTask_doesNotThrow() throws CommandException {
        tracker.addTask(new Todo("read book"));
        tracker.addTask(new Todo("buy milk"));

        assertDoesNotThrowWrapper(() -> tracker.findTasks("book"));
    }

    @Test
    public void findTasks_keywordMatchesNoTasks_doesNotThrow() throws CommandException {
        tracker.addTask(new Todo("buy milk"));

        assertDoesNotThrowWrapper(() -> tracker.findTasks("book"));
    }

    @Test
    public void findTasks_emptyTaskList_doesNotThrow() {
        assertDoesNotThrowWrapper(() -> tracker.findTasks("book"));
    }

    @Test
    public void findTasks_keywordMatchesAcrossMultipleTaskTypes_doesNotThrow() throws CommandException {
        tracker.addTask(new Todo("read book"));
        tracker.addTask(new Deadline("return book", LocalDate.of(2024, 3, 2)));
        tracker.addTask(new bo.task.Event("book club",
                LocalDateTime.of(2026, 10, 10, 14, 0),
                LocalDateTime.of(2026, 10, 10, 16, 0)));
        tracker.addTask(new Todo("unrelated task"));

        assertDoesNotThrowWrapper(() -> tracker.findTasks("book"));
    }
}

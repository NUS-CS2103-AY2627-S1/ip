package echo.task;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import echo.exception.InvalidTaskNumberException;



/**
 * Tests {@link TaskList} operations: adding, retrieving, deleting, and
 * listing tasks, including out-of-bounds handling.
 */
public class TaskListTest {

    /**
     * Minimal concrete {@link Task} so these tests exercise {@link TaskList}
     * without depending on Todo/Deadline/Event subclasses.
     */
    private static class TaskStub extends Task {
        public TaskStub(String description) {
            super(description);
        }

        @Override
        public TaskType getTaskType() {
            return null;
        }
    }

    @Test
    public void add_singleTask_sizeIncreases() {
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("read book"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void getTask_validIndex_returnsTaskAtThatPosition() throws InvalidTaskNumberException {
        TaskList tasks = new TaskList();
        Task first = new TaskStub("thing 1");
        Task second = new TaskStub("thing 2");
        tasks.add(first);
        tasks.add(second);

        assertEquals(second, tasks.getTask(1));
    }

    @Test
    public void getTask_negativeIndex_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("only"));

        assertThrows(InvalidTaskNumberException.class, () -> tasks.getTask(-1));
    }

    @Test
    public void getTask_indexEqualToSize_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("only"));

        assertThrows(InvalidTaskNumberException.class, () -> tasks.getTask(1));
    }

    @Test
    public void getTask_emptyList_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(InvalidTaskNumberException.class, () -> tasks.getTask(0));
    }

    @Test
    public void delete_validIndex_returnsRemovedTaskAndShrinksList() throws InvalidTaskNumberException {
        TaskList tasks = new TaskList();
        Task toRemove = new TaskStub("borrow book");
        tasks.add(new TaskStub("return book"));
        tasks.add(toRemove);

        Task removed = tasks.delete(1);

        assertEquals(toRemove, removed);
        assertEquals(1, tasks.size());
    }

    @Test
    public void delete_invalidIndex_listUnchanged() throws InvalidTaskNumberException {
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("only"));

        assertThrows(InvalidTaskNumberException.class, () -> tasks.delete(5));
        assertEquals(1, tasks.size());
        assertEquals("[ ] only", tasks.getTask(0).toString());
    }

    @Test
    public void formatAsListText_multipleTasks_numberedInInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("alpha"));
        tasks.add(new TaskStub("beta"));

        String expected = "Current mission manifest:\n"
                + "1.[ ] alpha\n"
                + "2.[ ] beta";
        assertEquals(expected, tasks.listAsText());
    }

    @Test
    public void size_newList_returnsZero() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    public void getAll_returnsTasksInInsertionOrder() {
        TaskList tasks = new TaskList();
        Task first = new TaskStub("alpha");
        Task second = new TaskStub("beta");
        tasks.add(first);
        tasks.add(second);

        List<Task> all = tasks.getAll();
        assertEquals(2, all.size());
        assertEquals(first, all.get(0));
        assertEquals(second, all.get(1));
    }

    @Test
    public void getAll_afterLaterAddition_reflectsNewTask() {
        // The view remains current while preventing callers from changing
        // the list structure directly.
        TaskList tasks = new TaskList();
        List<Task> view = tasks.getAll();

        tasks.add(new TaskStub("late arrival"));

        assertEquals(1, view.size());
    }

    @Test
    public void getAll_attemptToModifyList_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("only"));

        List<Task> view = tasks.getAll();

        assertThrows(UnsupportedOperationException.class, () -> view.add(new TaskStub("bypassed API")));
        assertEquals(1, tasks.size());
    }

    @Test
    public void addAll_severalTasks_appendedAfterExistingInOrder()
            throws InvalidTaskNumberException {
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("existing"));
        Task loaded1 = new TaskStub("loaded 1");
        Task loaded2 = new TaskStub("loaded 2");

        tasks.addAll(List.of(loaded1, loaded2));

        assertEquals(3, tasks.size());
        assertEquals(loaded1, tasks.getTask(1));
        assertEquals(loaded2, tasks.getTask(2));
    }

    @Test
    public void addAll_emptyList_listUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("only"));

        tasks.addAll(List.of());

        assertEquals(1, tasks.size());
    }

    @Test
    public void formatAsListText_emptyList_returnsHeaderWithTrailingNewline() {
        // Header ends with '\n'; entry separators go only between entries,
        // so an empty list keeps the header's own trailing newline.
        TaskList tasks = new TaskList();

        assertEquals("The mission manifest is clear.", tasks.listAsText());
    }

    @Test
    public void formatAsListText_doneTask_showsDoneMarker() {
        TaskList tasks = new TaskList();
        Task done = new TaskStub("finished");
        done.markDone();
        tasks.add(done);

        assertEquals("Current mission manifest:\n1.[X] finished",
                tasks.listAsText());
    }

    @Test
    public void searchListText_multipleMatches_showsFullListNumbersBetweenLines() {
        // Numbers refer to positions in the full list (not renumbered) so
        // they stay valid for mark/unmark/delete.
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("read book"));
        tasks.add(new TaskStub("water plants"));
        tasks.add(new TaskStub("return book"));

        String expected = "Radar found these matching missions:\n"
                + "1.[ ] read book\n"
                + "3.[ ] return book";
        assertEquals(expected, tasks.searchListText("book"));
    }

    @Test
    public void searchListText_noMatch_returnsHeaderOnly() {
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("read book"));

        assertEquals("Nothing appeared on the radar for 'zebra'.",
                tasks.searchListText("zebra"));
    }

    @Test
    public void searchListText_nonMatchingLastTask_hasNoTrailingNewline() {
        TaskList tasks = new TaskList();
        tasks.add(new TaskStub("read book"));
        tasks.add(new TaskStub("water plants"));

        assertFalse(tasks.searchListText("book").endsWith("\n"));
    }

    @Test
    public void getStatistics_mixedTasks_returnsGlobalAndWeeklyCounts() {
        TaskList tasks = new TaskList();
        Todo completedTodo = new Todo("read book");
        completedTodo.markDone(LocalDateTime.of(2026, 9, 7, 9, 30));
        Deadline completedDeadline = new Deadline("return book", LocalDateTime.of(2026, 9, 8, 18, 0));
        completedDeadline.markDone(LocalDateTime.of(2026, 8, 31, 23, 59));
        Event completedEvent = new Event("meeting", LocalDateTime.of(2026, 9, 3, 10, 0),
                LocalDateTime.of(2026, 9, 3, 11, 0));
        completedEvent.markDone(LocalDateTime.of(2026, 9, 7, 12, 0));
        tasks.add(completedTodo);
        tasks.add(completedDeadline);
        tasks.add(completedEvent);
        tasks.add(new Todo("buy groceries"));

        String expected = "Mission progress report:\n\n"
                + "All missions:\n"
                + "Total missions: 4\n"
                + "Completed: 3\n"
                + "Active: 1\n"
                + "Todos completed: 1\n"
                + "Events completed: 1\n"
                + "Deadlines completed: 1\n\n"
                + "This week:\n"
                + "Completed: 2\n"
                + "Todos completed: 1\n"
                + "Events completed: 1\n"
                + "Deadlines completed: 0";

        assertEquals(expected, tasks.getStatistics(LocalDateTime.of(2026, 9, 7, 12, 0)));
    }

    @Test
    public void getStatistics_completionAtMondayStart_countsInCurrentWeek() {
        TaskList tasks = new TaskList();
        Todo boundaryTask = new Todo("at boundary");
        boundaryTask.markDone(LocalDateTime.of(2026, 9, 7, 0, 0));
        Todo earlierTask = new Todo("before boundary");
        earlierTask.markDone(LocalDateTime.of(2026, 9, 6, 23, 59));
        tasks.add(boundaryTask);
        tasks.add(earlierTask);

        assertTrue(tasks.getStatistics(LocalDateTime.of(2026, 9, 7, 0, 0))
                .contains("Completed: 1\nTodos completed: 1"));
    }
}

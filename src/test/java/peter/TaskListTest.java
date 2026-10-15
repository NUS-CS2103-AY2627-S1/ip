package peter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskList}.
 */
public class TaskListTest {
    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
    }

    @Test
    public void add_singleTask_increasesSize() throws PeterException {
        taskList.add(new Todo("read book"));
        assertEquals(1, taskList.size());
    }

    @Test
    public void add_exactDuplicateTask_throwsPeterExceptionAndLeavesListUnchanged() throws PeterException {
        taskList.add(new Todo("read book"));

        assertThrows(PeterException.class, () -> taskList.add(new Todo("read book")));
        assertEquals(1, taskList.size());
    }

    @Test
    public void add_duplicateAfterMarkingDone_stillThrowsPeterException() throws PeterException {
        // Completion state should not affect what counts as a duplicate.
        taskList.add(new Todo("read book"));
        taskList.mark(0);

        assertThrows(PeterException.class, () -> taskList.add(new Todo("read book")));
    }

    @Test
    public void add_sameDescriptionDifferentTaskType_doesNotThrow() throws PeterException {
        taskList.add(new Todo("submit report"));

        taskList.add(new Deadline("submit report", LocalDateTime.of(2019, 12, 2, 18, 0)));

        assertEquals(2, taskList.size());
    }

    @Test
    public void get_validIndex_returnsCorrectTask() throws PeterException {
        Todo todo = new Todo("read book");
        taskList.add(todo);
        assertEquals(todo, taskList.get(0));
    }

    @Test
    public void get_negativeIndex_throwsPeterException() throws PeterException {
        taskList.add(new Todo("read book"));
        assertThrows(PeterException.class, () -> taskList.get(-1));
    }

    @Test
    public void get_indexEqualToSize_throwsPeterException() throws PeterException {
        taskList.add(new Todo("read book"));
        // valid indices are 0..size-1, so index == size is out of range
        assertThrows(PeterException.class, () -> taskList.get(1));
    }

    @Test
    public void get_emptyList_throwsPeterException() {
        assertThrows(PeterException.class, () -> taskList.get(0));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() throws PeterException {
        Todo todo = new Todo("read book");
        taskList.add(todo);
        taskList.add(new Todo("return book"));

        Task removed = taskList.delete(0);

        assertEquals(todo, removed);
        assertEquals(1, taskList.size());
        assertEquals("return book", taskList.get(0).getDescription());
    }

    @Test
    public void delete_invalidIndex_throwsPeterExceptionAndLeavesListUnchanged() throws PeterException {
        taskList.add(new Todo("read book"));

        assertThrows(PeterException.class, () -> taskList.delete(5));
        // list should be untouched since the delete never happened
        assertEquals(1, taskList.size());
    }

    @Test
    public void mark_validIndex_marksTaskDone() throws PeterException {
        taskList.add(new Todo("read book"));

        Task marked = taskList.mark(0);

        assertTrue(marked.toString().startsWith("[T][X]"));
    }

    @Test
    public void unmark_validIndex_marksTaskNotDone() throws PeterException {
        taskList.add(new Todo("read book"));
        taskList.mark(0);

        Task unmarked = taskList.unmark(0);

        assertTrue(unmarked.toString().startsWith("[T][ ]"));
    }

    @Test
    public void mark_invalidIndex_throwsPeterException() {
        assertThrows(PeterException.class, () -> taskList.mark(0));
    }

    @Test
    public void getScheduleForDate_matchingDeadlineAndEvent_returnsDeadlineBeforeEvent() throws PeterException {
        LocalDate targetDate = LocalDate.of(2019, 12, 2);
        Deadline matchingDeadline = new Deadline("return book",
                LocalDateTime.of(2019, 12, 2, 18, 0));
        Event matchingEvent = new Event("project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0));
        Deadline nonMatchingDeadline = new Deadline("submit report",
                LocalDateTime.of(2019, 12, 3, 18, 0));

        // Added in event-then-deadline order, to confirm the result is
        // reordered rather than just returned in insertion order.
        taskList.add(matchingEvent);
        taskList.add(matchingDeadline);
        taskList.add(nonMatchingDeadline);
        taskList.add(new Todo("unrelated todo"));

        List<Task> schedule = taskList.getScheduleForDate(targetDate);

        assertEquals(List.of(matchingDeadline, matchingEvent), schedule);
    }

    @Test
    public void getScheduleForDate_multipleDeadlinesAndEvents_sortsEachGroupChronologically() throws PeterException {
        LocalDate targetDate = LocalDate.of(2019, 12, 2);
        Deadline laterDeadline = new Deadline("submit report",
                LocalDateTime.of(2019, 12, 2, 20, 0));
        Deadline earlierDeadline = new Deadline("return book",
                LocalDateTime.of(2019, 12, 2, 9, 0));
        Event laterEvent = new Event("dinner",
                LocalDateTime.of(2019, 12, 2, 19, 0),
                LocalDateTime.of(2019, 12, 2, 21, 0));
        Event earlierEvent = new Event("project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0));

        // Added out of chronological order within each type, to confirm
        // each group is sorted rather than left in insertion order.
        taskList.add(laterDeadline);
        taskList.add(laterEvent);
        taskList.add(earlierEvent);
        taskList.add(earlierDeadline);

        List<Task> schedule = taskList.getScheduleForDate(targetDate);

        assertEquals(List.of(earlierDeadline, laterDeadline, earlierEvent, laterEvent), schedule);
    }

    @Test
    public void getScheduleForDate_noMatches_returnsEmptyList() throws PeterException {
        taskList.add(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));

        List<Task> schedule = taskList.getScheduleForDate(LocalDate.of(2020, 1, 1));

        assertTrue(schedule.isEmpty());
    }

    @Test
    public void constructor_withLoadedTasks_preservesAllTasks() {
        Todo loadedTodo = new Todo("read book");
        TaskList loadedList = new TaskList(List.of(loadedTodo));

        assertEquals(1, loadedList.size());
        assertEquals(loadedTodo, loadedList.getAll().get(0));
    }

    @Test
    public void constructor_withVarargsTasks_preservesAllTasksInOrder() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("return book");
        TaskList varargsList = new TaskList(firstTask, secondTask);

        assertEquals(2, varargsList.size());
        assertEquals(firstTask, varargsList.getAll().get(0));
        assertEquals(secondTask, varargsList.getAll().get(1));
    }
}

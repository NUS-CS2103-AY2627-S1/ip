package nudge.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nudge.exception.NudgeException;

/**
 * Tests task-list operations performed by {@link TaskList}.
 */
class TaskListTest {
    @Test
    void constructor_nullTask_assertionFails() {
        List<Task> tasksWithNull = new ArrayList<>();
        tasksWithNull.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(tasksWithNull));
    }

    @Test
    void add_nullTask_assertionFails() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    void delete_validIndex_removesAndReturnsSelectedTask() throws NudgeException {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task deletedTask = tasks.delete(1);

        assertSame(secondTask, deletedTask);
        assertEquals(1, tasks.getSize());
        assertSame(firstTask, tasks.get(0));
    }

    @Test
    void delete_emptyList_exceptionThrown() {
        TaskList tasks = new TaskList();

        NudgeException exception = assertThrows(NudgeException.class, () -> tasks.delete(0));

        assertEquals("There are no tasks in your list yet.", exception.getMessage());
    }

    @Test
    void delete_negativeIndex_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("return book")));

        NudgeException exception = assertThrows(NudgeException.class, () -> tasks.delete(-1));

        assertEquals("Choose a task number from 1 to 2.", exception.getMessage());
    }

    @Test
    void delete_indexEqualToSize_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("return book")));

        NudgeException exception = assertThrows(NudgeException.class, () -> tasks.delete(2));

        assertEquals("Choose a task number from 1 to 2.", exception.getMessage());
    }

    @Test
    void delete_invalidIndexForSingleTask_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        NudgeException exception = assertThrows(NudgeException.class, () -> tasks.delete(1));

        assertEquals("Choose task number 1.", exception.getMessage());
    }

    @Test
    void find_matchingKeyword_returnsMatchingTasksInOriginalOrder() {
        Task firstMatchingTask = new Todo("read book");
        Task otherTask = new Todo("write code");
        Task secondMatchingTask = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstMatchingTask, otherTask, secondMatchingTask));

        assertEquals(List.of(firstMatchingTask, secondMatchingTask), tasks.find("book"));
    }

    @Test
    void find_absentKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("code"));
    }

    @Test
    void sortDeadlinesByDate_mixedTasks_sortsOnlyDeadlinePositionsStably() {
        Todo firstTodo = new Todo("read book");
        Deadline lateDeadline = new Deadline("submit report", LocalDate.of(2026, 10, 20));
        Event event = new Event("project meeting", LocalDate.of(2026, 9, 18),
                LocalDate.of(2026, 9, 19));
        Deadline firstEqualDeadline = new Deadline("return first book",
                LocalDate.of(2026, 9, 15));
        Todo secondTodo = new Todo("write notes");
        Deadline secondEqualDeadline = new Deadline("return second book",
                LocalDate.of(2026, 9, 15));
        firstEqualDeadline.markAsDone();
        TaskList tasks = new TaskList(List.of(firstTodo, lateDeadline, event,
                firstEqualDeadline, secondTodo, secondEqualDeadline));

        tasks.sortDeadlinesByDate();

        assertEquals(List.of(firstTodo, firstEqualDeadline, event, secondEqualDeadline,
                secondTodo, lateDeadline), tasks.getTasks());
    }

    @Test
    void sortDeadlinesByDate_withoutDeadlines_orderUnchanged() {
        Todo todo = new Todo("read book");
        Event event = new Event("project meeting", LocalDate.of(2026, 9, 18),
                LocalDate.of(2026, 9, 19));
        TaskList tasks = new TaskList(List.of(todo, event));

        tasks.sortDeadlinesByDate();

        assertEquals(List.of(todo, event), tasks.getTasks());
    }
}

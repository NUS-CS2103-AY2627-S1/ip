package panda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {

    @Test
    void constructor_emptyList_createsEmptyTaskList() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
    }

    @Test
    void constructor_initialTasks_copiesTasksIntoList() {
        Todo first = new Todo("read book");
        Todo second = new Todo("submit report");

        TaskList taskList = new TaskList(List.of(first, second));

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    void get_validIndex_returnsTaskAtThatPosition() {
        Todo task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task));

        assertSame(task, taskList.get(0));
    }

    @Test
    void get_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(1));
    }

    @Test
    void add_task_appendsToEndOfList() {
        TaskList taskList = new TaskList();
        Todo task = new Todo("buy groceries");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
    }

    @Test
    void containsEquivalent_sameTodoDetails_returnsTrueRegardlessOfStatus() {
        Todo existingTask = new Todo("buy groceries");
        existingTask.markAsDone();
        TaskList taskList = new TaskList(List.of(existingTask));

        assertEquals(true, taskList.containsEquivalent(new Todo("buy groceries")));
    }

    @Test
    void containsEquivalent_differentEventTime_returnsFalse() {
        Event existingEvent = new Event("team meeting",
                LocalDateTime.of(2026, 10, 20, 9, 0), LocalDateTime.of(2026, 10, 20, 10, 0));
        Event differentEvent = new Event("team meeting",
                LocalDateTime.of(2026, 10, 20, 10, 0), LocalDateTime.of(2026, 10, 20, 11, 0));
        TaskList taskList = new TaskList(List.of(existingEvent));

        assertEquals(false, taskList.containsEquivalent(differentEvent));
    }

    @Test
    void find_keyword_returnsMatchingTasksIgnoreCase() {
        Todo first = new Todo("read book");
        Deadline second = new Deadline("return book", "2019-10-20");
        Todo third = new Todo("buy milk");
        TaskList taskList = new TaskList(List.of(first, second, third));

        List<Task> matches = taskList.find("BOOK");

        assertEquals(2, matches.size());
        assertSame(first, matches.get(0));
        assertSame(second, matches.get(1));
    }

    @Test
    void find_noMatches_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        List<Task> matches = taskList.find("grocery");

        assertEquals(0, matches.size());
    }

    @Test
    void sortDeadlinesByDate_ascendingOrdersDeadlinesAndPreservesOtherTaskOrder() {
        Todo firstTodo = new Todo("read book");
        Event event = new Event("team meeting",
                LocalDateTime.of(2026, 10, 20, 9, 0),
                LocalDateTime.of(2026, 10, 20, 10, 0));
        Deadline laterDeadline = new Deadline("submit report", LocalDateTime.of(2026, 10, 22, 12, 0));
        Todo secondTodo = new Todo("buy milk");
        Deadline earlierDeadline = new Deadline("return book", LocalDateTime.of(2026, 10, 21, 12, 0));
        TaskList taskList = new TaskList(List.of(firstTodo, event, laterDeadline, secondTodo, earlierDeadline));

        List<Task> sortedTasks = taskList.sortDeadlinesByDate(taskList.asList(), SortDirection.ASCENDING);

        assertEquals(List.of(earlierDeadline, laterDeadline, firstTodo, event, secondTodo), sortedTasks);
        assertEquals(List.of(firstTodo, event, laterDeadline, secondTodo, earlierDeadline), taskList.asList());
    }

    @Test
    void sortDeadlinesByDate_descendingPreservesEqualDeadlineOrder() {
        Deadline firstSameDateDeadline = new Deadline("first", LocalDateTime.of(2026, 10, 21, 12, 0));
        Deadline laterDeadline = new Deadline("later", LocalDateTime.of(2026, 10, 22, 12, 0));
        Deadline secondSameDateDeadline = new Deadline("second", LocalDateTime.of(2026, 10, 21, 12, 0));
        Todo todo = new Todo("read book");
        TaskList taskList = new TaskList(List.of(firstSameDateDeadline, laterDeadline, secondSameDateDeadline, todo));

        List<Task> sortedTasks = taskList.sortDeadlinesByDate(taskList.asList(), SortDirection.DESCENDING);

        assertEquals(List.of(laterDeadline, firstSameDateDeadline, secondSameDateDeadline, todo), sortedTasks);
    }

    @Test
    void remove_validIndex_returnsRemovedTaskAndUpdatesList() {
        Todo first = new Todo("read book");
        Todo second = new Todo("submit report");
        TaskList taskList = new TaskList(List.of(first, second));

        Task removed = taskList.remove(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    void remove_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(1));
    }

    @Test
    void markTask_validIndex_marksTaskDone() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        Task updated = taskList.markTask(0);

        assertEquals("X", updated.getStatusIcon());
        assertEquals("X", taskList.get(0).getStatusIcon());
    }

    @Test
    void unmarkTask_validIndex_marksTaskNotDone() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));
        taskList.get(0).markAsDone();

        Task updated = taskList.unmarkTask(0);

        assertEquals(" ", updated.getStatusIcon());
        assertEquals(" ", taskList.get(0).getStatusIcon());
    }

    @Test
    void markTask_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.markTask(0));
    }

    @Test
    void unmarkTask_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmarkTask(1));
    }

    @Test
    void asList_returnsUnderlyingMutableList() {
        TaskList taskList = new TaskList();
        Todo task = new Todo("read book");

        taskList.asList().add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
    }
}

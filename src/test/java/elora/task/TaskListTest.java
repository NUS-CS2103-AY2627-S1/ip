package elora.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class TaskListTest {

    @Test
    void size_newTaskList_isZero() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    void add_singleTask_increasesSizeByOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));
        assertEquals(1, tasks.size());
    }

    @Test
    void get_validIndex_returnsTaskAtThatPosition() {
        TaskList tasks = new TaskList();
        Task first = new Task("read book");
        Task second = new Task("return book");
        tasks.add(first);
        tasks.add(second);
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    @Test
    void remove_validIndex_returnsRemovedTaskAndShrinksList() {
        TaskList tasks = new TaskList();
        Task first = new Task("read book");
        Task second = new Task("return book");
        tasks.add(first);
        tasks.add(second);

        Task removed = tasks.remove(0);

        assertEquals(first, removed);
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    void getTasksOnDate_deadlineOnThatDate_isIncluded() {
        TaskList tasks = new TaskList();
        Deadline dueOnTarget = new Deadline("pay bills", LocalDate.parse("2019-10-15"));
        tasks.add(dueOnTarget);

        ArrayList<Task> matches = tasks.getTasksOnDate(LocalDate.parse("2019-10-15"));

        assertEquals(1, matches.size());
        assertTrue(matches.contains(dueOnTarget));
    }

    @Test
    void getTasksOnDate_noTaskOnThatDate_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("pay bills", LocalDate.parse("2019-10-15")));

        ArrayList<Task> matches = tasks.getTasksOnDate(LocalDate.parse("2099-01-01"));

        assertEquals(0, matches.size());
    }

    @Test
    void getTasksOnDate_todoOnAnyDate_isNeverIncluded() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("unrelated"));

        ArrayList<Task> matches = tasks.getTasksOnDate(LocalDate.parse("2019-10-15"));

        assertEquals(0, matches.size());
    }

    @Test
    void getTasksOnDate_isoDateRangedEventCoveringThatDate_isIncluded() {
        TaskList tasks = new TaskList();
        Event trip = new Event("trip", "2029-01-01", "2029-01-05");
        tasks.add(trip);

        ArrayList<Task> matches = tasks.getTasksOnDate(LocalDate.parse("2029-01-02"));

        assertEquals(1, matches.size());
        assertTrue(matches.contains(trip));
    }

    @Test
    void getTasksOnDate_freeTextEvent_isNeverIncluded() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("meeting", "Mon 2pm", "4pm"));

        ArrayList<Task> matches = tasks.getTasksOnDate(LocalDate.parse("2019-10-15"));

        assertEquals(0, matches.size());
    }

    @Test
    void sortByDate_deadlinesOutOfOrder_areSortedChronologically() {
        TaskList tasks = new TaskList();
        Deadline later = new Deadline("submit report", LocalDate.parse("2026-12-01"));
        Deadline sooner = new Deadline("pay bills", LocalDate.parse("2019-10-15"));
        tasks.add(later);
        tasks.add(sooner);

        tasks.sortByDate();

        assertEquals(sooner, tasks.get(0));
        assertEquals(later, tasks.get(1));
    }

    @Test
    void sortByDate_mixOfDatedAndUndatedTasks_undatedTasksKeepRelativeOrderAtEnd() {
        TaskList tasks = new TaskList();
        Todo firstTodo = new Todo("water plants");
        Deadline deadline = new Deadline("pay bills", LocalDate.parse("2019-10-15"));
        Todo secondTodo = new Todo("read book");
        tasks.add(firstTodo);
        tasks.add(deadline);
        tasks.add(secondTodo);

        tasks.sortByDate();

        assertEquals(deadline, tasks.get(0));
        assertEquals(firstTodo, tasks.get(1));
        assertEquals(secondTodo, tasks.get(2));
    }

    @Test
    void sortByDate_isoDateRangedEventAndDeadline_sortedByStartOrDueDate() {
        TaskList tasks = new TaskList();
        Deadline later = new Deadline("submit report", LocalDate.parse("2029-06-01"));
        Event sooner = new Event("trip", "2019-10-15", "2019-10-20");
        tasks.add(later);
        tasks.add(sooner);

        tasks.sortByDate();

        assertEquals(sooner, tasks.get(0));
        assertEquals(later, tasks.get(1));
    }

    @Test
    void sortByDate_freeTextEventAmongTodos_keepsRelativeOrderAtEnd() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("water plants");
        Event freeTextEvent = new Event("meeting", "Mon 2pm", "4pm");
        Deadline deadline = new Deadline("pay bills", LocalDate.parse("2019-10-15"));
        tasks.add(todo);
        tasks.add(freeTextEvent);
        tasks.add(deadline);

        tasks.sortByDate();

        assertEquals(deadline, tasks.get(0));
        assertEquals(todo, tasks.get(1));
        assertEquals(freeTextEvent, tasks.get(2));
    }
}

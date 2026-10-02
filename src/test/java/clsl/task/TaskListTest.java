package clsl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import clsl.ClslException;

/** Tests task-list operations that depend on a task's type and dates. */
public class TaskListTest {
    @Test
    void newTaskListStartsEmptyAndExposesAnUnmodifiableView() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        assertEquals(List.of(), taskList.asList());
        assertEquals(List.of(), taskList.getTasksOn(LocalDate.of(2026, 9, 1)));
        assertEquals(List.of(), taskList.findTasks("book"));
        assertThrows(UnsupportedOperationException.class, () ->
                taskList.asList().add(new ToDo("read book")));
    }

    @Test
    void taskListCanGetDeleteMarkAndUnmarkTasks() throws ClslException {
        ToDo todo = new ToDo("read book");
        TaskList taskList = new TaskList();

        taskList.add(todo);
        assertEquals(todo, taskList.get(0));
        taskList.mark(0);
        assertEquals("[X]", todo.getStatusIcon());
        taskList.unmark(0);
        assertEquals("[ ]", todo.getStatusIcon());
        assertEquals(todo, taskList.delete(0));
        assertEquals(0, taskList.size());
    }

    @Test
    void getTasksOn_tasksWithDifferentDates_returnsOnlyMatchingTasks() {
        ToDo todo = new ToDo("read book");
        Deadline deadline = new Deadline("submit report", "2026-09-02");
        Event event = new Event("conference", "2026-09-01", "2026-09-03");
        TaskList taskList = new TaskList(List.of(todo, deadline, event));

        List<Task> matchingTasks = taskList.getTasksOn(LocalDate.of(2026, 9, 2));

        assertEquals(List.of(deadline, event), matchingTasks);
    }

    @Test
    void getTasksOn_eventBoundaryDates_includesEvent() {
        Event event = new Event("conference", "2026-09-01", "2026-09-03");
        TaskList taskList = new TaskList(List.of(event));

        assertEquals(List.of(event), taskList.getTasksOn(LocalDate.of(2026, 9, 1)));
        assertEquals(List.of(event), taskList.getTasksOn(LocalDate.of(2026, 9, 3)));
    }

    @Test
    void getTasksOn_noTaskMatches_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));

        assertEquals(List.of(), taskList.getTasksOn(LocalDate.of(2026, 9, 2)));
    }

    @Test
    void findTasks_matchingDescriptions_returnsMatchingTasksIgnoringCase() {
        ToDo readBook = new ToDo("read book");
        Deadline returnBook = new Deadline("return book", "2026-09-02");
        TaskList taskList = new TaskList(List.of(readBook, returnBook, new ToDo("write report")));

        assertEquals(List.of(readBook, returnBook), taskList.findTasks("BOOK"));
    }

    @Test
    void add_todoWithDifferentCaseAndWhitespace_throwsException() throws ClslException {
        TaskList taskList = new TaskList(List.of(new ToDo("Buy milk")));

        ClslException exception = assertThrows(ClslException.class, () ->
                taskList.add(new ToDo("BU Y   MILK")));

        assertEquals("already added", exception.getMessage());
        assertEquals(1, taskList.size());
    }

    @Test
    void add_sameDeadlineDetailsIgnoringCase_throwsException() throws ClslException {
        TaskList taskList = new TaskList(List.of(new Deadline("Submit report", "2026-09-10")));

        assertThrows(ClslException.class, () ->
                taskList.add(new Deadline("submit REPORT", "2026-09-10")));
    }

    @Test
    void add_sameDeadlineDescriptionWithDifferentDateIsAllowed() throws ClslException {
        TaskList taskList = new TaskList(List.of(new Deadline("Submit report", "2026-09-10")));

        taskList.add(new Deadline("submit report", "2026-09-11"));

        assertEquals(2, taskList.size());
    }

    @Test
    void add_sameEventDetailsIgnoringCase_throwsException() throws ClslException {
        TaskList taskList = new TaskList(List.of(
                new Event("Team meeting", "2026-09-10", "2026-09-11")));

        assertThrows(ClslException.class, () ->
                taskList.add(new Event("team MEETING", "2026-09-10", "2026-09-11")));
    }

    @Test
    void add_sameEventDescriptionWithDifferentRangeIsAllowed() throws ClslException {
        TaskList taskList = new TaskList(List.of(
                new Event("Team meeting", "2026-09-10", "2026-09-11")));

        taskList.add(new Event("team meeting", "2026-09-10", "2026-09-12"));

        assertEquals(2, taskList.size());
    }

    @Test
    void add_differentTypeOrDate_addsTask() throws ClslException {
        TaskList taskList = new TaskList(List.of(new ToDo("Buy milk")));

        taskList.add(new Deadline("Buy milk", "2026-09-10"));
        taskList.add(new ToDo("Buy milk on Friday"));

        assertEquals(3, taskList.size());
    }

    @Test
    void constructor_existingDuplicates_keepsFirstOccurrence() {
        Task firstTask = new ToDo("Buy milk");
        Task secondTask = new ToDo("buy milk");

        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        assertEquals(List.of(firstTask), taskList.asList());
    }

    @Test
    void findTasksReturnsOnlyDescriptionsContainingKeyword() {
        ToDo readBook = new ToDo("read book");
        TaskList taskList = new TaskList(List.of(readBook, new ToDo("write report")));

        assertEquals(List.of(readBook), taskList.findTasks("book"));
        assertEquals(List.of(), taskList.findTasks("holiday"));
    }
}

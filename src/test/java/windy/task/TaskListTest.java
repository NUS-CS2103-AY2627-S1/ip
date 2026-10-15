package windy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import windy.exception.InvalidInputFormatException;

/**
 * Tests the task management and date-based filtering behavior of {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void copyAndReplace_isolateMutableTasksAndPreserveAllFields() throws InvalidInputFormatException {
        TaskList original = new TaskList(new ArrayList<>(List.of(
                new Todo("A", true),
                new Deadline("B", false, "2026-9-7"),
                new Event("C", true, "2026-9-7", "2026-9-8"))));
        List<String> records = original.getTasks().stream().map(Task::toDataString).toList();
        TaskList snapshot = original.copy();
        original.getTasks().forEach(task -> task.setDone(!task.isDone()));
        original.deleteTask(1);
        assertEquals(records, snapshot.getTasks().stream().map(Task::toDataString).toList());

        original.replaceWith(snapshot);
        original.getTasks().forEach(task -> task.setDone(!task.isDone()));
        assertEquals(records, snapshot.getTasks().stream().map(Task::toDataString).toList());
        snapshot.replaceWith(snapshot);
        assertEquals(records, snapshot.getTasks().stream().map(Task::toDataString).toList());
    }

    @Test
    public void addTask_newTask_addsTaskToList() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Todo todo = new Todo("buy groceries", false);

        taskList.addTask(todo);

        assertEquals(1, taskList.getNumTasks());
        assertSame(todo, taskList.getTask(0));
    }

    @Test
    public void deleteTask_existingTask_removesAndReturnsTask() {
        Todo firstTask = new Todo("buy groceries", false);
        Todo secondTask = new Todo("read book", false);
        TaskList taskList = new TaskList(new ArrayList<>(List.of(firstTask, secondTask)));

        Task deletedTask = taskList.deleteTask(0);

        assertSame(firstTask, deletedTask);
        assertEquals(List.of(secondTask), taskList.getTasks());
    }

    @Test
    public void findTaskByDate_multipleTasks_returnsMatchingIncompleteTasks()
            throws InvalidInputFormatException {
        LocalDate searchDate = LocalDate.of(2026, 8, 27);
        Deadline matchingDeadline = new Deadline("submit assignment", false, "2026-8-27");
        Event matchingEvent = new Event("project meeting", false, "2026-8-26", "2026-8-28");
        Event nonMatchingEvent = new Event("holiday", false, "2026-9-1", "2026-9-2");
        Deadline completedDeadline = new Deadline("finished work", true, "2026-8-27");
        Todo todo = new Todo("buy groceries", false);
        TaskList taskList = new TaskList(List.of(
                matchingDeadline, matchingEvent, nonMatchingEvent, completedDeadline, todo));

        List<Task> foundTasks = taskList.findTasksByDate(searchDate);

        assertEquals(List.of(matchingDeadline, matchingEvent), foundTasks);
    }

    @Test
    public void findTaskByDate_noMatchingTasks_returnsEmptyList()
            throws InvalidInputFormatException {
        TaskList taskList = new TaskList(List.of(
                new Event("holiday", false, "2026-9-1", "2026-9-2"),
                new Todo("buy groceries", false)));

        List<Task> foundTasks = taskList.findTasksByDate(LocalDate.of(2026, 8, 27));

        assertTrue(foundTasks.isEmpty());
    }

    @Test
    public void findTasksByDate_startEndAndDeadlineDates_areInclusive()
            throws InvalidInputFormatException {
        Event event = new Event("conference", false, "2026-9-3", "2026-9-5");
        Deadline deadline = new Deadline("submit", false, "2026-9-5");
        TaskList tasks = new TaskList(List.of(event, deadline));

        assertEquals(List.of(deadline), tasks.findTasksByDate(LocalDate.of(2026, 9, 2)));
        assertEquals(List.of(event, deadline), tasks.findTasksByDate(LocalDate.of(2026, 9, 3)));
        assertEquals(List.of(event, deadline), tasks.findTasksByDate(LocalDate.of(2026, 9, 5)));
        assertTrue(tasks.findTasksByDate(LocalDate.of(2026, 9, 6)).isEmpty());

        event.setDone(true);
        deadline.setDone(true);
        assertTrue(tasks.findTasksByDate(LocalDate.of(2026, 9, 5)).isEmpty());
    }

    @Test
    public void findTasksContainingKeyword_differentLetterCase_returnsMatchingTasks() {
        Todo matchingTodo = new Todo("Read BOOK", false);
        Todo nonMatchingTodo = new Todo("buy groceries", false);
        TaskList taskList = new TaskList(List.of(matchingTodo, nonMatchingTodo));

        List<Task> foundTasks = taskList.findTasksContainingKeyword("book");

        assertEquals(List.of(matchingTodo), foundTasks);
    }
}

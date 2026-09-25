package goat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-number parsing against collection boundaries.
 */
public class TaskListTest {
    private final TaskList tasks = new TaskList(List.of(
            new Todo("first"), new Todo("second"), new Todo("third")));

    @Test
    public void parseIndex_validOneBasedNumber_returnsZeroBasedIndex() throws GoatException {
        assertEquals(0, tasks.parseIndex("1", "mark"));
        assertEquals(2, tasks.parseIndex("3", "delete"));
    }

    @Test
    public void parseIndex_missingNumber_throwsUsageGuidance() {
        GoatException exception = assertThrows(
                GoatException.class, () -> tasks.parseIndex("", "mark"));

        assertEquals("Please specify a task number. Try: mark <task number>",
                exception.getMessage());
    }

    @Test
    public void parseIndex_nonNumericNumber_throwsHelpfulException() {
        GoatException exception = assertThrows(
                GoatException.class, () -> tasks.parseIndex("two", "delete"));

        assertEquals("The task number must be a positive whole number.", exception.getMessage());
    }

    @Test
    public void parseIndex_numbersOutsideList_throwsRangeGuidance() {
        GoatException belowRange = assertThrows(
                GoatException.class, () -> tasks.parseIndex("0", "mark"));
        GoatException aboveRange = assertThrows(
                GoatException.class, () -> tasks.parseIndex("4", "mark"));

        assertEquals("Task 0 does not exist. Choose a number from 1 to 3.",
                belowRange.getMessage());
        assertEquals("Task 4 does not exist. Choose a number from 1 to 3.",
                aboveRange.getMessage());
    }

    @Test
    public void parseIndex_emptyTaskList_throwsEmptyListGuidance() {
        TaskList emptyTasks = new TaskList();

        GoatException exception = assertThrows(
                GoatException.class, () -> emptyTasks.parseIndex("1", "mark"));

        assertEquals("There are no tasks in the list yet.", exception.getMessage());
    }

    @Test
    public void find_matchingKeyword_returnsMatchingTasksInOriginalOrder() {
        TaskList searchableTasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("finish homework"),
                new Todo("return book")));

        TaskList matches = searchableTasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[T][ ] return book", matches.get(1).toString());
    }

    @Test
    public void find_differentCase_returnsNoMatches() {
        TaskList matches = tasks.find("FIRST");

        assertEquals(0, matches.size());
    }

    @Test
    public void get_indexOutsideList_assertionError() {
        assertThrows(AssertionError.class, () -> tasks.get(tasks.size()));
    }

    @Test
    public void add_nullTask_assertionError() {
        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void addAndDelete_validTasks_preservesExpectedOrder() {
        TaskList editableTasks = new TaskList();
        editableTasks.add(new Todo("first"));
        editableTasks.add(new Todo("third"));
        editableTasks.add(1, new Todo("second"));

        Task deletedTask = editableTasks.delete(0);

        assertEquals("[T][ ] first", deletedTask.toString());
        assertEquals("[T][ ] second", editableTasks.get(0).toString());
        assertEquals("[T][ ] third", editableTasks.get(1).toString());
    }

    @Test
    public void asList_returnedSnapshotCannotModifyTaskList() {
        List<Task> snapshot = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () ->
                snapshot.add(new Todo("fourth")));
        assertEquals(3, tasks.size());
        assertTrue(snapshot.contains(tasks.get(0)));
    }

    @Test
    public void sortedChronologically_mixedTasks_ordersDatesThenTodos() {
        Todo completedTodo = new Todo("Buy apples");
        completedTodo.markAsDone();
        TaskList unsortedTasks = new TaskList(List.of(
                new Todo("write notes"),
                new Deadline("submit report", LocalDate.of(2026, 10, 15)),
                new Event("camp", LocalDate.of(2026, 9, 20),
                        LocalDate.of(2026, 9, 22)),
                new Deadline("apply", LocalDate.of(2026, 9, 20)),
                completedTodo));

        TaskList sortedTasks = unsortedTasks.sortedChronologically();

        assertEquals("[D][ ] apply (by: Sep 20 2026)", sortedTasks.get(0).toString());
        assertEquals("[E][ ] camp (from: Sep 20 2026 to: Sep 22 2026)",
                sortedTasks.get(1).toString());
        assertEquals("[D][ ] submit report (by: Oct 15 2026)",
                sortedTasks.get(2).toString());
        assertEquals("[T][X] Buy apples", sortedTasks.get(3).toString());
        assertEquals("[T][ ] write notes", sortedTasks.get(4).toString());
        assertEquals("[T][ ] write notes", unsortedTasks.get(0).toString());
    }
}

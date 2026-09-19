package fox.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import fox.exception.FoxException;

/** JUnit tests for task-list indexing, mutation, deletion, and capacity. */
class TaskListJUnitTest {
    @Test
    void supportsOneBasedAccessAndMutations() throws FoxException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList list = new TaskList(2, first, second);

        assertEquals(first, list.get(1));
        assertEquals(second, list.get(2));
        assertEquals(first, list.markDone(1));
        assertEquals(first, list.delete(1));
        assertEquals(1, list.size());
        assertEquals(second, list.get(1));
    }

    @Test
    void acceptsZeroOneOrMultipleInitialTasks() throws FoxException {
        Task first = new Todo("first");
        Task second = new Todo("second");

        assertEquals(0, new TaskList(3).size());
        assertEquals(1, new TaskList(3, first).size());
        assertEquals(2, new TaskList(3, first, second).size());
    }

    @Test
    void rejectsInvalidIndexesAndOverflow() throws FoxException {
        TaskList list = new TaskList(1);
        list.add(new Todo("only task"));

        assertThrows(FoxException.class, () -> list.get(0));
        assertThrows(FoxException.class, () -> list.get(2));
        assertThrows(FoxException.class, () -> list.delete(-1));
        assertThrows(FoxException.class, () -> list.add(new Todo("overflow")));
    }

    @Test
    void rejectsNegativeCapacityAndOversizedInitialData() {
        assertThrows(IllegalArgumentException.class, () -> new TaskList(-1));
        assertThrows(FoxException.class, () ->
                new TaskList(0, new Todo("one")));
        assertThrows(FoxException.class, () -> new TaskList(1, (Task) null));
        assertThrows(FoxException.class, () -> new TaskList(2,
                new Todo("duplicate"), new Todo("duplicate")));
    }

    @Test
    void rejectsDuplicateTasksButAllowsDifferentDetails() throws FoxException {
        Todo completedTodo = new Todo("read book");
        completedTodo.markAsDone();
        TaskList list = new TaskList(8, completedTodo);

        FoxException duplicateTodo = assertThrows(FoxException.class, () ->
                list.add(new Todo("read book")));
        assertEquals("☹ OOPS!!! This task is already in your task list.", duplicateTodo.getMessage());

        list.add(new Deadline("read book", LocalDate.of(2026, 12, 1)));
        list.add(new Deadline("submit report", LocalDate.of(2026, 12, 1)));
        list.add(new Deadline("submit report", LocalDate.of(2026, 12, 2)));
        list.add(new Event("meeting", "10am", "11am"));
        list.add(new Event("meeting", "10am", "12pm"));

        assertThrows(FoxException.class, () ->
                list.add(new Deadline("submit report", LocalDate.of(2026, 12, 1))));
        assertThrows(FoxException.class, () ->
                list.add(new Event("meeting", "10am", "11am")));
        assertEquals(6, list.size());
    }

    @Test
    void findsWholeWordsCaseInsensitivelyAndKeepsOriginalNumbers() throws FoxException {
        TaskList list = new TaskList(4,
                new Todo("buy milk"),
                new Todo("buying milkshake"),
                new Todo("READ project notes"),
                new Todo("plan holiday"));

        assertEquals(List.of(1), list.findMatchingTaskNumbers("BUY"));
        assertEquals(List.of(1, 3), list.findMatchingTaskNumbers("milk", "project"));
        assertEquals(List.of(), list.findMatchingTaskNumbers("absent"));
    }
}

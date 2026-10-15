package alexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalInt;

import org.junit.jupiter.api.Test;

/** Tests task search behavior performed by {@link TaskList}. */
class TaskListTest {
    @Test
    void addRemoveAndGet_tasksMaintainTheirOrder() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(second, tasks.get(1));
        assertEquals(first, tasks.remove(0));
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    void constructor_sourceListChanges_doesNotChangeTaskList() {
        List<Task> sourceTasks = new java.util.ArrayList<>(List.of(new Todo("read book")));
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.add(new Todo("buy groceries"));

        assertEquals(1, tasks.size());
    }

    @Test
    void asList_attemptedModification_throwsException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(UnsupportedOperationException.class,
                () -> tasks.asList().add(new Todo("buy groceries")));
    }
    @Test
    void findTasks_matchingKeyword_returnsMatchesInListOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("buy groceries"),
                new Todo("return BOOK")));

        List<Task> matches = tasks.findTasks("book");

        assertEquals(List.of("[T][ ] read book", "[T][ ] return BOOK"),
                matches.stream().map(Task::toString).toList());
    }

    @Test
    void findTasks_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.findTasks("meeting").isEmpty());
    }

    @Test
    void findDuplicateTaskNumber_matchingTaskTypes_returnsFirstOneBasedNumber() {
        Todo completedTodo = new Todo("read book");
        completedTodo.markAsDone();
        TaskList tasks = new TaskList(List.of(
                completedTodo,
                new Deadline("return book", LocalDate.of(2026, 10, 15)),
                new Event("project meeting", LocalDate.of(2026, 10, 16), LocalDate.of(2026, 10, 17))));

        assertEquals(OptionalInt.of(1), tasks.findDuplicateTaskNumber(new Todo("read book")));
        assertEquals(OptionalInt.of(2),
                tasks.findDuplicateTaskNumber(new Deadline("return book", LocalDate.of(2026, 10, 15))));
        assertEquals(OptionalInt.of(3), tasks.findDuplicateTaskNumber(
                new Event("project meeting", LocalDate.of(2026, 10, 16), LocalDate.of(2026, 10, 17))));
    }

    @Test
    void findDuplicateTaskNumber_differentTypeOrDates_returnsEmpty() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2026, 10, 15)),
                new Event("project meeting", LocalDate.of(2026, 10, 16), LocalDate.of(2026, 10, 17))));

        assertTrue(tasks.findDuplicateTaskNumber(
                new Deadline("read book", LocalDate.of(2026, 10, 15))).isEmpty());
        assertTrue(tasks.findDuplicateTaskNumber(
                new Deadline("return book", LocalDate.of(2026, 10, 16))).isEmpty());
        assertTrue(tasks.findDuplicateTaskNumber(
                new Event("project meeting", LocalDate.of(2026, 10, 16), LocalDate.of(2026, 10, 18))).isEmpty());
    }
}

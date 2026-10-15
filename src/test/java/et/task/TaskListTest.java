package et.task;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests task-list searching and chronological ordering.
 */
class TaskListTest {
    @Test
    void findTasks_keywordMatchesDescriptionsIgnoringCase() {
        Task readBook = new Todo("read book");
        Task returnBook = new Deadline("return Book", LocalDateTime.of(2019, 6, 6, 0, 0), false);
        Task buyGroceries = new Todo("buy groceries");
        TaskList tasks = new TaskList(List.of(readBook, returnBook, buyGroceries));

        assertEquals(List.of(readBook, returnBook), tasks.findTasks("BOOK"));
    }

    @Test
    void findTasks_keywordDoesNotMatch_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.findTasks("notes"));
    }

    @Test
    void getChronologicallySortedTaskNumbers_mixedTasks_ordersByStatusDateAndType() {
        Todo incompleteTodo = new Todo("write reflection");
        Deadline completeLaterDeadline = new Deadline(
                "submit report", LocalDateTime.of(2027, 1, 10, 18, 0), true);
        Event incompleteEvent = new Event(
                "team meeting", LocalDateTime.of(2027, 1, 5, 9, 0), true,
                LocalDateTime.of(2027, 1, 5, 10, 0), true);
        Deadline incompleteDateOnlyDeadline = new Deadline(
                "pay fee", LocalDateTime.of(2027, 1, 5, 0, 0), false);
        Event completeEarlierEvent = new Event(
                "launch", LocalDateTime.of(2027, 1, 2, 12, 0), true,
                LocalDateTime.of(2027, 1, 2, 13, 0), true);
        Todo completeTodo = new Todo("archive notes");
        completeLaterDeadline.markAsDone();
        completeEarlierEvent.markAsDone();
        completeTodo.markAsDone();
        TaskList tasks = new TaskList(List.of(incompleteTodo, completeLaterDeadline, incompleteEvent,
                incompleteDateOnlyDeadline, completeEarlierEvent, completeTodo));

        assertEquals(List.of(4, 3, 1, 5, 2, 6), tasks.getChronologicallySortedTaskNumbers());
    }

    @Test
    void getChronologicallySortedTaskNumbers_equalDatesAndTodos_preservesCanonicalOrderWithoutMutation() {
        Todo firstTodo = new Todo("first todo");
        Event event = new Event(
                "meeting", LocalDateTime.of(2027, 1, 5, 9, 0), true,
                LocalDateTime.of(2027, 1, 5, 10, 0), true);
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2027, 1, 5, 9, 0), true);
        Todo secondTodo = new Todo("second todo");
        List<Task> canonicalOrder = List.of(firstTodo, event, deadline, secondTodo);
        TaskList tasks = new TaskList(canonicalOrder);

        assertEquals(List.of(2, 3, 1, 4), tasks.getChronologicallySortedTaskNumbers());
        assertEquals(canonicalOrder, tasks.getTasks());
    }
}

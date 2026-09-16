package crow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void constructor_copiesInitialList() {
        Todo todo = new Todo("read book");
        List<Task> initialTasks = new java.util.ArrayList<>();
        initialTasks.add(todo);

        TaskList taskList = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(1, taskList.getTaskCount());
        assertSame(todo, taskList.get(0));
    }

    @Test
    void addMarkUnmarkDelete_updatesList() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book");

        taskList.add(todo);
        assertEquals(1, taskList.getTaskCount());

        taskList.mark(0);
        assertTrue(todo.isDone());

        taskList.unmark(0);
        assertFalse(todo.isDone());

        assertSame(todo, taskList.delete(0));
        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    void getTasks_resultCannotBeModifiedByCaller() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(UnsupportedOperationException.class,
                () -> taskList.getTasks().add(new Todo("return book")));
    }

    @Test
    void find_matchingKeyword_returnsMatchesInOriginalOrder() {
        TaskList taskList = new TaskList();
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy bread");
        Todo secondMatch = new Todo("return BOOK");
        taskList.add(firstMatch);
        taskList.add(nonMatch);
        taskList.add(secondMatch);

        List<Task> matchingTasks = taskList.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void find_noMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("buy bread")));

        assertTrue(taskList.find("book").isEmpty());
    }

    @Test
    void internalOperations_invalidArguments_triggerAssertions() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.add(null));
        assertThrows(AssertionError.class, () -> taskList.find(" "));
        assertThrows(AssertionError.class, () -> taskList.mark(0));
    }

    @Test
    void constructor_mixedTasks_automaticallyGroupsAndSortsTasks() {
        Todo zooTodo = new Todo("visit zoo");
        Todo bookTodo = new Todo("borrow book");
        Deadline laterDeadline = new Deadline("apply permit",
                LocalDateTime.of(2026, 9, 5, 12, 0));
        Deadline alphabeticallyLaterDeadline = new Deadline("write report",
                LocalDateTime.of(2026, 9, 4, 18, 0));
        Deadline alphabeticallyEarlierDeadline = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 4, 18, 0));
        Event laterEvent = new Event("team dinner",
                LocalDateTime.of(2026, 9, 6, 19, 0), LocalDateTime.of(2026, 9, 6, 21, 0));
        Event earlierEvent = new Event("team meeting",
                LocalDateTime.of(2026, 9, 5, 10, 0), LocalDateTime.of(2026, 9, 5, 11, 0));
        TaskList taskList = new TaskList(List.of(laterEvent, laterDeadline, zooTodo,
                alphabeticallyLaterDeadline, bookTodo, earlierEvent, alphabeticallyEarlierDeadline));

        assertEquals(List.of(bookTodo, zooTodo, alphabeticallyEarlierDeadline,
                alphabeticallyLaterDeadline, laterDeadline, earlierEvent, laterEvent), taskList.getTasks());
    }
}

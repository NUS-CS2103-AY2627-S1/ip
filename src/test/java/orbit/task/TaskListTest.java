package orbit.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import orbit.exception.OrbitException;

class TaskListTest {
    @Test
    void delete_middleTask_removesOnlySelectedTask() throws OrbitException {
        Todo first = new Todo("first");
        Deadline second = new Deadline("second", LocalDate.of(2026, 9, 4));
        Event third = new Event("third", LocalDate.of(2026, 9, 5),
                LocalDate.of(2026, 9, 6));
        TaskList tasks = new TaskList(List.of(first, second, third));

        Task removed = tasks.delete(2);

        assertSame(second, removed);
        assertEquals(List.of(first, third), tasks.asList());
    }

    @Test
    void restore_deletedTask_returnsItToOriginalPosition() throws OrbitException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        Todo third = new Todo("third");
        TaskList tasks = new TaskList(List.of(first, second, third));
        Task removed = tasks.delete(2);

        tasks.restore(2, removed);

        assertEquals(List.of(first, second, third), tasks.asList());
    }

    @Test
    void restore_invalidRollbackState_throwsAssertionError() {
        TaskList tasks = new TaskList(List.of(new Todo("only")));

        assertThrows(AssertionError.class, () -> tasks.restore(0, new Todo("restored")));
        assertThrows(AssertionError.class, () -> tasks.restore(3, new Todo("restored")));
        assertThrows(AssertionError.class, () -> tasks.restore(1, null));
    }

    @Test
    void get_outOfRangeNumber_throwsHelpfulError() {
        TaskList tasks = new TaskList(List.of(new Todo("only")));

        OrbitException zeroError = assertThrows(OrbitException.class, () -> tasks.get(0));
        OrbitException largeError = assertThrows(OrbitException.class, () -> tasks.get(2));

        assertEquals("Task number 0 is out of range.", zeroError.getMessage());
        assertEquals("Task number 2 is out of range.", largeError.getMessage());
    }

    @Test
    void find_matchingDescriptions_returnsOrderedReadOnlyNonMutatingMatches() {
        Todo first = new Todo("read book");
        Deadline second = new Deadline("return book", LocalDate.of(2026, 9, 4));
        Event unrelated = new Event("study group", LocalDate.of(2026, 9, 5),
                LocalDate.of(2026, 9, 6));
        TaskList tasks = new TaskList(List.of(first, second, first, unrelated));
        List<Task> originalTasks = List.copyOf(tasks.asList());

        List<Task> matches = tasks.find("book");

        assertEquals(List.of(first, second, first), matches);
        assertEquals(originalTasks, tasks.asList());
        assertThrows(UnsupportedOperationException.class, () -> matches.add(unrelated));
    }

    @Test
    void find_noMatchingDescription_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("missing"));
    }

    @Test
    void sortChronologically_mixedTasks_ordersDatesStablyAndPutsTodosLast() {
        Todo firstTodo = new Todo("first todo");
        Deadline lateDeadline = new Deadline("later", LocalDate.of(2026, 9, 9));
        Event earlyEvent = new Event("early event", LocalDate.of(2026, 9, 2),
                LocalDate.of(2026, 9, 3));
        Deadline sameDayDeadline = new Deadline("same day", LocalDate.of(2026, 9, 2));
        Todo secondTodo = new Todo("second todo");
        lateDeadline.markAsDone();
        TaskList tasks = new TaskList(List.of(firstTodo, lateDeadline, earlyEvent,
                sameDayDeadline, secondTodo));

        tasks.sortChronologically();

        assertEquals(List.of(earlyEvent, sameDayDeadline, lateDeadline, firstTodo, secondTodo),
                tasks.asList());
        assertTrue(lateDeadline.isDone());
    }

    @Test
    void sortChronologically_emptyAndSingleTaskLists_remainValid() {
        TaskList emptyTasks = new TaskList();
        Todo onlyTask = new Todo("only");
        TaskList singleTask = new TaskList(List.of(onlyTask));

        emptyTasks.sortChronologically();
        singleTask.sortChronologically();

        assertEquals(List.of(), emptyTasks.asList());
        assertEquals(List.of(onlyTask), singleTask.asList());
    }
}

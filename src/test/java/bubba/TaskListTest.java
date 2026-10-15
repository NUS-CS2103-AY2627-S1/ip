package bubba;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Checks deletion boundaries, ordering and preservation of tasks after invalid requests.
 */
public class TaskListTest {
    @Test
    public void delete_validNumber_returnsTaskAndPreservesRemainingOrder() throws BubbaException {
        for (int number = 1; number <= 3; number++) {
            Task first = new Todo("first");
            Task second = new Todo("second");
            Task third = new Todo("third");
            List<Task> originalTasks = List.of(first, second, third);
            TaskList tasks = new TaskList(originalTasks);

            assertSame(originalTasks.get(number - 1), tasks.delete(number));
            assertEquals(2, tasks.size());
            List<Task> expectedTasks = new ArrayList<>(originalTasks);
            expectedTasks.remove(number - 1);
            assertEquals(expectedTasks, tasks.toList());
        }
    }

    @Test
    public void delete_onlyTask_leavesEmptyList() throws BubbaException {
        Task task = new Todo("only task");
        TaskList tasks = new TaskList(List.of(task));
        assertSame(task, tasks.delete(1));
        assertEquals(0, tasks.size());
    }

    @Test
    public void delete_outOfRange_throwsWithoutChangingList() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        for (int number : new int[] {-1, 0, 3, Integer.MAX_VALUE}) {
            assertThrows(BubbaException.class, () -> tasks.delete(number));
            assertEquals(List.of(first, second), tasks.toList());
        }
    }

    @Test
    public void delete_emptyList_throwsException() {
        TaskList tasks = new TaskList();
        assertThrows(BubbaException.class, () -> tasks.delete(1));
        assertEquals(0, tasks.size());
    }

    @Test
    public void find_matchingKeyword_returnsMatchesInOriginalOrder() {
        Task readBook = new Todo("read book");
        Task exercise = new Todo("exercise");
        Task returnBook = new Todo("return BOOK to library");
        TaskList tasks = new TaskList(List.of(readBook, exercise, returnBook));

        assertEquals(List.of(readBook, returnBook), tasks.find("book"));
        assertEquals(3, tasks.size());
    }

    @Test
    public void find_phraseAndDifferentCase_matchesDescriptionOnly() {
        Task matchingTask = new Todo("Read The Book");
        Task nonMatchingTask = new Todo("read notes");
        TaskList tasks = new TaskList(List.of(matchingTask, nonMatchingTask));

        assertEquals(List.of(matchingTask), tasks.find("the book"));
        assertEquals(List.of(), tasks.find("deadline"));
    }
}

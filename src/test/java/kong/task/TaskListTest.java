package kong.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void getTasksOnDateReturnsOnlyMatchingDeadlineAndEvent() {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        Task matchingEvent = new Event("conference", LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16"));
        Task nonMatchingEvent = new Event("holiday", LocalDate.parse("2019-10-20"), LocalDate.parse("2019-10-21"));
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(matchingEvent);
        tasks.add(nonMatchingEvent);

        assertEquals(2, tasks.getTasksOnDate(LocalDate.parse("2019-10-15")).size());
        assertEquals(matchingEvent, tasks.getTasksOnDate(LocalDate.parse("2019-10-15")).get(0));
        assertEquals(deadline, tasks.getTasksOnDate(LocalDate.parse("2019-10-15")).get(1));
        assertTrue(tasks.getTasksOnDate(LocalDate.parse("2019-10-30")).isEmpty());
    }

    @Test
    void addAndRemoveStoreAndRemoveTasksInOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");

        assertTrue(tasks.isEmpty());
        tasks.add(firstTask);
        tasks.add(secondTask);

        assertEquals(2, tasks.size());
        assertEquals(firstTask, tasks.get(0));
        assertEquals(firstTask, tasks.remove(0));
        assertEquals(secondTask, tasks.get(0));
        assertFalse(tasks.isEmpty());
    }

    @Test
    void addRejectsNullTask() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    void findTasks_descriptionsWithDifferentCasing_returnsMatchesInListOrder() {
        TaskList tasks = new TaskList();
        Task firstMatch = new Todo("read Book");
        Task nonMatch = new Event("project meeting", LocalDate.parse("2019-10-14"),
                LocalDate.parse("2019-10-16"));
        Task secondMatch = new Deadline("return book", LocalDate.parse("2019-10-15"));
        tasks.add(firstMatch);
        tasks.add(nonMatch);
        tasks.add(secondMatch);

        assertEquals(2, tasks.findTasks("BOOK").size());
        assertEquals(secondMatch, tasks.findTasks("BOOK").get(0));
        assertEquals(firstMatch, tasks.findTasks("BOOK").get(1));
        assertTrue(tasks.findTasks("movie").isEmpty());
    }

    @Test
    void add_mixedTasks_insertsDatedTasksChronologicallyBeforeTodosAndPreservesTies() {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        Task laterDeadline = new Deadline("submit report", LocalDate.parse("2019-10-20"));
        Task event = new Event("conference", LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16"));
        Task earlierDeadline = new Deadline("return book", LocalDate.parse("2019-10-14"));
        tasks.add(todo);
        tasks.add(laterDeadline);
        tasks.add(event);
        tasks.add(earlierDeadline);

        assertEquals(event, tasks.get(0));
        assertEquals(earlierDeadline, tasks.get(1));
        assertEquals(laterDeadline, tasks.get(2));
        assertEquals(todo, tasks.get(3));
    }

    @Test
    void hasDuplicateDetectsDuplicateTasksOfSameTypeAndDetails() {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        Task event = new Event("conference", LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16"));

        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        assertTrue(tasks.hasDuplicate(new Todo("read book")));
        assertTrue(tasks.hasDuplicate(new Todo("READ BOOK")));
        assertTrue(tasks.hasDuplicate(new Deadline("Return Book", LocalDate.parse("2019-10-15"))));
        assertTrue(tasks.hasDuplicate(new Event("CONFERENCE", LocalDate.parse("2019-10-14"),
                LocalDate.parse("2019-10-16"))));

        assertFalse(tasks.hasDuplicate(new Todo("other book")));
        assertFalse(tasks.hasDuplicate(new Deadline("return book", LocalDate.parse("2019-10-16"))));
        assertFalse(tasks.hasDuplicate(new Event("conference", LocalDate.parse("2019-10-14"),
                LocalDate.parse("2019-10-15"))));
    }

    @Test
    void constructorFiltersOutDuplicateTasks() {
        java.util.ArrayList<Task> taskList = new java.util.ArrayList<>();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("READ BOOK"));
        taskList.add(new Deadline("return book", LocalDate.parse("2019-10-15")));
        taskList.add(new Deadline("return book", LocalDate.parse("2019-10-15")));

        TaskList tasks = new TaskList(taskList);

        assertEquals(2, tasks.size());
    }

    @Test
    void constructor_nullCollectionOrNullElements_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));

        java.util.ArrayList<Task> listWithNull = new java.util.ArrayList<>();
        listWithNull.add(null);
        assertThrows(AssertionError.class, () -> new TaskList(listWithNull));
    }

    @Test
    void hasDuplicate_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.hasDuplicate(null));
    }

    @Test
    void remove_outOfBoundsIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(0));
    }

    @Test
    void asList_returnsUnderlyingCollection() {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        tasks.add(todo);

        assertEquals(1, tasks.asList().size());
        assertEquals(todo, tasks.asList().get(0));
    }
}

package epi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {

    @Test
    void copy_mixedTasks_preservesDataButIsolatesMutations() {
        TaskList original = new TaskList();
        original.add(new Task("generic"));
        original.add(new Todo("book"));
        original.add(new Deadline("report", "2020-02-29 1200"));
        original.add(new Event("meeting", "2020-02-29 1200", "2020-02-29 1300"));
        original.get(1).markAsDone();
        TaskList copy = original.copy();

        assertEquals(original.size(), copy.size());
        for (int i = 0; i < original.size(); i++) {
            assertNotSame(original.get(i), copy.get(i));
            assertEquals(original.get(i).getClass(), copy.get(i).getClass());
            assertEquals(original.get(i).toFileFormat(), copy.get(i).toFileFormat());
            String originalStatus = original.get(i).getStatusIcon();
            copy.get(i).markAsDone();
            copy.get(i).markAsUndone();
            assertEquals(originalStatus, original.get(i).getStatusIcon());
        }
        copy.delete(0);
        copy.add(new Todo("new"));
        assertEquals("[ ] generic", original.get(0).toString());
        assertEquals(4, original.size());
    }

    @Test
    void copy_emptyList_returnsIndependentEmptyList() {
        TaskList original = new TaskList();
        TaskList copy = original.copy();
        copy.add(new Todo("new"));
        assertEquals(0, original.size());
    }

    @Test
    void constructor_newTaskList_hasZeroTasks() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
    }

    @Test
    void add_singleTask_increasesSizeAndMakesTaskRetrievable() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    void add_multipleTasks_preservesInsertionOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");

        tasks.add(firstTask);
        tasks.add(secondTask);

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    void iterator_tasksAreReturnedInInsertionOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Iterator<Task> iterator = tasks.iterator();

        assertSame(firstTask, iterator.next());
        assertSame(secondTask, iterator.next());
    }

    @Test
    void delete_singleTask_taskListBecomesEmpty() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.delete(0);

        assertEquals(0, tasks.size());
    }

    @Test
    void delete_firstTask_removesFirstTaskAndPreservesOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy book"));

        tasks.delete(0);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] return book", tasks.get(0).toString());
        assertEquals("[T][ ] buy book", tasks.get(1).toString());
    }

    @Test
    void delete_middleTask_removesMiddleTaskAndPreservesOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy book"));

        tasks.delete(1);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] buy book", tasks.get(1).toString());
    }

    @Test
    void delete_lastTask_removesLastTaskAndPreservesEarlierTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy book"));

        tasks.delete(2);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] return book", tasks.get(1).toString());
    }

    @Test
    void find_keywordMatchesDescriptionsIgnoringCaseAndPreservingOrder() {
        TaskList tasks = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("buy groceries");
        Task third = new Todo("return book");
        tasks.add(first);
        tasks.add(second);
        tasks.add(third);

        List<Task> matches = tasks.find("BOOK");

        assertEquals(List.of(first, third), matches);
    }

    @Test
    void find_keywordWithNoMatches_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(List.of(), tasks.find("movie"));
    }

    @Test
    void getSortedByDate_emptyList_returnsEmptyList() {
        TaskList tasks = new TaskList();

        assertEquals(List.of(), tasks.getSortedByDate());
        assertEquals(0, tasks.size());
    }

    @Test
    void getSortedByDate_singleTask_returnsSameTask() {
        TaskList tasks = new TaskList();
        Task task = new Deadline("return book", "2019-12-02 1800");
        tasks.add(task);

        assertEquals(List.of(task), tasks.getSortedByDate());
        assertSame(task, tasks.get(0));
    }

    @Test
    void getSortedByDate_deadlines_ordersByYearDateAndTime() {
        TaskList tasks = new TaskList();
        Task nextYear = new Deadline("next year", "2020-01-01 0900");
        Task evening = new Deadline("evening", "2019-12-31 1800");
        Task morning = new Deadline("morning", "2019-12-31 0800");
        Task first = new Deadline("first", "2019-01-01 0000");
        tasks.add(nextYear);
        tasks.add(evening);
        tasks.add(morning);
        tasks.add(first);

        assertEquals(List.of(first, morning, evening, nextYear), tasks.getSortedByDate());
    }

    @Test
    void getSortedByDate_mixedTypes_usesEventStartAndPutsTodosLast() {
        TaskList tasks = new TaskList();
        Task firstTodo = new Todo("notes");
        Task later = new Deadline("report", "2020-01-03 0900");
        Task event = new Event("conference", "2020-01-01 0900", "2020-01-05 1700");
        Task earlier = new Deadline("book", "2020-01-02 0900");
        Task secondTodo = new Todo("shopping");
        tasks.add(firstTodo);
        tasks.add(later);
        tasks.add(event);
        tasks.add(earlier);
        tasks.add(secondTodo);

        assertEquals(List.of(event, earlier, later, firstTodo, secondTodo), tasks.getSortedByDate());
    }

    @Test
    void getSortedByDate_equalDates_preservesOrderAcrossTypes() {
        TaskList tasks = new TaskList();
        Task event = new Event("meeting", "2019-12-02 1400", "2019-12-02 1600");
        Task firstDeadline = new Deadline("return book", "2019-12-02 1400");
        Task secondDeadline = new Deadline("return book", "2019-12-02 1400");
        tasks.add(event);
        tasks.add(firstDeadline);
        tasks.add(secondDeadline);

        assertEquals(List.of(event, firstDeadline, secondDeadline), tasks.getSortedByDate());
    }

    @Test
    void getSortedByDate_onlyUndatedTasks_preservesInsertionOrder() {
        TaskList tasks = new TaskList();
        Task first = new Todo("zebra notes");
        Task second = new Todo("apple notes");
        tasks.add(first);
        tasks.add(second);

        assertEquals(List.of(first, second), tasks.getSortedByDate());
    }

    @Test
    void getSortedByDate_completedTasks_keepsChronologicalOrderAndStatus() {
        TaskList tasks = new TaskList();
        Task pending = new Deadline("later", "2019-12-02 1800");
        Task done = new Deadline("earlier", "2019-12-02 0900");
        done.markAsDone();
        tasks.add(pending);
        tasks.add(done);

        assertEquals(List.of(done, pending), tasks.getSortedByDate());
        assertEquals("X", done.getStatusIcon());
        assertEquals(" ", pending.getStatusIcon());
    }

    @Test
    void getSortedByDate_returnedListIsModified_doesNotChangeOriginalList() {
        TaskList tasks = new TaskList();
        Task first = new Todo("notes");
        Task second = new Deadline("return book", "2019-12-02 1800");
        tasks.add(first);
        tasks.add(second);

        List<Task> sorted = tasks.getSortedByDate();
        assertEquals(List.of(second, first), sorted);
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
        sorted.clear();

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
        assertEquals(List.of(second, first), tasks.getSortedByDate());
    }
}

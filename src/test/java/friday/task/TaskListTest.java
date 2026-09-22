package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests operations on the application's task collection. */
class TaskListTest {

    @Test
    void add_validTask_increasesSize() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));

        assertEquals(1, tasks.size());
    }

    @Test
    void delete_existingTask_removesAndReturnsTask() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        tasks.add(first);
        tasks.add(new Todo("second"));

        Task deleted = tasks.delete(0);

        assertSame(first, deleted);
        assertEquals("[T][ ] second", tasks.get(0).toString());
        assertEquals(1, tasks.size());
    }

    @Test
    void add_nullTask_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> tasks.add(null));
    }

    @Test
    void iterator_removeAttempt_unsupportedOperationExceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        var iterator = tasks.iterator();
        iterator.next();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void find_keywordInMultipleDescriptions_returnsAllMatchesInListOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy groceries"));
        tasks.add(new Todo("return book"));

        var matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[T][ ] return book", matches.get(1).toString());
    }

    @Test
    void find_missingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(0, tasks.find("movie").size());
    }

    @Test
    void sortAlphabetically_mixedCaseDescriptions_sortsFromAToZIgnoringCase() {
        TaskList tasks = createUnsortedTasks();

        tasks.sortAlphabetically();

        assertEquals("[T][ ] apple", tasks.get(0).toString());
        assertEquals("[T][ ] Banana", tasks.get(1).toString());
        assertEquals("[T][ ] cherry", tasks.get(2).toString());
    }

    @Test
    void sortReverseAlphabetically_mixedCaseDescriptions_sortsFromZToAIgnoringCase() {
        TaskList tasks = createUnsortedTasks();

        tasks.sortReverseAlphabetically();

        assertEquals("[T][ ] cherry", tasks.get(0).toString());
        assertEquals("[T][ ] Banana", tasks.get(1).toString());
        assertEquals("[T][ ] apple", tasks.get(2).toString());
    }

    @Test
    void sortByType_mixedTaskTypes_groupsTasksInAlphabeticalTypeOrder() {
        TaskList tasks = new TaskList();
        Todo firstTodo = new Todo("buy milk");
        Todo secondTodo = new Todo("read book");
        tasks.add(firstTodo);
        tasks.add(new Event(new String[] {"meeting", "2pm", "3pm"}));
        tasks.add(new Deadline(new String[] {"submit report", "2026-09-30"}));
        tasks.add(secondTodo);

        tasks.sortByType();

        assertEquals("[D][ ] submit report (by: Sep 30 2026)", tasks.get(0).toString());
        assertEquals("[E][ ] meeting (from: 2pm to: 3pm)", tasks.get(1).toString());
        assertSame(firstTodo, tasks.get(2));
        assertSame(secondTodo, tasks.get(3));
    }

    private TaskList createUnsortedTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("cherry"));
        tasks.add(new Todo("apple"));
        tasks.add(new Todo("Banana"));
        return tasks;
    }
}

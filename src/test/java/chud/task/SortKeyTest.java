package chud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class SortKeyTest {
    @Test
    void fromArgument_recognizedWords_returnsMatchingKey() {
        assertEquals(SortKey.DATE, SortKey.fromArgument("date"));
        assertEquals(SortKey.DESCRIPTION, SortKey.fromArgument("description"));
        assertEquals(SortKey.TYPE, SortKey.fromArgument("type"));
        assertEquals(SortKey.DONE, SortKey.fromArgument("done"));
    }

    @Test
    void fromArgument_unrecognizedWord_returnsNull() {
        assertNull(SortKey.fromArgument("priority"));
    }

    @Test
    void fromArgument_wrongCase_returnsNull() {
        assertNull(SortKey.fromArgument("Date"));
    }

    @Test
    void dateComparator_ascending_ordersByDateWithTodosLast() {
        Todo todo = new Todo("pack bags");
        Deadline later = new Deadline("return book", TaskDateTime.parse("2019-12-10"));
        Deadline earlier = new Deadline("submit report", TaskDateTime.parse("2019-12-01"));
        List<Task> tasks = new ArrayList<>(Arrays.asList(todo, later, earlier));

        tasks.sort(SortKey.DATE.comparator(SortDirection.ASC));

        assertEquals(Arrays.asList(earlier, later, todo), tasks);
    }

    @Test
    void dateComparator_descending_reversesDatedTasksButTodosStayLast() {
        Todo todo = new Todo("pack bags");
        Deadline later = new Deadline("return book", TaskDateTime.parse("2019-12-10"));
        Deadline earlier = new Deadline("submit report", TaskDateTime.parse("2019-12-01"));
        List<Task> tasks = new ArrayList<>(Arrays.asList(todo, earlier, later));

        tasks.sort(SortKey.DATE.comparator(SortDirection.DESC));

        assertEquals(Arrays.asList(later, earlier, todo), tasks);
    }

    @Test
    void descriptionComparator_ascending_ordersAlphabeticallyCaseInsensitively() {
        Todo banana = new Todo("Banana");
        Todo apple = new Todo("apple");
        List<Task> tasks = new ArrayList<>(Arrays.asList(banana, apple));

        tasks.sort(SortKey.DESCRIPTION.comparator(SortDirection.ASC));

        assertEquals(Arrays.asList(apple, banana), tasks);
    }

    @Test
    void descriptionComparator_descending_reversesOrder() {
        Todo banana = new Todo("banana");
        Todo apple = new Todo("apple");
        List<Task> tasks = new ArrayList<>(Arrays.asList(apple, banana));

        tasks.sort(SortKey.DESCRIPTION.comparator(SortDirection.DESC));

        assertEquals(Arrays.asList(banana, apple), tasks);
    }

    @Test
    void typeComparator_ascending_ordersTodoThenDeadlineThenEvent() {
        Event event = new Event("meeting", TaskDateTime.parse("2019-12-02"), TaskDateTime.parse("2019-12-03"));
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02"));
        Todo todo = new Todo("pack bags");
        List<Task> tasks = new ArrayList<>(Arrays.asList(event, deadline, todo));

        tasks.sort(SortKey.TYPE.comparator(SortDirection.ASC));

        assertEquals(Arrays.asList(todo, deadline, event), tasks);
    }

    @Test
    void doneComparator_ascending_ordersNotDoneBeforeDone() {
        Todo done = new Todo("borrow book");
        done.markAsDone();
        Todo notDone = new Todo("return book");
        List<Task> tasks = new ArrayList<>(Arrays.asList(done, notDone));

        tasks.sort(SortKey.DONE.comparator(SortDirection.ASC));

        assertEquals(Arrays.asList(notDone, done), tasks);
    }

    @Test
    void doneComparator_descending_ordersDoneBeforeNotDone() {
        Todo done = new Todo("borrow book");
        done.markAsDone();
        Todo notDone = new Todo("return book");
        List<Task> tasks = new ArrayList<>(Arrays.asList(notDone, done));

        tasks.sort(SortKey.DONE.comparator(SortDirection.DESC));

        assertEquals(Arrays.asList(done, notDone), tasks);
    }
}

package nelson.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task collection operations in {@link TaskList}. */
public class TaskListTest {
    @Test
    public void newTaskList_hasZeroTasks() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
    }

    @Test
    public void addTask_storesTaskAndUpdatesSize() {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
    }

    @Test
    public void addMultipleTasks_preservesInsertionOrder() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");
        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    public void removeTask_returnsRemovedTaskAndUpdatesSize() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");
        taskList.add(first);
        taskList.add(second);

        Task removed = taskList.remove(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    public void clearTaskList_removesAllTasks() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("first"));
        taskList.add(new Todo("second"));

        taskList.clear();

        assertEquals(0, taskList.size());
    }

    @Test
    public void constructFromIterable_copiesTasksInOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");

        TaskList taskList = new TaskList(List.of(first, second));

        assertIterableEquals(List.of(first, second), taskList);
    }

    @Test
    public void getInvalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
    }

    @Test
    public void removeInvalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(1));
    }

    @Test
    public void findMatchingKeyword_returnsCaseInsensitiveMatchesInOrder() {
        TaskList taskList = new TaskList();
        Task first = new Todo("Read a book");
        Task second = new Todo("Attend class");
        Task third = new Todo("Return the BOOK");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        List<Task> matches = taskList.find("book");

        assertIterableEquals(List.of(first, third), matches);
        matches.remove(first);
        assertIterableEquals(List.of(first, second, third), taskList);
    }

    @Test
    public void findNoMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read a book"));

        assertEquals(List.of(), taskList.find("meeting"));
    }

    @Test
    public void sortByDescription_mixedCase_ordersTasksIgnoringCase() {
        TaskList taskList = new TaskList();
        Task first = new Todo("write report");
        Task second = new Todo("Attend lecture");
        Task third = new Todo("buy book");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        taskList.sortByDescription();

        assertIterableEquals(List.of(second, third, first), taskList);
    }

    @Test
    public void sortByDescription_sameIgnoringCase_preservesInsertionOrder() {
        Task first = new Todo("Read book");
        Task second = new Todo("read BOOK");
        TaskList taskList = new TaskList(List.of(first, second));

        taskList.sortByDescription();

        assertIterableEquals(List.of(first, second), taskList);
    }
}

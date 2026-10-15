package jeff.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void constructor_noArgs_startsEmpty() {
        TaskList taskList = new TaskList();
        assertEquals(0, taskList.size());
    }

    @Test
    public void constructor_withTasks_copiesGivenList() {
        List<Task> initial = new ArrayList<>();
        initial.add(new Todo("read book"));
        TaskList taskList = new TaskList(initial);

        initial.add(new Todo("write essay"));

        assertEquals(1, taskList.size());
    }

    @Test
    public void add_increasesSizeAndStoresTaskAtEnd() {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
    }

    @Test
    public void add_multipleTasks_addsAllInOrder() {
        TaskList taskList = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("write essay");

        taskList.add(first, second);

        assertEquals(List.of(first, second), taskList.getTasks());
    }

    @Test
    public void add_noTasks_leavesListUnchanged() {
        TaskList taskList = new TaskList();

        taskList.add();

        assertEquals(0, taskList.size());
    }

    @Test
    public void remove_returnsRemovedTaskAndDecreasesSize() {
        TaskList taskList = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("write essay");
        taskList.add(first, second);

        Task removed = taskList.remove(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    public void getTasks_reflectsAddsAndRemovesInOrder() {
        TaskList taskList = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("write essay");
        taskList.add(first, second);

        List<Task> tasks = taskList.getTasks();

        assertEquals(List.of(first, second), tasks);
    }

    @Test
    public void find_keywordInSomeDescriptions_returnsOnlyMatchesInOrder() {
        TaskList taskList = new TaskList();
        Task readBook = new Todo("read book");
        Task writeEssay = new Todo("write essay");
        Task returnBook = new Todo("return book");
        taskList.add(readBook, writeEssay, returnBook);

        List<Task> matches = taskList.find("book");

        assertEquals(List.of(readBook, returnBook), matches);
    }

    @Test
    public void find_noMatches_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertEquals(List.of(), taskList.find("essay"));
    }

    @Test
    public void sortByDate_mixOfTaskTypes_sortsByDateThenKeepsUndatedTasksInOriginalOrder() {
        TaskList taskList = new TaskList();
        Task lateTodo = new Todo("z todo");
        Task lateDeadline = new Deadline("late deadline", "2025-05-01");
        Task earlyTodo = new Todo("a todo");
        Task earlyEvent = new Event("early event", "2025-01-01", "2025-01-05");
        Task midDeadline = new Deadline("mid deadline", "2025-02-01");
        taskList.add(lateTodo, lateDeadline, earlyTodo, earlyEvent, midDeadline);

        taskList.sortByDate();

        assertEquals(List.of(earlyEvent, midDeadline, lateDeadline, lateTodo, earlyTodo), taskList.getTasks());
    }

    @Test
    public void sortByDate_noDatedTasks_keepsOriginalOrder() {
        TaskList taskList = new TaskList();
        Task first = new Todo("z todo");
        Task second = new Todo("a todo");
        taskList.add(first, second);

        taskList.sortByDate();

        assertEquals(List.of(first, second), taskList.getTasks());
    }

    @Test
    public void sortByDate_tiedDates_keepsOriginalRelativeOrder() {
        TaskList taskList = new TaskList();
        Task firstDeadline = new Deadline("first added", "2025-06-01");
        Task secondDeadline = new Deadline("second added", "2025-06-01");
        taskList.add(secondDeadline, firstDeadline);

        taskList.sortByDate();

        assertEquals(List.of(secondDeadline, firstDeadline), taskList.getTasks());
    }
}

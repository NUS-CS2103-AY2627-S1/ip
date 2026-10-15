package probe.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import probe.ProbeException;

class TaskListTest {

    @Test
    void addAndSizeTrackTasks() {
        TaskList taskList = new TaskList();

        taskList.add(new Todo("Read a book"));
        taskList.add(new Todo("Write notes"));

        assertEquals(2, taskList.size());
    }

    @Test
    void getReturnsTaskUsingOneBasedNumber() throws ProbeException {
        Task first = new Todo("First task");
        Task second = new Todo("Second task");
        TaskList taskList = new TaskList(List.of(first, second));

        assertEquals(first, taskList.get(1));
        assertEquals(second, taskList.get(2));
    }

    @Test
    void deleteRemovesAndReturnsTask() throws ProbeException {
        Task task = new Todo("Remove me");
        TaskList taskList = new TaskList(List.of(task));

        Task removed = taskList.delete(1);

        assertEquals(task, removed);
        assertEquals(0, taskList.size());
    }

    @Test
    void getRejectsInvalidTaskNumbers() {
        TaskList taskList = new TaskList(List.of(new Todo("Only task")));

        assertThrows(ProbeException.class, () -> taskList.get(0));
        assertThrows(ProbeException.class, () -> taskList.get(2));
    }

    @Test
    void deleteRejectsInvalidTaskNumbers() {
        TaskList taskList = new TaskList(List.of(new Todo("Only task")));

        assertThrows(ProbeException.class, () -> taskList.delete(0));
        assertThrows(ProbeException.class, () -> taskList.delete(2));
    }

    @Test
    void searchReturnsTasksContainingKeyword() {
        TaskList taskList = new TaskList(List.of(
                new Todo("Read a book"),
                new Todo("Write code"),
                new Todo("Book a ticket")));

        TaskList matches = taskList.search("book");

        assertEquals(2, matches.size());
        assertEquals("Read a book", matches.asList().get(0).getDescription());
        assertEquals("Book a ticket", matches.asList().get(1).getDescription());
    }

    @Test
    void searchIsCaseInsensitive() {
        TaskList taskList = new TaskList(List.of(new Todo("Team Meeting")));

        TaskList matches = taskList.search("meeting");

        assertEquals(1, matches.size());
    }

    @Test
    void searchReturnsEmptyListWhenThereAreNoMatches() {
        TaskList taskList = new TaskList(List.of(new Todo("Read a book")));

        TaskList matches = taskList.search("exercise");

        assertEquals(0, matches.size());
    }
}

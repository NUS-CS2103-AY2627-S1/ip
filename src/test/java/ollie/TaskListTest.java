package ollie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void find_mixedTaskTypesAndCase_returnsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read Book"),
                new Deadline("return book", LocalDate.of(2019, 12, 2)),
                new Event("project meeting", LocalDate.of(2019, 12, 3),
                        LocalDate.of(2019, 12, 5))));

        TaskList matchingTasks = tasks.find("BOOK");

        assertEquals(2, matchingTasks.size());
        assertEquals("Read Book", matchingTasks.get(0).getDescription());
        assertEquals("return book", matchingTasks.get(1).getDescription());
    }

    @Test
    public void find_noMatchingTasks_returnsEmptyTaskList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        TaskList matchingTasks = tasks.find("meeting");

        assertEquals(0, matchingTasks.size());
    }

    @Test
    public void mark_invalidIndex_throwsAssertionError() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.mark(1));
    }

    @Test
    public void add_duplicateTask_throwsOllieException() throws OllieException {
        TaskList tasks = new TaskList(List.of(new Todo("Read  Book")));

        assertThrows(OllieException.class, () -> tasks.add(new Todo("read book")));
        tasks.add(new Deadline("read book", LocalDate.of(2019, 12, 2)));
        Deadline duplicateDeadline = new Deadline("READ BOOK", LocalDate.of(2019, 12, 2));
        assertThrows(OllieException.class, () -> tasks.add(duplicateDeadline));

        assertEquals(2, tasks.size());
    }
}

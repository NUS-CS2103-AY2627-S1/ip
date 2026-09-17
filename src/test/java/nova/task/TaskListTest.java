package nova.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection behavior where ordering and date selection are significant.
 */
public class TaskListTest {
    @Test
    public void findTasksOn_matchingDate_returnsScheduledTasksWithOriginalNumbers() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book",
                LocalDateTime.of(2019, 12, 2, 18, 0), true);
        Event event = new Event("conference",
                LocalDateTime.of(2019, 12, 1, 9, 0), true,
                LocalDateTime.of(2019, 12, 3, 17, 0), true);
        Deadline laterDeadline = new Deadline("submit report",
                LocalDateTime.of(2019, 12, 4, 0, 0), false);
        TaskList tasks = new TaskList(List.of(todo, deadline, event, laterDeadline));

        List<TaskList.NumberedTask> result = tasks.findTasksOn(LocalDate.of(2019, 12, 2));

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).number());
        assertSame(deadline, result.get(0).task());
        assertEquals(3, result.get(1).number());
        assertSame(event, result.get(1).task());
    }

    @Test
    public void findTasksOn_eventBoundaryDates_includesStartAndEnd() {
        Event event = new Event("conference",
                LocalDateTime.of(2019, 12, 1, 9, 0), true,
                LocalDateTime.of(2019, 12, 3, 17, 0), true);
        TaskList tasks = new TaskList(List.of(event));

        assertEquals(1, tasks.findTasksOn(LocalDate.of(2019, 12, 1)).size());
        assertEquals(1, tasks.findTasksOn(LocalDate.of(2019, 12, 3)).size());
    }

    @Test
    public void findTasksOn_outsideScheduledDates_returnsEmptyList() {
        Deadline deadline = new Deadline("return book",
                LocalDateTime.of(2019, 12, 2, 0, 0), false);
        Event event = new Event("conference",
                LocalDateTime.of(2019, 12, 3, 0, 0), false,
                LocalDateTime.of(2019, 12, 4, 0, 0), false);
        TaskList tasks = new TaskList(List.of(new Todo("read book"), deadline, event));

        assertEquals(List.of(), tasks.findTasksOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void findTasksByDescription_matchingKeyword_returnsTasksWithOriginalNumbers() {
        Todo readBook = new Todo("read book");
        Deadline returnBook = new Deadline("return BOOK",
                LocalDateTime.of(2019, 12, 2, 0, 0), false);
        Event meeting = new Event("project meeting",
                LocalDateTime.of(2019, 12, 3, 14, 0), true,
                LocalDateTime.of(2019, 12, 3, 16, 0), true);
        TaskList tasks = new TaskList(List.of(readBook, meeting, returnBook));

        List<TaskList.NumberedTask> result = tasks.findTasksByDescription("book");

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).number());
        assertSame(readBook, result.get(0).task());
        assertEquals(3, result.get(1).number());
        assertSame(returnBook, result.get(1).task());
    }

    @Test
    public void findTasksByDescription_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.findTasksByDescription("meeting"));
    }

    @Test
    public void getTasks_returnedSnapshotCannotMutateTaskList() {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));
        List<Task> snapshot = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("write book")));
        tasks.add(new Todo("write book"));

        assertEquals(1, snapshot.size());
        assertEquals(2, tasks.size());
    }
}

package otaku.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests chronological task ordering. */
public class TaskSorterTest {
    @Test
    public void sortByDate_mixedTasks_ordersDatedTasksStablyBeforeTodos() {
        Task laterDeadline = new Deadline("later", LocalDate.of(2026, 12, 31));
        Task todo = new Todo("undated");
        Task event = new Event("event", LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 3));
        Task sameDateDeadline = new Deadline("same date", LocalDate.of(2026, 1, 2));
        List<Task> tasks = new ArrayList<>(List.of(laterDeadline, todo, event, sameDateDeadline));

        TaskSorter.sortByDate(tasks);

        assertEquals(List.of(event, sameDateDeadline, laterDeadline, todo), tasks);
    }

    @Test
    public void sortByDate_emptyList_remainsEmpty() {
        List<Task> tasks = new ArrayList<>();

        TaskSorter.sortByDate(tasks);

        assertEquals(List.of(), tasks);
    }

    @Test
    public void sortByDate_onlyTodos_preservesOriginalOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        List<Task> tasks = new ArrayList<>(List.of(first, second));

        TaskSorter.sortByDate(tasks);

        assertEquals(List.of(first, second), tasks);
    }
}

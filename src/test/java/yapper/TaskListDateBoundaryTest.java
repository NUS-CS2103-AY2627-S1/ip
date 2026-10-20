package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests date filtering at midnight and after mutations that change displayed task numbers.
 */
public class TaskListDateBoundaryTest {
    @Test
    public void indicesOn_equalEventEndpoints_matchesOnlyThatCalendarDate() {
        LocalDate date = LocalDate.of(2024, 12, 31);
        TaskList tasks = new TaskList(List.of(new Event("instant", date.atStartOfDay(), date.atStartOfDay())));
        assertEquals(List.of(), tasks.indicesOn(date.minusDays(1)));
        assertEquals(List.of(0), tasks.indicesOn(date));
        assertEquals(List.of(), tasks.indicesOn(date.plusDays(1)));
    }

    @Test
    public void indicesOn_yearBoundary_includesBothEventDatesAndExactDeadlineDate() {
        LocalDate december = LocalDate.of(2024, 12, 31);
        LocalDate january = december.plusDays(1);
        TaskList tasks = new TaskList(List.of(new Todo("undated"),
                new Event("countdown", december.atTime(23, 59), january.atStartOfDay()),
                new Deadline("new year", january.atStartOfDay())));
        assertEquals(List.of(1), tasks.indicesOn(december));
        assertEquals(List.of(1, 2), tasks.indicesOn(january));
        assertEquals(List.of(), tasks.indicesOn(january.plusDays(1)));
    }

    @Test
    public void indicesOn_afterDeletionAndAddition_recomputesOriginalIndicesWithoutMutation() {
        LocalDate date = LocalDate.of(2024, 2, 29);
        TaskList tasks = new TaskList(List.of(new Todo("undated"),
                new Deadline("due", date.atTime(18, 0)), new Todo("other")));
        tasks.mark(1);
        tasks.delete(0);
        tasks.add(new Event("meeting", date.atTime(12, 0), date.atTime(13, 0)));
        List<String> before = tasks.asList().stream().map(Task::toDataString).toList();
        assertEquals(List.of(0, 2), tasks.indicesOn(date));
        assertEquals(before, tasks.asList().stream().map(Task::toDataString).toList());
    }
}

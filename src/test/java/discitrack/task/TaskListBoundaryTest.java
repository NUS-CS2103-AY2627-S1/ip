package discitrack.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import discitrack.exception.DisciTrackException;

/**
 * Exercises list positions and inclusive date boundaries with independent fixtures.
 */
public class TaskListBoundaryTest {
    @TestFactory
    public Stream<DynamicTest> get_validPositions_returnsExpectedTask() {
        return IntStream.rangeClosed(1, 3).mapToObj(number -> DynamicTest.dynamicTest("get task " + number, () -> {
            TaskList tasks = threeTasks();
            assertEquals(List.of("first", "middle", "last").get(number - 1), tasks.get(number).getActivity());
            assertEquals(3, tasks.size());
        }));
    }

    @TestFactory
    public Stream<DynamicTest> delete_validPositions_preservesRemainingOrder() {
        return IntStream.rangeClosed(1, 3).mapToObj(number -> DynamicTest.dynamicTest("delete task " + number, () -> {
            TaskList tasks = threeTasks();
            Task removed = tasks.get(number);
            List<Task> expected = tasks.asList().stream().filter(task -> task != removed).toList();
            assertSame(removed, tasks.delete(number));
            assertEquals(expected, tasks.asList());
            assertEquals(2, tasks.size());
            assertSame(expected.get(0), tasks.get(1));
            assertSame(expected.get(1), tasks.get(2));
        }));
    }

    @TestFactory
    public Stream<DynamicTest> invalidPositions_rejectGetAndDelete_withoutMutation() {
        return Stream.of(0, -1, 4, Integer.MAX_VALUE).map(number ->
                DynamicTest.dynamicTest("invalid task number " + number, () -> {
                    TaskList tasks = threeTasks();
                    List<Task> original = List.copyOf(tasks.asList());
                    assertThrows(DisciTrackException.class, () -> tasks.get(number));
                    assertThrows(DisciTrackException.class, () -> tasks.delete(number));
                    assertEquals(original, tasks.asList());
                }));
    }

    @Test
    public void emptyList_getAndDelete_rejectFirstPosition() {
        TaskList tasks = new TaskList();
        assertThrows(DisciTrackException.class, () -> tasks.get(1));
        assertThrows(DisciTrackException.class, () -> tasks.delete(1));
        assertEquals(0, tasks.size());
    }

    @TestFactory
    public Stream<DynamicTest> dateSearch_eventBoundaries_includeOnlyItsDates() {
        LocalDate start = LocalDate.of(2026, 9, 30);
        return IntStream.rangeClosed(-1, 3).mapToObj(offset ->
                DynamicTest.dynamicTest("event date offset " + offset, () -> {
                    TaskList tasks = new TaskList();
                    Event event = new Event("camp", start, start.plusDays(2));
                    tasks.add(event);
                    List<Task> expected = offset >= 0 && offset <= 2 ? List.of(event) : List.of();
                    assertEquals(expected, tasks.findTasksByDate(start.plusDays(offset)));
                }));
    }

    @TestFactory
    public Stream<DynamicTest> dateSearch_sameDayEvent_matchesOnlyThatDay() {
        LocalDate date = LocalDate.of(2026, 9, 30);
        return IntStream.rangeClosed(-1, 1).mapToObj(offset ->
                DynamicTest.dynamicTest("same-day event offset " + offset, () -> {
                    TaskList tasks = new TaskList();
                    Event event = new Event("meeting", date, date);
                    tasks.add(event);
                    assertEquals(offset == 0 ? List.of(event) : List.of(),
                            tasks.findTasksByDate(date.plusDays(offset)));
                }));
    }

    @Test
    public void dateSearch_multipleMatches_preservesOrderAndExcludesUnrelatedTasks() {
        LocalDate date = LocalDate.of(2026, 9, 30);
        TaskList tasks = new TaskList();
        Deadline deadline = new Deadline("report", date);
        Event event = new Event("camp", date.minusDays(1), date.plusDays(1));
        event.markAsDone();
        tasks.add(new Todo("undated"));
        tasks.add(deadline);
        tasks.add(new Deadline("tomorrow", date.plusDays(1)));
        tasks.add(event);
        assertEquals(List.of(deadline, event), tasks.findTasksByDate(date));
    }

    private TaskList threeTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("middle"));
        tasks.add(new Todo("last"));
        return tasks;
    }
}

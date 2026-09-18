package waffles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the task collection operations provided by {@link TaskList}.
 */
class TaskListTest {
    /** Verifies adding, retrieving, and removing tasks preserves list order. */
    @Test
    void taskOperations_addAndRemove_preserveOrder() {
        TaskList taskList = new TaskList(List.of(new Todo("first")));
        Task second = new Todo("second");

        taskList.addTask(second);

        assertEquals(2, taskList.size());
        assertEquals("first", taskList.getTask(0).getDescription());
        assertEquals(second, taskList.removeTask(1));
        assertEquals(1, taskList.size());
    }

    /** Verifies callers cannot mutate the task list through its storage view. */
    @Test
    void asList_mutationAttempt_throwsUnsupportedOperationException() {
        TaskList taskList = new TaskList(List.of(new Todo("keep")));

        assertThrows(UnsupportedOperationException.class,
                () -> taskList.asList().add(new Todo("should fail")));
        assertEquals(1, taskList.size());
    }

    /** Verifies the no-argument constructor creates an empty list. */
    @Test
    void emptyConstructor_createsEmptyList() {
        assertEquals(0, new TaskList().size());
    }

    /** Verifies find matches descriptions case-insensitively and keeps list order. */
    @Test
    void findTasks_caseInsensitiveKeyword_returnsMatchingTasksInOrder() {
        Task first = new Todo("read book");
        Task second = new Todo("book a room");
        Task third = new Todo("clean desk");
        TaskList taskList = new TaskList(List.of(first, second, third));

        assertEquals(List.of(first, second), taskList.findTasks("BOOK"));
        assertEquals(List.of(), taskList.findTasks("spaceship"));
    }

    /** Verifies overlapping events and deadlines are reported as scheduling conflicts. */
    @Test
    void findSchedulingConflicts_overlappingSchedules_areReported() {
        Event existingEvent = new Event("conference", LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 17));
        Deadline existingDeadline = new Deadline("submit report", LocalDate.of(2026, 9, 20));
        TaskList taskList = new TaskList(List.of(existingEvent, existingDeadline));

        Event overlappingEvent = new Event("workshop", LocalDate.of(2026, 9, 17),
                LocalDate.of(2026, 9, 18));
        Deadline overlappingDeadline = new Deadline("send slides", LocalDate.of(2026, 9, 20));

        assertEquals(List.of(existingEvent), taskList.findSchedulingConflicts(overlappingEvent));
        assertEquals(List.of(existingDeadline), taskList.findSchedulingConflicts(overlappingDeadline));
    }

    /** Verifies unscheduled tasks and non-overlapping dates are not reported as conflicts. */
    @Test
    void findSchedulingConflicts_nonOverlappingSchedules_areIgnored() {
        Event existingEvent = new Event("conference", LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 17));
        TaskList taskList = new TaskList(List.of(existingEvent, new Todo("read notes")));

        Event separateEvent = new Event("workshop", LocalDate.of(2026, 9, 18),
                LocalDate.of(2026, 9, 19));
        Todo todo = new Todo("prepare slides");

        assertEquals(List.of(), taskList.findSchedulingConflicts(separateEvent));
        assertEquals(List.of(), taskList.findSchedulingConflicts(todo));
    }
}

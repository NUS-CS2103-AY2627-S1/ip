package kafka.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import kafka.exception.KafkaException;

/**
 * Tests task ordering, status changes, and deletion in a task list.
 */
class TaskListTest {
    @Test
    void isEmptyReflectsWhetherTasksAreStored() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());

        tasks.addTask(new Todo("read book"));

        assertFalse(tasks.isEmpty());
    }

    @Test
    void markAndUnmarkChangeSelectedTask() throws KafkaException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Todo("return book"));

        Task markedTask = tasks.markTask(2);
        assertEquals("[T][X] return book", markedTask.display());

        Task unmarkedTask = tasks.unmarkTask(2);
        assertEquals("[T][ ] return book", unmarkedTask.display());
    }

    @Test
    void unavailableTaskNumberDoesNotChangeExistingTask() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.addTask(task);

        assertThrows(KafkaException.class, () -> tasks.markTask(2));
        assertEquals("[T][ ] read book", task.display());
    }

    @Test
    void deleteTaskReturnsRemovedTaskAndShiftsRemainingTasks() throws KafkaException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first"));
        tasks.addTask(new Deadline("second", "Sunday"));
        tasks.addTask(new Event("third", "Monday", "Tuesday"));

        Task deletedTask = tasks.deleteTask(2);

        assertEquals("[D][ ] second (by: Sunday)", deletedTask.display());
        assertEquals(2, tasks.size());
        Task markedTask = tasks.markTask(2);
        assertEquals("[E][X] third (from: Monday to: Tuesday)", markedTask.display());
    }

    @Test
    void findTasksReturnsCaseInsensitiveMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Deadline("return BOOK", "June 6th"));
        tasks.addTask(new Todo("write essay"));

        List<Task> matches = tasks.findTasks("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).display());
        assertEquals("[D][ ] return BOOK (by: June 6th)", matches.get(1).display());
    }

    @Test
    void renameTaskReturnsSnapshotsAndPreservesOtherDetails() throws KafkaException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Deadline("submit draft", "Sunday"));
        tasks.markTask(1);

        RenameResult result = tasks.renameTask(1, "submit final report");

        assertEquals("[D][X] submit draft (by: Sunday)", result.oldDisplay());
        assertEquals("[D][X] submit final report (by: Sunday)", result.newDisplay());
        assertEquals("D | 1 | submit final report | Sunday",
                tasks.getTasks().get(0).toDataString());
    }

    @Test
    void getTasks_snapshot_isOrderedUnmodifiableAndIndependentOfLaterListChanges() throws KafkaException {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");
        tasks.addTask(first);
        tasks.addTask(second);
        List<Task> snapshot = tasks.getTasks();

        assertEquals(List.of(first, second), snapshot);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("third")));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        tasks.deleteTask(1);
        tasks.addTask(new Todo("third"));
        assertEquals(List.of(first, second), snapshot);
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE})
    void taskOperations_invalidNumber_throwWithoutChangingList(int taskNumber) {
        TaskList tasks = new TaskList();
        Task original = new Todo("original");
        tasks.addTask(original);

        assertThrows(KafkaException.class, () -> tasks.markTask(taskNumber));
        assertThrows(KafkaException.class, () -> tasks.unmarkTask(taskNumber));
        assertThrows(KafkaException.class, () -> tasks.deleteTask(taskNumber));
        assertThrows(KafkaException.class, () -> tasks.renameTask(taskNumber, "changed"));
        assertThrows(KafkaException.class, () -> tasks.snoozeDeadline(taskNumber, "Sunday"));
        assertThrows(KafkaException.class, () -> tasks.snoozeEvent(taskNumber,
                Optional.of("Sunday"), Optional.empty()));

        assertEquals(List.of(original), tasks.getTasks());
        assertEquals("T | 0 | original", original.toDataString());
    }

    @Test
    void taskOperations_emptyList_throwKafkaException() {
        TaskList tasks = new TaskList();

        assertThrows(KafkaException.class, () -> tasks.markTask(1));
        assertThrows(KafkaException.class, () -> tasks.unmarkTask(1));
        assertThrows(KafkaException.class, () -> tasks.deleteTask(1));
        assertThrows(KafkaException.class, () -> tasks.renameTask(1, "changed"));
        assertThrows(KafkaException.class, () -> tasks.snoozeDeadline(1, "Sunday"));
        assertThrows(KafkaException.class, () -> tasks.snoozeEvent(1, Optional.empty(), Optional.of("Sunday")));
        assertTrue(tasks.isEmpty());
    }

    @Test
    void deleteTask_lastTask_returnsOriginalAndEmptiesList() throws KafkaException {
        TaskList tasks = new TaskList();
        Task task = new Todo("only task");
        tasks.addTask(task);

        assertSame(task, tasks.markTask(1));
        assertSame(task, tasks.unmarkTask(1));
        assertSame(task, tasks.deleteTask(1));
        assertEquals(0, tasks.size());
        assertTrue(tasks.isEmpty());
    }

    @Test
    void findTasks_scheduleAndStatus_searchesDisplayedTextWithoutChangingList() {
        TaskList tasks = new TaskList();
        Task deadline = new Deadline("report", "Sunday");
        Task event = new Event("meeting", "Sunday", "Monday");
        deadline.mark();
        tasks.addTask(deadline);
        tasks.addTask(event);

        assertEquals(List.of(deadline, event), tasks.findTasks("SUNDAY"));
        assertEquals(List.of(deadline), tasks.findTasks("[X]"));
        assertTrue(tasks.findTasks("missing").isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> tasks.findTasks("Sunday").clear());
        assertEquals(List.of(deadline, event), tasks.getTasks());
    }

    @Test
    void snoozeDeadline_completedDeadline_returnsSnapshotsAndPreservesOtherTasks() throws KafkaException {
        TaskList tasks = new TaskList();
        Task neighbor = new Todo("neighbor");
        Task deadline = new Deadline("report", "Friday");
        deadline.mark();
        tasks.addTask(neighbor);
        tasks.addTask(deadline);

        SnoozeResult result = tasks.snoozeDeadline(2, "Sunday");

        assertEquals("[D][X] report (by: Friday)", result.oldDisplay());
        assertEquals("[D][X] report (by: Sunday)", result.newDisplay());
        assertEquals("D | 1 | report | Sunday", deadline.toDataString());
        assertEquals("T | 0 | neighbor", neighbor.toDataString());
        assertEquals(List.of(neighbor, deadline), tasks.getTasks());
    }

    @ParameterizedTest
    @CsvSource({"Wednesday,, Wednesday, Tuesday", ",Thursday, Monday, Thursday",
        "Wednesday, Thursday, Wednesday, Thursday"})
    void snoozeEvent_optionalEndpoints_preservesUnchangedDetails(String newFrom, String newTo,
            String expectedFrom, String expectedTo) throws KafkaException {
        TaskList tasks = new TaskList();
        Task event = new Event("meeting", "Monday", "Tuesday");
        event.mark();
        tasks.addTask(event);
        Task neighbor = new Todo("neighbor");
        tasks.addTask(neighbor);

        SnoozeResult result = tasks.snoozeEvent(1, Optional.ofNullable(newFrom), Optional.ofNullable(newTo));

        assertEquals("[E][X] meeting (from: Monday to: Tuesday)", result.oldDisplay());
        assertEquals("[E][X] meeting (from: " + expectedFrom + " to: " + expectedTo + ")", result.newDisplay());
        assertEquals("E | 1 | meeting | " + expectedFrom + " | " + expectedTo, event.toDataString());
        assertEquals(List.of(event, neighbor), tasks.getTasks());
        assertEquals("T | 0 | neighbor", neighbor.toDataString());
    }

    @Test
    void snoozeTask_wrongTaskType_throwsWithoutChangingTasks() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Deadline("report", "Friday"));
        tasks.addTask(new Event("meeting", "Monday", "Tuesday"));
        List<String> originalRecords = tasks.getTasks().stream().map(Task::toDataString).toList();

        for (int taskNumber : List.of(1, 3)) {
            assertThrows(KafkaException.class, () -> tasks.snoozeDeadline(taskNumber, "Sunday"));
        }
        for (int taskNumber : List.of(1, 2)) {
            assertThrows(KafkaException.class, () -> tasks.snoozeEvent(taskNumber, Optional.of("Sunday"),
                    Optional.empty()));
        }

        assertEquals(originalRecords, tasks.getTasks().stream().map(Task::toDataString).toList());
    }

    @Test
    void snoozeEvent_noEndpoints_rejectsBrokenInternalContract() {
        TaskList tasks = new TaskList();
        Task event = new Event("meeting", "Monday", "Tuesday");
        tasks.addTask(event);

        assertThrows(AssertionError.class, () -> tasks.snoozeEvent(1, Optional.empty(), Optional.empty()));
        assertEquals("E | 0 | meeting | Monday | Tuesday", event.toDataString());
    }
}

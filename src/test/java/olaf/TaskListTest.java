package olaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests operations on the in-memory task list.
 */
class TaskListTest {
    @Test
    void find_keywordUsesDifferentCase_matchingTasksReturnedInInsertionOrder() {
        Task firstTask = new Todo("Read Book");
        Task secondTask = new Deadline("return book", LocalDate.of(2026, 9, 1));
        TaskList tasks = new TaskList(List.of(firstTask, new Todo("buy groceries"), secondTask));

        List<TaskMatch> matchingTasks = tasks.find("BOOK");

        assertEquals(List.of(new TaskMatch(1, firstTask), new TaskMatch(3, secondTask)), matchingTasks);
    }

    @Test
    void find_keywordAppearsWithinWord_partialMatchReturned() {
        Task matchingTask = new Todo("organize notebook");
        TaskList tasks = new TaskList(List.of(matchingTask));

        assertEquals(List.of(new TaskMatch(1, matchingTask)), tasks.find("book"));
    }

    @Test
    void find_keywordAppearsOnlyInFormattedDate_noMatchReturned() {
        Task datedTask = new Deadline("submit report", LocalDate.of(2026, 8, 30));
        TaskList tasks = new TaskList(List.of(datedTask));

        assertTrue(tasks.find("Aug").isEmpty());
    }

    @Test
    void find_keywordDoesNotAppear_emptyListReturned() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.find("exercise").isEmpty());
    }

    @Test
    void find_matchingTaskReturned_resultListIsImmutable() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<TaskMatch> matchingTasks = tasks.find("book");

        assertThrows(UnsupportedOperationException.class,
                () -> matchingTasks.add(new TaskMatch(2, new Todo("write book"))));
    }

    @Test
    void copy_allTaskTypes_mutableStateIsIndependent() throws Exception {
        LocalDate date = LocalDate.of(2026, 9, 1);
        TaskList original = new TaskList(List.of(new Todo("read book"), new Deadline("return book", date),
                new Event("meeting", date, date)));
        original.markAsDone(1);
        original.markAsDone(2);
        original.markAsDone(3);
        String originalDisplay = ResponseFormatter.formatTaskList(original);

        TaskList copy = original.copy();

        assertEquals(originalDisplay, ResponseFormatter.formatTaskList(copy));
        for (int index = 0; index < original.size(); index++) {
            assertNotSame(original.getTasks().get(index), copy.getTasks().get(index));
        }
        copy.markAsNotDone(1);
        copy.rescheduleDeadline(2, date.plusDays(1));
        copy.rescheduleEvent(3, date.plusDays(2), date.plusDays(3));
        copy.delete(1);
        copy.add(new Todo("new task"));
        assertEquals(originalDisplay, ResponseFormatter.formatTaskList(original));
    }

    @Test
    void rescheduleDeadline_earlierLaterPastAndUnchangedDates_taskPropertiesPreserved()
            throws InvalidTaskNumberException, TaskRescheduleException {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 1));
        deadline.markAsDone();
        List<Task> originalTasks = List.of(new Todo("read book"), deadline, new Todo("write notes"));
        TaskList tasks = new TaskList(originalTasks);
        LocalDate[] dates = {LocalDate.of(2026, 9, 20), LocalDate.of(2026, 8, 31),
                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1)};

        for (LocalDate date : dates) {
            Task updatedTask = tasks.rescheduleDeadline(2, date);

            assertSame(deadline, updatedTask);
            assertEquals(List.of(date.toString()), updatedTask.getAdditionalStorageFields());
            assertEquals("return book", updatedTask.getDescription());
            assertTrue(updatedTask.isDone());
            assertEquals(originalTasks, tasks.getTasks());
        }
    }

    @Test
    void rescheduleEvent_differentRangesIncludingSameDay_taskPropertiesPreserved()
            throws InvalidTaskNumberException, TaskRescheduleException {
        Event event = new Event("meeting", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2));
        event.markAsDone();
        List<Task> originalTasks = List.of(new Todo("read book"), event, new Todo("write notes"));
        TaskList tasks = new TaskList(originalTasks);
        LocalDate[][] ranges = {
                {LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 22)},
                {LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1)},
                {LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1)}
        };

        for (LocalDate[] range : ranges) {
            Task updatedTask = tasks.rescheduleEvent(2, range[0], range[1]);

            assertSame(event, updatedTask);
            assertEquals(List.of(range[0].toString(), range[1].toString()),
                    updatedTask.getAdditionalStorageFields());
            assertEquals("meeting", updatedTask.getDescription());
            assertTrue(updatedTask.isDone());
            assertEquals(originalTasks, tasks.getTasks());
        }
    }

    @Test
    void rescheduleEvent_endBeforeStart_neitherDateChanged() {
        Event event = new Event("meeting", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2));
        TaskList tasks = new TaskList(List.of(event));

        TaskRescheduleException exception = assertThrows(TaskRescheduleException.class,
                () -> tasks.rescheduleEvent(1, LocalDate.of(2026, 9, 22), LocalDate.of(2026, 9, 20)));

        assertEquals("The event end date must be on or after its start date.", exception.getMessage());
        assertEquals("[E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)", event.toString());
    }

    @Test
    void reschedule_invalidTaskNumbers_taskNumberExceptionThrown() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        for (TaskList tasks : List.of(new TaskList(), new TaskList(List.of(new Todo("read book"))))) {
            List<Task> originalTasks = tasks.getTasks();
            for (int taskNumber : new int[] {-1, 0, tasks.size() + 1, Integer.MAX_VALUE}) {
                assertThrows(InvalidTaskNumberException.class,
                        () -> tasks.rescheduleDeadline(taskNumber, date));
                assertThrows(InvalidTaskNumberException.class,
                        () -> tasks.rescheduleEvent(taskNumber, date, date));
                assertEquals(originalTasks, tasks.getTasks());
            }
        }
    }

    @Test
    void reschedule_wrongTaskTypes_taskSpecificGuidanceReturned() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Deadline("return book", date),
                new Event("meeting", date, date)));

        TaskRescheduleException todoDeadlineError = assertThrows(TaskRescheduleException.class,
                () -> tasks.rescheduleDeadline(1, date));
        TaskRescheduleException todoEventError = assertThrows(TaskRescheduleException.class,
                () -> tasks.rescheduleEvent(1, date, date));
        TaskRescheduleException deadlineError = assertThrows(TaskRescheduleException.class,
                () -> tasks.rescheduleEvent(2, date, date));
        TaskRescheduleException eventError = assertThrows(TaskRescheduleException.class,
                () -> tasks.rescheduleDeadline(3, date));

        assertEquals("ToDos have no dates to reschedule. Use 'list' to choose a deadline or event.",
                todoDeadlineError.getMessage());
        assertEquals(todoDeadlineError.getMessage(), todoEventError.getMessage());
        assertEquals("This task is a deadline. Use 'reschedule <task number> /by <yyyy-MM-dd>'.",
                deadlineError.getMessage());
        assertEquals("This task is an event. "
                + "Use 'reschedule <task number> /from <yyyy-MM-dd> /to <yyyy-MM-dd>'.",
                eventError.getMessage());
    }
}

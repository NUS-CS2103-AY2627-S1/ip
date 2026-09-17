package yachiyo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import yachiyo.exception.YachiyoException;

/**
 * Tests collection operations and validation performed by {@link TaskList}.
 */
public class TaskListTest {
    private static final LocalDate MATCHING_DATE = LocalDate.of(2026, 8, 20);

    @Test
    public void constructor_noTasks_emptyListCreated() {
        TaskList taskList = new TaskList();

        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
    }

    @Test
    public void constructor_varargsTasks_tasksCopied() {
        Task firstTask = new ToDo("Read book");
        Task secondTask = new ToDo("Return book");
        Task[] initialTasks = {firstTask, secondTask};

        TaskList taskList = new TaskList(initialTasks);
        initialTasks[0] = new ToDo("Buy book");

        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    @Test
    public void constructor_initialTasksModifiedExternally_listUnaffected() {
        Task firstTask = new ToDo("Read book");
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(firstTask);
        TaskList taskList = new TaskList(initialTasks);

        initialTasks.add(new ToDo("Return book"));

        assertEquals(List.of(firstTask), taskList.getTasks());
    }

    @Test
    public void constructor_nullTaskCollection_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new TaskList((List<Task>) null));
    }

    @Test
    public void constructor_nullTaskInCollection_assertionErrorThrown() {
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(initialTasks));
    }

    @Test
    public void add_taskAddedToEnd() {
        Task firstTask = new ToDo("Read book");
        Task secondTask = new ToDo("Return book");
        TaskList taskList = new TaskList();

        taskList.add(firstTask);
        taskList.add(secondTask);

        assertFalse(taskList.isEmpty());
        assertEquals(2, taskList.size());
        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    @Test
    public void add_nullTask_assertionErrorThrown() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.add(null));
    }

    @Test
    public void get_firstTask_firstTaskReturned() throws YachiyoException {
        Task firstTask = new ToDo("Read book");
        TaskList taskList = new TaskList(List.of(firstTask, new ToDo("Return book")));

        assertSame(firstTask, taskList.get(1));
    }

    @Test
    public void get_lastTask_lastTaskReturned() throws YachiyoException {
        Task lastTask = new ToDo("Return book");
        TaskList taskList = new TaskList(List.of(new ToDo("Read book"), lastTask));

        assertSame(lastTask, taskList.get(2));
    }

    @Test
    public void get_zeroTaskNumber_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("Read book")));

        assertThrows(YachiyoException.class, () -> taskList.get(0));
    }

    @Test
    public void get_negativeTaskNumber_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("Read book")));

        assertThrows(YachiyoException.class, () -> taskList.get(-1));
    }

    @Test
    public void get_taskNumberAboveSize_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("Read book")));

        assertThrows(YachiyoException.class, () -> taskList.get(2));
    }

    @Test
    public void replace_validTaskNumber_onlySelectedTaskReplaced() throws YachiyoException {
        Task firstTask = new ToDo("Read book");
        Task replacementTask = new ToDo("Submit report");
        Task lastTask = new ToDo("Return book");
        TaskList taskList = new TaskList(List.of(firstTask, new ToDo("Draft report"), lastTask));

        taskList.replace(2, replacementTask);

        assertEquals(List.of(firstTask, replacementTask, lastTask), taskList.getTasks());
    }

    @Test
    public void replace_taskNumberOutsideList_exceptionThrownAndListUnchanged() {
        Task originalTask = new ToDo("Read book");
        TaskList taskList = new TaskList(List.of(originalTask));

        assertThrows(YachiyoException.class, () ->
                taskList.replace(2, new ToDo("Return book")));
        assertEquals(List.of(originalTask), taskList.getTasks());
    }

    @Test
    public void replace_nullTask_assertionErrorThrownAndListUnchanged() {
        Task originalTask = new ToDo("Read book");
        TaskList taskList = new TaskList(List.of(originalTask));

        assertThrows(AssertionError.class, () -> taskList.replace(1, null));
        assertEquals(List.of(originalTask), taskList.getTasks());
    }

    @Test
    public void delete_validTaskNumber_taskRemovedAndReturned() throws YachiyoException {
        Task firstTask = new ToDo("Read book");
        Task deletedTask = new ToDo("Return book");
        Task lastTask = new ToDo("Buy book");
        TaskList taskList = new TaskList(List.of(firstTask, deletedTask, lastTask));

        Task result = taskList.delete(2);

        assertSame(deletedTask, result);
        assertEquals(List.of(firstTask, lastTask), taskList.getTasks());
        assertEquals(2, taskList.size());
    }

    @Test
    public void delete_taskNumberBelowRange_exceptionThrown() {
        Task firstTask = new ToDo("Read book");
        TaskList taskList = new TaskList(List.of(firstTask));

        assertThrows(YachiyoException.class, () -> taskList.delete(0));
        assertEquals(List.of(firstTask), taskList.getTasks());
    }

    @Test
    public void delete_taskNumberAboveRange_exceptionThrown() {
        Task firstTask = new ToDo("Read book");
        TaskList taskList = new TaskList(List.of(firstTask));

        assertThrows(YachiyoException.class, () -> taskList.delete(2));
        assertEquals(List.of(firstTask), taskList.getTasks());
    }

    @Test
    public void getRemainingTaskCount_emptyList_zeroReturned() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.getRemainingTaskCount());
    }

    @Test
    public void getRemainingTaskCount_mixedCompletionStates_incompleteCountReturned() {
        Task completedTask = new ToDo("Read book");
        completedTask.markAsDone();
        TaskList taskList = new TaskList(List.of(
                completedTask,
                new ToDo("Return book"),
                new ToDo("Buy book")
        ));

        assertEquals(2, taskList.getRemainingTaskCount());
    }

    @Test
    public void findTasks_noMatchingDescriptions_emptyListReturned() {
        TaskList taskList = new TaskList(List.of(
                new ToDo("Submit report"),
                new ToDo("Attend meeting")
        ));

        assertTrue(taskList.findTasks("book").isEmpty());
    }

    @Test
    public void findTasks_caseInsensitiveSubstring_originalTaskNumbersRetained() {
        Task firstMatchingTask = new ToDo("Read textbook");
        Task secondMatchingTask = new ToDo("Return TEXTBOOK");
        TaskList taskList = new TaskList(List.of(
                firstMatchingTask,
                new ToDo("Submit report"),
                secondMatchingTask
        ));

        assertEquals(
                List.of(
                        new NumberedTask(1, firstMatchingTask),
                        new NumberedTask(3, secondMatchingTask)
                ),
                taskList.findTasks("book")
        );
    }

    @Test
    public void getTasksOnDate_noMatchingTasks_emptyListReturned() {
        Deadline deadline = new Deadline(
                "Submit report",
                LocalDateTime.of(2026, 8, 21, 17, 0)
        );
        TaskList taskList = new TaskList(List.of(new ToDo("Read book"), deadline));

        assertTrue(taskList.getTasksOnDate(MATCHING_DATE).isEmpty());
    }

    @Test
    public void getTasksOnDate_matchingDeadlinesAndEvents_matchingTasksReturned() {
        Deadline deadline = new Deadline(
                "Submit report",
                MATCHING_DATE.atTime(17, 0)
        );
        Event event = new Event(
                "Orientation camp",
                MATCHING_DATE.minusDays(1).atTime(9, 0),
                MATCHING_DATE.plusDays(1).atTime(17, 0)
        );
        TaskList taskList = new TaskList(List.of(deadline, event));

        assertEquals(
                List.of(new NumberedTask(1, deadline), new NumberedTask(2, event)),
                taskList.getTasksOnDate(MATCHING_DATE)
        );
    }

    @Test
    public void getTasksOnDate_tasksFiltered_originalTaskNumbersRetained() {
        Deadline matchingDeadline = new Deadline(
                "Submit report",
                MATCHING_DATE.atTime(17, 0)
        );
        Deadline nonMatchingDeadline = new Deadline(
                "Pay bill",
                MATCHING_DATE.plusDays(1).atTime(12, 0)
        );
        Event matchingEvent = new Event(
                "Orientation camp",
                MATCHING_DATE.atTime(9, 0),
                MATCHING_DATE.atTime(17, 0)
        );
        TaskList taskList = new TaskList(List.of(
                new ToDo("Read book"),
                matchingDeadline,
                nonMatchingDeadline,
                matchingEvent
        ));

        assertEquals(
                List.of(
                        new NumberedTask(2, matchingDeadline),
                        new NumberedTask(4, matchingEvent)
                ),
                taskList.getTasksOnDate(MATCHING_DATE)
        );
    }

    @Test
    public void getTasks_tasksPresent_orderedSnapshotReturned() {
        Task firstTask = new ToDo("Read book");
        Task secondTask = new ToDo("Return book");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    @Test
    public void getTasks_returnedListModified_unsupportedOperationThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("Read book")));
        List<Task> tasks = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> tasks.add(new ToDo("Return book")));
    }

    @Test
    public void getTasks_listModifiedAfterSnapshot_snapshotUnaffected() {
        Task firstTask = new ToDo("Read book");
        TaskList taskList = new TaskList(List.of(firstTask));
        List<Task> copiedTasks = taskList.getTasks();

        taskList.add(new ToDo("Return book"));

        assertEquals(List.of(firstTask), copiedTasks);
        assertEquals(2, taskList.size());
    }
}

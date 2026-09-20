package loki.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import loki.exception.LokiExceptions;
import loki.parser.Parser;
import loki.parser.TaskUpdate;

/** Tests task storage, lookup, mutation, and read-only access in {@link TaskList}. */
class TaskListTest {
    private TaskList taskList;
    private Task firstTask;
    private Task secondTask;
    private Task thirdTask;

    @BeforeEach
    void setUp() {
        taskList = new TaskList();
        firstTask = new ToDo("first task");
        secondTask = new ToDo("second task");
        thirdTask = new ToDo("third task");
        taskList.add(firstTask);
        taskList.add(secondTask);
        taskList.add(thirdTask);
    }

    @Test
    void add_task_appendsTaskAndUpdatesListState() throws LokiExceptions {
        TaskList emptyTaskList = new TaskList();
        Task task = new ToDo("new task");

        emptyTaskList.add(task);

        assertEquals(1, emptyTaskList.size());
        assertFalse(emptyTaskList.isEmpty());
        assertSame(task, emptyTaskList.get(1));
    }

    @Test
    void add_nullTask_throwsIllegalArgumentExceptionAndLeavesListUnchanged() {
        int originalSize = taskList.size();

        assertThrows(IllegalArgumentException.class, () -> taskList.add(null));

        assertEquals(originalSize, taskList.size());
    }

    @Test
    void addAll_iterableOfTasks_appendsTasksInOrder() throws LokiExceptions {
        TaskList emptyTaskList = new TaskList();
        List<Task> addedTasks = List.of(new ToDo("fourth task"), new ToDo("fifth task"));

        emptyTaskList.addAll(addedTasks);

        assertEquals(2, emptyTaskList.size());
        assertSame(addedTasks.get(0), emptyTaskList.get(1));
        assertSame(addedTasks.get(1), emptyTaskList.get(2));
    }

    @Test
    void addAll_existingTasks_appendsWithoutReplacingThem() throws LokiExceptions {
        List<Task> addedTasks = List.of(new ToDo("fourth task"), new ToDo("fifth task"));

        taskList.addAll(addedTasks);

        assertEquals(5, taskList.size());
        assertSame(firstTask, taskList.get(1));
        assertSame(secondTask, taskList.get(2));
        assertSame(thirdTask, taskList.get(3));
        assertSame(addedTasks.get(0), taskList.get(4));
        assertSame(addedTasks.get(1), taskList.get(5));
    }

    @Test
    void addAll_nullIterable_throwsIllegalArgumentExceptionAndLeavesListUnchanged() {
        int originalSize = taskList.size();

        assertThrows(IllegalArgumentException.class, () -> taskList.addAll(null));

        assertEquals(originalSize, taskList.size());
    }

    @Test
    void addAll_iterableContainingNull_throwsIllegalArgumentException() {
        List<Task> tasksWithNull = Arrays.asList(new ToDo("valid task"), null);

        assertThrows(IllegalArgumentException.class, () -> taskList.addAll(tasksWithNull));
    }

    @Test
    void get_validOneBasedIndex_returnsCorrespondingTask() throws LokiExceptions {
        assertSame(firstTask, taskList.get(1));
        assertSame(secondTask, taskList.get(2));
        assertSame(thirdTask, taskList.get(3));
    }

    @Test
    void get_zeroIndex_throwsLokiExceptions() {
        assertThrows(LokiExceptions.class, () -> taskList.get(0));
    }

    @Test
    void get_negativeIndex_throwsLokiExceptions() {
        assertThrows(LokiExceptions.class, () -> taskList.get(-1));
    }

    @Test
    void get_indexAfterLastTask_throwsLokiExceptions() {
        assertThrows(LokiExceptions.class, () -> taskList.get(taskList.size() + 1));
    }

    @Test
    void get_emptyList_throwsLokiExceptions() {
        TaskList emptyTaskList = new TaskList();

        assertThrows(LokiExceptions.class, () -> emptyTaskList.get(1));
    }

    @Test
    void delete_validIndex_removesAndReturnsTask() throws LokiExceptions {
        Task deletedTask = taskList.delete(1);

        assertSame(firstTask, deletedTask);
        assertEquals(2, taskList.size());
        assertSame(secondTask, taskList.get(1));
        assertSame(thirdTask, taskList.get(2));
    }

    @Test
    void delete_middleIndex_removesSelectedTaskAndReindexesFollowingTasks() throws LokiExceptions {
        Task deletedTask = taskList.delete(2);

        assertSame(secondTask, deletedTask);
        assertSame(firstTask, taskList.get(1));
        assertSame(thirdTask, taskList.get(2));
    }

    @Test
    void delete_zeroOrOutOfRangeIndex_throwsLokiExceptions() {
        assertThrows(LokiExceptions.class, () -> taskList.delete(0));
        assertThrows(LokiExceptions.class, () -> taskList.delete(-1));
        assertThrows(LokiExceptions.class, () -> taskList.delete(taskList.size() + 1));
        assertEquals(3, taskList.size());
    }

    @Test
    void delete_fromEmptyList_throwsLokiExceptions() {
        TaskList emptyTaskList = new TaskList();

        assertThrows(LokiExceptions.class, () -> emptyTaskList.delete(1));
    }

    @Test
    void mark_validIndex_marksAndReturnsTask() throws LokiExceptions {
        Task markedTask = taskList.mark(2);

        assertSame(secondTask, markedTask);
        assertTrue(secondTask.isDone());
        assertFalse(firstTask.isDone());
        assertFalse(thirdTask.isDone());
    }

    @Test
    void mark_alreadyCompletedTask_remainsCompletedAndReturnsTask() throws LokiExceptions {
        secondTask.markDone();

        Task markedTask = taskList.mark(2);

        assertSame(secondTask, markedTask);
        assertTrue(secondTask.isDone());
    }

    @Test
    void unmark_validIndex_unmarksAndReturnsTask() throws LokiExceptions {
        secondTask.markDone();

        Task unmarkedTask = taskList.unmark(2);

        assertSame(secondTask, unmarkedTask);
        assertFalse(secondTask.isDone());
    }

    @Test
    void unmark_alreadyIncompleteTask_remainsIncompleteAndReturnsTask() throws LokiExceptions {
        Task unmarkedTask = taskList.unmark(2);

        assertSame(secondTask, unmarkedTask);
        assertFalse(secondTask.isDone());
    }

    @Test
    void unmark_onlyChangesSelectedTask() throws LokiExceptions {
        firstTask.markDone();
        secondTask.markDone();
        thirdTask.markDone();

        taskList.unmark(2);

        assertTrue(firstTask.isDone());
        assertFalse(secondTask.isDone());
        assertTrue(thirdTask.isDone());
    }

    @Test
    void markOrUnmark_invalidIndex_throwsLokiExceptions() {
        assertThrows(LokiExceptions.class, () -> taskList.mark(0));
        assertThrows(LokiExceptions.class, () -> taskList.unmark(taskList.size() + 1));
    }

    @Test
    void sizeAndIsEmpty_reflectAddAndDeleteOperations() throws LokiExceptions {
        TaskList emptyTaskList = new TaskList();
        assertTrue(emptyTaskList.isEmpty());
        assertEquals(0, emptyTaskList.size());

        emptyTaskList.add(new ToDo("temporary task"));
        assertFalse(emptyTaskList.isEmpty());
        assertEquals(1, emptyTaskList.size());

        emptyTaskList.delete(1);
        assertTrue(emptyTaskList.isEmpty());
        assertEquals(0, emptyTaskList.size());
    }

    @Test
    void asList_returnsReadOnlyViewThatReflectsListContents() {
        List<Task> view = taskList.asList();

        assertIterableEquals(List.of(firstTask, secondTask, thirdTask), view);
        assertThrows(UnsupportedOperationException.class, () -> view.add(new ToDo("not allowed")));

        taskList.add(new ToDo("new task"));
        assertEquals(4, view.size());
    }

    @Test
    void iterator_traversesTasksInInsertionOrder() {
        assertIterableEquals(List.of(firstTask, secondTask, thirdTask), taskList);
    }

    @Test
    void update_toDoTitle_preservesTypeStatusAndPosition() throws LokiExceptions {
        firstTask.markDone();
        TaskUpdate update = new Parser().parseUpdate("update 1 /title renamed first task");

        Task replacement = taskList.update(update);

        assertTrue(replacement instanceof ToDo);
        assertTrue(replacement.isDone());
        assertEquals("[T][X] renamed first task", replacement.toString());
        assertSame(replacement, taskList.get(1));
        assertSame(secondTask, taskList.get(2));
        assertSame(thirdTask, taskList.get(3));
    }

    @Test
    void update_deadlineAndEvent_preservesUnchangedFields() throws LokiExceptions {
        TaskList mixedTaskList = new TaskList();
        Deadline deadline = new Deadline("Submit report", LocalDateTime.of(2026, 9, 20, 18, 0));
        Event event = new Event("Consultation", LocalDateTime.of(2026, 9, 20, 14, 0),
                LocalDateTime.of(2026, 9, 20, 16, 0));
        deadline.markDone();
        mixedTaskList.add(deadline);
        mixedTaskList.add(event);

        Task updatedDeadline = mixedTaskList.update(
                new Parser().parseUpdate("update 1 /title Submit final report"));
        Task updatedEvent = mixedTaskList.update(
                new Parser().parseUpdate("update 2 /to 20/9/2026 1700"));

        assertEquals("D | 1 | Submit final report | 2026-09-20T18:00:00", updatedDeadline.saveString());
        assertEquals("E | 0 | Consultation | 2026-09-20T14:00:00 -> 2026-09-20T17:00:00",
                updatedEvent.saveString());
    }

    @Test
    void update_invalidReplacement_leavesOriginalTaskUnchanged() throws LokiExceptions {
        Task originalTask = taskList.get(1);
        TaskUpdate incompatibleUpdate = new TaskUpdate(1, Optional.empty(),
                Optional.of(LocalDateTime.of(2026, 9, 20, 18, 0)), Optional.empty(), Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskList.update(incompatibleUpdate));

        assertSame(originalTask, taskList.get(1));
        assertEquals("[T][ ] first task", taskList.get(1).toString());
    }

    @Test
    void update_reversedEventReplacement_leavesOriginalEventUnchanged() throws LokiExceptions {
        TaskList eventTaskList = new TaskList();
        LocalDateTime start = LocalDateTime.of(2026, 9, 20, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 20, 16, 0);
        Event originalEvent = new Event("Consultation", start, end);
        eventTaskList.add(originalEvent);
        TaskUpdate reversedUpdate = new TaskUpdate(1, Optional.empty(), Optional.empty(),
                Optional.of(end), Optional.of(start));

        assertThrows(IllegalArgumentException.class, () -> eventTaskList.update(reversedUpdate));

        assertSame(originalEvent, eventTaskList.get(1));
        assertEquals("E | 0 | Consultation | 2026-09-20T14:00:00 -> 2026-09-20T16:00:00",
                eventTaskList.get(1).saveString());
    }
}

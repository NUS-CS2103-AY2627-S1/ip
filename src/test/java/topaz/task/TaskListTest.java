package topaz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the task collection operations provided by {@link TaskList}. */
class TaskListTest {
    @Test
    void newTaskList_emptyList_hasZeroTasks() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
    }

    @Test
    void constructor_emptyInputList_createsEmptyTaskList() {
        TaskList taskList = new TaskList(List.of());

        assertEquals(0, taskList.size());
    }

    @Test
    void add_tasks_areStoredInInsertionOrder() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertEquals(first, taskList.get(0));
        assertEquals(second, taskList.get(1));
    }

    @Test
    void addAtIndex_task_isInsertedAtRequestedPosition() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task inserted = new Todo("inserted");
        Task last = new Todo("last");
        taskList.add(first);
        taskList.add(last);

        taskList.add(1, inserted);

        assertEquals(List.of(first, inserted, last), taskList.asList());
    }

    @Test
    void addAtIndex_atEnd_appendsTask() {
        TaskList taskList = new TaskList(List.of(new Todo("first")));
        Task last = new Todo("last");

        taskList.add(taskList.size(), last);

        assertEquals(last, taskList.get(1));
    }

    @Test
    void remove_task_isReturnedAndLaterTasksShiftLeft() {
        TaskList taskList = new TaskList(List.of(
                new Todo("first"), new Todo("second"), new Todo("last")));

        Task removed = taskList.remove(1);

        assertEquals("second", removed.getDescription());
        assertEquals(2, taskList.size());
        assertEquals("last", taskList.get(1).getDescription());
    }

    @Test
    void markAndUnmark_taskStatusIsReversible() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        taskList.markAsDone(0);
        assertTrue(taskList.get(0).isDone());

        taskList.markAsNotDone(0);
        assertFalse(taskList.get(0).isDone());
    }

    @Test
    void constructor_copiesInputList_subsequentInputChangesDoNotAffectTaskList() {
        List<Task> source = new ArrayList<>();
        source.add(new Todo("original"));
        TaskList taskList = new TaskList(source);

        source.clear();

        assertEquals(1, taskList.size());
        assertEquals("original", taskList.get(0).getDescription());
    }

    @Test
    void taskConstructors_invalidInternalData_throwsAssertionError() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 11, 12, 0);

        assertThrows(AssertionError.class, () -> new Todo(null));
        assertThrows(AssertionError.class, () -> new Todo("   "));
        assertThrows(AssertionError.class, () -> new Todo("task | details"));
        assertThrows(AssertionError.class, () -> new Deadline("task", null, false));
        assertThrows(AssertionError.class, () -> new Event("task", dateTime, null, false, false));
        assertThrows(AssertionError.class, () -> new FixedDurationTask("task", 0));
    }

    @Test
    void fixedDurationTask_displaysAndSerializesDuration() {
        FixedDurationTask task = new FixedDurationTask("read report", 120);

        assertEquals(TaskType.FIXED_DURATION, task.getTaskType());
        assertEquals("read report (for: 2 hours)", task.getDescription());
        assertEquals("F | 0 | read report | 120", task.toFileString());
    }

    @Test
    void asList_returnedViewCannotModifyTaskList() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        assertThrows(UnsupportedOperationException.class,
                () -> taskList.asList().add(new Todo("another")));
        assertEquals(1, taskList.size());
    }

    @Test
    void asList_afterAdd_reflectsCurrentTaskList() {
        TaskList taskList = new TaskList();
        List<Task> view = taskList.asList();

        taskList.add(new Todo("task"));

        assertEquals(1, view.size());
        assertEquals("task", view.get(0).getDescription());
    }

    @Test
    void find_keyword_returnsMatchingTasksCaseInsensitively() {
        TaskList taskList = new TaskList(List.of(new Todo("read book"), new Todo("buy milk"),
                new Todo("return book")));

        List<Task> matchingTasks = taskList.find("BOOK");

        assertEquals(List.of("read book", "return book"),
                matchingTasks.stream().map(Task::getDescription).toList());
    }

    @Test
    void find_unknownKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertTrue(taskList.find("holiday").isEmpty());
    }

    @Test
    void get_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(taskList.size()));
    }

    @Test
    void addAtIndex_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class,
                () -> taskList.add(1, new Todo("task")));
        assertThrows(IndexOutOfBoundsException.class,
                () -> taskList.add(-1, new Todo("task")));
    }

    @Test
    void remove_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(-1));
    }

    @Test
    void markAsDone_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.markAsDone(0));
    }

    @Test
    void markAsNotDone_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.markAsNotDone(0));
    }
    @Test
    void containsDuplicate_matchesDetailsWithoutLosingSchedulePrecision() {
        LocalDateTime start = LocalDateTime.of(2026, 12, 7, 12, 0);
        Task completed = new Todo("Read  Book");
        completed.markAsDone();
        TaskList tasks = new TaskList(List.of(completed, new Deadline("return book", start, true),
                new Event("meeting", start, start.plusHours(1), true, true),
                new FixedDurationTask("exercise", 60)));

        assertTrue(tasks.containsDuplicate(new Todo(" read book ")));
        assertTrue(tasks.containsDuplicate(new Deadline("RETURN BOOK", start, true)));
        assertTrue(tasks.containsDuplicate(new Event("meeting", start, start.plusHours(1), true, true)));
        assertTrue(tasks.containsDuplicate(new FixedDurationTask("exercise", 60)));
        assertFalse(tasks.containsDuplicate(new Deadline("return book", start.plusSeconds(1), true)));
        assertFalse(tasks.containsDuplicate(new Event("meeting", start, start.plusHours(2), true, true)));
        assertFalse(tasks.containsDuplicate(new FixedDurationTask("exercise", 61)));
        assertFalse(tasks.containsDuplicate(new Todo("return book")));
    }

}

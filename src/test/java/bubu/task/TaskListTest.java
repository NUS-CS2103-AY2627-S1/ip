package bubu.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import bubu.exception.DuplicateTaskException;

/** Tests task-list operations and validation rules. */
class TaskListTest {
    private static final LocalDateTime START_TIME = LocalDateTime.of(2026, 9, 18, 10, 0);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2026, 9, 18, 11, 0);

    @Test
    void add_newTask_storesTaskAndUpdatesSize() throws DuplicateTaskException {
        TaskList taskList = new TaskList();
        ToDo task = new ToDo("read notes");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.get(0));
    }

    @Test
    void add_duplicateTask_throwsAndPreservesOriginal() throws DuplicateTaskException {
        TaskList taskList = new TaskList();
        taskList.add(new Event("meeting", START_TIME, END_TIME));

        assertThrows(DuplicateTaskException.class, () ->
                taskList.add(new Event("meeting", START_TIME, END_TIME)));
        assertEquals(1, taskList.size());
    }

    @Test
    void insert_validIndex_placesTaskAtRequestedPosition() throws DuplicateTaskException {
        TaskList taskList = new TaskList(List.of(new ToDo("first"), new ToDo("second")));
        ToDo insertedTask = new ToDo("inserted");

        taskList.insert(1, insertedTask);

        assertEquals(insertedTask, taskList.get(1));
    }

    @Test
    void remove_validIndex_returnsTaskAndDecreasesSize() {
        TaskList taskList = new TaskList(List.of(new ToDo("remove me")));

        Task removedTask = taskList.remove(0);

        assertEquals("remove me", removedTask.getDescription());
        assertEquals(0, taskList.size());
    }

    @Test
    void hasIndex_boundaryValues_returnsExpectedResult() {
        TaskList taskList = new TaskList(List.of(new ToDo("only task")));

        assertTrue(taskList.hasIndex(0));
        assertFalse(taskList.hasIndex(-1));
        assertFalse(taskList.hasIndex(1));
    }

    @Test
    void asList_returnsReadOnlyView() {
        TaskList taskList = new TaskList(List.of(new ToDo("read only")));

        assertThrows(UnsupportedOperationException.class, () ->
                taskList.asList().add(new ToDo("not allowed")));
    }

    @Test
    void findMatchingTasks_keywordWithDifferentCase_returnsMatchingTasks() {
        TaskList taskList = new TaskList(List.of(new ToDo("read notes"), new ToDo("submit report")));

        List<Task> matches = taskList.findMatchingTasks("READ");

        assertEquals(List.of("read notes"), matches.stream().map(Task::getDescription).toList());
    }

    @Test
    void findIncompleteTasksDueBetween_excludesCompletedAndOutOfRangeTasks() {
        Deadline upcoming = new Deadline("upcoming", START_TIME.plusHours(1));
        Deadline completed = new Deadline("completed", START_TIME.plusHours(1));
        completed.markAsDone();
        Deadline later = new Deadline("later", END_TIME.plusDays(1));
        TaskList taskList = new TaskList(List.of(upcoming, completed, later));

        List<Task> reminders = taskList.findIncompleteTasksDueBetween(START_TIME, END_TIME);

        assertEquals(List.of(upcoming), reminders);
    }
}

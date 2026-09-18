package blud.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import blud.exception.DeletionException;
import blud.exception.DuplicateTaskException;
import blud.exception.TaskIndexException;

/** Tests task-list sorting and insertion behavior. */
public class TaskListTest {
    /** Verifies that deadline sorting puts undated tasks first and preserves ties. */
    @Test
    public void sort_deadlineAscending_isStableAndPutsUndatedTasksFirst() {
        ToDo todo = new ToDo(new String[] {"todo read book"});
        Deadline laterDeadline = new Deadline(new String[] {"deadline later", "by 10/6/2026 0900"});
        Deadline earlierDeadline = new Deadline(new String[] {"deadline earlier", "by 1/6/2026 0900"});
        Deadline sameDeadline = new Deadline(new String[] {"deadline same", "by 1/6/2026 0900"});
        TaskList taskList = new TaskList(List.of(laterDeadline, todo, earlierDeadline, sameDeadline));

        taskList.sort(TaskList.SortCriterion.DEADLINE, TaskList.SortDirection.ASCENDING);

        assertEquals(List.of(todo, earlierDeadline, sameDeadline, laterDeadline), taskList.getTaskList());
    }

    /** Verifies that event sorting uses event start dates and supports descending order. */
    @Test
    public void sort_eventDescending_usesEventStartDate() {
        Event earlierEvent = new Event(new String[] {
            "event earlier", "from 1/6/2026 0900", "to 1/6/2026 1000"});
        Event laterEvent = new Event(new String[] {
            "event later", "from 10/6/2026 0900", "to 10/6/2026 1000"});
        ToDo todo = new ToDo(new String[] {"todo read book"});
        TaskList taskList = new TaskList(List.of(earlierEvent, todo, laterEvent));

        taskList.sort(TaskList.SortCriterion.EVENT, TaskList.SortDirection.DESCENDING);

        assertEquals(List.of(todo, laterEvent, earlierEvent), taskList.getTaskList());
    }

    /** Verifies that status sorting places incomplete tasks first by default. */
    @Test
    public void sort_statusAscending_ordersIncompleteBeforeComplete() {
        ToDo completedTask = new ToDo(new String[] {"todo completed"});
        completedTask.mark();
        ToDo incompleteTask = new ToDo(new String[] {"todo incomplete"});
        TaskList taskList = new TaskList(List.of(completedTask, incompleteTask));

        taskList.sort(TaskList.SortCriterion.STATUS, TaskList.SortDirection.ASCENDING);

        assertEquals(List.of(incompleteTask, completedTask), taskList.getTaskList());
    }

    /** Verifies that a new task is inserted after existing equal sort values. */
    @Test
    public void addTask_afterSorting_insertsAfterEqualValues() {
        Deadline firstDeadline = new Deadline(new String[] {"deadline first", "by 1/6/2026 0900"});
        Deadline secondDeadline = new Deadline(new String[] {"deadline second", "by 10/6/2026 0900"});
        Deadline insertedDeadline = new Deadline(new String[] {"deadline inserted", "by 1/6/2026 0900"});
        TaskList taskList = new TaskList(List.of(secondDeadline, firstDeadline));
        taskList.sort(TaskList.SortCriterion.DEADLINE, TaskList.SortDirection.ASCENDING);

        taskList.addTask(insertedDeadline);

        assertEquals(List.of(firstDeadline, insertedDeadline, secondDeadline), taskList.getTaskList());
    }

    /** Verifies that duplicate task details are rejected. */
    @Test
    public void addTask_duplicateTask_throwsDuplicateTaskException() {
        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo(new String[] {"todo read"}));

        assertThrows(DuplicateTaskException.class,
                () -> taskList.addTask(new ToDo(new String[] {"todo read"})));
    }

    /** Verifies that deletion uses one-based command indices. */
    @Test
    public void delete_validCommandIndex_removesTask() {
        TaskList taskList = new TaskList(List.of(new ToDo(new String[] {"todo read"})));

        Task deletedTask = taskList.delete(new String[] {"delete", "1"});

        assertEquals("[T][ ] read", deletedTask.toString());
        assertEquals(0, taskList.getSize());
    }

    /** Verifies that invalid task indices produce user-facing errors. */
    @Test
    public void getTaskForCommandIndex_invalidIndex_throwsTaskIndexException() {
        TaskList taskList = new TaskList();

        assertThrows(TaskIndexException.class, () -> taskList.getTaskForCommandIndex(0));
    }

    /** Verifies that malformed deletion commands produce deletion errors. */
    @Test
    public void delete_missingIndex_throwsDeletionException() {
        assertThrows(DeletionException.class, () -> new TaskList().delete(new String[] {"delete"}));
    }
}

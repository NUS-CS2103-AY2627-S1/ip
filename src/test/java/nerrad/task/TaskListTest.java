package nerrad.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list operations that are used by the chatbot's task commands.
 */
class TaskListTest {
    @Test
    void constructorAndGetTasks_copyInputAndExposeReadOnlyView() {
        List<Task> originalTasks = new ArrayList<>();
        originalTasks.add(new Todo("read book"));

        TaskList taskList = new TaskList(originalTasks);
        originalTasks.clear();

        assertEquals(1, taskList.size());
        assertEquals("read book", taskList.get(0).getDescription());
        assertThrows(UnsupportedOperationException.class,
                () -> taskList.getTasks().add(new Todo("should not be added")));
    }

    @Test
    void addInsertAndRemove_tasksKeepExpectedOrder() {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo middleTask = new Todo("return book");
        Todo lastTask = new Todo("buy bread");

        taskList.add(firstTask, lastTask);
        taskList.add(1, middleTask);

        assertEquals(List.of(firstTask, middleTask, lastTask), taskList.getTasks());
        assertEquals(middleTask, taskList.remove(1));
        assertEquals(List.of(firstTask, lastTask), taskList.getTasks());
    }

    @Test
    void add_nullTask_assertionErrorThrown() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.add((Task) null));
    }

    @Test
    void setDone_markAndUnmarkTask_updatesCompletionState() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        taskList.setDone(0, true);
        assertTrue(taskList.get(0).isDone());
        taskList.setDone(0, false);

        assertFalse(taskList.get(0).isDone());
    }

    @Test
    void getCompletedCount_mixedCompletionStates_returnsCorrectCount() {
        TaskList taskList = new TaskList();
        Todo completedTask = new Todo("read book");
        completedTask.markAsDone();
        taskList.add(completedTask, new Todo("write report"));

        assertEquals(1, taskList.getCompletedCount());
    }

    @Test
    void findTasks_matchingDescriptions_returnsTasksInListOrder() {
        TaskList taskList = new TaskList();
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("write report");
        Todo secondMatch = new Todo("return book");
        taskList.add(firstMatch);
        taskList.add(nonMatch);
        taskList.add(secondMatch);

        assertEquals(List.of(firstMatch, secondMatch), taskList.findTasks("book"));
        assertTrue(taskList.findTasks("missing").isEmpty());
    }
}

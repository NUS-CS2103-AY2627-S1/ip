package min.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import min.task.Task;
import min.task.Todo;

class TaskListTest {
    @Test
    void constructor_inputListMutated_doesNotChangeTaskList() {
        Todo todo = new Todo("read book");
        List<Task> originalTasks = new ArrayList<>();
        originalTasks.add(todo);
        TaskList taskList = new TaskList(originalTasks);

        originalTasks.clear();

        assertEquals(1, taskList.size());
        assertSame(todo, taskList.getTasks().get(0));
    }

    @Test
    void addTask_newTask_appendsTaskAndUpdatesSize() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("buy milk");
        TaskList taskList = new TaskList(List.of(firstTask));

        taskList.addTask(secondTask);

        assertEquals(2, taskList.size());
        assertEquals(2, taskList.getDisplayedTaskCount());
        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    @Test
    void markTask_validIndex_marksAndReturnsSelectedTask() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("buy milk");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        Task markedTask = taskList.markTask(1);

        assertSame(secondTask, markedTask);
        assertFalse(firstTask.isDone());
        assertTrue(secondTask.isDone());
    }

    @Test
    void unmarkTask_validIndex_unmarksAndReturnsSelectedTask() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("buy milk");
        secondTask.markAsDone();
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        Task unmarkedTask = taskList.unmarkTask(1);

        assertSame(secondTask, unmarkedTask);
        assertFalse(firstTask.isDone());
        assertFalse(secondTask.isDone());
    }

    @Test
    void deleteTask_validIndex_removesAndReturnsSelectedTask() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("buy milk");
        Todo thirdTask = new Todo("submit report");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask, thirdTask));

        Task deletedTask = taskList.deleteTask(1);

        assertSame(secondTask, deletedTask);
        assertEquals(2, taskList.size());
        assertEquals(List.of(firstTask, thirdTask), taskList.getTasks());
    }

    @Test
    void showMatchingTasks_matchingKeyword_returnsMatchesInOriginalOrder() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        Todo caseDifferentTask = new Todo("Read article");
        TaskList taskList = new TaskList(
                List.of(firstMatch, nonMatch, secondMatch, caseDifferentTask));

        List<Task> matchingTasks = taskList.showMatchingTasks("read");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void showMatchingTasks_noMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList(
                List.of(new Todo("read book"), new Todo("buy milk")));

        List<Task> matchingTasks = taskList.showMatchingTasks("exercise");

        assertTrue(matchingTasks.isEmpty());
    }

    @Test
    void markTask_activeFindView_marksSecondMatchingTask() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        taskList.showMatchingTasks("read");

        Task markedTask = taskList.markTask(1);

        assertSame(secondMatch, markedTask);
        assertFalse(nonMatch.isDone());
        assertTrue(secondMatch.isDone());
    }

    @Test
    void unmarkTask_activeFindView_unmarksSecondMatchingTask() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        nonMatch.markAsDone();
        secondMatch.markAsDone();
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        taskList.showMatchingTasks("read");

        Task unmarkedTask = taskList.unmarkTask(1);

        assertSame(secondMatch, unmarkedTask);
        assertTrue(nonMatch.isDone());
        assertFalse(secondMatch.isDone());
    }

    @Test
    void deleteTask_activeFindView_deletesSecondMatchingTask() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        taskList.showMatchingTasks("read");

        Task deletedTask = taskList.deleteTask(1);

        assertSame(secondMatch, deletedTask);
        assertEquals(List.of(firstMatch, nonMatch), taskList.getTasks());
        assertEquals(1, taskList.getDisplayedTaskCount());
    }

    @Test
    void showAllTasks_activeFindView_restoresFullListNumbering() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        taskList.showMatchingTasks("read");

        List<Task> displayedTasks = taskList.showAllTasks();
        Task markedTask = taskList.markTask(1);

        assertEquals(List.of(firstMatch, nonMatch, secondMatch), displayedTasks);
        assertSame(nonMatch, markedTask);
    }

    @Test
    void showMatchingTasks_secondSearch_replacesDisplayedView() {
        Todo firstMatch = new Todo("read book");
        Todo secondMatch = new Todo("buy milk");
        Todo thirdMatch = new Todo("reread notes");
        TaskList taskList = new TaskList(List.of(firstMatch, secondMatch, thirdMatch));
        taskList.showMatchingTasks("read");

        List<Task> matchingTasks = taskList.showMatchingTasks("milk");
        Task markedTask = taskList.markTask(0);

        assertEquals(List.of(secondMatch), matchingTasks);
        assertSame(secondMatch, markedTask);
    }

    @Test
    void addTask_activeFindView_preservesDisplayedSnapshot() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        Todo addedMatch = new Todo("read article");
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        taskList.showMatchingTasks("read");

        taskList.addTask(addedMatch);
        Task markedTask = taskList.markTask(1);

        assertEquals(4, taskList.size());
        assertEquals(2, taskList.getDisplayedTaskCount());
        assertSame(secondMatch, markedTask);
        assertFalse(addedMatch.isDone());
    }

    @Test
    void getTasks_returnedList_isReadOnlySnapshot() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("buy milk");
        TaskList taskList = new TaskList(List.of(firstTask));
        List<Task> snapshot = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(secondTask));

        taskList.addTask(secondTask);

        assertEquals(1, snapshot.size());
        assertSame(firstTask, snapshot.get(0));
        assertEquals(2, taskList.size());
    }
}

package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;

/** Tests task-list mutation, access, and boundary behavior. */
class TaskListTest {

    @Test
    void constructor_sourceListChanged_taskListUnaffected() {
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("read book"));
        TaskList taskList = new TaskList(source);

        source.clear();

        assertEquals(1, taskList.getTaskCount());
    }

    @Test
    void of_multipleTasks_returnsTaskListContainingTasks() throws BobException {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");

        TaskList taskList = TaskList.of(firstTask, secondTask);

        assertEquals(2, taskList.getTaskCount());
        assertSame(firstTask, taskList.get(0));
        assertSame(secondTask, taskList.get(1));
    }

    @Test
    void add_validTask_taskAdded() throws BobException {
        TaskList taskList = new TaskList();
        Task task = new ToDo("read book");

        taskList.add(task);

        assertEquals(1, taskList.getTaskCount());
        assertSame(task, taskList.get(0));
    }

    @Test
    void get_validIndex_returnsTask() throws BobException {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        TaskList taskList = TaskList.of(firstTask, secondTask);

        assertSame(firstTask, taskList.get(0));
        assertSame(secondTask, taskList.get(1));
    }

    @Test
    void get_invalidIndex_bobExceptionThrown() {
        TaskList taskList = TaskList.of(new ToDo("read book"));

        assertThrows(BobException.class, () -> taskList.get(-1));
        assertThrows(BobException.class, () -> taskList.get(1));
    }

    @Test
    void remove_validIndex_removesAndReturnsTask() throws BobException {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        TaskList taskList = TaskList.of(firstTask, secondTask);

        Task removedTask = taskList.remove(0);

        assertSame(firstTask, removedTask);
        assertEquals(1, taskList.getTaskCount());
        assertSame(secondTask, taskList.get(0));
    }

    @Test
    void remove_invalidIndex_bobExceptionThrownAndListUnchanged() {
        TaskList taskList = TaskList.of(new ToDo("read book"));

        assertThrows(BobException.class, () -> taskList.remove(-1));
        assertThrows(BobException.class, () -> taskList.remove(1));
        assertEquals(1, taskList.getTaskCount());
    }

    @Test
    void getTasks_modifyReturnedList_unsupportedOperationExceptionThrown() {
        TaskList taskList = TaskList.of(new ToDo("read book"));

        assertThrows(UnsupportedOperationException.class,
                () -> taskList.getTasks().add(new ToDo("write report")));
        assertEquals(1, taskList.getTaskCount());
    }

    @Test
    void find_keywordWithDifferentCase_returnsMatchingTasksInOriginalOrder() {
        Task firstMatch = new ToDo("read book");
        Task nonMatch = new ToDo("write report");
        Task secondMatch = new ToDo("return BOOK");
        TaskList taskList = TaskList.of(firstMatch, nonMatch, secondMatch);

        List<Task> matchingTasks = taskList.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void find_noMatchingDescription_returnsEmptyList() {
        TaskList taskList = TaskList.of(new ToDo("write report"));

        assertEquals(List.of(), taskList.find("book"));
    }

    @Test
    void find_emptyKeyword_returnsAllTasksInOriginalOrder() {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        TaskList taskList = TaskList.of(firstTask, secondTask);

        assertEquals(List.of(firstTask, secondTask), taskList.find(""));
    }

    @Test
    void search_prefixTermsWithDifferentCase_returnsMatchingTasksInOriginalOrder() {
        Task firstMatch = new ToDo("buy books");
        Task nonMatch = new ToDo("write report");
        Task secondMatch = new ToDo("read a book");
        TaskList taskList = TaskList.of(firstMatch, nonMatch, secondMatch);

        assertEquals(List.of(firstMatch, secondMatch), taskList.search("BOO"));
    }

    @Test
    void search_multipleTerms_requiresEveryTermToMatch() {
        Task matchingTask = new ToDo("project meeting");
        Task missingTerm = new ToDo("project report");
        TaskList taskList = TaskList.of(matchingTask, missingTerm);

        assertEquals(List.of(matchingTask), taskList.search("pro meet"));
    }

    @Test
    void search_partialWordPrefix_matchesOnlyWordPrefixes() {
        Task prefixMatch = new ToDo("buy books");
        Task substringOnly = new ToDo("notebook");
        TaskList taskList = TaskList.of(prefixMatch, substringOnly);

        assertEquals(List.of(prefixMatch), taskList.search("boo"));
    }

    @Test
    void search_noMatchingDescription_returnsEmptyList() {
        TaskList taskList = TaskList.of(new ToDo("write report"));

        assertEquals(List.of(), taskList.search("book"));
    }

    @Test
    void search_queryWithExtraWhitespace_ignoresWhitespace() {
        Task matchingTask = new ToDo("project meeting");
        TaskList taskList = TaskList.of(matchingTask);

        assertEquals(List.of(matchingTask), taskList.search("  PRO   mee  "));
    }

    @Test
    void containsEquivalentTask_sameDetails_returnsExpectedResult() {
        TaskList taskList = TaskList.of(new ToDo("read book"));

        assertEquals(true, taskList.containsEquivalentTask(new ToDo("read book")));
        assertEquals(false, taskList.containsEquivalentTask(new ToDo("write book")));
    }
}

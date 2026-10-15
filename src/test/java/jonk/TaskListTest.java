package jonk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests task lookup, deletion, and rollback operations in {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void constructor_sourceListChanges_keepsIndependentTaskOrder() {
        Task originalTask = new Todo("original");
        List<Task> source = new ArrayList<>(List.of(originalTask));
        TaskList tasks = new TaskList(source);

        source.clear();
        tasks.add(new Todo("added"));

        assertEquals(2, tasks.size());
        assertSame(originalTask, tasks.asList().getFirst());
        assertEquals(List.of(), source);
    }

    @Test
    public void asList_listChanges_keepsUnmodifiableSnapshot() throws JonkException {
        Task originalTask = new Todo("original");
        TaskList tasks = new TaskList(List.of(originalTask));
        List<Task> snapshot = tasks.asList();

        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        tasks.delete(1);
        tasks.add(new Todo("replacement"));

        assertEquals(List.of(originalTask), snapshot);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    public void deleteAndRestore_boundaryPosition_preservesOrder(int number) throws JonkException {
        List<Task> original = List.of(new Todo("first"), new Todo("middle"), new Todo("last"));
        TaskList tasks = new TaskList(original);
        Task removed = tasks.delete(number);

        assertSame(original.get(number - 1), removed);
        assertEquals(2, tasks.size());
        tasks.restore(number, removed);

        assertEquals(original, tasks.asList());
    }

    @Test
    public void restore_onlyDeletedTask_restoresEmptyList() throws JonkException {
        Task original = new Todo("only");
        TaskList tasks = new TaskList(List.of(original));
        tasks.delete(1);

        tasks.restore(1, original);

        assertEquals(List.of(original), tasks.asList());
    }

    @Test
    public void restore_nullTaskOrZeroPosition_rejectsWithoutChangingList() {
        Task original = new Todo("only");
        TaskList tasks = new TaskList(List.of(original));

        assertThrows(AssertionError.class, () -> tasks.restore(1, null));
        assertThrows(AssertionError.class, () -> tasks.restore(0, original));
        assertEquals(List.of(original), tasks.asList());
    }

    @Test
    public void find_dateOrStatusText_doesNotMatchOutsideDescription() {
        Task task = new Deadline("return book", "2026-09-17");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        assertEquals(List.of(), tasks.find("2026"));
        assertEquals(List.of(), tasks.find("[X]"));
        assertEquals(List.of(task), tasks.find("turn bo"));
        assertEquals(List.of(), new TaskList().find("book"));
    }

    @Test
    public void get_firstTaskNumber_returnsFirstTask() throws JonkException {
        Task firstTask = new Todo("first task");
        TaskList taskList = new TaskList(List.of(firstTask, new Todo("last task")));

        assertSame(firstTask, taskList.get(1));
    }

    @Test
    public void get_lastTaskNumber_returnsLastTask() throws JonkException {
        Task lastTask = new Todo("last task");
        TaskList taskList = new TaskList(List.of(new Todo("first task"), lastTask));

        assertSame(lastTask, taskList.get(2));
    }

    @Test
    public void get_emptyList_throwsJonkException() {
        TaskList taskList = new TaskList();

        JonkException exception = assertThrows(JonkException.class, () -> taskList.get(1));

        assertEquals("That mission number is not on the flight plan.", exception.getMessage());
    }

    @Test
    public void get_zeroTaskNumber_throwsJonkException() {
        TaskList taskList = new TaskList(List.of(new Todo("only task")));

        JonkException exception = assertThrows(JonkException.class, () -> taskList.get(0));

        assertEquals("That mission number is not on the flight plan.", exception.getMessage());
    }

    @Test
    public void get_negativeTaskNumber_throwsJonkException() {
        TaskList taskList = new TaskList(List.of(new Todo("only task")));

        JonkException exception = assertThrows(JonkException.class, () -> taskList.get(-1));

        assertEquals("That mission number is not on the flight plan.", exception.getMessage());
    }

    @Test
    public void get_taskNumberBeyondListSize_throwsJonkException() {
        TaskList taskList = new TaskList(List.of(new Todo("only task")));

        JonkException exception = assertThrows(JonkException.class, () -> taskList.get(2));

        assertEquals("That mission number is not on the flight plan.", exception.getMessage());
    }

    @Test
    public void find_keywordInMultipleDescriptions_returnsMatchingTasksInOriginalOrder() {
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Event("project meeting", "2019-12-03", "2019-12-04");
        Task secondMatch = new Deadline("return book", "2019-12-02");
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        assertEquals(List.of(firstMatch, secondMatch), taskList.find("book"));
    }

    @Test
    public void find_keywordNotInAnyDescription_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), taskList.find("library"));
    }

    @Test
    public void delete_middleTask_returnsTaskAndRemovesIt() throws JonkException {
        Task firstTask = new Todo("first task");
        Task middleTask = new Todo("middle task");
        Task lastTask = new Todo("last task");
        TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask));

        Task deletedTask = taskList.delete(2);

        assertSame(middleTask, deletedTask);
        assertEquals(List.of(firstTask, lastTask), taskList.asList());
    }

    @Test
    public void find_differentCase_returnsOnlyExactCaseMatches() {
        Task matchingTask = new Todo("read book");
        TaskList taskList = new TaskList(List.of(new Todo("read Book"), matchingTask));

        assertEquals(List.of(matchingTask), taskList.find("book"));
    }

    @Test
    public void find_resultSnapshot_isUnmodifiableAndIndependentOfListChanges() throws JonkException {
        Task matchingTask = new Todo("read book");
        TaskList taskList = new TaskList(List.of(matchingTask));
        List<Task> matches = taskList.find("book");

        assertThrows(UnsupportedOperationException.class, () -> matches.add(new Todo("return book")));
        taskList.delete(1);
        taskList.add(new Todo("buy book"));

        assertEquals(List.of(matchingTask), matches);
    }

    @Test
    public void delete_invalidTaskNumber_throwsJonkExceptionAndPreservesList() {
        Task onlyTask = new Todo("only task");
        TaskList taskList = new TaskList(List.of(onlyTask));

        JonkException exception = assertThrows(JonkException.class, () -> taskList.delete(2));

        assertEquals("That mission number is not on the flight plan.", exception.getMessage());
        assertEquals(List.of(onlyTask), taskList.asList());
    }

    @Test
    public void removeLast_multipleTasks_removesFinalTask() {
        Task firstTask = new Todo("first task");
        TaskList taskList = new TaskList(List.of(firstTask, new Todo("last task")));

        taskList.removeLast();

        assertEquals(List.of(firstTask), taskList.asList());
    }

    @Test
    public void removeLast_emptyList_throwsAssertionError() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, taskList::removeLast);
    }

    @Test
    public void restore_deletedTask_reinsertsTaskAtFormerPosition() throws JonkException {
        Task firstTask = new Todo("first task");
        Task middleTask = new Todo("middle task");
        Task lastTask = new Todo("last task");
        TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask));
        Task deletedTask = taskList.delete(2);

        taskList.restore(2, deletedTask);

        assertEquals(List.of(firstTask, middleTask, lastTask), taskList.asList());
    }

    @Test
    public void restore_invalidTaskNumber_throwsAssertionError() {
        TaskList taskList = new TaskList(List.of(new Todo("existing task")));

        assertThrows(AssertionError.class, () -> taskList.restore(3, new Todo("restored task")));
    }
}

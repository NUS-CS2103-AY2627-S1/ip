package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import megatron.exception.TaskNotFoundException;

/**
 * Tests the one-based task lookup provided by {@link TaskList}.
 */
class TaskListTest {
    private final Task firstTask = new Todo("first task");
    private final Task middleTask = new Todo("middle task");
    private final Task lastTask = new Todo("last task");
    private final TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask));

    @Test
    void getTask_validFirstTaskNumber_returnsFirstTask() throws TaskNotFoundException {
        assertSame(firstTask, taskList.getTask(1));
    }

    @Test
    void getTask_validMiddleTaskNumber_returnsMiddleTask() throws TaskNotFoundException {
        assertSame(middleTask, taskList.getTask(2));
    }

    @Test
    void getTask_validLastTaskNumber_returnsLastTask() throws TaskNotFoundException {
        assertSame(lastTask, taskList.getTask(3));
    }

    @Test
    void getTask_zeroTaskNumber_throwsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> taskList.getTask(0));
    }

    @Test
    void getTask_negativeTaskNumber_throwsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> taskList.getTask(-1));
    }

    @Test
    void getTask_taskNumberAboveListSize_throwsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> taskList.getTask(4));
    }

    @Test
    void getTask_emptyList_throwsTaskNotFoundException() {
        TaskList emptyTaskList = new TaskList();

        assertThrows(TaskNotFoundException.class, () -> emptyTaskList.getTask(1));
    }

    @Test
    void constructor_copiesInitialTasks() throws TaskNotFoundException {
        List<Task> initialTasks = new ArrayList<>(List.of(firstTask));
        TaskList copiedTaskList = new TaskList(initialTasks);

        initialTasks.add(lastTask);

        assertEquals(1, copiedTaskList.size());
        assertSame(firstTask, copiedTaskList.getTask(1));
    }

    @Test
    void constructor_nullTasks_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TaskList(null));
    }

    @Test
    void add_task_appendsTask() throws TaskNotFoundException {
        TaskList emptyTaskList = new TaskList();

        emptyTaskList.add(firstTask);

        assertEquals(1, emptyTaskList.size());
        assertSame(firstTask, emptyTaskList.getTask(1));
    }

    @Test
    void add_nullTask_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TaskList().add(null));
    }

    @Test
    void removeTask_validTaskNumber_returnsAndRemovesTask() throws TaskNotFoundException {
        TaskList tasks = new TaskList(List.of(firstTask, middleTask, lastTask));

        Task removedTask = tasks.removeTask(2);

        assertSame(middleTask, removedTask);
        assertEquals(2, tasks.size());
        assertSame(lastTask, tasks.getTask(2));
    }

    @Test
    void setDone_validTaskNumber_marksAndReturnsTask() throws TaskNotFoundException {
        Task task = new Todo("task");
        TaskList tasks = new TaskList(List.of(task));

        Task markedTask = tasks.setDone(1);

        assertSame(task, markedTask);
        assertTrue(task.isDone());
    }

    @Test
    void setNotDone_validTaskNumber_unmarksAndReturnsTask() throws TaskNotFoundException {
        Task task = new Todo("task");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        Task unmarkedTask = tasks.setNotDone(1);

        assertSame(task, unmarkedTask);
        assertFalse(task.isDone());
    }

    @Test
    void iterator_returnsTasksInListOrder() {
        TaskList tasks = new TaskList(List.of(firstTask, middleTask, lastTask));
        Iterator<Task> iterator = tasks.iterator();

        assertTrue(iterator.hasNext());
        assertSame(firstTask, iterator.next());
        assertSame(middleTask, iterator.next());
        assertSame(lastTask, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void find_matchingKeyword_returnsMatchesInListOrder() throws TaskNotFoundException {
        TaskList matches = taskList.find("task");

        assertEquals(3, matches.size());
        assertSame(firstTask, matches.getTask(1));
        assertSame(middleTask, matches.getTask(2));
        assertSame(lastTask, matches.getTask(3));
    }

    @Test
    void find_partialKeyword_returnsOnlyMatchingTasks() throws TaskNotFoundException {
        TaskList matches = taskList.find("st");

        assertEquals(2, matches.size());
        assertSame(firstTask, matches.getTask(1));
        assertSame(lastTask, matches.getTask(2));
    }

    @Test
    void find_differentCase_returnsCaseInsensitiveMatch() throws TaskNotFoundException {
        Task match = new Todo("read book");
        TaskList tasks = new TaskList(List.of(match, new Todo("buy groceries")));

        TaskList matches = tasks.find("BOO");

        assertEquals(1, matches.size());
        assertSame(match, matches.getTask(1));
    }

    @Test
    void find_multipleTermsInAnyOrder_returnsTaskMatchingAllTerms() throws TaskNotFoundException {
        Task match = new Todo("read book");
        TaskList tasks = new TaskList(List.of(match, new Todo("return book")));

        TaskList matches = tasks.find("BOOK read");

        assertEquals(1, matches.size());
        assertSame(match, matches.getTask(1));
    }

    @Test
    void find_singleEditTerms_returnFuzzyMatch() throws TaskNotFoundException {
        Task match = new Todo("read book");
        TaskList tasks = new TaskList(List.of(match, new Todo("buy groceries")));

        for (String searchTerm : List.of("bok", "boon", "boook")) {
            TaskList matches = tasks.find(searchTerm);

            assertEquals(1, matches.size());
            assertSame(match, matches.getTask(1));
        }
    }

    @Test
    void find_twoEditTerm_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(0, tasks.find("bppk").size());
    }

    @Test
    void find_twoCharacterTerm_doesNotUseFuzzyMatching() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(0, tasks.find("bk").size());
    }

    @Test
    void findMatches_matchingTasks_retainsOriginalTaskNumbers() {
        Task secondTask = new Todo("read book");
        Task fourthTask = new Todo("return book");
        TaskList tasks = new TaskList(List.of(
                new Todo("buy groceries"), secondTask, new Todo("watch movie"), fourthTask));

        List<TaskMatch> matches = tasks.findMatches("book");

        assertEquals(2, matches.size());
        assertEquals(2, matches.get(0).taskNumber());
        assertSame(secondTask, matches.get(0).task());
        assertEquals(4, matches.get(1).taskNumber());
        assertSame(fourthTask, matches.get(1).task());
    }

    @Test
    void find_unknownKeyword_returnsEmptyList() {
        assertEquals(0, taskList.find("book").size());
    }

    @Test
    void find_nullKeyword_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> taskList.find(null));
    }
}

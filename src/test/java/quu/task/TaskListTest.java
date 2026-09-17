package quu.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import quu.exception.QuuException;
import quu.exception.TaskNotFoundException;
import quu.exception.UnsupportedEditException;

/**
 * Tests {@link TaskList}'s bookkeeping, its index bounds, and its keyword search.
 *
 * <p>Note the deliberate asymmetry these tests pin down: {@code getTaskAt} is zero-based
 * while {@code removeTask}, {@code markTask} and {@code unmarkTask} are one-based,
 * because the latter three take the number the user typed.
 */
public class TaskListTest {

    private TaskList listOf(String... descriptions) {
        TaskList taskList = new TaskList();
        for (String description : descriptions) {
            taskList.addTask(new ToDo(description));
        }
        return taskList;
    }

    @Test
    public void addTask_increasesSize() {
        TaskList taskList = new TaskList();
        assertEquals(0, taskList.getSize());
        taskList.addTask(new ToDo("read book"));
        assertEquals(1, taskList.getSize());
    }

    @Test
    public void getTaskAt_isZeroBased() {
        TaskList taskList = listOf("first", "second");
        assertTrue(taskList.getTaskAt(0).toString().contains("first"));
        assertTrue(taskList.getTaskAt(1).toString().contains("second"));
    }

    @Test
    public void removeTask_isOneBased_andReturnsRemovedTask() throws TaskNotFoundException {
        TaskList taskList = listOf("first", "second");
        Task removed = taskList.removeTask(1);
        assertTrue(removed.toString().contains("first"));
        assertEquals(1, taskList.getSize());
        assertTrue(taskList.getTaskAt(0).toString().contains("second"));
    }

    @Test
    public void removeTask_indexZero_throwsTaskNotFound() {
        TaskList taskList = listOf("first");
        assertThrows(TaskNotFoundException.class, () -> taskList.removeTask(0));
    }

    @Test
    public void removeTask_indexPastEnd_throwsTaskNotFound() {
        TaskList taskList = listOf("first");
        assertThrows(TaskNotFoundException.class, () -> taskList.removeTask(2));
    }

    @Test
    public void markTask_marksTaskAtOneBasedIndex() throws TaskNotFoundException {
        TaskList taskList = listOf("first", "second");
        Task marked = taskList.markTask(2);
        assertEquals("[T][X] second", marked.toString());
        assertEquals("[T][ ] first", taskList.getTaskAt(0).toString());
    }

    @Test
    public void unmarkTask_clearsAPreviouslyMarkedTask() throws TaskNotFoundException {
        TaskList taskList = listOf("first");
        taskList.markTask(1);
        assertEquals("[T][ ] first", taskList.unmarkTask(1).toString());
    }

    @Test
    public void markTask_outOfRange_throwsTaskNotFound() {
        TaskList taskList = listOf("first");
        assertThrows(TaskNotFoundException.class, () -> taskList.markTask(2));
    }

    @Test
    public void constructor_withExistingList_adoptsItsContents() {
        List<Task> existing = new ArrayList<>();
        existing.add(new ToDo("read book"));
        TaskList taskList = new TaskList(existing);
        assertEquals(1, taskList.getSize());
        assertEquals("[T][ ] read book", taskList.getTaskAt(0).toString());
    }

    @Test
    public void buildFoundList_keywordInSomeDescriptions_returnsOnlyThoseTasks() {
        TaskList taskList = listOf("read book", "buy milk", "return book");
        TaskList found = taskList.buildFoundList("book");
        assertEquals(2, found.getSize());
        assertEquals("[T][ ] read book", found.getTaskAt(0).toString());
        assertEquals("[T][ ] return book", found.getTaskAt(1).toString());
    }

    @Test
    public void buildFoundList_differingCase_stillMatches() {
        TaskList taskList = listOf("Read Book");
        assertEquals(1, taskList.buildFoundList("book").getSize());
        assertEquals(1, taskList.buildFoundList("BOOK").getSize());
    }

    @Test
    public void buildFoundList_partOfAWord_stillMatches() {
        TaskList taskList = listOf("read book");
        assertEquals(1, taskList.buildFoundList("oo").getSize());
    }

    @Test
    public void buildFoundList_noMatch_returnsEmptyList() {
        TaskList taskList = listOf("read book", "buy milk");
        assertEquals(0, taskList.buildFoundList("laundry").getSize());
    }

    @Test
    public void buildFoundList_searchesDescriptionOnly_notTheRenderedDate() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Deadline("return book", "2026-06-06"));
        // "Jun" appears in the task's printed form but not in its description.
        assertEquals(0, taskList.buildFoundList("Jun").getSize());
        assertEquals(1, taskList.buildFoundList("return").getSize());
    }

    @Test
    public void buildFoundList_sharesTaskObjects_soMarkingIsVisibleInBothLists() {
        TaskList taskList = listOf("read book");
        TaskList found = taskList.buildFoundList("book");
        found.getTaskAt(0).mark();
        assertEquals("[T][X] read book", taskList.getTaskAt(0).toString());
    }

    @Test
    public void buildFoundList_isASeparateList_soAddingToItLeavesTheOriginalAlone() {
        TaskList taskList = listOf("read book", "buy milk");
        TaskList found = taskList.buildFoundList("book");
        found.addTask(new ToDo("extra"));
        assertEquals(2, taskList.getSize());
    }

    @Test
    public void editTask_descriptionChanged_keepsPositionAndSize() throws QuuException {
        TaskList taskList = listOf("read book", "return book", "buy milk");
        taskList.editTask(2, Map.of(Task.KEY_DESCRIPTION, "return two books"));
        assertEquals(3, taskList.getSize());
        assertEquals("[T][ ] read book", taskList.getTaskAt(0).toString());
        assertEquals("[T][ ] return two books", taskList.getTaskAt(1).toString());
        assertEquals("[T][ ] buy milk", taskList.getTaskAt(2).toString());
    }

    @Test
    public void editTask_taskWasDone_staysDone() throws QuuException {
        TaskList taskList = listOf("read book");
        taskList.markTask(1);
        Task editedTask = taskList.editTask(1, Map.of(Task.KEY_DESCRIPTION, "read two books"));
        assertTrue(editedTask.isDone());
        assertEquals("[T][X] read two books", taskList.getTaskAt(0).toString());
    }

    @Test
    public void editTask_flagTheTaskDoesNotHave_leavesListUnchanged() {
        TaskList taskList = listOf("read book");
        assertThrows(UnsupportedEditException.class, () -> taskList.editTask(1, Map.of("by", "2026-06-06")));
        assertEquals(1, taskList.getSize());
        assertEquals("[T][ ] read book", taskList.getTaskAt(0).toString());
    }

    @Test
    public void editTask_indexPastTheEnd_throwsTaskNotFound() {
        TaskList taskList = listOf("read book");
        assertThrows(TaskNotFoundException.class, () ->
                taskList.editTask(2, Map.of(Task.KEY_DESCRIPTION, "read two books")));
    }

}

package kojispawn.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import kojispawn.exception.KojisPawnException;

/**
 * Tests task-list ownership, searching, and mutations.
 */
public class TaskListTest {
    @Test
    public void constructor_sourceListChanges_doesNotChangeTaskList() {
        List<Task> source = new ArrayList<>(List.of(new Todo("read book")));
        TaskList taskList = new TaskList(source);

        source.clear();

        assertEquals(1, taskList.size());
        assertThrows(UnsupportedOperationException.class, () ->
                taskList.getTasks().add(new Todo("write book")));
    }

    @Test
    public void add_validTask_appendsTask() {
        TaskList taskList = new TaskList();

        taskList.add(new Todo("first"));
        taskList.add(new Todo("second"));

        assertEquals(2, taskList.size());
        assertEquals("[T][ ] second", taskList.getTasks().get(1).toString());
    }

    @Test
    public void findOn_mixedTasks_returnsMatchingDeadlinesInOriginalOrder() {
        LocalDate requestedDate = LocalDate.of(2019, 12, 2);
        TaskList taskList = new TaskList(List.of(
                new Deadline("first deadline", requestedDate),
                new Todo("undated task"),
                new Deadline("different deadline", requestedDate.plusDays(1)),
                new Deadline("second deadline", requestedDate)));

        List<String> matches = taskList.findOn(requestedDate).stream()
                .map(Task::toString)
                .toList();

        assertEquals(List.of(
                "[D][ ] first deadline (by: Dec 2 2019)",
                "[D][ ] second deadline (by: Dec 2 2019)"), matches);
    }

    @Test
    public void find_keyword_returnsOnlyTasksWithMatchingDescriptions() {
        TaskList taskList = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2019, 12, 2)),
                new Event("project meeting", "book club", "4pm"),
                new Todo("write notes")));

        List<String> matches = taskList.find("book").stream()
                .map(Task::toString)
                .toList();

        assertEquals(List.of(
                "[T][ ] read book",
                "[D][ ] return book (by: Dec 2 2019)"), matches);
        assertEquals(List.of(), taskList.find("2019"));
    }

    @Test
    public void markThenUnmark_validTask_changesCompletionState() throws KojisPawnException {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertEquals("[T][X] read book", taskList.mark(1).toString());
        assertEquals("[T][ ] read book", taskList.unmark(1).toString());
    }

    @Test
    public void copy_originalTaskChanges_doesNotChangeCopy() throws KojisPawnException {
        TaskList original = new TaskList(List.of(
                new Todo("todo"),
                new Deadline("deadline", LocalDate.of(2019, 12, 2)),
                new Event("event", "2pm", "4pm")));
        original.mark(2);

        TaskList copy = original.copy();
        original.mark(1);
        original.delete(3);

        assertEquals(List.of(
                "[T][ ] todo",
                "[D][X] deadline (by: Dec 2 2019)",
                "[E][ ] event (from: 2pm to: 4pm)"),
                copy.getTasks().stream().map(Task::toString).toList());
    }

    @Test
    public void replaceWith_sourceChanges_doesNotChangeReplacement() throws KojisPawnException {
        TaskList source = new TaskList(List.of(new Todo("task")));
        TaskList replacement = new TaskList();

        replacement.replaceWith(source);
        source.mark(1);

        assertEquals("[T][ ] task", replacement.getTasks().get(0).toString());
    }

    @Test
    public void delete_validTask_removesAndReturnsTask() throws KojisPawnException {
        TaskList taskList = new TaskList(List.of(
                new Todo("keep"), new Todo("remove")));

        Task deletedTask = taskList.delete(2);

        assertEquals("[T][ ] remove", deletedTask.toString());
        assertEquals(List.of("[T][ ] keep"),
                taskList.getTasks().stream().map(Task::toString).toList());
    }

    @Test
    public void mark_emptyList_throwsException() {
        TaskList taskList = new TaskList();

        KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                taskList.mark(1));

        assertEquals("There are no tasks to mark yet.", exception.getMessage());
    }

    @Test
    public void markUnmarkAndDelete_nonPositiveTaskNumbers_throwException() {
        TaskList taskList = new TaskList(List.of(new Todo("only task")));

        KojisPawnException markException = assertThrows(KojisPawnException.class, () ->
                taskList.mark(0));
        KojisPawnException unmarkException = assertThrows(KojisPawnException.class, () ->
                taskList.unmark(-1));
        KojisPawnException deleteException = assertThrows(KojisPawnException.class, () ->
                taskList.delete(0));

        String expectedMessage = "The list begins at 1. Choose a positive task number.";
        assertEquals(expectedMessage, markException.getMessage());
        assertEquals(expectedMessage, unmarkException.getMessage());
        assertEquals(expectedMessage, deleteException.getMessage());
    }

    @Test
    public void delete_taskNumberAboveSize_throwsException() {
        TaskList taskList = new TaskList(List.of(new Todo("only task")));

        KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                taskList.delete(2));

        assertEquals("No task occupies that position. Choose a number from 1 to 1.",
                exception.getMessage());
    }
}

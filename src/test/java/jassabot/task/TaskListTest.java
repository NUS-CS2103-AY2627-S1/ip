package jassabot.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests ordered task-list mutation and its read-only view.
 */
public class TaskListTest {

    @Test
    public void constructor_sourceListChangedAfterConstruction_taskListUnaffected() {
        Todo originalTask = new Todo("read book");
        ArrayList<Task> source = new ArrayList<>(List.of(originalTask));

        TaskList tasks = new TaskList(source);
        source.clear();

        assertAll(() ->
                assertEquals(1, tasks.size()), () ->
                assertSame(originalTask, tasks.get(0))
        );
    }

    @Test
    public void addAndRemove_multiplePositions_preservesExpectedOrder() {
        Todo first = new Todo("first");
        Todo middle = new Todo("middle");
        Todo last = new Todo("last");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(last);
        tasks.add(1, middle);
        Task removed = tasks.remove(1);

        assertAll(() ->
                assertSame(middle, removed), () ->
                assertEquals(List.of(first, last), tasks.asList()), () ->
                assertEquals(2, tasks.size())
        );
    }

    @Test
    public void markAndUnmark_validIndex_updatesSelectedTaskOnly() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        tasks.mark(1);
        assertAll(() ->
                assertFalse(first.isDone()), () ->
                assertTrue(second.isDone())
        );
        tasks.unmark(1);
        assertFalse(second.isDone());
    }

    @Test
    public void find_mixedCaseKeyword_returnsMatchingTasksInOriginalOrder() {
        Todo firstMatch = new Todo("Read Book");
        Todo nonMatch = new Todo("buy groceries");
        Todo secondMatch = new Todo("return borrowed book");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matches = tasks.find("bOoK");

        assertEquals(List.of(firstMatch, secondMatch), matches);
    }

    @Test
    public void find_noMatchingDescription_returnsEmptyReadOnlyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        List<Task> matches = tasks.find("exercise");

        assertAll(() ->
                assertTrue(matches.isEmpty()), () ->
                assertThrows(UnsupportedOperationException.class, () ->
                        matches.add(new Todo("exercise")))
        );
    }

    @Test
    public void asList_modificationAttempt_throwsUnsupportedOperationException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<Task> readOnlyTasks = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () ->
                readOnlyTasks.add(new Todo("write report")));
    }

    @Test
    public void operations_invalidIndexes_leaveOrderAndCompletionUnchanged() {
        Todo task = new Todo("retained");
        TaskList tasks = new TaskList(List.of(task));
        for (int index : List.of(-1, 1, Integer.MAX_VALUE)) {
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.mark(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.unmark(index));
            assertEquals(List.of(task), tasks.asList());
            assertFalse(task.isDone());
        }
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(-1, new Todo("invalid")));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(2, new Todo("invalid")));
        assertEquals(List.of(task), tasks.asList());
    }

    @Test
    public void asList_existingView_tracksChangesButRejectsMutation() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first));
        List<Task> view = tasks.asList();
        tasks.add(second);
        assertEquals(List.of(first, second), view);
        tasks.remove(0);
        assertEquals(List.of(second), view);
        assertThrows(UnsupportedOperationException.class, () -> view.set(0, first));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        assertThrows(UnsupportedOperationException.class, view::clear);
        assertEquals(List.of(second), tasks.asList());
    }

    @Test
    public void find_substringAndDuplicateDescriptions_returnsReadOnlySnapshot() {
        Todo first = new Todo("Read BOOK");
        Todo second = new Todo("Read BOOK");
        TaskList tasks = new TaskList(List.of(first, new Todo("other"), second));
        List<Task> matches = tasks.find("oo");
        assertEquals(List.of(first, second), matches);
        assertThrows(UnsupportedOperationException.class, () -> matches.remove(0));
        tasks.remove(0);
        tasks.add(new Todo("another book"));
        assertEquals(List.of(first, second), matches);
        assertEquals(tasks.asList(), tasks.find(""));
    }
}

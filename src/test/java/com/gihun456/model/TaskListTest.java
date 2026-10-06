package com.gihun456.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.gihun456.GihunException;

public class TaskListTest {
    @Test
    public void add_singleTask_taskIsStored() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("Read book");

        taskList.add(todo);

        assertEquals(1, taskList.size());
        assertSame(todo, taskList.get(0));
    }

    @Test
    public void addAll_multipleTasks_tasksAreAppended() {
        TaskList taskList = new TaskList();
        Todo first = new Todo("Read book");
        Todo second = new Todo("Write code");

        taskList.addAll(List.of(first, second));

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    public void addAll_varargsTasks_tasksAreAppended() {
        TaskList taskList = new TaskList();
        Todo first = new Todo("Read book");
        Todo second = new Todo("Write code");

        taskList.addAll(first, second);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    public void constructor_varargsTasks_tasksAreStored() {
        Todo first = new Todo("Read book");
        Todo second = new Todo("Write code");

        TaskList taskList = new TaskList(first, second);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    public void remove_validIndex_removesAndReturnsTask() {
        TaskList taskList = new TaskList();
        Todo first = new Todo("Read book");
        Todo second = new Todo("Write code");
        taskList.addAll(List.of(first, second));

        Todo removed = (Todo) taskList.remove(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    public void getValidIndex_validPositiveInput_returnsZeroBasedIndex() {
        TaskList taskList = new TaskList();
        taskList.addAll(List.of(new Todo("Read book"), new Todo("Write code"), new Todo("Sleep")));

        assertEquals(1, assertDoesNotThrow(() -> taskList.getValidIndex("2")));
    }

    @Test
    public void getValidIndex_nullInput_throwsGihunException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read book"));

        assertThrows(GihunException.class, () -> taskList.getValidIndex(null));
    }

    @Test
    public void getValidIndex_blankInput_throwsGihunException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read book"));

        assertThrows(GihunException.class, () -> taskList.getValidIndex("   "));
    }

    @Test
    public void getValidIndex_nonNumericInput_throwsGihunException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read book"));

        assertThrows(GihunException.class, () -> taskList.getValidIndex("abc"));
    }

    @Test
    public void getValidIndex_outOfRangeInput_throwsGihunException() {
        TaskList taskList = new TaskList();
        taskList.addAll(List.of(new Todo("Read book"), new Todo("Write code")));

        assertThrows(GihunException.class, () -> taskList.getValidIndex("3"));
        assertThrows(GihunException.class, () -> taskList.getValidIndex("0"));
    }

    @Test
    public void markTask_validIndex_marksSelectedTaskDone() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("Read book");
        taskList.add(todo);

        taskList.markTask(0);

        assertEquals("X", taskList.get(0).getStatusIcon());
    }

    @Test
    public void unmarkTask_doneTask_marksSelectedTaskNotDone() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("Read book");
        taskList.add(todo);
        taskList.markTask(0);

        taskList.unmarkTask(0);

        assertEquals(" ", taskList.get(0).getStatusIcon());
    }

    @Test
    public void isEmpty_emptyList_returnsTrue() {
        TaskList taskList = new TaskList();

        assertTrue(taskList.isEmpty());
    }

    @Test
    public void isEmpty_nonEmptyList_returnsFalse() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read book"));

        assertFalse(taskList.isEmpty());
    }

    @Test
    public void getMatchedTasks_blankKeyword_throwsGihunException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read book"));

        assertThrows(GihunException.class, () -> taskList.getMatchedTasks("   "));
    }

    @Test
    public void getMatchedTasks_caseInsensitiveKeyword_returnsMatchingTasks() {
        TaskList taskList = new TaskList();
        Todo readBook = new Todo("Read book");
        Todo writeCode = new Todo("Write code");
        Todo reviewNotes = new Todo("Review notes");
        taskList.addAll(List.of(readBook, writeCode, reviewNotes));

        List<Task> matchedTasks = assertDoesNotThrow(() -> taskList.getMatchedTasks("BOOK"));

        assertEquals(List.of(readBook), matchedTasks);
    }

    @Test
    public void getMatchedTasks_noMatches_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.addAll(List.of(new Todo("Read book"), new Todo("Write code")));

        List<Task> matchedTasks = assertDoesNotThrow(() -> taskList.getMatchedTasks("travel"));

        assertTrue(matchedTasks.isEmpty());
    }

    @Test
    public void asList_returnsCopyOfTasks() {
        TaskList taskList = new TaskList();
        Todo first = new Todo("Read book");
        Todo second = new Todo("Write code");
        taskList.addAll(List.of(first, second));

        List<Task> tasks = taskList.asList();
        tasks.add(new Todo("Extra task"));

        assertEquals(2, taskList.size());
        assertEquals(List.of(first, second), taskList.asList());
    }

    @Test
    public void getConflicts_overlappingDatedTasks_returnsAllIncompleteConflicts() {
        Deadline deadline = new Deadline("Deadline", LocalDateTime.of(2026, 9, 15, 10, 30));
        Event event = new Event(
                "Meeting",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 11, 0));
        Todo todo = new Todo("Todo");
        TaskList taskList = new TaskList(deadline, event, todo);

        List<TaskList.Conflict> conflicts = taskList.getConflicts(
                new Event(
                        "Workshop",
                        LocalDateTime.of(2026, 9, 15, 10, 15),
                        LocalDateTime.of(2026, 9, 15, 10, 45)));

        assertEquals(List.of(1, 2), conflicts.stream().map(TaskList.Conflict::taskNumber).toList());
    }

    @Test
    public void getConflicts_boundaryAndCompletedTasks_areIgnored() {
        Event completed = new Event(
                "Completed",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 11, 0));
        completed.markAsDone();
        Event touching = new Event(
                "Touching",
                LocalDateTime.of(2026, 9, 15, 11, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0));
        TaskList taskList = new TaskList(completed, touching);

        List<TaskList.Conflict> conflicts = taskList.getConflicts(new Deadline(
                "Deadline",
                LocalDateTime.of(2026, 9, 15, 11, 0)));

        assertTrue(conflicts.isEmpty());
    }

    @Test
    public void getConflicts_malformedExistingEvent_isIgnored() {
        Event malformed = new Event(
                "Malformed",
                LocalDateTime.of(2026, 9, 15, 11, 0),
                LocalDateTime.of(2026, 9, 15, 10, 0));
        TaskList taskList = new TaskList(malformed);

        assertTrue(taskList.getConflicts(new Deadline(
                "Deadline",
                LocalDateTime.of(2026, 9, 15, 10, 30))).isEmpty());
    }

    @Test
    public void getConflicts_equalDeadlineTimestamps_returnsConflict() {
        Deadline existing = new Deadline("Existing", LocalDateTime.of(2026, 9, 15, 10, 0));
        TaskList taskList = new TaskList(existing);

        assertEquals(1, taskList.getConflicts(
                new Deadline("New", LocalDateTime.of(2026, 9, 15, 10, 0))).size());
    }

    @Test
    public void getConflicts_touchingEvents_returnsNoConflict() {
        Event existing = new Event(
                "Existing",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 11, 0));
        TaskList taskList = new TaskList(existing);

        assertTrue(taskList.getConflicts(new Event(
                "New",
                LocalDateTime.of(2026, 9, 15, 11, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0))).isEmpty());
    }
}

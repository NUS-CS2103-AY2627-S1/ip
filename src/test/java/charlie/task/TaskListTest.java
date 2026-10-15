package charlie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import charlie.exception.CharlieException;

public class TaskListTest {
    @Test
    public void constructor_initialTasks_copiesInputList() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("first", false));
        TaskList taskList = new TaskList(initialTasks);

        initialTasks.clear();

        assertEquals(1, taskList.getSize());
    }

    @Test
    public void addGetReplaceAndDelete_validIndexes_updateList() {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("first", false);
        Todo replacementTask = new Todo("replacement", false);

        taskList.add(firstTask);
        assertEquals(firstTask, taskList.get(0));
        assertEquals(replacementTask, taskList.replace(0, replacementTask));
        assertEquals(replacementTask, taskList.get(0));
        assertEquals(replacementTask, taskList.delete(0));
        assertEquals(0, taskList.getSize());
    }

    @Test
    public void proposedTaskStates_doNotMutateOriginalList() {
        Todo firstTask = new Todo("first", false);
        Todo secondTask = new Todo("second", false);
        Todo replacementTask = new Todo("replacement", true);
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        List<Task> afterAdding = taskList.getTasksAfterAdding(replacementTask);
        List<Task> afterDeleting = taskList.getTasksAfterDeleting(0);
        List<Task> afterReplacing = taskList.getTasksAfterReplacing(0, replacementTask);
        List<Task> afterMarking = taskList.getTasksAfterChangingStatus(0, true);

        assertEquals(List.of(firstTask, secondTask, replacementTask), afterAdding);
        assertEquals(List.of(secondTask), afterDeleting);
        assertEquals(List.of(replacementTask, secondTask), afterReplacing);
        assertEquals("[T][X] first", afterMarking.get(0).toString());
        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
        assertEquals("[T][ ] first", taskList.get(0).toString());
        assertThrows(UnsupportedOperationException.class, () -> afterAdding.add(firstTask));
    }

    @Test
    public void getTasks_returnedSnapshotCannotChangeTaskMembership() {
        TaskList taskList = new TaskList(List.of(new Todo("first", false)));
        List<Task> snapshot = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        taskList.add(new Todo("second", false));

        assertEquals(1, snapshot.size());
        assertEquals(2, taskList.getSize());
    }

    @Test
    public void indexedOperations_emptyList_exceptionThrown() {
        TaskList taskList = new TaskList();

        CharlieException exception = assertThrows(CharlieException.class, () -> taskList.get(0));

        assertEquals("There are no tasks in the list.", exception.getMessage());
    }

    @Test
    public void indexedOperations_outOfRange_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("first", false)));

        CharlieException belowRange = assertThrows(
                CharlieException.class, () -> taskList.mark(-1));
        CharlieException aboveRange = assertThrows(
                CharlieException.class, () -> taskList.delete(1));

        assertEquals("Please enter a task number from 1 to 1.", belowRange.getMessage());
        assertEquals("Please enter a task number from 1 to 1.", aboveRange.getMessage());
    }

    @Test
    public void mark_changedFromUndoneToDone_returnsDoneTask() {
        Todo todo = new Todo("sample", false);
        TaskList taskList = new TaskList(List.of(todo));
        assertTrue(taskList.mark(0).isDone);
    }

    @Test
    public void unmark_changedFromDoneToUndone_returnsUndoneTask() {
        Todo todo = new Todo("sample", true);
        TaskList taskList = new TaskList(List.of(todo));

        assertEquals("[T][ ] sample", taskList.unmark(0).toString());
    }

    @Test
    public void findOnDate_allTasksMatch_returnsAllTasks() {
        TaskList taskList = new TaskList();
        for (int i = 0; i < 10; i++) {
            taskList.add(new Deadline(
                    "sample " + i,
                    false,
                    LocalDate.of(2026, 1, 1)));
        }

        assertEquals(10, taskList.findOnDate(LocalDate.of(2026, 1, 1)).size());
    }

    @Test
    public void findOnDate_noTasksMatch_returnsEmptyList() {
        TaskList taskList = new TaskList();
        for (int i = 0; i < 10; i++) {
            taskList.add(new Deadline(
                    "sample " + i,
                    false,
                    LocalDate.of(2026, 1, 1)));
        }

        assertEquals(0, taskList.findOnDate(LocalDate.of(2025, 1, 1)).size());
    }

    @Test
    public void findOnDate_dateWithinEventRange_returnsEvent() {
        TaskList taskList = new TaskList(List.of(
                new Event(
                        "sample",
                        false,
                        LocalDateTime.of(2026, 1, 1, 0, 0),
                        LocalDateTime.of(2026, 1, 10, 0, 0))));

        assertEquals(1, taskList.findOnDate(LocalDate.of(2026, 1, 5)).size());
    }

    @Test
    public void findOnDate_eventBoundaryDates_returnsEvent() {
        Event event = new Event(
                "sample",
                false,
                LocalDateTime.of(2026, 1, 1, 23, 0),
                LocalDateTime.of(2026, 1, 3, 1, 0));
        TaskList taskList = new TaskList(List.of(event, new Todo("undated", false)));

        assertEquals(List.of(event), taskList.findOnDate(LocalDate.of(2026, 1, 1)));
        assertEquals(List.of(event), taskList.findOnDate(LocalDate.of(2026, 1, 3)));
        assertTrue(taskList.findOnDate(LocalDate.of(2026, 1, 4)).isEmpty());
    }

    @Test
    public void findByKeyword_multipleMatches_returnsMatchesInOriginalOrder() {
        Todo firstMatch = new Todo("borrow book", false);
        Todo nonMatch = new Todo("submit report", false);
        Todo secondMatch = new Todo("return book", false);
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        assertEquals(List.of(firstMatch, secondMatch), taskList.findByKeyword("book"));
    }

    @Test
    public void findByKeyword_phraseMatches_returnsMatchingTask() {
        Todo matchingTask = new Todo("project meeting", false);
        TaskList taskList = new TaskList(List.of(
                new Todo("project report", false),
                matchingTask));

        assertEquals(List.of(matchingTask), taskList.findByKeyword("project meeting"));
    }

    @Test
    public void findByKeyword_mixedCaseDescriptionAndKeyword_returnsMatchingTask() {
        Todo matchingTask = new Todo("boRRoW boOk", false);
        TaskList taskList = new TaskList(List.of(matchingTask));

        assertEquals(List.of(matchingTask), taskList.findByKeyword("BOOK"));
    }

    @Test
    public void findByKeyword_noMatch_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("borrow book", false)));

        assertTrue(taskList.findByKeyword("report").isEmpty());
    }

    @Test
    public void add_sameTaskDetailsWithDifferentStatus_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("borrow book", false)));

        CharlieException exception = assertThrows(
                CharlieException.class, () -> taskList.add(new Todo("borrow book", true)));

        assertEquals("This task already exists in the list.", exception.getMessage());
        assertEquals(1, taskList.getSize());
    }

    @Test
    public void replace_detailsMatchingAnotherTask_exceptionThrown() {
        Todo firstTask = new Todo("borrow book", false);
        Todo secondTask = new Todo("return book", false);
        Todo duplicateTask = new Todo("borrow book", false);
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        CharlieException exception = assertThrows(
                CharlieException.class, () -> taskList.replace(1, duplicateTask));

        assertEquals("This task already exists in the list.", exception.getMessage());
        assertEquals(secondTask, taskList.get(1));
    }
}

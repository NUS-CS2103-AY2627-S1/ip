package pebby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void taskListConstructor_varargs_createsListContainingTasksInOrder() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("buy groceries");

        TaskList tasks = new TaskList(firstTask, secondTask);

        assertEquals(List.of(firstTask, secondTask), tasks.asList());
    }

    @Test
    void constructor_newTask_initializesDescriptionAndIncompleteState() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void markAsDone_incompleteTask_marksTaskComplete() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());
    }

    @Test
    void markAsNotDone_completedTask_marksTaskIncomplete() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void setDone_true_marksTaskComplete() {
        Task task = new Task("read book");

        task.setDone(true);

        assertTrue(task.isDone());
    }

    @Test
    void setDone_false_marksCompletedTaskIncomplete() {
        Task task = new Task("read book");
        task.markAsDone();

        task.setDone(false);

        assertFalse(task.isDone());
    }

    @Test
    void findTasks_keywordMatchesDescriptions_returnsTasksInTaskListOrder() {
        TaskList tasks = new TaskList();
        Todo firstMatch = tasks.addTodo("read book");
        tasks.addTodo("buy groceries");
        Todo secondMatch = tasks.addTodo("return book");

        List<Task> matchingTasks = tasks.findTasks("book");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void findTasks_keywordMatchesNoDescriptions_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        assertTrue(tasks.findTasks("lecture").isEmpty());
    }

    @Test
    void findTasks_emptyKeyword_returnsEveryTaskInTaskListOrder() {
        TaskList tasks = new TaskList();
        Todo firstTask = tasks.addTodo("read book");
        Todo secondTask = tasks.addTodo("buy groceries");

        assertEquals(List.of(firstTask, secondTask), tasks.findTasks(""));
    }

    @Test
    void getScheduleFor_dateIncludesDeadlinesAndEvents_thenListsIncompleteTasksFirst() {
        TaskList tasks = new TaskList();
        Deadline completedDeadline = tasks.addDeadline("submit draft", "2019-12-02");
        completedDeadline.markAsDone();
        Event spanningEvent = tasks.addEvent("project meeting", "2019-12-01", "2019-12-03");
        Deadline incompleteDeadline = tasks.addDeadline("return book", "2019-12-02");
        tasks.addTodo("read book");
        tasks.addEvent("other event", "2019-12-04", "2019-12-05");

        List<Task> scheduledTasks = tasks.getScheduleFor(LocalDate.of(2019, 12, 2));

        assertEquals(List.of(spanningEvent, incompleteDeadline, completedDeadline), scheduledTasks);
    }

    @Test
    void eventIsOn_dateWithinRange_returnsTrue() {
        Event event = new Event("project meeting", "2019-12-01", "2019-12-03");

        assertTrue(event.isOn(LocalDate.of(2019, 12, 1)));
        assertTrue(event.isOn(LocalDate.of(2019, 12, 2)));
        assertTrue(event.isOn(LocalDate.of(2019, 12, 3)));
        assertFalse(event.isOn(LocalDate.of(2019, 11, 30)));
        assertFalse(event.isOn(LocalDate.of(2019, 12, 4)));
    }

    @Test
    void addTodo_duplicateDetails_throwsException() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> tasks.addTodo("read book"));

        assertEquals("An identical task is already in the list.", exception.getMessage());
    }

    @Test
    void event_constructorStartIsNotBeforeEnd_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Event("meeting", "2019-12-03", "2019-12-03"));

        assertEquals("The start date must be before the end date.", exception.getMessage());
    }

    @Test
    void deadline_constructorWrittenDate_createsDeadline() {
        Deadline deadline = new Deadline("return book", "15 June 2026");

        assertEquals("2026-06-15", deadline.getBy());
    }

    @Test
    void deadline_parseDate_acceptsIsoAndCaseInsensitiveWrittenDates() {
        assertEquals(LocalDate.of(2026, 6, 15), Deadline.parseDate("2026-06-15"));
        assertEquals(LocalDate.of(2026, 6, 15), Deadline.parseDate("15 jUnE 2026"));
    }

    @Test
    void parseDate_invalidDates_showsHelpfulException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Deadline.parseDate("2026-02-29"));

        assertTrue(exception.getMessage().contains("yyyy-MM-dd or d MMMM yyyy"));
    }

    @Test
    void task_constructorInvalidDescriptions_throwsSpecificExceptions() {
        IllegalArgumentException blankDescriptionException = assertThrows(
                IllegalArgumentException.class, () -> new Todo(" "));
        IllegalArgumentException paddedDescriptionException = assertThrows(
                IllegalArgumentException.class, () -> new Todo(" read"));
        IllegalArgumentException controlCharacterException = assertThrows(
                IllegalArgumentException.class, () -> new Todo("read\nbook"));

        assertEquals("Please provide a description.", blankDescriptionException.getMessage());
        assertEquals("Task descriptions cannot start or end with spaces.",
                paddedDescriptionException.getMessage());
        assertEquals("Task descriptions cannot contain control characters.",
                controlCharacterException.getMessage());
    }

    @Test
    void taskHasSameDetails_sameTypeAndDetails_returnsTrueRegardlessOfStatus() {
        Todo firstTodo = new Todo("read book");
        Todo secondTodo = new Todo("read book");
        secondTodo.markAsDone();

        assertTrue(firstTodo.hasSameDetails(secondTodo));
        assertFalse(firstTodo.hasSameDetails(new Deadline("read book", "2026-06-15")));
        assertFalse(firstTodo.hasSameDetails(null));
    }

    @Test
    void taskListAsList_returnsUnmodifiableCopyAndDeleteReturnsRemovedTask() {
        TaskList tasks = new TaskList();
        Todo todo = tasks.addTodo("read book");
        List<Task> snapshot = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("buy milk")));
        assertEquals(todo, tasks.delete(0));
        assertEquals(0, tasks.size());
        assertEquals(List.of(todo), snapshot);
    }

    @Test
    void taskList_duplicateDetailsWithDifferentDates_addsDistinctDatedTasks() {
        TaskList tasks = new TaskList();

        tasks.addDeadline("return book", "2026-06-15");
        tasks.addDeadline("return book", "2026-06-16");
        tasks.addEvent("meeting", "2026-06-15", "2026-06-16");
        tasks.addEvent("meeting", "2026-06-16", "2026-06-17");

        assertEquals(4, tasks.size());
    }
}

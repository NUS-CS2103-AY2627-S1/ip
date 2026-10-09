package sumo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import sumo.exception.SumoException;

/** Tests the collection operations and date filtering performed by {@link TaskList}. */
public class TaskListTest {
    /** Verifies that construction takes a defensive copy of the source list. */
    @Test
    public void constructor_sourceListChanged_taskListUnaffected() {
        List<Task> source = new java.util.ArrayList<>();
        source.add(new Todo("first"));
        TaskList taskList = new TaskList(source);

        source.add(new Todo("second"));

        assertEquals(1, taskList.size());
    }

    /** Verifies that callers cannot mutate the returned task snapshot. */
    @Test
    public void getTasks_returnedSnapshotCannotBeModified() {
        TaskList taskList = new TaskList(List.of(new Todo("first")));

        List<Task> snapshot = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("second")));
        assertEquals(1, taskList.size());
    }

    /** Verifies add, delete, and insertion order. */
    @Test
    public void addDeleteAndInsert_tasksRemainInExpectedOrder() throws SumoException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList taskList = new TaskList();
        taskList.add(first);
        taskList.add(second);

        Task removed = taskList.delete(0);
        taskList.insert(0, removed);

        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    public void add_duplicateWithDifferentCaseSpacingAndStatus_rejected() throws SumoException {
        Todo original = new Todo("read book");
        original.markAsDone();
        TaskList tasks = new TaskList(List.of(original));

        assertThrows(SumoException.class, () -> tasks.add(new Todo("  READ   book  ")));
        assertEquals(List.of(original), tasks.getTasks());
    }

    @Test
    public void add_datedDuplicates_rejectedButDifferentDatesAndTypesAllowed() throws SumoException {
        LocalDate date = LocalDate.of(2026, 2, 3);
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("report", date));
        tasks.add(new Event("report", date, date.plusDays(1)));

        assertThrows(SumoException.class, () -> tasks.add(new Deadline("REPORT", date.atStartOfDay())));
        assertThrows(SumoException.class, () -> tasks.add(new Event("report", date, date.plusDays(1))));
        tasks.add(new Todo("report"));
        tasks.add(new Deadline("report", date.plusDays(1)));
        tasks.add(new Event("report", date, date.plusDays(2)));
        assertEquals(5, tasks.size());
    }

    /** Verifies that completion status can be set and cleared. */
    @Test
    public void setDone_trueThenFalse_completionStateUpdated() {
        Todo todo = new Todo("read");
        TaskList taskList = new TaskList(List.of(todo));

        taskList.setDone(0, true);
        assertTrue(todo.isDone());

        taskList.setDone(0, false);
        assertFalse(todo.isDone());
    }

    /** Verifies matching deadlines are returned for a date. */
    @Test
    public void findOn_deadlineOnRequestedDate_deadlineReturned() {
        Deadline matching = new Deadline("submit", LocalDate.of(2026, 2, 3));
        Deadline other = new Deadline("renew", LocalDate.of(2026, 2, 4));
        TaskList taskList = new TaskList(List.of(matching, other));

        assertEquals(List.of(matching), taskList.findOn(LocalDate.of(2026, 2, 3)));
    }

    /** Verifies that both event-range boundaries are inclusive. */
    @Test
    public void findOn_eventRangeBoundaries_eventsReturnedOnBothBoundaries() {
        Event event = new Event("camp", LocalDate.of(2026, 2, 3), LocalDate.of(2026, 2, 5));
        TaskList taskList = new TaskList(List.of(event));

        assertEquals(List.of(event), taskList.findOn(LocalDate.of(2026, 2, 3)));
        assertEquals(List.of(event), taskList.findOn(LocalDate.of(2026, 2, 5)));
    }

    /** Verifies that todos and out-of-range events do not match. */
    @Test
    public void findOn_outsideEventRangeAndTodo_noTasksReturned() {
        Event event = new Event("camp", LocalDate.of(2026, 2, 3), LocalDate.of(2026, 2, 5));
        Todo todo = new Todo("pack");
        TaskList taskList = new TaskList(List.of(event, todo));

        assertTrue(taskList.findOn(LocalDate.of(2026, 2, 2)).isEmpty());
        assertTrue(taskList.findOn(LocalDate.of(2026, 2, 6)).isEmpty());
    }

    /** Verifies that matching tasks retain their original order. */
    @Test
    public void findOn_multipleMatches_originalOrderPreserved() {
        Event first = new Event("camp", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 5));
        Deadline second = new Deadline("submit", LocalDate.of(2026, 2, 3));
        TaskList taskList = new TaskList(List.of(first, new Todo("pack"), second));

        assertEquals(List.of(first, second), taskList.findOn(LocalDate.of(2026, 2, 3)));
    }

    @Test
    public void find_matchingDescriptions_matchesInOriginalOrder() {
        Todo first = new Todo("read book");
        Deadline second = new Deadline("return book", LocalDate.of(2026, 2, 3));
        TaskList taskList = new TaskList(List.of(first, new Todo("write notes"), second));

        assertEquals(List.of(first, second), taskList.find("book"));
    }

    @Test
    public void find_keywordWithDifferentCase_matchesInOriginalOrder() {
        Todo first = new Todo("read BOOK");
        Todo second = new Todo("Book a room");
        TaskList taskList = new TaskList(List.of(first, new Todo("write notes"), second));

        assertEquals(List.of(first, second), taskList.find("bOoK"));
        assertEquals("read BOOK", first.getDescription());
        assertTrue(taskList.find("missing").isEmpty());
    }

    @Test
    public void find_turkishDefaultLocale_matchesIndependentlyOfSystemLocale() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Todo task = new Todo("FINISH report");
            TaskList taskList = new TaskList(List.of(task));

            assertEquals(List.of(task), taskList.find("finish"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void getChronologicallySortedTasks_mixedTasks_sortedByStatusAndDates() {
        Todo firstTodo = new Todo("first todo");
        Event laterEvent = new Event("later event", LocalDateTime.of(2026, 2, 1, 5, 0),
                LocalDateTime.of(2026, 2, 10, 17, 0));
        Deadline deadline = new Deadline("deadline", LocalDate.of(2026, 2, 9));
        Event earlierEvent = new Event("earlier event", LocalDateTime.of(2026, 2, 3, 9, 0),
                LocalDateTime.of(2026, 2, 10, 17, 0));
        Todo secondTodo = new Todo("second todo");
        Deadline completedDeadline = new Deadline("completed deadline", LocalDate.of(2026, 1, 1));
        completedDeadline.markAsDone();
        TaskList taskList = new TaskList(List.of(firstTodo, laterEvent, deadline, earlierEvent,
                secondTodo, completedDeadline));

        assertEquals(List.of(deadline, laterEvent, earlierEvent, firstTodo, secondTodo, completedDeadline),
                taskList.getChronologicallySortedTasks());
        assertEquals(List.of(firstTodo, laterEvent, deadline, earlierEvent, secondTodo, completedDeadline),
                taskList.getTasks());
    }

    @Test
    public void getChronologicallySortedTasks_equalPrimaryValues_preservesExistingOrder() {
        Event firstEvent = new Event("first event", LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 10));
        Deadline secondTask = new Deadline("second task", LocalDate.of(2026, 2, 10));
        Event thirdEvent = new Event("third event", LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 10));
        TaskList taskList = new TaskList(List.of(firstEvent, secondTask, thirdEvent));

        assertEquals(List.of(firstEvent, secondTask, thirdEvent), taskList.getChronologicallySortedTasks());
    }
}

package anaconda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import anaconda.exception.AnacondaException;
import anaconda.parser.Command;

/**
 * Tests ownership of the collection, one-based task operations, and inclusive/exact date filters.
 */
public class TaskListTest {
    @Test
    public void constructor_inputCollection_copiesStructureButSharesTasks() {
        Task task = new ToDo("book");
        ArrayList<Task> originalTasks = new ArrayList<>(List.of(task));
        TaskList tasks = new TaskList(originalTasks);
        originalTasks.clear();
        assertEquals(1, tasks.size());
        assertSame(task, tasks.asList().getFirst());
        tasks.add(new ToDo("another"));
        assertTrue(originalTasks.isEmpty());
    }

    @Test
    public void add_multipleTasks_preservesOrderAndAllowsDuplicateDescriptions() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
        Task first = new ToDo("book");
        Task second = new ToDo("book");
        tasks.add(first);
        tasks.add(second);
        assertEquals(2, tasks.size());
        assertEquals(List.of(first, second), tasks.asList());
    }

    @Test
    public void hasDuplicate_matchingTasks_ignoresCaseAndCompletionWithoutChangingList() {
        LocalDate date = LocalDate.of(2026, 9, 13);
        List<Task> originals = List.of(new ToDo("Read book"), new Deadline("Report", date),
                new Event("Meeting", date, date.plusDays(1)));
        List<Task> candidates = List.of(new ToDo("read BOOK"), new Deadline("REPORT", date),
                new Event("meeting", date, date.plusDays(1)));
        TaskList tasks = new TaskList(originals);
        for (int i = 0; i < originals.size(); i++) {
            assertTrue(tasks.hasDuplicate(candidates.get(i)));
            originals.get(i).markAsDone();
            assertTrue(tasks.hasDuplicate(candidates.get(i)));
        }
        assertEquals(originals, tasks.asList());
        assertTrue(originals.stream().allMatch(Task::isDone));
        assertTrue(candidates.stream().noneMatch(Task::isDone));
    }

    @Test
    public void hasDuplicate_differentDetailsOrEmptyList_returnsFalse() {
        LocalDate date = LocalDate.of(2026, 9, 13);
        assertFalse(new TaskList().hasDuplicate(new ToDo("book")));
        TaskList todos = new TaskList(List.of(new ToDo("book")));
        assertFalse(todos.hasDuplicate(new ToDo("books")));
        assertFalse(todos.hasDuplicate(new Deadline("book", date)));
        assertFalse(todos.hasDuplicate(new Event("book", date, date)));
        TaskList deadlines = new TaskList(List.of(new Deadline("book", date)));
        assertFalse(deadlines.hasDuplicate(new Deadline("book", date.plusDays(1))));
        assertFalse(deadlines.hasDuplicate(new Event("book", date, date)));
        TaskList events = new TaskList(List.of(new Event("book", date, date.plusDays(2))));
        assertFalse(events.hasDuplicate(new Event("book", date.plusDays(1), date.plusDays(2))));
        assertFalse(events.hasDuplicate(new Event("book", date, date.plusDays(3))));
        todos.clear();
        assertFalse(todos.hasDuplicate(new ToDo("book")));
    }

    @Test
    public void delete_firstMiddleAndLastTask_returnsRemovedTaskAndRenumbers() throws AnacondaException {
        for (int number : new int[] {1, 2, 3}) {
            ArrayList<Task> expectedTasks =
                    new ArrayList<>(List.of(new ToDo("a"), new ToDo("b"), new ToDo("c")));
            TaskList tasks = new TaskList(expectedTasks);
            Task removed = expectedTasks.remove(number - 1);
            assertSame(removed, tasks.delete(number));
            assertEquals(expectedTasks, tasks.asList());
            assertEquals(2, tasks.size());
            assertSame(expectedTasks.getFirst(), tasks.delete(1));
            assertSame(expectedTasks.getLast(), tasks.delete(1));
            assertTrue(tasks.asList().isEmpty());
        }
    }

    @Test
    public void delete_invalidTaskNumber_throwsWithoutChangingList() {
        Task task = new ToDo("book");
        TaskList tasks = new TaskList(List.of(task));
        for (int number : new int[] {Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE}) {
            assertEquals("Task " + number + " does not exist.",
                    assertThrows(AnacondaException.class, () -> tasks.delete(number)).getMessage());
            assertEquals(List.of(task), tasks.asList());
        }
        assertThrows(AnacondaException.class, () -> new TaskList().delete(1));
    }

    @Test
    public void mark_validTask_updatesOnlySelectedTaskAndSupportsRepeatedChanges() throws AnacondaException {
        Task first = new ToDo("first");
        Task last = new ToDo("last");
        TaskList tasks = new TaskList(List.of(first, last));
        assertSame(last, tasks.mark(2, true));
        assertTrue(last.isDone());
        assertFalse(first.isDone());
        tasks.mark(2, true);
        assertTrue(last.isDone());
        assertSame(last, tasks.mark(2, false));
        tasks.mark(2, false);
        assertFalse(last.isDone());
        tasks.mark(1, true);
        assertTrue(first.isDone());
        assertEquals(List.of(first, last), tasks.asList());
    }

    @Test
    public void mark_taskIgnoresDoneUpdate_throwsAssertionError() {
        Task task = new ToDo("broken completion update") {
            @Override
            public void markAsDone() {
                // Simulate a subtype that violates Task's completion contract.
            }
        };
        TaskList tasks = new TaskList(List.of(task));

        assertEquals("Task completion status must match the requested state",
                assertThrows(AssertionError.class, () -> tasks.mark(1, true)).getMessage());
    }

    @Test
    public void mark_taskIgnoresUndoneUpdate_throwsAssertionError() {
        Task task = new ToDo("broken incomplete update") {
            @Override
            public void markAsUndone() {
                // Simulate a subtype that violates Task's completion contract.
            }
        };
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        assertEquals("Task completion status must match the requested state",
                assertThrows(AssertionError.class, () -> tasks.mark(1, false)).getMessage());
    }

    @Test
    public void mark_invalidTaskNumber_throwsWithoutChangingTaskState() {
        Task task = new ToDo("book");
        TaskList tasks = new TaskList(List.of(task));
        for (int number : new int[] {Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE}) {
            for (boolean isDone : new boolean[] {true, false}) {
                assertEquals("Task " + number + " does not exist.",
                        assertThrows(AnacondaException.class, () ->
                                tasks.mark(number, isDone)).getMessage());
                assertFalse(task.isDone());
                assertEquals(List.of(task), tasks.asList());
            }
        }
        assertThrows(AnacondaException.class, () -> new TaskList().mark(1, true));
    }

    @Test
    public void clear_populatedOrEmptyList_allowsAddingAgain() {
        TaskList tasks = new TaskList(List.of(new ToDo("book")));
        tasks.clear();
        tasks.clear();
        assertEquals(0, tasks.size());
        assertTrue(tasks.asList().isEmpty());
        Task newTask = new ToDo("new");
        tasks.add(newTask);
        assertEquals(List.of(newTask), tasks.asList());
    }

    @Test
    public void asList_returnedSnapshot_cannotChangeCollectionAndDoesNotTrackAdditions() {
        Task first = new ToDo("first");
        TaskList tasks = new TaskList(List.of(first));
        List<Task> snapshotTasks = tasks.asList();
        assertThrows(UnsupportedOperationException.class, () -> snapshotTasks.add(new ToDo("injected")));
        assertThrows(UnsupportedOperationException.class, () -> snapshotTasks.remove(0));
        tasks.add(new ToDo("second"));
        first.markAsDone();
        assertEquals(List.of(first), snapshotTasks);
        assertTrue(snapshotTasks.getFirst().isDone());
        assertEquals(2, tasks.size());
    }

    @Test
    public void snapshot_laterMutations_restoresOriginalOrderTypesAndStatuses() throws AnacondaException {
        LocalDate date = LocalDate.of(2026, 9, 10);
        Task todo = new ToDo("book");
        Task deadline = new Deadline("report", date);
        Task event = new Event("meeting", date.minusDays(1), date);
        deadline.markAsDone();
        TaskList tasks = new TaskList(List.of(todo, deadline, event));
        TaskList.Snapshot snapshot = tasks.snapshot();

        tasks.mark(1, true);
        tasks.mark(2, false);
        tasks.delete(2);
        tasks.clear();
        tasks.add(new ToDo("replacement"));
        tasks.restore(snapshot);

        assertEquals(List.of(todo, deadline, event), tasks.asList());
        assertFalse(todo.isDone());
        assertTrue(deadline.isDone());
        assertSame(event, tasks.asList().getLast());
        assertEquals(date, deadline.getEndDate());
        assertEquals(date, event.getEndDate());
    }

    @Test
    public void restore_multipleSnapshots_canReuseEachCapturedState() throws AnacondaException {
        TaskList tasks = new TaskList();
        TaskList.Snapshot empty = tasks.snapshot();
        Task task = new ToDo("book");
        tasks.add(task);
        TaskList.Snapshot incomplete = tasks.snapshot();
        tasks.mark(1, true);
        TaskList.Snapshot completed = tasks.snapshot();

        tasks.restore(incomplete);
        assertFalse(task.isDone());
        tasks.restore(completed);
        assertTrue(task.isDone());
        tasks.restore(empty);
        assertTrue(tasks.asList().isEmpty());
        tasks.restore(incomplete);
        assertEquals(List.of(task), tasks.asList());
        assertFalse(task.isDone());
    }

    @Test
    public void find_keywordMatchesDescriptions_ignoresCaseAndPreservesOrderAndState() {
        Task first = new ToDo("Read Book");
        Task second = new Deadline("return book", LocalDate.of(2026, 8, 19));
        Task dateOnlyMatch = new Deadline("submit report", LocalDate.of(2026, 8, 20));
        second.markAsDone();
        TaskList tasks = new TaskList(List.of(first, dateOnlyMatch, second));

        assertEquals(List.of(first, second), tasks.find("BOOK"));
        assertEquals(List.of(first), tasks.find("read bo"));
        assertTrue(tasks.find("2026").isEmpty());
        assertEquals(List.of(first, dateOnlyMatch, second), tasks.asList());
        assertTrue(second.isDone());
    }

    @Test
    public void find_duplicateMatches_preservesOccurrencesAndTaskIdentity() {
        Task repeated = new ToDo("Read book");
        Task another = new ToDo("Read book");
        TaskList tasks = new TaskList(List.of(repeated, new ToDo("unrelated"), another, repeated));

        List<Task> matches = tasks.find("BOOK");

        assertEquals(List.of(repeated, another, repeated), matches);
        assertSame(repeated, matches.getFirst());
        assertSame(repeated, matches.getLast());
        assertEquals(4, tasks.size());
    }

    @Test
    public void find_emptyList_returnsUnmodifiableEmptyResult() {
        List<Task> matches = new TaskList().find("book");
        assertTrue(matches.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> matches.add(new ToDo("book")));
    }

    @Test
    public void find_noMatchesOrLaterListChanges_returnsIndependentUnmodifiableSnapshot() {
        Task task = new ToDo("book");
        TaskList tasks = new TaskList(List.of(task));
        assertTrue(tasks.find("missing").isEmpty());

        List<Task> matches = tasks.find("book");
        assertThrows(UnsupportedOperationException.class, matches::clear);
        tasks.clear();
        assertEquals(List.of(task), matches);
    }

    @Test
    public void filterByDate_boundaryDates_includesEqualityAndUsesEventEndRatherThanStart() {
        LocalDate date = LocalDate.of(2026, 8, 19);
        Task before = new Deadline("before", date.minusDays(1));
        Task exact = new Deadline("exact", date);
        Task after = new Deadline("after", date.plusDays(1));
        Task eventBefore = new Event("before event", date.minusDays(3), date.minusDays(1));
        Task eventExact = new Event("exact event", date.minusDays(2), date);
        Task spanning = new Event("spanning", date.minusDays(1), date.plusDays(2));
        exact.markAsDone();
        List<Task> originalTasks =
                List.of(after, eventExact, new ToDo("undated"), before, exact, spanning, eventBefore);
        TaskList tasks = new TaskList(originalTasks);

        assertEquals(List.of(eventExact, before, exact, eventBefore),
                tasks.filterByDate(date, Command.BY, false));
        assertEquals(List.of(after, eventExact, exact, spanning),
                tasks.filterByDate(date, Command.FROM, false));
        for (Command command : new Command[] {Command.BY, Command.FROM}) {
            assertEquals(List.of(eventExact, exact), tasks.filterByDate(date, command, true));
        }
        assertEquals(originalTasks, tasks.asList());
        assertTrue(exact.isDone());
    }

    @Test
    public void filterByDate_emptyUndatedOrNoMatchingTasks_returnsEmptyList() {
        LocalDate date = LocalDate.of(2026, 8, 19);
        for (Command command : new Command[] {Command.BY, Command.FROM}) {
            for (boolean isSharp : new boolean[] {false, true}) {
                assertTrue(new TaskList().filterByDate(date, command, isSharp).isEmpty());
                assertTrue(new TaskList(List.of(new ToDo("book")))
                        .filterByDate(date, command, isSharp).isEmpty());
            }
        }
        TaskList tasks = new TaskList(List.of(new Deadline("book", date)));
        assertTrue(tasks.filterByDate(date.minusDays(1), Command.BY, false).isEmpty());
        assertTrue(tasks.filterByDate(date.plusDays(1), Command.FROM, false).isEmpty());
        assertTrue(tasks.filterByDate(date.plusDays(1), Command.BY, true).isEmpty());
    }

    @Test
    public void filterByDate_nonFilterCommand_throwsWithoutChangingTasks() {
        LocalDate date = LocalDate.of(2026, 8, 19);
        Task task = new Deadline("book", date);
        TaskList tasks = new TaskList(List.of(task));
        for (Command command : Command.values()) {
            if (command == Command.BY || command == Command.FROM) {
                continue;
            }
            for (boolean isSharp : new boolean[] {false, true}) {
                assertEquals("Command does not filter by date: " + command,
                        assertThrows(IllegalArgumentException.class, () ->
                                tasks.filterByDate(date, command, isSharp)).getMessage());
            }
        }
        assertEquals(List.of(task), tasks.asList());
        assertFalse(task.isDone());
    }

    @Test
    public void filterByDate_emptyListWithInvalidDirection_stillRejectsDirection() {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2026, 8, 19);
        for (boolean isSharp : new boolean[] {false, true}) {
            assertThrows(IllegalArgumentException.class, () -> tasks.filterByDate(date, Command.LIST, isSharp));
            assertThrows(IllegalArgumentException.class, () -> tasks.filterByDate(date, null, isSharp));
        }
    }

    @Test
    public void filterByDate_duplicateMatches_preservesOccurrencesAndTaskIdentity() {
        LocalDate date = LocalDate.of(2026, 8, 19);
        Task repeated = new Deadline("book", date);
        Task another = new Event("meeting", date.minusDays(1), date);
        TaskList tasks = new TaskList(List.of(repeated, new ToDo("undated"), another, repeated));

        for (Command direction : new Command[] {Command.BY, Command.FROM}) {
            for (boolean isSharp : new boolean[] {false, true}) {
                List<Task> matches = tasks.filterByDate(date, direction, isSharp);
                assertEquals(List.of(repeated, another, repeated), matches);
                assertSame(repeated, matches.getFirst());
                assertSame(repeated, matches.getLast());
            }
        }
        assertEquals(4, tasks.size());
    }

    @Test
    public void filterByDate_result_isAnUnmodifiableSnapshot() {
        LocalDate date = LocalDate.of(2026, 8, 19);
        Task task = new Deadline("book", date);
        TaskList tasks = new TaskList(List.of(task));
        List<Task> matchingTasks = tasks.filterByDate(date, Command.BY, false);
        assertThrows(UnsupportedOperationException.class, matchingTasks::clear);
        tasks.clear();
        assertEquals(List.of(task), matchingTasks);
    }
}

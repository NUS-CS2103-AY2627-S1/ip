package groot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-collection operations performed by {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void collections_mutationsAndSnapshots_preserveOwnershipAndOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        ArrayList<Task> input = new ArrayList<>(List.of(first));
        TaskList tasks = new TaskList(input);
        input.clear();
        assertEquals(1, tasks.size());
        List<Task> snapshot = tasks.asList();
        tasks.add(second);
        assertEquals(List.of(first), snapshot);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(second));
        assertSame(first, tasks.delete(0));
        tasks.add(0, first);
        tasks.add(tasks.size(), new Todo("third"));
        assertEquals(List.of(first, second), tasks.asList().subList(0, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(tasks.size()));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(5, first));
        assertEquals(3, tasks.size());
        assertThrows(UnsupportedOperationException.class, () -> tasks.find("first").clear());
        assertThrows(UnsupportedOperationException.class, () -> tasks.getSortedDeadlineIndices(false).add(0));
    }

    @Test
    public void markAsDone_allTaskTypes_completesOnlySelectedTask() {
        TaskList tasks = createMixedTasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            assertSame(task, tasks.markAsDone(i));
            assertSame(task, tasks.markAsDone(i));
            assertTrue(task.isDone());
            for (int j = i + 1; j < tasks.size(); j++) {
                assertFalse(tasks.get(j).isDone());
            }
        }
    }

    @Test
    public void markAsNotDone_allTaskTypes_unmarksOnlySelectedTask() {
        TaskList tasks = createMixedTasks();
        for (Task task : tasks.asList()) {
            task.markAsDone();
        }
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            assertSame(task, tasks.markAsNotDone(i));
            assertSame(task, tasks.markAsNotDone(i));
            assertFalse(task.isDone());
            for (int j = i + 1; j < tasks.size(); j++) {
                assertTrue(tasks.get(j).isDone());
            }
        }
    }

    @Test
    public void setDone_allTaskTypes_restoresBothStatuses() {
        TaskList tasks = createMixedTasks();
        for (int i = 0; i < tasks.size(); i++) {
            tasks.setDone(i, true);
            assertTrue(tasks.get(i).isDone());
            tasks.setDone(i, false);
            assertFalse(tasks.get(i).isDone());
        }
    }

    @Test
    public void markAsDone_brokenTaskSubtype_assertionThrown() {
        TaskList tasks = new TaskList(new FrozenTask(false));
        AssertionError error = assertThrows(AssertionError.class, () -> tasks.markAsDone(0));
        assertEquals("Marking a task must leave it completed", error.getMessage());
    }

    @Test
    public void markAsNotDone_brokenTaskSubtype_assertionThrown() {
        TaskList tasks = new TaskList(new FrozenTask(true));
        AssertionError error = assertThrows(AssertionError.class, () -> tasks.markAsNotDone(0));
        assertEquals("Unmarking a task must leave it incomplete", error.getMessage());
    }

    @Test
    public void setDone_brokenTaskSubtype_assertionThrownForBothStatuses() {
        TaskList incompleteTasks = new TaskList(new FrozenTask(false));
        TaskList completedTasks = new TaskList(new FrozenTask(true));
        AssertionError markError = assertThrows(AssertionError.class, () -> incompleteTasks.setDone(0, true));
        AssertionError unmarkError = assertThrows(AssertionError.class, () -> completedTasks.setDone(0, false));
        assertEquals("Restored status must match the requested status", markError.getMessage());
        assertEquals(markError.getMessage(), unmarkError.getMessage());
    }

    /**
     * Creates one task of each production subtype to check their shared status contract.
     */
    private TaskList createMixedTasks() {
        return new TaskList(new Todo("read book"),
                new Deadline("submit report", LocalDate.of(2026, 9, 10)),
                new Event("team meeting", "2pm", "3pm"));
    }

    /**
     * Deliberately violates the status contract to verify that assertions detect broken subclasses.
     */
    private static class FrozenTask extends Task {
        private FrozenTask(boolean isDone) {
            super("broken task");
            this.isDone = isDone;
        }

        @Override
        public void markAsDone() {
            // Deliberately leave the status unchanged to simulate an implementation bug.
        }

        @Override
        public void markAsNotDone() {
            // Deliberately leave the status unchanged to simulate an implementation bug.
        }
    }

    /**
     * Verifies that find matches descriptions without regard to case and preserves list order.
     */
    @Test
    public void find_mixedCaseKeyword_returnsMatchingTasksInOrder() {
        Task firstMatch = new Todo("Read Book");
        Task nonMatch = new Todo("buy groceries");
        Task secondMatch = new Deadline("return book", LocalDate.of(2026, 8, 31));
        TaskList tasks = new TaskList(firstMatch, nonMatch, secondMatch);

        assertEquals(List.of(firstMatch, secondMatch), tasks.find("BOOK"));
    }

    /**
     * Verifies that find supports phrases within descriptions.
     */
    @Test
    public void find_phraseKeyword_returnsMatchingTask() {
        Task matchingTask = new Event("project team meeting", "2pm", "3pm");
        TaskList tasks = new TaskList(
                matchingTask,
                new Todo("project report"),
                new Todo("team lunch"));

        assertEquals(List.of(matchingTask), tasks.find("team meeting"));
    }

    /**
     * Verifies that find does not search deadline or event metadata.
     */
    @Test
    public void find_keywordOnlyInMetadata_returnsEmptyList() {
        TaskList tasks = new TaskList(
                new Deadline("submit report", LocalDate.of(2026, 8, 31)),
                new Event("project meeting", "Monday", "Tuesday"));

        assertEquals(List.of(), tasks.find("2026"));
        assertEquals(List.of(), tasks.find("Monday"));
    }

    /**
     * Verifies that find returns no matches when no task description contains the keyword.
     */
    @Test
    public void find_noMatchingDescription_returnsEmptyList() {
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Deadline("submit report", LocalDate.of(2026, 8, 31)));

        assertEquals(List.of(), tasks.find("groceries"));
    }

    @Test
    public void getSortedDeadlineIndices_mixedTasksAndDates_returnsOnlyDeadlinesChronologically() {
        Deadline completedDeadline = new Deadline("leap day", LocalDate.of(2024, 2, 29));
        completedDeadline.markAsDone();
        TaskList tasks = new TaskList(new Todo("todo"),
                new Deadline("next year", LocalDate.of(2025, 1, 1)), completedDeadline,
                new Event("meeting", "Mon 2pm", "4pm"),
                new Deadline("previous year", LocalDate.of(2023, 12, 31)),
                new Deadline("next month", LocalDate.of(2024, 3, 1)));
        List<Task> originalOrder = tasks.asList();
        List<String> originalData = originalOrder.stream().map(Task::toDataString).toList();

        assertEquals(List.of(4, 2, 5, 1), tasks.getSortedDeadlineIndices(false));
        assertEquals(List.of(1, 5, 2, 4), tasks.getSortedDeadlineIndices(true));
        assertEquals(List.of(4, 2, 5, 1), tasks.getSortedDeadlineIndices(false));
        assertEquals(originalOrder, tasks.asList());
        assertEquals(originalData, tasks.asList().stream().map(Task::toDataString).toList());
        assertTrue(completedDeadline.isDone());
    }

    @Test
    public void getSortedDeadlineIndices_equalDates_keepsAlphabeticalTiesInBothDirections() {
        LocalDate date = LocalDate.of(2026, 9, 12);
        TaskList tasks = new TaskList(new Deadline("beta", date), new Deadline("Alpha", date),
                new Deadline("alpha", date), new Deadline("Alpha", date),
                new Deadline("earlier", date.minusDays(1)), new Deadline("later", date.plusDays(1)));

        assertEquals(List.of(4, 1, 2, 3, 0, 5), tasks.getSortedDeadlineIndices(false));
        assertEquals(List.of(5, 1, 2, 3, 0, 4), tasks.getSortedDeadlineIndices(true));
        assertEquals(List.of(4, 1, 2, 3, 0, 5), tasks.getSortedDeadlineIndices(false));
    }

    @Test
    public void getSortedDeadlineIndices_noDeadlines_returnsEmptyList() {
        TaskList tasks = new TaskList(new Todo("todo"), new Event("event", "Mon", "Tue"));
        for (boolean isReversed : new boolean[]{false, true}) {
            assertEquals(List.of(), new TaskList().getSortedDeadlineIndices(isReversed));
            assertEquals(List.of(), tasks.getSortedDeadlineIndices(isReversed));
        }
    }

    @Test
    public void getSortedDeadlineIndices_singleDeadline_returnsOriginalIndex() {
        TaskList tasks = new TaskList(new Todo("todo"), new Deadline("report", LocalDate.of(2026, 9, 12)));
        assertEquals(List.of(1), tasks.getSortedDeadlineIndices(false));
        assertEquals(List.of(1), tasks.getSortedDeadlineIndices(true));
        assertThrows(UnsupportedOperationException.class, () -> tasks.getSortedDeadlineIndices(false).add(0));
    }

    @Test
    public void getSortedDeadlineIndices_afterDeletionAndAddition_recomputesMainListIndices() {
        LocalDate date = LocalDate.of(2026, 9, 12);
        TaskList tasks = new TaskList(new Todo("todo"), new Deadline("later", date.plusDays(1)),
                new Deadline("earlier", date));
        assertEquals(List.of(2, 1), tasks.getSortedDeadlineIndices(false));

        tasks.delete(1);
        tasks.add(new Deadline("earliest", date.minusDays(1)));

        assertEquals(List.of(2, 1), tasks.getSortedDeadlineIndices(false));
        assertEquals("todo", tasks.get(0).getDescription());
        assertEquals("earlier", tasks.get(1).getDescription());
        assertEquals("earliest", tasks.get(2).getDescription());
    }

    /**
     * Verifies that find returns no matches for an empty task list.
     */
    @Test
    public void find_emptyTaskList_returnsEmptyList() {
        assertEquals(List.of(), new TaskList().find("book"));
    }
}

package dingleberry.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/** Tests task-list operations that work across multiple tasks. */
class TaskListTest {
    /** Defines the year used by date-based task fixtures. */
    private static final int TEST_YEAR = 2026;
    /** Defines the month used by date-based task fixtures. */
    private static final int TEST_MONTH = 8;
    /** Defines the first day used by date-based task fixtures. */
    private static final int FIRST_TEST_DAY = 25;
    /** Defines the second day used by date-based task fixtures. */
    private static final int SECOND_TEST_DAY = 26;
    /** Defines the hour used by date-based task fixtures. */
    private static final int TEST_HOUR = 18;
    /** Defines the event start hour used by date-based task fixtures. */
    private static final int EVENT_START_HOUR = 14;
    /** Defines the event end hour used by date-based task fixtures. */
    private static final int EVENT_END_HOUR = 15;
    /** Defines the later event end hour used by date-based task fixtures. */
    private static final int LATER_EVENT_END_HOUR = 16;

    @Test
    void constructorSourceListChangesDoesNotChangeTaskList() {
        ArrayList<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new Todo("read chapter one"));
        TaskList taskList = new TaskList(sourceTasks);

        sourceTasks.add(new Todo("buy milk"));

        assertEquals(1, taskList.size());
    }

    @Test
    void asArrayListReturnedListChangesDoesNotChangeTaskList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read chapter one"));
        ArrayList<Task> copiedTasks = taskList.asArrayList();

        copiedTasks.add(new Todo("buy milk"));

        assertEquals(1, taskList.size());
    }

    @Test
    void findByKeywordMatchingTasksReturnsOnlyMatches() {
        TaskList taskList = new TaskList(
            new Todo("read chapter one"),
            new Todo("buy milk"),
            new Todo("read chapter two"));

        TaskList filteredTasks = taskList.findByKeyword("read");

        assertEquals(2, filteredTasks.size());
        assertEquals("[T][ ] read chapter one",
            filteredTasks.get(0).toString());
        assertEquals("[T][ ] read chapter two",
            filteredTasks.get(1).toString());
    }

    @Test
    void findByKeywordNoMatchesReturnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read chapter one"));

        TaskList filteredTasks = taskList.findByKeyword("banana");

        assertEquals(0, filteredTasks.size());
    }

    @Test
    void findByKeywordIgnoreCaseMatchesRegardlessOfLetterCase() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read chapter one"));

        TaskList filteredTasks = taskList.findByKeyword("read");

        assertEquals(1, filteredTasks.size());
    }

        @Test
        void containsEquivalentTaskMatchesTypeDescriptionAndDates() {
        TaskList taskList = new TaskList(new Deadlines("submit report",
            LocalDateTime.of(TEST_YEAR, TEST_MONTH, FIRST_TEST_DAY,
                TEST_HOUR, 0)));

        assertTrue(taskList.containsEquivalentTask(new Deadlines(
            "submit report", LocalDateTime.of(TEST_YEAR, TEST_MONTH,
                FIRST_TEST_DAY, TEST_HOUR, 0))));
        assertFalse(taskList.containsEquivalentTask(new Deadlines(
            "submit report", LocalDateTime.of(TEST_YEAR, TEST_MONTH,
                SECOND_TEST_DAY, TEST_HOUR, 0))));
        assertFalse(taskList.containsEquivalentTask(
            new Todo("submit report")));
        }

        @Test
        void containsEquivalentTaskIgnoresCompletionStatus() {
        Task existingTask = new Todo("submit report");
        existingTask.markAsDone();
        TaskList taskList = new TaskList(existingTask);

        assertTrue(taskList.containsEquivalentTask(
            new Todo("submit report")));
        }

        @Test
        void containsEquivalentTaskRejectsNullAndDifferentDescriptions() {
        TaskList taskList = new TaskList(new Todo("submit report"));

        assertFalse(taskList.containsEquivalentTask(null));
        assertFalse(taskList.containsEquivalentTask(
            new Todo("submit final report")));
        }

        @Test
        void containsEquivalentTaskMatchesEventDates() {
        TaskList taskList = new TaskList(new Events("team meeting",
            LocalDateTime.of(TEST_YEAR, TEST_MONTH, FIRST_TEST_DAY,
                EVENT_START_HOUR, 0),
            LocalDateTime.of(TEST_YEAR, TEST_MONTH, FIRST_TEST_DAY,
                EVENT_END_HOUR, 0)));

        assertTrue(taskList.containsEquivalentTask(new Events("team meeting",
            LocalDateTime.of(TEST_YEAR, TEST_MONTH, FIRST_TEST_DAY,
                EVENT_START_HOUR, 0),
            LocalDateTime.of(TEST_YEAR, TEST_MONTH, FIRST_TEST_DAY,
                EVENT_END_HOUR, 0))));
        assertFalse(taskList.containsEquivalentTask(new Events("team meeting",
            LocalDateTime.of(TEST_YEAR, TEST_MONTH, FIRST_TEST_DAY,
                EVENT_START_HOUR, 0),
            LocalDateTime.of(TEST_YEAR, TEST_MONTH, FIRST_TEST_DAY,
                LATER_EVENT_END_HOUR, 0))));
        }
}

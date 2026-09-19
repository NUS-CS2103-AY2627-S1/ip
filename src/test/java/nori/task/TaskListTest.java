package nori.task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import nori.NoriException;

/**
 * Tests the task list's keyword search and its display lines.
 */
public class TaskListTest {

    @Test
    public void constructor_varargs_preservesArgumentOrder() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");

        TaskList tasks = new TaskList(firstTask, secondTask);

        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    public void constructor_varargs_copiesCallerArray() {
        Task originalTask = new Todo("original");
        Task[] suppliedTasks = {originalTask};
        TaskList tasks = new TaskList(suppliedTasks);

        suppliedTasks[0] = new Todo("replacement");

        assertSame(originalTask, tasks.get(0));
    }

    @Test
    public void constructor_emptyVarargs_createsEmptyList() {
        TaskList tasks = new TaskList(new Task[0]);

        assertEquals(0, tasks.size());
    }

    @Test
    public void getTasksMatchingKeywordDisplayLines_singleMatch_returnsHeadingAndTask() throws NoriException {
        TaskList tasks = buildSampleList();

        assertArrayEquals(new String[] {
            "Noot noot! I found these fish:",
            "3.[E][ ] book fair (from: 2019-06-06 1000 to: 2019-06-06 1800)",
        }, tasks.getTasksMatchingKeywordDisplayLines("fair"));
    }

    @Test
    public void getTasksMatchingKeywordDisplayLines_multipleMatches_keepsOriginalTaskNumbers()
            throws NoriException {
        TaskList tasks = buildSampleList();

        assertArrayEquals(new String[] {
            "Noot noot! I found these fish:",
            "1.[T][X] read book",
            "2.[D][ ] return book (by: Jun 06 2019)",
            "3.[E][ ] book fair (from: 2019-06-06 1000 to: 2019-06-06 1800)",
            "4.[T][ ] Bookshop errand",
        }, tasks.getTasksMatchingKeywordDisplayLines("book"));
    }

    @Test
    public void getTasksMatchingKeywordDisplayLines_differentCase_stillMatches() throws NoriException {
        TaskList tasks = buildSampleList();

        String[] upperCaseResult = tasks.getTasksMatchingKeywordDisplayLines("BOOK");
        String[] mixedCaseResult = tasks.getTasksMatchingKeywordDisplayLines("BoOk");

        assertArrayEquals(tasks.getTasksMatchingKeywordDisplayLines("book"), upperCaseResult);
        assertArrayEquals(tasks.getTasksMatchingKeywordDisplayLines("book"), mixedCaseResult);
    }

    @Test
    public void getTasksMatchingKeywordDisplayLines_matchInsideWord_returnsTask() throws NoriException {
        TaskList tasks = buildSampleList();

        assertArrayEquals(new String[] {
            "Noot noot! I found these fish:",
            "4.[T][ ] Bookshop errand",
        }, tasks.getTasksMatchingKeywordDisplayLines("shop"));
    }

    @Test
    public void getTasksMatchingKeywordDisplayLines_noMatch_reportsNoMatchingTasks() throws NoriException {
        TaskList tasks = buildSampleList();

        assertArrayEquals(new String[] {"No matching fish in this sea. Try another keyword!"},
                tasks.getTasksMatchingKeywordDisplayLines("bicycle"));
    }

    @Test
    public void getTasksMatchingKeywordDisplayLines_emptyList_reportsNoMatchingTasks() {
        assertArrayEquals(new String[] {"No matching fish in this sea. Try another keyword!"},
                new TaskList().getTasksMatchingKeywordDisplayLines("book"));
    }

    @Test
    public void getTasksMatchingKeywordDisplayLines_dateOutsideDescription_doesNotMatch()
            throws NoriException {
        TaskList tasks = buildSampleList();

        // "2019-06-06" is the deadline's due date and the event's start and end,
        // but it appears in no description, so the description-only search misses it.
        assertArrayEquals(new String[] {"No matching fish in this sea. Try another keyword!"},
                tasks.getTasksMatchingKeywordDisplayLines("2019-06-06"));
    }

    @Test
    public void getTasksMatchingKeywordDisplayLines_emptyKeyword_returnsEveryTask() throws NoriException {
        TaskList tasks = buildSampleList();

        assertEquals(tasks.size() + 1, tasks.getTasksMatchingKeywordDisplayLines("").length);
    }

    @Test
    public void getDisplayLines_emptyList_promptsForATask() {
        assertArrayEquals(new String[] {"The iceberg is empty. Try \"todo borrow book\". Noot noot!"},
                new TaskList().getDisplayLines());
    }

    @Test
    public void getTaskIndex_validNumber_returnsZeroBasedIndex() throws NoriException {
        TaskList tasks = buildSampleList();

        assertEquals(0, tasks.getTaskIndex("1", "mark"));
        assertEquals(3, tasks.getTaskIndex("4", "mark"));
    }

    @Test
    public void getScheduleDisplayLines_mixedList_ordersAndGroupsTheDay() throws NoriException {
        TaskList tasks = buildSampleList();

        assertArrayEquals(new String[] {
            "Noot noot! Schedule for 2019-06-06:",
            "10:00 3.[E][ ] book fair (from: 2019-06-06 1000 to: 2019-06-06 1800)",
            "Due:",
            "      2.[D][ ] return book (by: Jun 06 2019)",
            "Anytime:",
            "      1.[T][X] read book",
            "      4.[T][ ] Bookshop errand",
            "      5.[T][ ] buy milk",
        }, tasks.getScheduleDisplayLines(LocalDate.of(2019, 6, 6)));
    }

    @Test
    public void getScheduleDisplayLines_dateWithNoDatedTasks_stillOffersTheTodos() throws NoriException {
        TaskList tasks = buildSampleList();

        assertArrayEquals(new String[] {
            "Noot noot! Schedule for 2019-07-01:",
            "Anytime:",
            "      1.[T][X] read book",
            "      4.[T][ ] Bookshop errand",
            "      5.[T][ ] buy milk",
        }, tasks.getScheduleDisplayLines(LocalDate.of(2019, 7, 1)));
    }

    @Test
    public void getScheduleDisplayLines_emptyList_reportsQuietIce() {
        TaskList tasks = new TaskList();

        assertArrayEquals(new String[] {"Nothing on the schedule for 2019-06-06. The ice is quiet."},
                tasks.getScheduleDisplayLines(LocalDate.of(2019, 6, 6)));
    }

    /**
     * Returns a list holding one task of each type, with a completed first task.
     *
     * @return the sample task list used by these tests.
     * @throws NoriException if a sample task cannot be created.
     */
    private static TaskList buildSampleList() throws NoriException {
        Task readBook = new Todo("read book");
        readBook.markAsDone();
        return new TaskList(
                readBook,
                new Deadline("return book", LocalDate.of(2019, 6, 6)),
                new Event("book fair", "2019-06-06 1000", "2019-06-06 1800"),
                new Todo("Bookshop errand"),
                new Todo("buy milk"));
    }

    @Test
    public void isSameTask_repeatedTodoIgnoringCompletion_returnsTrue() {
        Todo storedTodo = new Todo("read book");
        storedTodo.markAsDone();

        assertTrue(storedTodo.isSameTask(new Todo("read book")));
    }

    @Test
    public void isSameTask_differentTaskType_returnsFalse() throws NoriException {
        assertFalse(new Todo("read book").isSameTask(
                new Deadline("read book", LocalDate.parse("2019-01-01"))));
    }

    @Test
    public void isSameTask_descriptionsDifferingInCase_returnsFalse() {
        assertFalse(new Todo("read book").isSameTask(new Todo("Read Book")));
    }

    @Test
    public void isSameTask_descriptionsDifferingOnlyInSpacing_returnsTrue() {
        assertTrue(new Todo("read book").isSameTask(new Todo("read   book")));
        assertTrue(new Todo("read book").isSameTask(new Todo(" read\tbook ")));
    }

    @Test
    public void isSameTask_eventSpansDifferingOnlyInSpacing_returnsTrue() throws NoriException {
        assertTrue(new Event("fair", "2019-06-06 1000", "1800")
                .isSameTask(new Event("fair", "2019-06-06   1000", "1800")));
    }

    @Test
    public void isSameTask_deadlinesOnDifferentDates_returnsFalse() {
        assertFalse(new Deadline("report", LocalDate.parse("2019-01-01"))
                .isSameTask(new Deadline("report", LocalDate.parse("2019-01-02"))));
    }

    @Test
    public void isSameTask_eventsOverDifferentSpans_returnsFalse() throws NoriException {
        assertFalse(new Event("fair", "1pm", "2pm").isSameTask(new Event("fair", "1pm", "3pm")));
    }

    @Test
    public void isSameTask_nullTask_returnsFalse() {
        assertFalse(new Todo("read book").isSameTask(null));
    }

    @Test
    public void findSameTask_repeatedTask_returnsTheStoredTask() {
        Todo storedTodo = new Todo("read book");
        TaskList tasks = new TaskList(new Todo("other"), storedTodo);

        assertEquals(Optional.of(storedTodo), tasks.findSameTask(new Todo("read book")));
    }

    @Test
    public void findSameTask_newTask_returnsEmpty() {
        TaskList tasks = new TaskList(new Todo("read book"));

        assertEquals(Optional.empty(), tasks.findSameTask(new Todo("write book")));
    }
}

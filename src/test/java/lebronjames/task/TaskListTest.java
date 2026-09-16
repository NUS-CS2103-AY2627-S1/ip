package lebronjames.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lebronjames.LebronJamesException;

/**
 * Tests {@link TaskList}, which owns the task list and turns the one-based task
 * numbers the user types into the tasks themselves.
 *
 * <p>The boundary cases matter most here: an off-by-one in the number-to-index
 * conversion would let the user mark the wrong task, or reject a valid one.
 */
public class TaskListTest {
    /**
     * Builds a list holding the given number of numbered to-dos.
     *
     * @param count How many tasks to create.
     * @return List holding "task 1" up to "task count".
     */
    private static TaskList listOf(int count) {
        TaskList tasks = new TaskList();
        for (int i = 1; i <= count; i++) {
            tasks.add(new Todo("task " + i));
        }
        return tasks;
    }

    @Test
    public void constructor_noArguments_emptyList() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void constructor_savedTasks_listHoldsThem() {
        ArrayList<Task> saved = new ArrayList<>();
        saved.add(new Todo("read book"));
        saved.add(new Todo("return book"));

        TaskList tasks = new TaskList(saved);

        assertEquals(2, tasks.size());
        assertEquals("read book", tasks.asList().get(0).getDescription());
    }

    @Test
    public void add_severalTasks_appendedInOrder() {
        TaskList tasks = listOf(3);

        assertEquals(3, tasks.size());
        assertEquals("task 1", tasks.asList().get(0).getDescription());
        assertEquals("task 3", tasks.asList().get(2).getDescription());
    }

    @Test
    public void get_firstAndLastTaskNumber_correctTaskReturned() throws LebronJamesException {
        TaskList tasks = listOf(3);

        // Task numbers shown to the user start at 1, but the list is indexed
        // from 0; both ends of the range must map correctly.
        assertEquals("task 1", tasks.get(1).getDescription());
        assertEquals("task 3", tasks.get(3).getDescription());
    }

    @Test
    public void get_taskNumberOutOfRange_exceptionThrown() {
        TaskList tasks = listOf(3);

        assertThrows(LebronJamesException.class, () -> tasks.get(0));
        assertThrows(LebronJamesException.class, () -> tasks.get(4));
        assertThrows(LebronJamesException.class, () -> tasks.get(-1));
    }

    @Test
    public void get_emptyList_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> new TaskList().get(1));
    }

    @Test
    public void get_outOfRange_messageNamesTheNumberTyped() {
        LebronJamesException exception =
                assertThrows(LebronJamesException.class, () -> listOf(3).get(9));

        assertTrue(exception.getMessage().contains("9"), "message should quote the number the user typed");
    }

    @Test
    public void remove_middleTask_removedAndOthersShiftDown() throws LebronJamesException {
        TaskList tasks = listOf(3);

        Task removed = tasks.remove(2);

        assertEquals("task 2", removed.getDescription());
        assertEquals(2, tasks.size());
        // After removing task 2, the old task 3 must become task 2.
        assertEquals("task 1", tasks.get(1).getDescription());
        assertEquals("task 3", tasks.get(2).getDescription());
    }

    @Test
    public void remove_lastTask_listBecomesEmpty() throws LebronJamesException {
        TaskList tasks = listOf(1);

        assertEquals("task 1", tasks.remove(1).getDescription());
        assertEquals(0, tasks.size());
    }

    @Test
    public void remove_taskNumberOutOfRange_exceptionThrownAndListUnchanged() {
        TaskList tasks = listOf(2);

        assertThrows(LebronJamesException.class, () -> tasks.remove(3));
        assertThrows(LebronJamesException.class, () -> tasks.remove(0));
        // A rejected delete must not quietly remove something else.
        assertEquals(2, tasks.size());
    }

    @Test
    public void findTasksWithKeyword_keywordInDescription_matchesFound() {
        TaskList tasks = listOf(0);
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy milk"));

        List<Task> found = tasks.findTasksWithKeyword("book");

        assertEquals(2, found.size());
        assertEquals("read book", found.get(0).getDescription());
        assertEquals("return book", found.get(1).getDescription());
    }

    @Test
    public void findTasksWithKeyword_differentCapitalisation_stillMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        assertEquals(1, tasks.findTasksWithKeyword("book").size());
        assertEquals(1, tasks.findTasksWithKeyword("BOOK").size());
    }

    @Test
    public void findTasksWithKeyword_partOfAWord_matches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("visit the bookshop"));

        // Matching inside a word is the more forgiving behaviour for someone
        // half-remembering what they typed.
        assertEquals(1, tasks.findTasksWithKeyword("book").size());
    }

    @Test
    public void findTasksWithKeyword_noMatch_emptyListNotNull() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));

        assertTrue(tasks.findTasksWithKeyword("book").isEmpty());
    }

    @Test
    public void findTasksWithKeyword_matchesEveryTaskType_allReturned() throws LebronJamesException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", TaskDateTime.parse("2019-06-06")));
        tasks.add(new Event("book fair", TaskDateTime.parse("2019-06-06"), TaskDateTime.parse("2019-06-07")));

        // The search looks at descriptions, so the kind of task is irrelevant.
        assertEquals(3, tasks.findTasksWithKeyword("book").size());
    }

    @Test
    public void findTasksWithKeyword_emptyList_noMatches() {
        assertTrue(new TaskList().findTasksWithKeyword("book").isEmpty());
    }

    @Test
    public void findTasksOn_deadlineOnThatDate_included() throws LebronJamesException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", TaskDateTime.parse("2019-12-02")));

        assertEquals(1, tasks.findTasksOn(LocalDate.of(2019, 12, 2)).size());
        assertEquals(0, tasks.findTasksOn(LocalDate.of(2019, 12, 3)).size());
    }

    @Test
    public void findTasksOn_todo_neverIncluded() throws LebronJamesException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        // A to-do carries no date, so no date should ever match it.
        assertEquals(0, tasks.findTasksOn(LocalDate.of(2019, 12, 2)).size());
    }

    @Test
    public void findTasksOn_multiDayEvent_includedOnEveryDayItSpans() throws LebronJamesException {
        TaskList tasks = new TaskList();
        tasks.add(new Event("camp", TaskDateTime.parse("2019-12-02"), TaskDateTime.parse("2019-12-05")));

        assertEquals(0, tasks.findTasksOn(LocalDate.of(2019, 12, 1)).size());
        assertEquals(1, tasks.findTasksOn(LocalDate.of(2019, 12, 2)).size(), "first day");
        assertEquals(1, tasks.findTasksOn(LocalDate.of(2019, 12, 4)).size(), "middle day");
        assertEquals(1, tasks.findTasksOn(LocalDate.of(2019, 12, 5)).size(), "last day");
        assertEquals(0, tasks.findTasksOn(LocalDate.of(2019, 12, 6)).size());
    }

    @Test
    public void findTasksOn_severalMatches_returnedInListOrder() throws LebronJamesException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("first", TaskDateTime.parse("2019-12-02 0900")));
        tasks.add(new Todo("ignored"));
        tasks.add(new Deadline("second", TaskDateTime.parse("2019-12-02 1000")));

        List<Task> found = tasks.findTasksOn(LocalDate.of(2019, 12, 2));

        assertEquals(2, found.size());
        assertEquals("first", found.get(0).getDescription());
        assertEquals("second", found.get(1).getDescription());
    }

    @Test
    public void findTasksOn_noMatches_emptyListNotNull() {
        assertTrue(new TaskList().findTasksOn(LocalDate.of(2019, 12, 2)).isEmpty());
    }

    @Test
    public void asList_callerTriesToModify_unsupportedOperationThrown() {
        TaskList tasks = listOf(2);
        List<Task> view = tasks.asList();

        // The point of the view is that a caller cannot add or remove tasks
        // without TaskList knowing, which is what keeps the save file in step.
        assertThrows(UnsupportedOperationException.class, () -> view.add(new Todo("sneaked in")));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        assertEquals(2, tasks.size());
    }

    @Test
    public void asList_taskAddedAfterwards_viewShowsIt() {
        TaskList tasks = listOf(1);
        List<Task> view = tasks.asList();

        tasks.add(new Todo("added later"));

        // A view, not a snapshot: Storage saves whatever the list holds now.
        assertEquals(2, view.size());
    }

    @Test
    public void findDuplicateNumber_noSuchTask_noDuplicateReturned() {
        assertEquals(TaskList.NO_DUPLICATE, listOf(3).findDuplicateNumber(new Todo("read book")));
    }

    @Test
    public void findDuplicateNumber_emptyList_noDuplicateReturned() {
        assertEquals(TaskList.NO_DUPLICATE, new TaskList().findDuplicateNumber(new Todo("read book")));
    }

    @Test
    public void findDuplicateNumber_taskPresent_oneBasedNumberReturned() {
        // listOf builds "task 1".."task 3", so the match is the second one and
        // must be reported as 2, the number the user sees in the list.
        assertEquals(2, listOf(3).findDuplicateNumber(new Todo("task 2")));
    }

    @Test
    public void findDuplicateNumber_severalCopies_firstOneReported() {
        // A save file written before duplicates were refused can still hold two
        // copies; pointing at the first is the stable answer.
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        tasks.add(new Todo("read book"));

        assertEquals(1, tasks.findDuplicateNumber(new Todo("read book")));
    }

    @Test
    public void findDuplicateNumber_differsOnlyInCase_stillFound() {
        assertEquals(2, listOf(3).findDuplicateNumber(new Todo("TASK 2")));
    }
}

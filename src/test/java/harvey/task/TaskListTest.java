package harvey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import harvey.HarveyException;

/**
 * Tests {@link TaskList}.
 * <p>
 * The interesting behavior here is the numbering. The user counts tasks from 1 and the
 * underlying list counts from 0, so the boundaries either side of the valid range are
 * where an off-by-one error would show up. Those cases get the most attention below.
 */
public class TaskListTest {
    /** Builds a list of three todos named a, b and c, for tests that need existing tasks. */
    private TaskList threeTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        tasks.add(new Todo("c"));
        return tasks;
    }

    @Test
    public void newTaskList_noArguments_isEmpty() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void add_oneTask_sizeGrowsAndListNoLongerEmpty() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
    }

    @Test
    public void get_firstTaskNumber_returnsFirstTask() throws HarveyException {
        TaskList tasks = threeTasks();
        // Task 1 must be the first task added, not the second: this is the off-by-one
        // that the conversion from the user's numbering exists to prevent.
        assertEquals("[T][ ] a", tasks.get(1).toString());
    }

    @Test
    public void get_lastTaskNumber_returnsLastTask() throws HarveyException {
        TaskList tasks = threeTasks();
        assertEquals("[T][ ] c", tasks.get(3).toString());
    }

    @Test
    public void get_taskNumberJustPastEnd_exceptionThrown() {
        TaskList tasks = threeTasks();
        // 3 is valid and 4 is not, so this is the boundary that matters.
        HarveyException e = assertThrows(HarveyException.class, () -> tasks.get(4));
        assertEquals("There is no task 4. You have 3 task(s), so pick a number from 1 to 3.",
                e.getMessage());
    }

    @Test
    public void get_zero_exceptionThrown() {
        // 0 is a valid index into the underlying list but not a valid task number,
        // so forgetting the lower bound would let this through silently.
        TaskList tasks = threeTasks();
        assertThrows(HarveyException.class, () -> tasks.get(0));
    }

    @Test
    public void get_negativeTaskNumber_exceptionThrown() {
        TaskList tasks = threeTasks();
        assertThrows(HarveyException.class, () -> tasks.get(-1));
    }

    @Test
    public void get_anyTaskNumberOnEmptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(HarveyException.class, () -> tasks.get(1));
    }

    @Test
    public void get_validTaskNumber_returnsTheStoredObjectNotACopy() throws HarveyException {
        // Harvey marks a task by calling markAsDone() on what get() returned, so the
        // change is only visible in the list if get() hands back the stored object.
        TaskList tasks = new TaskList();
        Todo stored = new Todo("read book");
        tasks.add(stored);
        assertSame(stored, tasks.get(1));
    }

    @Test
    public void delete_middleTaskNumber_removesItAndClosesTheGap() throws HarveyException {
        TaskList tasks = threeTasks();
        Task removed = tasks.delete(2);

        assertEquals("[T][ ] b", removed.toString());
        assertEquals(2, tasks.size());
        // c must move up to task 2; a gap would break every later task number.
        assertEquals("[T][ ] a", tasks.get(1).toString());
        assertEquals("[T][ ] c", tasks.get(2).toString());
    }

    @Test
    public void delete_lastRemainingTask_listBecomesEmpty() throws HarveyException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only"));
        tasks.delete(1);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void delete_taskNumberJustPastEnd_exceptionThrownAndListUnchanged() {
        TaskList tasks = threeTasks();
        assertThrows(HarveyException.class, () -> tasks.delete(4));
        assertEquals(3, tasks.size());
    }

    @Test
    public void asList_afterAdds_containsTasksInInsertionOrder() {
        TaskList tasks = threeTasks();
        assertEquals(3, tasks.asList().size());
        assertEquals("[T][ ] a", tasks.asList().get(0).toString());
        assertEquals("[T][ ] c", tasks.asList().get(2).toString());
    }

    @Test
    public void find_keywordInSomeTasks_returnsOnlyThose() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("sports day"));
        tasks.add(new Todo("return book"));

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.asList().get(0).toString());
        assertEquals("[T][ ] return book", matches.asList().get(1).toString());
    }

    @Test
    public void find_result_canStillBeAddedTo() {
        // The matches are collected into a mutable list, so the returned TaskList behaves
        // like any other. Collecting into an immutable list would fail only here.
        TaskList matches = threeTasks().find("a");
        matches.add(new Todo("added later"));

        assertEquals(2, matches.size());
    }

    @Test
    public void find_keywordInNoTask_returnsEmptyList() {
        TaskList tasks = threeTasks();
        assertTrue(tasks.find("nothing here").isEmpty());
    }

    @Test
    public void find_onEmptyList_returnsEmptyList() {
        assertTrue(new TaskList().find("book").isEmpty());
    }

    @Test
    public void find_anyKeyword_leavesTheOriginalListUntouched() {
        // The matches are a separate list, so searching must not renumber or shorten
        // the real one; mark and delete still work off the numbers list shows.
        TaskList tasks = threeTasks();
        tasks.find("a");
        assertEquals(3, tasks.size());
    }

    @Test
    public void find_matchingTask_returnsTheStoredObjectNotACopy() throws HarveyException {
        // Marking a task found by search must change the task in the real list.
        TaskList tasks = new TaskList();
        Todo stored = new Todo("read book");
        tasks.add(stored);

        assertSame(stored, tasks.find("book").get(1));
    }

    /** Builds an event on 10 Sep 2026 running between the two given hours. */
    private Event eventFrom(String description, int startHour, int endHour) throws HarveyException {
        return new Event(description,
                LocalDateTime.of(2026, 9, 10, startHour, 0),
                LocalDateTime.of(2026, 9, 10, endHour, 0));
    }

    @Test
    public void findClash_emptyList_returnsEmpty() throws HarveyException {
        assertEquals(Optional.empty(), new TaskList().findClash(eventFrom("tuition Ben", 16, 18)));
    }

    @Test
    public void findClash_overlappingEvent_returnsThatEvent() throws HarveyException {
        TaskList tasks = new TaskList();
        Event sarah = eventFrom("tuition Sarah", 16, 18);
        tasks.add(sarah);

        assertEquals(Optional.of(sarah), tasks.findClash(eventFrom("tuition Ben", 17, 19)));
    }

    @Test
    public void findClash_backToBackEvent_returnsEmpty() throws HarveyException {
        TaskList tasks = new TaskList();
        tasks.add(eventFrom("tuition Sarah", 16, 18));

        assertEquals(Optional.empty(), tasks.findClash(eventFrom("tuition Ben", 18, 20)));
    }

    @Test
    public void findClash_onlyTodosAndDeadlines_returnsEmpty() throws HarveyException {
        // Neither a todo nor a deadline occupies a stretch of time, so neither can clash
        // however close its date is to the event being added.
        TaskList tasks = new TaskList();
        tasks.add(new Todo("mark scripts"));
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 10)));

        assertEquals(Optional.empty(), tasks.findClash(eventFrom("tuition Ben", 16, 18)));
    }

    @Test
    public void findClash_severalEventsOneClashing_returnsTheClashingOne() throws HarveyException {
        TaskList tasks = new TaskList();
        tasks.add(eventFrom("tuition Amy", 9, 11));
        Event clashing = eventFrom("tuition Sarah", 16, 18);
        tasks.add(clashing);
        tasks.add(eventFrom("tuition Cody", 20, 22));

        assertEquals(Optional.of(clashing), tasks.findClash(eventFrom("tuition Ben", 17, 19)));
    }

    @Test
    public void findClash_twoClashingEvents_returnsTheEarlierOneInTheList() throws HarveyException {
        // Reporting the first match keeps the error message short; the user fixes one
        // conflict at a time and the next add reports the next one.
        TaskList tasks = new TaskList();
        Event first = eventFrom("tuition Sarah", 16, 18);
        tasks.add(first);
        tasks.add(eventFrom("tuition Cody", 17, 19));

        assertEquals(Optional.of(first), tasks.findClash(eventFrom("tuition Ben", 16, 20)));
    }

    @Test
    public void findClash_theEventItself_returnsEmpty() throws HarveyException {
        // An event already in the list must not be reported as clashing with itself,
        // which matters if a task is ever re-checked after being added.
        TaskList tasks = new TaskList();
        Event sarah = eventFrom("tuition Sarah", 16, 18);
        tasks.add(sarah);

        assertEquals(Optional.empty(), tasks.findClash(sarah));
    }

    @Test
    public void findDuplicate_sameTodoInOtherCase_returnsStoredTask() throws HarveyException {
        TaskList tasks = threeTasks();
        assertSame(tasks.get(2), tasks.findDuplicate(new Todo("B")).orElseThrow());
    }

    @Test
    public void findDuplicate_deadlineWithSameDescriptionAsTodo_returnsEmpty() {
        // Different kinds of task are different tasks, even with the same words.
        TaskList tasks = threeTasks();
        assertEquals(Optional.empty(), tasks.findDuplicate(new Deadline("a", LocalDate.of(2026, 10, 1))));
    }

    @Test
    public void findDuplicate_eventAtOtherTime_returnsEmpty() throws HarveyException {
        TaskList tasks = new TaskList();
        tasks.add(eventFrom("tuition Ben", 16, 18));
        assertEquals(Optional.empty(), tasks.findDuplicate(eventFrom("tuition Ben", 18, 20)));
    }

    @Test
    public void findDuplicate_taskAlreadyInList_notReportedAsItsOwnDuplicate() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        tasks.add(todo);
        assertEquals(Optional.empty(), tasks.findDuplicate(todo));
    }
}

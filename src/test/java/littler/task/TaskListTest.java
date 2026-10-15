package littler.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import littler.datetime.StringDateTimeConverter.ParsedDateTime;
import littler.exception.LittleRException;
import littler.task.attribute.PriorityLevel;

public class TaskListTest {

    // ---- add / size / isEmpty ----

    @Test
    public void isEmpty_newTaskList_returnsTrue() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void add_singleTask_increasesSizeAndIsRetrievable() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
        assertFalse(tasks.isEmpty());
        assertEquals("read book", tasks.get(0).getName());
    }

    // ---- delete ----

    @Test
    public void delete_validIndex_removesAndReturnsTask() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));

        Task removed = tasks.delete(0);

        assertEquals("read book", removed.getName());
        assertEquals(1, tasks.size());
        assertEquals("write essay", tasks.get(0).getName());
    }

    @Test
    public void delete_negativeIndex_throwsException() throws LittleRException {
        // throws LittleRException added: new Todo() now declares it
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(LittleRException.class, () -> tasks.delete(-1));
    }

    @Test
    public void delete_indexEqualToSize_throwsException() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(LittleRException.class, () -> tasks.delete(1));
    }

    // ---- mark / unmark ----

    @Test
    public void mark_validIndex_marksTaskAsDone() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        Task marked = tasks.mark(0);

        assertTrue(marked.toString().startsWith("[T][X]"));
    }

    @Test
    public void mark_outOfBoundsIndex_throwsException() throws LittleRException {
        TaskList tasks = new TaskList();
        assertThrows(LittleRException.class, () -> tasks.mark(0));
    }

    @Test
    public void unmark_markedTask_returnsToUnmarkedState() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.mark(0);

        Task unmarked = tasks.unmark(0);

        assertTrue(unmarked.toString().startsWith("[T][ ]"));
    }

    @Test
    public void unmark_outOfBoundsIndex_throwsException() throws LittleRException {
        TaskList tasks = new TaskList();
        assertThrows(LittleRException.class, () -> tasks.unmark(0));
    }

    // ---- get / getLast ----

    @Test
    public void get_outOfBoundsIndex_throwsException() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(LittleRException.class, () -> tasks.get(5));
    }

    @Test
    public void getLast_afterMultipleAdds_returnsMostRecent() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        assertEquals("second", tasks.getLast().getName());
    }

    // ---- getTasksOn ----

    @Test
    public void getTasksOn_mixedTaskTypes_returnsOnlyMatchingSchedulableTasks() throws LittleRException {
        TaskList tasks = new TaskList();

        ParsedDateTime targetDate = new ParsedDateTime(LocalDate.of(2026, 8, 6), null);
        ParsedDateTime otherDate = new ParsedDateTime(LocalDate.of(2026, 8, 7), null);

        tasks.add(new Todo("no date, should never match"));
        tasks.add(new Deadline("due on target", targetDate));
        tasks.add(new Deadline("due on other day", otherDate));
        tasks.add(new Event("spans target date",
            new ParsedDateTime(LocalDate.of(2026, 8, 5), null),
            new ParsedDateTime(LocalDate.of(2026, 8, 8), null)));

        ArrayList<Task> matches = tasks.getTasksOn(targetDate);

        assertEquals(2, matches.size());
    }

    @Test
    public void getTasksOn_noMatches_returnsEmptyList() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("no date"));
        ParsedDateTime date = new ParsedDateTime(LocalDate.of(2026, 8, 6), null);
        assertTrue(tasks.getTasksOn(date).isEmpty());
    }

    // ---- containsDuplicate ----

    @Test
    public void containsDuplicate_identicalTodo_returnsTrue() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertTrue(tasks.containsDuplicate(new Todo("read book")));
    }

    @Test
    public void containsDuplicate_differentDescription_returnsFalse() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertFalse(tasks.containsDuplicate(new Todo("write essay")));
    }

    // ---- findByKeyword ----

    @Test
    public void findByKeyword_partialMatch_returnsMatchingTasks() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book to library"));
        tasks.add(new Todo("write essay"));

        ArrayList<Task> results = tasks.findByKeyword("book");

        assertEquals(2, results.size());
    }

    @Test
    public void findByKeyword_caseInsensitive_matchesRegardlessOfCase() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        ArrayList<Task> results = tasks.findByKeyword("read");

        assertEquals(1, results.size());
    }

    @Test
    public void findByKeyword_noMatch_returnsEmptyList() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        ArrayList<Task> results = tasks.findByKeyword("xyz");

        assertTrue(results.isEmpty());
    }

    // ---- getSortedByName ----

    @Test
    public void getSortedByName_ascending_returnsAlphabeticalOrder() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("zebra"));
        tasks.add(new Todo("apple"));
        tasks.add(new Todo("mango"));

        ArrayList<Task> sorted = tasks.getSortedByName(false);

        assertEquals("apple", sorted.get(0).getName());
        assertEquals("mango", sorted.get(1).getName());
        assertEquals("zebra", sorted.get(2).getName());
    }

    @Test
    public void getSortedByName_descending_returnsReverseAlphabeticalOrder() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("apple"));
        tasks.add(new Todo("zebra"));

        ArrayList<Task> sorted = tasks.getSortedByName(true);

        assertEquals("zebra", sorted.get(0).getName());
        assertEquals("apple", sorted.get(1).getName());
    }

    // ---- getSortedByPriority ----

    @Test
    public void getSortedByPriority_descending_putsHighFirstnullPriorityLast() throws LittleRException {
        TaskList tasks = new TaskList();
        Todo low = new Todo("low task");
        low.setPriority(PriorityLevel.LOW);
        Todo high = new Todo("high task");
        high.setPriority(PriorityLevel.HIGH);
        Todo noPriority = new Todo("no priority");

        tasks.add(low);
        tasks.add(noPriority);
        tasks.add(high);

        ArrayList<Task> sorted = tasks.getSortedByPriority(true);

        assertEquals("high task", sorted.get(0).getName());
        assertEquals("low task", sorted.get(1).getName());
        assertEquals("no priority", sorted.get(2).getName());
    }

    @Test
    public void getSortedByPriority_ascending_putsLowFirstnullPriorityLast() throws LittleRException {
        TaskList tasks = new TaskList();
        Todo noPriority = new Todo("no priority");
        Todo low = new Todo("low task");
        low.setPriority(PriorityLevel.LOW);
        Todo high = new Todo("high task");
        high.setPriority(PriorityLevel.HIGH);
        Todo medium = new Todo("medium task");
        medium.setPriority(PriorityLevel.MEDIUM);

        tasks.add(noPriority);
        tasks.add(low);
        tasks.add(high);
        tasks.add(medium);

        // Even ascending (low-first), null-priority still trails behind
        ArrayList<Task> sorted = tasks.getSortedByPriority(false);

        assertEquals("low task", sorted.get(0).getName());
        assertEquals("no priority", sorted.get(3).getName());
    }

    // ---- getSortedByDate ----

    @Test
    public void getSortedByDate_ascending_putsEarliestFirst() throws LittleRException {
        TaskList tasks = new TaskList();
        ParsedDateTime earlier = new ParsedDateTime(LocalDate.of(2026, 8, 5), null);
        ParsedDateTime later = new ParsedDateTime(LocalDate.of(2026, 8, 10), null);

        tasks.add(new Deadline("later task", later));
        tasks.add(new Deadline("earlier task", earlier));

        ArrayList<Task> sorted = tasks.getSortedByDate(false);

        assertEquals("earlier task", sorted.get(0).getName());
    }

    @Test
    public void getSortedByDate_todoAlwaysAfterDatedTasks() throws LittleRException {
        TaskList tasks = new TaskList();
        ParsedDateTime date = new ParsedDateTime(LocalDate.of(2030, 1, 1), null);

        tasks.add(new Todo("undated todo"));
        tasks.add(new Deadline("far future deadline", date));

        ArrayList<Task> sorted = tasks.getSortedByDate(false);

        assertEquals("far future deadline", sorted.get(0).getName());
        assertEquals("undated todo", sorted.get(1).getName());
    }

    // ---- getSortedByTag ----

    @Test
    public void getSortedByTag_ascending_putsTaggedTasksFirst() throws LittleRException {
        TaskList tasks = new TaskList();
        Todo untagged = new Todo("untagged");
        Todo tagged = new Todo("tagged");
        tagged.addTag("work");

        tasks.add(untagged);
        tasks.add(tagged);

        ArrayList<Task> sorted = tasks.getSortedByTag(false);

        assertEquals("tagged", sorted.get(0).getName());
        assertEquals("untagged", sorted.get(1).getName());
    }

    // ---- undo ----

    @Test
    public void undo_afterSnapshot_restoresPreviousState() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("original"));

        tasks.setUndoSnapshot(tasks.snapshotTasks());
        tasks.add(new Todo("added after snapshot"));

        tasks.undo();

        assertEquals(1, tasks.size());
        assertEquals("original", tasks.get(0).getName());
    }

    @Test
    public void undo_withNoSnapshot_throwsException() {
        TaskList tasks = new TaskList();
        assertThrows(LittleRException.class, tasks::undo);
    }

    @Test
    public void undo_calledTwiceInARow_throwsOnSecondCall() throws LittleRException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task"));
        tasks.setUndoSnapshot(tasks.snapshotTasks());
        tasks.add(new Todo("extra"));

        tasks.undo();
        // Snapshot is consumed after first undo; second call should fail
        assertThrows(LittleRException.class, tasks::undo);
    }
}

package suki;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for the sort command: the ordering rules, and the fact that sorting
 * rearranges the list without changing what is in it.
 */
public class SortTest {

    /** Returns the descriptions of the tasks, in their current order. */
    private List<String> descriptionsOf(TaskList tasks) {
        return tasks.asList().stream().map(Task::getDescription).toList();
    }

    @Test
    public void sort_mixedDates_ordersEarliestFirst() throws SukiException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("later deadline", DateTime.parse("2019-12-01")));
        tasks.add(new Event("early event",
                DateTime.parse("2019-10-15 1400"), DateTime.parse("2019-10-15 1600")));
        tasks.add(new Deadline("middle deadline", DateTime.parse("2019-11-01")));

        tasks.sortByScheduledDate();

        assertEquals(List.of("early event", "middle deadline", "later deadline"),
                descriptionsOf(tasks));
    }

    @Test
    public void sort_event_orderedByItsStartNotItsEnd() throws SukiException {
        TaskList tasks = new TaskList();
        // The deadline falls between the event's start and its end, so it comes
        // second only if the event is ordered by when it starts.
        tasks.add(new Deadline("deadline", DateTime.parse("2019-10-15 1500")));
        tasks.add(new Event("event",
                DateTime.parse("2019-10-15 1400"), DateTime.parse("2019-10-15 1600")));

        tasks.sortByScheduledDate();

        assertEquals(List.of("event", "deadline"), descriptionsOf(tasks));
    }

    @Test
    public void sort_todos_goAfterEveryDatedTask() throws SukiException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("undated"));
        tasks.add(new Deadline("dated", DateTime.parse("2019-12-01")));

        tasks.sortByScheduledDate();

        assertEquals(List.of("dated", "undated"), descriptionsOf(tasks));
    }

    @Test
    public void sort_todos_keepTheOrderTheyWereAddedIn() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        tasks.sortByScheduledDate();

        assertEquals(List.of("first", "second", "third"), descriptionsOf(tasks));
    }

    @Test
    public void sort_sameDate_keepsTheOrderTheyWereAddedIn() throws SukiException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("added first", DateTime.parse("2019-10-15")));
        tasks.add(new Deadline("added second", DateTime.parse("2019-10-15")));

        tasks.sortByScheduledDate();

        assertEquals(List.of("added first", "added second"), descriptionsOf(tasks));
    }

    @Test
    public void sort_emptyList_doesNothing() {
        TaskList tasks = new TaskList();
        tasks.sortByScheduledDate();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void sort_keepsEveryTask() throws SukiException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a todo"));
        tasks.add(new Deadline("a deadline", DateTime.parse("2019-12-01")));
        tasks.add(new Event("an event",
                DateTime.parse("2019-10-15 1400"), DateTime.parse("2019-10-15 1600")));

        tasks.sortByScheduledDate();

        assertEquals(3, tasks.size());
    }

    @Test
    public void sort_isIdempotent() throws SukiException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("second", DateTime.parse("2019-12-01")));
        tasks.add(new Todo("undated"));
        tasks.add(new Deadline("first", DateTime.parse("2019-10-15")));

        tasks.sortByScheduledDate();
        List<String> afterOnce = descriptionsOf(tasks);
        tasks.sortByScheduledDate();

        assertEquals(afterOnce, descriptionsOf(tasks), "sorting twice should change nothing");
    }

    @Test
    public void parse_sortCommand_isRecognised() {
        assertEquals(CommandType.SORT, Parser.parse("sort").commandType());
    }

    @Test
    public void getScheduledDateTime_todo_isEmpty() {
        assertTrue(new Todo("no date").getScheduledDateTime().isEmpty());
    }
}

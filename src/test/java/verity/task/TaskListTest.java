package verity.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {

    @Test
    void constructor_default_isEmpty() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    void constructor_givenList_copiesRatherThanAliases() {
        List<Task> source = new ArrayList<>();
        source.add(new Todo("read book"));

        TaskList tasks = new TaskList(source);
        source.add(new Todo("write essay"));

        assertEquals(1, tasks.size(), "mutating the original list afterwards must not affect the TaskList");
    }

    @Test
    void add_singleTask_sizeIncreasesAndTaskRetrievable() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertFalse(tasks.isEmpty());
        assertEquals(todo, tasks.get(0));
    }

    @Test
    void remove_existingIndex_removesAndReturnsTask() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("read book");
        Todo second = new Todo("write essay");
        tasks.add(first);
        tasks.add(second);

        Task removed = tasks.remove(0);

        assertEquals(first, removed);
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    void isValidIndex_negativeIndex_falseReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertFalse(tasks.isValidIndex(-1));
    }

    @Test
    void isValidIndex_emptyList_zeroIsInvalid() {
        TaskList tasks = new TaskList();

        assertFalse(tasks.isValidIndex(0));
    }

    @Test
    void isValidIndex_lastIndex_trueReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));

        assertTrue(tasks.isValidIndex(1));
    }

    @Test
    void isValidIndex_oneBeyondLastIndex_falseReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertFalse(tasks.isValidIndex(1));
    }

    @Test
    void getTasks_returnedListIsUnmodifiable() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> view = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> view.add(new Todo("write essay")));
    }

    @Test
    void getTasksOnDate_deadlineOnExactDate_included() {
        TaskList tasks = new TaskList();
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        tasks.add(deadline);

        List<Task> matches = tasks.getTasksOnDate(LocalDate.of(2019, 12, 2));

        assertEquals(List.of(deadline), matches);
    }

    @Test
    void getTasksOnDate_deadlineOnDifferentDate_excluded() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", LocalDate.of(2019, 12, 2)));

        List<Task> matches = tasks.getTasksOnDate(LocalDate.of(2019, 12, 3));

        assertTrue(matches.isEmpty());
    }

    @Test
    void getTasksOnDate_eventOnStartBoundary_included() {
        TaskList tasks = new TaskList();
        Event event = new Event("conference", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 3));
        tasks.add(event);

        List<Task> matches = tasks.getTasksOnDate(LocalDate.of(2019, 12, 1));

        assertEquals(List.of(event), matches);
    }

    @Test
    void getTasksOnDate_eventOnEndBoundary_included() {
        TaskList tasks = new TaskList();
        Event event = new Event("conference", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 3));
        tasks.add(event);

        List<Task> matches = tasks.getTasksOnDate(LocalDate.of(2019, 12, 3));

        assertEquals(List.of(event), matches);
    }

    @Test
    void getTasksOnDate_eventBetweenBoundaries_included() {
        TaskList tasks = new TaskList();
        Event event = new Event("conference", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 3));
        tasks.add(event);

        List<Task> matches = tasks.getTasksOnDate(LocalDate.of(2019, 12, 2));

        assertEquals(List.of(event), matches);
    }

    @Test
    void getTasksOnDate_eventOutsideRange_excluded() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("conference", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 3)));

        List<Task> matches = tasks.getTasksOnDate(LocalDate.of(2019, 12, 4));

        assertTrue(matches.isEmpty());
    }

    @Test
    void getTasksOnDate_todo_neverMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("some task"));

        List<Task> matches = tasks.getTasksOnDate(LocalDate.of(2019, 12, 2));

        assertTrue(matches.isEmpty());
    }

    @Test
    void getTasksOnDate_mixedList_returnsOnlyMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        Deadline matchingDeadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        Event matchingEvent = new Event("conference", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 3));
        tasks.add(new Todo("unrelated todo"));
        tasks.add(matchingDeadline);
        tasks.add(new Deadline("other deadline", LocalDate.of(2019, 12, 25)));
        tasks.add(matchingEvent);

        List<Task> matches = tasks.getTasksOnDate(LocalDate.of(2019, 12, 2));

        assertEquals(List.of(matchingDeadline, matchingEvent), matches);
    }

    @Test
    void findByKeyword_substringInDescription_matched() {
        TaskList tasks = new TaskList();
        Todo matching = new Todo("read book");
        tasks.add(matching);
        tasks.add(new Todo("write essay"));

        List<Task> matches = tasks.findByKeyword("book");

        assertEquals(List.of(matching), matches);
    }

    @Test
    void findByKeyword_differentCase_stillMatched() {
        TaskList tasks = new TaskList();
        Todo matching = new Todo("read book");
        tasks.add(matching);

        List<Task> matches = tasks.findByKeyword("BOOK");

        assertEquals(List.of(matching), matches);
    }

    @Test
    void findByKeyword_noDescriptionContainsKeyword_emptyListReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> matches = tasks.findByKeyword("zzz");

        assertTrue(matches.isEmpty());
    }

    @Test
    void findByKeyword_matchesAcrossAllTaskTypes_allReturnedInOrder() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        Event event = new Event("book club", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        tasks.add(todo);
        tasks.add(new Todo("write essay"));
        tasks.add(deadline);
        tasks.add(event);

        List<Task> matches = tasks.findByKeyword("book");

        assertEquals(List.of(todo, deadline, event), matches);
    }
}

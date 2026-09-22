package steph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import steph.task.Deadline;
import steph.task.Event;
import steph.task.Task;
import steph.task.ToDo;

/**
 * Unit tests for {@link TaskList}.
 *
 * <p>Most of {@code TaskList} is a thin pass-through to an {@code ArrayList},
 * but a few behaviours are deliberate design decisions worth pinning down:
 * <ul>
 *   <li>the {@code List}-taking constructor makes a <em>defensive copy</em>, so
 *       the caller's list and the {@code TaskList} cannot disturb each other;</li>
 *   <li>{@link TaskList#asList()} hands back an <em>unmodifiable</em> view;</li>
 *   <li>{@link TaskList#findMatch(String)} does a case-insensitive substring
 *       search and returns an unmodifiable <em>snapshot</em>.</li>
 * </ul>
 */
public class TaskListTest {

    @Test
    public void constructor_noArgs_startsEmpty() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void constructor_fromList_containsTheSameTasksInOrder() {
        Task a = new ToDo("a");
        Task b = new ToDo("b");

        TaskList list = new TaskList(List.of(a, b));

        assertEquals(2, list.size());
        assertSame(a, list.get(0));
        assertSame(b, list.get(1));
    }

    @Test
    public void constructor_fromList_laterChangeToSourceListDoesNotAffectTaskList() {
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("a"));

        TaskList list = new TaskList(source);
        source.add(new ToDo("added after construction"));

        assertEquals(1, list.size());
    }

    @Test
    public void constructor_fromList_changeToTaskListDoesNotAffectSourceList() {
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("a"));

        TaskList list = new TaskList(source);
        list.add(new ToDo("b"));

        assertEquals(1, source.size());
    }

    @Test
    public void addThenGet_returnsThatTask() {
        TaskList list = new TaskList();
        Task task = new ToDo("write tests");

        list.add(task);

        assertSame(task, list.get(0));
    }

    @Test
    public void remove_returnsRemovedTaskAndShrinksList() {
        Task a = new ToDo("a");
        Task b = new ToDo("b");
        TaskList list = new TaskList(List.of(a, b));

        Task removed = list.remove(0);

        assertSame(a, removed);
        assertEquals(1, list.size());
        assertSame(b, list.get(0));
    }

    @Test
    public void asList_isUnmodifiable_mutationThrows() {
        TaskList list = new TaskList(List.of(new ToDo("a")));

        List<Task> view = list.asList();

        assertThrows(UnsupportedOperationException.class, () -> view.add(new ToDo("b")));
    }

    @Test
    public void asList_isALiveView_reflectsLaterAdditions() {
        TaskList list = new TaskList();
        List<Task> view = list.asList();

        list.add(new ToDo("added after asList() was called"));

        assertEquals(1, view.size());
    }

    // ====================================================================
    // findMatch
    //
    // Contract: returns the tasks whose name contains the keyword
    // (case-insensitive substring), in list order, as an unmodifiable
    // snapshot.
    // ====================================================================

    @Test
    public void findMatch_keywordInSomeNames_returnsOnlyThoseInOrder() {
        Task read = new ToDo("read book");
        Task returnBook = new ToDo("return book");
        Task buy = new ToDo("buy milk");
        TaskList list = new TaskList(List.of(read, returnBook, buy));

        List<Task> matches = list.findMatch("book");

        assertEquals(List.of(read, returnBook), matches);
    }

    @Test
    public void findMatch_differentCase_stillMatches() {
        Task task = new ToDo("Read Book");
        TaskList list = new TaskList(List.of(task));

        assertEquals(List.of(task), list.findMatch("book"));
    }

    @Test
    public void findMatch_noNameContainsKeyword_returnsEmptyList() {
        TaskList list = new TaskList(List.of(new ToDo("buy milk")));

        assertTrue(list.findMatch("book").isEmpty());
    }

    @Test
    public void findMatch_result_isUnmodifiable() {
        TaskList list = new TaskList(List.of(new ToDo("read book")));

        List<Task> matches = list.findMatch("book");

        assertThrows(UnsupportedOperationException.class, () -> matches.add(new ToDo("x")));
    }

    @Test
    public void findMatch_result_isASnapshotNotALiveView() {
        TaskList list = new TaskList(List.of(new ToDo("read book")));

        List<Task> matches = list.findMatch("book");
        list.add(new ToDo("borrow book"));

        assertEquals(1, matches.size());
    }

    // ====================================================================
    // findClashingEvents / addEvent
    //
    // Contract: only not-done Events are compared against a candidate Event
    // (overlap itself is Event#clashesWith, tested in EventTest); addEvent
    // rejects a clash unless forced, and adds/reports normally otherwise.
    // ====================================================================

    @Test
    public void findClashingEvents_overlappingPendingEvent_returnsIt() {
        Event existing = event("meeting", 9, 0, 10, 0);
        TaskList list = new TaskList(List.of(existing));

        List<Event> clashes = list.findClashingEvents(event("clash", 9, 30, 10, 30));

        assertEquals(List.of(existing), clashes);
    }

    @Test
    public void findClashingEvents_nonOverlappingEvent_returnsEmpty() {
        TaskList list = new TaskList(List.of(event("meeting", 9, 0, 10, 0)));

        List<Event> clashes = list.findClashingEvents(event("later", 14, 0, 15, 0));

        assertTrue(clashes.isEmpty());
    }

    @Test
    public void findClashingEvents_doneClashingEvent_excludedFromResult() {
        Event done = event("meeting", 9, 0, 10, 0);
        done.complete();
        TaskList list = new TaskList(List.of(done));

        List<Event> clashes = list.findClashingEvents(event("clash", 9, 30, 10, 30));

        assertTrue(clashes.isEmpty());
    }

    @Test
    public void findClashingEvents_toDoAndDeadlineIgnored_onlyEventsConsidered() {
        Task todo = new ToDo("unrelated todo");
        Task deadline = new Deadline("unrelated deadline", LocalDateTime.of(2019, 10, 15, 9, 30));
        TaskList list = new TaskList(List.of(todo, deadline));

        List<Event> clashes = list.findClashingEvents(event("clash", 9, 0, 10, 0));

        assertTrue(clashes.isEmpty());
    }

    @Test
    public void findClashingEvents_multipleClashingEvents_returnsAllInOrder() {
        Event first = event("first", 9, 0, 10, 0);
        Event second = event("second", 9, 30, 10, 30);
        TaskList list = new TaskList(List.of(first, second));

        List<Event> clashes = list.findClashingEvents(event("new", 9, 15, 10, 15));

        assertEquals(List.of(first, second), clashes);
    }

    @Test
    public void addEvent_noClash_addsAndReturnsConfirmationMessage() throws StephException {
        TaskList list = new TaskList();
        Event newEvent = event("meeting", 9, 0, 10, 0);

        String response = list.addEvent(new ParsedEvent(newEvent, false));

        assertEquals(1, list.size());
        assertSame(newEvent, list.get(0));
        assertEquals("Got it. I've added this task:\n  " + newEvent
                + "\nNow you have 1 tasks in the list.", response);
    }

    @Test
    public void addEvent_clashesNotForced_throwsAndEventNotAdded() {
        TaskList list = new TaskList(List.of(event("meeting", 9, 0, 10, 0)));

        assertThrows(StephException.class, () ->
                list.addEvent(new ParsedEvent(event("clash", 9, 30, 10, 30), false)));
        assertEquals(1, list.size());
    }

    @Test
    public void addEvent_clashesButForced_addsDespiteClash() throws StephException {
        TaskList list = new TaskList(List.of(event("meeting", 9, 0, 10, 0)));
        Event forced = event("clash", 9, 30, 10, 30);

        list.addEvent(new ParsedEvent(forced, true));

        assertEquals(2, list.size());
        assertSame(forced, list.get(1));
    }

    @Test
    public void addEvent_clashesWithMultipleEvents_messageListsAllOfThem() {
        Event first = event("first", 9, 0, 10, 0);
        Event second = event("second", 9, 30, 10, 30);
        TaskList list = new TaskList(List.of(first, second));

        StephException thrown = assertThrows(StephException.class, () ->
                list.addEvent(new ParsedEvent(event("new", 9, 15, 10, 15), false)));

        assertTrue(thrown.getMessage().contains(first.toString()), thrown.getMessage());
        assertTrue(thrown.getMessage().contains(second.toString()), thrown.getMessage());
    }

    @Test
    public void size_afterAddAndRemove_tracksCount() {
        TaskList list = new TaskList();
        list.add(new ToDo("a"));
        list.add(new ToDo("b"));
        list.remove(0);

        assertEquals(1, list.size());
    }

    @Test
    public void get_byIndex_returnsTasksInInsertionOrder() {
        TaskList list = new TaskList();
        ToDo first = new ToDo("a");
        ToDo second = new ToDo("b");
        list.add(first);
        list.add(second);

        assertSame(first, list.get(0));
        assertSame(second, list.get(1));
    }

    @Test
    public void findMatch_emptyKeyword_matchesEveryTask() {
        TaskList list = new TaskList();
        list.add(new ToDo("a"));
        list.add(new ToDo("b"));

        assertEquals(2, list.findMatch("").size());
    }

    @Test
    public void findClashingEvents_emptyList_returnsEmpty() {
        assertTrue(new TaskList().findClashingEvents(event("new", 9, 0, 10, 0)).isEmpty());
    }

    @Test
    public void addEvent_listHasOtherTaskTypes_countsAllTasksInMessage() throws StephException {
        TaskList list = new TaskList();
        list.add(new ToDo("a"));
        list.add(new Deadline("b", LocalDateTime.of(2019, 10, 15, 9, 0)));

        String message = list.addEvent(new ParsedEvent(event("new", 9, 0, 10, 0), false));

        assertTrue(message.endsWith("Now you have 3 tasks in the list."), message);
    }

    private static Event event(String name, int fromHour, int fromMinute, int toHour, int toMinute) {
        LocalDateTime from = LocalDateTime.of(2019, 10, 15, fromHour, fromMinute);
        LocalDateTime to = LocalDateTime.of(2019, 10, 15, toHour, toMinute);
        return new Event(name, from, to);
    }
}

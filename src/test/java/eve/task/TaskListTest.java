package eve.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import eve.EveException;

public class TaskListTest {
    @Test
    public void toIndex_validNumbers_returnsZeroBasedIndex() throws EveException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));
        tasks.add(new ToDo("b"));

        assertEquals(0, tasks.toIndex(1));
        assertEquals(1, tasks.toIndex(2));
    }

    @Test
    public void toIndex_zero_throwsWithTaskNumberInMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));

        EveException exception = assertThrows(EveException.class, () -> tasks.toIndex(0));
        assertTrue(exception.getMessage().contains("no task number 0"));
    }

    @Test
    public void toIndex_negative_throws() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));

        assertThrows(EveException.class, () -> tasks.toIndex(-1));
    }

    @Test
    public void toIndex_pastEndOfList_throwsWithTaskNumberInMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));

        EveException exception = assertThrows(EveException.class, () -> tasks.toIndex(2));
        assertTrue(exception.getMessage().contains("no task number 2"));
    }

    @Test
    public void toIndex_emptyList_anyNumberThrows() {
        TaskList tasks = new TaskList();

        assertThrows(EveException.class, () -> tasks.toIndex(1));
    }

    @Test
    public void occurringOn_mixOfTaskTypes_returnsOnlyMatchingOnes() {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2019, 10, 7);
        Task todo = new ToDo("just a todo");
        Task matchingDeadline = new Deadline("return book", date);
        Task nonMatchingDeadline = new Deadline("other", date.plusDays(1));
        Task matchingEvent = new Event("trip", date.minusDays(3), date.plusDays(4));
        tasks.add(todo);
        tasks.add(matchingDeadline);
        tasks.add(nonMatchingDeadline);
        tasks.add(matchingEvent);

        List<Task> matches = tasks.occurringOn(date);

        assertEquals(2, matches.size());
        assertTrue(matches.contains(matchingDeadline));
        assertTrue(matches.contains(matchingEvent));
    }

    @Test
    public void occurringOn_noMatches_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("just a todo"));

        List<Task> matches = tasks.occurringOn(LocalDate.of(2019, 1, 1));

        assertTrue(matches.isEmpty());
    }

    @Test
    public void schedule_mixOfTaskTypesAddedOutOfOrder_sortsChronologicallyAndExcludesTodos() {
        TaskList tasks = new TaskList();
        Task todo = new ToDo("just a todo");
        Task laterDeadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        Task event = new Event("trip", LocalDate.of(2019, 10, 4), LocalDate.of(2019, 10, 11));
        Task earlierDeadline = new Deadline("earlier task", LocalDate.of(2019, 9, 1));
        tasks.add(todo);
        tasks.add(laterDeadline);
        tasks.add(event);
        tasks.add(earlierDeadline);

        List<Task> schedule = tasks.schedule();

        assertEquals(List.of(earlierDeadline, event, laterDeadline), schedule);
    }

    @Test
    public void schedule_noDatedTasks_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("just a todo"));

        assertTrue(tasks.schedule().isEmpty());
    }

    @Test
    public void add_increasesSizeAndAppendsToTheEnd() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));

        tasks.add(new ToDo("b"));

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] b", tasks.get(1).toString());
    }

    @Test
    public void delete_removesAndReturnsTheTaskAtThatIndex() {
        TaskList tasks = new TaskList();
        Task first = new ToDo("a");
        Task second = new ToDo("b");
        tasks.add(first);
        tasks.add(second);

        Task removed = tasks.delete(0);

        assertEquals(first, removed);
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    public void constructor_fromExistingList_copiesItRatherThanSharingIt() {
        List<Task> initial = new ArrayList<>(List.of(new ToDo("a")));
        TaskList tasks = new TaskList(initial);

        initial.add(new ToDo("b"));

        assertEquals(1, tasks.size());
    }

    @Test
    public void asList_isUnmodifiable() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));

        assertThrows(UnsupportedOperationException.class, () -> tasks.asList().add(new ToDo("b")));
    }

    @Test
    public void matching_keywordPresentInSomeDescriptions_returnsOnlyThose() {
        TaskList tasks = new TaskList();
        Task matching = new ToDo("read book");
        tasks.add(matching);
        tasks.add(new ToDo("join sports club"));

        List<Task> matches = tasks.matching("book");

        assertEquals(List.of(matching), matches);
    }

    @Test
    public void matching_keywordAbsent_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertTrue(tasks.matching("nonexistent").isEmpty());
    }
}

package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

public class TaskListTest {

    private static final LocalDateTime AUG_6_2PM = LocalDateTime.of(2019, 8, 6, 14, 0);
    private static final LocalDateTime AUG_8_4PM = LocalDateTime.of(2019, 8, 8, 16, 0);

    @Test
    public void size_newList_isZero() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void add_severalTasks_keptInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("second"));

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][ ] second", tasks.get(1).toString());
    }

    @Test
    public void add_severalTasksAtOnce_sameAsAddingThemOneByOne() {
        TaskList atOnce = new TaskList();
        atOnce.add(new Todo("first"), new Todo("second"));

        TaskList oneByOne = new TaskList();
        oneByOne.add(new Todo("first"));
        oneByOne.add(new Todo("second"));

        assertEquals(oneByOne.size(), atOnce.size());
        assertEquals(oneByOne.get(0).toString(), atOnce.get(0).toString());
        assertEquals(oneByOne.get(1).toString(), atOnce.get(1).toString());
    }

    @Test
    public void add_noTasks_listUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        tasks.add();

        assertEquals(1, tasks.size());
    }

    @Test
    public void remove_middleTask_returnsItAndClosesTheGap() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("second"), new Todo("third"));

        Task removed = tasks.remove(1);

        assertEquals("[T][ ] second", removed.toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] third", tasks.get(1).toString());
    }

    @Test
    public void constructor_givenList_doesNotAliasIt() {
        List<Task> source = new ArrayList<>();
        source.add(new Todo("first"));
        TaskList tasks = new TaskList(source);

        source.add(new Todo("added later"));

        assertEquals(1, tasks.size());
    }

    @Test
    public void asList_anyList_isReadOnly() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        List<Task> view = tasks.asList();

        assertEquals(1, view.size());
        assertThrows(UnsupportedOperationException.class, () -> view.add(new Todo("sneaky")));
    }

    @Test
    public void getTasksOn_mixedTaskTypes_returnsOnlyMatchesInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("no date"),
                new Deadline("due that day", AUG_6_2PM),
                new Deadline("due later", AUG_8_4PM),
                new Event("spans the day", AUG_6_2PM, AUG_8_4PM));

        List<Task> matches = tasks.getTasksOn(LocalDate.of(2019, 8, 6));

        assertEquals(2, matches.size());
        assertEquals("due that day", matchDescription(matches.get(0)));
        assertEquals("spans the day", matchDescription(matches.get(1)));
    }

    @Test
    public void getTasksOn_dayInsideEventSpanOnly_returnsTheEvent() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("due elsewhere", AUG_6_2PM),
                new Event("spans the day", AUG_6_2PM, AUG_8_4PM));

        List<Task> matches = tasks.getTasksOn(LocalDate.of(2019, 8, 7));

        assertEquals(1, matches.size());
        assertEquals("spans the day", matchDescription(matches.get(0)));
    }

    @Test
    public void getTasksOn_noTaskOnThatDate_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("due that day", AUG_6_2PM));

        assertEquals(0, tasks.getTasksOn(LocalDate.of(2020, 1, 1)).size());
    }

    @Test
    public void find_keywordInSomeDescriptions_returnsOnlyThoseInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"),
                new Todo("buy milk"),
                new Deadline("return book", AUG_6_2PM));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matchDescription(matches.get(0)));
        assertEquals("return book", matchDescription(matches.get(1)));
    }

    @Test
    public void find_differentCase_stillMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        assertEquals(1, tasks.find("book").size());
        assertEquals(1, tasks.find("BOOK").size());
    }

    @Test
    public void find_partialWord_matches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("bookkeeping"));

        assertEquals(1, tasks.find("book").size());
    }

    @Test
    public void find_keywordAbsent_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(0, tasks.find("bicycle").size());
    }

    @Test
    public void checkIndexInRange_indexInsideList_noExceptionThrown() throws NovaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("second"), new Todo("third"));

        tasks.checkIndexInRange(0);
        tasks.checkIndexInRange(2);
    }

    @Test
    public void checkIndexInRange_indexPastEndOfList_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("second"), new Todo("third"));

        NovaException e = assertThrows(NovaException.class, () -> tasks.checkIndexInRange(3));
        assertEquals("There is no task 4. Your list has 3.", e.getMessage());
    }

    @Test
    public void checkIndexInRange_negativeIndexOrEmptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        assertThrows(NovaException.class, () -> tasks.checkIndexInRange(-1));
        assertThrows(NovaException.class, () -> new TaskList().checkIndexInRange(0));
    }

    @Test
    public void sort_mixedTasks_datedFirstInTimeOrderThenUndated() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("zebra"),
                new Deadline("later", AUG_8_4PM),
                new Todo("apple"),
                new Event("earlier", AUG_6_2PM, AUG_8_4PM));

        tasks.sort();

        assertEquals("earlier", tasks.get(0).getDescription());
        assertEquals("later", tasks.get(1).getDescription());
        assertEquals("apple", tasks.get(2).getDescription());
        assertEquals("zebra", tasks.get(3).getDescription());
    }

    @Test
    public void sort_eventsOrderedByStartNotEnd() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("starts later", AUG_8_4PM, AUG_8_4PM),
                new Event("starts earlier", AUG_6_2PM, AUG_8_4PM));

        tasks.sort();

        assertEquals("starts earlier", tasks.get(0).getDescription());
    }

    @Test
    public void sort_sameMoment_ordersByDescriptionIgnoringCase() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("banana", AUG_6_2PM),
                new Deadline("Apple", AUG_6_2PM));

        tasks.sort();

        assertEquals("Apple", tasks.get(0).getDescription());
        assertEquals("banana", tasks.get(1).getDescription());
    }

    @Test
    public void sort_calledTwice_orderUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("zebra"), new Deadline("due", AUG_6_2PM), new Todo("apple"));

        tasks.sort();
        List<String> afterFirst = tasks.asList().stream().map(Task::getDescription).toList();
        tasks.sort();
        List<String> afterSecond = tasks.asList().stream().map(Task::getDescription).toList();

        assertEquals(afterFirst, afterSecond);
    }

    @Test
    public void sort_emptyList_staysEmpty() {
        TaskList tasks = new TaskList();

        tasks.sort();

        assertEquals(0, tasks.size());
    }

    @Test
    public void positionOfDuplicate_matchingTaskPresent_returnsOneBasedPosition() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("read book"), new Todo("third"));

        assertEquals(2, tasks.positionOfDuplicate(new Todo("READ BOOK")));
    }

    @Test
    public void positionOfDuplicate_noMatch_returnsZero() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(0, tasks.positionOfDuplicate(new Todo("buy milk")));
        assertEquals(0, tasks.positionOfDuplicate(new Deadline("read book", AUG_6_2PM)));
    }

    @Test
    public void positionOfDuplicate_emptyList_returnsZero() {
        assertEquals(0, new TaskList().positionOfDuplicate(new Todo("read book")));
    }

    /** Pulls the description out of a task's save-file line, for readable assertions. */
    private static String matchDescription(Task task) {
        return task.toFileString().split(" \\| ", -1)[2];
    }
}

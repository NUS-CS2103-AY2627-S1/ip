package wodan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import wodan.WodanException;

/**
 * Unit tests for {@link TaskList} add, delete, date lookup, and keyword search.
 */
public class TaskListTest {
    @Test
    public void isEmptyAndSize_afterAdd_falseAndCount() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
        tasks.add(new Todo("a"));
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
    }

    @Test
    public void addAndDelete_updatesSizeAndOrder() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        tasks.add(first, second);
        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        Task removed = tasks.delete(0);
        assertEquals(first, removed);
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    public void constructor_copyOfLoadedList_independentFromOriginal() {
        ArrayList<Task> loaded = new ArrayList<>();
        loaded.add(new Todo("keep"));
        TaskList tasks = new TaskList(loaded);
        loaded.add(new Todo("extra"));
        assertEquals(1, tasks.size());
        assertEquals("keep", tasks.get(0).getDescription());
    }

    @Test
    public void taskNumbersOn_mixedTasks_keepsOriginalListNumbers() throws WodanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("no date"));
        tasks.add(new Deadline("due that day", TaskDateTime.parse("2019-12-02")));
        tasks.add(new Deadline("due another day", TaskDateTime.parse("2019-12-03")));
        Event spanning = new Event("meet",
                TaskDateTime.parse("2019-12-01"),
                TaskDateTime.parse("2019-12-03"));
        tasks.add(spanning);

        ArrayList<Integer> numbers = tasks.taskNumbersOn(LocalDate.of(2019, 12, 2));
        assertEquals(2, numbers.size());
        assertEquals(Integer.valueOf(2), numbers.get(0));
        assertEquals(Integer.valueOf(4), numbers.get(1));
    }

    @Test
    public void taskNumbersOn_noMatches_emptyList() throws WodanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("no date"));
        tasks.add(new Deadline("due later", TaskDateTime.parse("2019-12-03")));
        assertTrue(tasks.taskNumbersOn(LocalDate.of(2019, 12, 2)).isEmpty());
    }

    @Test
    public void taskNumbersMatching_keywordInDescription_ignoresCase() throws WodanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("read song"));
        tasks.add(new Deadline("return book", TaskDateTime.parse("2019-06-06")));
        tasks.add(new Todo("other"));

        ArrayList<Integer> numbers = tasks.taskNumbersMatching("BOOK");
        assertEquals(2, numbers.size());
        assertEquals(Integer.valueOf(1), numbers.get(0));
        assertEquals(Integer.valueOf(3), numbers.get(1));
        assertFalse(tasks.get(2).hasDescriptionContaining("2019"));
    }

    @Test
    public void categoryNames_mixedTags_generalFirstThenSorted() {
        TaskList tasks = new TaskList();
        Todo work = new Todo("work item");
        work.setCategory("work");
        Todo school = new Todo("school item");
        school.setCategory("school");
        tasks.add(work, new Todo("untagged"), school);
        ArrayList<String> names = tasks.categoryNames();
        assertEquals(3, names.size());
        assertEquals(Task.DEFAULT_CATEGORY, names.get(0));
        assertEquals("school", names.get(1));
        assertEquals("work", names.get(2));
    }

    @Test
    public void categoryNames_onlyTagged_omitsGeneral() {
        TaskList tasks = new TaskList();
        Todo school = new Todo("school item");
        school.setCategory("school");
        tasks.add(school);
        ArrayList<String> names = tasks.categoryNames();
        assertEquals(1, names.size());
        assertEquals("school", names.get(0));
    }

    @Test
    public void containsSameDetails_duplicateTodoAndDistinctDeadline_trueThenFalse()
            throws WodanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("borrow book"));
        assertTrue(tasks.containsSameDetails(new Todo("borrow book")));
        assertFalse(tasks.containsSameDetails(new Todo("other")));
        assertFalse(tasks.containsSameDetails(
                new Deadline("borrow book", TaskDateTime.parse("2019-12-02"))));
    }

    @Test
    public void taskNumbersMatching_noMatches_emptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read song"));
        assertTrue(tasks.taskNumbersMatching("book").isEmpty());
    }

    @Test
    public void getTasks_returnsLiveList_sameOrder() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("a");
        tasks.add(todo);
        assertEquals(todo, tasks.getTasks().get(0));
        assertEquals(1, tasks.getTasks().size());
    }

    @Test
    public void categoryNames_emptyList_empty() {
        assertTrue(new TaskList().categoryNames().isEmpty());
    }
}

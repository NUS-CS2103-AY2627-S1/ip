package zhangwei.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import zhangwei.ZhangWeiException;

/**
 * Tests {@link SortCriterion}, which decides both which keywords the user may
 * type after "sort" and what order each one produces. The orderings matter
 * most where tasks are hard to compare: a todo has no date at all, and two
 * descriptions can differ only in case.
 */
public class SortCriterionTest {

    /** Returns the given tasks sorted by the given criterion, as a new list. */
    private List<Task> sorted(SortCriterion criterion, Task... tasks) {
        List<Task> ordered = new ArrayList<>(List.of(tasks));
        ordered.sort(criterion.getComparator());
        return ordered;
    }

    /** Returns the descriptions of the given tasks, in order. */
    private List<String> descriptionsOf(List<Task> tasks) {
        List<String> descriptions = new ArrayList<>();
        for (Task task : tasks) {
            descriptions.add(task.getDescription());
        }
        return descriptions;
    }

    private Deadline deadlineOn(String description, int year, int month, int day) {
        return new Deadline(description, LocalDate.of(year, month, day));
    }

    @Test
    public void fromKeyword_knownKeyword_criterionReturned() throws ZhangWeiException {
        assertSame(SortCriterion.DATE, SortCriterion.fromKeyword("date"));
        assertSame(SortCriterion.DESCRIPTION, SortCriterion.fromKeyword("description"));
        assertSame(SortCriterion.STATUS, SortCriterion.fromKeyword("status"));
    }

    @Test
    public void fromKeyword_unknownKeyword_exceptionListsTheValidOnes() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> SortCriterion.fromKeyword("banana"));
        assertTrue(e.getMessage().contains("banana"));
        assertTrue(e.getMessage().contains("date, description, status"));
    }

    @Test
    public void fromKeyword_uppercaseKeyword_exceptionThrown() {
        // Keywords are matched exactly, as command keywords are.
        assertThrows(ZhangWeiException.class, () -> SortCriterion.fromKeyword("DATE"));
    }

    @Test
    public void listKeywords_allCriteria_commaSeparatedInDeclaredOrder() {
        assertEquals("date, description, status", SortCriterion.listKeywords());
    }

    @Test
    public void dateComparator_datedTasks_earliestFirst() {
        List<Task> ordered = sorted(SortCriterion.DATE,
                deadlineOn("later", 2019, 12, 2),
                deadlineOn("earlier", 2018, 1, 1),
                new Event("middle", LocalDate.of(2019, 1, 1), LocalDate.of(2019, 1, 2)));

        assertEquals(List.of("earlier", "middle", "later"), descriptionsOf(ordered));
    }

    @Test
    public void dateComparator_todosMixedIn_todosLast() {
        // A todo has no date, so it cannot be placed among the dated tasks.
        List<Task> ordered = sorted(SortCriterion.DATE,
                new Todo("no date"),
                deadlineOn("dated", 2019, 12, 2));

        assertEquals(List.of("dated", "no date"), descriptionsOf(ordered));
    }

    @Test
    public void dateComparator_severalTodos_originalOrderKept() {
        // The sort is stable, so tasks it cannot tell apart do not move.
        List<Task> ordered = sorted(SortCriterion.DATE,
                new Todo("first"), new Todo("second"), new Todo("third"));

        assertEquals(List.of("first", "second", "third"), descriptionsOf(ordered));
    }

    @Test
    public void descriptionComparator_mixedCase_orderedIgnoringCase() {
        List<Task> ordered = sorted(SortCriterion.DESCRIPTION,
                new Todo("zebra"), new Todo("Apple"), new Todo("banana"));

        assertEquals(List.of("Apple", "banana", "zebra"), descriptionsOf(ordered));
    }

    @Test
    public void statusComparator_mixedStatuses_notDoneFirst() {
        Todo done = new Todo("done one");
        done.markAsDone();

        List<Task> ordered = sorted(SortCriterion.STATUS, done, new Todo("still to do"));

        assertEquals(List.of("still to do", "done one"), descriptionsOf(ordered));
    }
}

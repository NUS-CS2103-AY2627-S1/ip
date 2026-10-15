package serina.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import serina.exception.SerinaException;

/**
 * Tests task-list operations that search across stored tasks.
 */
public class TaskListTest {
    private final Todo readBook = new Todo("read book");
    private final Deadline returnBook = new Deadline("return book", LocalDate.of(2026, 8, 30));
    private final Event bookClubMeeting = new Event(
            "book club meeting", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2));
    private final Todo buyBread = new Todo("buy bread");
    private final TaskList tasks = new TaskList(readBook, returnBook, bookClubMeeting, buyBread);

    @Test
    public void find_keywordMatchingMultipleTasks_returnsMatchesInOriginalOrder() {
        assertEquals(List.of(readBook, returnBook, bookClubMeeting), tasks.find("book"));
    }

    @Test
    public void find_keywordWithDifferentCase_returnsCaseInsensitiveMatches() {
        assertEquals(List.of(readBook, returnBook, bookClubMeeting), tasks.find("BOOK"));
    }

    @Test
    public void find_partialKeyword_returnsSubstringMatches() {
        assertEquals(List.of(returnBook), tasks.find("turn"));
    }

    @Test
    public void find_multiplePartialKeywordsInAnyOrder_returnsTasksContainingEveryKeyword() {
        assertEquals(List.of(bookClubMeeting), tasks.find("  MEET\tboo  "));
    }

    @Test
    public void find_keywordsSplitAcrossDifferentTasks_returnsNoMatches() {
        assertEquals(List.of(), tasks.find("book bread"));
    }

    @Test
    public void find_oneCharacterKeyword_returnsSubstringMatches() {
        assertEquals(List.of(readBook, returnBook, bookClubMeeting, buyBread), tasks.find("b"));
    }

    @Test
    public void find_keywordWithSurroundingWhitespace_returnsTrimmedKeywordMatches() {
        assertEquals(List.of(buyBread), tasks.find("  bread  "));
    }

    @Test
    public void find_keywordPresentOnlyInTaskMetadata_returnsNoMatches() {
        assertEquals(List.of(), tasks.find("2026"));
    }

    @Test
    public void find_keywordNotPresent_returnsNoMatches() {
        assertEquals(List.of(), tasks.find("exercise"));
    }

    @Test
    public void getAndDelete_validBoundaryNumbers_returnExpectedTasks() throws SerinaException {
        assertEquals(readBook, tasks.getTask(" 1 "));
        assertEquals(buyBread, tasks.getTask("4"));

        assertEquals(readBook, tasks.delete("1"));
        assertEquals(3, tasks.size());
        assertEquals(returnBook, tasks.getTask("1"));
    }

    @Test
    public void getTask_invalidNumbers_throwsWithoutChangingList() {
        List<String> invalidNumbers = List.of("0", "-1", "5", "one", "2147483648", "1.0", "");

        for (String invalidNumber : invalidNumbers) {
            assertThrows(SerinaException.class, () -> tasks.getTask(invalidNumber));
        }
        assertEquals(4, tasks.size());
    }

    @Test
    public void add_duplicateAndCapacity_rejectsWithoutChangingList() throws SerinaException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Inspect engines"));
        assertThrows(SerinaException.class, () -> taskList.add(new Todo(" inspect   ENGINES ")));

        for (int index = 2; index <= 100; index++) {
            taskList.add(new Todo("task " + index));
        }
        assertEquals(100, taskList.size());
        assertThrows(SerinaException.class, () -> taskList.add(new Todo("task 101")));
        assertEquals(100, taskList.size());
    }

    @Test
    public void copyAndReplace_mutatingCopies_doesNotChangeOtherLists() throws SerinaException {
        TaskList copy = tasks.copy();
        copy.getTask("1").markAsDone();
        copy.delete("4");

        assertFalse(tasks.getTask("1").isDone());
        assertEquals(4, tasks.size());

        TaskList replacementTarget = new TaskList();
        replacementTarget.replaceWith(copy);
        copy.getTask("1").markAsNotDone();

        assertTrue(replacementTarget.getTask("1").isDone());
        assertEquals(3, replacementTarget.size());
    }

    @Test
    public void asList_returnedView_cannotChangeListStructure() {
        assertThrows(UnsupportedOperationException.class, () -> tasks.asList().remove(0));
        assertEquals(4, tasks.size());
    }
}

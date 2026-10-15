package edith.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task ordering, mutation, and snapshot isolation. */
public class TaskListTest {
    @Test
    public void addGetAndRemove_multipleTasks_preservesOrder() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(first, tasks.remove(0));
        assertSame(second, tasks.get(0));
        assertEquals(1, tasks.size());
    }

    @Test
    public void containsEquivalent_ignoresCompletionAndTagsButChecksTaskDetails() {
        Todo existing = new Todo("read book");
        existing.markAsDone();
        existing.addTag("#school");
        TaskList tasks = new TaskList(List.of(existing));

        assertTrue(tasks.containsEquivalent(new Todo("read book")));
        assertFalse(tasks.containsEquivalent(new Todo("read notes")));
    }

    @Test
    public void constructor_sourceListChanged_taskListUnaffected() {
        List<Task> source = new ArrayList<>();
        source.add(new Todo("original"));
        TaskList tasks = new TaskList(source);

        source.clear();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] original", tasks.get(0).toString());
    }

    @Test
    public void asList_returnedSnapshotCannotBeModified() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<Task> snapshot = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("write book")));
        assertEquals(1, tasks.size());
    }

    @Test
    public void isEmpty_emptyThenPopulated_returnsExpectedState() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        tasks.add(new Todo("read book"));
        assertFalse(tasks.isEmpty());
    }

    @Test
    public void find_wordOrPhrase_returnsCaseInsensitiveMatchesInOriginalOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read Book"),
                new Todo("book flight"),
                new Todo("read notes")));

        TaskList wordMatches = tasks.find("BOOK");
        TaskList phraseMatches = tasks.find("read book");

        assertEquals(2, wordMatches.size());
        assertEquals("[T][ ] Read Book", wordMatches.get(0).toString());
        assertEquals("[T][ ] book flight", wordMatches.get(1).toString());
        assertEquals(1, phraseMatches.size());
        assertEquals("[T][ ] Read Book", phraseMatches.get(0).toString());
    }

    @Test
    public void find_validTagQuery_matchesOnlyExactTagsIgnoringCase() {
        Todo tagged = new Todo("read notes");
        tagged.addTag("#Fun");
        Todo longerTag = new Todo("plan holiday");
        longerTag.addTag("#funny");
        TaskList tasks = new TaskList(List.of(
                new Todo("plan #fun party"), tagged, longerTag));

        TaskList matches = tasks.find("#FUN");

        assertEquals(1, matches.size());
        assertSame(tagged, matches.get(0));
        assertTrue(tasks.find("#fu").isEmpty());
    }

    @Test
    public void find_plainText_matchesDescriptionsAndTagsInOriginalOrder() {
        Todo tagged = new Todo("read notes");
        tagged.addTag("#Fun");
        Todo longerTag = new Todo("plan holiday");
        longerTag.addTag("#funny");
        Todo described = new Todo("plan #fun party");
        TaskList tasks = new TaskList(List.of(tagged, described, longerTag));

        TaskList matches = tasks.find("fun");

        assertEquals(3, matches.size());
        assertSame(tagged, matches.get(0));
        assertSame(described, matches.get(1));
        assertSame(longerTag, matches.get(2));
    }

    @Test
    public void find_hashPhrase_matchesDescriptionAsPlainText() {
        Todo described = new Todo("plan #fun party");
        Todo tagged = new Todo("plan holiday");
        tagged.addTag("#fun");
        TaskList tasks = new TaskList(List.of(tagged, described));

        TaskList matches = tasks.find("#fun party");

        assertEquals(1, matches.size());
        assertSame(described, matches.get(0));
    }
}

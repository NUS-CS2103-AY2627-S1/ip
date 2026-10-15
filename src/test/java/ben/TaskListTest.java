package ben;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests for {@link TaskList}, focusing on 1-based indexing and range checks. */
class TaskListTest {
    @Test
    void addAndSize_trackTheNumberOfTasks() throws BenException {
        TaskList list = new TaskList();
        assertTrue(list.isEmpty());
        list.add(new Todo("a"));
        list.add(new Todo("b"));
        assertEquals(2, list.size());
        assertFalse(list.isEmpty());
    }

    @Test
    void add_exactDuplicateTask_throwsBenException() throws BenException {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        assertThrows(BenException.class, () -> list.add(new Todo("Read Book")));
        assertEquals(1, list.size());
    }

    @Test
    void add_sameDescriptionDifferentType_isNotADuplicate() throws BenException {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Deadline("read book", "2019-12-02"));
        assertEquals(2, list.size());
    }

    @Test
    void get_usesOneBasedIndexing() throws BenException {
        TaskList list = new TaskList();
        list.add(new Todo("first"));
        list.add(new Todo("second"));
        assertEquals("[T][ ] second", list.get(2).toString());
    }

    @Test
    void get_indexOutOfRange_throwsBenException() throws BenException {
        TaskList list = new TaskList();
        list.add(new Todo("only"));
        assertThrows(BenException.class, () -> list.get(0));
        assertThrows(BenException.class, () -> list.get(2));
    }

    @Test
    void remove_returnsRemovedTaskAndShrinksList() throws BenException {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));
        Task removed = list.remove(1);
        assertEquals("[T][ ] a", removed.toString());
        assertEquals(1, list.size());
    }

    @Test
    void find_matchesKeywordCaseInsensitivelyAndKeepsOrder() throws BenException {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("buy milk"));
        list.add(new Todo("return Book to library"));
        assertEquals(2, list.find("book").size());
        assertEquals("[T][ ] read book", list.find("book").get(0).toString());
        assertTrue(list.find("nonsense").isEmpty());
    }
}

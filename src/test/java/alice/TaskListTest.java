package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void add_singleTask_sizeIncreases() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void add_multipleTasks_correctOrderMaintained() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("return book", tasks.get(1).getDescription());
    }

    @Test
    public void remove_validIndex_taskRemovedAndReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        Task removed = tasks.remove(0);

        assertEquals("read book", removed.getDescription());
        assertEquals(1, tasks.size());
        assertEquals("return book", tasks.get(0).getDescription());
    }

    @Test
    public void isValidIndex_indexWithinBounds_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertTrue(tasks.isValidIndex(0));
    }

    @Test
    public void isValidIndex_negativeIndex_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertFalse(tasks.isValidIndex(-1));
    }

    @Test
    public void isValidIndex_indexEqualToSize_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertFalse(tasks.isValidIndex(1));
    }

    @Test
    public void isValidIndex_emptyList_returnsFalse() {
        TaskList tasks = new TaskList();
        assertFalse(tasks.isValidIndex(0));
    }

    @Test
    public void find_singleKeyword_returnsSubstringMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy milk"));

        assertEquals(2, tasks.find("book").size());
    }

    @Test
    public void find_keywordWithNoMatch_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.find("umbrella").isEmpty());
    }

    @Test
    public void find_multipleKeywords_returnsTheUnion() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        tasks.add(new Todo("wash car"));

        assertEquals(2, tasks.find("book", "milk").size());
    }

    @Test
    public void find_taskMatchingSeveralKeywords_isReturnedOnlyOnce() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(1, tasks.find("read", "book").size());
    }

    @Test
    public void find_preservesOriginalListOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("book one"));
        tasks.add(new Todo("unrelated"));
        tasks.add(new Todo("book two"));

        assertEquals("book one", tasks.find("book").get(0).getDescription());
        assertEquals("book two", tasks.find("book").get(1).getDescription());
    }

    @Test
    public void find_isCaseSensitive() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read Book"));

        assertTrue(tasks.find("book").isEmpty());
        assertEquals(1, tasks.find("Book").size());
    }

    @Test
    public void find_matchesMidWord() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("bookshelf"));

        assertEquals(1, tasks.find("ookshe").size());
    }

    @Test
    public void size_emptyList_returnsZero() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }
}

package lion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests task-list operations that change task order or completion state.
 */
public class TaskListTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void redirectStorageToTempDir() {
        Storage.setDirectoryPathForTesting(tempDir.toString());
    }

    @AfterEach
    void restoreStorage() {
        Storage.setDirectoryPathForTesting(null);
    }

    @Test
    void constructor_nullBackingList_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    void find_nullKeyword_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.find(null));
    }

    @Test
    void save_writesTasksThatCanBeLoadedBack() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2/12/2026 1800"));

        assertDoesNotThrow(tasks::save);

        List<Task> reloaded = Storage.load();
        assertEquals(2, reloaded.size());
        assertEquals("[T] [ ] read book", reloaded.get(0).toString());
        assertEquals("[D] [ ] return book (by: Dec 02 2026 6:00 PM)", reloaded.get(1).toString());
    }

    @Test
    void add_multipleTasks_increasesSizeAndPreservesOrder() {
        TaskList tasks = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("write notes");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    void delete_middleTask_returnsDeletedTaskAndClosesGap() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task middle = new Todo("middle");
        Task last = new Todo("last");
        tasks.add(first);
        tasks.add(middle);
        tasks.add(last);

        Task deleted = tasks.delete(1);

        assertSame(middle, deleted);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(last, tasks.get(1));
    }

    @Test
    void markThenUnmark_taskStatusChangesAccordingly() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.mark(0);
        assertEquals("1", tasks.get(0).getStatusIcon());

        tasks.unmark(0);
        assertEquals("0", tasks.get(0).getStatusIcon());
    }

    @Test
    void find_matchingDescriptions_returnsMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Todo("write notes");
        Task secondMatch = new Deadline("return book", "2/12/2026 1800");
        tasks.add(firstMatch);
        tasks.add(nonMatch);
        tasks.add(secondMatch);

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertSame(firstMatch, matches.get(0));
        assertSame(secondMatch, matches.get(1));
    }

    @Test
    void find_noMatchingDescription_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        TaskList matches = tasks.find("notes");

        assertEquals(0, matches.size());
    }

    @Test
    void hasDuplicate_sameTypeAndDescription_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.hasDuplicate(new Todo("read book")));
    }

    @Test
    void hasDuplicate_sameDescriptionDifferentType_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertFalse(tasks.hasDuplicate(new Deadline("read book", "2/12/2026 1800")));
    }

    @Test
    void hasDuplicate_deadlineSameDescriptionDifferentDate_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", "2/12/2026 1800"));

        assertFalse(tasks.hasDuplicate(new Deadline("return book", "3/12/2026 1800")));
    }

    @Test
    void hasDuplicate_eventSameDescriptionAndTimes_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("meeting", "Monday 2pm", "Monday 4pm"));

        assertTrue(tasks.hasDuplicate(new Event("meeting", "Monday 2pm", "Monday 4pm")));
    }

    @Test
    void hasDuplicate_noMatchingTask_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertFalse(tasks.hasDuplicate(new Todo("write notes")));
    }
}

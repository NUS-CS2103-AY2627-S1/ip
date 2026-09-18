package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import alfred.AlfredException;

/**
 * Tests add, get, delete, mark, unmark, and bounds checks on {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void add_appendsInInsertionOrder() throws AlfredException {
        TaskList tasks = new TaskList();
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        tasks.add(first);
        tasks.add(second);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() throws AlfredException {
        TaskList tasks = new TaskList();
        Task keep = new ToDo("keep");
        Task remove = new ToDo("remove");
        tasks.add(keep);
        tasks.add(remove);
        Task deleted = tasks.delete(1);
        assertSame(remove, deleted);
        assertEquals(1, tasks.size());
        assertSame(keep, tasks.get(0));
    }

    @Test
    public void delete_invalidIndex_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("only"));
        AlfredException emptyIndex = assertThrows(AlfredException.class, () -> tasks.delete(1));
        assertEquals("that task number does not exist, sir.", emptyIndex.getMessage());
        AlfredException negative = assertThrows(AlfredException.class, () -> tasks.delete(-1));
        assertEquals("that task number does not exist, sir.", negative.getMessage());
    }

    @Test
    public void get_emptyList_throwsException() {
        TaskList tasks = new TaskList();
        AlfredException exception = assertThrows(AlfredException.class, () -> tasks.get(0));
        assertEquals("that task number does not exist, sir.", exception.getMessage());
    }

    @Test
    public void markAndUnmark_updateCompletion() throws AlfredException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        Task marked = tasks.mark(0);
        assertEquals("[T][X] read book", marked.getDisplayText());
        Task unmarked = tasks.unmark(0);
        assertEquals("[T][ ] read book", unmarked.getDisplayText());
    }

    @Test
    public void mark_invalidIndex_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        AlfredException exception = assertThrows(AlfredException.class, () -> tasks.mark(4));
        assertEquals("that task number does not exist, sir.", exception.getMessage());
    }

    @Test
    public void getTasks_returnsUnmodifiableView() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        List<Task> view = tasks.getTasks();
        assertEquals(1, view.size());
        assertThrows(UnsupportedOperationException.class, () -> view.add(new ToDo("other")));
    }

    @Test
    public void find_keywordInDescription_returnsMatchingTasksInOrder() {
        TaskList tasks = new TaskList();
        Task readBook = new ToDo("read book");
        Task meeting = new ToDo("project meeting");
        Task returnBook = new Deadline("return book", TaskDateTime.parseUserInput("2019-06-06"));
        tasks.add(readBook);
        tasks.add(meeting);
        tasks.add(returnBook);
        readBook.markAsDone();
        returnBook.markAsDone();

        List<Task> matches = tasks.find("book");
        assertEquals(2, matches.size());
        assertSame(readBook, matches.get(0));
        assertSame(returnBook, matches.get(1));
    }

    @Test
    public void find_ignoresCaseAndSkipsDateText() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Read Book"));
        tasks.add(new Deadline("submit report", TaskDateTime.parseUserInput("2019-06-06")));

        List<Task> matches = tasks.find("BOOK");
        assertEquals(1, matches.size());
        assertEquals("Read Book", matches.get(0).getDescription());
        assertEquals(0, tasks.find("June").size());
    }

    @Test
    public void copyConstructor_doesNotAliasCallerList() throws AlfredException {
        List<Task> original = new ArrayList<>();
        original.add(new ToDo("kept"));
        TaskList tasks = new TaskList(original);
        original.add(new ToDo("extra"));
        assertEquals(1, tasks.size());
        assertEquals("kept", tasks.get(0).getDescription());
    }

    @Test
    public void archive_hidesTaskFromLiveListAndRenumbers() throws AlfredException {
        TaskList tasks = new TaskList();
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        Task third = new ToDo("third");
        tasks.add(first);
        tasks.add(second);
        tasks.add(third);

        Task archived = tasks.archive(1);
        assertSame(second, archived);
        assertTrue(second.isArchived());
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(third, tasks.get(1));
        assertEquals(1, tasks.getArchivedTasks().size());
        assertSame(second, tasks.getArchivedTasks().get(0));
    }

    @Test
    public void archiveAll_emptyLiveList_throwsException() {
        TaskList tasks = new TaskList();
        AlfredException exception = assertThrows(AlfredException.class, tasks::archiveAll);
        assertEquals("there are no tasks to archive, sir.", exception.getMessage());
    }

    @Test
    public void restoreAll_emptyArchive_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("live"));
        AlfredException exception = assertThrows(AlfredException.class, tasks::restoreAll);
        assertEquals("there are no archived tasks, sir.", exception.getMessage());
    }

    @Test
    public void deleteArchived_emptyArchive_throwsException() {
        TaskList tasks = new TaskList();
        AlfredException exception = assertThrows(AlfredException.class, () -> tasks.deleteArchived(0));
        assertEquals("there are no archived tasks, sir.", exception.getMessage());
    }

    @Test
    public void restore_appendsToEndOfLiveList() throws AlfredException {
        TaskList tasks = new TaskList();
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        Task third = new ToDo("third");
        tasks.add(first);
        tasks.add(second);
        tasks.add(third);
        tasks.archive(0);
        tasks.archive(0);

        Task restored = tasks.restore(0);
        assertSame(first, restored);
        assertFalse(first.isArchived());
        assertEquals(2, tasks.size());
        assertSame(third, tasks.get(0));
        assertSame(first, tasks.get(1));
    }

    @Test
    public void restoreAll_preservesArchiveOrder() throws AlfredException {
        TaskList tasks = new TaskList();
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        Task third = new ToDo("third");
        tasks.add(first);
        tasks.add(second);
        tasks.add(third);
        tasks.archive(0);
        tasks.archive(0);

        int restoredCount = tasks.restoreAll();
        assertEquals(2, restoredCount);
        assertEquals(3, tasks.size());
        assertSame(third, tasks.get(0));
        assertSame(first, tasks.get(1));
        assertSame(second, tasks.get(2));
        assertTrue(tasks.getArchivedTasks().isEmpty());
    }

    @Test
    public void deleteArchived_removesPermanently() throws AlfredException {
        TaskList tasks = new TaskList();
        Task keep = new ToDo("keep");
        Task gone = new ToDo("gone");
        tasks.add(keep);
        tasks.add(gone);
        tasks.archive(1);

        Task deleted = tasks.deleteArchived(0);
        assertSame(gone, deleted);
        assertEquals(1, tasks.size());
        assertTrue(tasks.getArchivedTasks().isEmpty());
        assertEquals(1, tasks.getAllTasks().size());
    }

    @Test
    public void delete_liveIndex_cannotSeeArchivedTask() throws AlfredException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("live"));
        Task archived = new ToDo("archived");
        tasks.add(archived);
        tasks.archive(1);
        AlfredException exception = assertThrows(AlfredException.class, () -> tasks.delete(1));
        assertEquals("that task number does not exist, sir.", exception.getMessage());
        assertTrue(archived.isArchived());
    }

    @Test
    public void find_skipsArchivedTasks() throws AlfredException {
        TaskList tasks = new TaskList();
        Task live = new ToDo("read book");
        Task archived = new ToDo("return book");
        tasks.add(live);
        tasks.add(archived);
        tasks.archive(1);
        List<Task> matches = tasks.find("book");
        assertEquals(1, matches.size());
        assertSame(live, matches.get(0));
    }
}

package zaru.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zaru.exception.ZaruException;
import zaru.storage.Storage;

/** Tests task-list mutations and loading from persistent storage. */
public class TaskListTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies that adding and deleting tasks updates list contents and size. */
    @Test
    public void addAndDelete_tasks_updatesListContents() throws ZaruException {
        TaskList tasks = createTaskList("tasks.txt");

        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("return book"));
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTaskString(1));

        tasks.delete(1);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] return book", tasks.getTaskString(1));
    }

    /** Verifies that completion state can be toggled for a task. */
    @Test
    public void completeAndUncomplete_task_updatesCompletionState() throws ZaruException {
        TaskList tasks = createTaskList("completion.txt");
        tasks.add(new ToDo("read book"));

        tasks.markAsComplete(1);
        assertEquals("[T][x] read book", tasks.getTaskString(1));

        tasks.markAsIncomplete(1);
        assertEquals("[T][ ] read book", tasks.getTaskString(1));
    }

    /** Verifies that tasks with matching type and details are not added twice. */
    @Test
    public void add_duplicateTask_throwsException() throws ZaruException {
        TaskList tasks = createTaskList("duplicates.txt");
        tasks.add(new ToDo("Read Book"));

        ZaruException exception = assertThrows(ZaruException.class, () -> tasks.add(new ToDo("read book")));

        assertEquals("That task already exists in your list.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    /** Verifies that failed saves roll back every in-memory task-list mutation. */
    @Test
    public void mutations_saveFailure_restorePreviousState() throws ZaruException {
        ControllableStorage storage = new ControllableStorage(temporaryDirectory.resolve("rollback.txt"));
        TaskList tasks = new TaskList(storage);
        tasks.add(new ToDo("write notes"));
        tasks.add(new ToDo("buy book"));
        storage.setFailing(true);

        assertThrows(ZaruException.class, () -> tasks.add(new ToDo("read book")));
        assertEquals(2, tasks.size());

        assertThrows(ZaruException.class, () -> tasks.delete(1));
        assertEquals("[T][ ] write notes", tasks.getTaskString(1));

        assertThrows(ZaruException.class, () -> tasks.markAsComplete(1));
        assertEquals("[T][ ] write notes", tasks.getTaskString(1));

        assertThrows(ZaruException.class, tasks::sort);
        assertEquals("[T][ ] write notes", tasks.getTaskString(1));
        assertEquals("[T][ ] buy book", tasks.getTaskString(2));
    }

    /** Verifies that filtering returns tasks whose titles contain the target text. */
    @Test
    public void filterByTitle_matchingTitles_returnsMatchingTasks() throws ZaruException {
        TaskList tasks = createTaskList("filter-matches.txt");
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("return book"));
        tasks.add(new ToDo("watch movie"));

        List<Task> matchingTasks = tasks.filterByTitle("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return book", matchingTasks.get(1).getDescription());
    }

    /** Verifies that filtering ignores differences in letter case. */
    @Test
    public void filterByTitle_differentLetterCase_returnsMatchingTasks() throws ZaruException {
        TaskList tasks = createTaskList("filter-case.txt");
        tasks.add(new ToDo("Read Book"));

        List<Task> matchingTasks = tasks.filterByTitle("book");

        assertEquals(1, matchingTasks.size());
        assertEquals("Read Book", matchingTasks.get(0).getDescription());
    }

    /** Verifies that filtering returns an empty list when no title matches. */
    @Test
    public void filterByTitle_noMatchingTitles_returnsEmptyList() throws ZaruException {
        TaskList tasks = createTaskList("filter-empty.txt");
        tasks.add(new ToDo("read book"));

        assertEquals(List.of(), tasks.filterByTitle("exercise"));
    }

    /** Verifies that sorting groups task types and orders dated tasks chronologically. */
    @Test
    public void sort_mixedTaskTypes_sortsByTypeThenChronologically() throws ZaruException {
        TaskList tasks = createTaskList("sort.txt");
        tasks.add(new Event("later event", LocalDateTime.of(2026, 9, 8, 14, 0),
                LocalDateTime.of(2026, 9, 8, 15, 0)));
        tasks.add(new Deadline("later deadline", LocalDateTime.of(2026, 9, 10, 18, 0)));
        tasks.add(new ToDo("write notes"));
        tasks.add(new Event("earlier event", LocalDateTime.of(2026, 9, 8, 10, 0),
                LocalDateTime.of(2026, 9, 8, 11, 0)));
        tasks.add(new Deadline("earlier deadline", LocalDateTime.of(2026, 9, 9, 18, 0)));
        tasks.add(new ToDo("buy book"));

        tasks.sort();

        assertEquals("[T][ ] buy book", tasks.getTaskString(1));
        assertEquals("[T][ ] write notes", tasks.getTaskString(2));
        assertEquals("[D][ ] earlier deadline (by: Sep 09 2026, 6:00PM)", tasks.getTaskString(3));
        assertEquals("[D][ ] later deadline (by: Sep 10 2026, 6:00PM)", tasks.getTaskString(4));
        assertEquals(
                "[E][ ] earlier event (from: Sep 08 2026, 10:00AM) (to: Sep 08 2026, 11:00AM)",
                tasks.getTaskString(5));
        assertEquals(
                "[E][ ] later event (from: Sep 08 2026, 2:00PM) (to: Sep 08 2026, 3:00PM)",
                tasks.getTaskString(6));
    }
    /** Verifies that saved tasks are loaded into a task list. */
    @Test
    public void loadFromStorage_savedTasks_populatesList() throws ZaruException {
        Storage storage = new Storage(temporaryDirectory.resolve("saved.txt"));
        storage.save(List.of(new ToDo("read book")));
        TaskList tasks = new TaskList(storage);

        tasks.loadFromStorage();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTaskString(1));
    }

    /** Creates a task list backed by a temporary save file. */
    private TaskList createTaskList(String fileName) {
        return new TaskList(new Storage(temporaryDirectory.resolve(fileName)));
    }

    /** Storage helper whose writes can be disabled to test mutation rollback. */
    private static class ControllableStorage extends Storage {
        private boolean isFailing;

        ControllableStorage(Path filePath) {
            super(filePath);
        }

        /** Enables or disables simulated save failures. */
        private void setFailing(boolean isFailing) {
            this.isFailing = isFailing;
        }

        @Override
        public void save(List<Task> tasks) throws ZaruException {
            if (isFailing) {
                throw new ZaruException("Simulated save failure.");
            }

            super.save(tasks);
        }
    }
}

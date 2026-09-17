package potatobot.model.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import potatobot.exception.PotatoBotException;

public class TaskListTest {
    @Test
    public void add_indexedInsertion_preservesOrderAtEveryPosition() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(0, new Task("last"));
        tasks.add(0, new Task("first"));
        tasks.add(1, new Task("middle"));
        tasks.add(tasks.size(), new Task("end"));
        assertEquals("[ ] first" + System.lineSeparator() + "[ ] middle" + System.lineSeparator()
                + "[ ] last" + System.lineSeparator() + "[ ] end", tasks.toFileContents());
    }

    @Test
    public void add_indexedInsertionAtCapacity_rejectsWithoutMutation() throws PotatoBotException {
        TaskList tasks = new TaskList();
        for (int i = 0; i < TaskList.MAX_SIZE; i++) {
            tasks.add(new Task("task " + i));
        }
        String original = tasks.toFileContents();
        assertEquals("Your potato sack is full! It can only hold 100 items.",
                assertThrows(PotatoBotException.class, () -> tasks.add(0, new Task("extra"))).getMessage());
        assertEquals(original, tasks.toFileContents());
    }

    @Test
    public void access_invalidIndices_rejectsWithoutMutation() throws PotatoBotException {
        TaskList tasks = new TaskList();
        Task task = new Task("original");
        tasks.add(task);
        for (int index : new int[] { -1, 1 }) {
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.markDone(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.markReset(index));
        }
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(-1, new Task("invalid")));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(2, new Task("invalid")));
        assertSame(task, tasks.get(0));
        assertEquals(1, tasks.size());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void find_nonconsecutiveMatches_renumbersWithoutChangingOriginalList() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("skip"), new Task("book one"), new Task("skip again"), new Task("book two"));
        tasks.markDone(3);
        String original = tasks.toFileContents();
        assertEquals("Here are the tasks in your potato sack:\n  1.[ ] book one\n  2.[X] book two", tasks.find("book"));
        assertEquals(original, tasks.toFileContents());
        assertEquals(tasks.printList(), tasks.find(""));
        assertEquals("Nothing to see here...", new TaskList().find("book"));
    }

    @Test
    public void delete_onlyTask_restoresEmptyState() throws PotatoBotException {
        TaskList tasks = new TaskList();
        Task task = new Task("only");
        tasks.add(task);
        assertSame(task, tasks.delete(0));
        assertTrue(tasks.isEmpty());
        assertEquals("", tasks.toFileContents());
        assertEquals("Nothing to see here...", tasks.printList());
    }

    @Test
    public void constructor_newTaskList_emptyStateCreated() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
        assertEquals("Nothing to see here...", tasks.printList());
        assertEquals("", tasks.toFileContents());
    }

    @Test
    public void add_belowCapacity_taskStored() throws PotatoBotException {
        TaskList tasks = new TaskList();
        Task task = new Task("read book");

        tasks.add(task);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    public void add_multipleTasks_allTasksStoredInOrder() throws PotatoBotException {
        TaskList tasks = new TaskList();
        Task firstTask = new Task("read book");
        Task secondTask = new Todo("write code");

        tasks.add(firstTask, secondTask);

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    public void add_atCapacity_exceptionThrown() throws PotatoBotException {
        TaskList tasks = new TaskList();
        for (int i = 0; i < TaskList.MAX_SIZE; i++) {
            tasks.add(new Task("task " + i));
        }

        PotatoBotException exception = assertThrows(
                PotatoBotException.class, () -> tasks.add(new Task("overflow")));

        assertEquals(
                "Your potato sack is full! It can only hold 100 items.",
                exception.getMessage());
        assertEquals(TaskList.MAX_SIZE, tasks.size());
    }

    @Test
    public void add_batchExceedsRemainingCapacity_noTasksAdded() throws PotatoBotException {
        // Fill the tasklist
        TaskList tasks = new TaskList();
        for (int i = 0; i < TaskList.MAX_SIZE - 1; i++) {
            tasks.add(new Task("task " + i));
        }

        assertThrows(PotatoBotException.class,
                () -> tasks.add(new Task("first extra"), new Task("second extra")));

        assertEquals(TaskList.MAX_SIZE - 1, tasks.size());
        tasks.add(new Task("last task"));
        tasks.add();
        assertEquals(TaskList.MAX_SIZE, tasks.size());
    }

    @Test
    public void markDone_existingTask_completionStatusUpdated() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));

        tasks.markDone(0);

        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    public void markReset_completedTask_incompleteStatusRestored() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));
        tasks.markDone(0);

        tasks.markReset(0);

        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    public void delete_existingTask_taskRemovedAndReturned() throws PotatoBotException {
        TaskList tasks = new TaskList();
        Task firstTask = new Task("first");
        Task secondTask = new Task("second");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task deletedTask = tasks.delete(0);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
    }

    @Test
    public void printList_multipleTasks_numberedStatusesReturned() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));
        tasks.add(new Todo("write code"));
        tasks.markDone(1);

        assertEquals(
                "Here are the tasks in your potato sack:\n"
                        + "  1.[ ] read book\n"
                        + "  2.[X] write code (Todo)",
                tasks.printList());
    }

    @Test
    public void find_keywordMatchesDescriptions_filteredTasksReturned() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));
        Task completedTask = new Task("return book");
        tasks.add(completedTask);
        tasks.add(new Task("write code"));
        tasks.markDone(1);

        assertEquals(
                "Here are the tasks in your potato sack:\n"
                        + "  1.[ ] read book\n"
                        + "  2.[X] return book",
                tasks.find("BOOK"));
    }

    @Test
    public void find_keywordHasNoMatches_emptyListMessageReturned() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));

        assertEquals("Nothing to see here...", tasks.find("movie"));
    }

    @Test
    public void toFileContents_multipleTasks_storageFormatReturned() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));
        tasks.add(new Todo("write code"));
        tasks.markDone(1);

        assertEquals(
                "[ ] read book" + System.lineSeparator() + "[X] write code (Todo)",
                tasks.toFileContents());
    }
}

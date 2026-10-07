package mary.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import mary.storage.Storage;
import mary.task.Deadline;
import mary.task.Task;
import mary.task.Todo;

/**
 * Checks sort output, saved ordering, argument validation and persistence failure.
 */
class SortCommandTest extends CommandTestSupport {
    /**
     * Checks that list numbering, saved order and subsequent numbered commands agree.
     */
    @Test
    void execute_validSort_savesOrderAndUsesNewNumbers() throws Exception {
        tasks.add(new Todo("read"));
        tasks.add(new Deadline("return", LocalDateTime.of(2026, 12, 2, 18, 0)));
        String output = execute(new SortCommand(" \t"));
        assertEquals(" Tasks lined up chronologically (deadlines by due time, events by start time; todos last).\n"
                + " Here's your task lineup:\n"
                + " 1.[D][ ] return (by: 2 Dec 2026 18:00)\n 2.[T][ ] read\n", output);
        assertEquals(tasks.getTasks().stream().map(Task::toStorageRecord).toList(),
                storage.load().stream().map(Task::toStorageRecord).toList());
        execute(new MarkCommand("mark 1"));
        assertEquals("X", tasks.get(0).getStatusIcon());
        execute(new DeleteCommand("delete 2"));
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof Deadline);
    }

    /**
     * Checks that sorting an empty list does not create a data file.
     */
    @Test
    void execute_emptyList_reportsEmptyWithoutWriting() {
        assertEquals(" Nothing to chase yet! Add a task with 'todo description'.\n", execute(new SortCommand("")));
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    /**
     * Checks that unsupported options leave both task ordering and saved data untouched.
     */
    @Test
    void execute_extraArguments_reportsUsageWithoutChanges() {
        Task todo = new Todo("read");
        Task deadline = new Deadline("return", LocalDateTime.of(2026, 12, 2, 18, 0));
        tasks.add(todo);
        tasks.add(deadline);
        assertEquals(" Error: use 'sort' without arguments to order tasks chronologically.\n",
                execute(new SortCommand(" descending")));
        assertEquals(List.of(todo, deadline), tasks.getTasks());
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    /**
     * Checks that save errors suppress success output and preserve task contents in memory.
     */
    @Test
    void execute_saveFailure_reportsErrorWithInMemoryOrder() {
        Task todo = new Todo("read");
        Task deadline = new Deadline("return", LocalDateTime.of(2026, 12, 2, 18, 0));
        tasks.add(todo);
        tasks.add(deadline);
        storage = new Storage(directory.toString());
        String output = execute(new SortCommand(""));
        assertTrue(output.startsWith(" Error: could not save tasks"));
        assertFalse(output.contains("Tasks lined up"));
        assertEquals(List.of(todo, deadline), tasks.getTasks());
    }
}

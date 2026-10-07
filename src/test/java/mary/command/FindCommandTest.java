package mary.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import mary.task.Deadline;
import mary.task.Event;
import mary.task.Todo;

/**
 * Tests search output, validation, and absence of persistence side effects.
 */
class FindCommandTest extends CommandTestSupport {
    /**
     * Checks case-insensitive results across task types with independent numbering.
     */
    @Test
    void execute_matchesAcrossTypes_preservesOrderAndStatus() throws Exception {
        tasks.add(new Todo("buy bread"));
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        tasks.add(new Deadline("return Book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.add(new Event("BOOK club", LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0)));
        storage.save(tasks.getTasks());
        String saved = Files.readString(directory.resolve("tasks.txt"));

        assertEquals(" Look what I sniffed out:\n"
                + " 1.[T][X] read book\n"
                + " 2.[D][ ] return Book (by: 2 Dec 2019 18:00)\n"
                + " 3.[E][ ] BOOK club (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)\n",
                execute(new FindCommand("  bOoK  ")));
        assertEquals(saved, Files.readString(directory.resolve("tasks.txt")));
        assertEquals(4, tasks.size());
        assertEquals("X", todo.getStatusIcon());
    }

    /**
     * Checks both empty-list and nonmatching-list responses without creating a file.
     */
    @Test
    void execute_noMatches_reportsEmptyResultsWithoutSaving() {
        assertEquals(" No matching tasks in sight. Try another keyword.\n", execute(new FindCommand("book")));
        tasks.add(new Todo("read"));
        assertEquals(" No matching tasks in sight. Try another keyword.\n", execute(new FindCommand("book")));
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    /**
     * Checks that a blank keyword reports corrective usage without modifying tasks.
     */
    @Test
    void execute_blankKeyword_reportsUsage() {
        tasks.add(new Todo("read"));
        for (String keyword : new String[] {"", " ", "\t  "}) {
            assertEquals(" Error: use 'find keyword', for example 'find book'.\n",
                    execute(new FindCommand(keyword)));
        }
        assertEquals(1, tasks.size());
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    /**
     * Checks that multiword search text is treated as one literal phrase.
     */
    @Test
    void execute_phrase_matchesLiteralSubstring() {
        tasks.add(new Todo("read book tonight"));
        tasks.add(new Todo("read another book"));
        assertEquals(" Look what I sniffed out:\n 1.[T][ ] read book tonight\n",
                execute(new FindCommand("READ BOOK")));
    }
}

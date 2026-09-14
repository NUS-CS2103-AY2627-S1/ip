package toot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list search behavior.
 */
public class TaskListTest {
    @Test
    public void find_mixedCaseKeyword_matchesDescriptionsOnlyInOriginalOrder() {
        Todo todo = new Todo("Read Book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 6, 6));
        Event event = new Event("meeting", "book club", "midnight");
        TaskList tasks = new TaskList(List.of(todo, deadline, event));

        List<Task> matches = tasks.find("BOOK");

        assertEquals(2, matches.size());
        assertSame(todo, matches.get(0));
        assertSame(deadline, matches.get(1));
    }
}

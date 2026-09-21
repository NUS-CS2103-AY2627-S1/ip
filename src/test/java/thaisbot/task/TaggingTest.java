package thaisbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaggingTest {
    @Test
    public void taskTags_areDeduplicatedAndDisplayed() {
        Todo todo = new Todo("read book");
        todo.addTags(List.of("fun", "school", "fun"));

        assertEquals(List.of("fun", "school"), todo.getTags());
        assertEquals("[T][ ] read book #fun #school", todo.toString());
        assertEquals("T | 0 | read book | #fun | #school", todo.toFileString());
    }

    @Test
    public void deadlineAndEvent_displayTagsAtEnd() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 9, 10, 0, 0), false);
        deadline.addTags(List.of("school"));

        Event event = new Event("group study",
                LocalDateTime.of(2026, 9, 10, 9, 0), true,
                LocalDateTime.of(2026, 9, 10, 11, 0), true);
        event.addTags(List.of("project"));

        assertTrue(deadline.toString().endsWith("#school"));
        assertTrue(event.toString().endsWith("#project"));
    }

    @Test
    public void matchesSearch_searchesDescriptionsAndTags() {
        Todo todo = new Todo("read book");
        todo.addTags(List.of("fun"));

        assertTrue(todo.matchesSearch("book"));
        assertTrue(todo.matchesSearch("fun"));
        assertTrue(todo.matchesSearch("#fun"));
        assertFalse(todo.matchesSearch("math"));
    }
}

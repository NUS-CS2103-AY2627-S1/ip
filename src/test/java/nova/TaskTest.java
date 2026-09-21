package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void factoryMethods_newTasks_haveExpectedRepresentations() {
        Task todo = Task.todo("read book");
        Task deadline = Task.deadline(
                "return book", LocalDateTime.of(2026, 9, 18, 21, 30));
        Task event = Task.event("consultation", "2pm", "4pm");

        assertEquals("[T][ ] read book", todo.toDisplayString());
        assertEquals("TODO\t0\tread book\t", todo.toStorageString());
        assertTrue(deadline.toDisplayString().startsWith(
                "[D][ ] return book (by: "));
        assertTrue(deadline.toDisplayString().contains("18 2026 9:30"));
        assertEquals("DEADLINE\t0\treturn book\t2026-09-18T21:30",
                deadline.toStorageString());
        assertEquals("[E][ ] consultation (from: 2pm to: 4pm)",
                event.toDisplayString());
        assertEquals("EVENT\t0\tconsultation\t(from: 2pm to: 4pm)",
                event.toStorageString());
    }

    @Test
    public void fromStorageString_validTasks_preservesAllFields() {
        String[] storedTasks = {
            "TODO\t1\tread book\t",
            "DEADLINE\t1\treturn book\t2026-09-18T21:30",
            "EVENT\t1\tconsultation\t(from: 2pm to: 4pm)"
        };

        for (String storedTask : storedTasks) {
            assertEquals(storedTask,
                    Task.fromStorageString(storedTask).toStorageString());
        }
    }

    @Test
    public void fromStorageString_invalidTasks_throwsException() {
        String[] invalidTasks = {
            "",
            "TODO\t0\tmissing-extra-info",
            "TODO\t2\tinvalid status\t",
            "TODO\t0\tunexpected extra info\textra",
            "TODO\t0\t\t",
            "REMINDER\t0\tread book\t",
            "DEADLINE\t0\treturn book\ttomorrow"
        };

        for (String invalidTask : invalidTasks) {
            assertThrows(RuntimeException.class, () -> Task.fromStorageString(invalidTask));
        }
    }

    @Test
    public void markDone_incompleteTask_updatesStatusAndStorage() {
        Task task = Task.todo("read book");

        task.markDone();

        assertEquals("[T][X] read book", task.toDisplayString());
        assertEquals("TODO\t1\tread book\t", task.toStorageString());
    }

    @Test
    public void factoryMethods_invalidText_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Task.todo("  "));
        assertThrows(IllegalArgumentException.class, () -> Task.todo("first\nsecond"));
        assertThrows(IllegalArgumentException.class, () -> Task.deadline("return book", null));
        assertThrows(IllegalArgumentException.class, () -> Task.event("consultation", "", "4pm"));
    }

    @Test
    public void containsKeyword_variedKeywords_matchesCaseSensitively() {
        Task task = Task.todo("Read a storybook");

        assertTrue(task.containsKeyword("story"));
        assertTrue(task.containsKeyword(""));
        assertFalse(task.containsKeyword("Story"));
        assertFalse(task.containsKeyword("novel"));
    }
}

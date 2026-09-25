package goat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests internal assumptions enforced by task assertions.
 */
public class TaskTest {
    @Test
    public void constructor_blankDescription_assertionError() {
        assertThrows(AssertionError.class, () -> new Todo(" "));
    }

    @Test
    public void completionChanges_updateDisplayAndFileFormats() {
        Task task = new Deadline("submit report", LocalDate.of(2026, 9, 30));

        task.markAsDone();
        assertEquals("[D][X] submit report (by: Sep 30 2026)", task.toString());
        assertEquals("D | 1 | submit report | 2026-09-30", task.toFileString());

        task.markAsNotDone();
        assertEquals("[D][ ] submit report (by: Sep 30 2026)", task.toString());
    }

    @Test
    public void toFileString_specialCharacters_escapesFieldDelimiters() {
        Todo todo = new Todo("read A|B \\ notes");

        assertEquals("T | 0 | read A\\|B \\\\ notes", todo.toFileString());
    }
}

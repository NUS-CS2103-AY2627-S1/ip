package noms.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the save-file encoding logic in {@link Task}: {@link Task#escape},
 * {@link Task#unescape}, and the {@code toFileFormat} representations of each
 * task type.
 *
 * This logic is high value because it guards data integrity: a bug in the
 * escaping rules can corrupt or truncate saved tasks (for example when a
 * description contains a {@code |} or {@code \}), and the damage would only
 * surface on the next load. The tests pin down the escaping rules, the
 * round trip, the malformed-input failure, and the exact on-disk format.
 */
public class TaskTest {

    // --- escape ---

    @Test
    public void escape_plainText_unchanged() {
        assertEquals("read book", Task.escape("read book"));
    }

    @Test
    public void escape_pipe_isBackslashEscaped() {
        assertEquals("buy milk \\| bread", Task.escape("buy milk | bread"));
    }

    @Test
    public void escape_backslash_isDoubled() {
        assertEquals("a\\\\b", Task.escape("a\\b"));
    }

    @Test
    public void escape_backslashThenPipe_escapesBackslashFirst() {
        // Input \|  must become \\\|  (doubled backslash, then escaped pipe),
        // never \\|  which would read back as an escaped backslash + separator.
        assertEquals("\\\\\\|", Task.escape("\\|"));
    }

    // --- unescape ---

    @Test
    public void unescape_plainText_unchanged() {
        assertEquals("read book", Task.unescape("read book"));
    }

    @Test
    public void unescape_reversesEscape_forTrickyText() {
        String original = "C:\\logs\\a\\|b and | pipes";
        assertEquals(original, Task.unescape(Task.escape(original)));
    }

    @Test
    public void unescape_trailingBackslash_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Task.unescape("abc\\"));
    }

    @Test
    public void unescape_backslashBeforeNormalChar_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Task.unescape("a\\b"));
    }

    // --- toFileFormat ---

    @Test
    public void toFileFormat_incompleteTodo_hasZeroFlag() {
        assertEquals("T | 0 | read book", new ToDo("read book").toFileFormat());
    }

    @Test
    public void toFileFormat_completedTodo_hasOneFlag() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void toFileFormat_deadline_includesEscapedDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        assertEquals("D | 0 | return book | 2019-06-06", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_event_includesBothDates() {
        Event event = new Event("meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        assertEquals("E | 0 | meeting | 2019-08-06 | 2019-08-07", event.toFileFormat());
    }

    @Test
    public void toFileFormat_descriptionWithPipe_isEscaped() {
        // The user's own | must be escaped so it is not read back as a field
        // separator on the next load.
        assertEquals("T | 0 | a \\| b", new ToDo("a | b").toFileFormat());
    }
}

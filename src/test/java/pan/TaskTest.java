package pan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Task#applyUpdate(String, String)} and the overrides of
 * it in {@link Todo}, {@link Deadline} and {@link Event} - the logic behind the
 * {@code update} command.
 *
 * <p>All four live in one test class because {@code applyUpdate} is a single
 * polymorphic operation declared on {@link Task}: what is worth testing is which
 * option each kind of task accepts, which only makes sense read side by side.
 *
 * <p>Assertions are on {@link Task#toFileString()} rather than
 * {@code toString()}, matching {@link ParserTest}: {@code toFileString()} writes
 * dates in fixed ISO-8601 form, so the tests do not depend on the machine's
 * locale. Failure-path tests only check that a {@link PanException} is thrown,
 * not its message text.
 */
public class TaskTest {

    private static final LocalDateTime TWO_PM = LocalDateTime.of(2019, 12, 1, 14, 0);
    private static final LocalDateTime FOUR_PM = LocalDateTime.of(2019, 12, 1, 16, 0);

    // ---------- the option each kind of task accepts ----------

    @Test
    public void applyUpdate_descOnTodo_changesDescription() throws PanException {
        Todo todo = new Todo("read book");

        todo.applyUpdate("desc", "read a long book");

        assertEquals("T | 0 | read a long book", todo.toFileString());
    }

    @Test
    public void applyUpdate_byOnDeadline_changesDate() throws PanException {
        Deadline deadline = new Deadline("return book", TWO_PM);

        deadline.applyUpdate("by", "2019-12-02 1000");

        assertEquals("D | 0 | return book | 2019-12-02T10:00", deadline.toFileString());
    }

    @Test
    public void applyUpdate_descOnDeadline_changesDescriptionOnly() throws PanException {
        Deadline deadline = new Deadline("return book", TWO_PM);

        deadline.applyUpdate("desc", "return the library book");

        assertEquals("D | 0 | return the library book | 2019-12-01T14:00", deadline.toFileString());
    }

    // ---------- the headline requirement: change one detail, keep the rest ----------

    @Test
    public void applyUpdate_toOnEvent_leavesStartUnchanged() throws PanException {
        Event event = new Event("project meeting", TWO_PM, FOUR_PM);

        event.applyUpdate("to", "2019-12-01 1900");

        assertEquals("E | 0 | project meeting | 2019-12-01T14:00 | 2019-12-01T19:00",
                event.toFileString());
    }

    @Test
    public void applyUpdate_fromOnEvent_leavesEndUnchanged() throws PanException {
        Event event = new Event("project meeting", TWO_PM, FOUR_PM);

        event.applyUpdate("from", "2019-12-01 1300");

        assertEquals("E | 0 | project meeting | 2019-12-01T13:00 | 2019-12-01T16:00",
                event.toFileString());
    }

    @Test
    public void applyUpdate_taskAlreadyDone_staysDone() throws PanException {
        Event event = new Event("project meeting", TWO_PM, FOUR_PM);
        event.markAsDone();

        event.applyUpdate("to", "2019-12-01 1900");

        assertEquals("E | 1 | project meeting | 2019-12-01T14:00 | 2019-12-01T19:00",
                event.toFileString());
    }

    // ---------- options that do not apply ----------

    @Test
    public void applyUpdate_byOnTodo_exceptionThrown() {
        Todo todo = new Todo("read book");

        assertThrows(PanException.class, () -> todo.applyUpdate("by", "2019-12-01 1800"));
    }

    @Test
    public void applyUpdate_fromOnDeadline_exceptionThrown() {
        Deadline deadline = new Deadline("return book", TWO_PM);

        assertThrows(PanException.class, () -> deadline.applyUpdate("from", "2019-12-01 1400"));
    }

    @Test
    public void applyUpdate_byOnEvent_exceptionThrown() {
        Event event = new Event("project meeting", TWO_PM, FOUR_PM);

        assertThrows(PanException.class, () -> event.applyUpdate("by", "2019-12-01 1800"));
    }

    @Test
    public void applyUpdate_unknownOption_exceptionThrown() {
        Todo todo = new Todo("read book");

        assertThrows(PanException.class, () -> todo.applyUpdate("colour", "red"));
    }

    // ---------- an event may not be made to end before it starts ----------

    @Test
    public void applyUpdate_toBeforeStart_exceptionThrown() {
        Event event = new Event("project meeting", TWO_PM, FOUR_PM);

        assertThrows(PanException.class, () -> event.applyUpdate("to", "2019-12-01 1300"));
    }

    @Test
    public void applyUpdate_toBeforeStart_leavesEventUnchanged() {
        Event event = new Event("project meeting", TWO_PM, FOUR_PM);

        assertThrows(PanException.class, () -> event.applyUpdate("to", "2019-12-01 1300"));

        assertEquals("E | 0 | project meeting | 2019-12-01T14:00 | 2019-12-01T16:00",
                event.toFileString());
    }

    @Test
    public void applyUpdate_fromAfterEnd_exceptionThrown() {
        Event event = new Event("project meeting", TWO_PM, FOUR_PM);

        assertThrows(PanException.class, () -> event.applyUpdate("from", "2019-12-01 1700"));
    }

    @Test
    public void applyUpdate_fromEqualsEnd_isAccepted() throws PanException {
        Event event = new Event("project meeting", TWO_PM, FOUR_PM);

        event.applyUpdate("from", "2019-12-01 1600");

        assertEquals("E | 0 | project meeting | 2019-12-01T16:00 | 2019-12-01T16:00",
                event.toFileString());
    }

    // ---------- a rejected update must not change anything ----------

    @Test
    public void applyUpdate_unreadableDate_exceptionThrown() {
        Deadline deadline = new Deadline("return book", TWO_PM);

        assertThrows(PanException.class, () -> deadline.applyUpdate("by", "tomorrow"));
    }

    @Test
    public void applyUpdate_unreadableDate_leavesTaskUnchanged() {
        Event event = new Event("project meeting", TWO_PM, FOUR_PM);

        assertThrows(PanException.class, () -> event.applyUpdate("to", "someday"));

        assertEquals("E | 0 | project meeting | 2019-12-01T14:00 | 2019-12-01T16:00",
                event.toFileString());
    }
}

package kaya.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import kaya.exception.KayaException;
import kaya.task.Deadline;
import kaya.task.Event;
import kaya.task.Task;
import kaya.task.Todo;

/**
 * Checks replacement tasks without relying on the parser or storage layer.
 */
public class UpdateCommandTest {
    @Test
    public void applyTo_descriptionUpdate_preservesOriginalTaskAndCompletion() throws KayaException {
        Deadline original = new Deadline("old description", LocalDate.of(2026, 9, 20));
        original.markAsDone();

        Deadline replacement = assertInstanceOf(Deadline.class,
                new UpdateCommand(0, "/description", "new description").applyTo(original));

        assertNotSame(original, replacement);
        assertEquals("old description", original.getDescription());
        assertEquals("new description", replacement.getDescription());
        assertEquals(LocalDate.of(2026, 9, 20), replacement.getBy());
        assertTrue(replacement.isDone());
        replacement.markAsNotDone();
        assertTrue(original.isDone());
    }

    @Test
    public void applyTo_eventDateUpdate_preservesOtherEndpointAndAllowsSameDay() throws KayaException {
        Event original = new Event("meeting", LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 21));

        Event replacement = assertInstanceOf(Event.class,
                new UpdateCommand(0, "/to", "2026-09-20").applyTo(original));

        assertEquals(LocalDate.of(2026, 9, 20), replacement.getFrom());
        assertEquals(LocalDate.of(2026, 9, 20), replacement.getTo());
        assertEquals(LocalDate.of(2026, 9, 21), original.getTo());
        assertFalse(replacement.isDone());
    }

    @Test
    public void applyTo_invalidDirectUpdates_rejectsUnsupportedOrBlankValues() {
        Todo original = new Todo("unchanged");

        assertThrows(KayaException.class, () -> new UpdateCommand(0, "/description", " \t ").applyTo(original));
        assertThrows(KayaException.class, () -> new UpdateCommand(0, "/description", "new").applyTo(new Task("base")));
        assertThrows(KayaException.class, () -> new UpdateCommand(0, "/by", "2026-09-20").applyTo(original));
        assertEquals("unchanged", original.getDescription());
        assertFalse(original.isDone());
    }
}

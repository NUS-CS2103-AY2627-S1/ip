package am.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;

import am.command.InvalidCommandException;

/** Verifies partial edits preserve untouched details and reject invalid results. */
public class TaskEditTest {
    @Test
    void changesOnlyTheEndTimeOfACompletedEvent() {
        EventTask original = new EventTask("meeting", "2026-09-10 1400", "2026-09-10 1600");
        original.mark();
        Task edited = new TaskEdit(Map.of("/to", "1800")).applyTo(original);

        assertEquals("E | 1 | meeting | 2026-09-10T14:00 | 2026-09-10T18:00", edited.toSerialized());
        assertEquals("E | 1 | meeting | 2026-09-10T14:00 | 2026-09-10T16:00", original.toSerialized());
        assertNotSame(original, edited);
    }

    @Test
    void keepsTheEndpointDateAndUntouchedTimestampPrecision() {
        EventTask original = new EventTask("overnight", LocalDateTime.parse("2026-09-10T23:00:30"),
                LocalDateTime.parse("2026-09-11T02:00:45"));
        Task edited = new TaskEdit(Map.of("/to", "0100")).applyTo(original);

        assertEquals("E | 0 | overnight | 2026-09-10T23:00:30 | 2026-09-11T01:00", edited.toSerialized());
    }

    @Test
    void appliesDateOnlyDefaultsAndValidatesBothEndpointsTogether() {
        EventTask original = new EventTask("meeting", "2026-09-10 1400", "2026-09-10 1600");
        Task edited = new TaskEdit(Map.of("/from", "2026-09-12", "/to", "2026-09-12")).applyTo(original);
        assertEquals("E | 0 | meeting | 2026-09-12T00:00 | 2026-09-12T23:59", edited.toSerialized());

        Task equalEndpoints = new TaskEdit(Map.of("/from", "2000-01-01 1500", "/to", "2000-01-01 1500"))
                .applyTo(original);
        assertEquals("E | 0 | meeting | 2000-01-01T15:00 | 2000-01-01T15:00", equalEndpoints.toSerialized());
    }

    @Test
    void editsTodoNamesAndDeadlineFieldsWithoutResettingCompletion() {
        TodoTask todo = new TodoTask("old");
        todo.mark();
        assertEquals("T | 1 | new", new TaskEdit(Map.of("/name", "new")).applyTo(todo).toSerialized());
        DeadlineTask deadline = new DeadlineTask("report", "2026-09-10 1400");
        assertEquals("D | 0 | renamed | 2026-09-11T23:59",
                new TaskEdit(Map.of("/name", "renamed", "/by", "2026-09-11")).applyTo(deadline).toSerialized());
        assertEquals("D | 0 | report | 2026-09-10T09:00",
                new TaskEdit(Map.of("/by", "0900")).applyTo(deadline).toSerialized());
    }

    @Test
    void rejectsInvalidFieldsDescriptionsAndDatesWithoutMutation() {
        EventTask original = new EventTask("meeting", "2026-09-10 1400", "2026-09-10 1600");
        String before = original.toSerialized();
        for (String value : new String[]{"1300", "900", "2400", "2026-02-30", "tomorrow"}) {
            TaskEdit edit = new TaskEdit(Map.of("/to", value));
            InvalidCommandException exception = assertThrows(
                    InvalidCommandException.class, () -> edit.applyTo(original));
            assertEquals("You messed up the command.\n"
                    + "Example: edit 1 /from 2026-09-12 1400 /to 2026-09-12 1600", exception.getMessage());
        }
        for (String value : new String[]{"", " ", "a|b", "a\nb", "a\rb"}) {
            TaskEdit edit = new TaskEdit(Map.of("/name", value));
            InvalidCommandException exception = assertThrows(
                    InvalidCommandException.class, () -> edit.applyTo(original));
            assertEquals("You messed up the command.\nExample: edit 1 /name buy milk", exception.getMessage());
        }
        assertThrows(InvalidCommandException.class, () -> new TaskEdit(Map.of("/by", "1800")).applyTo(original));
        assertThrows(InvalidCommandException.class, () -> new TaskEdit(Map.of()).applyTo(original));
        assertEquals(before, original.toSerialized());
    }

    @Test
    void replacesOneTaskInPlaceAndReturnsTheOriginal() {
        TaskList tasks = new TaskList();
        TodoTask first = new TodoTask("first");
        TodoTask second = new TodoTask("second");
        TodoTask third = new TodoTask("third");
        tasks.addTask(first);
        tasks.addTask(second);
        tasks.addTask(third);
        Task replacement = new TaskEdit(Map.of("/name", "changed")).applyTo(second);
        Task previous = tasks.replaceTask(1, replacement);

        assertEquals(3, tasks.getLength());
        assertSame(first, tasks.getTask(0));
        assertSame(replacement, tasks.getTask(1));
        assertSame(third, tasks.getTask(2));
        assertSame(second, previous);

        tasks.replaceTask(1, previous);
        assertSame(second, tasks.getTask(1));
    }
}

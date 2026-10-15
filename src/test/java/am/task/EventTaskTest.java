package am.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import am.storage.CorruptedDataException;

/** Tests serialization and deserialization of {@link EventTask}. */
public class EventTaskTest {
    @Test
    void serializesAndDeserializesEventTask() throws CorruptedDataException {
        EventTask original = new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 28, 14, 0),
                LocalDateTime.of(2026, 8, 28, 16, 0));
        original.mark();

        String serialized = original.toSerialized();
        assertEquals("E | 1 | project meeting | 2026-08-28T14:00 | 2026-08-28T16:00", serialized);

        EventTask restored = assertInstanceOf(EventTask.class, Task.fromSerialized(serialized));
        assertEquals(original.toString(), restored.toString());
        assertEquals(serialized, restored.toSerialized());
    }
}

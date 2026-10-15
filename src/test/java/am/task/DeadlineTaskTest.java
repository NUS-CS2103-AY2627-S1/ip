package am.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import am.storage.CorruptedDataException;

/** Tests serialization and deserialization of {@link DeadlineTask}. */
public class DeadlineTaskTest {
    @Test
    void serializesAndDeserializesDeadlineTask() throws CorruptedDataException {
        DeadlineTask original = new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 8, 28, 23, 59));

        String serialized = original.toSerialized();
        assertEquals("D | 0 | submit report | 2026-08-28T23:59", serialized);

        DeadlineTask restored = assertInstanceOf(
                DeadlineTask.class, Task.fromSerialized(serialized));
        assertEquals(original.toString(), restored.toString());
        assertEquals(serialized, restored.toSerialized());
    }
}

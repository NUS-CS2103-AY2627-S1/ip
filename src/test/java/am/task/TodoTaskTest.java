package am.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import am.storage.CorruptedDataException;

/** Tests serialization and deserialization of {@link TodoTask}. */
public class TodoTaskTest {
    @Test
    void serializesAndDeserializesTodoTask() throws CorruptedDataException {
        TodoTask original = new TodoTask("buy bread");
        original.mark();

        String serialized = original.toSerialized();
        assertEquals("T | 1 | buy bread", serialized);

        TodoTask restored = assertInstanceOf(TodoTask.class, Task.fromSerialized(serialized));
        assertEquals(original.toString(), restored.toString());
        assertEquals(serialized, restored.toSerialized());
    }
}

package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import exception.FriedbergInternalException;

/**
 * Tests validation of the common fields required by every serialized task.
 */
class TaskTest {
    @Test
    void deserialize_missingCommonFields_throwsInternalException() {
        String[] records = {"", "name", "name,P", "name,P,"};
        String expectedMessage = new FriedbergInternalException(
                "Expected serialized task to contain name, status, and type").getMessage();

        for (String record : records) {
            FriedbergInternalException error = assertThrows(
                    FriedbergInternalException.class, () -> Task.deserialize(record), record);
            assertEquals(expectedMessage, error.getMessage(), record);
        }
    }
}

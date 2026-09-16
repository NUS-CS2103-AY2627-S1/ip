package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests priority parsing and task representations. */
public class PriorityTest {

    /** Verifies that names are case-insensitive and numeric aliases are supported. */
    @Test
    public void fromUserInput_supportedNamesAndAliases_correctPriorityReturned()
            throws BobbyException {
        assertEquals(Priority.HIGH, Priority.fromUserInput("high"));
        assertEquals(Priority.HIGH, Priority.fromUserInput("HIGH"));
        assertEquals(Priority.HIGH, Priority.fromUserInput("1"));
        assertEquals(Priority.MEDIUM, Priority.fromUserInput("medium"));
        assertEquals(Priority.MEDIUM, Priority.fromUserInput("2"));
        assertEquals(Priority.LOW, Priority.fromUserInput("Low"));
        assertEquals(Priority.LOW, Priority.fromUserInput("3"));
        assertEquals(Priority.NONE, Priority.fromUserInput("none"));
    }

    /** Verifies that unsupported priority values produce a helpful error. */
    @Test
    public void fromUserInput_unsupportedValue_exceptionThrown() {
        BobbyException exception = assertThrows(BobbyException.class, () ->
                Priority.fromUserInput("urgent"));

        assertEquals("Error! The priority level must be high, medium, low, none, 1, 2, or 3.",
                exception.getMessage());
    }

    /** Verifies that tasks default to no priority and omit the priority badge. */
    @Test
    public void task_newTask_noPriorityUsed() {
        Task task = new ToDo("read book");

        assertEquals(Priority.NONE, task.getPriority());
        assertEquals("[T][ ] read book", task.toString());
        assertEquals("T | 0 | read book | NONE", task.toStorageString());
    }

    /** Verifies that an assigned priority is displayed, stored, and retained when marked. */
    @Test
    public void task_assignedPriority_priorityDisplayedStoredAndRetained() {
        Task task = new ToDo("read book");
        task.setPriority(Priority.HIGH);
        task.markAsDone();

        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals("[T][X][P: HIGH] read book", task.toString());
        assertEquals("T | 1 | read book | HIGH", task.toStorageString());

        task.unmarkAsDone();
        assertEquals(Priority.HIGH, task.getPriority());
    }
}

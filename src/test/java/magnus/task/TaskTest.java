package magnus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the state changes, display format, and storage format of {@link Task}.
 */
public class TaskTest {

    @Test
    public void constructor_validDescription_createsIncompleteTask() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
    }

    @Test
    public void constructor_multilineDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("first line\nsecond line"));
    }

    @Test
    public void constructor_surroundingWhitespace_stripsDescription() {
        Task task = new Task("  read book  ");

        assertEquals("read book", task.getDescription());
    }

    @Test
    public void markAsDone_incompleteTask_taskIsCompleted() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("[X] read book", task.toString());
        assertEquals("T,1,read book", task.serialize());
    }

    @Test
    public void markAsUndone_completedTask_taskIsIncomplete() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsUndone();

        assertFalse(task.isDone());
        assertEquals("[ ] read book", task.toString());
        assertEquals("T,0,read book", task.serialize());
    }

    @Test
    public void serialize_simpleDescription_returnsUnquotedData() {
        Task task = new Task("read book");

        assertEquals("T,0,read book", task.serialize());
    }

    @Test
    public void serialize_descriptionWithComma_quotesDescription() {
        Task task = new Task("buy milk, eggs");

        assertEquals("T,0,\"buy milk, eggs\"", task.serialize());
    }

    @Test
    public void serialize_descriptionWithQuotes_escapesAndQuotesDescription() {
        Task task = new Task("read \"Dune\"");

        assertEquals("T,0,\"read \"\"Dune\"\"\"", task.serialize());
    }

    @Test
    public void encodeDataField_nullOrMultilineField_rejectsInvalidStorageData() {
        TestableTask task = new TestableTask("read book");

        assertThrows(NullPointerException.class, () -> task.encodeField(null));
        assertThrows(IllegalArgumentException.class, () -> task.encodeField("first\nsecond"));
        assertThrows(IllegalArgumentException.class, () -> task.encodeField("first\rsecond"));
    }

    @Test
    public void constructor_descriptionWithCarriageReturn_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("first line\rsecond line"));
    }

    @Test
    public void toString_incompleteTask_returnsIncompleteStatusAndDescription() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_completedTask_returnsCompletedStatusAndDescription() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("[X] read book", task.toString());
    }

    /**
     * Exposes protected storage encoding so its defensive validation can be unit tested.
     */
    private static final class TestableTask extends Task {
        private TestableTask(String description) {
            super(description);
        }

        private String encodeField(String field) {
            return encodeDataField(field);
        }
    }
}

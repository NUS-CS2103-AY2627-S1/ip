package jasper.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import jasper.JasperException;
import jasper.task.Deadline;

public class DeadlineSerializerTest {
    private final LocalDateTime testDate = LocalDateTime.of(2023, 10, 31, 23, 59);

    @Test
    public void serialize_uncompletedDeadline_correctFormat() {
        Deadline deadline = new Deadline("return book", testDate);
        assertEquals("D | 0 | return book | 2023-10-31T23:59", DeadlineSerializer.serialize(deadline));
    }

    @Test
    public void deserialize_validCompletedString_markedDeadlineReturned() throws JasperException {
        Deadline deadline = DeadlineSerializer.deserialize("D | 1 | return book | 2023-10-31T23:59");
        assertEquals("return book", deadline.getDescription());
        assertTrue(deadline.isDone());
        assertEquals(testDate.toString(), deadline.getBy());
    }

    @Test
    public void deserialize_invalidDateFormat_exceptionThrown() {
        assertThrows(JasperException.class, () ->
                DeadlineSerializer.deserialize("D | 0 | return book | invalid-date")
        );
    }

    @Test
    public void deserialize_missingParts_exceptionThrown() {
        assertThrows(JasperException.class, () ->
            DeadlineSerializer.deserialize("D | 0 | return book")
        );
    }
}

package janet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    public void createTask_emptyLabel_exceptionThrown() {
        assertThrows(JanetException.class, () -> new Todo(false, ""));
    }

    @Test
    public void compareTaskLabel_labelsCompared_alphabeticalOrderReturned() throws JanetException {
        Task first = new Todo(false, "Buy milk");
        Task second = new Todo(false, "Clean room");

        assertTrue(first.compareTaskLabel(second) < 0);
        assertTrue(second.compareTaskLabel(first) > 0);
        assertEquals(0, first.compareTaskLabel(new Todo(true, "Buy milk")));
    }

    @Test
    public void toFileFormat_taskStatusAndLabelProvided_expectedFormatReturned() throws JanetException {
        assertEquals("T|0|Read book", new Todo(false, "Read book").toFileFormat());
        assertEquals("T|1|Read book", new Todo(true, "Read book").toFileFormat());
    }

    @Test
    public void equals_sameTaskTypeAndLabel_trueReturned() throws JanetException {
        Task incomplete = new Todo(false, "Read book");
        Task complete = new Todo(true, "Read book");
        Task differentLabel = new Todo(false, "Write book");

        assertEquals(incomplete, complete);
        assertNotEquals(incomplete, differentLabel);
        assertNotEquals(incomplete, "Read book");
    }

    @Test
    public void toString_taskStatusAndLabelProvided_expectedFormatReturned() throws JanetException {
        assertEquals("[T][ ] Read book", new Todo(false, "Read book").toString());
        assertEquals("[T][X] Read book", new Todo(true, "Read book").toString());
    }
}

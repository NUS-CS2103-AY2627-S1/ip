package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTypeTest {
    @Test
    public void todo_toString_hasTodoTag() {
        Todo todo = new Todo("borrow book");
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    public void deadline_toString_formatsDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void event_toString_formatsDates() {
        Event event = new Event("project meeting", LocalDate.of(2026, 8, 28), LocalDate.of(2026, 8, 29));
        assertEquals("[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)", event.toString());
    }
}

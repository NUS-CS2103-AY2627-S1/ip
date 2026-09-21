package sel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void getDdl_returnsExactValuePassedIn() {
        LocalDateTime ddl = LocalDateTime.of(2019, 12, 2, 18, 0);
        Deadline deadline = new Deadline("return book", ddl);
        assertEquals(ddl, deadline.getDdl());
    }

    @Test
    public void toString_notDone_formatsDateCorrectly() {
        LocalDateTime ddl = LocalDateTime.of(2019, 12, 2, 18, 0);
        Deadline deadline = new Deadline("return book", ddl);
        assertEquals("[D][ ] return book(by:Dec 2 2019, 6:00PM)", deadline.toString());
    }

    @Test
    public void toString_marked_showsXInStatusBox() {
        LocalDateTime ddl = LocalDateTime.of(2019, 12, 2, 18, 0);
        Deadline deadline = new Deadline("return book", ddl);
        deadline.mark();
        assertEquals("[D][X] return book(by:Dec 2 2019, 6:00PM)", deadline.toString());
    }

    @Test
    public void toString_morningTime_usesAmSuffix() {
        LocalDateTime ddl = LocalDateTime.of(2019, 12, 2, 9, 30);
        Deadline deadline = new Deadline("submit form", ddl);
        assertEquals("[D][ ] submit form(by:Dec 2 2019, 9:30AM)", deadline.toString());
    }

    @Test
    public void toString_midnight_formatsAsTwelveAm() {
        LocalDateTime ddl = LocalDateTime.of(2019, 12, 2, 0, 0);
        Deadline deadline = new Deadline("renew passport", ddl);
        assertEquals("[D][ ] renew passport(by:Dec 2 2019, 12:00AM)", deadline.toString());
    }

    @Test
    public void hasSameDetailsAs_needsSameDescriptionAndDueDate() {
        LocalDateTime due = LocalDateTime.of(2019, 12, 2, 18, 0);
        LocalDateTime otherDue = LocalDateTime.of(2019, 12, 3, 18, 0);
        Deadline deadline = new Deadline("return book", due);

        assertTrue(deadline.hasSameDetailsAs(new Deadline("return book", due)));
        assertTrue(deadline.hasSameDetailsAs(new Deadline("RETURN BOOK", due)));
        assertFalse(deadline.hasSameDetailsAs(new Deadline("return book", otherDue)));
        assertFalse(deadline.hasSameDetailsAs(new ToDo("return book")));
    }
}

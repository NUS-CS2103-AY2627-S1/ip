package nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests deadline date formatting. */
class DeadlineTest {
    @Test
    void toString_isoDate_displaysReadableDate() {
        Deadline deadline = new Deadline("return book", "2019-12-02");

        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }
}

package benji;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void deadlineToString_formatsDateCorrectly() {
        Deadline task = new Deadline("submit report", "2026-09-14");

        assertEquals("[D][ ] submit report (by: Sep 14 2026)", task.toString());
    }
}

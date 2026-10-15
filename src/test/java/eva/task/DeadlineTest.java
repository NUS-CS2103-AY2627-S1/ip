package eva.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    void toString_validDate_formatsDate() {
        Deadline deadline =
                new Deadline("submit project", "2026-08-29");

        assertEquals(
                "[D][ ] submit project (by: Aug 29 2026)",
                deadline.toString());
    }

    @Test
    void toFileString_validDate_usesIsoDate() {
        Deadline deadline =
                new Deadline("submit project", "2026-08-29");
        deadline.markAsDone();

        assertEquals(
                "D | 1 | submit project | 2026-08-29",
                deadline.toFileString());
    }

    @Test
    void constructor_invalidDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("submit project", "29-08-2026"));
    }
}

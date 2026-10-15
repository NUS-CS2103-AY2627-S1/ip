package janet;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void eventWithStartDateAfterEndDate_exceptionThrown() {
        assertThrows(JanetException.class, () -> new Event(
                false,
                "Invalid event",
                LocalDate.of(2026, 9, 13),
                LocalDate.of(2026, 9, 12)
        ));
    }
}

package es;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EventTest {
    @Test
    void event_rejectsEndBeforeStart() {
        String start = "2026-08-28 10:00";
        String end = "2026-08-28 09:00";
        assertThrows(EsException.class, () -> new Event("meeting", start, end));
    }

    @Test
    void event_rejectsEqualStartAndEnd() {
        String date = "2026-08-28";
        assertThrows(EsException.class, () -> new Event("meeting", date, date));
    }
}

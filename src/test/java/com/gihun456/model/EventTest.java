package com.gihun456.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.gihun456.ErrorMessages;
import com.gihun456.GihunException;

public class EventTest {
    @Test
    public void constructor_dateText_parsesBothDates() throws GihunException {
        Event event = new Event("Team meeting", "15/09/2026 1000", "15/09/2026 1100");

        assertEquals(LocalDateTime.of(2026, 9, 15, 10, 0), event.getStartDate());
        assertEquals(LocalDateTime.of(2026, 9, 15, 11, 0), event.getEndDate());
    }

    @Test
    public void constructor_blankDate_throwsExpectedError() {
        GihunException exception = assertThrows(GihunException.class, () ->
                new Event("Meeting", " ", "15/09/2026 1100"));

        assertEquals(ErrorMessages.EVENT_DATE_EMPTY, exception.getMessage());
    }

    @Test
    public void constructor_invalidDate_throwsExpectedError() {
        GihunException exception = assertThrows(GihunException.class, () ->
                new Event("Meeting", "tomorrow", "15/09/2026 1100"));

        assertEquals(ErrorMessages.INVALID_EVENT_DATE_FORMAT, exception.getMessage());
    }

    @Test
    public void constructor_endBeforeStart_throwsExpectedError() {
        GihunException exception = assertThrows(GihunException.class, () ->
                new Event("Meeting", "15/09/2026 1100", "15/09/2026 1000"));

        assertEquals(ErrorMessages.EVENT_END_BEFORE_START, exception.getMessage());
    }

    @Test
    public void toString_pendingAndCompletedEvent_formatsStatusAndDates() {
        Event event = new Event("Team meeting",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 11, 0));

        assertEquals("[E][ ] Team meeting (from: Sep 15 2026, 10:00 AM to: Sep 15 2026, 11:00 AM)",
                event.toString());
        event.markAsDone();
        assertEquals("[E][X] Team meeting (from: Sep 15 2026, 10:00 AM to: Sep 15 2026, 11:00 AM)",
                event.toString());
    }
}

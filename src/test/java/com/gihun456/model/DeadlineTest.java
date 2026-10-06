package com.gihun456.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.gihun456.ErrorMessages;
import com.gihun456.GihunException;

public class DeadlineTest {
    @Test
    public void constructor_dateText_parsesDate() throws GihunException {
        Deadline deadline = new Deadline("Submit report", "15/09/2026 1030");

        assertEquals(LocalDateTime.of(2026, 9, 15, 10, 30), deadline.getDueDate());
    }

    @Test
    public void constructor_blankDate_throwsExpectedError() {
        GihunException exception = assertThrows(GihunException.class, () ->
                new Deadline("Submit report", "   "));

        assertEquals(ErrorMessages.DEADLINE_DATE_EMPTY, exception.getMessage());
    }

    @Test
    public void constructor_invalidDate_throwsExpectedError() {
        GihunException exception = assertThrows(GihunException.class, () ->
                new Deadline("Submit report", "tomorrow"));

        assertEquals(ErrorMessages.INVALID_DEADLINE_FORMAT, exception.getMessage());
    }

    @Test
    public void toString_pendingAndCompletedDeadline_formatsStatusAndDate() {
        Deadline deadline = new Deadline("Submit report", LocalDateTime.of(2026, 9, 15, 10, 30));

        assertEquals("[D][ ] Submit report (by: Sep 15 2026, 10:30 AM)", deadline.toString());
        deadline.markAsDone();
        assertEquals("[D][X] Submit report (by: Sep 15 2026, 10:30 AM)", deadline.toString());
    }
}

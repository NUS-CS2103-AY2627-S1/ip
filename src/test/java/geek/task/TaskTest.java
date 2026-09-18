package geek.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import geek.exception.GeekException;

/**
 * Tests task state changes, date behavior, and persistence round trips.
 */
class TaskTest {
    @Test
    void todo_markAndUnmark_updatesStatusAndDisplay() {
        Task todo = Task.newTodo("read book");

        assertEquals("[T][ ] read book", todo.toString());

        todo.mark();
        assertAll(() -> assertEquals("X", todo.getStatus()), () -> assertEquals(
                        "[T][X] read book",
                        todo.toString()
                )
        );

        todo.unmark();
        assertAll(() -> assertEquals(" ", todo.getStatus()), () -> assertEquals(
                        "[T][ ] read book",
                        todo.toString()
                )
        );
    }

    @Test
    void deadline_dateOnly_displaysDateAndMatchesThatDate() {
        Task deadline = Task.newDeadline(
                "return book",
                "2/12/2019"
        );

        assertAll(() -> assertEquals(
                        "[D][ ] return book (by: Dec 2 2019)",
                        deadline.toString()
                ), () -> assertTrue(
                        deadline.occursOn(
                                LocalDate.of(2019, 12, 2)
                        )
                ), () -> assertFalse(
                        deadline.occursOn(
                                LocalDate.of(2019, 12, 3)
                        )
                )
        );
    }

    @Test
    void deadline_withTime_displaysDateAndTime() {
        Task deadline = Task.newDeadline(
                "submit report",
                "2/12/2019 1800"
        );

        assertEquals(
                "[D][ ] submit report (by: Dec 2 2019, 6:00 PM)",
                deadline.toString()
        );
    }

    @Test
    void event_spanningSeveralDates_matchesInclusiveDateRange() {
        Task event = Task.newEvent(
                "conference",
                "2/12/2019 2300",
                "4/12/2019 0100"
        );

        assertAll(() -> assertFalse(
                        event.occursOn(
                                LocalDate.of(2019, 12, 1)
                        )
                ), () -> assertTrue(
                        event.occursOn(
                                LocalDate.of(2019, 12, 2)
                        )
                ), () -> assertTrue(
                        event.occursOn(
                                LocalDate.of(2019, 12, 3)
                        )
                ), () -> assertTrue(
                        event.occursOn(
                                LocalDate.of(2019, 12, 4)
                        )
                ), () -> assertFalse(
                        event.occursOn(
                                LocalDate.of(2019, 12, 5)
                        )
                )
        );
    }

    @Test
    void newEvent_endNotAfterStart_throwsGeekException() {
        assertAll(() -> assertThrows(
                        GeekException.class, () -> Task.newEvent(
                                "meeting",
                                "2/12/2019 1800",
                                "2/12/2019 1800"
                        )
                ), () -> assertThrows(
                        GeekException.class, () -> Task.newEvent(
                                "meeting",
                                "2/12/2019 1800",
                                "2/12/2019 1700"
                        )
                )
        );
    }

    @Test
    void dataString_allTaskTypes_roundTripWithoutDataLoss() {
        Task todo = Task.newTodo("read book");
        todo.mark();
        Task deadline = Task.newDeadline(
                "submit report",
                "2/12/2019 1800"
        );
        Task event = Task.newEvent(
                "project meeting",
                "2/12/2019 1800",
                "2/12/2019 2000"
        );

        assertAll(() -> assertRoundTrip(todo), () -> assertRoundTrip(deadline), () -> assertRoundTrip(event)
        );
    }

    @Test
    void fromDataString_malformedData_throwsException() {
        assertAll(() -> assertThrows(
                        IllegalArgumentException.class, () -> Task.fromDataString("T\t0")
                ), () -> assertThrows(
                        IllegalArgumentException.class, () -> Task.fromDataString(
                                "T\t2\tcmVhZCBib29r"
                        )
                ), () -> assertThrows(
                        IllegalArgumentException.class, () -> Task.fromDataString(
                                "X\t0\tcmVhZCBib29r"
                        )
                )
        );
    }

    private static void assertRoundTrip(Task original) {
        Task restored = Task.fromDataString(
                original.toDataString()
        );

        assertAll(() -> assertEquals(
                        original.toDataString(),
                        restored.toDataString()
                ), () -> assertEquals(
                        original.toString(),
                        restored.toString()
                )
        );
    }
}

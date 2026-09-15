package kiwi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import kiwi.KiwiException;

/**
 * Unit tests for {@link Event}.
 */
class EventTest {

    private Event event;

    @BeforeEach
    void setUp() throws KiwiException {
        event = new Event("meeting",
                LocalDate.of(2019, 10, 4),
                LocalDate.of(2019, 10, 11));
    }

    @Test
    void constructor_endBeforeStart_throwsKiwiException() {
        KiwiException exception = assertThrows(KiwiException.class, () ->
                new Event("meeting", LocalDate.of(2019, 10, 11), LocalDate.of(2019, 10, 4)));
        assertEquals("Event end date cannot be before the start date.", exception.getMessage());
    }

    @Test
    void constructor_sameStartAndEnd_createsEvent() throws KiwiException {
        Event singleDay = new Event("standup",
                LocalDate.of(2019, 10, 4),
                LocalDate.of(2019, 10, 4));
        assertTrue(singleDay.occursOn(LocalDate.of(2019, 10, 4)));
    }

    @Test
    void hasSameDetails_sameRangeAndDescription_returnsTrue() throws KiwiException {
        Event other = new Event("meeting",
                LocalDate.of(2019, 10, 4),
                LocalDate.of(2019, 10, 11));
        assertTrue(event.hasSameDetails(other));
    }

    @Test
    void hasSameDetails_differentEndDate_returnsFalse() throws KiwiException {
        Event other = new Event("meeting",
                LocalDate.of(2019, 10, 4),
                LocalDate.of(2019, 10, 12));
        assertFalse(event.hasSameDetails(other));
    }

    @ParameterizedTest
    @MethodSource("occursOnCases")
    void occursOn_dateRelativeToRange_returnsExpectedMatch(LocalDate date, boolean expected) {
        assertEquals(expected, event.occursOn(date));
    }

    static Stream<Arguments> occursOnCases() {
        return Stream.of(
                Arguments.of(LocalDate.of(2019, 10, 3), false),
                Arguments.of(LocalDate.of(2019, 10, 4), true),
                Arguments.of(LocalDate.of(2019, 10, 7), true),
                Arguments.of(LocalDate.of(2019, 10, 11), true),
                Arguments.of(LocalDate.of(2019, 10, 12), false)
        );
    }

    @Test
    void toSaveFormat_includesIsoDates() {
        assertEquals("E | 0 | meeting | 2019-10-04 | 2019-10-11", event.toSaveFormat());
    }

    @Test
    void toSaveFormat_whenDone_usesDoneFlag() throws KiwiException {
        event.markAsDone();
        assertTrue(event.toSaveFormat().startsWith("E | 1 | "));
    }
}

package samantha.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import samantha.exception.InputException;

class DateTimeValueTest {
    @Test
    void parse_dateWithoutTime_returnsDateAndEmptyTime() throws InputException {
        DateTimeValue value = DateTimeValue.parse(" 2/12/2019 ");

        assertEquals(LocalDate.of(2019, 12, 2), value.getDate());
        assertTrue(value.getTime().isEmpty());
        assertEquals("Dec 2 2019", value.toString());
        assertEquals("2/12/2019", value.toStorageString());
    }

    @Test
    void parse_dashSeparatedDateWithTime_returnsFormattedValue() throws InputException {
        DateTimeValue value = DateTimeValue.parse("05-01-2020 1800");

        assertEquals(LocalDate.of(2020, 1, 5), value.getDate());
        assertEquals(LocalTime.of(18, 0), value.getTime().orElseThrow());
        assertEquals("Jan 5 2020, 6:00 PM", value.toString());
        assertEquals("5/1/2020 1800", value.toStorageString());
    }

    @Test
    void parse_leapDateAtMidnight_returnsValidValue() throws InputException {
        DateTimeValue value = DateTimeValue.parse("29/2/2024 0000");

        assertEquals("Feb 29 2024, 12:00 AM", value.toString());
    }

    @Test
    void parseDate_dateWithoutTime_returnsDate() throws InputException {
        assertEquals(LocalDate.of(2019, 12, 2), DateTimeValue.parseDate("2/12/2019"));
    }

    @Test
    void parseDate_timeSupplied_inputExceptionThrown() {
        assertThrows(InputException.class, () -> DateTimeValue.parseDate("2/12/2019 1800"));
    }

    @ParameterizedTest
    @MethodSource("invalidDateTimes")
    void parse_invalidDateTime_inputExceptionThrown(String input) {
        assertThrows(InputException.class, () -> DateTimeValue.parse(input));
    }

    private static Stream<Arguments> invalidDateTimes() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("2/12-2019"),
                Arguments.of("2/12/19"),
                Arguments.of("31/02/2019"),
                Arguments.of("2/12/2019 2400"),
                Arguments.of("2/12/2019 1260"),
                Arguments.of("next Tuesday"));
    }
}

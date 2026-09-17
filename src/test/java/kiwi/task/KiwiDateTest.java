package kiwi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import kiwi.KiwiException;

/**
 * Unit tests for {@link KiwiDate} parsing and formatting.
 */
class KiwiDateTest {

    private static final String PARSE_ERROR_MESSAGE =
            "Please use a date as yyyy-MM-dd, e.g. 2019-12-02";

    @Nested
    class Parse {

        @Test
        void parse_validIsoDate_returnsLocalDate() throws KiwiException {
            assertEquals(LocalDate.of(2019, 12, 2), KiwiDate.parse("2019-12-02"));
        }

        @Test
        void parse_dateWithSurroundingWhitespace_returnsTrimmedDate() throws KiwiException {
            assertEquals(LocalDate.of(2019, 10, 15), KiwiDate.parse("  2019-10-15  "));
        }

        @Test
        void parse_leapYearDate_returnsLocalDate() throws KiwiException {
            assertEquals(LocalDate.of(2020, 2, 29), KiwiDate.parse("2020-02-29"));
        }

        @Test
        void parse_yearZeroDate_returnsLocalDate() throws KiwiException {
            assertEquals(LocalDate.of(0, 1, 1), KiwiDate.parse("0000-01-01"));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "",
            "   ",
            "02-12-2019",
            "2019/12/02",
            "2019-12",
            "not-a-date",
            "-0005-03-01",
            "+10000-01-01",
            "10000-01-01",
            "+2019-12-02",
            "-2019-12-02"
        })
        void parse_invalidInput_throwsKiwiException(String input) {
            KiwiException exception = assertThrows(KiwiException.class, () -> KiwiDate.parse(input));
            assertEquals(PARSE_ERROR_MESSAGE, exception.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "2019-13-01",
            "2019-01-32",
            "2019-02-30",
            "2019-02-29"
        })
        void parse_impossibleCalendarDate_throwsKiwiException(String input) {
            KiwiException exception = assertThrows(KiwiException.class, () -> KiwiDate.parse(input));
            assertEquals(input + " is not a real calendar date. " + PARSE_ERROR_MESSAGE,
                    exception.getMessage());
        }
    }

    @Nested
    class Format {

        @ParameterizedTest
        @MethodSource("kiwi.task.KiwiDateTest#formatTestCases")
        void format_validDate_returnsEnglishDisplayFormat(LocalDate date, String expected) {
            assertEquals(expected, KiwiDate.format(date));
        }
    }

    static Stream<Arguments> formatTestCases() {
        return Stream.of(
                Arguments.of(LocalDate.of(2019, 10, 15), "Oct 15 2019"),
                Arguments.of(LocalDate.of(2019, 12, 2), "Dec 02 2019"),
                Arguments.of(LocalDate.of(2020, 2, 29), "Feb 29 2020"),
                Arguments.of(LocalDate.of(2020, 1, 1), "Jan 01 2020"),
                Arguments.of(LocalDate.of(2019, 7, 4), "Jul 04 2019"),
                Arguments.of(LocalDate.of(0, 1, 1), "Jan 01 0000"),
                Arguments.of(LocalDate.of(1, 1, 1), "Jan 01 0001")
        );
    }
}

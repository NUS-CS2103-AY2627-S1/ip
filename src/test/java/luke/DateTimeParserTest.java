package luke;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import luke.datetime.DateTimeParser;

/**
 * Tests date and time parsing helpers.
 */
final class DateTimeParserTest {
    @Test
    void formatDateOrTimeFromFlag_supportedDateValues() {
        TestSupport.assertEquals("Dec 2 2019", DateTimeParser.formatDateOrTimeFromFlag("2/12/2019"));
        TestSupport.assertEquals("Dec 2 2019", DateTimeParser.formatDateOrTimeFromFlag("2-12-2019"));
        TestSupport.assertEquals("Dec 2 2019", DateTimeParser.formatDateOrTimeFromFlag("2019-12-2"));
        TestSupport.assertEquals("Dec 2 2019", DateTimeParser.formatDateOrTimeFromFlag("2019/12/2"));
        TestSupport.assertEquals("May 4 2020", DateTimeParser.formatDateOrTimeFromFlag("May 4 2020"));
        TestSupport.assertEquals("Dec 2 2019", DateTimeParser.formatDateOrTimeFromFlag("2 Dec 2019"));
        TestSupport.assertEquals("Dec 2 2019", DateTimeParser.formatDateOrTimeFromFlag("2 December 2019"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "2/12/2019 1800", "2/12/2019 18:00", "2/12/2019 6pm", "2/12/2019 6:00pm",
        "2-12-2019 1800", "2019-12-2 1800", "2019/12/2 1800", "2 Dec 2019 1800",
        "2 December 2019 1800"
    })
    void formatDateOrTimeFromFlag_supportedDateTimeValues(String input) {
        TestSupport.assertEquals("Dec 2 2019 6:00 PM", DateTimeParser.formatDateOrTimeFromFlag(input));
    }

    @Test
    void formatDateOrTimeFromFlag_supportedDateTimeWithDifferentDate() {
        TestSupport.assertEquals("Sep 9 1999 8:00 PM",
                DateTimeParser.formatDateOrTimeFromFlag("1999/9/9 20:00:00"));
    }

    @Test
    void formatDateOrTimeFromFlag_supportedTimeValues() {
        TestSupport.assertEquals("6:00 PM", DateTimeParser.formatDateOrTimeFromFlag("1800"));
        TestSupport.assertEquals("6:00 PM", DateTimeParser.formatDateOrTimeFromFlag("18:00"));
        TestSupport.assertEquals("12:00 AM", DateTimeParser.formatDateOrTimeFromFlag("12:00:00 AM"));
        TestSupport.assertEquals("6:00 PM", DateTimeParser.formatDateOrTimeFromFlag("6:00 pm"));
        TestSupport.assertEquals("6:00 PM", DateTimeParser.formatDateOrTimeFromFlag("6:00pm"));
        TestSupport.assertEquals("6:00 PM", DateTimeParser.formatDateOrTimeFromFlag("6pm"));
        TestSupport.assertEquals("6:00 PM", DateTimeParser.formatDateOrTimeFromFlag("6 pm"));
    }

    @Test
    void formatDateOrTimeFromFlag_unsupportedDateTimeValue_unchanged() {
        String formattedDateTime = DateTimeParser.formatDateOrTimeFromFlag("someday maybe");

        TestSupport.assertEquals("someday maybe", formattedDateTime);
    }

    @Test
    void parseSortKey_supportedDateTimeValues() {
        TestSupport.assertEquals(
                LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseSortKey("Dec 2 2019 6:00 PM"));
        TestSupport.assertEquals(
                LocalDateTime.of(2020, 5, 4, 0, 0),
                DateTimeParser.parseSortKey("May 4 2020"));
        TestSupport.assertEquals(
                LocalDateTime.of(1999, 9, 9, 20, 0),
                DateTimeParser.parseSortKey("1999/9/9 20:00:00"));
        TestSupport.assertEquals(LocalDate.MIN.atTime(21, 0), DateTimeParser.parseSortKey("2100"));
        TestSupport.assertEquals(null, DateTimeParser.parseSortKey("someday maybe"));
    }
}

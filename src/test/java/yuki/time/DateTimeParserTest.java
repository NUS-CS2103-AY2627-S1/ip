package yuki.time;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests accepted date formats and the stable storage representation. */
class DateTimeParserTest {
    @Test
    void of_nullValue_nullPointerExceptionThrown() {
        assertThrows(NullPointerException.class, () -> TaskDateTime.of((LocalDateTime) null));
        assertThrows(NullPointerException.class, () -> TaskDateTime.of((LocalDate) null));
        assertThrows(NullPointerException.class, () -> TaskDateTime.of((String) null));
    }

    @Test
    void parse_supportedDateTimeFormats_dateTimeReturned() {
        TaskDateTime compactTime = DateTimeParser.parse("2/12/2026 1800");
        TaskDateTime colonTime = DateTimeParser.parse("2/12/2026 18:00");

        LocalDateTime expected = LocalDateTime.of(2026, 12, 2, 18, 0);
        assertAll(() -> assertEquals(expected, compactTime.getDateTime()), () ->
                assertEquals(expected, colonTime.getDateTime()));
    }

    @Test
    void parse_supportedDateFormats_dateReturned() {
        TaskDateTime localFormat = DateTimeParser.parse("2/12/2026");
        TaskDateTime isoFormat = DateTimeParser.parse("2026-12-02");

        LocalDate expected = LocalDate.of(2026, 12, 2);
        assertAll(() -> assertEquals(expected, localFormat.getDate()), () ->
                assertEquals(expected, isoFormat.getDate()));
    }

    @Test
    void parse_unrecognizedDate_originalTextReturned() {
        TaskDateTime value = DateTimeParser.parse("next Friday evening");

        assertAll(() -> assertFalse(value.hasDateTime()), () ->
                assertFalse(value.hasDateOnly()), () ->
                assertEquals("next Friday evening", value.getText()));
    }

    @Test
    void of_blankOrControlCharacterText_exceptionThrown() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () ->
                        TaskDateTime.of("   ")), () ->
                assertThrows(IllegalArgumentException.class, () ->
                        TaskDateTime.of("next\u0000Friday")));
    }

    @Test
    void format_parsedValues_friendlyTextReturned() {
        assertAll(() -> assertEquals("Dec 2 2026 18:00",
                        DateTimeParser.format(TaskDateTime.of(
                                LocalDateTime.of(2026, 12, 2, 18, 0)))), () ->
                assertEquals("Dec 2 2026",
                        DateTimeParser.format(TaskDateTime.of(LocalDate.of(2026, 12, 2)))));
    }

    @Test
    void formatStoredAndParseStored_allValueTypes_roundTripPreserved() {
        TaskDateTime dateTime = TaskDateTime.of(LocalDateTime.of(2026, 12, 2, 18, 0));
        TaskDateTime date = TaskDateTime.of(LocalDate.of(2026, 12, 2));
        TaskDateTime text = TaskDateTime.of("someday");

        TaskDateTime restoredDateTime = DateTimeParser.parseStored(DateTimeParser.formatStored(dateTime));
        TaskDateTime restoredDate = DateTimeParser.parseStored(DateTimeParser.formatStored(date));
        TaskDateTime restoredText = DateTimeParser.parseStored(DateTimeParser.formatStored(text));

        assertAll(() -> assertEquals(dateTime.getDateTime(), restoredDateTime.getDateTime()), () ->
                assertEquals(date.getDate(), restoredDate.getDate()), () ->
                assertEquals(text.getText(), restoredText.getText()));
    }

    @Test
    void parseStored_legacyFormats_valuesRestored() {
        TaskDateTime dateTime = DateTimeParser.parseStored("2026-12-02T18:00");
        TaskDateTime date = DateTimeParser.parseStored("2026-12-02");
        TaskDateTime time = DateTimeParser.parseStored("T:18:30");
        TaskDateTime text = DateTimeParser.parseStored("next Friday");

        assertAll(() -> assertEquals(LocalDateTime.of(2026, 12, 2, 18, 0),
                        dateTime.getDateTime()), () ->
                assertEquals(LocalDate.of(2026, 12, 2), date.getDate()), () ->
                assertEquals("6:30 PM", time.getText()), () ->
                assertEquals("next Friday", text.getText()));
    }

    @Test
    void format_textValue_originalTextReturned() {
        assertEquals("next Friday", DateTimeParser.format(TaskDateTime.of("next Friday")));
    }

    @Test
    void isBefore_valuesOfSameAndDifferentTypes_correctOrderingReturned() {
        TaskDateTime earlier = TaskDateTime.of(LocalDate.of(2026, 8, 6));
        TaskDateTime later = TaskDateTime.of(LocalDate.of(2026, 8, 7));
        TaskDateTime earlierDateTime = TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 10, 0));
        TaskDateTime laterDateTime = TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 11, 0));
        TaskDateTime text = TaskDateTime.of("later");

        assertAll(() -> assertTrue(earlier.isBefore(later)), () ->
                assertFalse(later.isBefore(earlier)), () ->
                assertTrue(earlierDateTime.isBefore(laterDateTime)), () ->
                assertFalse(laterDateTime.isBefore(earlierDateTime)), () ->
                assertFalse(earlier.isBefore(text)), () ->
                assertThrows(NullPointerException.class, () -> earlier.isBefore(null)));
    }

    @Test
    void hasComparableType_allRepresentations_correctResultReturned() {
        TaskDateTime date = TaskDateTime.of(LocalDate.of(2026, 8, 6));
        TaskDateTime anotherDate = TaskDateTime.of(LocalDate.of(2026, 8, 7));
        TaskDateTime dateTime = TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 10, 0));
        TaskDateTime anotherDateTime = TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 11, 0));
        TaskDateTime text = TaskDateTime.of("later");

        assertAll(() -> assertTrue(date.hasComparableType(anotherDate)), () ->
                assertTrue(dateTime.hasComparableType(anotherDateTime)), () ->
                assertFalse(date.hasComparableType(dateTime)), () ->
                assertFalse(text.hasComparableType(text)), () ->
                assertThrows(NullPointerException.class, () -> date.hasComparableType(null)));
    }

    @Test
    void hasSameValueAs_equalDifferentAndNullValues_correctResultReturned() {
        TaskDateTime date = TaskDateTime.of(LocalDate.of(2026, 8, 6));

        assertAll(() -> assertTrue(date.hasSameValueAs(
                        TaskDateTime.of(LocalDate.of(2026, 8, 6)))), () ->
                assertFalse(date.hasSameValueAs(TaskDateTime.of(LocalDate.of(2026, 8, 7)))), () ->
                assertFalse(date.hasSameValueAs(
                        TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 0, 0)))), () ->
                assertFalse(date.hasSameValueAs(TaskDateTime.of("2026-08-06"))), () ->
                assertFalse(date.hasSameValueAs(null)));
    }

    @Test
    void toReminderDateTime_dateTimeDateAndText_expectedValuesReturned() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 9, 18, 0);
        TaskDateTime dateValue = TaskDateTime.of(LocalDate.of(2026, 9, 9));

        assertAll(() -> assertEquals(dateTime,
                        TaskDateTime.of(dateTime).toReminderDateTime().orElseThrow()), () ->
                assertEquals(LocalDateTime.of(2026, 9, 9, 23, 59),
                        dateValue.toReminderDateTime().orElseThrow()), () ->
                assertTrue(TaskDateTime.of("someday").toReminderDateTime().isEmpty()));
    }
}

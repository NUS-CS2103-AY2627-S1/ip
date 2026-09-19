package eli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests supported, natural-language, and invalid task dates. */
public class TaskDateTimeFormatterTest {
    @Test
    public void formatDateTime_supportedValue_returnsFriendlyFormat() {
        assertEquals("Sep 15 2026, 2:05PM",
                TaskDateTimeFormatter.formatDateTime("2026-09-15 1405"));
    }

    @Test
    public void formatDateOrDateTime_supportedValues_returnsFriendlyFormats() {
        assertEquals("Sep 18 2026",
                TaskDateTimeFormatter.formatDateOrDateTime("2026-09-18"));
        assertEquals("Sep 18 2026, 11:59PM",
                TaskDateTimeFormatter.formatDateOrDateTime("2026-09-18 2359"));
    }

    @Test
    public void formatting_naturalLanguage_preservesOriginalValue() {
        assertEquals("tomorrow afternoon",
                TaskDateTimeFormatter.formatDateTime("tomorrow afternoon"));
        assertEquals("next Friday",
                TaskDateTimeFormatter.formatDateOrDateTime("next Friday"));
    }

    @Test
    public void parseInputDateTime_supportedAndInvalidValues() {
        assertEquals(14, TaskDateTimeFormatter.parseInputDateTime(
                "2026-09-15 1400").orElseThrow().getHour());
        assertTrue(TaskDateTimeFormatter.parseInputDateTime("tomorrow").isEmpty());
    }

    @Test
    public void invalidDateDetection_distinguishesImpossibleAndNaturalValues() {
        assertTrue(TaskDateTimeFormatter.isInvalidDateOrDateTime("2026-02-30"));
        assertTrue(TaskDateTimeFormatter.isInvalidDateOrDateTime("2026-09-15 2500"));
        assertFalse(TaskDateTimeFormatter.isInvalidDateOrDateTime("next Friday"));
        assertTrue(TaskDateTimeFormatter.isInvalidDateTime("2026-09-15 2500"));
        assertFalse(TaskDateTimeFormatter.isInvalidDateTime("tomorrow afternoon"));
    }

    @Test
    public void parseDisplayValues_supportedAndInvalidValues() {
        assertEquals(14, TaskDateTimeFormatter.parseDisplayDateTime(
                "Sep 15 2026, 2:00PM").orElseThrow().getHour());
        assertTrue(TaskDateTimeFormatter.parseDisplayDateTime("tomorrow").isEmpty());
        assertEquals(18, TaskDateTimeFormatter.parseDisplayDateOrDateTime(
                "Sep 18 2026").orElseThrow().getDayOfMonth());
        assertEquals(14, TaskDateTimeFormatter.parseDisplayDateOrDateTime(
                "Sep 15 2026, 2:00PM").orElseThrow().getHour());
        assertTrue(TaskDateTimeFormatter.parseDisplayDateOrDateTime("later").isEmpty());
    }
}

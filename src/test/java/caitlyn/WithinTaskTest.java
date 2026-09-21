package caitlyn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests inclusive window validation, minute precision, and display/storage fidelity. */
class WithinTaskTest {
    @Test
    void withinTask_dateOnlyWindow_preservesDatesAndStatus() {
        WithinTask task = new WithinTask("collect certificate", "2027-01-15", "2027-01-25");

        assertEquals("[W][ ] collect certificate (within: Jan 15 2027 to: Jan 25 2027)", task.toString());
        assertEquals("W | 0 | collect certificate | 2027-01-15 | 2027-01-25", task.toStorageString());
        task.markAsDone();
        assertEquals("[W][X] collect certificate (within: Jan 15 2027 to: Jan 25 2027)", task.toString());
        task.markAsNotDone();
        assertEquals("W | 0 | collect certificate | 2027-01-15 | 2027-01-25", task.toStorageString());
    }

    @Test
    void withinTask_supportedFormats_normalizesWithoutLosingExplicitTimes() {
        for (String input : List.of("2027-01-15 0900", "15/1/2027 0900", "2027-01-15 09:00",
                "15/1/2027 09:00", "2027-01-15T09:00", "2027-01-15T09:00:00.000")) {
            WithinTask task = new WithinTask("collect", input, "25/1/2027 17:00");
            assertEquals("W | 0 | collect | 2027-01-15T09:00 | 2027-01-25T17:00", task.toStorageString());
            assertEquals("[W][ ] collect (within: Jan 15 2027 9:00 AM to: Jan 25 2027 5:00 PM)",
                    task.toString());
        }
        assertEquals("[W][ ] collect (within: Jan 15 2027 12:00 AM to: Jan 15 2027)",
                new WithinTask("collect", "2027-01-15T00:00", "2027-01-15").toString());
        assertEquals("W | 0 | collect | 2027-01-15 | 2027-01-25",
                new WithinTask("collect", "15/1/2027", "25/1/2027").toStorageString());
    }

    @Test
    void withinTask_equalAndMixedBoundaries_acceptsWholeDaysAndSingleInstants() {
        for (String from : List.of("2027-01-15", "2027-01-15T09:00")) {
            for (String to : List.of("2027-01-15", "2027-01-15T09:00")) {
                assertEquals("W | 0 | submit | " + from + " | " + to,
                        new WithinTask("submit", from, to).toStorageString());
            }
        }
        assertEquals("W | 0 | submit | 2027-01-15T23:59 | 2027-01-15",
                new WithinTask("submit", "2027-01-15T23:59", "2027-01-15").toStorageString());
    }

    @Test
    void withinTask_calendarEdges_acceptsPastLeapYearAndMaximumDates() {
        for (String[] bounds : List.of(
                new String[]{"1900-12-31", "1901-01-01"},
                new String[]{"2028-02-29", "2028-03-01"},
                new String[]{"+999999999-12-31T23:59", "+999999999-12-31"})) {
            assertEquals("W | 0 | task | " + bounds[0] + " | " + bounds[1],
                    new WithinTask("task", bounds[0], bounds[1]).toStorageString());
        }
    }

    @Test
    void withinTask_invalidFields_rejectsBlankDescriptionsDatesAndPrecision() {
        assertThrows(IllegalArgumentException.class, () -> new WithinTask(null, "2027-01-15", "2027-01-25"));
        assertThrows(IllegalArgumentException.class, () -> new WithinTask(" \t ", "2027-01-15", "2027-01-25"));
        for (String invalid : List.of("", "tomorrow", "09:00", "2027-02-29", "2027-01-15 2460",
                "2027-01-15T09:00:01", "2027-01-15T09:00:00.001", "2027-01-15T09:00Z")) {
            assertThrows(IllegalArgumentException.class, () -> new WithinTask("task", invalid, "2027-03-01"));
            assertThrows(IllegalArgumentException.class, () -> new WithinTask("task", "2027-01-01", invalid));
        }
        assertThrows(IllegalArgumentException.class, () -> new WithinTask("task", null, "2027-01-25"));
        assertThrows(IllegalArgumentException.class, () -> new WithinTask("task", "2027-01-15", null));
    }

    @Test
    void withinTask_reversedWindows_rejectsAllBoundaryPrecisionCombinations() {
        for (String from : List.of("2027-01-26", "2027-01-26T09:00")) {
            for (String to : List.of("2027-01-25", "2027-01-25T17:00")) {
                assertThrows(IllegalArgumentException.class, () -> new WithinTask("task", from, to));
            }
        }
        assertThrows(IllegalArgumentException.class, () ->
                new WithinTask("task", "2027-01-15T09:01", "2027-01-15T09:00"));
    }

    @Test
    void withinTask_descriptionCharacters_preservesInternalTextAndEscapesStorage() {
        WithinTask task = new WithinTask("  collect | file \\backup  notes  ", "2027-01-15", "2027-01-25");
        assertEquals("collect | file \\backup  notes", task.getDescription());
        assertEquals("W | 0 | collect \\| file \\\\backup  notes | 2027-01-15 | 2027-01-25",
                task.toStorageString());
    }
}

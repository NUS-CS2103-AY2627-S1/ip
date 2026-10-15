package sevensix;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Verifies event chronology and task identity independently of command dispatch. */
class TaskValidationTest {
    /** A change to any one event endpoint field must yield a distinct schedule.
     *
     * @param startDate the candidate start date.
     * @param startTime the candidate start time.
     * @param endDate the candidate end date.
     * @param endTime the candidate end time.
     */
    @ParameterizedTest
    @CsvSource({"2024-01-02,12:00,2024-01-03,13:00", "2024-01-01,11:00,2024-01-03,13:00",
        "2024-01-01,12:00,2024-01-04,13:00", "2024-01-01,12:00,2024-01-03,14:00"})
    void hasSameDetailsRejectsEachChangedEventField(String startDate, String startTime,
            String endDate, String endTime) {
        Event original = new Event("trip", LocalDate.of(2024, 1, 1), LocalTime.NOON,
                LocalDate.of(2024, 1, 3), LocalTime.of(13, 0));
        Event candidate = new Event("trip", LocalDate.parse(startDate), LocalTime.parse(startTime),
                LocalDate.parse(endDate), LocalTime.parse(endTime));

        assertFalse(TaskValidation.hasSameDetails(original, candidate));
        assertFalse(TaskValidation.hasSameDetails(candidate, original));
    }

    /** Equal or reversed event endpoints must be rejected, including date-only values. */
    @Test
    void parseEventRejectsNonPositiveDurations() {
        for (String range : List.of("2024-01-01 /to 2024-01-01", "2024-01-02 /to 2024-01-01",
                "2024-01-01 1200 /to 2024-01-01 1200", "2024-01-01 1300 /to 2024-01-01 1200",
                "2024-01-01 1200 /to 2024-01-01")) {
            assertThrows(SevenSixException.class, () -> Parser.parseEvent("event meeting /from " + range), range);
        }
    }

    /** Date-only endpoints use midnight and timed events may span midnight. */
    @Test
    void parseEventAcceptsPositiveDurations() throws SevenSixException {
        for (String range : List.of("2024-01-01 /to 2024-01-02", "2024-01-01 /to 2024-01-01 1200",
                "2024-01-01 2300 /to 2024-01-02 0100", "2024-01-01 1200 /to 2024-01-01 1201")) {
            TaskValidation.validate(Parser.parseEvent("event meeting /from " + range));
        }
    }

    /** Completion status does not make an otherwise identical task unique. */
    @Test
    void hasDuplicateMatchesTaskDetailsRegardlessOfStatus() throws SevenSixException {
        TaskList tasks = new TaskList();
        Deadline existing = Parser.parseDeadline("deadline report /by 2024-02-29 1200");
        existing.markAsDone();
        tasks.add(existing);

        assertTrue(tasks.hasDuplicate(Parser.parseDeadline("deadline report /by 29/2/2024 1200")));
        assertFalse(tasks.hasDuplicate(Parser.parseDeadline("deadline report /by 2024-02-29 1300")));
        assertFalse(tasks.hasDuplicate(new Todo("report")));
        assertFalse(tasks.hasDuplicate(new Deadline("Report", LocalDate.of(2024, 2, 29))));
    }

    /** Both event endpoints and optional times are part of its identity. */
    @Test
    void hasSameDetailsComparesTheCompleteEventSchedule() throws SevenSixException {
        Event first = Parser.parseEvent("event meeting /from 2024-01-01 /to 2024-01-02");
        Event same = Parser.parseEvent("event meeting /from 1/1/2024 /to 2/1/2024");
        Event different = Parser.parseEvent("event meeting /from 2024-01-01 /to 2024-01-03");

        assertTrue(TaskValidation.hasSameDetails(first, same));
        assertFalse(TaskValidation.hasSameDetails(first, different));
    }
}

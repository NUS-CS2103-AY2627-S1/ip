package gunna.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test class for testing the Deadline class.
 * Tests cover creation with dates, string representations, file format, and date parsing.
 */
public class DeadlineTest {

    private Deadline deadline;
    private LocalDate testDate;

    @BeforeEach
    public void setUp() {
        testDate = LocalDate.of(2024, 12, 25);
        deadline = new Deadline("Submit assignment", testDate);
    }

    @Test
    public void constructor_newDeadline_isNotDone() {
        assertFalse(deadline.isDone(), "New deadline should not be marked as done");
    }

    @Test
    public void constructor_newDeadline_hasCorrectDescription() {
        assertEquals("Submit assignment", deadline.getDescription(),
                "Deadline description should match the constructor argument");
    }

    @Test
    public void getByDate_newDeadline_returnsCorrectDate() {
        assertEquals(testDate, deadline.getByDate(),
                "getByDate should return the LocalDate object");
    }

    @Test
    public void getBy_newDeadline_returnsFormattedDate() {
        assertEquals("Dec 25 2024", deadline.getBy(),
                "getBy should return date in 'MMM dd yyyy' format");
    }

    @Test
    public void getByForStorage_newDeadline_returnsDateInStorageFormat() {
        assertEquals("2024-12-25", deadline.getByForStorage(),
                "getByForStorage should return date in 'yyyy-MM-dd' format");
    }

    @Test
    public void toString_notDoneDeadline_correctFormat() {
        assertEquals("[D][ ] Submit assignment (by: Dec 25 2024)", deadline.toString(),
                "String representation should show [D] prefix and formatted date");
    }

    @Test
    public void toString_doneDeadline_correctFormat() {
        deadline.markAsDone();
        assertEquals("[D][X] Submit assignment (by: Dec 25 2024)", deadline.toString(),
                "String representation should show [D] prefix and [X] for done deadline");
    }

    @Test
    public void toFileFormat_notDoneDeadline_correctFormat() {
        assertEquals("D | 0 | Submit assignment | 2024-12-25", deadline.toFileFormat(),
                "File format should use 'D' as task type and yyyy-MM-dd date format");
    }

    @Test
    public void toFileFormat_doneDeadline_correctFormat() {
        deadline.markAsDone();
        assertEquals("D | 1 | Submit assignment | 2024-12-25", deadline.toFileFormat(),
                "File format should have 1 as status indicator for done deadline");
    }

    @Test
    public void createWithDateString_validDate_createsDeadline() {
        Deadline created = Deadline.createWithDateString("Return books", "2024-06-15");
        assertEquals("Return books", created.getDescription(),
                "Created deadline should have correct description");
        assertEquals(LocalDate.of(2024, 6, 15), created.getByDate(),
                "Created deadline should have correct date");
    }

    @Test
    public void createWithDateString_validDate_formatsCorrectly() {
        Deadline created = Deadline.createWithDateString("Return books", "2024-06-15");
        assertEquals("Jun 15 2024", created.getBy(),
                "Created deadline should format date correctly");
    }

    @Test
    public void createWithDateString_invalidDateFormat_throwsException() {
        assertThrows(DateTimeParseException.class, () -> {
            Deadline.createWithDateString("Invalid date", "25-12-2024");
        }, "Should throw DateTimeParseException for invalid date format");
    }

    @Test
    public void createWithDateString_invalidDate_throwsException() {
        assertThrows(DateTimeParseException.class, () -> {
            Deadline.createWithDateString("Invalid date", "2024-13-45");
        }, "Should throw DateTimeParseException for invalid date values");
    }

    @Test
    public void createWithDateString_notADate_throwsException() {
        assertThrows(DateTimeParseException.class, () -> {
            Deadline.createWithDateString("Invalid date", "not a date");
        }, "Should throw DateTimeParseException for non-date string");
    }

    @Test
    public void createWithDateString_emptyString_throwsException() {
        assertThrows(DateTimeParseException.class, () -> {
            Deadline.createWithDateString("Empty date", "");
        }, "Should throw DateTimeParseException for empty date string");
    }

    @Test
    public void constructor_leapYearDate_handlesCorrectly() {
        LocalDate leapDate = LocalDate.of(2024, 2, 29);
        Deadline leapDeadline = new Deadline("Leap year task", leapDate);
        assertEquals("Feb 29 2024", leapDeadline.getBy(),
                "Should handle leap year date correctly");
    }

    @Test
    public void toString_emptyDescription_correctFormat() {
        Deadline emptyDeadline = new Deadline("", testDate);
        assertEquals("[D][ ]  (by: Dec 25 2024)", emptyDeadline.toString(),
                "Should handle empty description in toString");
    }

    @Test
    public void toFileFormat_emptyDescription_correctFormat() {
        Deadline emptyDeadline = new Deadline("", testDate);
        assertEquals("D | 0 |  | 2024-12-25", emptyDeadline.toFileFormat(),
                "Should handle empty description in file format");
    }

    @Test
    public void constructor_firstDayOfYear_formatsCorrectly() {
        LocalDate firstDay = LocalDate.of(2025, 1, 1);
        Deadline newYearDeadline = new Deadline("New Year task", firstDay);
        assertEquals("Jan 01 2025", newYearDeadline.getBy(),
                "Should format first day of year correctly");
    }

    @Test
    public void constructor_lastDayOfYear_formatsCorrectly() {
        LocalDate lastDay = LocalDate.of(2025, 12, 31);
        Deadline newYearEveDeadline = new Deadline("Year end task", lastDay);
        assertEquals("Dec 31 2025", newYearEveDeadline.getBy(),
                "Should format last day of year correctly");
    }

    @Test
    public void constructor_descriptionWithPipeCharacter_handlesCorrectly() {
        Deadline pipeDeadline = new Deadline("Task | with | pipes", testDate);
        assertTrue(pipeDeadline.toFileFormat().contains("Task | with | pipes"),
                "Should preserve pipe characters in description");
    }
}

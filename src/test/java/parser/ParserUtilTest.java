package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import exception.FriedbergException;
import exception.FriedbergUserInputException;

/**
 * Tests the strict command grammar shared by Friedberg commands.
 */
class ParserUtilTest {
    @Test
    void validateRawInput_validSpacing_doesNotThrow() throws Exception {
        ParserUtil.validateRawInput("todo read a book");
    }

    @Test
    void validateRawInput_invalidWhitespace_throwsUserInputException() {
        String[] invalidInputs = {" todo read", "todo read ", "todo  read", "todo\tread", "todo\nread", ""};

        for (String input : invalidInputs) {
            assertThrows(FriedbergUserInputException.class, () -> ParserUtil.validateRawInput(input), input);
        }
        assertThrows(FriedbergUserInputException.class, () -> ParserUtil.validateRawInput(null));
    }

    @Test
    void parseIndexCommand_positiveDigits_returnsZeroBasedIndex() throws Exception {
        assertEquals(0, ParserUtil.parseIndexCommand("mark 1", "mark"));
        assertEquals(41, ParserUtil.parseIndexCommand("delete 42", "delete"));
    }

    @Test
    void parseIndexCommand_invalidIndex_throwsUserInputException() {
        String[] invalidInputs = {
            "mark", "mark 0", "mark -1", "mark +1", "mark 1.0", "mark one", "mark 1!",
            "mark 2147483648", "mark 1 extra"
        };

        for (String input : invalidInputs) {
            assertThrows(
                    FriedbergUserInputException.class, () -> ParserUtil.parseIndexCommand(input, "mark"), input);
        }
    }

    @Test
    void validateDescription_supportedText_succeeds() throws Exception {
        ParserUtil.validateDescription("Müller's report (part-2)!", "task description");
    }

    @Test
    void validateDescription_unsafeCharacters_throwsUserInputException() {
        String[] invalidDescriptions = {"", "bad,name", "bad/name", "#hidden", "bad\nname"};

        for (String description : invalidDescriptions) {
            assertThrows(FriedbergUserInputException.class, () ->
                    ParserUtil.validateDescription(description, "task description"), description);
        }
    }

    @Test
    void parseStrictDate_validNormalAndLeapDates_returnsDates() throws Exception {
        assertEquals(LocalDate.of(2026, 9, 10), ParserUtil.parseStrictDate("2026-09-10", "/by"));
        assertEquals(LocalDate.of(2024, 2, 29), ParserUtil.parseStrictDate("2024-02-29", "/by"));
    }

    @Test
    void parseStrictDate_invalidFormatOrNonexistentDate_throwsUserInputException() {
        String[] invalidDates = {
            "2026/09/10", "2026-9-10", "2026-09-1", "2026-09-10T12:00",
            "2025-02-29", "2026-04-31", "2026-13-01", "2026-00-10"
        };

        for (String date : invalidDates) {
            assertThrows(
                    FriedbergUserInputException.class, () -> ParserUtil.parseStrictDate(date, "/by"), date);
        }
    }

    @Test
    void parseDeadlineCommand_duplicateOrMissingParameter_throwsUserInputException() {
        assertThrows(FriedbergUserInputException.class, () ->
                ParserUtil.parseDeadlineCommand("deadline report /by 2026-09-10 /by 2026-09-11"));
        assertThrows(FriedbergUserInputException.class, () ->
                ParserUtil.parseDeadlineCommand("deadline report 2026-09-10"));
        assertThrows(FriedbergUserInputException.class, () ->
                ParserUtil.parseDeadlineCommand("deadline /by 2026-09-10"));
    }

    @Test
    void parseEventCommand_invalidParametersAndOrder_throwsUserInputException() {
        String[] invalidInputs = {
            "event meeting /from 2026-09-09 /from 2026-09-10 /to 2026-09-11",
            "event meeting /from 2026-09-09 /to 2026-09-10 /to 2026-09-11",
            "event meeting /to 2026-09-10 /from 2026-09-09",
            "event meeting /from 2026-09-10 /to 2026-09-10",
            "event meeting /from 2026-09-11 /to 2026-09-10"
        };

        for (String input : invalidInputs) {
            assertThrows(
                    FriedbergUserInputException.class, () -> ParserUtil.parseEventCommand(input), input);
        }
    }

    @Test
    void commandParser_commandPrefixIsNotExact_throwsFriedbergException() {
        assertThrows(FriedbergException.class, () -> CommandParser.parse("todoing read book"));
        assertThrows(FriedbergException.class, () -> CommandParser.parse("list!"));
    }
}

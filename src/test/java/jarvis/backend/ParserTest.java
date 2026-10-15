package jarvis.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import jarvis.classes.Deadline;
import jarvis.classes.Event;
import jarvis.exceptions.IncompleteCommandException;
import jarvis.exceptions.InvalidDateAndTimeException;
import jarvis.exceptions.InvalidStartAndEndTimeException;
import jarvis.exceptions.TooSimpleArgumentException;

/** Tests command parsing behavior. */
public class ParserTest {

    /**
     * Verifies that null input is rejected by parseToDo.
     *
     * Assertions are enabled when running the tests so that the
     * explicit null assertion in Parser is exercised.
     */
    @Test
    public void parseToDo_nullInput_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseToDo(null));
    }

    /** Verifies that a todo command produces the expected description. */
    @Test
    public void parseToDo_validCommand_returnsTask() throws Exception {
        assertEquals("[T][] Buy milk", Parser.parseToDo("todo Buy milk").toString());
    }

    /** Verifies that an incomplete todo command is rejected. */
    @Test
    public void parseToDo_missingDescription_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseToDo("todo"));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseToDo("todo "));
    }

    /**
     * Verifies that leading and trailing spaces do not create an
     * invalid task description.
     */
    @Test
    public void parseToDo_leadingAndTrailingSpaces_parsesTask() throws Exception {
        assertEquals(
                "[T][] Buy milk",
                Parser.parseToDo("  todo Buy milk  ").toString()
        );
    }

    /**
     * Verifies that multiple spaces between the command and
     * description are handled.
     */
    @Test
    public void parseToDo_multipleSpaces_parsesTask() throws Exception {
        assertEquals(
                "[T][] Buy milk",
                Parser.parseToDo("todo    Buy milk").toString()
        );
    }

    /**
     * Verifies that a command with only whitespace after todo
     * is rejected.
     */
    @Test
    public void parseToDo_onlyWhitespace_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseToDo("todo    "));
    }

    /**
     * Verifies that an empty input is rejected.
     */
    @Test
    public void parseToDo_emptyInput_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseToDo(""));
    }

    /**
     * Verifies that a command containing only the command word
     * with leading spaces is handled appropriately.
     */
    @Test
    public void parseToDo_leadingSpacesOnlyCommand_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseToDo("   todo"));
    }




    /**
     * Verifies that null input is rejected by parseDeadline.
     */
    @Test
    public void parseDeadline_nullInput_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseDeadline(null));
    }

    /** Verifies that date-only and date-time deadlines are supported. */
    @Test
    public void parseDeadline_validCommands_returnsDeadlines() throws Exception {
        Deadline dateOnly = Parser.parseDeadline("deadline Submit report /by 2026-08-22");
        Deadline dateTime = Parser.parseDeadline("deadline Submit report /by 2026-08-22 18:00");

        assertEquals("[D][] Submit report (by: 08 22 2026)", dateOnly.toString());
        assertEquals("[D][] Submit report (by: 08 22 2026 18:00)", dateTime.toString());
    }

    /** Verifies that an incomplete deadline command is rejected. */
    @Test
    public void parseDeadline_missingDescription_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseDeadline("deadline"));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseDeadline("deadline "));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseDeadline("deadline /by"));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseDeadline("deadline /by "));
    }

    /** Verifies that invalid deadline dates are rejected. */
    @Test
    public void parseDeadline_invalidDate_throwsException() {
        String tomorrow = "deadline Submit report /by tomorrow";
        String yearOnly = "deadline Submit report /by 2026";
        String monthOnly = "deadline Submit report /by 2026-08";
        String shortDay = "deadline Submit report /by 2026-08-7";
        String shortMinute = "deadline Submit report /by 2026-08-07 00:";
        String incompleteMinute = "deadline Submit report /by 2026-08-08 00:0";

        assertThrows(InvalidDateAndTimeException.class, () -> Parser.parseDeadline(tomorrow));
        assertThrows(InvalidDateAndTimeException.class, () -> Parser.parseDeadline(yearOnly));
        assertThrows(InvalidDateAndTimeException.class, () -> Parser.parseDeadline(monthOnly));
        assertThrows(InvalidDateAndTimeException.class, () -> Parser.parseDeadline(shortDay));
        assertThrows(InvalidDateAndTimeException.class, () -> Parser.parseDeadline(shortMinute));
        assertThrows(InvalidDateAndTimeException.class, () -> Parser.parseDeadline(incompleteMinute));
    }

    /**
     * Verifies that leading and trailing spaces are tolerated.
     */
    @Test
    public void parseDeadline_leadingAndTrailingSpaces_parsesDeadline() throws Exception {
        Deadline deadline = Parser.parseDeadline(
                "  deadline Submit report /by 2026-08-22 18:00  "
        );

        assertEquals("[D][] Submit report (by: 08 22 2026 18:00)",
                deadline.toString()
        );
    }

    /**
     * Verifies that multiple spaces around the /by parameter are handled.
     */
    @Test
    public void parseDeadline_multipleSpaces_parsesDeadline() throws Exception {
        Deadline deadline = Parser.parseDeadline(
                "deadline Submit report    /by    2026-08-22"
        );

        assertEquals("[D][] Submit report (by: 08 22 2026)",
                deadline.toString()
        );
    }

    /**
     * Verifies that an empty command is rejected.
     */
    @Test
    public void parseDeadline_emptyInput_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseDeadline(""));
    }

    /**
     * Verifies that whitespace-only input is rejected.
     */
    @Test
    public void parseDeadline_whitespaceOnly_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseDeadline("     "));
    }

    /**
     * Verifies that the /by parameter cannot appear without a task.
     */
    @Test
    public void parseDeadline_missingTask_throwsException() {
        assertThrows(
                IncompleteCommandException.class, () -> Parser.parseDeadline(
                        "deadline /by 2026-08-22"
                )
        );
    }

    /**
     * Verifies that the /by parameter cannot be specified twice.
     */
    @Test
    public void parseDeadline_duplicateByParameter_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseDeadline(
                        "deadline Submit report "
                                + "/by 2026-08-22 /by 2026-08-23"
                )
        );
    }

    /**
     * Verifies that an additional unexpected parameter after
     * the date is rejected.
     */
    @Test
    public void parseDeadline_extraParameter_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseDeadline(
                        "deadline Submit report /by 2026-08-22 tomorrow"
                )
        );
    }

    /**
     * Verifies that an invalid time hour is rejected.
     */
    @Test
    public void parseDeadline_invalidHour_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseDeadline(
                        "deadline Submit report /by 2026-08-22 24:00"
                )
        );
    }

    /**
     * Verifies that an invalid time minute is rejected.
     */
    @Test
    public void parseDeadline_invalidMinute_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseDeadline(
                        "deadline Submit report /by 2026-08-22 12:60"
                )
        );
    }

    /**
     * Verifies that special characters in the date are rejected.
     */
    @Test
    public void parseDeadline_specialCharactersInDate_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseDeadline(
                        "deadline Submit report /by 2026/08/22"
                )
        );
    }



    /**
     * Verifies that null input is rejected by parseEvent.
     */
    @Test
    public void parseEvent_nullInput_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseEvent(null));
    }

    /** Verifies that events parse their start and end times. */
    @Test
    public void parseEvent_validCommand_returnsEvent() throws Exception {
        Event dateOnly = Parser.parseEvent("event Meeting /from 2026-08-22 /to 2026-08-23");
        Event dateTime = Parser.parseEvent("event Meeting /from 2026-08-22 09:00 /to 2026-08-22 10:00");

        assertEquals("[E][] Meeting (from: 08 22 2026 to: 08 23 2026)", dateOnly.toString());
        assertEquals("[E][] Meeting (from: 08 22 2026 09:00 to: 08 22 2026 10:00)", dateTime.toString());
    }

    /** Verifies that an incomplete event command is rejected. */
    @Test
    public void parseEvent_missingDescription_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseEvent("event /from /to"));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseEvent("event Meeting /from "));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseEvent("event Meeting /from 2026-08-27 /to"));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseEvent("event Meeting /from 2026-08-27 /to "));
    }

    /** Verifies that invalid event dates are rejected. */
    @Test
    public void parseEvent_invalidDate_throwsException() {
        String invalidDay = "event Meeting /from 2026-08-99 /to 2026-08-99";
        String invalidMonth = "event Meeting /from 2026-99-27 /to 2026-99-28";
        String invalidStartTime = "event Meeting /from 2026-08-27 99:99 /to 2026-08-28 00:00";
        String invalidEndTime = "event Meeting /from 2026-08-27 00:00 /to 2026-08-28 99:99";

        assertThrows(InvalidDateAndTimeException.class, () -> Parser.parseEvent(invalidDay));
        assertThrows(InvalidDateAndTimeException.class, () -> Parser.parseEvent(invalidMonth));
        assertThrows(InvalidDateAndTimeException.class, () -> Parser.parseEvent(invalidStartTime));
        assertThrows(InvalidDateAndTimeException.class, () -> Parser.parseEvent(invalidEndTime));
    }

    /** Verifies that an event ending before it starts is rejected. */
    @Test
    public void parseEvent_endBeforeStart_throwsException() {
        String endBeforeStart = "event Meeting /from 2026-08-23 /to 2026-08-22";
        String endBeforeStartWithTime =
                "event Meeting /from 2026-08-22 10:00 /to 2026-08-22 09:00";

        assertThrows(InvalidStartAndEndTimeException.class, () -> Parser.parseEvent(endBeforeStart));
        assertThrows(InvalidStartAndEndTimeException.class, () -> Parser.parseEvent(endBeforeStartWithTime));
    }

    /**
     * Verifies that leading and trailing spaces are tolerated.
     */
    @Test
    public void parseEvent_leadingAndTrailingSpaces_parsesEvent() throws Exception {
        Event event = Parser.parseEvent(
                "  event Meeting /from 2026-08-22 09:00 "
                        + "/to 2026-08-22 10:00  "
        );

        assertEquals("[E][] Meeting (from: 08 22 2026 09:00 "
                        + "to: 08 22 2026 10:00)",
                event.toString()
        );
    }

    /**
     * Verifies that multiple spaces around event parameters
     * are handled.
     */
    @Test
    public void parseEvent_multipleSpaces_parsesEvent() throws Exception {
        Event event = Parser.parseEvent(
                "event Meeting    /from    2026-08-22 09:00    "
                        + "/to    2026-08-22 10:00"
        );

        assertEquals(
                "[E][] Meeting (from: 08 22 2026 09:00 "
                        + "to: 08 22 2026 10:00)",
                event.toString()
        );
    }

    /**
     * Verifies that an empty event command is rejected.
     */
    @Test
    public void parseEvent_emptyInput_throwsException() {
        assertThrows(
                IncompleteCommandException.class, () -> Parser.parseEvent("")
        );
    }

    /**
     * Verifies that whitespace-only event input is rejected.
     */
    @Test
    public void parseEvent_whitespaceOnly_throwsException() {
        assertThrows(
                IncompleteCommandException.class, () -> Parser.parseEvent("     ")
        );
    }

    /**
     * Verifies that the /from parameter cannot be duplicated.
     */
    @Test
    public void parseEvent_duplicateFromParameter_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseEvent(
                        "event Meeting "
                                + "/from 2026-08-22 09:00 "
                                + "/from 2026-08-22 10:00 "
                                + "/to 2026-08-22 11:00"
                )
        );
    }

    /**
     * Verifies that the /to parameter cannot be duplicated.
     */
    @Test
    public void parseEvent_duplicateToParameter_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseEvent(
                        "event Meeting "
                                + "/from 2026-08-22 09:00 "
                                + "/to 2026-08-22 10:00 "
                                + "/to 2026-08-22 11:00"
                )
        );
    }

    /**
     * Verifies that both /from and /to parameters must be present.
     */
    @Test
    public void parseEvent_missingFrom_throwsException() {
        assertThrows(
                IncompleteCommandException.class, () -> Parser.parseEvent(
                        "event Meeting /to 2026-08-22 10:00"
                )
        );
    }

    /**
     * Verifies that both /from and /to parameters must be present.
     */
    @Test
    public void parseEvent_missingTo_throwsException() {
        assertThrows(
                IncompleteCommandException.class, () -> Parser.parseEvent(
                        "event Meeting /from 2026-08-22 10:00"
                )
        );
    }

    /**
     * Verifies that equal start and end times are rejected.
     *
     * An event should have a positive duration.
     */
    @Test
    public void parseEvent_sameStartAndEndTime_throwsException() {
        assertThrows(
                InvalidStartAndEndTimeException.class, () -> Parser.parseEvent(
                        "event Meeting "
                                + "/from 2026-08-22 10:00 "
                                + "/to 2026-08-22 10:00"
                )
        );
    }

    /**
     * Verifies that equal start and end dates are rejected for
     * date-only events.
     */
    @Test
    public void parseEvent_sameStartAndEndDate_throwsException() {
        assertThrows(
                InvalidStartAndEndTimeException.class, () -> Parser.parseEvent(
                        "event Meeting "
                                + "/from 2026-08-22 "
                                + "/to 2026-08-22"
                )
        );
    }

    /**
     * Verifies that an invalid start hour is rejected.
     */
    @Test
    public void parseEvent_invalidStartHour_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseEvent(
                        "event Meeting "
                                + "/from 2026-08-22 24:00 "
                                + "/to 2026-08-23 10:00"
                )
        );
    }

    /**
     * Verifies that an invalid end minute is rejected.
     */
    @Test
    public void parseEvent_invalidEndMinute_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseEvent(
                        "event Meeting "
                                + "/from 2026-08-22 10:00 "
                                + "/to 2026-08-23 10:60"
                )
        );
    }

    /**
     * Verifies that date and date-time formats cannot be mixed.
     */
    @Test
    public void parseEvent_mixedDateFormats_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseEvent(
                        "event Meeting "
                                + "/from 2026-08-22 "
                                + "/to 2026-08-23 10:00"
                )
        );
    }

    /**
     * Verifies that an unexpected parameter is rejected.
     */
    @Test
    public void parseEvent_extraParameter_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseEvent(
                        "event Meeting "
                                + "/from 2026-08-22 10:00 "
                                + "/to 2026-08-23 10:00 extra"
                )
        );
    }

    /**
     * Verifies that special characters in the date are rejected.
     */
    @Test
    public void parseEvent_specialCharactersInDate_throwsException() {
        assertThrows(
                InvalidDateAndTimeException.class, () -> Parser.parseEvent(
                        "event Meeting "
                                + "/from 2026/08/22 "
                                + "/to 2026/08/23"
                )
        );
    }



    /**
     * Verifies that null input is rejected by parseTaskNumber.
     */
    @Test
    public void parseTaskNumber_nullInput_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseTaskNumber(null));
    }

    /** Verifies that task numbers are parsed from commands. */
    @Test
    public void parseTaskNumber_validCommand_returnsNumber() throws Exception {
        assertEquals(3, Parser.parseTaskNumber("delete 3"));
        assertEquals(2, Parser.parseTaskNumber("mark 2"));
        assertEquals(8, Parser.parseTaskNumber("unmark 8"));
    }

    /** Verifies that an incomplete mark/unmark/delete command is rejected. */
    @Test
    public void parseTaskNumber_missingDescription_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskNumber("delete"));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskNumber("delete "));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskNumber("mark"));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskNumber("mark "));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskNumber("unmark"));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskNumber("unmark "));
    }

    /**
     * Verifies that leading and trailing spaces are tolerated.
     */
    @Test
    public void parseTaskNumber_leadingAndTrailingSpaces_returnsNumber() throws Exception {
        assertEquals(
                3,
                Parser.parseTaskNumber("  delete 3  ")
        );
    }

    /**
     * Verifies that multiple spaces between the command and
     * task number are tolerated.
     */
    @Test
    public void parseTaskNumber_multipleSpaces_returnsNumber() throws Exception {
        assertEquals(
                3,
                Parser.parseTaskNumber("delete    3")
        );
    }

    /**
     * Verifies that zero is rejected as a task number.
     *
     * Parser itself currently only parses the number. Range validation
     * is performed later by JarvisController.
     */
    @Test
    public void parseTaskNumber_zero_returnsZero() throws Exception {
        assertEquals(
                0,
                Parser.parseTaskNumber("delete 0")
        );
    }

    /**
     * Verifies that negative numbers are parsed successfully.
     *
     * Range validation is intentionally performed by JarvisController.
     */
    @Test
    public void parseTaskNumber_negativeNumber_returnsNegativeNumber() throws Exception {
        assertEquals(
                -1,
                Parser.parseTaskNumber("delete -1")
        );
    }

    /**
     * Verifies that decimal task numbers are rejected.
     */
    @Test
    public void parseTaskNumber_decimalNumber_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskNumber("delete 1.5"));
    }

    /**
     * Verifies that alphabetic task numbers are rejected.
     */
    @Test
    public void parseTaskNumber_alphabeticNumber_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskNumber("delete abc"));
    }

    /**
     * Verifies that a command with an extra argument currently
     * ignores the extra argument.
     *
     * This test documents current behavior and should be changed
     * to expect an exception if strict command validation is added.
     */
    @Test
    public void parseTaskNumber_extraArgument_currentlyParsesFirstNumber() throws Exception {
        assertEquals(
                3,
                Parser.parseTaskNumber("delete 3 extra")
        );
    }



    /**
     * Verifies that null input is rejected by parseTaskKeyWord.
     */
    @Test
    public void parseTaskKeyWord_nullInput_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseTaskKeyWord(null));
    }

    /** Verifies that key words are parsed from commands. */
    @Test
    public void parseKeyWord_validCommand_returnsKeyWord() throws Exception {
        assertEquals("book", Parser.parseTaskKeyWord("find book"));
        assertEquals("read", Parser.parseTaskKeyWord("find read"));
        assertEquals("book", Parser.parseTaskKeyWord("find BOOK"));
        assertEquals("read", Parser.parseTaskKeyWord("find READ"));
    }

    /** Verifies that an incomplete find command is rejected. */
    @Test
    public void parseKeyWord_missingDescription_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskKeyWord("find"));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskKeyWord("find "));
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskKeyWord("find    "));
    }

    /** Verifies that a simple find key word is rejected. */
    @Test
    public void parseKeyWord_simpleKeyWord_throwsException() {
        assertThrows(TooSimpleArgumentException.class, () -> Parser.parseTaskKeyWord("find a"));
        assertThrows(TooSimpleArgumentException.class, () -> Parser.parseTaskKeyWord("find A"));
        assertThrows(TooSimpleArgumentException.class, () -> Parser.parseTaskKeyWord("find 1"));
    }

    /**
    * Verifies that leading and trailing spaces are tolerated.
    */
    @Test
    public void parseTaskKeyWord_leadingAndTrailingSpaces_returnsKeyword() throws Exception {
        assertEquals(
                "book",
                Parser.parseTaskKeyWord("  find book  ")
        );
    }

    /**
     * Verifies that multiple spaces after find are tolerated.
    */
    @Test
    public void parseTaskKeyWord_multipleSpaces_returnsKeyword() throws Exception {
        assertEquals(
                "book",
                Parser.parseTaskKeyWord("find    book")
        );
    }

    /**
    * Verifies that keywords are converted to lowercase.
    */
    @Test
    public void parseTaskKeyWord_mixedCase_returnsLowercaseKeyword() throws Exception {
        assertEquals(
                "assignment",
                Parser.parseTaskKeyWord("find AsSiGnMeNt")
        );
    }

    /**
    * Verifies that whitespace-only input is rejected.
    */
    @Test
    public void parseTaskKeyWord_whitespaceOnly_throwsException() {
        assertThrows(IncompleteCommandException.class, () -> Parser.parseTaskKeyWord("find     "));
    }

    /**
    * Verifies that a two-character keyword is accepted.
    */
    @Test
    public void parseTaskKeyWord_twoCharacters_returnsKeyword() throws Exception {
        assertEquals(
                "ab",
                Parser.parseTaskKeyWord("find ab")
        );
    }

    /**
     * Verifies that a keyword containing spaces is currently accepted.
     *
     * If the product requirement is that <key word> must be exactly
     * one word, this should instead expect an exception.
     */
    @Test
    public void parseTaskKeyWord_multipleWords_currentlyReturnsFullInput() throws Exception {
        assertEquals(
                "buy milk",
                Parser.parseTaskKeyWord("find buy milk")
        );
    }

    /**
     * Verifies that special characters are currently treated as
     * part of the keyword.
     *
     * If special characters are not allowed by the product requirements,
     * the production Parser should be changed and this test should
     * expect an exception instead.
     */
    @Test
    public void parseTaskKeyWord_specialCharacters_currentlyAccepted() throws Exception {
        assertEquals(
                "milk!",
                Parser.parseTaskKeyWord("find milk!")
        );
    }
}

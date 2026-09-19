package nori.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nori.NoriException;
import nori.task.DateRange;

/**
 * Tests the parsing of command input and date arguments.
 */
public class ParserTest {

    @Test
    public void parse_supportedKeywords_returnsMatchingCommands() {
        assertInstanceOf(TodoCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(DeadlineCommand.class, Parser.parse("deadline return book /by 2019-12-02"));
        assertInstanceOf(EventCommand.class, Parser.parse("event meeting /from 2pm /to 4pm"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-10-15"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_commandWithExtraSpacing_trimsCommandDetails() {
        TodoCommand command = assertInstanceOf(TodoCommand.class, Parser.parse("todo    read book"));

        assertEquals("read book", command.details);
    }

    @Test
    public void parse_keywordPrefix_returnsUnknownCommand() {
        assertInstanceOf(UnknownCommand.class, Parser.parse("todos read book"));
    }

    @Test
    public void parse_byeWithDetails_returnsUnknownCommand() {
        assertInstanceOf(UnknownCommand.class, Parser.parse("bye now"));
    }

    @Test
    public void parse_emptyInput_returnsUnknownCommand() {
        assertInstanceOf(UnknownCommand.class, Parser.parse(""));
    }

    @Test
    public void parse_keywordWithoutSeparatingSpace_returnsUnknownCommand() {
        assertInstanceOf(UnknownCommand.class, Parser.parse("mark1"));
        assertInstanceOf(UnknownCommand.class, Parser.parse("listing"));
    }

    @Test
    public void parse_nonByeKeywordWithDetails_returnsMatchingCommand() {
        assertInstanceOf(HelpCommand.class, Parser.parse("help me"));
        assertInstanceOf(ListCommand.class, Parser.parse("list /from 2019-01-01 /to 2021-01-01"));
    }

    @Test
    public void parse_keywordOnly_returnsCommandWithEmptyDetails() {
        ListCommand command = assertInstanceOf(ListCommand.class, Parser.parse("list"));

        assertEquals("", command.details);
    }

    @Test
    public void parse_findWithKeyword_keepsKeywordAsDetails() {
        FindCommand command = assertInstanceOf(FindCommand.class, Parser.parse("find book"));

        assertEquals("book", command.details);
    }

    @Test
    public void parse_findWithMultiWordKeyword_keepsWholePhrase() {
        FindCommand command = assertInstanceOf(FindCommand.class, Parser.parse("find return book"));

        assertEquals("return book", command.details);
    }

    @Test
    public void parse_bareFind_returnsFindCommandWithEmptyDetails() {
        FindCommand command = assertInstanceOf(FindCommand.class, Parser.parse("find"));

        assertEquals("", command.details);
    }

    @Test
    public void parse_findPrefix_returnsUnknownCommand() {
        assertInstanceOf(UnknownCommand.class, Parser.parse("finder book"));
    }

    @Test
    public void parseDate_validDate_returnsParsedDate() throws NoriException {
        assertEquals(LocalDate.of(2019, 10, 15), Parser.parseDate("2019-10-15"));
    }

    @Test
    public void parseDate_validLeapDay_returnsParsedDate() throws NoriException {
        assertEquals(LocalDate.of(2024, 2, 29), Parser.parseDate("2024-02-29"));
    }

    @Test
    public void parseDate_emptyInput_throwsHelpfulException() {
        NoriException exception = assertThrows(NoriException.class, () -> Parser.parseDate(""));

        assertEquals("NOOT?! \"on\" needs a date. Try \"on 2019-10-15\".", exception.getMessage());
    }

    @Test
    public void parseDate_nonIsoDate_throwsHelpfulException() {
        NoriException exception = assertThrows(NoriException.class, () -> Parser.parseDate("15-10-2019"));

        assertEquals("NOOT?! I cannot understand \"15-10-2019\" as a date."
                + " Use a date like \"2019-10-15\".", exception.getMessage());
    }

    @Test
    public void parseDate_impossibleDate_throwsHelpfulException() {
        NoriException exception = assertThrows(NoriException.class, () -> Parser.parseDate("2023-02-29"));

        assertEquals("NOOT?! I cannot understand \"2023-02-29\" as a date."
                + " Use a date like \"2019-10-15\".", exception.getMessage());
    }

    @Test
    public void parseListDateRange_validRange_returnsInclusiveBounds() throws NoriException {
        DateRange range = Parser.parseListDateRange("/from 2019-01-01 /to 2021-01-01");

        assertEquals(LocalDate.of(2019, 1, 1), range.getFrom());
        assertEquals(LocalDate.of(2021, 1, 1), range.getTo());
    }

    @Test
    public void parseListDateRange_sameStartAndEnd_returnsSingleDayRange() throws NoriException {
        DateRange range = Parser.parseListDateRange("/from 2024-02-29 /to 2024-02-29");

        assertEquals(LocalDate.of(2024, 2, 29), range.getFrom());
        assertEquals(LocalDate.of(2024, 2, 29), range.getTo());
    }

    @Test
    public void parseListDateRange_unexpectedDetails_throwsHelpfulException() {
        assertRangeParsingFails("unexpected details",
                "NOOT?! Use either \"list\" or \"list /from 2019-01-01 /to 2021-01-01\".");
    }

    @Test
    public void parseListDateRange_missingToSeparator_throwsHelpfulException() {
        assertRangeParsingFails("/from 2019-01-01",
                "NOOT?! A date-range list needs \"/to\" and an end date."
                        + " Try \"list /from 2019-01-01 /to 2021-01-01\".");
    }

    @Test
    public void parseListDateRange_missingStartDate_throwsHelpfulException() {
        assertRangeParsingFails("/from  /to 2021-01-01",
                "NOOT?! \"/from\" needs a start date."
                        + " Try \"list /from 2019-01-01 /to 2021-01-01\".");
    }

    @Test
    public void parseListDateRange_missingEndDate_throwsHelpfulException() {
        assertRangeParsingFails("/from 2019-01-01 /to ",
                "NOOT?! \"/to\" needs an end date."
                        + " Try \"list /from 2019-01-01 /to 2021-01-01\".");
    }

    @Test
    public void parseListDateRange_invalidStartDate_throwsHelpfulException() {
        assertRangeParsingFails("/from 2019-02-29 /to 2021-01-01",
                "NOOT?! I cannot understand \"2019-02-29\" as the /from date."
                        + " Use a date like \"2019-10-15\".");
    }

    @Test
    public void parseListDateRange_invalidEndDate_throwsHelpfulException() {
        assertRangeParsingFails("/from 2019-01-01 /to tomorrow",
                "NOOT?! I cannot understand \"tomorrow\" as the /to date."
                        + " Use a date like \"2019-10-15\".");
    }

    @Test
    public void parseListDateRange_endBeforeStart_throwsHelpfulException() {
        assertRangeParsingFails("/from 2021-01-02 /to 2021-01-01",
                "NOOT?! The \"/to\" date cannot be before the \"/from\" date. Time only waddles forward.");
    }

    @Test
    public void parse_blankInput_returnsNudge() {
        assertEquals("Noot? You didn't say anything."
                + " Type \"help\" and I'll show you what my flippers can do.", getRefusal(""));
    }

    @Test
    public void parse_keywordInCapitals_suggestsTheLowercaseKeyword() {
        assertEquals("NOOT?! My commands are all lowercase. Did you mean \"todo\"?",
                getRefusal("TODO read book"));
        assertEquals("NOOT?! My commands are all lowercase. Did you mean \"deadline\"?",
                getRefusal("Deadline report /by 2019-10-15"));
    }

    @Test
    public void parse_misspeltKeyword_suggestsTheNearestKeyword() {
        assertEquals("NOOT?! I do not know \"todos\". Did you mean \"todo\"?",
                getRefusal("todos read book"));
        assertEquals("NOOT?! I do not know \"listen\". Did you mean \"list\"?",
                getRefusal("listen"));
        assertEquals("NOOT?! I do not know \"marker\". Did you mean \"mark\"?",
                getRefusal("marker 1"));
        assertEquals("NOOT?! I do not know \"delet\". Did you mean \"delete\"?",
                getRefusal("delet 1"));
    }

    @Test
    public void parse_keywordFollowedByJunk_reportsTheKeywordTakesNothing() {
        assertEquals("NOOT?! \"bye\" takes nothing after it. Type \"bye\" on its own.",
                getRefusal("bye now"));
    }

    @Test
    public void parse_wordResemblingNoKeyword_reportsEveryCommand() {
        String everyCommand = "CONFUSED NOOT! My flippers do not understand that command."
                + " Try todo, deadline, event, on, list, find, mark, unmark, delete, help, or bye.";

        assertEquals(everyCommand, getRefusal("xyzzy frobnicate"));
        assertEquals(everyCommand, getRefusal("l"));
    }

    @Test
    public void parse_commandAtTheLengthLimit_returnsTheCommand() {
        String input = "todo " + "a".repeat(495);

        assertEquals(500, input.length());
        assertInstanceOf(TodoCommand.class, Parser.parse(input));
    }

    @Test
    public void parse_commandPastTheLengthLimit_returnsRefusal() {
        String input = "todo " + "a".repeat(496);

        assertEquals(501, input.length());
        assertEquals("GIANT NOOT! That command is 501 characters long."
                + " Keep it to 500 or fewer; my flippers are small.", getRefusal(input));
    }

    @Test
    public void parse_commandHidingATab_returnsRefusal() {
        assertEquals("NOOT?! That command hides a tab or control character."
                + " My flippers read plain text only.", getRefusal("todo read\tbook"));
    }

    @Test
    public void parseListDateRange_repeatedEndDate_throwsRepeatedOption() {
        assertRangeParsingFails("/from 2019-01-01 /to 2019-05-05 /to 2019-09-09",
                "NOOT?! A date-range list takes only one \"/to\"."
                        + " Try \"list /from 2019-01-01 /to 2021-01-01\".");
    }

    @Test
    public void parseListDateRange_deadlineOption_throwsUnexpectedOption() {
        assertRangeParsingFails("/from 2019-01-01 /to 2019-05-05 /by 2020-01-01",
                "NOOT?! A date-range list does not use \"/by\"."
                        + " Try \"list /from 2019-01-01 /to 2021-01-01\".");
    }

    /**
     * Runs a command that refuses its input and returns the reason it gave.
     *
     * @param input the input to parse and run.
     * @return the refusal message.
     */
    private static String getRefusal(String input) {
        NoriException exception = assertThrows(
                NoriException.class, () -> Parser.parse(input).execute(null, null, null));
        return exception.getMessage();
    }

    /**
     * Verifies that parsing a date range fails with the expected user-facing message.
     *
     * @param input the date-range details to parse.
     * @param expectedMessage the expected error message.
     */
    private static void assertRangeParsingFails(String input, String expectedMessage) {
        NoriException exception = assertThrows(NoriException.class, () -> Parser.parseListDateRange(input));

        assertEquals(expectedMessage, exception.getMessage());
    }
}

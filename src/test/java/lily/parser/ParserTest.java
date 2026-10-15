package lily.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import lily.exception.LilyException;
import lily.task.Event;
import lily.task.Task;
import org.junit.jupiter.api.Test;

/**
 * Tests Lily's central command tokenizer and task-index validation.
 *
 * <p>These parser methods are pure functions: given the same input they always produce
 * the same output, have no side effects, and have clearly enumerable command-format
 * edge cases. That makes them valuable to test directly without mocks or fixtures.
 */
public class ParserTest {

    @Test
    public void parseCommand_mixedCaseCommand_normalizesCommandAndPreservesArguments() throws LilyException {
        Parser.ParsedCommand command = Parser.parseCommand("ToDo Read Java Book");

        assertEquals("todo", command.commandWord());
        assertEquals("Read Java Book", command.arguments());
    }

    @Test
    public void parseCommand_commandWithoutArguments_returnsEmptyArgument() throws LilyException {
        Parser.ParsedCommand command = Parser.parseCommand("LIST");

        assertEquals("list", command.commandWord());
        assertEquals("", command.arguments());
    }

    @Test
    public void parseCommand_leadingWhitespace_ignored() throws LilyException {
        Parser.ParsedCommand command = Parser.parseCommand("  list");

        assertEquals("list", command.commandWord());
        assertEquals("", command.arguments());
    }

    @Test
    public void parseCommand_trailingWhitespace_ignored() throws LilyException {
        Parser.ParsedCommand command = Parser.parseCommand("todo read book  ");

        assertEquals("todo", command.commandWord());
        assertEquals("read book", command.arguments());
    }

    @Test
    public void parseCommand_repeatedSpacesInTaskDescription_preserved() throws LilyException {
        Parser.ParsedCommand command = Parser.parseCommand("todo  read  book");

        assertEquals("todo", command.commandWord());
        assertEquals("read  book", command.arguments());
    }

    @Test
    public void parseCommand_repeatedSpacesInNonTaskCommand_exceptionThrown() {
        assertSpacingError("mark  1");
    }

    @Test
    public void parseCommand_tabInsteadOfSpace_exceptionThrown() {
        assertSpacingError("todo\tread book");
    }

    @Test
    public void parseTodo_blankDescription_exceptionThrown() {
        LilyException thrown = assertThrows(LilyException.class, () -> Parser.parseTodo("todo"));

        assertEquals("This task needs a little label before I can plant it. "
                + "Add a description after todo.", thrown.getMessage());
    }

    @Test
    public void parseTodo_controlCharacterInDescription_exceptionThrown() {
        LilyException thrown = assertThrows(LilyException.class,
                () -> Parser.parseTodo("todo inspect\u0000report"));

        assertEquals("Task descriptions cannot contain line breaks or control characters.",
                thrown.getMessage());
    }

    @Test
    public void parseTodo_unicodeAndPunctuation_preserved() throws LilyException {
        Task task = Parser.parseTodo("todo Buy café snacks: 茶, cake & milk!");

        assertEquals("Buy café snacks: 茶, cake & milk!", task.getDescription());
    }

    @Test
    public void parseTodo_repeatedSpacesInDescription_preserved() throws LilyException {
        Task task = Parser.parseTodo("todo read  book");

        assertEquals("read  book", task.getDescription());
    }

    @Test
    public void parseDeadline_singleByClause_parsedSuccessfully() throws LilyException {
        Task task = Parser.parseDeadline("deadline Submit report /by 2026-09-20 1800");

        assertEquals("Submit report", task.getDescription());
    }

    @Test
    public void parseDeadline_repeatedSpacesInDescription_preserved() throws LilyException {
        Task task = Parser.parseDeadline("deadline Submit  report  /by 2026-09-20 1800");

        assertEquals("Submit  report", task.getDescription());
    }

    @Test
    public void parseDeadline_missingOrDuplicatedByClause_exceptionThrown() {
        assertThrows(LilyException.class,
                () -> Parser.parseDeadline("deadline Submit report 2026-09-20"));
        assertThrows(LilyException.class,
                () -> Parser.parseDeadline("deadline Submit /by report /by 2026-09-20"));
    }

    @Test
    public void parseDeadline_misplacedByClause_exceptionThrown() {
        LilyException noDescription = assertThrows(LilyException.class,
                () -> Parser.parseDeadline("deadline /by 2026-09-20"));
        LilyException noDate = assertThrows(LilyException.class,
                () -> Parser.parseDeadline("deadline Submit report /by"));

        assertEquals("This deadline needs a little label before I can plant it. "
                + "Add a description before /by.", noDescription.getMessage());
        assertEquals("Add a date/time after '/by'.", noDate.getMessage());
    }

    @Test
    public void parseEvent_clausesExactlyOnceAndInOrder_parsedSuccessfully() throws LilyException {
        Event event = assertInstanceOf(Event.class, Parser.parseEvent(
                "event Pair programming /from 2026-09-20 1400 /to 2026-09-20 1600"));

        assertEquals("Pair programming", event.getDescription());
    }

    @Test
    public void parseEvent_repeatedSpacesInDescription_preserved() throws LilyException {
        Event event = assertInstanceOf(Event.class, Parser.parseEvent(
                "event Pair  programming  /from 2026-09-20 1400 /to 2026-09-20 1600"));

        assertEquals("Pair  programming", event.getDescription());
    }

    @Test
    public void parseEvent_blankDescription_exceptionThrown() {
        LilyException thrown = assertThrows(LilyException.class,
                () -> Parser.parseEvent("event /from 2026-09-20 1400 /to 2026-09-20 1600"));

        assertEquals("This event needs a little label before I can plant it. "
                + "Add a description before /from.", thrown.getMessage());
    }

    @Test
    public void parseEvent_equalStartAndEnd_accepted() throws LilyException {
        Event event = assertInstanceOf(Event.class, Parser.parseEvent(
                "event Instant reminder /from 2026-09-20 1400 /to 2026-09-20 1400"));

        assertEquals(LocalDateTime.of(2026, 9, 20, 14, 0), event.getFrom());
        assertEquals(event.getFrom(), event.getTo());
    }

    @Test
    public void parseEvent_missingOrDuplicatedClause_exceptionThrown() {
        assertThrows(LilyException.class,
                () -> Parser.parseEvent("event Meeting /from 2026-09-20"));
        assertThrows(LilyException.class, () -> Parser.parseEvent(
                "event Meeting /from 2026-09-20 /from 2026-09-21 /to 2026-09-22"));
        assertThrows(LilyException.class, () -> Parser.parseEvent(
                "event Meeting /from 2026-09-20 /to 2026-09-21 /to 2026-09-22"));
    }

    @Test
    public void parseEvent_toBeforeFromClause_exceptionThrown() {
        LilyException thrown = assertThrows(LilyException.class, () -> Parser.parseEvent(
                "event Meeting /to 2026-09-21 /from 2026-09-20"));

        assertEquals("Place '/from' before '/to': "
                + "event <description> /from <start> /to <end>.", thrown.getMessage());
    }

    @Test
    public void parseEvent_missingClauseValue_exceptionThrown() {
        LilyException noStart = assertThrows(LilyException.class,
                () -> Parser.parseEvent("event Meeting /from /to 2026-09-21"));
        LilyException noEnd = assertThrows(LilyException.class,
                () -> Parser.parseEvent("event Meeting /from 2026-09-20 /to"));

        assertEquals("Add a date/time after '/from'.", noStart.getMessage());
        assertEquals("Add a date/time after '/to'.", noEnd.getMessage());
    }

    @Test
    public void parseEvent_extraTextAfterEndDate_exceptionThrown() {
        assertThrows(LilyException.class, () -> Parser.parseEvent(
                "event Meeting /from 2026-09-20 /to 2026-09-21 unexpected"));
    }

    @Test
    public void parseTaskIndex_singleDigit_returnsZeroBasedIndex() throws LilyException {
        assertEquals(0, Parser.parseTaskIndex("1"));
    }

    @Test
    public void parseTaskIndex_multiDigit_returnsZeroBasedIndex() throws LilyException {
        assertEquals(4, Parser.parseTaskIndex("5"));
        assertEquals(41, Parser.parseTaskIndex("42"));
    }

    @Test
    public void parseTaskIndex_leadingZeros_parsedAsDecimal() throws LilyException {
        assertEquals(6, Parser.parseTaskIndex("007"));
    }

    @Test
    public void parseTaskIndex_explicitPlusSign_exceptionThrown() {
        assertInvalidTaskNumber("+5");
    }

    @Test
    public void parseTaskIndex_zero_exceptionThrown() {
        assertInvalidTaskNumber("0");
    }

    @Test
    public void parseTaskIndex_negativeNumber_exceptionThrown() {
        assertInvalidTaskNumber("-5");
    }

    @Test
    public void parseTaskIndex_nonNumericText_exceptionThrown() {
        assertInvalidTaskNumber("abc");
    }

    @Test
    public void parseTaskIndex_emptyString_exceptionThrown() {
        assertInvalidTaskNumber("");
    }

    @Test
    public void parseTaskIndex_blankWhitespaceOnly_exceptionThrown() {
        assertInvalidTaskNumber(" ");
    }

    @Test
    public void parseTaskIndex_surroundingWhitespace_exceptionThrown() {
        assertInvalidTaskNumber(" 5 ");
    }

    @Test
    public void parseTaskIndex_decimalNumber_exceptionThrown() {
        assertInvalidTaskNumber("1.5");
    }

    @Test
    public void parseTaskIndex_valueOverflowsInt_exceptionThrown() {
        LilyException thrown = assertThrows(LilyException.class,
                () -> Parser.parseTaskIndex("99999999999999999999"));
        assertEquals("That task number is too large.", thrown.getMessage());
    }

    @Test
    public void parseTaskIndex_nullArgument_exceptionThrown() {
        assertInvalidTaskNumber(null);
    }

    private static void assertSpacingError(String input) {
        LilyException thrown = assertThrows(LilyException.class, () -> Parser.parseCommand(input));
        assertEquals("Those spaces are a little tangled. "
                + "Use single spaces between command arguments.", thrown.getMessage());
    }

    private static void assertInvalidTaskNumber(String input) {
        LilyException thrown = assertThrows(LilyException.class, () -> Parser.parseTaskIndex(input));
        assertEquals("I need one positive whole-number task number. "
                + "Check list to find the right one.", thrown.getMessage());
    }
}

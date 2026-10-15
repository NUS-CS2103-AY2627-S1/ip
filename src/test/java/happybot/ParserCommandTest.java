package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ParserCommandTest {
    private static final String BYE_USAGE = "Use: bye.";

    private static final String LIST_USAGE = "Use: list.";

    private static final String STATS_USAGE = "Use: stats.";

    private static final String TASK_NUMBER_MESSAGE = "Please provide one positive whole task number.";

    private static final String EMPTY_COMMAND_MESSAGE = "Please enter a command.";

    // ==================== validateCommandInput ====================

    @Test
    public void validateCommandInput_nullOrWhitespace_exceptionThrown() {
        HappyBotException nullError = assertThrows(HappyBotException.class,
                () -> Parser.validateCommandInput(null));
        HappyBotException whitespaceError = assertThrows(HappyBotException.class,
                () -> Parser.validateCommandInput(" \t "));

        assertEquals(EMPTY_COMMAND_MESSAGE, nullError.getMessage());
        assertEquals(EMPTY_COMMAND_MESSAGE, whitespaceError.getMessage());
    }

    @Test
    public void validateCommandInput_commandWithWhitespace_noExceptionThrown() throws HappyBotException {
        Parser.validateCommandInput("  todo\tread book  ");
    }

    // ==================== parseCommandWord ====================

    @Test
    public void parseCommandWord_commandWithBody_firstWordReturned() {
        assertEquals("deadline", Parser.parseCommandWord("deadline return book /by 2019-12-02"));
    }

    @Test
    public void parseCommandWord_commandWithoutBody_wholeLineReturned() {
        assertEquals("list", Parser.parseCommandWord("list"));
    }

    @Test
    public void parseCommandWord_emptyInput_emptyStringReturned() {
        assertEquals("", Parser.parseCommandWord(""));
    }

    @Test
    public void parseCommandWord_nullInput_emptyStringReturned() {
        assertEquals("", Parser.parseCommandWord(null));
    }

    // ==================== parseCommandBody ====================

    @Test
    public void parseCommandBody_commandWithBody_textAfterFirstSpaceReturned() {
        assertEquals("read book", Parser.parseCommandBody("todo read book"));
    }

    @Test
    public void parseCommandBody_commandWithoutBody_emptyStringReturned() {
        assertEquals("", Parser.parseCommandBody("list"));
    }

    @Test
    public void parseCommandBody_bodyHoldsSpaces_spacesKept() {
        // Only the first space is consumed, so the rest of the line reaches the command untouched.
        assertEquals("return book /by 2019-12-02",
                Parser.parseCommandBody("deadline return book /by 2019-12-02"));
    }

    @Test
    public void parseCommandBody_nullInput_emptyStringReturned() {
        assertEquals("", Parser.parseCommandBody(null));
    }

    // ==================== validateByeCommand and validateListCommand ====================

    @Test
    public void validateNoArgumentCommands_blankOrWhitespaceBody_noExceptionThrown() throws HappyBotException {
        Parser.validateByeCommand("");
        Parser.validateByeCommand(" \t ");
        Parser.validateListCommand("");
        Parser.validateListCommand(" \t ");
    }

    @Test
    public void validateByeCommand_argumentGiven_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.validateByeCommand("now"));

        assertEquals(BYE_USAGE, e.getMessage());
    }

    @Test
    public void validateListCommand_argumentGiven_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.validateListCommand("now"));

        assertEquals(LIST_USAGE, e.getMessage());
    }

    // ==================== parseTaskNumber ====================

    @Test
    public void parseTaskNumber_numberWithSurroundingSpaces_numberReturned() throws HappyBotException {
        assertEquals(2, Parser.parseTaskNumber("  2  "));
    }

    @Test
    public void parseTaskNumber_zeroOrNegativeNumber_exceptionThrown() {
        HappyBotException zeroError = assertThrows(HappyBotException.class,
                () -> Parser.parseTaskNumber("0"));
        HappyBotException negativeError = assertThrows(HappyBotException.class,
                () -> Parser.parseTaskNumber("-1"));

        assertEquals(TASK_NUMBER_MESSAGE, zeroError.getMessage());
        assertEquals(TASK_NUMBER_MESSAGE, negativeError.getMessage());
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseTaskNumber("two"));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void parseTaskNumber_decimalNumber_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseTaskNumber("2.5"));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void parseTaskNumber_multipleArguments_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseTaskNumber("1 2"));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void parseTaskNumber_emptyBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseTaskNumber(""));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void parseTaskNumber_numberTooLargeForInteger_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseTaskNumber("999999999999999999999999"));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    // ==================== validateStatsCommand ====================

    @Test
    public void validateStatsCommand_noArgument_noExceptionThrown() throws HappyBotException {
        Parser.validateStatsCommand("");
    }

    @Test
    public void validateStatsCommand_spacesOnly_noExceptionThrown() throws HappyBotException {
        Parser.validateStatsCommand("   ");
    }

    @Test
    public void validateStatsCommand_argumentGiven_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.validateStatsCommand("week"));

        assertEquals(STATS_USAGE, e.getMessage());
    }
}

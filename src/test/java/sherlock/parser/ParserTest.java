package sherlock.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sherlock.command.Command;
import sherlock.exception.SherlockException;

/** Tests conversion of user input into Sherlock commands. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTodo_createsAddCommandWithTask() throws SherlockException {
        Command command = parser.parse("todo review clues", 0);

        assertEquals(Command.Type.ADD, command.getType());
        assertEquals("[T][ ] review clues", command.getTask().toString());
    }

    @Test
    void parseCommandWithOuterWhitespace_ignoresAccidentalSpacing() throws SherlockException {
        Command command = parser.parse("   todo review clues   ", 0);

        assertEquals(Command.Type.ADD, command.getType());
        assertEquals("[T][ ] review clues", command.getTask().toString());
    }

    @Test
    void parseDeadlineWithExtraDelimiterSpacing_createsDeadline() throws SherlockException {
        Command command = parser.parse("deadline return book   /by   2026-09-18", 0);

        assertEquals(Command.Type.ADD, command.getType());
        assertEquals("[D][ ] return book (by: Sep 18 2026)", command.getTask().toString());
    }

    @Test
    void parseMark_usesOneBasedTaskNumber() throws SherlockException {
        Command command = parser.parse("mark 2", 2);

        assertEquals(Command.Type.MARK, command.getType());
        assertEquals(2, command.getTaskNumber());
    }

    @Test
    void parseFind_createsFindCommandWithKeyword() throws SherlockException {
        Command command = parser.parse("find book", 0);

        assertEquals(Command.Type.FIND, command.getType());
        assertEquals("book", command.getKeyword());
    }

    @Test
    void parseHelp_createsHelpCommand() throws SherlockException {
        Command command = parser.parse("help", 0);

        assertEquals(Command.Type.HELP, command.getType());
    }

    @Test
    void parseTodoWithoutDescription_throwsHelpfulException() {
        SherlockException exception = assertThrows(
                SherlockException.class, () -> parser.parse("todo", 0));

        assertEquals("I need a case description before I can add it.", exception.getMessage());
    }

    @Test
    void parseFindWithoutKeyword_throwsHelpfulException() {
        SherlockException exception = assertThrows(
                SherlockException.class, () -> parser.parse("find", 0));

        assertEquals("I need a keyword to search the casebook.", exception.getMessage());
    }

    @Test
    void parseDeadlineWithRepeatedBy_throwsHelpfulException() {
        String input = "deadline return book /by 2026-09-18 /by 2026-09-19";
        SherlockException exception = assertThrows(
                SherlockException.class, () -> parser.parse(input, 0));

        assertEquals("A deadline must include exactly one /by followed by a date.", exception.getMessage());
    }

    @Test
    void parseEventWithoutTo_throwsHelpfulException() {
        SherlockException exception = assertThrows(
                SherlockException.class, () -> parser.parse("event interview /from 2pm", 0));

        assertEquals("An event must include exactly one /to followed by an end time.", exception.getMessage());
    }
}

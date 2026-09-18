package geek.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import geek.exception.GeekException;
import geek.parser.Parser.Command;
import geek.parser.Parser.CommandType;

/**
 * Tests the conversion of user input into executable commands.
 */
class ParserTest {
    @Test
    void parse_simpleCommands_returnsMatchingCommandTypes() {
        Command byeCommand = Parser.parse("bye");
        Command listCommand = Parser.parse("list");
        Command sortCommand = Parser.parse("sort");

        assertAll(() -> assertEquals(
                        CommandType.BYE,
                        byeCommand.type()
                ), () -> assertEquals(
                        CommandType.LIST,
                        listCommand.type()
                ), () -> assertEquals(
                        CommandType.SORT,
                        sortCommand.type()
                )
        );
    }

    @Test
    void parse_extraWhitespace_acceptsOtherwiseValidCommands() {
        Command listCommand = Parser.parse("  list\t ");
        Command markCommand = Parser.parse("\tmark\t3  ");

        assertAll(() -> assertEquals(
                        CommandType.LIST,
                        listCommand.type()
                ), () -> assertEquals(
                        CommandType.MARK,
                        markCommand.type()
                ), () -> assertEquals(
                        3,
                        markCommand.taskNumber()
                )
        );
    }

    @Test
    void parse_simpleCommandsWithArguments_throwSpecificExceptions() {
        GeekException byeException = assertThrows(
                GeekException.class, () -> Parser.parse("bye now")
        );
        GeekException listException = assertThrows(
                GeekException.class, () -> Parser.parse("list all")
        );

        assertAll(() -> assertEquals(
                        "The bye command does not accept arguments.",
                        byeException.getMessage()
                ), () -> assertEquals(
                        "The list command does not accept arguments.",
                        listException.getMessage()
                )
        );
    }

    @Test
    void parse_sortWithArguments_throwsGeekException() {
        GeekException exception = assertThrows(
                GeekException.class, () -> Parser.parse("sort date")
        );

        assertEquals(
                "The sort command does not accept arguments.",
                exception.getMessage()
        );
    }

    @Test
    void parse_markWithValidTaskNumber_returnsMarkCommand() {
        Command command = Parser.parse("mark 3");

        assertAll(() -> assertEquals(
                        CommandType.MARK,
                        command.type()
                ), () -> assertEquals(3, command.taskNumber())
        );
    }

    @Test
    void parse_findWithKeyword_returnsFindCommand() {
        Command command = Parser.parse("find   BOOK");

        assertAll(() -> assertEquals(
                        CommandType.FIND,
                        command.type()
                ), () -> assertEquals("BOOK", command.keyword())
        );
    }

    @Test
    void parse_findWithoutKeyword_throwsGeekException() {
        GeekException exception = assertThrows(
                GeekException.class, () -> Parser.parse("find   ")
        );

        assertEquals(
                "Please provide a keyword after find.",
                exception.getMessage()
        );
    }

    @Test
    void parse_todoWithDescription_returnsAddCommand() {
        Command command = Parser.parse("todo read book");

        assertAll(() -> assertEquals(
                        CommandType.ADD,
                        command.type()
                ), () -> assertEquals(
                        "[T][ ] read book",
                        command.task().toString()
                )
        );
    }

    @Test
    void parse_deadlineWithDateTime_returnsAddCommand() {
        Command command = Parser.parse(
                "deadline return book /by Dec 2 2019 6:00 PM"
        );

        assertAll(() -> assertEquals(
                        CommandType.ADD,
                        command.type()
                ), () -> assertEquals(
                        "[D][ ] return book "
                                + "(by: Dec 2 2019, 6:00 PM)",
                        command.task().toString()
                )
        );
    }

    @Test
    void parse_deadlineWithDuplicateByDelimiter_throwsSpecificException() {
        GeekException exception = assertThrows(
                GeekException.class, () -> Parser.parse(
                        "deadline return book /by 2/12/2019 "
                                + "/by 3/12/2019"
                )
        );

        assertEquals(
                "Use /by only once in a deadline command.",
                exception.getMessage()
        );
    }

    @Test
    void parse_deadlineWithSimilarText_doesNotMistakeTextForDelimiter() {
        Command command = Parser.parse(
                "deadline review /bypass rules /by 2/12/2019"
        );

        assertEquals(
                "[D][ ] review /bypass rules (by: Dec 2 2019)",
                command.task().toString()
        );
    }

    @Test
    void parse_eventWithValidRange_returnsAddCommand() {
        Command command = Parser.parse(
                "event project meeting "
                        + "/from 2/12/2019 1800 "
                        + "/to 2/12/2019 2000"
        );

        assertAll(() -> assertEquals(
                        CommandType.ADD,
                        command.type()
                ), () -> assertEquals(
                        "[E][ ] project meeting "
                                + "(from: Dec 2 2019, 6:00 PM "
                                + "to: Dec 2 2019, 8:00 PM)",
                        command.task().toString()
                )
        );
    }

    @Test
    void parse_eventWithDuplicateDelimiter_throwsSpecificException() {
        GeekException exception = assertThrows(
                GeekException.class, () -> Parser.parse(
                        "event meeting /from 2/12/2019 1800 "
                                + "/from 2/12/2019 1900 "
                                + "/to 2/12/2019 2000"
                )
        );

        assertEquals(
                "Use /from and /to only once in an event command.",
                exception.getMessage()
        );
    }

    @Test
    void parse_onWithSupportedDate_returnsOnCommand() {
        Command command = Parser.parse("on Dec 2, 2019");

        assertAll(() -> assertEquals(
                        CommandType.ON,
                        command.type()
                ), () -> assertEquals(
                        LocalDate.of(2019, 12, 2),
                        command.date()
                )
        );
    }

    @Test
    void parse_blankInput_throwsGeekException() {
        GeekException exception = assertThrows(
                GeekException.class, () -> Parser.parse("   ")
        );

        assertEquals(
                "Please enter a command.",
                exception.getMessage()
        );
    }

    @Test
    void parse_markWithoutTaskNumber_throwsGeekException() {
        assertThrows(
                GeekException.class, () -> Parser.parse("mark")
        );
    }

    @Test
    void parse_markWithNonNumericTaskNumber_throwsGeekException() {
        GeekException exception = assertThrows(
                GeekException.class, () -> Parser.parse("mark three")
        );

        assertEquals(
                "Please enter a valid task number.",
                exception.getMessage()
        );
    }

    @Test
    void parse_deadlineWithoutByDelimiter_throwsGeekException() {
        assertThrows(
                GeekException.class, () -> Parser.parse("deadline return book")
        );
    }

    @Test
    void parse_eventEndingAtStartTime_throwsGeekException() {
        assertThrows(
                GeekException.class, () -> Parser.parse(
                        "event meeting "
                                + "/from 2/12/2019 1800 "
                                + "/to 2/12/2019 1800"
                )
        );
    }

    @Test
    void parse_onWithInvalidDate_throwsGeekException() {
        assertThrows(
                GeekException.class, () -> Parser.parse("on 2019-02-29")
        );
    }

    @Test
    void parse_unknownCommand_throwsGeekException() {
        assertThrows(
                GeekException.class, () -> Parser.parse("sing a song")
        );
    }
}

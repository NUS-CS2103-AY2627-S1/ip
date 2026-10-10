package jasper.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import jasper.JasperException;
import jasper.command.ByeCommand;
import jasper.command.DeadlineCommand;
import jasper.command.DeleteCommand;
import jasper.command.EventCommand;
import jasper.command.ListCommand;
import jasper.command.MarkCommand;
import jasper.command.TodoCommand;
import jasper.command.UnmarkCommand;

class ParserTest {
    @Test
    public void parseCmd_knownCommands_success() {
        try {
            assertInstanceOf(ByeCommand.class, Parser.parseCmd("bye"));
            assertInstanceOf(DeadlineCommand.class, Parser.parseCmd("deadline return book /by 2014-07-23 21:49"));
            assertInstanceOf(DeleteCommand.class, Parser.parseCmd("delete 1"));
            assertInstanceOf(EventCommand.class,
                    Parser.parseCmd("event play /from 2018-03-19 22:30 /to 2018-03-20 05:27"));
            assertInstanceOf(ListCommand.class, Parser.parseCmd("list"));
            assertInstanceOf(MarkCommand.class, Parser.parseCmd("mark 2"));
            assertInstanceOf(TodoCommand.class, Parser.parseCmd("todo read"));
            assertInstanceOf(UnmarkCommand.class, Parser.parseCmd("unmark 3"));
        } catch (JasperException e) {
            fail();
        }
    }

    @Test
    public void parseCmd_whitespaceLeadingTrailingAndBetweenArguments_success() {
        try {
            assertInstanceOf(TodoCommand.class, Parser.parseCmd("   todo        buy milk   "));
        } catch (JasperException e) {
            fail();
        }
    }

    @Test
    void parseCmd_unknownCommand_exceptionThrown() {
        try {
            Parser.parseCmd("sample_unknown_command");
            fail();
        } catch (JasperException e) {
            assertEquals("Unknown command: sample_unknown_command", e.getMessage());
        }
    }

    @Test
    public void parseDateTime_validFormat_success() {
        try {
            assertEquals(
                    LocalDateTime.of(2021, 11, 9, 13, 28),
                    Parser.parseDateTime("2021-11-09 13:28")
            );
        } catch (JasperException e) {
            fail();
        }
    }

    @Test
    public void parseDateTime_invalidFormat_exceptionThrown() {
        Class<JasperException> cls = JasperException.class;
        assertThrows(cls, () -> Parser.parseDateTime("26-07-2025 13:28"));
        assertThrows(cls, () -> Parser.parseDateTime("26-07-2025"));
        assertThrows(cls, () -> Parser.parseDateTime("13:28"));
    }

    @Test
    public void parseDateTime_invalidDateTime_exceptionThrown() {
        Class<JasperException> cls = JasperException.class;
        assertThrows(cls, () -> Parser.parseDateTime("2025-02-31 13:28"));
        assertThrows(cls, () -> Parser.parseDateTime("2025-07-28 25:00"));
        assertThrows(cls, () -> Parser.parseDateTime("2025-07-12 13:60"));
    }
}

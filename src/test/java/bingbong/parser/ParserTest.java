package bingbong.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import bingbong.command.AddCommand;
import bingbong.command.ExitCommand;
import bingbong.command.ListCommand;
import bingbong.exception.BingBongException;

public class ParserTest {

    @Test
    public void parse_validCommands_returnsCorrectTypes() throws BingBongException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_unknownInputKeyword_exceptionThrown() {
        BingBongException exception = assertThrows(BingBongException.class, () -> {
            Parser.parse("invalidKeyword command string");
        });
        assertEquals("Unknown command. Please try again.", exception.getMessage());
    }

    @Test
    public void parse_invalidTodoInput_exceptionThrown() {
        assertThrows(BingBongException.class, () -> {
            Parser.parse("todo   ");
        });
    }

    @Test
    public void parseDeadline_missingByFlag_exceptionThrown() {
        assertThrows(BingBongException.class, () -> {
            Parser.parseDeadline("deadline buy eggs 24/08/2026");
        });
    }

    @Test
    public void parseDeadline_duplicateByFlags_exceptionThrown() {
        assertThrows(BingBongException.class, () -> {
            Parser.parseDeadline("deadline return books /by tonight /by tomorrow");
        });
    }

    @Test
    public void parseEvent_invalidEventFlags_exceptionThrown() {
        assertThrows(BingBongException.class, () -> {
            Parser.parseEvent("event career fair /from Monday");
        });
    }
}

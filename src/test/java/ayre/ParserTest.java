package ayre;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import ayre.exceptions.UnknownCommandException;
import ayre.exceptions.WrongNumberOfArgumentsException;

public class ParserTest {
    @Test
    public void parseInput_nonCommand_exceptionThrown() {
        assertThrows(UnknownCommandException.class, () -> Parser.parseInput("hello"));
        assertThrows(UnknownCommandException.class, () -> Parser.parseInput("bye123"));
        assertThrows(UnknownCommandException.class, () -> Parser.parseInput("by"));
        assertThrows(UnknownCommandException.class, () -> Parser.parseInput(" "));
    }

    @Test
    public void parseInput_validCommandAndArguments_success() throws Exception {
        assertEquals(0, Parser.parseInput("bye").args().size());
        assertEquals(0, Parser.parseInput("b").args().size());
        assertEquals(0, Parser.parseInput("list").args().size());
        assertEquals(0, Parser.parseInput("l").args().size());
        assertEquals(List.of("1"), Parser.parseInput("mark 1").args());
        assertEquals(List.of("1"), Parser.parseInput("m 1").args());
        assertEquals(List.of("1"), Parser.parseInput("unmark 1").args());
        assertEquals(List.of("1"), Parser.parseInput("u 1").args());
        assertEquals(List.of("1"), Parser.parseInput("delete 1").args());
        assertEquals(List.of("1"), Parser.parseInput("rm 1").args());
        assertEquals(List.of("name"), Parser.parseInput("todo name").args());
        assertEquals(List.of("name"), Parser.parseInput("t name").args());
        assertEquals(List.of("name", "2003-01-01"),
                Parser.parseInput("deadline name /by 2003-01-01").args());
        assertEquals(List.of("name", "2003-01-01"),
                Parser.parseInput("d name /by 2003-01-01").args());
        assertEquals(List.of("name", "2003-01-01", "2003-01-01"),
                Parser.parseInput("event name /from 2003-01-01 /to 2003-01-01").args());
        assertEquals(List.of("name", "2003-01-01", "2003-01-01"),
                Parser.parseInput("e name /from 2003-01-01 /to 2003-01-01").args());
    }

    @Test
    public void parseInput_extraArguments_exceptionThrown() {
        assertThrows(WrongNumberOfArgumentsException.class, () ->
                Parser.parseInput("bye bye"));
        assertThrows(WrongNumberOfArgumentsException.class, () ->
                Parser.parseInput("b 1"));
        assertThrows(WrongNumberOfArgumentsException.class, () ->
                Parser.parseInput("list 3"));
        assertThrows(WrongNumberOfArgumentsException.class, () ->
                Parser.parseInput("l l"));
    }

    @Test
    public void parseInput_invalidUseOfTaskTimeFlags_exceptionThrown() {
        assertThrows(WrongNumberOfArgumentsException.class, () -> Parser.parseInput(
                "deadline name /by 2003-01-01 /by 2003-01-01"));
        assertThrows(WrongNumberOfArgumentsException.class, () -> Parser.parseInput(
                "event name /from 2003-01-01 /from 2003-01-01"));
        assertThrows(WrongNumberOfArgumentsException.class, () -> Parser.parseInput(
                "event name /to 2003-01-01 /to 2003-01-01"));
    }
}

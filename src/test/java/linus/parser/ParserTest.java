package linus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import linus.invalidtaskexception.InvalidTaskException;

public class ParserTest {
    @Test
    public void parse_listCommand_listReturned() throws InvalidTaskException {
        Parser parser = new Parser();
        assertEquals(List.of("list"), parser.parse("list"));
    }

    @Test
    public void parse_invalidListCommandWithSpace_exceptionThrown() {
        Parser parser = new Parser();
        assertThrows(InvalidTaskException.class, () -> parser.parse("list "));
    }

    @Test
    public void parse_invalidListCommandWithTab_exceptionThrown() {
        Parser parser = new Parser();
        assertThrows(InvalidTaskException.class, () -> parser.parse("list   "));
    }

    @Test
    public void parse_invalidListCommandWithString_exceptionThrown() {
        Parser parser = new Parser();
        assertThrows(InvalidTaskException.class, () -> parser.parse("list all"));
    }

    @Test
    public void parse_invalidListCommandWithInt_exceptionThrown() {
        Parser parser = new Parser();
        assertThrows(InvalidTaskException.class, () -> parser.parse("list 3"));
    }

    @Test
    public void parse_invalidListCommandWithSpecialChar_exceptionThrown() {
        Parser parser = new Parser();
        assertThrows(InvalidTaskException.class, () -> parser.parse("list!"));
    }

    @Test
    public void parse_invalidListCommandWithNewLine_exceptionThrown() {
        Parser parser = new Parser();
        assertThrows(InvalidTaskException.class, () -> parser.parse("list\n"));
    }

    @Test
    public void parse_invalidCommand_exceptionThrown() {
        Parser parser = new Parser();
        assertThrows(InvalidTaskException.class, () -> parser.parse("deadline! finish increment /by 2026-08-28"));
    }
}

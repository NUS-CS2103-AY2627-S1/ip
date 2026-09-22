package wiz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void getCommandWord_commandWithArguments_extractedCorrectly() {
        assertEquals("todo", Parser.getCommandWord("todo read book"));
    }

    @Test
    public void getCommandWord_commandOnly_extractedCorrectly() {
        assertEquals("list", Parser.getCommandWord("list"));
    }

    @Test
    public void getCommandWord_emptyInput_emptyStringReturned() {
        assertEquals("", Parser.getCommandWord(""));
        assertEquals("", Parser.getCommandWord("   "));
        assertEquals("", Parser.getCommandWord("\t \n"));
    }

    @Test
    public void getCommandWord_mixedCaseAndWhitespace_normalizedToLowercase() {
        assertEquals("todo", Parser.getCommandWord("  TODO   read book  "));
        assertEquals("deadline", Parser.getCommandWord("\tDeAdLiNe return book /by 2026-10-10 1800 "));
        assertEquals("event", Parser.getCommandWord("EVENT project meeting"));
    }

    @Test
    public void getArguments_commandWithArguments_extractedCorrectly() {
        assertEquals("read book", Parser.getArguments("todo read book"));
        assertEquals("return book /by 2026-10-10 1800", Parser.getArguments("  deadline   return book /by 2026-10-10 1800  "));
    }

    @Test
    public void getArguments_commandOnly_emptyStringReturned() {
        assertEquals("", Parser.getArguments("list"));
        assertEquals("", Parser.getArguments("  list  "));
        assertEquals("", Parser.getArguments(""));
        assertEquals("", Parser.getArguments("   "));
    }
}

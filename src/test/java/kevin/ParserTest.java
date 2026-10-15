package kevin;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ParserTest {
    @Test
    public void parseDeadline_descriptionWithDate() throws KevinException {
        Parser parser = new Parser("deadline sleep /by 4/9/26 1130pm");
        Matcher matcher = parser.parseDeadline();

        assertEquals("sleep", matcher.group("description"));
        assertEquals("4/9/26 1130pm", matcher.group("by"));

        parser = new Parser("deadline sleep very long /by 17/12/26 1am");
        matcher = parser.parseDeadline();

        assertEquals("sleep very long", matcher.group("description"));
        assertEquals("17/12/26 1am", matcher.group("by"));
    }

    @Test
    public void parseDeadline_extraWhitespaces() throws KevinException {
        Parser parser = new Parser("     deadline      sleep  very   long     /by 4/9/26   130pm     ");
        Matcher matcher = parser.parseDeadline();

        assertEquals("sleep  very   long", matcher.group("description"));
        assertEquals("4/9/26   130pm", matcher.group("by"));
    }

    @Test
    public void parseDeadline_noDate_exceptionThrown() {
        Parser parser = new Parser("deadline sleep /by ");

        assertThrows(KevinException.class, () -> parser.parseDeadline());
    }

    @Test
    public void parseDeadline_emptyDescription_exceptionThrown() {
        Parser parserNoDescription = new Parser("deadline /by 4/9/26 130pm");

        assertThrows(KevinException.class, () -> parserNoDescription.parseDeadline());

        Parser parserWhitespaceAsDescription = new Parser("deadline        /by 4/9/26 130pm");

        assertThrows(KevinException.class, () -> parserWhitespaceAsDescription.parseDeadline());
    }

    @Test
    public void parseDeadline_noBackslash_exceptionThrown() {
        Parser parser = new Parser("deadline sleep by 4/9/26 130pm");

        assertThrows(KevinException.class, () -> parser.parseDeadline());
    }

    @Test
    public void parseDeadline_wrongKeyword_exceptionThrown() throws KevinException {
        Parser parser = new Parser("deadline sleep /bye 4/9/26 130pm");

        assertThrows(KevinException.class, () -> parser.parseDeadline());

        Parser newParser = new Parser("deadlines sleep /by 4/9/26 130pm");

        assertThrows(KevinException.class, () -> newParser.parseDeadline());
    }
}

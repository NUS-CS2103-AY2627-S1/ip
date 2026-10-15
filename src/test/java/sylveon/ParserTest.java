package sylveon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests splitting user commands into command words and arguments. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void getCommandWord_commandWithArguments_returnsFirstWord() {
        assertEquals("deadline", parser.getCommandWord("deadline submit report /by 2026-09-15"));
    }

    @Test
    public void getArguments_commandWithExtraSpaces_returnsTrimmedArguments() {
        assertEquals("submit report /by 2026-09-15",
                parser.getArguments("  deadline   submit report /by 2026-09-15  "));
    }

    @Test
    public void getArguments_commandWithoutArguments_returnsEmptyString() {
        assertEquals("", parser.getArguments("list"));
    }
}

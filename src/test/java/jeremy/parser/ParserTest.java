package jeremy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jeremy.exception.JeremyException;
import jeremy.task.Deadline;
import jeremy.task.Event;

public class ParserTest {

    private Parser parser;

    @BeforeEach
    public void setUp() {
        parser = new Parser();
    }

    @Test
    public void getCommandWord_lowerCasesAndStripsArguments() {
        assertEquals("todo", parser.getCommandWord("TODO read book"));
        assertEquals("list", parser.getCommandWord("list"));
    }

    @Test
    public void getArguments_returnsTextAfterFirstToken() {
        assertEquals("read book", parser.getArguments("todo read book"));
        assertEquals("", parser.getArguments("list"));
        assertEquals("read book", parser.getArguments("  todo   read book  "));
    }

    @Test
    public void parseIndex_validNumber_returnsParsedInt() throws JeremyException {
        assertEquals(3, parser.parseIndex("3", "mark"));
    }

    @Test
    public void parseIndex_emptyArgs_throwsWithActionWordInMessage() {
        JeremyException ex = assertThrows(JeremyException.class, () -> parser.parseIndex("", "delete"));
        assertTrue(ex.getMessage().contains("delete"));
    }

    @Test
    public void parseIndex_nonNumeric_throwsJeremyException() {
        assertThrows(JeremyException.class, () -> parser.parseIndex("abc", "mark"));
    }

    @Test
    public void parseDeadline_wellFormed_extractsDescriptionAndBy() throws JeremyException {
        Deadline deadline = parser.parseDeadline("return book /by Sunday");

        assertEquals("return book", deadline.getDescription());
        assertEquals("Sunday", deadline.getBy());
    }

    @Test
    public void parseDeadline_missingByClause_throwsJeremyException() {
        assertThrows(JeremyException.class, () -> parser.parseDeadline("return book"));
    }

    @Test
    public void parseDeadline_missingDescription_throwsJeremyException() {
        assertThrows(JeremyException.class, () -> parser.parseDeadline("/by Sunday"));
    }

    @Test
    public void parseDeadline_repeatedByParameter_throwsJeremyException() {
        assertThrows(JeremyException.class,
                () -> parser.parseDeadline("return book /by Sunday /by Monday"));
    }

    @Test
    public void parseDeadline_invalidDate_throwsJeremyException() {
        assertThrows(JeremyException.class,
                () -> parser.parseDeadline("return book /by 30/2/2025"));
    }

    @Test
    public void parseEvent_wellFormed_extractsAllThreeFields() throws JeremyException {
        Event event = parser.parseEvent("meeting /from Mon 2pm /to 4pm");

        assertEquals("meeting", event.getDescription());
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    public void parseEvent_missingToClause_throwsJeremyException() {
        assertThrows(JeremyException.class, () -> parser.parseEvent("meeting /from Mon 2pm"));
    }

    @Test
    public void parseEvent_repeatedParameter_throwsJeremyException() {
        assertThrows(JeremyException.class,
                () -> parser.parseEvent("meeting /from 2pm /from 3pm /to 4pm"));
    }

    @Test
    public void parseEvent_endNotAfterStart_throwsJeremyException() {
        assertThrows(JeremyException.class,
                () -> parser.parseEvent("meeting /from 4pm /to 4pm"));
    }

    @Test
    public void parseTodoDescription_empty_throwsJeremyException() {
        assertThrows(JeremyException.class, () -> parser.parseTodoDescription(""));
    }

    @Test
    public void parseTodoDescription_nonEmpty_returnsAsIs() throws JeremyException {
        assertEquals("read book", parser.parseTodoDescription("read book"));
    }
}

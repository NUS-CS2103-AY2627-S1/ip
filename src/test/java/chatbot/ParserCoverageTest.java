package chatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Covers parser branches for commands with structured arguments. */
class ParserCoverageTest {
    @Test
    void parser_extractsAllCommandArguments() {
        String event = "event  project sync /from 2026-09-02 1000 /to 2026-09-02 1100";
        assertEquals("event", Parser.getCommand(event));
        assertEquals("project sync", Parser.getEventDescription(event));
        assertEquals("2026-09-02 1000", Parser.getEventStart(event));
        assertEquals("2026-09-02 1100", Parser.getEventEnd(event));
        assertEquals("2026-09-02", Parser.getOnCommandDateText("on 2026-09-02"));
    }

    @Test
    void parser_rejectsMissingOrMalformedArguments() {
        assertThrows(InvalidInputException.class, () -> Parser.getMarkOrUnmarkTaskNumber("mark"));
        assertThrows(InvalidInputException.class, () -> Parser.getMarkOrUnmarkTaskNumber("mark nope"));
        assertThrows(InvalidInputException.class, () -> Parser.getDeleteTaskNumber("delete nope"));
        assertThrows(InvalidInputException.class, () -> Parser.getDeleteTaskNumber("delete"));
        assertThrows(InvalidInputException.class, () -> Parser.getDeadlineDescription("deadline task"));
        assertThrows(InvalidInputException.class, () -> Parser.getEventEnd("event meeting /from 2026-09-02 1000"));
        assertThrows(InvalidInputException.class, () -> Parser.getOnCommandDateText("on"));
    }
}

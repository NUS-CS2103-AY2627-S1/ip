package com.gihun456.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.gihun456.ErrorMessages;
import com.gihun456.GihunException;

public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_commandWithExtraWhitespace_returnsOperationAndArguments() throws GihunException {
        Parser.ParsedInput parsed = parser.parse("   deadline   Submit report /by 15/09/2026 1030  ");

        assertEquals(Operation.DEADLINE, parsed.getOperation());
        assertEquals("Submit report /by 15/09/2026 1030", parsed.getArguments());
    }

    @Test
    public void parse_blankInput_throwsExpectedError() {
        assertEquals(ErrorMessages.EMPTY_COMMAND, assertThrows(GihunException.class, () ->
                parser.parse("  ")).getMessage());
        assertEquals(ErrorMessages.EMPTY_COMMAND, assertThrows(GihunException.class, () ->
                parser.parse(null)).getMessage());
    }

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws GihunException {
        assertEquals("[D][ ] Submit report (by: Sep 15 2026, 10:30 AM)",
                parser.parseDeadline("Submit report /by 15/09/2026 1030").toString());
    }

    @Test
    public void parseDeadline_missingParts_throwsExpectedErrors() {
        assertEquals(ErrorMessages.DEADLINE_DESCRIPTION_EMPTY,
                assertThrows(GihunException.class, () -> parser.parseDeadline("")).getMessage());
        assertEquals(ErrorMessages.DEADLINE_BY_MISSING,
                assertThrows(GihunException.class, () -> parser.parseDeadline("Submit report")).getMessage());
        assertEquals(ErrorMessages.DEADLINE_DESCRIPTION_EMPTY,
                assertThrows(GihunException.class, () -> parser.parseDeadline("/by 15/09/2026")).getMessage());
        assertEquals(ErrorMessages.DEADLINE_DUE_DATE_EMPTY,
                assertThrows(GihunException.class, () -> parser.parseDeadline("Submit report /by")).getMessage());
    }

    @Test
    public void parseEvent_validInput_returnsEvent() throws GihunException {
        assertEquals("[E][ ] Meeting (from: Sep 15 2026, 10:00 AM to: Sep 15 2026, 11:00 AM)",
                parser.parseEvent("Meeting /from 15/09/2026 1000 /to 15/09/2026 1100").toString());
    }

    @Test
    public void parseEvent_missingParts_throwsExpectedErrors() {
        assertEquals(ErrorMessages.EVENT_DESCRIPTION_EMPTY,
                assertThrows(GihunException.class, () -> parser.parseEvent("")).getMessage());
        assertEquals(ErrorMessages.EVENT_FROM_TO_MISSING,
                assertThrows(GihunException.class, () -> parser.parseEvent("Meeting")).getMessage());
        assertEquals(ErrorMessages.EVENT_FROM_BEFORE_TO,
                assertThrows(GihunException.class, () ->
                        parser.parseEvent("Meeting /to 15/09/2026 1100 /from 15/09/2026 1000"))
                        .getMessage());
        assertEquals(ErrorMessages.EVENT_DESCRIPTION_EMPTY,
                assertThrows(GihunException.class, () ->
                        parser.parseEvent("/from 15/09/2026 1000 /to 15/09/2026 1100")).getMessage());
        assertEquals(ErrorMessages.EVENT_START_DATE_EMPTY,
                assertThrows(GihunException.class, () ->
                        parser.parseEvent("Meeting /from /to 15/09/2026 1100")).getMessage());
        assertEquals(ErrorMessages.EVENT_END_DATE_EMPTY,
                assertThrows(GihunException.class, () ->
                        parser.parseEvent("Meeting /from 15/09/2026 1000 /to")).getMessage());
    }
}

package cooper.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import cooper.exception.CooperException;

/** Exercises malformed commands and whitespace without allowing unchecked parser failures. */
public class ParserErrorHandlingTest {
    @Test
    public void whitespaceAndCase_preserveDescriptionsAndParseDates() {
        assertEquals("T | 0 | read  caf?\t?", Parser.parseTodo("  ToDo\t read  caf?\t?  ").toDataString());
        assertEquals(1, Parser.parseTaskNumber("  MARK\t  001  ", "syntax"));
        assertEquals("D | 0 | report  draft | 2026-10-01T09:00",
                Parser.parseDeadline(" DEADLINE\treport  draft  /by\t2026/10/01   09:00 ").toDataString());
        assertEquals("E | 0 | meeting | 2026-10-01T00:00 | 2026-10-02T00:00",
                Parser.parseEvent(" Event\tmeeting\t/from 01-10-2026  /to\t02-10-2026 ").toDataString());
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(Action.FIND, Parser.parseAction("find text"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void malformedEvents_reportDomainErrors() {
        for (String input : new String[] {
            "event meeting", "event meeting /from 2026-10-01", "event meeting /from /to 2026-10-02",
            "event meeting /from 2026-10-01 /to", "event meeting /to 2026-10-02 /from 2026-10-01",
            "event meeting /from 2026-10-01 /from 2026-10-02 /to 2026-10-03",
            "event meeting /from 2026-10-01 /to 2026-10-02 /to 2026-10-03",
            "event meeting /from 2026-02-30 /to 2026-03-02",
            "event meeting /from 2026-10-01 25:00 /to 2026-10-02"
        }) {
            assertThrows(CooperException.class, () -> Parser.parseEvent(input), input);
        }
    }

    @Test
    public void equalOrReversedEvents_reportDateOrderError() {
        for (String input : new String[] {
            "event meeting /from 2026-10-02 /to 2026-10-01",
            "event meeting /from 2026-10-01 /to 2026-10-01",
            "event meeting /from 2026-10-01 12:00 /to 2026-10-01 11:59"
        }) {
            assertEquals("I need the end time to be later than the start time.",
                    assertThrows(CooperException.class, () -> Parser.parseEvent(input)).getMessage());
        }
    }

    @Test
    public void malformedDeadlinesAndBlankTodos_reportDomainErrors() {
        for (String input : new String[] {
            "deadline", "deadline report /by", "deadline report /by 2026-10-01 /by 2026-10-02",
            "deadline /by 2026-10-01", "deadline report /from 2026-10-01",
            "deadline report /by 2026-10-01 /by", "deadline report /by 2026-02-30"
        }) {
            assertThrows(CooperException.class, () -> Parser.parseDeadline(input), input);
        }
        assertThrows(CooperException.class, () -> Parser.parseTodo(" todo  \t"));
    }

    @Test
    public void invalidNumbersAndExtraArguments_reportDomainErrors() {
        for (String token : new String[] {"0", "-1", "+1", "1.0", "?", "2147483648", "99999999999999999999"}) {
            assertThrows(CooperException.class, () -> Parser.parseTaskNumber("mark " + token, "syntax"), token);
        }
        assertEquals(Integer.MAX_VALUE, Parser.parseTaskNumber("mark 2147483647", "syntax"));
        for (String input : new String[] {"list extra", "BYE extra", "todo first\nsecond", " \t "}) {
            assertThrows(CooperException.class, () -> Parser.parseAction(input), input);
        }
        assertThrows(CooperException.class, () -> Parser.parseTaskNumber("mark 1 extra", "syntax"));
    }
}

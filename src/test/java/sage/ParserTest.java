package sage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sage.exception.SageException;
import sage.parser.Parser;

class ParserTest {
    @Test
    void parseCommand_uppercaseTodo_reportsLowercaseRequirement() {
        SageException exception = assertThrows(SageException.class,
                () -> new Parser().parseCommand("Todo read book"));

        assertEquals("The 'Todo' command must be lowercase. Please use 'todo'.",
                exception.getMessage());
    }

    @Test
    void parseCommand_uppercaseDeadline_reportsLowercaseRequirement() {
        SageException exception = assertThrows(SageException.class,
                () -> new Parser().parseCommand("Deadline submit report /by 1/1/2026 1200"));

        assertEquals("The 'Deadline' command must be lowercase. Please use 'deadline'.",
                exception.getMessage());
    }

    @Test
    void parseCommand_uppercaseEvent_reportsLowercaseRequirement() {
        SageException exception = assertThrows(SageException.class,
                () -> new Parser().parseCommand("EVENT meeting /from 1/1/2026 1200 /to 1/1/2026 1300"));

        assertEquals("The 'EVENT' command must be lowercase. Please use 'event'.",
                exception.getMessage());
    }
}

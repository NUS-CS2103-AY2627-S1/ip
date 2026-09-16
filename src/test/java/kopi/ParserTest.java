package kopi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTask_validCommands_tasksCreated() throws KopiException {
        Task todo = parser.parseTask("todo read book", Command.TODO);
        Task deadline = parser.parseTask(
                "deadline   return book   /by   2026-09-06", Command.DEADLINE);
        Task event = parser.parseTask(
                "event  project meeting   /from   2026-09-07   /to   2026-09-08",
                Command.EVENT);

        assertEquals("T | 0 | read book", todo.toDataString());
        assertEquals("D | 0 | return book | 2026-09-06", deadline.toDataString());
        assertEquals(
                "E | 0 | project meeting | 2026-09-07 | 2026-09-08",
                event.toDataString());
    }

    @Test
    void parseTask_invalidDate_exceptionThrown() {
        KopiException textException = assertThrows(KopiException.class, () ->
                parser.parseTask("deadline return book /by Sunday", Command.DEADLINE));
        KopiException nonexistentDateException = assertThrows(KopiException.class, () ->
                parser.parseTask("deadline return book /by 2026-02-30", Command.DEADLINE));

        assertEquals("Use dates in yyyy-MM-dd format.", textException.getMessage());
        assertEquals("Use dates in yyyy-MM-dd format.", nonexistentDateException.getMessage());
    }

    @Test
    void parseTask_missingDescription_exceptionThrown() {
        KopiException exception = assertThrows(KopiException.class, () ->
                parser.parseTask("todo", Command.TODO));

        assertEquals("A todo needs a description.", exception.getMessage());
    }

    @Test
    void parseTask_invalidEventRange_exceptionThrown() {
        KopiException sameDateException = assertThrows(KopiException.class, () ->
                parser.parseTask(
                        "event meeting /from 2026-09-08 /to 2026-09-08",
                        Command.EVENT));
        KopiException reversedDateException = assertThrows(KopiException.class, () ->
                parser.parseTask(
                        "event meeting /from 2026-09-09 /to 2026-09-08",
                        Command.EVENT));

        assertEquals("An event must end after it starts.", sameDateException.getMessage());
        assertEquals("An event must end after it starts.", reversedDateException.getMessage());
    }

    @Test
    void parseTask_storageDelimiterInDescription_exceptionThrown() {
        KopiException exception = assertThrows(KopiException.class, () ->
                parser.parseTask("todo buy milk | eggs", Command.TODO));

        assertEquals("Task descriptions cannot contain |.", exception.getMessage());
    }

    @Test
    void parseTask_repeatedParameters_exceptionThrown() {
        KopiException deadlineException = assertThrows(KopiException.class, () ->
                parser.parseTask(
                        "deadline return book /by 2026-09-06 /by 2026-09-07",
                        Command.DEADLINE));
        KopiException eventException = assertThrows(KopiException.class, () ->
                parser.parseTask(
                        "event meeting /from 2026-09-07 /from 2026-09-08 /to 2026-09-09",
                        Command.EVENT));

        assertEquals("Use: deadline DESCRIPTION /by DATE", deadlineException.getMessage());
        assertEquals("Use: event DESCRIPTION /from START /to END", eventException.getMessage());
    }

    @Test
    void validateNoArguments_extraArguments_exceptionThrown() throws KopiException {
        parser.validateNoArguments("  LIST  ", "list");

        KopiException exception = assertThrows(KopiException.class, () ->
                parser.validateNoArguments("list later", "list"));

        assertEquals("Use: list", exception.getMessage());
    }

    @Test
    void parseTaskNumber_validAndInvalidNumbers_handledCorrectly() throws KopiException {
        assertEquals(1, parser.parseTaskNumber("mark 2", "mark", 3));
        assertThrows(KopiException.class, () -> parser.parseTaskNumber("mark zero", "mark", 3));
        assertThrows(KopiException.class, () -> parser.parseTaskNumber("mark 4", "mark", 3));
    }

    @Test
    void parseKeyword_presentAndMissingKeywords_handledCorrectly() throws KopiException {
        assertEquals("return book", parser.parseKeyword("find return book", "find"));
        assertThrows(KopiException.class, () -> parser.parseKeyword("find", "find"));
    }
}

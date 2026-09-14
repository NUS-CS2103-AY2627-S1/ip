package kaya.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import kaya.command.CommandType;
import kaya.command.UpdateCommand;
import kaya.exception.KayaException;
import kaya.task.Deadline;
import kaya.task.Event;

/** Tests command parsing and validation performed by {@link Parser}. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws KayaException {
        Deadline deadline = parser.parseDeadline("deadline return book /by 2026-09-01");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 1), deadline.getBy());
    }

    @Test
    public void parseFindKeyword_validInput_returnsKeyword() throws KayaException {
        assertEquals("project meeting", parser.parseFindKeyword("find project meeting"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_throwsKayaException() {
        assertThrows(KayaException.class, () -> parser.parseFindKeyword("find"));
        assertThrows(KayaException.class, () -> parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseDeadline_invalidDate_throwsKayaException() {
        assertThrows(KayaException.class, () ->
                parser.parseDeadline("deadline return book /by 01-09-2026"));
        assertThrows(KayaException.class, () ->
                parser.parseDeadline("deadline return book /by 2026-02-30"));
    }

    @Test
    public void parseDeadline_missingDescriptionOrSeparator_throwsKayaException() {
        assertThrows(KayaException.class, () ->
                parser.parseDeadline("deadline /by 2026-09-01"));
        assertThrows(KayaException.class, () ->
                parser.parseDeadline("deadline return book 2026-09-01"));
    }

    @Test
    public void parseEvent_validInput_returnsEvent() throws KayaException {
        Event event = parser.parseEvent(
                "event project meeting /from 2026-09-02 /to 2026-09-03");

        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.of(2026, 9, 2), event.getFrom());
        assertEquals(LocalDate.of(2026, 9, 3), event.getTo());
    }

    @Test
    public void parseEvent_endBeforeStart_throwsKayaException() {
        assertThrows(KayaException.class, () -> parser.parseEvent(
                "event project meeting /from 2026-09-03 /to 2026-09-02"));
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws KayaException {
        assertEquals(0, parser.parseTaskIndex("mark 1", "mark", 3));
        assertEquals(2, parser.parseTaskIndex("delete 3", "delete", 3));
    }

    @Test
    public void parseTaskIndex_invalidNumber_throwsKayaException() {
        assertThrows(KayaException.class, () ->
                parser.parseTaskIndex("mark", "mark", 3));
        assertThrows(KayaException.class, () ->
                parser.parseTaskIndex("mark two", "mark", 3));
        assertThrows(KayaException.class, () ->
                parser.parseTaskIndex("mark 0", "mark", 3));
        assertThrows(KayaException.class, () ->
                parser.parseTaskIndex("mark 4", "mark", 3));
    }

    @Test
    public void parseDateCommands_extraWhitespace_preservesDescriptions() throws KayaException {
        Deadline deadline = parser.parseDeadline("  deadline\tread  book\t/by\t2026-09-20  ");
        Event event = parser.parseEvent(" event\tteam  meeting\t/from\t2026-09-20\t/to\t2026-09-20 ");

        assertEquals("read  book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 20), deadline.getBy());
        assertEquals("team  meeting", event.getDescription());
        assertEquals(event.getFrom(), event.getTo());
    }

    @Test
    public void parseDateCommands_repeatedOrReorderedFields_rejectsAmbiguity() {
        String[] invalidEvents = {
            "event meeting /to 2026-09-20 /from 2026-09-20 /to 2026-09-21",
            "event meeting /from 2026-09-20 /from 2026-09-21 /to 2026-09-22",
            "event meeting /to 2026-09-21 /from 2026-09-20"
        };
        for (String input : invalidEvents) {
            assertThrows(KayaException.class, () -> parser.parseEvent(input), input);
        }
        assertThrows(KayaException.class, () -> parser.parseDeadline(
                "deadline report /by 2026-09-20 /by 2026-09-21"));
    }

    @Test
    public void parseTodo_descriptionWithSpecialCharacters_preservesLiteralContent() throws KayaException {
        assertEquals("读书  | café /by notes", parser.parseTodo("  todo\t读书  | café /by notes  ").getDescription());
        for (String input : new String[] {"todo", " todo \t "}) {
            assertThrows(KayaException.class, () -> parser.parseTodo(input), input);
        }
        assertEquals(CommandType.TODO, parser.parseCommandType("  todo\t读书 "));
    }

    @Test
    public void parseDeadline_missingDate_explainsWhichValueIsMissing() {
        KayaException error = assertThrows(KayaException.class, () -> parser.parseDeadline("deadline report /by  "));

        assertTrue(error.getMessage().contains("date after /by"));
    }

    @Test
    public void parseEvent_missingValues_identifiesTheMissingDetail() {
        String[][] cases = {
            {"event /from 2026-09-20 /to 2026-09-21", "description"},
            {"event meeting /from /to 2026-09-21", "starting date"},
            {"event meeting /from 2026-09-20 /to", "ending date"}
        };
        for (String[] example : cases) {
            KayaException error = assertThrows(KayaException.class, () -> parser.parseEvent(example[0]));
            assertTrue(error.getMessage().contains(example[1]), example[0]);
        }
    }

    @Test
    public void parseDateCommands_leapDayAndLiteralSlashes_respectsCalendarAndFieldBoundaries() throws KayaException {
        Deadline deadline = parser.parseDeadline("deadline check folder/by and https://example.com /by 2024-02-29");

        assertEquals("check folder/by and https://example.com", deadline.getDescription());
        assertEquals(LocalDate.of(2024, 2, 29), deadline.getBy());
        assertThrows(KayaException.class, () -> parser.parseDeadline("deadline report /by 2025-02-29"));
        assertThrows(KayaException.class, () -> parser.parseEvent("event meeting /from tomorrow /to 2026-09-20"));
    }

    @Test
    public void parseTaskIndex_numericBoundariesAndExtraArguments_rejectsInvalidSelection() throws KayaException {
        for (String number : new String[] {"-1", "1.5", "1 2", "2147483648", "-2147483648"}) {
            assertThrows(KayaException.class, () -> parser.parseTaskIndex("mark " + number, "mark", 3), number);
        }
        assertThrows(KayaException.class, () -> parser.parseTaskIndex("mark 1", "mark", 0));
        assertEquals(2, parser.parseTaskIndex("  mark\t3  ", "mark", 3));
    }

    @Test
    public void parseUpdate_whitespaceAndLiteralDescription_extractsOneField() throws KayaException {
        UpdateCommand update = parser.parseUpdate("  update\t2\t/description  notes /by example  ", 2);

        assertEquals(1, update.index());
        assertEquals("/description", update.field());
        assertEquals("notes /by example", update.value());
        assertThrows(KayaException.class, () -> parser.parseUpdate("update 1 /description\t", 2));
        assertThrows(KayaException.class, () -> parser.parseUpdate("update 1 /unknown text", 2));
    }
}

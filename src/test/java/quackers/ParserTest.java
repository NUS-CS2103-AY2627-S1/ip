package quackers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ParserTest {
    @Test
    void parseCommandType_supportedCommands_returnsMatchingTypes() throws QuackersException {
        assertEquals(CommandType.BYE, Parser.parseCommandType("bye"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
        assertEquals(CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(CommandType.NOTE, Parser.parseCommandType("note Bring a laptop charger"));
        assertEquals(CommandType.DEADLINE,
                Parser.parseCommandType("deadline return book /by 2027-02-28"));
        assertEquals(CommandType.EVENT,
                Parser.parseCommandType("event meeting /from 2pm /to 4pm"));
    }

    @Test
    void parseCommandType_unknownCommand_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseCommandType("quack loudly"));

        assertEquals("Quack? I don't know what that means :-(", error.getMessage());
    }

    @Test
    void parseDeadline_validIsoDate_returnsTypedDeadline() throws QuackersException {
        Deadline deadline = Parser.parseDeadline("deadline submit report /by 2027-02-28");

        assertEquals(LocalDate.of(2027, 2, 28), deadline.getDueDate());
        assertEquals("[D][ ] submit report (by: Feb 28 2027)", deadline.toString());
    }

    @Test
    void parseDeadline_invalidDate_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseDeadline("deadline submit report /by 2027-02-29"));

        assertEquals("Quack? Use yyyy-MM-dd for the deadline date.", error.getMessage());
    }

    @Test
    void parseTaskIndex_nonNumericIndex_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseTaskIndex("delete first", "delete"));

        assertEquals("Quack? Please enter a valid task number.", error.getMessage());
    }

    @Test
    void parseFindKeyword_missingKeyword_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseFindKeyword("find"));

        assertEquals("Quack? Give me a keyword to find!", error.getMessage());
    }

    @Test
    void parseNote_validText_returnsNote() throws QuackersException {
        Note note = Parser.parseNote("note Bring a laptop charger");

        assertEquals("[N] Bring a laptop charger", note.toString());
    }

    @Test
    void parseNote_missingText_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseNote("note"));

        assertEquals("Quack? Give me note text!", error.getMessage());
    }

    @Test
    void parseCommandType_keywordPrefixOnly_exceptionThrown() {
        // "marking" merely starts with "mark", so it must not be treated as a mark command.
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseCommandType("marking 1"));

        assertEquals("Quack? I don't know what that means :-(", error.getMessage());
    }

    @Test
    void parseTodo_validDescription_returnsTodo() throws QuackersException {
        Todo todo = Parser.parseTodo("todo read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void parseTodo_missingDescription_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseTodo("todo"));

        assertEquals("Quack? Give me a todo description!", error.getMessage());
    }

    @Test
    void parseEvent_validCommand_returnsEvent() throws QuackersException {
        Event event = Parser.parseEvent("event project meeting /from 2pm /to 4pm");

        assertEquals("2pm", event.getStartTime());
        assertEquals("4pm", event.getEndTime());
        assertEquals("[E][ ] project meeting (from: 2pm to: 4pm)", event.toString());
    }

    @Test
    void parseEvent_missingFromMarker_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseEvent("event project meeting /to 4pm"));

        assertEquals("Quack? Use /from START /to END for an event.", error.getMessage());
    }

    @Test
    void parseEvent_missingToMarker_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseEvent("event project meeting /from 2pm"));

        assertEquals("Quack? Use /from START /to END for an event.", error.getMessage());
    }

    @Test
    void parseEvent_markersInWrongOrder_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseEvent("event meeting /to 4pm /from 2pm"));

        assertEquals("Quack? Use /from START /to END for an event.", error.getMessage());
    }

    @Test
    void parseEvent_missingDescription_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseEvent("event /from 2pm /to 4pm"));

        assertEquals("Quack? Give me an event description!", error.getMessage());
    }

    @Test
    void parseEvent_missingStartTime_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseEvent("event meeting /from  /to 4pm"));

        assertEquals("Quack? Give me an event start time!", error.getMessage());
    }

    @Test
    void parseEvent_missingEndTime_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseEvent("event meeting /from 2pm /to "));

        assertEquals("Quack? Give me an event end time!", error.getMessage());
    }

    @Test
    void parseDeadline_missingByMarker_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseDeadline("deadline submit report"));

        assertEquals("Quack? Use /by to give the deadline.", error.getMessage());
    }

    @Test
    void parseDeadline_missingDescription_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseDeadline("deadline /by 2027-02-28"));

        assertEquals("Quack? Give me a deadline description!", error.getMessage());
    }

    @Test
    void parseDeadline_missingDate_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseDeadline("deadline submit report /by "));

        assertEquals("Quack? Give me a deadline date!", error.getMessage());
    }

    @Test
    void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws QuackersException {
        // Users count from one, but the task list is indexed from zero.
        assertEquals(0, Parser.parseTaskIndex("mark 1", "mark"));
        assertEquals(2, Parser.parseTaskIndex("delete 3", "delete"));
    }

    @Test
    void parseTaskIndex_missingNumber_exceptionThrown() {
        QuackersException error = assertThrows(
                QuackersException.class, () -> Parser.parseTaskIndex("mark", "mark"));

        assertEquals("Quack? Please enter a valid task number.", error.getMessage());
    }

    @Test
    void parseFindKeyword_keywordWithExtraSpaces_returnsTrimmedKeyword() throws QuackersException {
        assertEquals("book", Parser.parseFindKeyword("find   book  "));
    }
}

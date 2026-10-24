package echo.parser; // same package as the class being tested

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import echo.EchoException;
import echo.task.Deadline;
import echo.task.Event;
import echo.task.Task;

public class ParserTest {

    // ---- parseTodo ----

    @Test
    public void parseTodo_validInput_returnsTodo() throws EchoException {
        Task task = Parser.parseTodo("read book");
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseTodo_emptyDescription_throwsException() {
        EchoException e = assertThrows(EchoException.class, () -> Parser.parseTodo("  "));
        assertEquals("The description of a todo cannot be empty.", e.getMessage());
    }

    @Test
    public void parseTodo_descriptionContainsFileSeparator_throwsException() {
        EchoException e = assertThrows(EchoException.class, () -> Parser.parseTodo("read | book"));
        assertEquals("A task's description cannot contain ' | ', "
                + "since that's used internally to save your tasks.", e.getMessage());
    }

    // ---- parseDeadline ----

    @Test
    public void parseDeadline_validInput_returnsDeadlineWithParsedDate() throws EchoException {
        Task task = Parser.parseDeadline("return book /by 2019-06-06");
        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Jun 06 2019)", task.toString());
    }

    @Test
    public void parseDeadline_missingByClause_throwsException() {
        EchoException e = assertThrows(EchoException.class, () -> Parser.parseDeadline("return book"));
        assertEquals("A deadline needs a '/by <when>' clause. Example: deadline return book /by Sunday",
                e.getMessage());
    }

    @Test
    public void parseDeadline_emptyDescription_throwsException() {
        EchoException e = assertThrows(EchoException.class, () -> Parser.parseDeadline(" /by 2019-06-06"));
        assertEquals("The description of a deadline cannot be empty.", e.getMessage());
    }

    @Test
    public void parseDeadline_emptyByClause_throwsException() {
        EchoException e = assertThrows(EchoException.class, () -> Parser.parseDeadline("return book /by "));
        assertEquals("The '/by' time of a deadline cannot be empty.", e.getMessage());
    }

    @Test
    public void parseDeadline_unparsableDate_throwsException() {
        EchoException e = assertThrows(EchoException.class, () ->
                Parser.parseDeadline("return book /by June 6th"));
        assertEquals("Invalid date for '/by'. Use yyyy-mm-dd, e.g. 2019-10-15.", e.getMessage());
    }

    @Test
    public void parseDeadline_irregularWhitespaceAroundBy_stillParses() throws EchoException {
        Task task = Parser.parseDeadline("return book    /by   2019-06-06");
        assertEquals("[D][ ] return book (by: Jun 06 2019)", task.toString());
    }

    @Test
    public void parseDeadline_descriptionContainsFileSeparator_throwsException() {
        EchoException e = assertThrows(EchoException.class, () ->
                Parser.parseDeadline("return | book /by 2019-06-06"));
        assertEquals("A task's description cannot contain ' | ', "
                + "since that's used internally to save your tasks.", e.getMessage());
    }

    // ---- parseEvent ----

    @Test
    public void parseEvent_validInput_returnsEventWithParsedDates() throws EchoException {
        Task task = Parser.parseEvent("trip /from 2019-08-01 /to 2019-08-03");
        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] trip (from: Aug 01 2019 to: Aug 03 2019)", task.toString());
    }

    @Test
    public void parseEvent_missingFromClause_throwsException() {
        EchoException e = assertThrows(EchoException.class, () -> Parser.parseEvent("trip"));
        assertEquals("An event needs a '/from <when>' clause. Example: event meeting /from Mon 2pm /to 4pm",
                e.getMessage());
    }

    @Test
    public void parseEvent_missingToClause_throwsException() {
        EchoException e = assertThrows(EchoException.class, () ->
                Parser.parseEvent("trip /from 2019-08-01"));
        assertEquals("An event needs a '/to <when>' clause after '/from'.", e.getMessage());
    }

    @Test
    public void parseEvent_toBeforeFrom_throwsException() {
        EchoException e = assertThrows(EchoException.class, () ->
                Parser.parseEvent("trip /from 2019-08-05 /to 2019-08-01"));
        assertEquals("An event's '/to' date cannot be before its '/from' date.", e.getMessage());
    }

    @Test
    public void parseEvent_irregularWhitespaceAroundClauses_stillParses() throws EchoException {
        Task task = Parser.parseEvent("trip   /from  2019-08-01    /to  2019-08-03");
        assertEquals("[E][ ] trip (from: Aug 01 2019 to: Aug 03 2019)", task.toString());
    }

    @Test
    public void parseEvent_descriptionContainsFileSeparator_throwsException() {
        EchoException e = assertThrows(EchoException.class, () ->
                Parser.parseEvent("trip | gone /from 2019-08-01 /to 2019-08-03"));
        assertEquals("A task's description cannot contain ' | ', "
                + "since that's used internally to save your tasks.", e.getMessage());
    }

    // ---- parseFindKeyword ----

    @Test
    public void parseFindKeyword_validInput_returnsKeyword() throws EchoException {
        assertEquals("book", Parser.parseFindKeyword("book"));
    }

    @Test
    public void parseFindKeyword_emptyInput_throwsException() {
        EchoException e = assertThrows(EchoException.class, () -> Parser.parseFindKeyword("  "));
        assertEquals("Please give a keyword to search for. Example: find book", e.getMessage());
    }
}

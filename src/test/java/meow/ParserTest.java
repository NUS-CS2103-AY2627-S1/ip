package meow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    void parseTask_validTodo_returnsTodo() throws MeowException {
        Parser parser = new Parser();
        Task task = parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    void parseTask_validDeadline_returnsDeadline() throws MeowException {
        Parser parser = new Parser();
        Task task = parser.parseTask("deadline CS2103T assignment /by 2026-08-28");

        assertInstanceOf(Deadline.class, task);
        assertEquals("CS2103T assignment", task.getDescription());

        Deadline deadline = (Deadline) task;
        assertEquals(LocalDate.of(2026, 8, 28), deadline.getDueDate());
    }

    @Test
    void parseTask_invalidDeadline_throwsException() {
        Parser parser = new Parser();

        assertThrows(MeowException.class, () -> parser.parseTask("deadline CS2103T assignment /by 2026-99-99"));
    }

    @Test
    void parseTask_validEvent_returnsEvent() throws MeowException {
        Parser parser = new Parser();
        Task task = parser.parseTask(
                "event Splashdown /from 2026-09-05 /to 2026-09-06");

        assertInstanceOf(Event.class, task);
        assertEquals("Splashdown", task.getDescription());

        Event event = (Event) task;
        assertEquals(LocalDate.of(2026, 9, 5), event.getFromDate());
        assertEquals(LocalDate.of(2026, 9, 6), event.getToDate());
    }

    @Test
    void parseTask_invalidEventDate_throwsException() {
        Parser parser = new Parser();

        assertThrows(MeowException.class, () ->
                parser.parseTask("event Splashdown /from tomorrow /to 2026-09-06"));
    }

    @Test
    void parseTask_eventEndsBeforeStart_throwsException() {
        Parser parser = new Parser();

        assertThrows(MeowException.class, () ->
                parser.parseTask("event Splashdown /from 2026-09-10 /to 2026-09-05"));
    }

    @Test
    void parseTask_unknownCommand_throwsException() {
        Parser parser = new Parser();

        assertThrows(MeowException.class, () -> parser.parseTask("blah blah"));
    }

    @Test
    void parseFindKeyword_validCommand_returnsKeyword() throws MeowException {
        Parser parser = new Parser();
        String keyword = parser.parseFindKeyword("find book");

        assertEquals("book", keyword);
    }

    @Test
    void parseFindKeyword_extraSpaces_returnsTrimmedKeyword() throws MeowException {
        Parser parser = new Parser();
        String keyword = parser.parseFindKeyword("find    book   ");

        assertEquals("book", keyword);
    }

    @Test
    void parseFindKeyword_missingKeyword_throwsException() {
        Parser parser = new Parser();

        assertThrows(MeowException.class, () -> parser.parseFindKeyword("find "));
    }
}

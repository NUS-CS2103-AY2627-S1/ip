package zabud.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zabud.session.Session;
import zabud.tasks.Deadline;
import zabud.tasks.Event;
import zabud.tasks.Todo;

/**
 * Tests command-line parsing, validation, and end-to-end command execution.
 */
class ParserTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void parse_handlesWhitespaceAndNamedValuesInOrder() {
        assertEquals(List.of(new ParsedToken("command", "deadline"), new ParsedToken("default", "test"),
                new ParsedToken("by", "1200")), Parser.parse("  deadline test /by 1200  "));
        assertEquals(List.of(new ParsedToken("command", "event"), new ParsedToken("default", "meeting"),
                new ParsedToken("from", "2/12/2019 1800"), new ParsedToken("to", "2/12/2019 1900")),
                Parser.parse("event meeting /from 2/12/2019 1800 /to 2/12/2019 1900"));
        assertEquals(List.of(new ParsedToken("command", "list"), new ParsedToken("default", "")),
                Parser.parse("list"));
        assertEquals("", Parser.parse("deadline test /by").getLast().value());
    }

    @Test
    void validation_rejectsUnknownMalformedAndOutOfRangeCommands() {
        Session session = new Session(temporaryDirectory.resolve("tasks.txt"));
        for (String input : List.of("unknown value", "todo", "todo /by tomorrow", "deadline test /when 1200",
                "deadline /by 1200", "deadline test /by 31/2/2019", "event meeting /to 1900 /from 1800",
                "event meeting /from 2400 /to 1800", "event meeting /from 1800 /to 1800",
                "event meeting /from 1900 /to 1800", "event meeting /from 2/12/2019 /to 1800",
                "event meeting /from 3/12/2019 /to 2/12/2019", "list extra", "bye later", "mark 1", "delete 0",
                "unmark nope", "find", "find book /by tomorrow")) {
            assertFalse(Parser.isValid(Parser.parse(input), session), input);
        }
        assertTrue(Parser.hint(Parser.parse("unknown")).contains("Unknown command"));
        assertTrue(Parser.hint(List.of()).contains("Unknown command"));
        assertTrue(Parser.hint(Parser.parse("event meeting /from 1900 /to 1800")).contains("later than"));
    }

    @Test
    void invoke_executesEverySupportedCommandAndUpdatesTasks() {
        Session session = new Session(temporaryDirectory.resolve("tasks.txt"));
        assertTrue(Parser.invoke("todo read book", session));
        assertTrue(Parser.invoke("deadline return book /by 2/12/2019 1800", session));
        assertTrue(Parser.invoke("event meeting /from 3/12/2019 0900 /to 3/12/2019 1000", session));

        assertEquals(3, session.getTaskList().size());
        assertInstanceOf(Todo.class, session.getTaskList().get(1));
        Deadline deadline = assertInstanceOf(Deadline.class, session.getTaskList().get(2));
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getDate());
        assertEquals(LocalTime.of(18, 0), deadline.getTime());
        Event event = assertInstanceOf(Event.class, session.getTaskList().get(3));
        assertEquals(LocalTime.of(9, 0), event.getFromTime());

        assertTrue(Parser.invoke("mark 1", session));
        assertTrue(session.getTaskList().get(1).isDone());
        assertTrue(Parser.invoke("unmark 1", session));
        assertFalse(session.getTaskList().get(1).isDone());
        assertTrue(Parser.invoke("list", session));
        assertTrue(Parser.invoke("find book", session));
        assertTrue(Parser.invoke("help", session));
        assertTrue(Parser.invoke("delete 2", session));
        assertEquals(2, session.getTaskList().size());
        assertFalse(Parser.invoke("bye", session));
    }
}

package rene.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rene.exception.ReneException;
import rene.task.Deadline;
import rene.task.Event;
import rene.task.Task;

/**
 * Covers parser behaviour around keyword recognition and argument spacing
 * that the primary parser tests do not exercise.
 */
class ParserEdgeCaseTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    void parse_commandKeywordsAreCaseSensitive() {
        assertThrows(ReneException.class, () -> parser.parse("LIST"));
        assertThrows(ReneException.class, () -> parser.parse("Todo read"));
        assertThrows(ReneException.class, () -> parser.parse("MARK 1"));
    }

    @Test
    void parse_bareKeywordIsRecognizedWithoutArgument() throws ReneException {
        ParsedCommand command = parser.parse("bye");

        assertEquals(CommandType.BYE, command.type());
        assertEquals("", command.argument());
    }

    @Test
    void parse_bareByeWithArgumentIsStillParsedAsBye() throws ReneException {
        ParsedCommand command = parser.parse("bye extra");

        assertEquals(CommandType.BYE, command.type());
        assertEquals("extra", command.argument());
    }

    @Test
    void parse_keywordFollowedByNonSpaceIsNotACommand() {
        // 'mark3' is not the command 'mark' followed by '3'.
        assertThrows(ReneException.class, () -> parser.parse("mark3"));
        assertThrows(ReneException.class, () -> parser.parse("findreport"));
    }

    @Test
    void parseTask_eventWithExtraSpacesAroundMarkers_stillParses() throws ReneException {
        Task task = parser.parseTask(parser.parse("event lecture  /from  2pm  /to  4pm"));

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("lecture", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    void parseTask_todoWithOnlySpaces_rejectsEmptyDescription() {
        ReneException exception =
                assertThrows(ReneException.class, () -> parser.parseTask(parser.parse("todo   ")));

        assertEquals("A todo requires a description. Try: todo read chapter 3", exception.getMessage());
    }

    @Test
    void parseTask_descriptionContainingTheWordByIsKept() throws ReneException {
        // 'by' without the slash must not confuse the /by marker search.
        Task task = parser.parseTask(parser.parse("deadline review by /by 2026-08-31"));

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("review by", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 31), deadline.getDueDate());
    }

    @Test
    void parseTask_duplicateByInsideDescriptionIsRejected() {
        // Two ' /by ' sequences are a duplicate-marker error, not a description.
        assertThrows(ReneException.class, () -> parser.parseTask(parser.parse(
                "deadline sort /by /by 2026-08-31")));
    }
}

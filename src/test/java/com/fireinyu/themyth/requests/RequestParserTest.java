package com.fireinyu.themyth.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.tasks.TaskOrder;
import com.fireinyu.themyth.util.MythDateTime;

/**
 * Unit tests for {@link RequestParser}.
 */
public class RequestParserTest {

    private final RequestParser parser = new RequestParser();

    @Test
    public void parse_missingKeywordValue_preservesIndexException() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> parser.parse("list /sort"));
    }

    @Test
    public void parse_duplicateKeyword_usesLastValue() {
        Request request = parser.parse("list /sort created /sort name");
        assertEquals(TaskOrder.NAME, request.getArg("sort", TaskOrder.class));
    }

    @Test
    public void parse_unknownCommand_discardsExtraArguments() {
        Request request = parser.parse("unknown extra /unused value");
        assertEquals("unknown", request.getArg(0, String.class));
        assertThrows(IndexOutOfBoundsException.class, () -> request.getArg(1, String.class));
    }

    /**
     * Tests parsing of exit command.
     */
    @Test
    public void parse_exitCommand_returnsExitRequest() {
        Request req = parser.parse("bye");
        assertInstanceOf(ExitRequest.class, req);
    }

    /**
     * Tests parsing of list command without and with keyword arguments.
     */
    @Test
    public void parse_listCommand_returnsListRequest() {
        Request req1 = parser.parse("list");
        assertInstanceOf(ListRequest.class, req1);

        Request req2 = parser.parse("list /sort created");
        assertInstanceOf(ListRequest.class, req2);
    }

    /**
     * Tests parsing of at command.
     */
    @Test
    public void parse_atCommand_returnsAtRequest() {
        Request req1 = parser.parse("at 2025-06-01-09-00-00");
        assertInstanceOf(AtRequest.class, req1);

        Request req2 = parser.parse("at 2025-06-01-09-00-00 /sort created");
        assertInstanceOf(AtRequest.class, req2);
    }

    /**
     * Tests parsing of due command.
     */
    @Test
    public void parse_dueCommand_returnsDueRequest() {
        Request req1 = parser.parse("due 2025-10-15-18-00-00");
        assertInstanceOf(DueRequest.class, req1);

        Request req2 = parser.parse("due 2025-10-15-18-00-00 /sort created");
        assertInstanceOf(DueRequest.class, req2);
    }

    /**
     * Tests parsing of todo command.
     */
    @Test
    public void parse_todoCommand_returnsTodoRequest() {
        Request req = parser.parse("todo read");
        assertInstanceOf(TodoRequest.class, req);
        assertEquals("read", req.getArg(1, String.class));
    }

    /**
     * Tests parsing of deadline command with /by keyword.
     */
    @Test
    public void parse_deadlineCommand_returnsDeadlineRequest() {
        Request req = parser.parse("deadline homework /by 2025-10-15-18-00-00");
        assertInstanceOf(DeadlineRequest.class, req);
        assertEquals("homework", req.getArg(1, String.class));
    }

    /**
     * Tests parsing of event command with /from and /to keywords.
     */
    @Test
    public void parse_eventCommand_returnsEventRequest() {
        Request req = parser.parse("event camp /from 2025-06-01-09-00-00 /to 2025-06-01-17-00-00");
        assertInstanceOf(EventRequest.class, req);
        assertEquals("camp", req.getArg(1, String.class));
    }

    /**
     * Tests parsing of mark, unmark, and delete commands.
     */
    @Test
    public void parse_indexBasedCommands_returnsCorrectRequests() {
        Request markReq = parser.parse("mark 2");
        assertInstanceOf(MarkRequest.class, markReq);
        assertEquals(2, markReq.getArg(1, Integer.class));

        Request unmarkReq = parser.parse("unmark 3");
        assertInstanceOf(UnmarkRequest.class, unmarkReq);
        assertEquals(3, unmarkReq.getArg(1, Integer.class));

        Request deleteReq = parser.parse("delete 1");
        assertInstanceOf(DeleteRequest.class, deleteReq);
        assertEquals(1, deleteReq.getArg(1, Integer.class));
    }

    /**
     * Tests parsing of find command.
     */
    @Test
    public void parse_findCommand_returnsFindRequest() {
        Request req = parser.parse("find book");
        assertInstanceOf(FindRequest.class, req);
        assertEquals("book", req.getArg(1, String.class));
    }

    /**
     * Tests parsing of trivia quiz commands.
     */
    @Test
    public void parse_quizCommands_returnsQuizAndAnswerRequests() {
        Request quizReq = parser.parse("quiz");
        assertInstanceOf(QuizRequest.class, quizReq);

        Request answerReq = parser.parse("answer 1");
        assertInstanceOf(AnswerRequest.class, answerReq);
        assertEquals(1, answerReq.getArg(1, Integer.class));
    }

    /**
     * Tests parsing of unrecognized command.
     */
    @Test
    public void parse_unknownCommand_returnsGenericRequest() {
        Request req = parser.parse("unknown 123");
        assertEquals("unknown", req.getArg(0, String.class));
    }

    /**
     * Tests that a double-quoted positional argument containing spaces is treated as a single argument.
     */
    @Test
    public void parse_quotedPositionalArgumentWithSpaces_treatedAsSingleArgument() {
        Request req = parser.parse("todo \"read a book\"");
        assertInstanceOf(TodoRequest.class, req);
        assertEquals("read a book", req.getArg(1, String.class));
    }

    /**
     * Tests that an escaped double-quote inside a quoted argument is preserved in the parsed argument.
     */
    @Test
    public void parse_quotedArgumentWithEscapedQuotes_preservesQuotesInArgument() {
        Request req = parser.parse("todo \"read \\\"The Hobbit\\\" book\"");
        assertInstanceOf(TodoRequest.class, req);
        assertEquals("read \"The Hobbit\" book", req.getArg(1, String.class));
    }

    /**
     * Tests that escaped double-quotes in unquoted arguments are accepted and unescaped.
     */
    @Test
    public void parse_unquotedArgumentWithEscapedQuotes_preservesQuotesInArgument() {
        Request req = parser.parse("todo read\\\"book");
        assertInstanceOf(TodoRequest.class, req);
        assertEquals("read\"book", req.getArg(1, String.class));
    }

    /**
     * Tests that an argument composed entirely of escaped quotes within double quotes is parsed correctly.
     */
    @Test
    public void parse_entirelyEscapedQuotesInQuotedArgument_returnsInnerQuotes() {
        Request req = parser.parse("todo \"\\\"quoted\\\"\"");
        assertInstanceOf(TodoRequest.class, req);
        assertEquals("\"quoted\"", req.getArg(1, String.class));
    }

    /**
     * Tests parsing of a quoted keyword argument value containing spaces or datetime formatting.
     */
    @Test
    public void parse_quotedKeywordArgument_treatedAsSingleArgument() {
        Request req = parser.parse("deadline \"homework assignment\" /by \"2025-10-15-18-00-00\"");
        assertInstanceOf(DeadlineRequest.class, req);
        assertEquals("homework assignment", req.getArg(1, String.class));
        assertEquals("2025-10-15-18-00-00", req.getArg("by", MythDateTime.class).dump());
    }

    /**
     * Tests parsing multiple quoted arguments across positional and keyword arguments.
     */
    @Test
    public void parse_multipleQuotedArguments_parsesAllArgumentsCorrectly() {
        Request req = parser.parse(
                "event \"annual gala\" /from \"2025-06-01-09-00-00\" /to \"2025-06-01-17-00-00\"");
        assertInstanceOf(EventRequest.class, req);
        assertEquals("annual gala", req.getArg(1, String.class));
        assertEquals("2025-06-01-09-00-00", req.getArg("from", MythDateTime.class).dump());
        assertEquals("2025-06-01-17-00-00", req.getArg("to", MythDateTime.class).dump());
    }

    /**
     * Tests parsing of find command with a double-quoted multi-word keyword.
     */
    @Test
    public void parse_quotedKeywordForFind_parsesMultiwordKeyword() {
        Request req = parser.parse("find \"software engineering\" /sort name");
        assertInstanceOf(FindRequest.class, req);
        assertEquals("software engineering", req.getArg(1, String.class));
    }

    /**
     * Tests that an empty double-quoted argument is parsed as an empty string argument.
     */
    @Test
    public void parse_emptyQuotedString_treatedAsEmptyArgument() {
        Request req = parser.parse("todo \"\"");
        assertInstanceOf(TodoRequest.class, req);
        assertEquals("", req.getArg(1, String.class));
    }

    /**
     * Tests that extra whitespace around arguments is ignored while spaces inside quotes are preserved.
     */
    @Test
    public void parse_multipleSpacesInsideAndOutsideQuotes_preservesInteriorSpacesOnly() {
        Request req = parser.parse("   todo    \"read   a   book\"   ");
        assertInstanceOf(TodoRequest.class, req);
        assertEquals("read   a   book", req.getArg(1, String.class));
    }
}

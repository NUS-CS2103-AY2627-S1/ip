package ada;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ParserTest {
    private final Parser parserObj = new Parser();
    //bye
    @Test
    void parse_bye_returnsByeCommand() throws AdaException {
        Parser.CommandInfo ci = parserObj.parse("bye");
        assertEquals("bye", ci.getCommand());
    }

    //list
    @Test
    void parse_list_returnsListCommand() throws AdaException {
        Parser.CommandInfo ci = parserObj.parse("list");
        assertEquals("list", ci.getCommand());
    }

    //valid sort
    @Test
    void parse_sort_returnsSortCommand() throws AdaException {
        Parser.CommandInfo command = parserObj.parse("sort");
        assertEquals("sort", command.getCommand());
    }

    //valid todo
    @Test
    void parse_validTodo_returnsTodoCommand() throws AdaException {
        Parser.CommandInfo ci = parserObj.parse("todo buy book");
        assertEquals("todo", ci.getCommand());
        assertEquals("buy book", ci.getDescription());
    }

    //todo with no task description
    @Test
    void parse_todoEmptyDesc_throwsAdaException() {
        assertThrows(AdaException.class, () -> parserObj.parse("todo "));
    }

    //valid mark
    @Test
    void parse_validMark_returnsMarkCommand() throws AdaException {
        Parser.CommandInfo ci = parserObj.parse("mark 2");
        assertEquals("mark", ci.getCommand());
        assertEquals(2, ci.getTaskNum());
    }

    //mark with not number
    @Test
    void parse_markNotNumber_throwsAdaException() {
        assertThrows(AdaException.class, () -> parserObj.parse("mark abc"));
    }

    //invalid mark format
    @Test
    void parse_markInvalidFormat_throwsAdaException() {
        assertThrows(AdaException.class, () -> parserObj.parse("mark1"));
    }

    // valid deadline
    @Test
    void parse_validDeadline_returnsDeadlineCommand() throws AdaException {
        Parser.CommandInfo ci = parserObj.parse("deadline essay /by friday");
        assertEquals("deadline", ci.getCommand());
        assertEquals("essay", ci.getDescription());
        assertEquals("friday", ci.getBy());
    }

    //deadline lack /by
    @Test
    void parse_deadlineMissingBy_throwsAdaException() {
        assertThrows(AdaException.class, () -> parserObj.parse("deadline finish assignment"));
    }

    // valid event
    @Test
    void parse_validEvent_returnsEventCommand() throws AdaException {
        Parser.CommandInfo ci = parserObj.parse("event team meeting /from mon /to fri");
        assertEquals("event", ci.getCommand());
        assertEquals("team meeting", ci.getDescription());
        assertEquals("mon", ci.getFrom());
        assertEquals("fri", ci.getTo());
    }

    // event lack /to
    @Test
    void parse_eventMissingTo_throwsAdaException() {
        assertThrows(AdaException.class, () -> parserObj.parse("event meeting /from mon"));
    }

    // valid delete
    @Test
    void parse_validDelete_returnsDeleteCommand() throws AdaException {
        Parser.CommandInfo ci = parserObj.parse("delete 3");
        assertEquals("delete", ci.getCommand());
        assertEquals(3, ci.getTaskNum());
    }

    //invalid command
    @Test
    void parse_unknownCommand_returnUnknown() throws AdaException {
        Parser.CommandInfo ci = parserObj.parse("helloworld");
        assertEquals("unknown", ci.getCommand());
    }
}

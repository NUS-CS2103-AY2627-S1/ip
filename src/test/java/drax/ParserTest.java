package drax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void parse_byeCommand_returnsByeCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.BYE, "", "", "", "");
        Parser.Command actual = Parser.parse("bye");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_todoCommand_returnsTodoCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.TODO, "", "homework", "", "");
        Parser.Command actual = Parser.parse("todo homework");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_bareTodo_returnsTodoWithEmptyDescription() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.TODO, "", "", "", "");

        assertEquals(expected, Parser.parse("todo"));
    }

    @Test
    public void parse_todoWithExtraWhitespace_trimsDescription() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.TODO, "", "finish homework", "", "");

        assertEquals(expected, Parser.parse("todo    finish homework   "));
    }

    @Test
    public void parse_deadlineCommand_returnsDeadlineCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.DEADLINE, "", "return book", "", "06/06/2026 1800");
        Parser.Command actual = Parser.parse("deadline return book /by 06/06/2026 1800");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_bareDeadline_returnsDeadlineWithEmptyFields() {
        Parser.Command expected =
                new Parser.Command(Parser.Type.DEADLINE, "", "", "", "");

        assertEquals(expected, Parser.parse("deadline"));
    }

    @Test
    public void parse_deadlineWithoutDate_returnsTaskWithEmptyDate() {
        Parser.Command expected =
                new Parser.Command(
                        Parser.Type.DEADLINE,
                        "",
                        "return book",
                        "",
                        ""
                );
        Parser.Command actual = Parser.parse("deadline return book");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_deadlineWithoutDescription_preservesDate() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.DEADLINE, "", "", "", "2026-09-11");

        assertEquals(expected, Parser.parse("deadline /by 2026-09-11"));
    }

    @Test
    public void parse_deadlineWithEmptyDate_returnsEmptyDate() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.DEADLINE, "", "return book", "", "");

        assertEquals(expected, Parser.parse("deadline return book /by "));
    }

    @Test
    public void parse_deadlineWithExtraWhitespace_trimsTaskAndDate() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.DEADLINE, "", "return book", "", "2026-09-11");

        assertEquals(expected, Parser.parse("deadline    return book    /by    2026-09-11   "));
    }

    @Test
    public void parse_deadlineWithUnspacedMarker_treatsMarkerAsDescription() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.DEADLINE, "", "return book/by 2026-09-11", "", "");

        assertEquals(expected, Parser.parse("deadline return book/by 2026-09-11"));
    }

    @Test
    public void parse_deadlineWithSecondByMarker_preservesItInDate() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.DEADLINE, "", "return book", "", "2026-09-11 /by evening");

        assertEquals(expected, Parser.parse("deadline return book /by 2026-09-11 /by evening"));
    }

    @Test
    public void parse_eventCommand_returnsEventCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.EVENT,
                "",
                "meeting",
                "20/08/2026 1200",
                "20/08/2026 1400");
        Parser.Command actual = Parser.parse("event meeting /from 20/08/2026 1200 /to 20/08/2026 1400");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_bareEvent_returnsEventWithEmptyFields() {
        Parser.Command expected = new Parser.Command(Parser.Type.EVENT, "", "", "", "");

        assertEquals(expected, Parser.parse("event"));
    }

    @Test
    public void parse_eventWithoutDates_returnsTaskWithEmptyDates() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.EVENT,
                "",
                "meeting",
                "",
                ""
        );
        Parser.Command actual = Parser.parse("event meeting");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_eventWithoutDescription_preservesDates() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.EVENT, "", "", "2026-09-11", "2026-09-12");

        assertEquals(
                expected,
                Parser.parse("event /from 2026-09-11 /to 2026-09-12"));
    }

    @Test
    public void parse_eventWithAdjacentMarkers_returnsEmptyDates() {
        Parser.Command actual = Parser.parse("event meeting /from /to 2026-09-12");

        assertEquals(Parser.Type.EVENT, actual.type());
        assertEquals("", actual.startDate());
        assertEquals("", actual.endDate());
    }

    @Test
    public void parse_eventWithReversedMarkers_returnsEmptyDates() {
        Parser.Command actual = Parser.parse("event meeting /to 2026-09-12 /from 2026-09-11");

        assertEquals(Parser.Type.EVENT, actual.type());
        assertEquals("", actual.startDate());
        assertEquals("", actual.endDate());
    }

    @Test
    public void parse_eventWithWhitespaceStartDate_returnsEmptyStartDate() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.EVENT, "", "meeting", "", "2026-09-12");

        assertEquals(expected, Parser.parse("event meeting /from  /to 2026-09-12"));
    }

    @Test
    public void parse_eventWithWhitespaceEndDate_returnsEmptyEndDate() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.EVENT, "", "meeting", "2026-09-11", "");

        assertEquals(expected, Parser.parse("event meeting /from 2026-09-11 /to "));
    }

    @Test
    public void parse_eventWithOnlyFromMarker_treatsMarkerAsDescription() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.EVENT, "", "meeting /from 2026-09-11", "", "");

        assertEquals(expected, Parser.parse("event meeting /from 2026-09-11"));
    }

    @Test
    public void parse_eventWithOnlyToMarker_treatsMarkerAsDescription() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.EVENT, "", "meeting /to 2026-09-12", "", "");

        assertEquals(expected, Parser.parse("event meeting /to 2026-09-12"));
    }

    @Test
    public void parse_eventWithExtraWhitespace_trimsAllExtractedFields() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.EVENT, "", "meeting", "2026-09-11", "2026-09-12");

        assertEquals(expected,
                Parser.parse("event    meeting    /from    2026-09-11    /to    2026-09-12   "));
    }

    @Test
    public void parse_eventWithSecondToMarker_preservesItInEndDate() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.EVENT, "", "meeting", "2026-09-11", "2026-09-12 /to evening");

        assertEquals(expected,
                Parser.parse("event meeting /from 2026-09-11 /to 2026-09-12 /to evening"));
    }

    @Test
    public void parse_unknownCommand_returnsUnknownCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.UNKNOWN, "blah", "", "", "");
        Parser.Command actual = Parser.parse("blah");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_emptyInput_returnsUnknownCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.UNKNOWN, "", "", "", "");

        assertEquals(expected, Parser.parse(""));
    }

    @Test
    public void parse_unknownCommandWithWhitespace_preservesOriginalInput() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.UNKNOWN, "  not a command  ", "", "", "");

        assertEquals(expected, Parser.parse("  not a command  "));
    }

    @Test
    public void parse_commandWithLeadingWhitespace_returnsUnknownCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.UNKNOWN, " todo homework", "", "", "");

        assertEquals(expected, Parser.parse(" todo homework"));
    }

    @Test
    public void parse_uppercaseCommand_returnsUnknownCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.UNKNOWN, "TODO homework", "", "", "");

        assertEquals(expected, Parser.parse("TODO homework"));
    }

    @Test
    public void parse_commandsWithExtraSuffix_returnsUnknownCommands() {
        List<String> inputs = List.of(
                "bye now", "listing", "marker 1", "unmarked 1", "todoing homework",
                "deadlines task", "eventful meeting", "deleted 1", "finder book",
                "undoing", "redoing");

        for (String input : inputs) {
            Parser.Command actual = Parser.parse(input);
            assertEquals(Parser.Type.UNKNOWN, actual.type(), input + " should be unknown");
            assertEquals(input, actual.argument(), input + " should be preserved");
        }
    }

    @Test
    public void parse_listCommand_returnsListCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.LIST, "", "", "", "");
        Parser.Command actual = Parser.parse("list");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_unmarkCommand_returnsUnmarkCommandWithIndex() {
        Parser.Command expected =
                new Parser.Command(
                        Parser.Type.UNMARK, "2", "", "", "");
        Parser.Command actual = Parser.parse("unmark 2");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_bareUnmark_returnsUnmarkCommandWithEmptyIndex() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.UNMARK, "", "", "", "");

        assertEquals(expected, Parser.parse("unmark"));
    }

    @Test
    public void parse_unmarkWithExtraWhitespace_trimsIndex() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.UNMARK, "2", "", "", "");

        assertEquals(expected, Parser.parse("unmark    2   "));
    }

    @Test
    public void parse_markCommand_returnsMarkCommandWithIndex() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.MARK, "3", "", "", "");
        Parser.Command actual = Parser.parse("mark 3");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_bareMark_returnsMarkCommandWithEmptyIndex() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.MARK, "", "", "", "");

        assertEquals(expected, Parser.parse("mark"));
    }

    @Test
    public void parse_markWithExtraWhitespace_trimsIndex() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.MARK, "3", "", "", "");

        assertEquals(expected, Parser.parse("mark    3   "));
    }


    @Test
    public void parse_deleteCommand_returnsDeleteCommandWithIndex() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.DELETE, "5", "", "", "");
        Parser.Command actual = Parser.parse("delete 5");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_bareDelete_returnsDeleteCommandWithEmptyIndex() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.DELETE, "", "", "", "");

        assertEquals(expected, Parser.parse("delete"));
    }

    @Test
    public void parse_deleteWithExtraWhitespace_trimsIndex() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.DELETE, "5", "", "", "");

        assertEquals(expected, Parser.parse("delete    5   "));
    }

    @Test
    public void parse_findCommand_returnsFindCommandWithKeyword() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.FIND, "clean house", "", "", "");
        Parser.Command actual = Parser.parse("find clean house");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_bareFind_returnsFindCommandWithEmptyKeyword() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.FIND, "", "", "", "");

        assertEquals(expected, Parser.parse("find"));
    }

    @Test
    public void parse_findWithExtraWhitespace_trimsKeyword() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.FIND, "clean house", "", "", "");

        assertEquals(expected, Parser.parse("find    clean house   "));
    }

    @Test
    public void parse_undoCommand_returnsUndoCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.UNDO, "", "", "", "");

        assertEquals(expected, Parser.parse("undo"));
    }

    @Test
    public void parse_redoCommand_returnsRedoCommand() {
        Parser.Command expected = new Parser.Command(
                Parser.Type.REDO, "", "", "", "");

        assertEquals(expected, Parser.parse("redo"));
    }
}

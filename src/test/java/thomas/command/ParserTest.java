package thomas.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.task.Todo;
import thomas.ui.Ui;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void getCommand_commandWithArguments_returnsCommandName() {
        assertEquals("deadline", parser.getCommand("deadline project /by 2026-08-26"));
        assertEquals("list", parser.getCommand("list"));
        assertEquals("list", parser.getCommand("  list  "));
        assertEquals("todo", parser.getCommand("todo\tread book"));
    }

    @Test
    void getArgument_commandPrefix_returnsTrimmedArgument() {
        assertEquals("finish report", parser.getArgument("todo   finish report", "todo"));
    }

    @Test
    void getTaskIndex_validOneBasedNumber_returnsZeroBasedIndex() throws ThomasException {
        assertEquals(1, parser.getTaskIndex("2", 3, "missing"));
    }

    @Test
    void getTaskIndex_emptyOrOutOfRange_throwsThomasException() {
        assertThrows(ThomasException.class, () -> parser.getTaskIndex("", 2, "missing"));
        assertThrows(ThomasException.class, () -> parser.getTaskIndex("0", 2, "missing"));
        assertThrows(ThomasException.class, () -> parser.getTaskIndex("3", 2, "missing"));
        assertThrows(ThomasException.class, () -> parser.getTaskIndex("one", 2, "missing"));
        assertThrows(ThomasException.class, () -> parser.getTaskIndex("1 2", 2, "missing"));
    }

    @Test
    void parse_supportedCommands_returnsMatchingCommandTypes() throws ThomasException {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(TodoCommand.class, parser.parse("todo read book"));
        assertInstanceOf(DeadlineCommand.class, parser.parse("deadline submit /by 2026-08-26"));
        assertInstanceOf(EventCommand.class, parser.parse("event meeting /from 2026-08-26 /to 2026-08-27"));
        assertInstanceOf(OnCommand.class, parser.parse("on 2026-08-26"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(AddPlaceCommand.class, parser.parse(
                "place Sushi /type restaurant /at Town /rating 4 /price 12"));
        assertInstanceOf(ListPlaceCommand.class, parser.parse("listplace"));
        assertInstanceOf(FindPlaceCommand.class, parser.parse("findplace sushi"));
        assertInstanceOf(EditPlaceCommand.class, parser.parse("editplace 1 /rating 5"));
        assertInstanceOf(DeletePlaceCommand.class, parser.parse("deleteplace 1"));
        assertInstanceOf(ConfirmDeletePlaceCommand.class, parser.parse("confirmdeleteplace 1"));
        assertInstanceOf(HelpCommand.class, parser.parse("/help"));
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(UnknownCommand.class, parser.parse("unknown"));
    }

    @Test
    void getDeadlineDetails_validCommand_returnsDescriptionAndDate() throws ThomasException {
        assertArrayEquals(new String[]{"submit report", "2026-08-26 1800"},
                parser.getDeadlineDetails("deadline submit report /by 2026-08-26 1800"));
    }

    @Test
    void getDeadlineDetails_missingParts_throwsThomasException() {
        assertThrows(ThomasException.class, () -> parser.getDeadlineDetails("deadline submit report"));
        assertThrows(ThomasException.class, () -> parser.getDeadlineDetails("deadline /by 2026-08-26"));
        assertThrows(ThomasException.class, () -> parser.getDeadlineDetails("deadline submit /by"));
        assertThrows(ThomasException.class, () ->
                parser.getDeadlineDetails("deadline submit /by 2026-08-26 /by 2026-08-27"));
    }

    @Test
    void getEventDetails_validCommand_returnsDescriptionAndDates() throws ThomasException {
        assertArrayEquals(new String[]{"team meeting", "2026-08-26 0900", "2026-08-26 1000"},
                parser.getEventDetails("event team meeting /from 2026-08-26 0900 /to 2026-08-26 1000"));
    }

    @Test
    void getEventDetails_missingOrReversedParts_throwsThomasException() {
        String shortEventCmd = "event meeting /from 2026-08-26";
        assertThrows(ThomasException.class, () -> parser.getEventDetails(shortEventCmd));
        String eventMissingTo = "event meeting /from /to 2026-08-26";
        assertThrows(ThomasException.class, () -> parser.getEventDetails(eventMissingTo));
        String eventWrongOrder = "event meeting /to 2026-08-26 /from 2026-08-27";
        assertThrows(ThomasException.class, () -> parser.getEventDetails(eventWrongOrder));
        String repeatedFrom = "event meeting /from 2026-08-26 /from 2026-08-27 /to 2026-08-28";
        assertThrows(ThomasException.class, () -> parser.getEventDetails(repeatedFrom));
    }

    @Test
    void parse_placeCommandWithDuplicateOrUnknownFields_throwsThomasException() {
        assertThrows(ThomasException.class, () -> parser.parse(
                "place Sushi /type restaurant /type cafe /at Town /rating 4 /price 12"));
        assertThrows(ThomasException.class, () -> parser.parse(
                "place Sushi /type restaurant /at Town /rating 4 /price 12 /tag ramen"));
    }

    @Test
    void parse_whitespaceAndUnexpectedArguments_handlesOrRejectsInput() throws ThomasException {
        assertInstanceOf(TodoCommand.class, parser.parse("  todo\tread book  "));
        assertThrows(ThomasException.class, () -> parser.parse("   "));
        assertThrows(ThomasException.class, () -> parser.parse("list extra"));
        assertThrows(ThomasException.class, () -> parser.parse("bye now"));
        assertThrows(ThomasException.class, () -> parser.parse("todo unsafe | description"));
    }

    @Test
    void parse_taskIndexesWithInvalidSyntax_returnsCommandsThatRejectOnExecution() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task"));
        Storage storage = new Storage("build", "parser-test.txt");

        assertThrows(ThomasException.class, () -> parser.parse("mark abc").execute(tasks, new Ui(), storage));
        assertThrows(ThomasException.class, () -> parser.parse("delete 1 2").execute(tasks, new Ui(), storage));
        assertThrows(ThomasException.class, () -> parser.parse("unmark 999999999999999999999").execute(
                tasks, new Ui(), storage));
    }

    @Test
    void parse_placeCommandsWithMissingOrMalformedDetails_throwsException() {
        assertThrows(ThomasException.class, () -> parser.parse("editplace"));
        assertThrows(ThomasException.class, () -> parser.parse("editplace 1"));
        assertThrows(ThomasException.class, () -> parser.parse("editplace abc /rating 5"));
        assertThrows(ThomasException.class, () -> parser.parse("editplace 1 /rating 5 /price 10"));
        assertThrows(ThomasException.class, () -> parser.parse("deleteplace"));
        assertThrows(ThomasException.class, () -> parser.parse("deleteplace -1"));
        assertThrows(ThomasException.class, () -> parser.parse("confirmdeleteplace zero"));
        assertThrows(ThomasException.class, () -> parser.parse("place Sushi /type"));
    }
}

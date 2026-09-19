package bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void parse_nullBlankAndUnknown_returnUnknown() {
        Parser parser = new Parser();
        assertEquals(CommandType.UNKNOWN, parser.parse(null));
        assertEquals(CommandType.UNKNOWN, parser.parse("  "));
        assertEquals(CommandType.UNKNOWN, parser.parse("wat"));
    }

    @Test
    public void parse_allSupportedKeywords_returnsTheirTypes() {
        Parser parser = new Parser();
        assertEquals(CommandType.DELETE, parser.parse("delete 1"));
        assertEquals(CommandType.MARK, parser.parse("mark 1"));
        assertEquals(CommandType.UNMARK, parser.parse("unmark 1"));
        assertEquals(CommandType.TODO, parser.parse("todo item"));
        assertEquals(CommandType.DEADLINE, parser.parse("deadline item"));
        assertEquals(CommandType.EVENT, parser.parse("event item"));
    }

    @Test
    public void parseDateTime_supportedFormats_returnDateTime() throws BobException {
        assertEquals(LocalDateTime.of(2026, 9, 1, 18, 0), Parser.parseDateTime("2026-09-01 1800"));
        assertEquals(LocalDateTime.of(2026, 9, 1, 18, 0), Parser.parseDateTime("1/9/2026 1800"));
    }

    @Test
    public void parseDateTime_invalidInput_throwsHelpfulException() {
        assertThrows(BobException.class, () -> Parser.parseDateTime(null));
        assertThrows(BobException.class, () -> Parser.parseDateTime("2026/09/01"));
    }

    @DisplayName("Test for parsing command")
    @Test
    public void parse_bye_returnsBye() {
        Parser parser = new Parser();
        assertEquals(CommandType.BYE, parser.parse("bye"));
    }

    @DisplayName("Test for parsing the list command")
    @Test
    public void parse_listCommand_returnsList() {
        Parser parser = new Parser();
        assertEquals(CommandType.LIST, parser.parse("list"));
    }

    @Test
    public void parse_findCommand_returnsFind() {
        Parser parser = new Parser();
        assertEquals(CommandType.FIND, parser.parse("find book"));
    }

    @Test
    public void parse_edit_returnsEdit() {
        Parser parser = new Parser();
        assertEquals(CommandType.EDIT, parser.parse("edit 1 New description"));
    }

    @DisplayName("Test for creating a deadline task")
    @Test
    public void addDeadline_validInput_returnsDeadline() throws BobException {
        TaskList taskList = new TaskList();

        Task deadline = taskList.addDeadline("Submit report /by 2026-09-01");

        assertEquals(1, taskList.size());
        assertEquals(
                "[D][ ] Submit report (by: Sept 01 2026)",
                deadline.toString());
    }
}

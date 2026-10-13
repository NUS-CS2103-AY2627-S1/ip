package ruby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ruby.RubyException;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.Deadline;
import ruby.task.Event;
import ruby.task.TaskList;

class ParserTest {

    @TempDir
    Path tempDir;

    private Storage storage() {
        return new Storage(tempDir.resolve("ruby.txt").toString());
    }

    @Test
    void parseDateTime_validDateTime_returnsDateTime() throws RubyException {
        assertEquals(LocalDateTime.of(2026, 8, 28, 18, 0), Parser.parseDateTime("2026-08-28 1800"));
    }

    @Test
    void parseDateTime_validDateOnly_returnsMidnight() throws RubyException {
        assertEquals(LocalDateTime.of(2026, 8, 28, 0, 0), Parser.parseDateTime("2026-08-28"));
    }

    @Test
    void parseDateTime_invalidText_throws() {
        assertThrows(RubyException.class, () -> Parser.parseDateTime("not a date"));
    }

    @Test
    void parseDateTime_nonExistentDate_throws() {
        assertThrows(RubyException.class, () -> Parser.parseDateTime("2026-02-30"));
    }

    @Test
    void formatDateTime_midnight_returnsDateOnly() {
        assertEquals("Oct 15 2019", Parser.formatDateTime(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    @Test
    void formatDateTime_withTime_returnsDateAndTime() {
        assertEquals("Oct 15 2019, 18:00",
                Parser.formatDateTime(LocalDateTime.of(2019, 10, 15, 18, 0)));
    }

    @Test
    void parseDeadline_validInput_parsesDescriptionAndDeadline() throws RubyException {
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2019-10-15 1800");
        assertEquals("D | 0 | return book | 2019-10-15T18:00", deadline.toDataString());
    }

    @Test
    void parseDeadline_missingBy_throws() {
        assertThrows(RubyException.class, () -> Parser.parseDeadline("deadline return book"));
    }

    @Test
    void parseDeadline_missingDescription_throws() {
        assertThrows(RubyException.class, () -> Parser.parseDeadline("deadline /by 2019-10-15"));
    }

    @Test
    void parseDeadline_missingDeadline_throws() {
        assertThrows(RubyException.class, () -> Parser.parseDeadline("deadline return book /by"));
    }

    @Test
    void parseEvent_validInput_parsesDescriptionStartEnd() throws RubyException {
        Event event = Parser.parseEvent("event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        assertEquals("E | 0 | project meeting | 2019-10-15T14:00 | 2019-10-15T16:00",
                event.toDataString());
    }

    @Test
    void parseEvent_missingTo_throws() {
        assertThrows(RubyException.class, () -> Parser.parseEvent("event project meeting /from 2019-10-15 1400"));
    }

    @Test
    void parseEvent_missingDescriptionStartOrEnd_throws() {
        assertThrows(RubyException.class, () -> Parser.parseEvent("event /from 2019-10-15 /to 2019-10-16"));
        assertThrows(RubyException.class, () -> Parser.parseEvent("event meeting /from /to 2019-10-16"));
        assertThrows(RubyException.class, () -> Parser.parseEvent("event meeting /from 2019-10-15 /to"));
    }

    @Test
    void parseDeadline_delimiterInsideDescriptionIsNotTreatedAsDelimiter() {
        assertThrows(RubyException.class, () -> Parser.parseDeadline("deadline revise/bytomorrow"));
    }

    @Test
    void parseDateTime_blankAndInvalidCalendarDate_throw() {
        assertThrows(RubyException.class, () -> Parser.parseDateTime("   "));
        assertThrows(RubyException.class, () -> Parser.parseDateTime("2026-02-30"));
    }

    @Test
    void parseEvent_endNotAfterStart_throws() {
        assertThrows(RubyException.class, () ->
                Parser.parseEvent("event meeting /from 2026-08-28 1800 /to 2026-08-28 1800"));
        assertThrows(RubyException.class, () ->
                Parser.parseEvent("event meeting /from 2026-08-28 1800 /to 2026-08-28 1700"));
    }

    @Test
    void parseTaskIndex_validNumber_returnsZeroBased() throws RubyException {
        assertEquals(2, Parser.parseTaskIndex("mark 3", "mark"));
    }

    @Test
    void parseTaskIndex_missingNumber_throws() {
        assertThrows(RubyException.class, () -> Parser.parseTaskIndex("mark", "mark"));
    }

    @Test
    void parseTaskIndex_nonNumber_throws() {
        assertThrows(RubyException.class, () -> Parser.parseTaskIndex("mark abc", "mark"));
    }

    @Test
    void parseTaskIndex_zeroOrNegative_throws() {
        assertThrows(RubyException.class, () -> Parser.parseTaskIndex("mark 0", "mark"));
        assertThrows(RubyException.class, () -> Parser.parseTaskIndex("mark -1", "mark"));
    }

    @Test
    void parse_emptyInput_throws() {
        assertThrows(RubyException.class, () -> Parser.parse(""));
        assertThrows(RubyException.class, () -> Parser.parse("   "));
    }

    @Test
    void parse_extraOrOuterSpaces_throws() {
        assertThrows(RubyException.class, () -> Parser.parse(" todo read book"));
        assertThrows(RubyException.class, () -> Parser.parse("todo read book "));
        assertThrows(RubyException.class, () -> Parser.parse("todo  read book"));
        assertThrows(RubyException.class, () -> Parser.parse("todo\tread book"));
    }

    @Test
    void parse_unknownCommand_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("dance"));
    }

    @Test
    void parse_todoCommand_returnsTodoCommand() throws RubyException {
        assertInstanceOf(TodoCommand.class, Parser.parse("todo read book"));
    }

    @Test
    void parse_knownCommands_returnTheirCommandTypes() throws RubyException {
        assertInstanceOf(DeadlineCommand.class, Parser.parse("deadline submit /by 2026-08-28"));
        assertInstanceOf(EventCommand.class, Parser.parse("event meeting /from 2026-08-28 /to 2026-08-29"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    void parse_commandWordPrefixWithoutWhitespace_throwsUnknownCommand() {
        assertThrows(RubyException.class, () -> Parser.parse("todoist read book"));
        assertThrows(RubyException.class, () -> Parser.parse("list extra"));
    }

    @Test
    void parse_findCommand_returnsFindCommand() throws RubyException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    void parse_findCommandWithoutKeyword_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("find"));
    }

    @Test
    void parse_contactAddWithAllFields_parsesFieldsInOrder() throws RubyException {
        Command command = Parser.parse(
                "contact add John Doe /phone 91234567 /email john@example.com /address 123 Street");
        ContactList contacts = new ContactList();

        String response = command.execute(new TaskList(), contacts, storage());

        assertEquals("Saved. I never forget a name:\n"
                + "  John Doe | 91234567 | john@example.com | 123 Street\n"
                + "That's 1 contact in your circle.", response);
    }

    @Test
    void parse_contactAddTagsInAnyOrder_storesCanonicalOrder() throws RubyException {
        Command command = Parser.parse("contact add John /email john@example.com /phone 91234567");
        ContactList contacts = new ContactList();

        String response = command.execute(new TaskList(), contacts, storage());

        assertEquals("Saved. I never forget a name:\n"
                + "  John | 91234567 | john@example.com\n"
                + "That's 1 contact in your circle.", response);
    }

    @Test
    void parse_contactAddWithNameOnly_returnsAddContactCommand() throws RubyException {
        assertInstanceOf(AddContactCommand.class, Parser.parse("contact add John Doe"));
    }

    @Test
    void parse_contactList_returnsListContactsCommand() throws RubyException {
        assertInstanceOf(ListContactsCommand.class, Parser.parse("contact list"));
    }

    @Test
    void parse_contactDelete_returnsDeleteContactCommand() throws RubyException {
        assertInstanceOf(DeleteContactCommand.class, Parser.parse("contact delete 1"));
    }

    @Test
    void parse_contactWithoutSubcommand_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("contact"));
    }

    @Test
    void parse_contactUnknownSubcommand_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("contact frobnicate"));
    }

    @Test
    void parse_contactListWithArguments_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("contact list extra"));
    }

    @Test
    void parse_contactAddMissingName_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("contact add /phone 91234567"));
    }

    @Test
    void parse_contactAddUnknownTag_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("contact add John /telegram @john"));
    }

    @Test
    void parse_contactAddMissingValue_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("contact add John /phone"));
    }

    @Test
    void parse_contactAddRepeatedTag_throws() {
        String input = "contact add John /phone 91234567 /phone 98765432";
        assertThrows(RubyException.class, () -> Parser.parse(input));
    }

    @Test
    void parse_contactAddInvalidPhone_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("contact add John /phone abc"));
    }

    @Test
    void parse_contactAddInvalidEmail_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("contact add John /email not-an-email"));
    }

    @Test
    void parse_contactAddFieldSeparator_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("contact add John | Doe"));
    }

    @Test
    void parse_contactDeleteInvalidIndex_throws() {
        assertThrows(RubyException.class, () -> Parser.parse("contact delete abc"));
        assertThrows(RubyException.class, () -> Parser.parse("contact delete 0"));
    }

    @Test
    void parse_contactAddAddressWithSpaces_preservesSingleSpacing() throws RubyException {
        Command command = Parser.parse("contact add Jane /address 123 Main Street");

        String response = command.execute(new TaskList(), new ContactList(), storage());

        assertEquals("Saved. I never forget a name:\n  Jane | 123 Main Street\nThat's 1 contact in your circle.",
                response);
    }

    @Test
    void parse_contactAddInvalidTagAndEmptyAddress_throw() {
        assertThrows(RubyException.class, () -> Parser.parse("contact add Jane /fax 123"));
        assertThrows(RubyException.class, () -> Parser.parse("contact add Jane /address"));
    }
}

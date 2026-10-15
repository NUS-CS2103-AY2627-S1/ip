package atlas.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import atlas.AtlasException;
import atlas.client.Client;
import atlas.task.Deadline;
import atlas.task.Event;
import atlas.task.Task;
import atlas.task.Todo;

/** Tests command recognition, task parsing and client parsing. */
public class ParserTest {

    /** Syntax of the client add command, used in its error messages. */
    private static final String CLIENT_ADD_SYNTAX = "client add <name> [/phone <number>] [/email <address>]";

    @Test
    void parsesKnownCommandsAndRejectsUnknownPrefixes() {
        assertEquals(Command.TODO, Parser.parseCommand("todo read"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline submit /by 2026-09-01"));
        assertEquals(Command.EVENT, Parser.parseCommand("event meeting /from 2pm /to 3pm"));
        assertEquals(Command.FIND, Parser.parseCommand("find book"));
        assertEquals(Command.CLIENT, Parser.parseCommand("client add Bob"));
        assertNull(Parser.parseCommand("today"));
        assertNull(Parser.parseCommand("todoist read"));
    }

    @Test
    void parsesFindKeywordsAndRejectsBlankKeyword() throws AtlasException {
        assertArrayEquals(new String[]{"book"}, Parser.parseKeywords("find book", Command.FIND));
        assertArrayEquals(new String[]{"book", "paper"},
                Parser.parseKeywords("find  book   paper", Command.FIND));

        AtlasException exception = assertThrows(AtlasException.class, () ->
                Parser.parseKeywords("find ", Command.FIND));
        assertEquals("What shall I seek, mortal? Use: find <keyword>", exception.getMessage());
    }

    @Test
    void parsesTaskIndexesAndReportsNonNumbersAsInvalid() {
        assertEquals(3, Parser.parseIndex("mark 3", Command.MARK));
        assertEquals(0, Parser.parseIndex("delete 0", Command.DELETE));
        assertEquals(-1, Parser.parseIndex("unmark many", Command.UNMARK));
        assertEquals(2, Parser.parseIndex("mark 2 ", Command.MARK));
        assertEquals(-1, Parser.parseIndex("mark", Command.MARK));
    }

    @Test
    void parsesTodoCommand() throws AtlasException {
        Task task = Parser.parseTask("todo read the Odyssey", Command.TODO);

        Todo todo = assertInstanceOf(Todo.class, task);
        assertEquals("read the Odyssey", todo.getDescription());
    }

    @Test
    void parsesDeadlineCommand() throws AtlasException {
        Task task = Parser.parseTask("deadline return book /by 2026-09-01", Command.DEADLINE);

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 1), deadline.getBy());
    }

    @Test
    void parsesEventCommand() throws AtlasException {
        Task task = Parser.parseTask("event project meeting /from 2pm /to 4pm", Command.EVENT);

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    void rejectsMalformedDeadlineAndEventCommands() {
        AtlasException missingBy = assertThrows(AtlasException.class, () ->
                Parser.parseTask("deadline submit", Command.DEADLINE));
        AtlasException invalidDate = assertThrows(AtlasException.class, () ->
                Parser.parseTask("deadline submit /by tomorrow", Command.DEADLINE));
        AtlasException missingTo = assertThrows(AtlasException.class, () ->
                Parser.parseTask("event meeting /from 2pm", Command.EVENT));

        assertEquals("The Fates weave on schedule. Use: deadline <desc> /by <when>", missingBy.getMessage());
        assertEquals("The Fates cannot read that date, mortal. Use: deadline <desc> /by yyyy-mm-dd",
                invalidDate.getMessage());
        assertEquals("Icarus never planned a landing either. Use: event <desc> /from <start> /to <end>",
                missingTo.getMessage());
    }

    @Test
    void rejectsTaskCommandsWithBlankDescriptions() {
        AtlasException exception = assertThrows(AtlasException.class, () ->
                Parser.parseTask("todo  ", Command.TODO));

        assertEquals("Name your labour, mortal: todo <desc>", exception.getMessage());
    }

    @Test
    void parsesClientSubcommandsAndRejectsUnknownOnes() throws AtlasException {
        assertEquals(ClientCommand.ADD, Parser.parseClientSubcommand("client add Bob"));
        assertEquals(ClientCommand.LIST, Parser.parseClientSubcommand("client list"));
        assertEquals(ClientCommand.FIND, Parser.parseClientSubcommand("client find Bob"));
        assertEquals(ClientCommand.DELETE, Parser.parseClientSubcommand("client delete 1"));

        AtlasException bare = assertThrows(AtlasException.class, () ->
                Parser.parseClientSubcommand("client"));
        AtlasException unknown = assertThrows(AtlasException.class, () ->
                Parser.parseClientSubcommand("client foo"));

        assertTrue(bare.getMessage().startsWith("The Oracle is silent on that client command."));
        assertEquals(bare.getMessage(), unknown.getMessage());
    }

    @Test
    void parsesClientAddWithOptionalFieldsInEitherOrder() throws AtlasException {
        Client nameOnly = Parser.parseClient("client add Bob");

        assertEquals("Bob", nameOnly.getName());
        assertEquals("", nameOnly.getPhone());
        assertEquals("", nameOnly.getEmail());

        Client withPhone = Parser.parseClient("client add Mary Jane /phone 91234567");

        assertEquals("Mary Jane", withPhone.getName());
        assertEquals("91234567", withPhone.getPhone());
        assertEquals("", withPhone.getEmail());

        Client reversed = Parser.parseClient("client add Chen Wei /email wei@x.com /phone 8123 4567");

        assertEquals("Chen Wei", reversed.getName());
        assertEquals("8123 4567", reversed.getPhone());
        assertEquals("wei@x.com", reversed.getEmail());
    }

    @Test
    void rejectsClientAddWithoutNameOrWithEmptyMarkerValues() {
        AtlasException noName = assertThrows(AtlasException.class, () ->
                Parser.parseClient("client add"));
        AtlasException markerWithoutName = assertThrows(AtlasException.class, () ->
                Parser.parseClient("client add /phone 91234567"));
        AtlasException noPhone = assertThrows(AtlasException.class, () ->
                Parser.parseClient("client add Bob /phone"));
        AtlasException noEmail = assertThrows(AtlasException.class, () ->
                Parser.parseClient("client add Bob /email"));

        assertEquals("Name your client, mortal: " + CLIENT_ADD_SYNTAX, noName.getMessage());
        assertEquals(noName.getMessage(), markerWithoutName.getMessage());
        assertEquals("A number must follow /phone. Use: " + CLIENT_ADD_SYNTAX, noPhone.getMessage());
        assertEquals("An address must follow /email. Use: " + CLIENT_ADD_SYNTAX, noEmail.getMessage());
    }

    @Test
    void rejectsAdjacentMarkersThatShareOneSpace() throws AtlasException {
        AtlasException noPhone = assertThrows(AtlasException.class, () ->
                Parser.parseClient("client add B /phone /email x@y.com"));
        AtlasException noEmail = assertThrows(AtlasException.class, () ->
                Parser.parseClient("client add A /email /phone 2"));

        assertEquals("A number must follow /phone. Use: " + CLIENT_ADD_SYNTAX, noPhone.getMessage());
        assertEquals("An address must follow /email. Use: " + CLIENT_ADD_SYNTAX, noEmail.getMessage());

        Client separated = Parser.parseClient("client add B /phone 1 /email x@y.com");

        assertEquals("B", separated.getName());
        assertEquals("1", separated.getPhone());
        assertEquals("x@y.com", separated.getEmail());
    }

    @Test
    void parsesClientFindKeywordsAndClientDeleteArguments() throws AtlasException {
        assertArrayEquals(new String[]{"Bob"}, Parser.parseClientKeywords("client find Bob"));
        assertArrayEquals(new String[]{"Bob", "x.com"},
                Parser.parseClientKeywords("client find Bob  x.com"));

        AtlasException blank = assertThrows(AtlasException.class, () ->
                Parser.parseClientKeywords("client find"));
        assertEquals("Whom shall I seek, mortal? Use: client find <keyword>", blank.getMessage());

        assertEquals("2", Parser.parseClientDeleteArgument("client delete 2 "));
        assertEquals("", Parser.parseClientDeleteArgument("client delete"));
        assertEquals(3, Parser.parseNumber(" 3 "));
        assertEquals(-1, Parser.parseNumber("abc"));
    }

    @Test
    void recognisesThePriorityCommand() {
        assertEquals(Command.PRIORITY, Parser.parseCommand("priority"));
        assertEquals(Command.PRIORITY, Parser.parseCommand("priority 2 high"));

        assertNull(Parser.parseCommand("priorities"));
    }

    @Test
    void parsesPriorityNumbersAndLevels() throws AtlasException {
        assertEquals(2, Parser.parsePriorityNumber("priority 2 high"));
        assertEquals(2, Parser.parsePriorityNumber("priority 2 "));
        assertEquals(-1, Parser.parsePriorityNumber("priority abc high"));
        assertEquals(3, Parser.parsePriorityNumber("priority 3\thigh"));

        AtlasException noArgument = assertThrows(AtlasException.class, () ->
                Parser.parsePriorityNumber("priority"));
        AtlasException blankArgument = assertThrows(AtlasException.class, () ->
                Parser.parsePriorityNumber("priority "));

        assertEquals("Which labour shall I rank? Use: " + Parser.PRIORITY_SYNTAX, noArgument.getMessage());
        assertEquals(noArgument.getMessage(), blankArgument.getMessage());

        assertEquals("high", Parser.parsePriorityLevel("priority 1 high"));
        assertEquals("medium", Parser.parsePriorityLevel("priority 2 medium"));
        assertEquals("low", Parser.parsePriorityLevel("priority 3 low"));
        assertEquals("none", Parser.parsePriorityLevel("priority 4 none"));
        assertEquals("high", Parser.parsePriorityLevel("priority 5  high"));
        assertEquals("high", Parser.parsePriorityLevel("priority 6\thigh"));
    }

    @Test
    void rejectsPriorityCommandsWithoutAUsableLevel() {
        AtlasException missing = assertThrows(AtlasException.class, () ->
                Parser.parsePriorityLevel("priority 1"));
        AtlasException trailingSpace = assertThrows(AtlasException.class, () ->
                Parser.parsePriorityLevel("priority 1 "));
        AtlasException unknown = assertThrows(AtlasException.class, () ->
                Parser.parsePriorityLevel("priority 1 urgent"));
        AtlasException extraWord = assertThrows(AtlasException.class, () ->
                Parser.parsePriorityLevel("priority 1 high extra"));

        assertEquals("Rank it high, medium or low, or none to clear it. Use: " + Parser.PRIORITY_SYNTAX,
                missing.getMessage());
        assertEquals(missing.getMessage(), trailingSpace.getMessage());
        assertEquals("The Fates know only high, medium, low or none. Use: " + Parser.PRIORITY_SYNTAX,
                unknown.getMessage());
        assertEquals(unknown.getMessage(), extraWord.getMessage());
    }

    @Test
    void parsesEventValuesWhenTheMarkersShareOneSpace() throws AtlasException {
        // " /from " ends with the very space that " /to " begins with, so the
        // start value has nowhere to run. This used to kill the whole process.
        AtlasException noStart = assertThrows(AtlasException.class, () ->
                Parser.parseTask("event ritual /from /to 4pm", Command.EVENT));
        AtlasException nothingAtAll = assertThrows(AtlasException.class, () ->
                Parser.parseTask("event /from /to 4pm", Command.EVENT));

        assertEquals("Even Icarus launched from somewhere. Use: event <desc> /from <start> /to <end>",
                noStart.getMessage());
        assertEquals("Name your labour, mortal: event <desc> /from <start> /to <end>",
                nothingAtAll.getMessage());
    }

    @Test
    void rejectsARepeatedMarkerInsteadOfStoringItInTheValue() {
        AtlasException twoStarts = assertThrows(AtlasException.class, () ->
                Parser.parseTask("event ritual /from 2pm /from 3pm /to 5pm", Command.EVENT));
        AtlasException twoEnds = assertThrows(AtlasException.class, () ->
                Parser.parseTask("event ritual /from 2pm /to 3pm /to 5pm", Command.EVENT));
        AtlasException twoDates = assertThrows(AtlasException.class, () ->
                Parser.parseTask("deadline submit /by 2026-09-01 /by 2026-09-02", Command.DEADLINE));
        AtlasException twoNumbers = assertThrows(AtlasException.class, () ->
                Parser.parseClient("client add Bob /phone 1 /phone 2"));
        AtlasException twoAddresses = assertThrows(AtlasException.class, () ->
                Parser.parseClient("client add Bob /email a@b.com /email c@d.com"));

        assertEquals("One departure is enough, mortal. Use: event <desc> /from <start> /to <end>",
                twoStarts.getMessage());
        assertEquals("One landing is enough, mortal. Use: event <desc> /from <start> /to <end>",
                twoEnds.getMessage());
        assertEquals("One reckoning is enough, mortal. Use: deadline <desc> /by <when>",
                twoDates.getMessage());
        assertEquals("One number is enough, mortal. Use: " + CLIENT_ADD_SYNTAX, twoNumbers.getMessage());
        assertEquals("One address is enough, mortal. Use: " + CLIENT_ADD_SYNTAX, twoAddresses.getMessage());
    }

    @Test
    void treatsLeadingWhitespaceAsInsignificant() {
        assertEquals(Command.TODO, Parser.parseCommand(" todo read"));
        assertEquals(Command.BYE, Parser.parseCommand("   bye"));
        assertEquals(Command.CLIENT, Parser.parseCommand("\tclient list"));
        assertNull(Parser.parseCommand("   "));
    }

    @Test
    void rejectsAnEventWhoseStartAndEndAreTheSameMoment() throws AtlasException {
        AtlasException sameSuffix = assertThrows(AtlasException.class, () ->
                Parser.parseTask("event vigil /from 2pm /to 2pm", Command.EVENT));
        AtlasException sameClockReadTwice = assertThrows(AtlasException.class, () ->
                Parser.parseTask("event vigil /from 14:00 /to 2pm", Command.EVENT));
        AtlasException sameCompactForm = assertThrows(AtlasException.class, () ->
                Parser.parseTask("event vigil /from 0900 /to 9:00am", Command.EVENT));

        assertTrue(sameSuffix.getMessage().startsWith("Time flows one way, mortal:"));
        assertEquals(sameSuffix.getMessage(), sameClockReadTwice.getMessage());
        assertEquals(sameSuffix.getMessage(), sameCompactForm.getMessage());

        // A value that is not a clock time cannot be ordered, so it is accepted.
        Event loose = assertInstanceOf(Event.class, Parser.parseTask(
                "event dinner /from 7pm at marina /to 7pm at marina", Command.EVENT));
        assertEquals("7pm at marina", loose.getFrom());

        // An end after midnight is a real thing, so a later start is allowed.
        Event overnight = assertInstanceOf(Event.class, Parser.parseTask(
                "event vigil /from 11pm /to 1am", Command.EVENT));
        assertEquals("11pm", overnight.getFrom());
    }
}

package rem.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import rem.command.AddCommand;
import rem.command.CommandType;
import rem.command.DeleteCommand;
import rem.command.DeleteNoteCommand;
import rem.command.ExitCommand;
import rem.command.FindCommand;
import rem.command.ListCommand;
import rem.command.MarkCommand;
import rem.command.NoteCommand;
import rem.command.OnCommand;
import rem.command.UnmarkCommand;
import rem.exception.EmptyDescriptionException;
import rem.exception.EmptyNoteException;
import rem.exception.InvalidDateException;
import rem.exception.InvalidDeadlineFormatException;
import rem.exception.InvalidEventFormatException;
import rem.exception.InvalidTaskNumberException;
import rem.exception.NoteTooLongException;
import rem.exception.RemException;
import rem.exception.UnknownCommandException;
import rem.task.Deadline;
import rem.task.Event;
import rem.task.Task;
import rem.task.Todo;

/**
 * Tests command argument validation and task construction by {@link Parser}.
 */
public class ParserTest {
    @Test
    public void parse_allCommandTypes_expectedCommandClassesReturned() throws RemException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2026-10-01"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(NoteCommand.class, Parser.parse("note 1 remember"));
        assertInstanceOf(DeleteNoteCommand.class, Parser.parse("deletenote 1"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline submit /by 2026-10-01"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meet /from 2026-10-01 /to 2026-10-02"));
    }

    @Test
    public void parse_nullBlankUnknownOrExtraArguments_exceptionThrown() {
        assertThrows(UnknownCommandException.class, () -> Parser.parse(null));
        assertThrows(UnknownCommandException.class, () -> Parser.parse("   "));
        assertThrows(UnknownCommandException.class, () -> Parser.parse("unknown"));
        RemException listException = assertThrows(RemException.class, () ->
                Parser.parse("list extra"));
        RemException byeException = assertThrows(RemException.class, () ->
                Parser.parse("bye later"));

        assertEquals("list does not take arguments.", listException.getMessage());
        assertEquals("bye does not take arguments.", byeException.getMessage());
    }

    @Test
    public void parse_multilineOrControlInput_exceptionThrown() {
        for (String input : List.of("todo a\nb", "todo a\rb", "todo a\u0000b",
                "todo a\u2028b", "todo a\u2029b")) {
            RemException exception = assertThrows(RemException.class, () ->
                    Parser.parse(input), input);
            assertEquals("Use a single line without control characters.", exception.getMessage());
        }
    }

    @Test
    public void getCommandType_mixedCaseKnownCommand_commandTypeReturned() {
        assertEquals(CommandType.DEADLINE, Parser.getCommandType("DeAdLiNe submit report"));
    }

    @Test
    public void getCommandType_blankOrUnknownCommand_unknownReturned() {
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType("   "));
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType("deadlines submit report"));
    }

    @Test
    public void parseTaskNumber_positiveInteger_numberReturned() throws InvalidTaskNumberException {
        assertEquals(12, Parser.parseTaskNumber("mark   12", "mark"));
    }

    @Test
    public void parseTaskNumber_missingNonNumericOrNonPositiveNumber_exceptionThrown() {
        assertThrows(InvalidTaskNumberException.class, () -> Parser.parseTaskNumber("mark", "mark"));
        assertThrows(InvalidTaskNumberException.class, () ->
                Parser.parseTaskNumber("mark two", "mark"));
        assertThrows(InvalidTaskNumberException.class, () ->
                Parser.parseTaskNumber("mark 0", "mark"));
        assertThrows(InvalidTaskNumberException.class, () ->
                Parser.parseTaskNumber("mark -1", "mark"));
    }

    @Test
    public void parseDate_validDate_dateReturned() throws InvalidDateException {
        assertEquals(LocalDate.of(2026, 8, 29), Parser.parseDate("on 2026-08-29"));
    }

    @Test
    public void parseDate_missingDateDateTimeOrInvalidDate_exceptionThrown() {
        assertThrows(InvalidDateException.class, () -> Parser.parseDate("on"));
        assertThrows(InvalidDateException.class, () -> Parser.parseDate("on 2026-08-29 1200"));
        assertThrows(InvalidDateException.class, () -> Parser.parseDate("on tomorrow"));
    }

    @Test
    public void commandParsing_turkishDefaultLocale_commandsRemainCaseInsensitive()
            throws RemException {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertEquals(CommandType.FIND, Parser.getCommandType("find book"));
            assertInstanceOf(Deadline.class,
                    Parser.createTask("DEADLINE submit report /BY 2026-08-29"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void parseKeyword_validKeyword_keywordReturned() throws EmptyDescriptionException {
        assertEquals("read book", Parser.parseKeyword("find   read book"));
    }

    @Test
    public void parseKeyword_missingKeyword_exceptionThrown() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parseKeyword("find"));
    }

    @Test
    public void createTask_validTodo_todoReturned() throws RemException {
        Task task = Parser.createTask("ToDo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void createTask_allTaskTypesWithNotes_notesAttached() throws RemException {
        Task todo = Parser.createTask("todo read book /note Borrow it from Alice");
        Task deadline = Parser.createTask(
                "deadline submit report /by 2026-08-29 /note Include /by details");
        Task event = Parser.createTask("event workshop /from 2026-08-29 0900 "
                + "/to 2026-08-29 1700 /note Bring notes");

        assertEquals("Borrow it from Alice", todo.getNote());
        assertEquals("Include /by details", deadline.getNote());
        assertEquals("Bring notes", event.getNote());
    }

    @Test
    public void parse_noteCommands_commandsReturned() throws RemException {
        assertInstanceOf(NoteCommand.class, Parser.parse("NoTe 2 Remember this"));
        assertInstanceOf(DeleteNoteCommand.class, Parser.parse("DeLeTeNoTe 2"));
    }

    @Test
    public void noteParsing_missingOrInvalidTaskNumber_exceptionThrown() {
        for (String input : List.of("note", "note abc text", "note 0 text", "note -1 text",
                "note 999999999999999999 text")) {
            assertThrows(InvalidTaskNumberException.class, () -> Parser.parse(input), input);
        }
    }

    @Test
    public void noteParsing_missingOrTooLongNote_exceptionThrown() {
        String longNote = "a".repeat(201);

        assertThrows(EmptyNoteException.class, () -> Parser.parse("note 1"));
        assertThrows(EmptyNoteException.class, () -> Parser.createTask("todo read /note"));
        assertThrows(NoteTooLongException.class, () -> Parser.parse("note 1 " + longNote));
        assertThrows(NoteTooLongException.class, () ->
                Parser.createTask("todo read /note " + longNote));
    }

    @Test
    public void noteParsing_twoHundredEmoji_noteAccepted() throws RemException {
        String note = "😀".repeat(200);

        Task task = Parser.createTask("todo celebrate /note " + note);

        assertEquals(note, task.getNote());
    }

    @Test
    public void createTask_noteLikeText_keptInDescription()
            throws RemException {
        Task task = Parser.createTask("todo review /noteworthy examples /noteish text");

        assertEquals("review /noteworthy examples /noteish text", task.getDescription());
        assertFalse(task.hasNote());
    }

    @Test
    public void createTask_noteFieldIsCaseInsensitiveAndMustBeStandalone() throws RemException {
        Task notedTask = Parser.createTask("todo read /NoTe remember");
        Task slashWithoutWhitespace = Parser.createTask("todo read/note remember");

        assertEquals("remember", notedTask.getNote());
        assertEquals("read/note remember", slashWithoutWhitespace.getDescription());
        assertFalse(slashWithoutWhitespace.hasNote());
    }

    @Test
    public void createTask_validDeadline_deadlineReturned() throws RemException {
        Deadline deadline = assertInstanceOf(Deadline.class,
                Parser.createTask("deadline submit report /by 2026-08-29 1800"));

        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 8, 29, 18, 0), deadline.getBy());
    }

    @Test
    public void createTask_validEvent_eventReturned() throws RemException {
        Event event = assertInstanceOf(Event.class,
                Parser.createTask("event workshop /from 2026-08-29 0900 /to 2026-08-30 1700"));

        assertEquals("workshop", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 8, 29, 9, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 30, 17, 0), event.getTo());
    }

    @Test
    public void createTask_emptyDescriptions_exceptionThrown() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.createTask("todo"));
        assertThrows(EmptyDescriptionException.class, () -> Parser.createTask("deadline"));
        assertThrows(EmptyDescriptionException.class, () -> Parser.createTask("event"));
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.createTask("deadline /by 2026-08-29"));
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.createTask("event /from 2026-08-29 /to 2026-08-30"));
    }

    @Test
    public void createTask_invalidDeadlineDetails_exceptionThrown() {
        assertThrows(InvalidDeadlineFormatException.class, () ->
                Parser.createTask("deadline submit report"));
        assertThrows(InvalidDeadlineFormatException.class, () ->
                Parser.createTask("deadline submit report /by tomorrow"));
    }

    @Test
    public void createTask_invalidEventDetails_exceptionThrown() {
        assertThrows(InvalidEventFormatException.class, () ->
                Parser.createTask("event workshop /from 2026-08-29"));
        assertThrows(InvalidEventFormatException.class, () ->
                Parser.createTask("event workshop /from tomorrow /to 2026-08-30"));
        assertThrows(InvalidEventFormatException.class, () ->
                Parser.createTask("event workshop /from 2026-08-30 /to 2026-08-29"));
    }

    @Test
    public void createTask_nonAddCommand_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> Parser.createTask("list"));
    }
    @Test
    public void createTask_whitespaceAndLiteralNoteText_preserved() throws RemException {
        Deadline task = assertInstanceOf(Deadline.class, Parser.createTask(
                "  deadline\tread  book\t/BY\t2026-10-01   1200\t/note literal /note /by text  "));
        assertEquals("read  book", task.getDescription());
        assertEquals("literal /note /by text", task.getNote());
        assertEquals(LocalDateTime.of(2026, 10, 1, 12, 0), task.getBy());
        assertInstanceOf(Event.class, Parser.createTask(
                "event\tmeeting\t/from\t2026-10-01\t0900\t/to\t2026-10-01\t1000"));
    }

    @Test
    public void createTask_invalidSchedulingFields_rejected() {
        for (String input : List.of(
                "deadline report /by 2026-10-01 /by 2026-10-02",
                "deadline report /by2026-10-01", "deadline report /by",
                "deadline report /to 2026-10-01 /by 2026-10-02",
                "event meeting /to 2026-10-02 /from 2026-10-01",
                "event meeting /from 2026-10-01 /from 2026-10-01 /to 2026-10-02",
                "event meeting /from 2026-10-01 /to 2026-10-02 /to 2026-10-03",
                "event meeting /from 2026-10-01 /to 2026-10-01",
                "event meeting /from 2026-10-01 /to",
                "event meeting /from 2026-10-01 /note missing end",
                "todo read /by 2026-10-01")) {
            assertThrows(RemException.class, () -> Parser.createTask(input), input);
        }
    }

    @Test
    public void parse_invalidDatesAndTimes_rejectedAcrossCommands() {
        for (String date : List.of("2026-02-30", "29/2/2026", "2026-04-31",
                "2026-13-01", "2026-10-01 2400", "2026-10-01 1260")) {
            assertThrows(RemException.class, () -> Parser.parse("deadline report /by " + date), date);
            assertThrows(RemException.class, () -> Parser.parse("on " + date), date);
            assertThrows(RemException.class, () ->
                    Parser.parse("event meeting /from " + date + " /to 2027-01-01"), date);
        }
    }

}

package pulbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import pulbot.PulbotException;
import pulbot.storage.Storage;
import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.TaskList;
import pulbot.task.Todo;
import pulbot.ui.Ui;

/** Tests conversion of user input into commands. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_validCommands_returnsMatchingCommandTypes() throws PulbotException {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(OnCommand.class, parser.parse("on 2/12/2019"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline return book /by 2/12/2019 1800"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event lecture /from 2/12/2019 1400 /to 2/12/2019 1600"));
    }

    @Test
    public void parse_missingRequiredDetails_throwsException() {
        assertThrows(PulbotException.class, () -> parser.parse("todo"));
        assertThrows(PulbotException.class, () -> parser.parse("find"));
        assertThrows(PulbotException.class, () -> parser.parse("deadline return book"));
        assertThrows(PulbotException.class, () -> parser.parse("event lecture"));
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        assertThrows(PulbotException.class, () -> parser.parse("   "));
        assertThrows(PulbotException.class, () -> parser.parse("unknown command"));
        assertThrows(PulbotException.class, () -> parser.parse("listing"));
        assertThrows(PulbotException.class, () -> parser.parse("bye now"));
        assertThrows(PulbotException.class, () -> parser.parse("list all"));
    }

    @Test
    public void parse_accidentalWhitespace_returnsMatchingCommandTypes() throws PulbotException {
        assertInstanceOf(ListCommand.class, parser.parse("  list  "));
        assertInstanceOf(AddCommand.class, parser.parse("todo    read book"));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline report   /by   2/12/2019 1800"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event class  /from  2/12/2019 1400   /to  2/12/2019 1600"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "todo     read book",
        "  todo read book",
        "todo read book  "
    })
    public void parse_todoWithWhitespaceVariants_returnsAddCommand(String input)
            throws PulbotException {
        assertInstanceOf(AddCommand.class, parser.parse(input));
    }

    @ParameterizedTest
    @CsvSource({
        "29/2/2024, 2024-02-29",
        "1/1/2026, 2026-01-01",
        "31/12/2026, 2026-12-31"
    })
    public void parse_validCalendarDate_returnsExpectedDate(String input, String expected)
            throws PulbotException {
        TaskList tasks = new TaskList();
        RecordingDateUi ui = new RecordingDateUi();

        parser.parse("on " + input).execute(tasks, ui, new NoOpStorage());

        assertEquals(java.time.LocalDate.parse(expected), ui.displayedDate);
    }

    @ParameterizedTest
    @ValueSource(strings = {"29/2/2023", "31/4/2026", "0/1/2026", "1/13/2026"})
    public void parse_invalidCalendarDate_throwsException(String input) {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("on " + input));
    }

    @Test
    public void parse_repeatedOrMisplacedMarkers_throwsHelpfulException() {
        PulbotException repeatedBy = assertThrows(PulbotException.class, () ->
                parser.parse("deadline report /by 2/12/2019 1800 /by 3/12/2019 1800"));
        assertEquals("Please use: deadline <description> /by <when>.", repeatedBy.getMessage());

        PulbotException reversedEventMarkers = assertThrows(PulbotException.class, () ->
                parser.parse("event class /to 2/12/2019 1600 /from 2/12/2019 1400"));
        assertEquals("Please use: event <description> /from <start> /to <end>.",
                reversedEventMarkers.getMessage());
    }

    @Test
    public void parse_eventWithInvalidDateRange_throwsException() {
        IllegalArgumentException equalTimes = assertThrows(IllegalArgumentException.class, () ->
                parser.parse("event class /from 2/12/2019 1400 /to 2/12/2019 1400"));
        assertEquals("The event end time must be after its start time.", equalTimes.getMessage());

        assertThrows(IllegalArgumentException.class, () ->
                parser.parse("event class /from 2/12/2019 1600 /to 2/12/2019 1400"));
    }

    @Test
    public void parse_invalidDates_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("on 2019-12-02"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("on 31/2/2019"));
        assertThrows(IllegalArgumentException.class, () ->
                parser.parse("deadline return book /by 31/2/2019 1800"));
        assertThrows(IllegalArgumentException.class, () ->
                parser.parse("event lecture /from tomorrow /to 2/12/2019 1600"));
    }

    @Test
    public void parse_addCommands_preservesTaskDetails() throws PulbotException {
        TaskList tasks = new TaskList();
        Ui ui = new SilentUi();
        Storage storage = new NoOpStorage();

        parser.parse("todo read book").execute(tasks, ui, storage);
        parser.parse("deadline return book /by 2/12/2019 1800").execute(tasks, ui, storage);
        parser.parse("event lecture /from 3/12/2019 1400 /to 3/12/2019 1600")
                .execute(tasks, ui, storage);

        Todo todo = assertInstanceOf(Todo.class, tasks.get(0));
        assertEquals("read book", todo.getDescription());
        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(1));
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        Event event = assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("lecture", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 3, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 16, 0), event.getTo());
    }

    /** Suppresses console output while parsed add commands are executed. */
    private static class SilentUi extends Ui {
        @Override
        public void showAddedTask(pulbot.task.Task task, int taskCount) {
            // No output is needed for this parser test.
        }
    }

    /** Avoids disk writes while parsed add commands are executed. */
    private static class NoOpStorage extends Storage {
        NoOpStorage() {
            super("unused");
        }

        @Override
        public void save(TaskList tasks) {
            // Persistence is tested separately in StorageTest.
        }
    }

    /** Records the date supplied by an on command. */
    private static class RecordingDateUi extends Ui {
        private java.time.LocalDate displayedDate;

        @Override
        public void showTasksOnDate(TaskList tasks, java.time.LocalDate date) {
            displayedDate = date;
        }
    }
}

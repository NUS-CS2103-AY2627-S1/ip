package notmarth.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import notmarth.command.AddContactCommand;
import notmarth.command.Command;
import notmarth.command.ExitCommand;
import notmarth.command.FindCommand;
import notmarth.command.MarkCommand;
import notmarth.command.OnCommand;
import notmarth.exception.NotMarthException;
import notmarth.model.Deadline;
import notmarth.model.Event;
import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.model.ToDo;
import notmarth.storage.Storage;
import notmarth.ui.Ui;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests conversion of user command text into commands and typed task values. */
class ParserTest {
    @TempDir
    private Path temporaryDirectory;

    private final Parser parser = new Parser();

    /** Verifies creation of each supported task subtype and its typed values. */
    @Test
    void parse_taskCreationCommands_returnsTypedTasks() throws Exception {
        TaskList tasks = new TaskList(List.of(), 3);
        Storage storage = new Storage(temporaryDirectory.resolve("battle-plan.txt").toString());
        Ui ui = new Ui();

        execute(parser.parse("todo borrow book"), tasks, ui, storage);
        execute(parser.parse("deadline return book /by 2/12/2019 1800"), tasks, ui, storage);
        execute(parser.parse("event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600"),
                tasks, ui, storage);

        Task todo = tasks.get(0);
        Task deadline = tasks.get(1);
        Task event = tasks.get(2);

        assertInstanceOf(ToDo.class, todo);
        assertEquals("borrow book", todo.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), ((Deadline) deadline).getBy());
        assertEquals(LocalDateTime.of(2019, 10, 15, 14, 0), ((Event) event).getFrom());
        assertEquals(LocalDateTime.of(2019, 10, 15, 16, 0), ((Event) event).getTo());
    }

    /** Verifies parsing of control commands and date queries. */
    @Test
    void parse_controlAndContactCommands_returnsCorrectCommands() throws NotMarthException {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 2"));
        assertInstanceOf(OnCommand.class, parser.parse("on 2019-10-15"));
        assertInstanceOf(AddContactCommand.class,
                parser.parse("contact Mrs Tan /phone 81234567 /address 12 Engage Road #04-05"));
    }

    @Test
    void parse_phoneWithDocumentedCharacters_returnsAddContactCommand() throws NotMarthException {
        for (String phone : List.of("(65)-8765-4321", "+65 8765 4321", "87654321", "(+65) 8765-4321")) {
            assertInstanceOf(AddContactCommand.class,
                    parser.parse("contact Vander /phone " + phone + " /address Somniel"));
        }
    }

    @Test
    void parse_phoneWithoutDigitsOrWithUnsupportedCharacters_throwsNotMarthException() {
        for (String phone : List.of("()+-", "8765abcd", "8765.4321", "8765/4321")) {
            assertThrows(NotMarthException.class,
                    () -> parser.parse("contact Vander /phone " + phone + " /address Somniel"));
        }
    }

    /** Verifies rejection of empty and unknown commands. */
    @Test
    void parse_emptyOrUnknownCommand_throwsNotMarthException() {
        NotMarthException empty = assertThrows(NotMarthException.class, () -> parser.parse(""));
        NotMarthException unknown = assertThrows(
                NotMarthException.class, () -> parser.parse("launch mission"));

        assertEquals("Please enter a command. Try todo, deadline, event, list, find, on, mark, unmark, "
                        + "delete, contact, listcontacts, findcontact, deletecontact, bye.",
                empty.getMessage());
        assertEquals(
                "I don't recognize that command. Try todo, deadline, event, list, find, on, mark, unmark, "
                        + "delete, contact, listcontacts, findcontact, deletecontact, bye.",
                unknown.getMessage());
    }

    /** Verifies rejection of malformed task and task-number commands. */
    @Test
    void parse_malformedCommand_throwsNotMarthException() {
        assertThrows(NotMarthException.class, () -> parser.parse("todo"));
        assertThrows(NotMarthException.class, () -> parser.parse("deadline report /by"));
        assertThrows(NotMarthException.class,
                () -> parser.parse("event meeting /from 2019-10-15 1400 /to 2019-10-14 1400"));
        assertThrows(NotMarthException.class, () -> parser.parse("mark two"));
        assertThrows(NotMarthException.class, () -> parser.parse("on 2019-10-15 1400"));
        assertThrows(NotMarthException.class, () -> parser.parse("find"));
        assertThrows(NotMarthException.class, () -> parser.parse("contact Mrs Tan /phone 81234567"));
    }

    /** Verifies that unsafe whitespace and repeated command markers are rejected. */
    @Test
    void parse_invalidWhitespaceOrRepeatedMarker_throwsNotMarthException() {
        assertThrows(NotMarthException.class, () -> parser.parse(" todo task"));
        assertThrows(NotMarthException.class, () -> parser.parse("todo  task"));
        assertThrows(NotMarthException.class,
                () -> parser.parse("deadline report /by 2019-10-15 /by 2019-10-16"));
        assertThrows(NotMarthException.class,
                () -> parser.parse("contact Mrs Tan /phone abc /address road"));
    }

    /** Verifies that null input is reported as a command error rather than a null-pointer failure. */
    @Test
    void parse_nullInput_throwsNotMarthException() {
        assertThrows(NotMarthException.class, () -> parser.parse(null));
    }

    /** Executes a command against the supplied test application state. */
    private void execute(Command command, TaskList tasks, Ui ui, Storage storage) throws NotMarthException {
        command.execute(tasks, ui, storage);
    }
}

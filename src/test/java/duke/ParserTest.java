package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests command classification, task construction, and argument validation. */
class ParserTest {
    private final Parser parser = new Parser();

    @ParameterizedTest
    @ValueSource(strings = {"+10000-09-20", "-0001-09-20", "2026-9-20", "2026-09-31"})
    void parseTask_nonCanonicalOrImpossibleDate_rejectsInput(String date) {
        assertThrows(WangsaException.class, () -> parser.parseTask("deadline task /by " + date));
    }

    @ParameterizedTest
    @ValueSource(strings = {"@ai How do I add a deadline?", "  @ai\tHow do I add a deadline?  "})
    void parse_aiQuestion_preservesQuestionAndRecognizesCommand(String input) throws WangsaException {
        Command.AiQuestion command = assertInstanceOf(Command.AiQuestion.class, parser.parse(input));

        assertEquals(Parser.CommandType.AI, command.type());
        assertEquals("How do I add a deadline?", command.question());
    }

    @ParameterizedTest
    @ValueSource(strings = {"@ai", " @ai\t ", "@aihelp", "@AI question", "help extra"})
    void parse_invalidHelpCommand_rejectsInput(String input) {
        assertThrows(WangsaException.class, () -> parser.parse(input));
    }

    @Test
    void parse_helpAndQuestionLimit_acceptsOnlySupportedSyntax() throws WangsaException {
        assertEquals(Parser.CommandType.HELP, parser.parse(" help ").type());
        assertInstanceOf(Command.AiQuestion.class, parser.parse("@ai " + "a".repeat(1000)));
        assertThrows(WangsaException.class, () -> parser.parse("@ai " + "a".repeat(1001)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "deadline task /by 2026-09-20 /by 2026-09-21",
        "deadline task /by2026-09-20",
        "event meeting /fromage 2pm /to 4pm",
        "event meeting /from 2pm /to 4pm /to 5pm",
        "event meeting /from 2pm /from 3pm /to 4pm",
        "event meeting /to 4pm /from 2pm",
        "event meeting /from 2pm /to 4pm /by 2026-09-20",
        "event /from 2pm /to 4pm",
        "event meeting /from /to 4pm"
    })
    void parseTask_invalidDetailMarkers_rejectsAmbiguousInput(String command) {
        assertThrows(WangsaException.class, () -> parser.parseTask(command));
    }

    @Test
    void parseTask_spacedDetailMarkers_preservesFreeFormValues() throws WangsaException {
        Event event = (Event) parser.parseTask("event\tmeeting\t/from\tMonday  2pm\t/to\tMonday 4pm");
        assertEquals("Monday  2pm", event.getFrom());
        assertEquals("Monday 4pm", event.getTo());
        Deadline deadline = (Deadline) parser.parseTask("deadline task\t/by\t2026-09-20");
        assertEquals(LocalDate.of(2026, 9, 20), deadline.getBy());
    }

    @ParameterizedTest
    @ValueSource(strings = {" todo read  book ", "todo\tread  book", "\t todo   read  book\t"})
    void parse_surroundingWhitespace_preservesDescriptionSpacing(String input) throws WangsaException {
        Task task = ((Command.AddTask) parser.parse(input)).task();

        assertEquals("read  book", task.getDescription());
        assertEquals("read  book", parser.parseTask(input).getDescription());
    }

    @Test
    void parse_spacedCommands_acceptsWhitespaceInBothInterfaces() throws WangsaException {
        assertEquals(Parser.CommandType.LIST, parser.parse("  list\t").type());
        assertEquals(Parser.CommandType.SORT, parser.parseCommandType("\tsort "));
        assertEquals(2, parser.parseTaskNumber("  mark\t2 "));
        assertEquals("read  book", parser.parseSearchKeyword(" find  read  book "));
        assertThrows(WangsaException.class, () -> parser.parse("\t  "));
        assertThrows(WangsaException.class, () -> parser.parse("list extra"));
        assertThrows(WangsaException.class, () -> parser.parse("TODO task"));
    }

    @Test
    void parseCommandType_recognizesSupportedCommands() throws WangsaException {
        assertEquals(Parser.CommandType.BYE, parser.parseCommandType("bye"));
        assertEquals(Parser.CommandType.LIST, parser.parseCommandType("list"));
        assertEquals(Parser.CommandType.MARK, parser.parseCommandType("mark 1"));
        assertEquals(Parser.CommandType.UNMARK, parser.parseCommandType("unmark 1"));
        assertEquals(Parser.CommandType.DELETE, parser.parseCommandType("delete 1"));
        assertEquals(Parser.CommandType.FIND, parser.parseCommandType("find book"));
        assertEquals(Parser.CommandType.SORT, parser.parseCommandType("sort"));
        assertEquals(Parser.CommandType.ADD_TASK, parser.parseCommandType("todo read book"));
    }

    @Test
    void parseTask_buildsAllTaskTypesAndParsesDates() throws WangsaException {
        assertInstanceOf(Todo.class, parser.parseTask("todo read book"));

        Deadline deadline = (Deadline) parser.parseTask("deadline return book /by 2019-12-02");
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());

        Event event = (Event) parser.parseTask("event meeting /from 2pm /to 4pm");
        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    void parseTask_rejectsMalformedOrInvalidDates() {
        assertThrows(WangsaException.class, () -> parser.parseTask("todo"));
        assertThrows(WangsaException.class, () -> parser.parseTask("deadline return book /by 2019-02-29"));
        assertThrows(WangsaException.class, () -> parser.parseTask("event meeting /from 2pm"));
        assertThrows(WangsaException.class, () -> parser.parseTask("unknown task"));
    }

    @Test
    void parseTaskNumber_rejectsMissingAndNonNumericArguments() throws WangsaException {
        assertEquals(12, parser.parseTaskNumber("delete 12"));
        assertThrows(WangsaException.class, () -> parser.parseTaskNumber("mark"));
        assertThrows(WangsaException.class, () -> parser.parseTaskNumber("mark one"));
    }

    @Test
    void parse_buildsTypedCommandsForInterfaceAdapters() throws WangsaException {
        assertEquals(Parser.CommandType.LIST, parser.parse("list").type());
        assertEquals(3, ((Command.TaskNumber) parser.parse("mark 3")).taskNumber());
        assertEquals("book", ((Command.Search) parser.parse("find book")).keyword());
        assertInstanceOf(Todo.class, ((Command.AddTask) parser.parse("todo read book")).task());
    }

    @Test
    void commandShapes_rejectIncompatibleActions() {
        assertThrows(IllegalArgumentException.class,
                () -> new Command.Simple(Parser.CommandType.MARK));
        assertThrows(IllegalArgumentException.class,
                () -> new Command.TaskNumber(Parser.CommandType.FIND, 1));
    }

    @Test
    void parseSearchKeyword_requiresAndReturnsKeyword() throws WangsaException {
        assertEquals("book", parser.parseSearchKeyword("find book"));
        assertThrows(WangsaException.class, () -> parser.parseSearchKeyword("find"));
    }
}

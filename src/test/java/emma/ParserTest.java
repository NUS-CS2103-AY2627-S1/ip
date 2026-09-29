package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import emma.command.AddDeadlineCommand;
import emma.command.AddEventCommand;
import emma.command.AddTodoCommand;
import emma.command.ByeCommand;
import emma.command.DeleteCommand;
import emma.command.FilterCommand;
import emma.command.FindCommand;
import emma.command.ListCommand;
import emma.command.MarkCommand;

/**
 * Checks that each line the user can type is turned into the right command, carrying
 * the arguments it was given.
 */
public class ParserTest {

    @TempDir
    Path tempDir;

    /** Runs a typed line against a fresh list, so a command's arguments can be seen in its reply. */
    private String run(String input, TaskList tasks) throws EmmaException {
        return Parser.parse(input).execute(tasks, new Storage(tempDir.resolve("emma.json").toString()));
    }

    @Test
    public void parse_everyCommandWord_returnsMatchingCommand() throws EmmaException {
        assertInstanceOf(ByeCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(AddTodoCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddDeadlineCommand.class, Parser.parse("deadline return book /by 2019-10-15"));
        assertInstanceOf(AddEventCommand.class,
                Parser.parse("event meeting /from 2019-10-15 /to 2019-10-16"));
        assertInstanceOf(FilterCommand.class, Parser.parse("filter /type todo"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_byeCommand_isTheOnlyOneThatEndsTheSession() throws EmmaException {
        assertTrue(Parser.parse("bye").isExit());
        assertFalse(Parser.parse("list").isExit());
        assertFalse(Parser.parse("todo read book").isExit());
        assertFalse(Parser.parse("filter /type todo").isExit());
    }

    @Test
    public void parse_addCommands_carryTheirDescriptionsAndDates() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        assertEquals("Got it, I've added this:\n  [T][ ] read book",
                run("todo read book", tasks));
        assertEquals("Got it, I've added this:\n  [D][ ] return book (by: Oct 15 2019)",
                run("deadline return book /by 2019-10-15", tasks));
        assertEquals("Got it, I've added this:\n  [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)",
                run("event meeting /from 2019-10-15 /to 2019-10-16", tasks));
    }

    @Test
    public void parse_markUnmarkAndDelete_actOnTheNumberedTask() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        run("todo read book", tasks);
        run("todo return book", tasks);

        assertEquals("Nice! I've marked this as done:\n  [T][x] return book",
                run("mark 2", tasks));
        assertEquals("Okay, I've marked this as not done yet:\n  [T][ ] return book",
                run("unmark 2", tasks));
        assertEquals("Okay, I've removed this:\n  [T][ ] read book", run("delete 1", tasks));
        assertEquals("Here's your tasks:\n1. [T][ ] return book", run("list", tasks));
    }

    @Test
    public void parse_listAndBye_produceTheirOwnReplies() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        assertEquals("You haven't given me anything to track yet!", run("list", tasks));
        assertEquals("Bye for now! Hope to see you again soon.", run("bye", tasks));
    }

    @Test
    public void parse_unknownCommand_isRejected() {
        assertEquals("Sorry, I don't know what that means!",
                assertThrows(EmmaException.class, () -> Parser.parse("blah")).getMessage());
        assertEquals("Sorry, I don't know what that means!",
                assertThrows(EmmaException.class, () -> Parser.parse("deadlin x /by 2019-10-15"))
                        .getMessage());
    }

    @Test
    public void parse_commandMissingItsParts_explainsTheUsage() {
        assertEquals("A todo needs a description, like \"todo read book\".",
                assertThrows(EmmaException.class, () -> Parser.parse("todo")).getMessage());
        assertEquals("A deadline needs a description and a date, "
                        + "like \"deadline return book /by 2019-10-15\".",
                assertThrows(EmmaException.class, () -> Parser.parse("deadline return book"))
                        .getMessage());
        assertEquals("An event needs a description, a start date and an end date, "
                        + "like \"event project meeting /from 2019-10-15 /to 2019-10-16\".",
                assertThrows(EmmaException.class, () -> Parser.parse("event meeting /from 2019-10-15"))
                        .getMessage());
    }

    @Test
    public void parse_dateThatIsNotARealDate_isRejected() {
        assertEquals("I need a due date as a date like 2019-10-15, but I got \"Sunday\".",
                assertThrows(EmmaException.class, () ->
                        Parser.parse("deadline return book /by Sunday")).getMessage());
        assertEquals("I need a due date as a date like 2019-10-15, but I got \"2019-02-30\".",
                assertThrows(EmmaException.class, () ->
                        Parser.parse("deadline return book /by 2019-02-30")).getMessage());
    }

    @Test
    public void parse_eventEndingBeforeItStarts_isRejected() {
        assertEquals("An event has to end on or after it starts, "
                        + "but Oct 15 2019 is before Oct 16 2019.",
                assertThrows(EmmaException.class, () ->
                        Parser.parse("event meeting /from 2019-10-16 /to 2019-10-15"))
                        .getMessage());
    }

    @Test
    public void parse_taskNumberThatIsNotAWholeNumber_isRejected() {
        assertEquals("I need a task number, like \"mark 1\".",
                assertThrows(EmmaException.class, () -> Parser.parse("mark abc")).getMessage());
        assertEquals("A find needs something to look for, like \"find book\".",
                assertThrows(EmmaException.class, () -> Parser.parse("find")).getMessage());
        assertEquals("I need a task number, like \"delete 1\".",
                assertThrows(EmmaException.class, () -> Parser.parse("delete")).getMessage());
    }

    @Test
    public void parse_addCommandMissingItsDescription_explainsTheUsage() {
        assertEquals("A deadline needs a description and a date, "
                        + "like \"deadline return book /by 2019-10-15\".",
                assertThrows(EmmaException.class, () -> Parser.parse("deadline /by 2019-10-15"))
                        .getMessage());
        assertEquals("An event needs a description, a start date and an end date, "
                        + "like \"event project meeting /from 2019-10-15 /to 2019-10-16\".",
                assertThrows(EmmaException.class, () ->
                        Parser.parse("event /from 2019-10-15 /to 2019-10-16")).getMessage());
    }

    @Test
    public void parse_addCommandWithAnEmptyDatePart_explainsTheUsage() {
        assertThrows(EmmaException.class, () -> Parser.parse("deadline return book /by "));
        assertThrows(EmmaException.class, () -> Parser.parse("event meeting /from  /to 2019-10-16"));
        assertThrows(EmmaException.class, () -> Parser.parse("event meeting /from 2019-10-15 /to "));
    }

    @Test
    public void parse_addCommandWithNoArgumentsAtAll_explainsTheUsage() {
        assertThrows(EmmaException.class, () -> Parser.parse("deadline"));
        assertThrows(EmmaException.class, () -> Parser.parse("event"));
    }

    @Test
    public void parse_unmarkWithoutAWholeNumber_isRejected() {
        assertEquals("I need a task number, like \"unmark 1\".",
                assertThrows(EmmaException.class, () -> Parser.parse("unmark abc")).getMessage());
        assertEquals("I need a task number, like \"mark 1\".",
                assertThrows(EmmaException.class, () -> Parser.parse("mark 1.5")).getMessage());
    }

    @Test
    public void parse_negativeOrZeroTaskNumber_isAcceptedHereAndRefusedLater() throws EmmaException {
        // The parser only checks the number is whole; whether a task has it is the
        // command's business, since only the command can see the list.
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 0"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete -1"));
    }

    @Test
    public void parse_textAfterACommandTakingNothing_isRejected() {
        assertEquals("\"list\" takes nothing after it, but I got \"mamama\".",
                assertThrows(EmmaException.class, () -> Parser.parse("list mamama")).getMessage());
        assertEquals("\"bye\" takes nothing after it, but I got \"now\".",
                assertThrows(EmmaException.class, () -> Parser.parse("bye now")).getMessage());
    }

    @Test
    public void parse_commandTakingNothingWithOnlySpacesAfterIt_isStillAccepted() throws EmmaException {
        assertInstanceOf(ListCommand.class, Parser.parse("list   "));
        assertInstanceOf(ListCommand.class, Parser.parse("  list  "));
        assertInstanceOf(ByeCommand.class, Parser.parse("bye  "));
    }

    @Test
    public void parse_textAfterACommandTakingAnArgument_isAlreadyRejectedByThatArgument() {
        // These read a number, a date or a flag, so trailing words fail that reading
        // rather than needing a check of their own.
        assertEquals("I need a task number, like \"mark 1\".",
                assertThrows(EmmaException.class, () -> Parser.parse("mark 1 blah")).getMessage());
        assertEquals("I need a task number, like \"delete 1\".",
                assertThrows(EmmaException.class, () -> Parser.parse("delete 1 blah")).getMessage());
        assertEquals("I don't know what \"blah\" means in a filter.",
                assertThrows(EmmaException.class, () ->
                        Parser.parse("filter /type todo blah")).getMessage());
        assertEquals("I need a due date as a date like 2019-10-15, "
                        + "but I got \"2019-10-15 blah\".",
                assertThrows(EmmaException.class, () ->
                        Parser.parse("deadline x /by 2019-10-15 blah")).getMessage());
    }

    @Test
    public void parse_textAfterTodoOrFind_isPartOfWhatWasAskedFor() throws EmmaException {
        // Unlike the others, these take free text, so extra words are not a mistake.
        TaskList tasks = new TaskList(List.of());
        assertEquals("Got it, I've added this:\n  [T][ ] read book twice",
                run("todo read book twice", tasks));
        assertEquals("Here's what I found:\n1. [T][ ] read book twice",
                run("find read book", tasks));
    }

    @Test
    public void parse_extraSpacesAroundArguments_areIgnored() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        assertEquals("Got it, I've added this:\n  [T][ ] read book",
                run("todo   read book", tasks));
        assertInstanceOf(FilterCommand.class, Parser.parse("filter  /type todo"));
        assertInstanceOf(FilterCommand.class, Parser.parse("filter /type deadline  /due-by  2019-10-15"));
    }
}

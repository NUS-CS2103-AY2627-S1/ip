package piplupbot;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import piplupbot.command.AddCommand;
import piplupbot.command.Command;
import piplupbot.command.DeleteCommand;
import piplupbot.command.ExitCommand;
import piplupbot.command.FindCommand;
import piplupbot.command.ListCommand;
import piplupbot.command.MarkCommand;
import piplupbot.command.SortCommand;
import piplupbot.task.TaskList;

/**
 * Tests {@link Parser#parse}, which turns a typed line into the command it asks
 * for.
 *
 * <p>Most of the cases below are refusals, and that is deliberate: a command
 * built from a half-understood line is worse than no command at all, because the
 * mistake is then stored rather than reported. {@code parse} therefore refuses
 * before anything happens, and each refusal is a rule worth pinning down.</p>
 *
 * <p>The rules that are easiest to break by accident are the ones about where a
 * separator is looked for. {@code /by}, {@code /from} and {@code /to} are
 * matched surrounded by spaces, so a description may contain those very
 * characters; {@code /to} is looked for only after {@code /from}, so the two
 * cannot be read in the wrong order; and a separator given twice is refused. All
 * three are invisible in ordinary use and would be lost by "simplifying" the
 * search.</p>
 *
 * <p>A command deliberately keeps to itself what it was built from, so a
 * successful parse is checked by carrying the command out and looking at the
 * list it produced -- which is also the only thing the user could observe. The
 * commands are given a save file inside a temporary folder, so a test can never
 * touch the real one.</p>
 */
public class ParserTest {

    /**
     * A folder JUnit creates for each test and deletes afterwards. It is left
     * package-private because {@code @TempDir} cannot fill in a private field.
     */
    @TempDir
    Path tempDir;

    /**
     * Returns the list left behind by parsing each line and carrying it out.
     *
     * @param inputs The lines to type, in order.
     * @return The task list after all of them have run.
     * @throws PiplupBotException If any line is refused.
     */
    private TaskList runCommands(String... inputs) throws PiplupBotException {
        TaskList tasks = new TaskList();
        Ui ui = new TextUi();
        Storage storage = new Storage(tempDir.resolve("piplupbot.txt"));

        for (String input : inputs) {
            Parser.parse(input).execute(tasks, ui, storage);
        }
        return tasks;
    }

    // ---------- Which command a line names ----------

    @Test
    public void parse_taskCommands_returnAddCommand() throws PiplupBotException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-10-15 1800"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2019-10-02 1400 /to 2019-10-02 1600"));
    }

    @Test
    public void parse_otherCommands_returnTheirOwnCommandTypes() throws PiplupBotException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(SortCommand.class, Parser.parse("sort date"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    /**
     * Only {@code bye} ends the conversation. The loop asks the command rather
     * than recognizing the word, so this is the property it actually relies on.
     */
    @Test
    public void parse_byeCommand_isTheOnlyExitCommand() throws PiplupBotException {
        assertTrue(Parser.parse("bye").isExit());

        for (String input : new String[] {
            "list", "todo read book", "mark 1", "delete 1", "find book", "sort date",
        }) {
            Command command = Parser.parse(input);
            assertFalse(command.isExit(), input + " should not end the conversation");
        }
    }

    // ---------- Reading the details out of a line ----------

    @Test
    public void parse_todoCommand_storesTheDescription() throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[T][ ] read book"},
                runCommands("todo read book").toNumberedLines());
    }

    @Test
    public void parse_deadlineCommand_storesDescriptionAndDate() throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[D][ ] return book (by: Oct 15 2019 06:00 PM)"},
                runCommands("deadline return book /by 2019-10-15 1800").toNumberedLines());
    }

    @Test
    public void parse_eventCommand_storesDescriptionStartAndEnd() throws PiplupBotException {
        assertArrayEquals(
                new String[] {
                    "1.[E][ ] project meeting (from: Oct 2 2019 02:00 PM to: Oct 2 2019 04:00 PM)",
                },
                runCommands("event project meeting /from 2019-10-02 1400 /to 2019-10-02 1600")
                        .toNumberedLines());
    }

    /** Extra spaces around the parts are removed, so they never reach the list. */
    @Test
    public void parse_extraSpacesAroundParts_storesThemTrimmed() throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[D][ ] return book (by: Oct 15 2019 06:00 PM)"},
                runCommands("deadline    return book    /by    2019-10-15 1800   ").toNumberedLines());
    }

    @Test
    public void parse_markAndUnmarkCommands_changeTheNamedTask() throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[T][X] read book", "2.[T][ ] write notes"},
                runCommands("todo read book", "todo write notes", "mark 1").toNumberedLines());

        assertArrayEquals(new String[] {"1.[T][ ] read book"},
                runCommands("todo read book", "mark 1", "unmark 1").toNumberedLines());
    }

    @Test
    public void parse_deleteCommand_removesTheNamedTask() throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[T][ ] write notes"},
                runCommands("todo read book", "todo write notes", "delete 1").toNumberedLines());
    }

    // ---------- Where a separator is looked for ----------

    /**
     * The separator is {@code /by} surrounded by spaces, so a description may
     * contain those characters itself. Searching for a bare {@code /by} would
     * cut this description in half.
     */
    @Test
    public void parse_descriptionContainingBySeparator_keepsItInTheDescription()
            throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[D][ ] submit A/by B (by: Oct 15 2019 06:00 PM)"},
                runCommands("deadline submit A/by B /by 2019-10-15 1800").toNumberedLines());
    }

    /**
     * {@code /to} is looked for only after {@code /from}, so an earlier
     * {@code /to} belongs to the description.
     */
    @Test
    public void parse_descriptionContainingToSeparator_keepsItInTheDescription()
            throws PiplupBotException {
        assertArrayEquals(
                new String[] {
                    "1.[E][ ] lunch /to dinner (from: Oct 2 2019 01:00 PM to: Oct 2 2019 02:00 PM)",
                },
                runCommands("event lunch /to dinner /from 2019-10-02 1300 /to 2019-10-02 1400")
                        .toNumberedLines());
    }

    /**
     * The same rule read from the other side: an event written with its end
     * before its start is refused rather than being pieced together in whatever
     * order the separators happen to appear.
     */
    @Test
    public void parse_eventWithToBeforeFrom_exceptionThrown() {
        assertThrows(PiplupBotException.class, () ->
                Parser.parse("event meeting /to 2019-10-02 1600 /from 2019-10-02 1400"));
    }

    /**
     * A separator typed twice is refused by name. Before this check, the second
     * {@code /by} was swallowed into the date, and the user was told only that
     * {@code 2019-10-15 1800 /by 2019-10-16 1800} is not a date -- true, but
     * little help in finding the mistake.
     *
     * <p>{@link Parser} finds every separator of every command through one
     * shared method, so the next case checks that the rule reaches
     * {@code /from} and {@code /to} as well.</p>
     */
    @Test
    public void parse_deadlineWithRepeatedSeparator_messageNamesTheSeparator() {
        PiplupBotException exception = assertThrows(PiplupBotException.class, () ->
                Parser.parse("deadline return book /by 2019-10-15 1800 /by 2019-10-16 1800"));
        assertArrayEquals(new String[] {"Pip... Please give the /by part only once."},
                exception.getMessageLines());
    }

    /**
     * Each of an event's separators is checked for a repeat anywhere after it
     * -- including after the other separator, which is where a check confined
     * to one part would miss a second {@code /from}.
     */
    @Test
    public void parse_eventWithRepeatedSeparator_messageNamesTheSeparator() {
        String event = "event meeting /from 2019-10-02 1400 /to 2019-10-02 1600";

        PiplupBotException repeatedFrom = assertThrows(PiplupBotException.class, () ->
                Parser.parse(event + " /from 2019-10-02 1500"));
        assertEquals("Pip... Please give the /from part only once.",
                repeatedFrom.getMessageLines()[0]);

        PiplupBotException repeatedTo = assertThrows(PiplupBotException.class, () ->
                Parser.parse(event + " /to 2019-10-02 1700"));
        assertEquals("Pip... Please give the /to part only once.",
                repeatedTo.getMessageLines()[0]);
    }

    /**
     * Two copies sharing the one space between them are still two copies. A
     * search for the second that began after the end of the first would miss
     * them, because that space would already be used up.
     */
    @Test
    public void parse_separatorRepeatedWithOneSpaceBetween_exceptionThrown() {
        PiplupBotException exception = assertThrows(PiplupBotException.class, () ->
                Parser.parse("deadline return book /by /by 2019-10-15 1800"));
        assertEquals("Pip... Please give the /by part only once.", exception.getMessageLines()[0]);
    }

    // ---------- Lines that are missing something ----------

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_todoWithoutDescription_messageShowsAnExample() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> Parser.parse("todo"));
        assertArrayEquals(
                new String[] {"Pip... A todo needs a description, e.g. todo borrow book."},
                exception.getMessageLines());
    }

    @Test
    public void parse_deadlineWithoutByPart_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("deadline return book"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("deadline"));
    }

    @Test
    public void parse_deadlineWithoutByPart_messageShowsAnExample() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> Parser.parse("deadline return book"));
        assertArrayEquals(new String[] {
            "Pip... A deadline needs a /by part, e.g. deadline return book /by 2019-10-15 1800.",
        }, exception.getMessageLines());
    }

    /** Both sides of {@code /by} must have something in them. */
    @Test
    public void parse_deadlineMissingOneSideOfSeparator_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("deadline /by 2019-10-15 1800"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("deadline return book /by "));
    }

    @Test
    public void parse_eventMissingASeparator_exceptionThrown() {
        assertThrows(PiplupBotException.class, () ->
                Parser.parse("event meeting /from 2019-10-02 1400"));
        assertThrows(PiplupBotException.class, () ->
                Parser.parse("event meeting /to 2019-10-02 1600"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("event meeting"));
    }

    @Test
    public void parse_eventMissingASeparator_messageShowsAnExample() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> Parser.parse("event project meeting"));
        assertArrayEquals(new String[] {
            "Pip... An event needs a /from and a /to part, "
                    + "e.g. event project meeting /from 2019-10-02 1400 /to 2019-10-02 1600.",
        }, exception.getMessageLines());
    }

    @Test
    public void parse_eventMissingOnePart_exceptionThrown() {
        assertThrows(PiplupBotException.class, () ->
                Parser.parse("event /from 2019-10-02 1400 /to 2019-10-02 1600"));
        assertThrows(PiplupBotException.class, () ->
                Parser.parse("event meeting /from  /to 2019-10-02 1600"));
    }

    /**
     * A date the bot cannot read reaches the user the same way a missing part
     * does, even though it is noticed further in, by the {@code Deadline}
     * constructor rather than by the parser.
     */
    @Test
    public void parse_taskWithUnreadableDate_exceptionThrown() {
        assertThrows(PiplupBotException.class, () ->
                Parser.parse("deadline return book /by next Friday"));
        assertThrows(PiplupBotException.class, () ->
                Parser.parse("event meeting /from sometime /to 2019-10-02 1600"));
    }

    // ---------- Lines that should name a task number ----------

    @Test
    public void parse_taskNumberMissing_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("mark"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("unmark"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("delete"));
    }

    @Test
    public void parse_taskNumberNotANumber_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("mark two"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("delete 1 2"));
    }

    /**
     * {@code Integer.parseInt} accepts a sign, and digits from other writing
     * systems, none of which is how {@code list} numbers a task. Without the
     * digit check, {@code mark +1} would quietly mark task 1, and none of these
     * lines would be refused here.
     */
    @Test
    public void parse_taskNumberNotPlainDigits_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("mark +1"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("unmark -1"));
        // U+0661 is the Arabic-Indic digit one, which parseInt reads as 1.
        assertThrows(PiplupBotException.class, () -> Parser.parse("delete \u0661"));
    }

    /**
     * Leading zeros are still plain digits, so the digit check must not be
     * written so strictly that it refuses {@code mark 01}.
     */
    @Test
    public void parse_taskNumberWithLeadingZero_accepted() throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[T][X] read book"},
                runCommands("todo read book", "mark 01").toNumberedLines());
    }

    /**
     * A number too large for an {@code int} passes the digit check, so it is
     * {@code parseInt} that refuses it. The catch around that call is what turns
     * the refusal into a reply rather than a crash.
     */
    @Test
    public void parse_taskNumberTooLarge_messageAsksForANumber() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> Parser.parse("mark 99999999999"));
        assertArrayEquals(new String[] {"Pip... Please give me a task number, e.g. mark 2."},
                exception.getMessageLines());
    }

    /**
     * The hint names the command the user actually typed, so the example it
     * gives is one they can copy. A hard-coded "mark" would be wrong for two of
     * the three commands that share this method.
     */
    @Test
    public void parse_taskNumberNotANumber_messageNamesTheTypedCommand() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> Parser.parse("delete three"));
        assertArrayEquals(new String[] {"Pip... Please give me a task number, e.g. delete 2."},
                exception.getMessageLines());
    }

    /**
     * Whether a task with that number exists is not the parser's question, so a
     * number naming no task is accepted here and refused later, when the command
     * runs against the list.
     */
    @Test
    public void parse_taskNumberNamingNoTask_returnsCommandThatFailsWhenRun()
            throws PiplupBotException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 99"));
        assertThrows(PiplupBotException.class, () -> runCommands("mark 99"));
    }

    // ---------- Lines that should name something to look for ----------

    /**
     * A bare {@code find} is refused rather than answered. Every description
     * contains the empty string, so an unguarded search would reply with the
     * whole list -- a confident answer to a line that never said what to look
     * for.
     */
    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("find"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("find   "));
    }

    @Test
    public void parse_findWithoutKeyword_messageShowsAnExample() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> Parser.parse("find"));
        assertArrayEquals(
                new String[] {"Pip... Please tell me what to look for, e.g. find book."},
                exception.getMessageLines());
    }

    /**
     * Everything after the command word is one keyword, spaces included, rather
     * than several words matched separately. The list below is searched through
     * the parsed command's own keyword, since a command keeps to itself what it
     * was built from.
     */
    @Test
    public void parse_findWithSeveralWords_looksForThemAsOnePhrase() throws PiplupBotException {
        TaskList tasks = runCommands("todo read book", "todo return book");

        assertArrayEquals(new String[] {"1.[T][ ] read book"},
                tasks.find("read book").toNumberedLines());
    }

    /**
     * {@code find} shows tasks without touching them, so the list is exactly as
     * it was afterwards -- numbering included, which the search's own numbering
     * does not disturb.
     */
    @Test
    public void parse_findCommand_leavesTheListUnchanged() throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[T][ ] read book", "2.[T][ ] write notes"},
                runCommands("todo read book", "todo write notes", "find book", "find notes")
                        .toNumberedLines());
    }

    // ---------- Lines that should name a sort key ----------

    /**
     * A bare {@code sort} is refused rather than answered, for the reason a bare
     * {@code find} is: picking a key on the user's behalf would be a confident
     * answer to a line that never said what to sort by.
     */
    @Test
    public void parse_sortWithoutKey_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("sort"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("sort   "));
    }

    @Test
    public void parse_sortWithoutKey_messageShowsAnExampleAndListsTheKeys() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> Parser.parse("sort"));
        assertArrayEquals(new String[] {
            "Pip... Please tell me what to sort by, e.g. sort date.",
            "Try: date, name, type, or status.",
        }, exception.getMessageLines());
    }

    /** Keys are matched exactly and in lower case, as command words are. */
    @Test
    public void parse_sortWithUnknownKey_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("sort Date"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("sort chronologically"));
    }

    /**
     * Everything after the key is read as the direction, so a word the command
     * has no room for is reported rather than quietly ignored: without that,
     * {@code sort date desc now} would sort and say nothing about the "now".
     */
    @Test
    public void parse_sortWithUnreadableDirection_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("sort date descending"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("sort date ASC"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("sort date desc now"));
    }

    /** A direction left out means ascending, which is the ordinary case. */
    @Test
    public void parse_sortWithNoDirection_sortsAscending() throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[T][ ] a", "2.[T][ ] b", "3.[T][ ] c"},
                runCommands("todo c", "todo a", "todo b", "sort name").toNumberedLines());
    }

    /**
     * Unlike {@code find}, {@code sort} changes the list it is given: the tasks
     * come back in the new order, which is what makes the numbers beside them
     * usable at {@code mark} and {@code delete}.
     */
    @Test
    public void parse_sortCommand_rearrangesTheStoredList() throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[T][ ] c", "2.[T][ ] b", "3.[T][ ] a"},
                runCommands("todo c", "todo a", "todo b", "sort name desc").toNumberedLines());
    }

    /**
     * Several spaces between the key and the direction do what one does. The
     * line is split at a run of spaces rather than at a single one; splitting at
     * one would leave the other spaces in front of {@code desc}, and the
     * direction would then be refused.
     */
    @Test
    public void parse_sortWithSeveralSpacesBeforeDirection_readsTheDirection() throws PiplupBotException {
        assertArrayEquals(new String[] {"1.[T][ ] c", "2.[T][ ] b", "3.[T][ ] a"},
                runCommands("todo c", "todo a", "todo b", "sort name   desc").toNumberedLines());
    }

    // ---------- Lines that name no command at all ----------

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("blah"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("list now"));
    }

    // ---------- Characters a line may not hold ----------

    /**
     * A tab looks like a space but is not one, so a line holding one is refused
     * with the reason. The check comes before the command word is matched: a
     * tab straight after {@code todo} would otherwise be answered with "I don't
     * know what ... means", quoting the tab back, which the message checked
     * here rules out.
     */
    @Test
    public void parse_lineWithTab_messageNamesTheProblem() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> Parser.parse("todo\tread book"));
        assertArrayEquals(new String[] {
            "Pip... That line has a tab or another control character in it.",
            "Please type it again with plain spaces, and without the arrow keys.",
        }, exception.getMessageLines());
    }

    /**
     * Inside a description, a control character would otherwise be stored. An
     * arrow key pressed in some consoles types ESC followed by {@code [A}, which
     * would then move the cursor every time the task was listed.
     */
    @Test
    public void parse_descriptionWithControlCharacter_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> Parser.parse("todo read\tbook"));
        assertThrows(PiplupBotException.class, () -> Parser.parse("todo read book\u001b[A"));
    }
}

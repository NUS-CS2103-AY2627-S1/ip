package ted;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ted.command.AddCommand;
import ted.command.Command;
import ted.command.DeleteCommand;
import ted.command.ExitCommand;
import ted.command.FindCommand;
import ted.command.ListCommand;
import ted.command.MarkCommand;
import ted.command.TagCommand;
import ted.task.Task;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Tests {@link Parser#parse(String)}, which turns a line of user input into
 * the command it stands for.
 * <p>
 * Parser is worth testing closely because it is where malformed input is
 * caught: every check it skips becomes a crash or a wrong task later on.
 */
public class ParserTest {
    /**
     * Verifies that a todo description produces an add command.
     */
    @Test
    public void parse_todoWithDescription_returnsAddCommand() throws TedException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo borrow book"));
    }

    /**
     * Verifies that a deadline with a valid date and time produces an add command.
     */
    @Test
    public void parse_deadlineWithValidDateTime_returnsAddCommand() throws TedException {
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2/12/2019 1800"));
    }

    /**
     * Verifies that an event with valid start and end times produces an add command.
     */
    @Test
    public void parse_eventWithValidDateTimes_returnsAddCommand() throws TedException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2/12/2019 1400 /to 2/12/2019 1600"));
    }

    /**
     * Verifies that the list keyword produces a list command.
     */
    @Test
    public void parse_listCommand_returnsListCommand() throws TedException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    /**
     * Verifies that delete followed by a task number produces a delete command.
     */
    @Test
    public void parse_deleteCommand_returnsDeleteCommand() throws TedException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 2"));
    }

    /**
     * Verifies that mark and unmark both produce mark commands.
     */
    @Test
    public void parse_markCommand_returnsMarkCommand() throws TedException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
    }

    /**
     * Verifies that bye produces an exit command that requests termination.
     */
    @Test
    public void parse_byeCommand_returnsExitingCommand() throws TedException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    /**
     * Verifies that a list command does not request termination.
     */
    @Test
    public void parse_otherCommand_doesNotExit() throws TedException {
        assertFalse(Parser.parse("list").isExit());
    }

    /**
     * Verifies that find with a keyword produces a find command.
     */
    @Test
    public void parse_findWithKeyword_returnsFindCommand() throws TedException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    /**
     * Verifies that find accepts a keyword containing spaces.
     */
    @Test
    public void parse_findWithMultiWordKeyword_returnsFindCommand() throws TedException {
        // The whole of the rest of the line is the keyword, spaces included.
        assertInstanceOf(FindCommand.class, Parser.parse("find return book"));
    }

    /**
     * Verifies that find rejects missing and whitespace-only keywords.
     */
    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        assertThrows(TedException.class, () -> Parser.parse("find"));
        assertThrows(TedException.class, () -> Parser.parse("find    "));
    }

    /**
     * Verifies that an unknown command reports its keyword and a supported command.
     */
    @Test
    public void parse_unknownKeyword_exceptionThrown() {
        TedException e = assertThrows(TedException.class, () -> Parser.parse("blah"));
        // The message should name the offending word and list what Ted does understand.
        assertTrue(e.getMessage().contains("blah"));
        assertTrue(e.getMessage().contains("todo"));
    }

    /**
     * Verifies that command keywords must match completely.
     */
    @Test
    public void parse_keywordWithTrailingLetters_exceptionThrown() {
        // "todos" must not be accepted as "todo": the command word is matched whole.
        assertThrows(TedException.class, () -> Parser.parse("todos borrow book"));
    }

    /**
     * Verifies that todo rejects missing and whitespace-only descriptions.
     */
    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        assertThrows(TedException.class, () -> Parser.parse("todo"));
        assertThrows(TedException.class, () -> Parser.parse("todo    "));
    }

    /**
     * Verifies that a deadline requires the /by separator.
     */
    @Test
    public void parse_deadlineWithoutBy_exceptionThrown() {
        assertThrows(TedException.class, () -> Parser.parse("deadline return book"));
    }

    /**
     * Verifies that a deadline requires a description.
     */
    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        assertThrows(TedException.class, () -> Parser.parse("deadline /by 2/12/2019 1800"));
    }

    /**
     * Verifies that a deadline requires a date and time after /by.
     */
    @Test
    public void parse_deadlineWithoutDateTime_exceptionThrown() {
        assertThrows(TedException.class, () -> Parser.parse("deadline return book /by"));
    }

    /**
     * Verifies that an unreadable deadline date and time appear in the error message.
     */
    @Test
    public void parse_deadlineWithUnreadableDateTime_exceptionThrown() {
        TedException e = assertThrows(TedException.class, () -> Parser.parse("deadline return book /by tomorrow"));
        assertTrue(e.getMessage().contains("tomorrow"));
    }

    /**
     * Verifies that a deadline rejects a date without a time.
     */
    @Test
    public void parse_deadlineWithDateButNoTime_exceptionThrown() {
        // The time is part of the expected format, so a bare date is rejected
        // rather than silently assumed to mean midnight.
        assertThrows(TedException.class, () -> Parser.parse("deadline return book /by 2/12/2019"));
    }

    /**
     * Verifies that a deadline rejects an impossible calendar date.
     */
    @Test
    public void parse_deadlineWithImpossibleDate_exceptionThrown() {
        // Both have the right shape, so the message must point at the values.
        TedException e = assertThrows(TedException.class, () -> Parser.parse("deadline essay /by 31/2/2019 1800"));
        assertTrue(e.getMessage().contains("not a real date"));
        e = assertThrows(TedException.class, () -> Parser.parse("deadline essay /by 2/12/2019 2400"));
        assertTrue(e.getMessage().contains("not a real date"));
    }

    /**
     * Verifies that an event requires both /from and /to separators.
     */
    @Test
    public void parse_eventMissingOneSeparator_exceptionThrown() {
        assertThrows(TedException.class, () -> Parser.parse("event meeting /from 2/12/2019 1400"));
        assertThrows(TedException.class, () -> Parser.parse("event meeting /to 2/12/2019 1600"));
    }

    /**
     * Verifies that an event rejects an end separator before its start separator.
     */
    @Test
    public void parse_eventWithSeparatorsSwapped_exceptionThrown() {
        assertThrows(TedException.class, () -> Parser.parse("event meeting /to 2/12/2019 1600 /from 2/12/2019 1400"));
    }

    /**
     * Verifies that /to within a description remains part of the event description.
     */
    @Test
    public void parse_eventDescriptionContainingSlashTo_descriptionKept() throws TedException {
        // "w/tom" contains "/to", which once was mistaken for the end-time separator.
        TaskList tasks = new TaskList();
        Parser.parse("event lunch w/tom /from 1/1/2026 1200 /to 1/1/2026 1300")
                .execute(tasks, new SilentUi(), new NoOpStorage());
        assertEquals("lunch w/tom", tasks.get(0).getDescription());
    }

    /**
     * Verifies that /by within a description remains part of the deadline description.
     */
    @Test
    public void parse_deadlineDescriptionContainingSlashBy_descriptionKept() throws TedException {
        TaskList tasks = new TaskList();
        Parser.parse("deadline read w/bytes /by 1/1/2026 1200")
                .execute(tasks, new SilentUi(), new NoOpStorage());
        assertEquals("read w/bytes", tasks.get(0).getDescription());
    }

    /**
     * Verifies that an event cannot end before it starts.
     */
    @Test
    public void parse_eventEndingBeforeStart_exceptionThrown() {
        assertThrows(TedException.class, () -> Parser.parse("event meeting /from 2/12/2019 1600 /to 2/12/2019 1400"));
    }

    /**
     * Verifies that an event cannot end at the moment it starts.
     */
    @Test
    public void parse_eventEndingWhenItStarts_exceptionThrown() {
        // A zero-length event is almost always a typo in one of the two times.
        assertThrows(TedException.class, () -> Parser.parse("event meeting /from 2/12/2019 1400 /to 2/12/2019 1400"));
    }

    /**
     * Verifies that delete and mark require a task number.
     */
    @Test
    public void parse_taskNumberMissing_exceptionThrown() {
        assertThrows(TedException.class, () -> Parser.parse("delete"));
        assertThrows(TedException.class, () -> Parser.parse("mark"));
    }

    /**
     * Verifies that a nonnumeric task number appears in the error message.
     */
    @Test
    public void parse_taskNumberNotANumber_exceptionThrown() {
        TedException e = assertThrows(TedException.class, () -> Parser.parse("delete two"));
        assertTrue(e.getMessage().contains("two"));
    }

    /**
     * Verifies that extra spaces around command arguments are accepted.
     */
    @Test
    public void parse_extraSpacesAroundArgument_argumentTrimmed() throws TedException {
        // Whether the user pads their input should make no difference.
        assertInstanceOf(AddCommand.class, Parser.parse("todo    borrow book   "));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete   2  "));
    }

    /**
     * Verifies that command keywords are recognized in any case, while the
     * rest of the input keeps the case it was typed in.
     */
    @Test
    public void parse_commandWordInCapitals_recognized() throws TedException {
        assertInstanceOf(ListCommand.class, Parser.parse("LIST"));
        assertInstanceOf(AddCommand.class, Parser.parse("Todo Borrow Book"));

        TaskList tasks = new TaskList();
        Parser.parse("Todo Borrow Book").execute(tasks, new SilentUi(), new NoOpStorage());
        assertEquals("[T][ ] Borrow Book", tasks.get(0).toString());
    }

    /**
     * Verifies that runs of spaces or tabs anywhere in the input are read as a
     * single space.
     */
    @Test
    public void parse_extraSpacesOrTabsInsideInput_readAsOneSpace() throws TedException {
        TaskList tasks = new TaskList();
        Command command = Parser.parse("deadline\treturn   book /by  2/12/2019   1800");
        command.execute(tasks, new SilentUi(), new NoOpStorage());
        assertEquals("D | 0 | 2019-12-02T18:00 | return book", tasks.get(0).toSaveFormat());
    }

    /**
     * Verifies that commands that work on their own reject anything typed after them.
     */
    @Test
    public void parse_listOrByeWithArgument_exceptionThrown() {
        TedException e = assertThrows(TedException.class, () -> Parser.parse("list #fun"));
        assertTrue(e.getMessage().contains("#fun"));
        assertThrows(TedException.class, () -> Parser.parse("bye now"));
    }

    /**
     * Verifies that giving an option twice is rejected, since there is no
     * telling which of the two the user meant.
     */
    @Test
    public void parse_optionGivenTwice_exceptionThrown() {
        String twoDueTimes = "deadline return book /by 2/12/2019 1800 /by 3/12/2019 1800";
        String twoStarts = "event meeting /from 2/12/2019 1400 /from 2/12/2019 1500 /to 2/12/2019 1600";
        String twoEnds = "event meeting /from 2/12/2019 1400 /to 2/12/2019 1500 /to 2/12/2019 1600";

        TedException e = assertThrows(TedException.class, () -> Parser.parse(twoDueTimes));
        assertTrue(e.getMessage().contains("You gave me /by twice"));
        assertThrows(TedException.class, () -> Parser.parse(twoStarts));
        assertThrows(TedException.class, () -> Parser.parse(twoEnds));
    }

    /**
     * Verifies that a command taking one task number rejects several.
     */
    @Test
    public void parse_severalTaskNumbers_exceptionThrown() {
        TedException e = assertThrows(TedException.class, () -> Parser.parse("delete 1 2"));
        assertTrue(e.getMessage().contains("One task at a time"));
    }

    /**
     * Verifies that a displayed task number is converted to a zero-based index.
     */
    @Test
    public void parse_deleteWithNumber_indexIsZeroBased() throws TedException {
        // The user counts from 1; a command built from "delete 1" must therefore
        // point at index 0. Proven by deleting from a one-task list.
        TaskList tasks = new TaskList(List.of(new Todo("only task")));
        Parser.parse("delete 1").execute(tasks, new SilentUi(), new NoOpStorage());
        assertEquals(0, tasks.size());
    }

    /**
     * Verifies that tag and untag with tags produce tag commands.
     */
    @Test
    public void parse_tagOrUntagWithTags_returnsTagCommand() throws TedException {
        assertInstanceOf(TagCommand.class, Parser.parse("tag 1 #fun"));
        assertInstanceOf(TagCommand.class, Parser.parse("untag 1 #fun #school"));
    }

    /**
     * Verifies that tagging normalizes case and ignores duplicate tags and extra spaces.
     */
    @Test
    public void parse_tagThenExecute_tagsTheTaskInLowerCase() throws TedException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        // Extra spaces between the tags, and a repeated tag, make no difference.
        Parser.parse("tag 1 #Fun   #school #FUN").execute(tasks, new SilentUi(), new NoOpStorage());
        assertEquals("[T][ ] read book #fun #school", tasks.get(0).toString());
    }

    /**
     * Verifies that untag removes only the specified tags.
     */
    @Test
    public void parse_untagThenExecute_removesOnlyThoseTags() throws TedException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Parser.parse("tag 1 #fun #school").execute(tasks, new SilentUi(), new NoOpStorage());
        Parser.parse("untag 1 #school").execute(tasks, new SilentUi(), new NoOpStorage());
        assertEquals("[T][ ] read book #fun", tasks.get(0).toString());
    }

    /**
     * Verifies that missing tag arguments produce usage examples for the requested command.
     */
    @Test
    public void parse_tagWithoutTaskOrTags_exceptionThrown() {
        TedException noArgument = assertThrows(TedException.class, () -> Parser.parse("tag"));
        assertTrue(noArgument.getMessage().contains("tag 2 #fun"));
        TedException noTags = assertThrows(TedException.class, () -> Parser.parse("untag 2"));
        assertTrue(noTags.getMessage().contains("untag 2 #fun"));
    }

    /**
     * Verifies that tagging rejects a nonnumeric task number with an explanation.
     */
    @Test
    public void parse_tagWithBadTaskNumber_exceptionThrown() {
        TedException e = assertThrows(TedException.class, () -> Parser.parse("tag two #fun"));
        assertTrue(e.getMessage().contains("\"two\" is not a task number"));
    }

    /**
     * Verifies that tagging rejects tags with a missing prefix, empty name, or invalid character.
     */
    @Test
    public void parse_tagWithInvalidTag_exceptionThrown() {
        TedException e = assertThrows(TedException.class, () -> Parser.parse("tag 2 fun"));
        assertTrue(e.getMessage().contains("\"fun\" is not a tag"));
        assertThrows(TedException.class, () -> Parser.parse("tag 2 #"));
        assertThrows(TedException.class, () -> Parser.parse("tag 2 #to-do"));
    }

    /**
     * Verifies that untag rejects absent tags without removing any existing tags.
     */
    @Test
    public void parse_untagTagTheTaskLacks_exceptionThrownAndTaskUnchanged() throws TedException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Parser.parse("tag 1 #fun").execute(tasks, new SilentUi(), new NoOpStorage());

        Command untag = Parser.parse("untag 1 #fun #nope");
        Storage storage = new NoOpStorage();
        TedException e = assertThrows(TedException.class, () -> untag.execute(tasks, new SilentUi(), storage));
        assertTrue(e.getMessage().contains("doesn't have the tag #nope"));
        // All or nothing: #fun must survive the rejected untag.
        assertEquals("[T][ ] read book #fun", tasks.get(0).toString());
    }

    /**
     * Verifies that tagging rejects a task number beyond the task list.
     */
    @Test
    public void parse_tagTaskNumberPastEnd_exceptionThrown() throws TedException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Command tag = Parser.parse("tag 9 #fun");
        assertThrows(TedException.class, () -> tag.execute(tasks, new SilentUi(), new NoOpStorage()));
    }

    /**
     * Verifies that a tag search ignores case and excludes tasks with only matching description text.
     */
    @Test
    public void parse_findWithTag_showsOnlyTasksWithThatTag() throws TedException {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("fun fair")));
        Parser.parse("tag 1 #fun").execute(tasks, new SilentUi(), new NoOpStorage());

        Ui ui = new SilentUi();
        Parser.parse("find #FUN").execute(tasks, ui, new NoOpStorage());
        String reply = ui.flush();
        assertTrue(reply.contains("read book #fun"));
        // "fun fair" contains the text "fun" but has no #fun tag.
        assertFalse(reply.contains("fun fair"));
    }

    /**
     * Verifies that tag searches reject multiple tags and empty tag names.
     */
    @Test
    public void parse_findWithInvalidTag_exceptionThrown() {
        TedException e = assertThrows(TedException.class, () -> Parser.parse("find #fun #school"));
        assertTrue(e.getMessage().contains("find #fun"));
        assertThrows(TedException.class, () -> Parser.parse("find #"));
    }

    /**
     * Verifies that a hash within a text keyword is accepted as part of the keyword.
     */
    @Test
    public void parse_findWithHashLaterInKeyword_returnsFindCommand() throws TedException {
        // Only a keyword that starts with # is a tag search.
        assertInstanceOf(FindCommand.class, Parser.parse("find book #fun"));
    }

    /**
     * A Ui that says nothing, so tests do not print over the test report.
     */
    private static class SilentUi extends Ui {
        /**
         * Suppresses removal messages during tests.
         *
         * @param task the removed task.
         * @param taskCount the number of remaining tasks.
         */
        @Override
        public void showRemoved(Task task, int taskCount) {
            // Deliberately silent.
        }
    }

    /**
     * Storage that keeps nothing, so tests never touch a real save file.
     */
    private static class NoOpStorage extends Storage {
        /**
         * Creates storage with an unused save path for tests.
         */
        NoOpStorage() {
            super("data/unused.txt");
        }

        /**
         * Suppresses writes to the save file during tests.
         *
         * @param tasks the tasks that would otherwise be saved.
         */
        @Override
        public void save(TaskList tasks) {
            // Deliberately does nothing.
        }
    }
}

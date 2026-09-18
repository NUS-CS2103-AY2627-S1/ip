package alfred;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alfred.command.AddCommand;
import alfred.command.Command;
import alfred.command.DeleteCommand;
import alfred.command.ExitCommand;
import alfred.command.FindCommand;
import alfred.command.ListCommand;
import alfred.command.MarkCommand;
import alfred.command.OnCommand;
import alfred.command.PriorityCommand;
import alfred.command.UnmarkCommand;
import alfred.task.TaskList;

/**
 * Tests {@link Parser#parse(String)}. A command has no getters, so what the
 * parser read is checked by carrying the command out against a task list of
 * its own. Refusals are checked word for word, since the user acts on them.
 */
public class ParserTest {
    private static final String DEADLINE_COMPLAINT = "A deadline needs a description and a /by date, sir.";

    private static final String EVENT_COMPLAINT =
            "An event needs a description, a /from date, and a /to date, sir.";

    private static final String NOT_A_NUMBER = "That is not a task number, sir.";

    private static final String NOT_A_LEVEL = "I know high, medium, low, and none as priorities, sir.";

    private static final String PRIORITY_COMPLAINT =
            "The priority command needs a task number and a level, sir.";

    private static final String FROM_BEFORE_TO = "The /from date has to come before the /to date, sir.";

    private TaskList tasks;

    private Ui ui;

    private Storage storage;

    /**
     * Gives each test its own task list and save file.
     *
     * @param tempDir a scratch folder JUnit creates and deletes per test.
     */
    @BeforeEach
    public void setUp(@TempDir Path tempDir) {
        tasks = new TaskList();
        ui = new Ui();
        storage = new Storage(tempDir.resolve("tasks.txt").toString());
    }

    @Test
    public void parse_bye_exitCommandReturned() throws AlfredException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_list_listCommandReturned() throws AlfredException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_todo_addCommandReturned() throws AlfredException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_deadline_addCommandReturned() throws AlfredException {
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-10-15"));
    }

    @Test
    public void parse_event_addCommandReturned() throws AlfredException {
        assertInstanceOf(AddCommand.class, Parser.parse("event meeting /from 2019-12-02 /to 2019-12-03"));
    }

    @Test
    public void parse_mark_markCommandReturned() throws AlfredException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
    }

    @Test
    public void parse_unmark_unmarkCommandReturned() throws AlfredException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_delete_deleteCommandReturned() throws AlfredException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_on_onCommandReturned() throws AlfredException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-10-15"));
    }

    @Test
    public void parse_find_findCommandReturned() throws AlfredException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_priority_priorityCommandReturned() throws AlfredException {
        assertInstanceOf(PriorityCommand.class, Parser.parse("priority 1 high"));
    }

    @Test
    public void parse_todoWithDescription_todoAdded() throws AlfredException {
        run("todo read book");
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void parse_extraSpacesBeforeDescription_descriptionTrimmed() throws AlfredException {
        run("todo    read book");
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void parse_tabsAndRunsOfSpaces_lineReadAsIfSingleSpaced() throws AlfredException {
        // A tab after the keyword would otherwise make it "mark<tab>1".
        run("   todo\tread    book   ");
        run("deadline   return book   /by   2019-10-15");
        run("mark\t1");
        run("priority   2   high");
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[D][ ][HIGH] return book (by: Oct 15 2019)", tasks.get(1).toString());
    }

    @Test
    public void parse_separatorInsideAnotherWord_descriptionKeptWhole() throws AlfredException {
        run("deadline fix a/by bug /by 2019-10-15");
        assertEquals("[D][ ] fix a/by bug (by: Oct 15 2019)", tasks.get(0).toString());
    }

    @Test
    public void parse_deadlineWithDate_dueDateRead() throws AlfredException {
        run("deadline return book /by 2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", tasks.get(0).toString());
    }

    @Test
    public void parse_eventWithDates_bothDatesRead() throws AlfredException {
        run("event project meeting /from 2019-12-02 /to 2019-12-03");
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)",
                tasks.get(0).toString());
    }

    @Test
    public void parse_eventDescriptionContainingTo_separatorAfterFromUsed() throws AlfredException {
        // The /to inside the description must not be taken for the separator.
        run("event lunch /to dinner /from 2019-12-02 /to 2019-12-03");
        assertEquals("[E][ ] lunch /to dinner (from: Dec 02 2019 to: Dec 03 2019)",
                tasks.get(0).toString());
    }

    @Test
    public void parse_eventStartingAndEndingSameDay_eventAdded() throws AlfredException {
        run("event sports day /from 2019-12-02 /to 2019-12-02");
        assertEquals("[E][ ] sports day (from: Dec 02 2019 to: Dec 02 2019)",
                tasks.get(0).toString());
    }

    @Test
    public void parse_markSecondTask_secondTaskMarked() throws AlfredException {
        run("todo first");
        run("todo second");
        run("mark 2");
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][X] second", tasks.get(1).toString());
    }

    @Test
    public void parse_unmarkMarkedTask_taskUnmarked() throws AlfredException {
        run("todo read book");
        run("mark 1");
        run("unmark 1");
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void parse_prioritySecondTask_secondTaskPrioritized() throws AlfredException {
        run("todo first");
        run("todo second");
        run("priority 2 high");
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][ ][HIGH] second", tasks.get(1).toString());
    }

    @Test
    public void parse_priorityNone_priorityTakenOff() throws AlfredException {
        run("todo read book");
        run("priority 1 low");
        run("priority 1 none");
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void parse_deleteFirstTask_firstTaskRemoved() throws AlfredException {
        run("todo first");
        run("todo second");
        run("delete 1");
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] second", tasks.get(0).toString());
    }

    @Test
    public void parse_emptyLine_exceptionThrown() {
        assertRefused("", "You'll have to give me something to work with, sir.");
    }

    @Test
    public void parse_unknownKeyword_keywordQuotedBack() {
        assertRefused("blah", "I'm afraid I don't know 'blah', sir.");
    }

    @Test
    public void parse_unknownKeywordWithArguments_onlyKeywordQuotedBack() {
        assertRefused("blah de blah", "I'm afraid I don't know 'blah', sir.");
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        assertRefused("todo", "A todo needs a description, sir.");
    }

    @Test
    public void parse_deadlineWithoutBySeparator_exceptionThrown() {
        assertRefused("deadline return book", DEADLINE_COMPLAINT);
    }

    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        assertRefused("deadline /by 2019-10-15", DEADLINE_COMPLAINT);
    }

    @Test
    public void parse_deadlineWithoutDate_exceptionThrown() {
        assertRefused("deadline return book /by", DEADLINE_COMPLAINT);
    }

    @Test
    public void parse_deadlineWithUnreadableDate_dateRefusalSurfaces() {
        assertRefused("deadline return book /by Sunday",
                "I don't know 'Sunday' as a date, sir. Do use yyyy-mm-dd, as in 2019-10-15.");
    }

    @Test
    public void parse_deadlineWithByTwice_exceptionThrown() {
        // Without this check the second /by would become part of the date.
        assertRefused("deadline return book /by 2019-10-15 /by 2019-10-16",
                "You've given /by more than once, sir. Once will do.");
    }

    @Test
    public void parse_eventWithToBeforeFrom_orderExplained() {
        assertRefused("event meeting /to 2019-12-03 /from 2019-12-02", FROM_BEFORE_TO);
    }

    @Test
    public void parse_eventWithoutFromSeparator_exceptionThrown() {
        assertRefused("event project meeting", EVENT_COMPLAINT);
    }

    @Test
    public void parse_eventWithoutToSeparator_exceptionThrown() {
        assertRefused("event project meeting /from 2019-12-02", EVENT_COMPLAINT);
    }

    @Test
    public void parse_eventWithoutDescription_exceptionThrown() {
        assertRefused("event /from 2019-12-02 /to 2019-12-03", EVENT_COMPLAINT);
    }

    @Test
    public void parse_eventWithoutEndDate_exceptionThrown() {
        assertRefused("event project meeting /from 2019-12-02 /to", EVENT_COMPLAINT);
    }

    @Test
    public void parse_eventEndingBeforeItStarts_exceptionThrown() {
        assertRefused("event project meeting /from 2019-12-03 /to 2019-12-02",
                "An event cannot end before it starts, sir.");
    }

    @Test
    public void parse_onWithoutDate_exceptionThrown() {
        assertRefused("on", "The on command needs a date, sir.");
    }

    @Test
    public void parse_onWithUnreadableDate_dateRefusalSurfaces() {
        assertRefused("on 2019-02-30",
                "There is no such date as '2019-02-30', sir. Do check the day and the month.");
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        assertRefused("find", "The find command needs a keyword, sir.");
    }

    @Test
    public void parse_markWithoutNumber_exceptionThrown() {
        assertRefused("mark", NOT_A_NUMBER);
    }

    @Test
    public void parse_markWithNonNumber_exceptionThrown() {
        assertRefused("mark two", NOT_A_NUMBER);
    }

    @Test
    public void parse_unmarkWithNonNumber_exceptionThrown() {
        assertRefused("unmark two", NOT_A_NUMBER);
    }

    @Test
    public void parse_deleteWithNonNumber_exceptionThrown() {
        assertRefused("delete two", NOT_A_NUMBER);
    }

    @Test
    public void parse_priorityWithoutAnything_exceptionThrown() {
        assertRefused("priority", PRIORITY_COMPLAINT);
    }

    @Test
    public void parse_priorityWithoutLevel_exceptionThrown() {
        assertRefused("priority 1", PRIORITY_COMPLAINT);
    }

    @Test
    public void parse_priorityWithoutNumber_exceptionThrown() {
        assertRefused("priority high", PRIORITY_COMPLAINT);
    }

    @Test
    public void parse_priorityWithNonNumber_exceptionThrown() {
        assertRefused("priority two high", NOT_A_NUMBER);
    }

    @Test
    public void parse_priorityWithUnknownLevel_levelRefusalSurfaces() {
        assertRefused("priority 1 urgent", NOT_A_LEVEL);
    }

    @Test
    public void parse_priorityWithWordsAfterTheLevel_exceptionThrown() {
        assertRefused("priority 1 high please", NOT_A_LEVEL);
    }

    /**
     * Reads a line and carries out the command it asks for.
     *
     * @param line the line to read, as the user would type it.
     * @throws AlfredException if the line is refused, or the command fails.
     */
    private void run(String line) throws AlfredException {
        Parser.parse(line).execute(tasks, ui, storage);
    }

    /**
     * Checks that a line is refused with one exact message.
     *
     * @param line the line to read.
     * @param expectedMessage the refusal the user should see.
     */
    private void assertRefused(String line, String expectedMessage) {
        AlfredException e = assertThrows(AlfredException.class, () -> Parser.parse(line));
        assertEquals(expectedMessage, e.getMessage());
    }
}

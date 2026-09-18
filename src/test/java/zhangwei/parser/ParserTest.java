package zhangwei.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zhangwei.ZhangWeiException;
import zhangwei.command.AddCommand;
import zhangwei.command.DeleteCommand;
import zhangwei.command.ExitCommand;
import zhangwei.command.FindCommand;
import zhangwei.command.ListCommand;
import zhangwei.command.MarkCommand;
import zhangwei.command.UnmarkCommand;
import zhangwei.storage.Storage;
import zhangwei.task.TaskList;
import zhangwei.task.Todo;
import zhangwei.ui.Ui;

/**
 * Tests {@link Parser}, the class that turns one typed line into a
 * {@link Command}. It holds the most branching logic in the program -- every
 * command word, every missing argument and every malformed date passes through
 * it -- so it is tested the most thoroughly.
 *
 * <p>The commands the parser builds keep their details private, so where the
 * details matter the test runs the command against a small task list and looks
 * at what happened to that list. That is also how the real program uses them.
 */
public class ParserTest {

    // JUnit injects a fresh temporary folder here; it must not be private.
    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;

    /**
     * Silences the chatbot's console output while a test runs, so executing a
     * command in a test does not clutter the test report.
     */
    @BeforeEach
    public void redirectOutput() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOut);
    }

    /** Returns storage pointing at a throwaway file, so saving does not touch real data. */
    private Storage tempStorage() {
        return new Storage(tempDir.resolve("tasks.txt").toString());
    }

    /**
     * Parses and runs the given line against the given list.
     * Returns the list so the caller can inspect the result.
     */
    private TaskList runOn(TaskList tasks, String input) throws ZhangWeiException {
        Parser.parse(input).execute(tasks, new Ui(), tempStorage());
        return tasks;
    }

    /** Returns how the task added by the given line reads to the user. */
    private String addedTask(String input) throws ZhangWeiException {
        return runOn(new TaskList(), input).get(1).toString();
    }

    /** Returns a list of three todos, for testing commands that take a task number. */
    private TaskList threeTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task 1"));
        tasks.add(new Todo("task 2"));
        tasks.add(new Todo("task 3"));
        return tasks;
    }

    // ---------- commands without arguments ----------

    @Test
    public void parse_bye_exitCommandReturned() throws ZhangWeiException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_list_listCommandReturned() throws ZhangWeiException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_byeWithTrailingSpaces_exitCommandReturned() throws ZhangWeiException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye   "));
    }

    @Test
    public void parse_listWithLeadingSpaces_listCommandReturned() throws ZhangWeiException {
        assertInstanceOf(ListCommand.class, Parser.parse("   list"));
    }

    // ---------- find ----------

    @Test
    public void parse_findWithKeyword_findCommandReturned() throws ZhangWeiException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("find"));
        assertTrue(e.getMessage().contains("What should I search for?"));
    }

    @Test
    public void parse_findWithOnlySpaces_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> Parser.parse("find    "));
    }

    // ---------- unknown input ----------

    @Test
    public void parse_unknownKeyword_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("blah"));
        assertTrue(e.getMessage().contains("blah"));
    }

    @Test
    public void parse_sortWithCriterion_listReordered() throws ZhangWeiException {
        TaskList tasks = new TaskList();
        runOn(tasks, "todo zebra");
        runOn(tasks, "todo apple");
        runOn(tasks, "sort description");

        assertEquals("apple", tasks.get(1).getDescription());
        assertEquals("zebra", tasks.get(2).getDescription());
    }

    @Test
    public void parse_sortWithoutCriterion_exceptionListsTheValidOnes() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("sort"));
        assertTrue(e.getMessage().contains("date, description, status"));
    }

    @Test
    public void parse_sortWithUnknownCriterion_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> Parser.parse("sort banana"));
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> Parser.parse(""));
    }

    @Test
    public void parse_uppercaseKeyword_exceptionThrown() {
        // Keywords are matched exactly, so "TODO" is not recognised.
        assertThrows(ZhangWeiException.class, () -> Parser.parse("TODO read book"));
    }

    // ---------- todo ----------

    @Test
    public void parse_todoWithDescription_addCommandReturned() throws ZhangWeiException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_todoWithDescription_todoAdded() throws ZhangWeiException {
        assertEquals("[T][ ] read book", addedTask("todo read book"));
    }

    @Test
    public void parse_todoWithExtraSpaces_descriptionTrimmed() throws ZhangWeiException {
        assertEquals("[T][ ] read book", addedTask("todo    read book   "));
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("todo"));
        assertTrue(e.getMessage().contains("needs a description"));
    }

    @Test
    public void parse_todoWithOnlySpaces_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> Parser.parse("todo    "));
    }

    @Test
    public void parse_todoContainingSeparator_exceptionThrown() {
        // "|" separates fields in the save file, so a task may not contain it.
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("todo read | book"));
        assertTrue(e.getMessage().contains("|"));
    }

    // ---------- deadline ----------

    @Test
    public void parse_deadlineWithValidDate_addCommandReturned() throws ZhangWeiException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("deadline return book /by 2019-12-02"));
    }

    @Test
    public void parse_deadlineWithValidDate_deadlineAdded() throws ZhangWeiException {
        assertEquals("[D][ ] return book (by: Dec 2 2019)",
                addedTask("deadline return book /by 2019-12-02"));
    }

    @Test
    public void parse_deadlineWithExtraSpaces_partsTrimmed() throws ZhangWeiException {
        assertEquals("[D][ ] return book (by: Dec 2 2019)",
                addedTask("deadline   return book   /by   2019-12-02  "));
    }

    @Test
    public void parse_deadlineWithoutBy_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("deadline return book"));
        assertTrue(e.getMessage().contains("/by"));
    }

    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> Parser.parse("deadline /by 2019-12-02"));
    }

    @Test
    public void parse_deadlineWithEmptyDate_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> Parser.parse("deadline return book /by"));
    }

    @Test
    public void parse_deadlineWithWrongDateFormat_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("deadline return book /by 02-12-2019"));
        assertTrue(e.getMessage().contains("yyyy-MM-dd"));
    }

    @Test
    public void parse_deadlineWithWordAsDate_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> Parser.parse("deadline return book /by Sunday"));
    }

    @Test
    public void parse_deadlineWithImpossibleDate_exceptionThrown() {
        // 30 February never exists, so LocalDate refuses it.
        assertThrows(ZhangWeiException.class,
                () -> Parser.parse("deadline return book /by 2019-02-30"));
    }

    @Test
    public void parse_deadlineContainingSeparator_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> Parser.parse("deadline return | book /by 2019-12-02"));
    }

    // ---------- event ----------

    @Test
    public void parse_eventWithValidDates_addCommandReturned() throws ZhangWeiException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event project meeting /from 2019-12-03 /to 2019-12-04"));
    }

    @Test
    public void parse_eventWithValidDates_eventAdded() throws ZhangWeiException {
        assertEquals("[E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)",
                addedTask("event project meeting /from 2019-12-03 /to 2019-12-04"));
    }

    @Test
    public void parse_eventWithoutTo_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("event project meeting /from 2019-12-03"));
        assertTrue(e.getMessage().contains("/to"));
    }

    @Test
    public void parse_eventWithoutFromOrTo_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> Parser.parse("event project meeting"));
    }

    @Test
    public void parse_eventWithoutDescription_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> Parser.parse("event /from 2019-12-03 /to 2019-12-04"));
    }

    @Test
    public void parse_eventWithEmptyToDate_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> Parser.parse("event project meeting /from 2019-12-03 /to"));
    }

    @Test
    public void parse_eventWithMalformedFromDate_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("event project meeting /from 3/12/2019 /to 2019-12-04"));
        assertTrue(e.getMessage().contains("/from"));
    }

    @Test
    public void parse_eventWithMalformedToDate_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("event project meeting /from 2019-12-03 /to 4/12/2019"));
        assertTrue(e.getMessage().contains("/to"));
    }

    @Test
    public void parse_eventEndingBeforeStart_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> Parser.parse("event project meeting /from 2019-12-05 /to 2019-12-01"));
    }

    @Test
    public void parse_eventWithToBeforeFrom_exceptionThrown() {
        // Otherwise the two dates would be read the wrong way round.
        assertThrows(ZhangWeiException.class,
                () -> Parser.parse("event project meeting /to 2019-12-04 /from 2019-12-03"));
    }

    @Test
    public void parse_eventStartingAndEndingSameDay_eventAdded() throws ZhangWeiException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event project meeting /from 2019-12-03 /to 2019-12-03"));
    }

    @Test
    public void parse_eventContainingSeparator_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> Parser.parse("event a | b /from 2019-12-03 /to 2019-12-04"));
    }

    // ---------- mark, unmark, delete ----------

    @Test
    public void parse_markWithNumber_markCommandReturned() throws ZhangWeiException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 2"));
    }

    @Test
    public void parse_markWithNumber_correctTaskMarked() throws ZhangWeiException {
        TaskList tasks = runOn(threeTasks(), "mark 2");
        assertFalse(tasks.get(1).isDone());
        assertTrue(tasks.get(2).isDone());
        assertFalse(tasks.get(3).isDone());
    }

    @Test
    public void parse_unmarkWithNumber_unmarkCommandReturned() throws ZhangWeiException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_unmarkWithNumber_correctTaskUnmarked() throws ZhangWeiException {
        TaskList tasks = threeTasks();
        tasks.get(2).markAsDone();
        runOn(tasks, "unmark 2");
        assertFalse(tasks.get(2).isDone());
    }

    @Test
    public void parse_deleteWithNumber_deleteCommandReturned() throws ZhangWeiException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 3"));
    }

    @Test
    public void parse_deleteWithNumber_correctTaskDeleted() throws ZhangWeiException {
        TaskList tasks = runOn(threeTasks(), "delete 2");
        assertEquals(2, tasks.size());
        assertEquals("task 3", tasks.get(2).getDescription());
    }

    @Test
    public void parse_markWithoutNumber_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("mark"));
        assertTrue(e.getMessage().contains("Which task?"));
    }

    @Test
    public void parse_markWithNonNumber_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> Parser.parse("mark two"));
        assertTrue(e.getMessage().contains("not a task number"));
    }

    @Test
    public void parse_deleteWithTwoNumbers_exceptionThrown() {
        // "2 3" is not a single task number, so it is refused rather than guessed at.
        assertThrows(ZhangWeiException.class, () -> Parser.parse("delete 2 3"));
    }

    @Test
    public void parse_markWithDecimalNumber_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> Parser.parse("mark 1.5"));
    }

    @Test
    public void parse_markWithOutOfRangeNumber_commandReturned() throws ZhangWeiException {
        // The parser only checks that it is a number; whether task 99 exists
        // depends on the task list, so that check happens when the command runs.
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 99"));
    }

    @Test
    public void parse_markWithOutOfRangeNumber_exceptionThrownOnExecute() {
        assertThrows(ZhangWeiException.class, () -> runOn(threeTasks(), "mark 99"));
    }
}

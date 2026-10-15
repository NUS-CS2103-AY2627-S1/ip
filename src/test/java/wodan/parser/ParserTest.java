package wodan.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import wodan.WodanException;
import wodan.command.AddCommand;
import wodan.command.DeleteCommand;
import wodan.command.ExitCommand;
import wodan.command.FindCommand;
import wodan.command.ListCommand;
import wodan.command.MarkCommand;
import wodan.command.OnCommand;
import wodan.command.TagCommand;
import wodan.command.UnmarkCommand;
import wodan.storage.Storage;
import wodan.task.Deadline;
import wodan.task.Event;
import wodan.task.Task;
import wodan.task.TaskList;
import wodan.task.Todo;
import wodan.ui.Ui;

/**
 * Unit tests for {@link Parser}, focusing on command interpretation and validation.
 */
public class ParserTest {
    @TempDir
    Path tempDir;

    @Test
    public void parse_todoWithDescription_addCommandWithTodo() throws WodanException {
        Task task = addedTask("todo borrow book");
        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] borrow book", task.toString());
    }

    @Test
    public void parse_deadlineWithIsoDate_addCommandWithDeadline() throws WodanException {
        Task task = addedTask("deadline return book /by 2019-12-02");
        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Dec 02 2019)", task.toString());
    }

    @Test
    public void parse_eventWithFromAndTo_addCommandWithEvent() throws WodanException {
        Task task = addedTask("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] meeting (from: Dec 02 2019, 2:00pm to: Dec 02 2019, 4:00pm)",
                task.toString());
    }

    @Test
    public void parse_listAndBye_matchingCommandTypes() throws WodanException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(ExitCommand.class, Parser.parse("BYE"));
        assertTrue(Parser.parse("bye").isExit());
        assertFalse(Parser.parse("list").isExit());
    }

    @Test
    public void parse_deleteOnAndFind_matchingCommandTypes() throws WodanException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(TagCommand.class, Parser.parse("tag 1 school"));
    }

    @Test
    public void parse_emptyLine_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse("   "));
        assertEquals(
                "Silence is not a command. Speak todo, deadline, event, list, mark, "
                        + "unmark, delete, on, find, tag, or bye.",
                ex.getMessage());
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse("blah"));
        assertEquals(CommandWord.unknownCommandMessage(), ex.getMessage());
    }

    @Test
    public void parse_todoMissingDescription_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse("todo"));
        assertEquals("A todo needs a quest name. Try: todo borrow book", ex.getMessage());
    }

    @Test
    public void parse_todoWithPipe_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse("todo a | b"));
        assertEquals(
                "A quest cannot contain '|'. The ravens use that mark in the save file.",
                ex.getMessage());
    }

    @Test
    public void parse_deadlineMissingBy_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "deadline return book"));
        assertEquals(
                "A deadline must include /by <when>. Try: deadline return book /by 2019-12-02",
                ex.getMessage());
    }

    @Test
    public void parse_deadlineDescriptionAfterBy_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "deadline /by 2019-12-02"));
        assertEquals(
                "A deadline needs a quest name before /by. Try: deadline return book /by 2019-12-02",
                ex.getMessage());
    }

    @Test
    public void parse_eventEndsBeforeStart_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /from 2019-12-02 1600 /to 2019-12-02 1400"));
        assertEquals("An event cannot end before or at the same time it starts.",
                ex.getMessage());
    }

    @Test
    public void parse_eventMissingTo_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /from 2019-12-02 1400"));
        assertEquals(
                "An event must include /to <end>. "
                        + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600",
                ex.getMessage());
    }

    @Test
    public void parse_todoWithExtraSpaces_collapsesGaps() throws WodanException {
        Task task = addedTask("  todo   borrow   book  ");
        assertEquals("[T][ ] borrow book", task.toString());
    }

    @Test
    public void parse_todoWithDateMarker_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "todo borrow book /by 2019-12-02"));
        assertEquals(
                "A todo has no date. Leave out /by, /from, and /to. Try: todo borrow book",
                ex.getMessage());
    }

    @Test
    public void parse_deadlineRepeatedBy_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "deadline return book /by 2019-12-02 /by 2019-12-03"));
        assertEquals(
                "Use /by only once. Try: deadline return book /by 2019-12-02",
                ex.getMessage());
    }

    @Test
    public void parse_deadlineWithFrom_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "deadline return book /from 2019-12-02"));
        assertEquals(
                "A deadline uses /by, not /from or /to. Try: deadline return book /by 2019-12-02",
                ex.getMessage());
    }

    @Test
    public void parse_eventEndsAtSameTime_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /from 2019-12-02 1400 /to 2019-12-02 1400"));
        assertEquals("An event cannot end before or at the same time it starts.",
                ex.getMessage());
    }

    @Test
    public void parse_eventSameDateOnly_addCommandWithEvent() throws WodanException {
        Task task = addedTask("event camp /from 2019-12-02 /to 2019-12-02");
        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] camp (from: Dec 02 2019 to: Dec 02 2019)", task.toString());
    }

    @Test
    public void parse_eventRepeatedTo_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /from 2019-12-02 1400 /to 2019-12-02 1600 /to 2019-12-02 1700"));
        assertEquals(
                "Use /to only once. "
                        + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600",
                ex.getMessage());
    }

    @Test
    public void parseMarkNumber_extraToken_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseMarkNumber(
                "1 extra", tasks));
        assertEquals("mark takes only a quest number. Try: mark 1", ex.getMessage());
    }

    @Test
    public void parse_duplicateTodo_exceptionThrown() throws WodanException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("wodan.txt").toString());
        Parser.parse("todo borrow book").execute(tasks, new Ui(), storage);
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "todo borrow book").execute(tasks, new Ui(), storage));
        assertEquals(
                "That quest is already on the list. The ravens will not record it twice.",
                ex.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    public void parseDeleteNumber_validIndex_returnsNumber() throws WodanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        assertEquals(2, Parser.parseDeleteNumber("2", tasks));
    }

    @Test
    public void parseDeleteNumber_emptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseDeleteNumber(
                "1", tasks));
        assertEquals(
                "There are no quests to delete yet. Add one with todo, deadline, or event.",
                ex.getMessage());
    }

    @Test
    public void parseDeleteNumber_outOfRange_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseDeleteNumber(
                "3", tasks));
        assertEquals(
                "There is no quest 3. The ravens watch over 1 quest. Try a number from 1 to 1.",
                ex.getMessage());
    }

    @Test
    public void parseDeleteNumber_notANumber_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseDeleteNumber(
                "first", tasks));
        assertEquals("'first' is not a quest number. Try: delete 1", ex.getMessage());
    }

    @Test
    public void parseDeleteNumber_missingNumber_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseDeleteNumber(
                "", new TaskList()));
        assertEquals("Which quest is too burdensome? Try: delete 1", ex.getMessage());
    }

    @Test
    public void parseMarkNumber_missingNumber_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseMarkNumber(
                "  ", new TaskList()));
        assertEquals("Which quest should the ravens mark? Try: mark 1", ex.getMessage());
    }

    @Test
    public void parseOnDate_isoDate_returnsLocalDate() throws WodanException {
        assertEquals(java.time.LocalDate.of(2019, 12, 2), Parser.parseOnDate("2019-12-02"));
    }

    @Test
    public void parseOnDate_missingDate_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseOnDate(""));
        assertEquals("Which day should the ravens search? Try: on 2019-12-02", ex.getMessage());
    }

    @Test
    public void parseFindKeyword_trimmedText_returnsKeyword() throws WodanException {
        assertEquals("book", Parser.parseFindKeyword("  book  "));
        assertEquals("return book", Parser.parseFindKeyword("return book"));
    }

    @Test
    public void parseTagNumber_validIndex_returnsNumber() throws WodanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        assertEquals(2, Parser.parseTagNumber("2 school", tasks));
    }

    @Test
    public void parseTagNumber_emptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseTagNumber(
                "1 school", tasks));
        assertEquals(
                "There are no quests to tag yet. Add one with todo, deadline, or event.",
                ex.getMessage());
    }

    @Test
    public void parseTagCategory_trimmedName_returnsCategory() throws WodanException {
        assertEquals("school", Parser.parseTagCategory("1 school"));
        assertEquals("group project", Parser.parseTagCategory("2  group project  "));
    }

    @Test
    public void parseTagCategory_missingName_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseTagCategory("1"));
        assertEquals("Name the brand. Try: tag 1 school", ex.getMessage());
    }

    @Test
    public void parseTagCategory_pipeInName_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseTagCategory(
                "1 a | b"));
        assertEquals(
                "A quest cannot contain '|'. The ravens use that mark in the save file.",
                ex.getMessage());
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseFindKeyword(""));
        assertEquals("Which word should the ravens seek? Try: find book", ex.getMessage());
    }

    @Test
    public void parse_markUnmarkAndDeadlineWithTime_matchingTypes() throws WodanException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("UNMARK 1"));
        Task task = addedTask("deadline return book /by 2/12/2019 1800");
        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00pm)", task.toString());
    }

    @Test
    public void parse_deadlineEmptyWhen_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "deadline return book /by   "));
        assertEquals(
                "The ravens need a time after /by. Try: deadline return book /by 2019-12-02",
                ex.getMessage());
    }

    @Test
    public void parse_deadlineEmptyDescription_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse("deadline"));
        assertEquals(
                "A deadline needs a quest name and /by <when>. "
                        + "Try: deadline return book /by 2019-12-02",
                ex.getMessage());
    }

    @Test
    public void parse_deadlinePipeInBy_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "deadline return book /by 2019|12-02"));
        assertEquals(
                "A quest cannot contain '|'. The ravens use that mark in the save file.",
                ex.getMessage());
    }

    @Test
    public void parse_eventMissingFrom_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting"));
        assertEquals(
                "An event must include /from <start> and /to <end>. "
                        + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600",
                ex.getMessage());
    }

    @Test
    public void parse_eventEmptyTo_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /from 2019-12-02 1400 /to"));
        assertEquals(
                "The ravens need an end time after /to. "
                        + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600",
                ex.getMessage());
    }

    @Test
    public void parse_eventEmptyFrom_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /from /to 2019-12-02 1600"));
        assertEquals(
                "The ravens need a start time after /from. "
                        + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600",
                ex.getMessage());
    }

    @Test
    public void parse_eventEmptyDescription_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse("event"));
        assertEquals(
                "An event needs a name, /from <start>, and /to <end>. "
                        + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600",
                ex.getMessage());
    }

    @Test
    public void parse_eventWithBy_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /by 2019-12-02 /from 2019-12-02 1400 /to 2019-12-02 1600"));
        assertEquals(
                "An event uses /from and /to, not /by. "
                        + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600",
                ex.getMessage());
    }

    @Test
    public void parse_eventRepeatedFrom_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /from 2019-12-02 1400 /from 2019-12-02 1500 /to 2019-12-02 1600"));
        assertEquals(
                "Use /from only once. "
                        + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600",
                ex.getMessage());
    }

    @Test
    public void parse_eventPipeInName_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meet | ing /from 2019-12-02 1400 /to 2019-12-02 1600"));
        assertEquals(
                "A quest cannot contain '|'. The ravens use that mark in the save file.",
                ex.getMessage());
    }

    @Test
    public void parseUnmarkNumber_extraTokenAndOutOfRange_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        WodanException extra = assertThrows(WodanException.class, () -> Parser.parseUnmarkNumber(
                "1 extra", tasks));
        assertEquals("unmark takes only a quest number. Try: unmark 1", extra.getMessage());
        WodanException range = assertThrows(WodanException.class, () -> Parser.parseUnmarkNumber(
                "2", tasks));
        assertEquals(
                "There is no quest 2. The ravens watch over 1 quest. Try a number from 1 to 1.",
                range.getMessage());
    }

    @Test
    public void parseDeleteNumber_extraToken_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseDeleteNumber(
                "1 extra", tasks));
        assertEquals("delete takes only a quest number. Try: delete 1", ex.getMessage());
    }

    @Test
    public void parseOnDate_paddedDate_returnsLocalDate() throws WodanException {
        assertEquals(java.time.LocalDate.of(2019, 12, 2), Parser.parseOnDate("  2019-12-02  "));
    }

    @Test
    public void parseFindKeyword_collapsedSpaces_returnsKeyword() throws WodanException {
        assertEquals("return book", Parser.parseFindKeyword("  return   book  "));
    }

    @Test
    public void parseMarkNumber_notANumberAndOutOfRange_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        WodanException notNumber = assertThrows(WodanException.class, () -> Parser.parseMarkNumber(
                "first", tasks));
        assertEquals("'first' is not a quest number. Try: mark 1", notNumber.getMessage());
        WodanException empty = assertThrows(WodanException.class, () -> Parser.parseMarkNumber(
                "1", new TaskList()));
        assertEquals(
                "There are no quests to mark yet. Add one with todo, deadline, or event.",
                empty.getMessage());
    }

    @Test
    public void parse_todoWithFrom_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "todo borrow book /from 2019-12-02"));
        assertEquals(
                "A todo has no date. Leave out /by, /from, and /to. Try: todo borrow book",
                ex.getMessage());
    }

    @Test
    public void parse_deadlineWithTo_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "deadline return book /by 2019-12-02 /to 2019-12-03"));
        assertEquals(
                "A deadline uses /by, not /from or /to. Try: deadline return book /by 2019-12-02",
                ex.getMessage());
    }

    @Test
    public void parse_eventNameMissingBeforeFrom_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event /from 2019-12-02 /to 2019-12-03"));
        assertEquals(
                "An event needs a quest name before /from. "
                        + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600",
                ex.getMessage());
    }

    @Test
    public void parse_eventPipeInFrom_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /from 2019|12-02 /to 2019-12-03"));
        assertEquals(
                "A quest cannot contain '|'. The ravens use that mark in the save file.",
                ex.getMessage());
    }

    @Test
    public void parseMarkNumber_validIndex_returnsNumber() throws WodanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        assertEquals(1, Parser.parseMarkNumber("1", tasks));
        assertEquals(1, Parser.parseUnmarkNumber("1", tasks));
    }

    @Test
    public void parseUnmarkNumber_emptyListAndMissing_exceptionThrown() {
        WodanException empty = assertThrows(WodanException.class, () -> Parser.parseUnmarkNumber(
                "1", new TaskList()));
        assertEquals(
                "There are no quests to unmark yet. Add one with todo, deadline, or event.",
                empty.getMessage());
        WodanException missing = assertThrows(WodanException.class, () -> Parser.parseUnmarkNumber(
                "", new TaskList()));
        assertEquals("Which quest should the ravens unmark? Try: unmark 1", missing.getMessage());
    }

    @Test
    public void parseTagNumber_notANumberAndOutOfRange_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        WodanException notNumber = assertThrows(WodanException.class, () -> Parser.parseTagNumber(
                "first school", tasks));
        assertEquals("'first' is not a quest number. Try: tag 1 school", notNumber.getMessage());
        WodanException range = assertThrows(WodanException.class, () -> Parser.parseTagNumber(
                "3 school", tasks));
        assertEquals(
                "There is no quest 3. The ravens watch over 1 quest. Try a number from 1 to 1.",
                range.getMessage());
        WodanException missing = assertThrows(WodanException.class, () -> Parser.parseTagNumber(
                "", tasks));
        assertEquals("Which quest should the ravens brand? Try: tag 1 school", missing.getMessage());
    }

    @Test
    public void parseOnDate_invalidDate_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseOnDate(
                "not-a-date"));
        assertEquals(
                "The ravens cannot read 'not-a-date'. Try: 2026-08-26 or 08/26/2019 1800",
                ex.getMessage());
    }

    @Test
    public void parseDeleteNumber_outOfRangePlural_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"), new Todo("b"));
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseDeleteNumber(
                "5", tasks));
        assertEquals(
                "There is no quest 5. The ravens watch over 2 quests. Try a number from 1 to 2.",
                ex.getMessage());
    }

    private Task addedTask(String command) throws WodanException {
        TaskList tasks = new TaskList();
        Parser.parse(command).execute(tasks, new Ui(),
                new Storage(tempDir.resolve("wodan.txt").toString()));
        assertInstanceOf(AddCommand.class, Parser.parse(command));
        return tasks.get(0);
    }
}

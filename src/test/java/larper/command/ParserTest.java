package larper.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import larper.exception.InvalidDateException;
import larper.exception.InvalidDescriptionException;
import larper.exception.InvalidTagException;
import larper.exception.InvalidTimeException;
import larper.exception.NoDescriptionException;
import larper.exception.NoTaskTypeException;
import larper.exception.MissingTagException;
import larper.exception.NonNumberDeleteException;
import larper.task.Deadline;
import larper.task.Event;
import larper.task.Task;
import larper.task.Todo;

public class ParserTest {
    private static final String TODAY_PROPERTY = "larper.today";

    private Parser parser;

    @BeforeEach
    public void setUp() {
        System.setProperty(TODAY_PROPERTY, "2026-08-23");
        parser = new Parser();
    }

    @AfterEach
    public void tearDown() {
        System.clearProperty(TODAY_PROPERTY);
    }

    @Test
    public void commandChecks_knownCommands_expectedBoolean() {
        assertTrue(parser.isExitCommand("exit"));
        assertFalse(parser.isExitCommand("bye"));

        assertTrue(parser.isListCommand("list"));
        assertFalse(parser.isListCommand("list all"));

        assertTrue(parser.isMarkCommand("mark 1"));
        assertFalse(parser.isMarkCommand("please mark 1"));

        assertTrue(parser.isUnmarkCommand("unmark 1"));
        assertFalse(parser.isUnmarkCommand("please unmark 1"));

        assertTrue(parser.isDeleteCommand("delete 1"));
        assertTrue(parser.isDeleteCommand("please delete 1"));
        assertFalse(parser.isDeleteCommand("deleted 1"));

        assertTrue(parser.isFindCommand("find"));
        assertFalse(parser.isFindCommand("find book"));

        assertTrue(parser.isTagCommand("tag 1 school"));
        assertTrue(parser.isUntagCommand("untag 1 school"));
        assertTrue(parser.isFindTagCommand("find tag school"));
        assertFalse(parser.isFindTagCommand("find tagger"));
    }

    @Test
    public void parseTaskNumber_validAndInvalidNumbers_expectedInteger() {
        assertEquals(12, parser.parseMarkNumber("mark 12"));
        assertEquals(-1, parser.parseMarkNumber("mark abc"));

        assertEquals(3, parser.parseUnmarkNumber("unmark 3"));
        assertEquals(-1, parser.parseUnmarkNumber("unmark abc"));
    }

    @Test
    public void parseDeleteNumber_validEmbeddedCommand_expectedInteger() throws NonNumberDeleteException {
        assertEquals(1, parser.parseDeleteNumber("delete 1"));
        assertEquals(89, parser.parseDeleteNumber("please delete 89"));
        assertEquals(-3, parser.parseDeleteNumber("please delete -3"));
    }

    @Test
    public void parseDeleteNumber_missingOrNonNumber_exceptionThrown() {
        assertThrows(NonNumberDeleteException.class, () -> parser.parseDeleteNumber("delete"));
        assertThrows(NonNumberDeleteException.class, () -> parser.parseDeleteNumber("delete two"));
    }

    @Test
    public void parseTagCommand_multipleNamesAndNumberSign_normalized() throws Exception {
        assertEquals(2, parser.parseTagTaskNumber("tag 2 #FUN school"));
        assertEquals("[fun, school]", parser.parseTagNames("tag 2 #FUN school").toString());
        assertEquals(2, parser.parseTagTaskNumber("untag 2 school"));
        assertEquals("[school]", parser.parseTagNames("untag 2 school").toString());
        assertEquals("school", parser.parseFindTag("find tag #SCHOOL"));
    }

    @Test
    public void parseTagCommand_missingOrInvalidTag_expectedExceptions() {
        assertThrows(MissingTagException.class, () -> parser.parseTagNames("tag 1"));
        assertThrows(InvalidTagException.class, () -> parser.parseTagNames("tag 1 school-time"));
        assertThrows(MissingTagException.class, () -> parser.parseFindTag("find tag"));
        assertThrows(InvalidTagException.class, () -> parser.parseFindTag("find tag school-time"));
    }

    @Test
    public void parseTask_validTodoDeadlineEvent_expectedTaskTypesAndStrings() throws Exception {
        Task todo = parser.parseTask("todo read book");
        assertInstanceOf(Todo.class, todo);
        assertEquals("[T][ ] read book", todo.toString());

        Task deadline = parser.parseTask("deadline submit report /by 2019-10-15 2pm");
        assertInstanceOf(Deadline.class, deadline);
        assertEquals("[D][ ] submit report (by: Oct 15 2019 1400)", deadline.toString());

        Task event = parser.parseTask("event project meeting /from aug 8 2pm /to aug 8 4pm");
        assertInstanceOf(Event.class, event);
        assertEquals("[E][ ] project meeting (from: Aug 08 2026 1400 to: Aug 08 2026 1600)",
                event.toString());
    }

    @Test
    public void parseTask_inlineTags_normalizedAndPlacedAfterTaskDetails() throws Exception {
        Task todo = parser.parseTask("todo read book #FUN #school");
        assertEquals("[T][ ] read book [#fun] [#school]", todo.toString());
        assertEquals("T | 0 | read book | #fun #school", todo.toFileString());

        Task deadline = parser.parseTask("deadline return book #Library /by 2026-08-23 no time");
        assertEquals("[D][ ] return book (by: Aug 23 2026) [#library]", deadline.toString());

        Task event = parser.parseTask("event project sync #Work /from 2026-08-24 2pm /to 2026-08-24 4pm");
        assertEquals("[E][ ] project sync (from: Aug 24 2026 1400 to: Aug 24 2026 1600) [#work]",
                event.toString());
    }

    @Test
    public void parseTask_invalidInlineTag_exceptionThrown() {
        assertThrows(InvalidTagException.class, () -> parser.parseTask("todo read book #fun-time"));
        assertThrows(InvalidTagException.class, () -> parser.parseTask("todo read book #"));
    }

    @Test
    public void parseTask_missingTime_tagsRetainedAfterFollowUp() throws Exception {
        assertThrows(InvalidTimeException.class,
                () -> parser.parseTask("deadline return book #Library /by 2026-03-09"));

        Task completedTask = parser.parseTask("no time");

        assertEquals("[D][ ] return book (by: Mar 09 2026) [#library]", completedTask.toString());
    }

    @Test
    public void parseTask_missingTypeOrDescription_expectedExceptions() {
        assertThrows(NoTaskTypeException.class, () -> parser.parseTask(""));
        assertThrows(NoTaskTypeException.class, () -> parser.parseTask("read book"));
        assertThrows(NoDescriptionException.class, () -> parser.parseTask("todo"));
        assertThrows(NoDescriptionException.class, () -> parser.parseTask("deadline"));
        assertThrows(NoDescriptionException.class, () -> parser.parseTask("event"));
        assertThrows(NoDescriptionException.class, () -> parser.parseTask("todo read /by 2026-08-23"));
        assertThrows(InvalidDescriptionException.class, () -> parser.parseTask("todo compare A | B"));
    }

    @Test
    public void parseTask_missingDateMarkers_expectedInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> parser.parseTask("deadline return book 2026-08-23"));
        assertThrows(InvalidDateException.class, () -> parser.parseTask("event meeting /from aug 8 2pm"));
        assertThrows(InvalidDateException.class, () -> parser.parseTask("event meeting /to aug 8 4pm"));
    }

    @Test
    public void parseTask_missingOptionalDeadlineTime_followUpCompletesTask() throws Exception {
        assertThrows(InvalidTimeException.class, () -> parser.parseTask("deadline return book /by 2026-03-09"));

        Task completedTask = parser.parseTask("no time");
        assertInstanceOf(Deadline.class, completedTask);
        assertEquals("[D][ ] return book (by: Mar 09 2026)", completedTask.toString());
    }

    @Test
    public void parseTask_invalidFollowUpTime_pendingTaskStillCompletesAfterValidAnswer() throws Exception {
        assertThrows(InvalidTimeException.class, () -> parser.parseTask("deadline return book /by 2026-03-09"));
        assertThrows(InvalidTimeException.class, () -> parser.parseTask("later"));

        Task completedTask = parser.parseTask("2pm");
        assertInstanceOf(Deadline.class, completedTask);
        assertEquals("[D][ ] return book (by: Mar 09 2026 1400)", completedTask.toString());
    }

    @Test
    public void parseTask_newTaskCommandAfterPendingTime_clearsPendingTask() throws Exception {
        assertThrows(InvalidTimeException.class, () -> parser.parseTask("deadline return book /by 2026-03-09"));

        Task todo = parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, todo);
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void parseTask_missingEventTimes_followUpsCompleteTask() throws Exception {
        assertThrows(InvalidTimeException.class, () -> parser.parseTask("event meeting /from 9 mar /to 10 mar"));
        assertThrows(InvalidTimeException.class, () -> parser.parseTask("2pm"));

        Task completedTask = parser.parseTask("no time");
        assertInstanceOf(Event.class, completedTask);
        assertEquals("[E][ ] meeting (from: Mar 09 2026 1400 to: Mar 10 2026)", completedTask.toString());
    }

    @Test
    public void parseTask_missingOnlyEventEndTime_followUpCompletesTask() throws Exception {
        assertThrows(InvalidTimeException.class, () -> parser.parseTask("event meeting /from 9 mar 2pm /to 10 mar"));

        Task completedTask = parser.parseTask("4pm");
        assertInstanceOf(Event.class, completedTask);
        assertEquals("[E][ ] meeting (from: Mar 09 2026 1400 to: Mar 10 2026 1600)",
                completedTask.toString());
    }
}

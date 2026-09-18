package sozius.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sozius.exception.SoziusException;
import sozius.tasklist.TaskList;

public class ParserTest {

    private TaskList tasks;
    private Parser parser;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        parser = new Parser(tasks);
    }

    // ---------- list ----------

    @Test
    public void parse_listEmpty_showsNoTasks() throws SoziusException {
        assertEquals("No tasks found", parser.parse("list"));
    }

    @Test
    public void parse_listWithTasks_showsNumberedTasks() throws SoziusException {
        parser.parse("todo read book");
        parser.parse("todo write report");
        String expected = "1. [T][ ] read book\n"
                + "2. [T][ ] write report\n";
        assertEquals(expected, parser.parse("list"));
    }

    @Test
    public void parse_listWithArguments_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> parser.parse("list all"));
        assertTrue(e.getMessage().contains("does not take any arguments"));
    }

    // ---------- todo ----------

    @Test
    public void parse_todo_addsTask() throws SoziusException {
        String response = parser.parse("todo read book");
        assertEquals("Got it. I've added this task:\n"
                + "[T][ ] read book\n"
                + "Now you have 1 tasks in the list", response);
        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
    }

    @Test
    public void parse_todoMissingDescription_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> parser.parse("todo"));
        assertTrue(e.getMessage().contains("Missing description"));
        assertEquals(0, tasks.size());
    }

    // ---------- deadline ----------

    @Test
    public void parse_deadline_addsTask() throws SoziusException {
        String response = parser.parse("deadline return book /by 2024-12-31 1800");
        assertEquals("Got it. I've added this task:\n"
                + "[D][ ] return book (by: Dec 31 2024 1800)\n"
                + "Now you have 1 tasks in the list", response);
    }

    @Test
    public void parse_deadlineDateOnly_addsTask() throws SoziusException {
        parser.parse("deadline return book /by 2024-12-31");
        assertEquals("[D][ ] return book (by: Dec 31 2024)",
                tasks.get(0).toUserString());
    }

    @Test
    public void parse_deadlineMissingEverything_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> parser.parse("deadline"));
        assertTrue(e.getMessage().contains("Missing description and duedate"));
    }

    @Test
    public void parse_deadlineMissingBy_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> parser.parse("deadline return book"));
        assertTrue(e.getMessage().contains("/by parameter should be provided exactly once"));
        assertEquals(0, tasks.size());
    }

    @Test
    public void parse_deadlineMissingDescription_throwsException() {
        assertThrows(SoziusException.class, () -> parser.parse("deadline /by 2024-12-31"));
        assertEquals(0, tasks.size());
    }

    @Test
    public void parse_deadlineMissingDate_throwsException() {
        assertThrows(SoziusException.class, () -> parser.parse("deadline return book /by"));
        assertEquals(0, tasks.size());
    }

    @Test
    public void parse_deadlineInvalidDate_throwsException() {
        assertThrows(SoziusException.class, () -> parser.parse("deadline return book /by tomorrow"));
        assertEquals(0, tasks.size());
    }

    // ---------- event ----------

    @Test
    public void parse_event_addsTask() throws SoziusException {
        String response = parser.parse("event meeting /from 2024-01-01 1000 /to 2024-01-01 1200");
        assertEquals("Got it. I've added this task:\n"
                + "[E][ ] meeting (from: Jan 1 2024 1000 to: Jan 1 2024 1200)\n"
                + "Now you have 1 tasks in the list", response);
    }

    @Test
    public void parse_eventMissingFrom_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> parser.parse("event meeting /to 2024-01-01"));
        assertTrue(e.getMessage().contains("/from parameter should be provided exactly once"));
    }

    @Test
    public void parse_eventMissingTo_throwsException() {
        SoziusException e = assertThrows(
                SoziusException.class, () -> parser.parse("event meeting /from 2024-01-01"));
        assertTrue(e.getMessage().contains("/to parameter should be provided exactly once"));
    }

    @Test
    public void parse_eventMissingDescription_throwsException() {
        assertThrows(SoziusException.class, () -> parser.parse("event /from 2024-01-01 /to 2024-01-02"));
        assertEquals(0, tasks.size());
    }

    // ---------- mark / unmark ----------

    @Test
    public void parse_mark_marksTaskDone() throws SoziusException {
        parser.parse("todo read book");
        String response = parser.parse("mark 1");
        assertEquals("Marked as done:\n[T][X] read book", response);
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void parse_markAlreadyDone_showsMessage() throws SoziusException {
        parser.parse("todo read book");
        parser.parse("mark 1");
        String response = parser.parse("mark 1");
        assertEquals("This task is already marked as done:\n[T][X] read book", response);
    }

    @Test
    public void parse_unmark_marksTaskNotDone() throws SoziusException {
        parser.parse("todo read book");
        parser.parse("mark 1");
        String response = parser.parse("unmark 1");
        assertEquals("Marked as not done:\n[T][ ] read book", response);
        assertTrue(!tasks.get(0).isDone());
    }

    @Test
    public void parse_unmarkNotDone_showsMessage() throws SoziusException {
        parser.parse("todo read book");
        String response = parser.parse("unmark 1");
        assertEquals("This task is not marked as done yet:\n[T][ ] read book", response);
    }

    @Test
    public void parse_markMissingIndex_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> parser.parse("mark"));
        assertTrue(e.getMessage().contains("Missing task number"));
    }

    @Test
    public void parse_markIndexOutOfRange_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> parser.parse("mark 1"));
        assertTrue(e.getMessage().contains("Invalid task number"));
    }

    @Test
    public void parse_markNonNumericIndex_throwsException() {
        // a non-numeric index should be rejected with a SoziusException, not crash
        assertThrows(SoziusException.class, () -> parser.parse("mark abc"));
    }

    // ---------- delete ----------

    @Test
    public void parse_delete_removesTask() throws SoziusException {
        parser.parse("todo read book");
        parser.parse("todo write report");
        String response = parser.parse("delete 1");
        assertEquals("Task deleted:\n[T][ ] read book", response);
        assertEquals(1, tasks.size());
        assertEquals("write report", tasks.get(0).getDescription());
    }

    @Test
    public void parse_deleteIndexOutOfRange_throwsException() {
        assertThrows(SoziusException.class, () -> parser.parse("delete 3"));
    }

    // ---------- find ----------

    @Test
    public void parse_findMatchingTask_showsMatch() throws SoziusException {
        parser.parse("todo read book");
        parser.parse("todo write report");
        String response = parser.parse("find book");
        assertTrue(response.startsWith("Searching for tasks:\n"));
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(!response.contains("write report"));
        assertTrue(response.contains("Found 1 task"));
    }

    @Test
    public void parse_findCaseInsensitive_showsMatches() throws SoziusException {
        parser.parse("todo Read Book");
        parser.parse("todo read report");
        String response = parser.parse("find READ");
        assertTrue(response.contains("Read Book"));
        assertTrue(response.contains("read report"));
        assertTrue(response.contains("Found 2 tasks"));
    }

    @Test
    public void parse_findNoMatch_showsZeroFound() throws SoziusException {
        parser.parse("todo read book");
        String response = parser.parse("find xyz");
        assertTrue(response.contains("Found 0 tasks"));
    }

    @Test
    public void parse_findMissingKeyword_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> parser.parse("find"));
        assertTrue(e.getMessage().contains("Missing keyword"));
    }

    // ---------- help / unknown ----------

    @Test
    public void parse_help_showsCommandList() throws SoziusException {
        String response = parser.parse("help");
        assertTrue(response.startsWith("List of commands:"));
        assertTrue(response.contains("list, ls:"));
        assertTrue(response.contains("find, f:"));
    }

    @Test
    public void parse_helpWithArguments_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> parser.parse("help me"));
        assertTrue(e.getMessage().contains("does not take any arguments"));
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> parser.parse("bye"));
        assertTrue(e.getMessage().contains("Unknown command"));
    }

    // ---------- aliases ----------

    @Test
    public void parse_aliases_behaveLikeFullCommands() throws SoziusException {
        parser.parse("td read book");
        assertEquals(1, tasks.size());
        assertEquals("1. [T][ ] read book\n", parser.parse("ls"));
        parser.parse("m 1");
        assertTrue(tasks.get(0).isDone());
        parser.parse("um 1");
        assertTrue(!tasks.get(0).isDone());
        parser.parse("del 1");
        assertEquals(0, tasks.size());
    }
}

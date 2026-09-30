package bean.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bean.exception.BeanListOutOfBoundsException;
import bean.exception.ExitCommandException;
import bean.exception.InvalidSyntaxException;
import bean.exception.UnknownCommandException;
import bean.task.BeanList;
import bean.task.Priority;

public class CommandParserTest {

    @Test
    public void getCommand_todoCommand_addsTask() {
        BeanList taskList = new BeanList();

        String response = CommandParser.getCommand("todo read textbook", taskList);

        assertEquals(1, taskList.getSize());
        assertEquals("[T]", taskList.getTaskTag(1));
        assertEquals("read textbook", taskList.getTaskName(1));
        assertEquals("Alrighty! I've added the following task:\n\nLOW [T][ ] read textbook"
                + "\n\nNow you have 1 tasks in the list.", response);
    }

    @Test
    public void getCommand_unknownCommand_throwsExceptionWithInput() {
        BeanList taskList = new BeanList();

        UnknownCommandException exception = assertThrows(UnknownCommandException.class, () ->
                CommandParser.getCommand("unknown command", taskList));

        assertEquals("unknown command", exception.getInvalidCommand());
    }

    @Test
    public void match_commandAndAlias_returnsMatchingCommand() {
        assertEquals(Commands.TODO, Commands.match("todo"));
        assertEquals(Commands.TODO, Commands.match("TD"));
        assertEquals(Commands.DEADLINE, Commands.match("dln"));
        assertEquals(Commands.FIND, Commands.match("findtask"));
        assertEquals(Commands.NONE, Commands.match("unsupported"));
    }

    @Test
    public void getCommand_findtaskAlias_returnsMatchingTasks() {
        BeanList taskList = new BeanList();
        taskList.addTodoSilent("Read textbook", Priority.LOW);
        taskList.addTodoSilent("Buy textbook", Priority.HIGH);
        taskList.addTodoSilent("Water plants", Priority.MEDIUM);

        String response = CommandParser.getCommand("findtask \"textbook\"", taskList);

        assertEquals("Here are the tasks in your list:\n\n"
                + "1. HIGH [T][ ] Buy textbook\n3. LOW [T][ ] Read textbook", response);
    }

    @Test
    public void getCommand_nonNumericTaskIndex_throwsInvalidSyntaxException() {
        BeanList taskList = new BeanList();

        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("mark first", taskList));
    }

    @Test
    public void match_allAliases_returnsExpectedCommands() {
        assertEquals(Commands.EXIT, Commands.match("Q"));
        assertEquals(Commands.LIST, Commands.match("LS"));
        assertEquals(Commands.MARK, Commands.match("mark"));
        assertEquals(Commands.UNMARK, Commands.match("unmark"));
        assertEquals(Commands.DEADLINE, Commands.match("DEADLINE"));
        assertEquals(Commands.EVENT, Commands.match("evt"));
        assertEquals(Commands.DELETE, Commands.match("del"));
        assertEquals(Commands.FIND, Commands.match("F"));
        assertEquals(Commands.NONE, Commands.match(null));
    }

    @Test
    public void getCommand_nullOrBlankInput_throwsUnknownCommandException() {
        BeanList taskList = new BeanList();

        assertThrows(UnknownCommandException.class, () -> CommandParser.getCommand(null, taskList));
        assertThrows(UnknownCommandException.class, () -> CommandParser.getCommand("   ", taskList));
    }

    @Test
    public void getCommand_listAliases_returnCurrentTasks() {
        BeanList taskList = new BeanList();
        taskList.addTodoSilent("Read", Priority.LOW);

        assertEquals(taskList.displayTasks(), CommandParser.getCommand(" list ", taskList));
        assertEquals(taskList.displayTasks(), CommandParser.getCommand("LS", taskList));
    }

    @Test
    public void getCommand_todoWithPriority_isCaseInsensitiveAndWhitespaceTolerant() {
        BeanList taskList = new BeanList();

        String response = CommandParser.getCommand(
                "  TD   prepare   presentation   /PrIoRiTy   hIgH  ", taskList);

        assertEquals(1, taskList.getSize());
        assertTrue(response.contains("HIGH [T][ ] prepare presentation"));
    }

    @Test
    public void getCommand_todoWithInvalidSyntax_throwsInvalidSyntaxException() {
        BeanList taskList = new BeanList();

        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("todo", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("todo task /priority", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("todo task /priority high extra", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("todo task /priority urgent", taskList));
        CommandParser.getCommand("todo call | bank /priority HIGH", taskList);
        assertEquals("call | bank", taskList.getTaskName(1));
    }

    @Test
    public void getCommand_deadlineWithAndWithoutPriority_addsTasks() {
        BeanList taskList = new BeanList();

        CommandParser.getCommand("deadline submit report /by 2026-08-28", taskList);
        CommandParser.getCommand("dln renew pass /by 2026-09-01 /priority MEDIUM", taskList);

        assertEquals(2, taskList.getSize());
        assertTrue(taskList.displayTasks().contains("MEDIUM [D][ ] renew pass"));
        assertTrue(taskList.displayTasks().contains("LOW [D][ ] submit report"));
    }

    @Test
    public void getCommand_deadlineWithInvalidSyntax_throwsInvalidSyntaxException() {
        BeanList taskList = new BeanList();

        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("deadline submit report", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("deadline submit /by", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("deadline /by 2026-08-28", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("deadline submit /priority high /by 2026-08-28", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("deadline submit /by 2026-08-28 /by 2026-09-01", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("deadline submit /by 2026-08-28 /priority high extra", taskList));
    }

    @Test
    public void getCommand_eventWithAndWithoutPriority_addsTasks() {
        BeanList taskList = new BeanList();

        CommandParser.getCommand("event conference /from 2026-08-28 /to 2026-08-29", taskList);
        CommandParser.getCommand("evt dinner /from 2026-09-01 /to 2026-09-02 /priority low", taskList);

        assertEquals(2, taskList.getSize());
        assertTrue(taskList.displayTasks().contains("LOW [E][ ] dinner"));
        assertTrue(taskList.displayTasks().contains("LOW [E][ ] conference"));
    }

    @Test
    public void getCommand_eventWithInvalidSyntax_throwsInvalidSyntaxException() {
        BeanList taskList = new BeanList();

        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("event conference /from 2026-08-28", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("event conference /to 2026-08-29 /from 2026-08-28", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("event /from 2026-08-28 /to 2026-08-29", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("event conference /from /to 2026-08-29", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("event conference /from 2026-08-28 /to 2026-08-29 /priority", taskList));
        assertThrows(InvalidSyntaxException.class, () -> CommandParser.getCommand(
                "event conference /from 2026-08-28 /to 2026-08-29 /priority high extra", taskList));
        assertTrue(CommandParser.getCommand(
                "event conference /from 2026-10-10 /to 2026-10-01", taskList)
                .contains("cannot end before it starts"));
        assertEquals(0, taskList.getSize());
    }

    @Test
    public void getCommand_taskActions_updateListAndRejectInvalidArguments() {
        BeanList taskList = new BeanList();
        taskList.addTodoSilent("Read", Priority.LOW);

        String markResponse = CommandParser.getCommand("mark 1", taskList);
        assertTrue(markResponse.contains("[X] Read"));
        assertTrue(CommandParser.getCommand("unmark 1", taskList).contains("[ ] Read"));
        assertThrows(BeanListOutOfBoundsException.class, () ->
                CommandParser.getCommand("delete 2", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("mark 1 extra", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("unmark first", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("mark", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("unmark", taskList));
    }

    @Test
    public void getCommand_find_validatesKeywordAndSearchesCaseInsensitively() {
        BeanList taskList = new BeanList();
        taskList.addTodoSilent("Read Textbook", Priority.LOW);

        assertTrue(CommandParser.getCommand("f \"textBOOK\"", taskList).contains("Read Textbook"));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("find", taskList));
        assertThrows(InvalidSyntaxException.class, () ->
                CommandParser.getCommand("find \"\"", taskList));
    }

    @Test
    public void getCommand_exitAliases_throwExitSignal() {
        BeanList taskList = new BeanList();

        ExitCommandException exception = assertThrows(ExitCommandException.class, () ->
                CommandParser.getCommand("q", taskList));

        assertEquals("Baiiii!", exception.getMessage());
        assertThrows(ExitCommandException.class, () -> CommandParser.getCommand("exit", taskList));
    }
}

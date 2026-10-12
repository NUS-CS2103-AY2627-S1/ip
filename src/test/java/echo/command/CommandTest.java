package echo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import echo.EchoException;
import echo.storage.Storage;
import echo.task.TaskList;
import echo.task.TaskManager;
import echo.ui.Ui;

/** Tests individual command executions and properties across all command classes. */
public class CommandTest {
    @TempDir
    private Path temporaryDirectory;

    private TaskManager taskManager;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        taskManager = new TaskManager(new TaskList(), new Storage(temporaryDirectory.resolve("tasks.txt")));
        ui = new Ui(new Scanner(""));
    }

    @Test
    public void helpCommand_executesAndReturnsHelp() {
        HelpCommand command = new HelpCommand();

        assertEquals(Command.Type.HELP, command.getType());
        assertFalse(command.isExit());

        String result = command.execute(taskManager, ui);
        assertTrue(result.contains("TACTICAL PROTOCOL DIRECTIVES"));
    }

    @Test
    public void listCommand_executesAndReturnsList() throws EchoException {
        ListCommand command = new ListCommand();

        assertEquals(Command.Type.LIST, command.getType());
        assertFalse(command.isExit());

        String emptyResult = command.execute(taskManager, ui);
        assertTrue(emptyResult.contains("Tactical radar is clear"));

        taskManager.addTodo("sample task");
        String populatedResult = command.execute(taskManager, ui);
        assertTrue(populatedResult.contains("sample task"));
    }

    @Test
    public void exitCommand_executesAndSignalsExit() {
        ExitCommand command = new ExitCommand();

        assertEquals(Command.Type.BYE, command.getType());
        assertTrue(command.isExit());

        String result = command.execute(taskManager, ui);
        assertFalse(result.isBlank());
    }

    @Test
    public void findCommand_executesAndFindsTasks() throws EchoException {
        taskManager.addTodo("read book");
        taskManager.addTodo("clean room");
        FindCommand command = new FindCommand("book");

        assertEquals(Command.Type.FIND, command.getType());
        assertEquals("book", command.getArgument(0));

        String result = command.execute(taskManager, ui);
        assertTrue(result.contains("read book"));
        assertFalse(result.contains("clean room"));
    }

    @Test
    public void todoCommand_executesAndAddsTask() throws EchoException {
        TodoCommand command = new TodoCommand("finish homework");

        assertEquals(Command.Type.TODO, command.getType());
        assertEquals("finish homework", command.getDescription());

        String result = command.execute(taskManager, ui);
        assertTrue(result.contains("[DIRECTIVE LOGGED]"));
        assertTrue(result.contains("finish homework"));
        assertEquals(1, taskManager.size());
    }

    @Test
    public void deadlineCommand_executesAndAddsTask() throws EchoException {
        DeadlineCommand command = new DeadlineCommand("submit project", "15-10-2026 23:59");

        assertEquals(Command.Type.DEADLINE, command.getType());
        assertEquals("submit project", command.getDescription());
        assertEquals("15-10-2026 23:59", command.getArgument(1));

        String result = command.execute(taskManager, ui);
        assertTrue(result.contains("[DIRECTIVE LOGGED]"));
        assertTrue(result.contains("submit project"));
        assertEquals(1, taskManager.size());
    }

    @Test
    public void eventCommand_executesAndAddsTask() throws EchoException {
        EventCommand command = new EventCommand("orientation camp", "01-10-2026", "03-10-2026");

        assertEquals(Command.Type.EVENT, command.getType());
        assertEquals("orientation camp", command.getDescription());
        assertEquals("01-10-2026", command.getArgument(1));
        assertEquals("03-10-2026", command.getArgument(2));

        String result = command.execute(taskManager, ui);
        assertTrue(result.contains("[DIRECTIVE LOGGED]"));
        assertTrue(result.contains("orientation camp"));
        assertEquals(1, taskManager.size());
    }

    @Test
    public void markAndUnmarkCommand_executeAndToggleTaskStatus() throws EchoException {
        taskManager.addTodo("read manual");
        MarkCommand markCommand = new MarkCommand(1);

        assertEquals(Command.Type.MARK, markCommand.getType());
        assertEquals(1, markCommand.getTaskNumber());

        String markResult = markCommand.execute(taskManager, ui);
        assertTrue(markResult.contains("[OBJECTIVE NEUTRALIZED]"));
        assertTrue(taskManager.getTask(1).isDone());

        UnmarkCommand unmarkCommand = new UnmarkCommand(1);
        assertEquals(Command.Type.UNMARK, unmarkCommand.getType());
        assertEquals(1, unmarkCommand.getTaskNumber());

        String unmarkResult = unmarkCommand.execute(taskManager, ui);
        assertTrue(unmarkResult.contains("[DIRECTIVE REOPENED]"));
        assertFalse(taskManager.getTask(1).isDone());
    }

    @Test
    public void deleteCommand_executesAndRemovesTask() throws EchoException {
        taskManager.addTodo("temporary task");
        DeleteCommand command = new DeleteCommand(1);

        assertEquals(Command.Type.DELETE, command.getType());
        assertEquals(1, command.getTaskNumber());

        String result = command.execute(taskManager, ui);
        assertTrue(result.contains("[PURGE COMPLETE]"));
        assertTrue(taskManager.isEmpty());
    }

    @Test
    public void commandType_enumConstants_exist() {
        assertEquals(11, Command.Type.values().length);
        assertEquals(Command.Type.HELP, Command.Type.valueOf("HELP"));
        assertEquals(Command.Type.BYE, Command.Type.valueOf("BYE"));
    }
}

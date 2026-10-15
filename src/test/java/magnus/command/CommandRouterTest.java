package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import magnus.exception.CommandNotFoundException;
import magnus.exception.CommandSyntaxException;
import magnus.exception.MagnusException;
import magnus.task.TaskList;

/**
 * Tests command dispatch and result metadata for {@link CommandRouter}.
 */
public class CommandRouterTest {
    @Test
    public void route_readOnlyCommand_returnsTypeMessageAndUnchangedFlag() throws MagnusException {
        CommandRouter router = new CommandRouter(new TaskList());

        CommandResult result = router.route("LIST");

        assertEquals(CommandType.LIST, result.commandType());
        assertEquals("\tThere are no tasks on the board yet.", result.message());
        assertFalse(result.hasTaskListChanged());
    }

    @Test
    public void route_mutatingCommand_executesCommandAndReturnsChangedFlag() throws MagnusException {
        TaskList tasks = new TaskList();
        CommandRouter router = new CommandRouter(tasks);

        CommandResult result = router.route("todo read book");

        assertEquals(CommandType.TODO, result.commandType());
        assertTrue(result.hasTaskListChanged());
        assertEquals("read book", tasks.getTask(0).getDescription());
    }

    @Test
    public void route_updateFollowUp_completesPendingUpdateAndReturnsChangedFlag() throws MagnusException {
        TaskList tasks = new TaskList();
        CommandRouter router = new CommandRouter(tasks);
        router.route("todo read book");

        CommandResult prompt = router.route("update 1");
        CommandResult update = router.route("write notes");

        assertEquals(CommandType.UPDATE, prompt.commandType());
        assertFalse(prompt.hasTaskListChanged());
        assertEquals(CommandType.UPDATE, update.commandType());
        assertTrue(update.hasTaskListChanged());
        assertEquals("write notes", tasks.getTask(0).getDescription());
    }

    @Test
    public void route_unknownCommand_throwsCommandNotFoundException() {
        CommandRouter router = new CommandRouter(new TaskList());

        CommandNotFoundException exception = assertThrows(
                CommandNotFoundException.class, () -> router.route("archive 1"));

        assertEquals("\tThat move is not in my playbook - I don't recognize the command 'archive'.",
                exception.getMessage());
    }

    @Test
    public void route_blankOrNullInput_throwsCommandSyntaxException() {
        CommandRouter router = new CommandRouter(new TaskList());

        assertThrows(CommandSyntaxException.class, () -> router.route("   "));
        assertThrows(CommandSyntaxException.class, () -> router.route(null));
    }

    @Test
    public void route_multilineInput_throwsCommandSyntaxExceptionBeforeExecution() {
        TaskList tasks = new TaskList();
        CommandRouter router = new CommandRouter(tasks);

        assertThrows(CommandSyntaxException.class, () -> router.route("todo first\ntodo second"));
        assertThrows(CommandSyntaxException.class, () -> router.route("todo first\rtodo second"));

        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void route_multilineUpdateInput_endsUpdateAndProcessesNextCommandNormally() throws MagnusException {
        for (String lineBreak : new String[] {"\n", "\r"}) {
            TaskList tasks = new TaskList();
            CommandRouter router = new CommandRouter(tasks);
            router.route("todo read book");
            router.route("update 1");

            String invalidUpdate = "write notes" + lineBreak + "list";
            assertThrows(CommandSyntaxException.class, () -> router.route(invalidUpdate));
            CommandResult result = router.route("list");

            assertEquals(CommandType.LIST, result.commandType());
            assertFalse(result.hasTaskListChanged());
            assertEquals("read book", tasks.getTask(0).getDescription());
        }
    }
}

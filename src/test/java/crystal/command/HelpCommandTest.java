package crystal.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import crystal.task.TaskList;

/**
 * Tests command-guide output and continued-session behavior.
 */
public class HelpCommandTest extends CommandTestBase {
    /** Verifies that help displays every command and does not end the session. */
    @Test
    public void execute_helpCommand_displaysCommandGuideAndDoesNotExit() {
        HelpCommand command = new HelpCommand();

        command.execute(new TaskList(), createUi(), createStorage());

        assertFalse(command.isExit());
        assertEquals("[Crystal's command guide:\n"
                + "- To add a todo, enter 'todo [description]'\n"
                + "- To add a deadline, enter 'deadline [description] /by [deadline]'\n"
                + "- To add an event, enter 'event [description] /from [start] /to [end]'\n"
                + "- To add a within-period task, enter "
                + "'within [description] /from [start] /to [end]'\n"
                + "- To view your task list, enter 'list'\n"
                + "- To view dated tasks on a date, enter 'list /on [date]'\n"
                + "- To find tasks by description, enter 'find [keyword]'\n"
                + "- To mark a task as done, enter 'mark [task number]'\n"
                + "- To mark a task as not done, enter 'unmark [task number]'\n"
                + "- To delete a task, enter 'delete [task number]'\n"
                + "- To view this command guide, enter 'help'\n"
                + "- To exit, enter 'bye']\n", getOutput());
    }
}

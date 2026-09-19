package nyonbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import nyonbot.Logic.Result;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

class DeleteCommandTest {
    @Test
    void execute_taskName_deletesTaskAndRequestsSave() throws NyonException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));
        HashMap<String, String> arguments = new HashMap<>();
        arguments.put(Command.DESCRIPTION_KEY, "read book");
        DeleteCommand command = new DeleteCommand(arguments, tasks);

        Result result = command.execute();

        assertEquals(0, tasks.size());
        assertTrue(result.shouldWrite());
    }
}

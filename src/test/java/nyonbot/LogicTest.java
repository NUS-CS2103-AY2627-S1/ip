package nyonbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nyonbot.Logic.Result;
import nyonbot.command.EchoCommand;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

class LogicTest {
    private final Logic logic = Logic.getInstance();

    @BeforeEach
    void clearTaskList() {
        logic.getList().clear();
    }

    @Test
    void getInstance_calledTwice_returnsSameInstance() {
        assertSame(logic, Logic.getInstance());
    }

    @Test
    void loadList_nonNullList_replacesExistingTasks() {
        logic.getList().add(new Task("old task"));
        TaskList replacement = new TaskList();
        replacement.add(new Task("new task"));

        logic.loadList(replacement);

        assertEquals(1, logic.getList().size());
        assertEquals("new task", logic.getList().get(0).getName());
    }

    @Test
    void loadList_null_clearsExistingTasks() {
        logic.getList().add(new Task("old task"));

        logic.loadList(null);

        assertEquals(0, logic.getList().size());
    }

    @Test
    void execute_command_returnsCommandResult() throws Exception {
        Result result = logic.execute(new EchoCommand(
                Parser.getInstance().parseArguments("echo hello")));

        assertEquals("hello", result.out());
        assertFalse(result.shouldExit());
        assertFalse(result.shouldWrite());
    }
}

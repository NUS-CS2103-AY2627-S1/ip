package arthur.state;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import arthur.tasks.Task;
import arthur.tasks.TodoTask;

// AI-assisted: Tests restoration of missing deserialized command-context fields.
public class CommandContextTest {
    @Test
    public void fixed_missingTasks_createsEmptyTaskListAndPreservesAliases() {
        HashMap<String, String> aliases = new HashMap<>();
        aliases.put("t", "todo");

        CommandContext fixedContext = new CommandContext(null, aliases).fixed();

        assertNotNull(fixedContext.tasks());
        assertSame(aliases, fixedContext.commandAliases());
    }

    @Test
    public void fixed_missingAliases_createsEmptyAliasMapAndPreservesTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new TodoTask("read book"));

        CommandContext fixedContext = new CommandContext(tasks, null).fixed();

        assertSame(tasks, fixedContext.tasks());
        assertNotNull(fixedContext.commandAliases());
    }

    @Test
    public void fixed_missingFields_createsEmptyCollections() {
        CommandContext fixedContext = new CommandContext(null, null).fixed();

        assertNotNull(fixedContext.tasks());
        assertNotNull(fixedContext.commandAliases());
    }
}

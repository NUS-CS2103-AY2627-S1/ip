package anders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import anders.storage.Storage;

/** Tests themed responses through parsing, command execution, and isolated persistence. */
public class AndersTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_taskLifecycle_preservesSavedStateAndUsesLanternVoice() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Anders bot = new Anders(storage);
        assertEquals("A clear path! No tasks yet. Try: todo read chapter 1", bot.getResponse("list"));
        assertTrue(bot.getResponse("todo read book /tags #school").contains("Your trail holds 1 task."));
        assertTrue(bot.getResponse("mark 1").contains("One more light along the path. Task marked as done:"));

        Anders reopened = new Anders(storage);
        assertTrue(reopened.getResponse("list").contains("1.[T][X] read book (tags: #school)"));
        assertTrue(reopened.getResponse("unmark 1").contains("Back on the trail. Task marked as not done:"));
        assertTrue(reopened.getResponse("delete 1").contains("Your trail holds 0 tasks."));
        assertTrue(storage.load().isEmpty());
        assertEquals("Rest well, wanderer. I'll keep the lantern lit.", reopened.getResponse("bye"));
    }

    @Test
    public void getResponse_invalidCommand_preservesStateAndRestoresConsoleOutput() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Anders bot = new Anders(storage);
        PrintStream originalOutput = System.out;
        bot.getResponse("todo keep this task");

        assertTrue(bot.getResponse("todo").startsWith("A little fog on the path. The description of a todo"));
        assertSame(originalOutput, System.out);
        assertEquals(1, storage.load().size());
        assertTrue(bot.getResponse("list").contains("1.[T][ ] keep this task"));
        assertSame(originalOutput, System.out);
    }

    @Test
    public void getResponse_commonMistakes_preservesTasksAndAcceptsNextCommand() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Anders bot = new Anders(storage);
        bot.getResponse("todo keep this task");
        for (String command : List.of("unknown", "todo", "deadline report /by 31/2/2026",
                "event meeting /from 2026-10-02 1600 /to 2026-10-02 1400", "mark abc", "delete 0",
                "unmark 999999999999999999999", "tag 1 invalid")) {
            assertTrue(bot.getResponse(command).startsWith("A little fog on the path."), command);
            assertEquals(1, storage.load().size(), command);
            assertTrue(bot.getResponse("list").contains("1.[T][ ] keep this task"), command);
        }
        assertTrue(bot.getResponse("todo next task").contains("Your trail holds 2 tasks."));
    }

    @Test
    public void getResponse_saveFailure_explainsUnsavedStateAndAllowsRecovery() throws Exception {
        Path parent = temporaryDirectory.resolve("data");
        Storage storage = new Storage(parent.resolve("tasks.txt").toString());
        Anders bot = new Anders(storage);
        Files.writeString(parent, "blocking file");
        for (String command : List.of("todo keep this task", "mark 1", "unmark 1", "tag 1 #school",
                "untag 1 #school", "delete 1", "todo keep this task")) {
            assertTrue(bot.getResponse(command).contains("Changes are only available in this session"), command);
        }
        assertTrue(bot.getResponse("list").contains("1.[T][ ] keep this task"));

        Files.delete(parent);
        assertTrue(bot.getResponse("todo another task").contains("Your trail holds 2 tasks."));
        assertEquals(2, new Storage(parent.resolve("tasks.txt").toString()).load().size());
    }

}

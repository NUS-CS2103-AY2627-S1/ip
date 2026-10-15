package beemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BeemoTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void getCommandResult_exitCommand_exitRequested() {
        Beemo beemo = new Beemo(tempDirectory.resolve("data/tasks.txt"));

        CommandResult result = beemo.getCommandResult("bye");

        assertTrue(result.isExit());
    }

    @Test
    void getCommandResult_nonExitCommand_exitNotRequested() {
        Beemo beemo = new Beemo(tempDirectory.resolve("data/tasks.txt"));

        CommandResult result = beemo.getCommandResult("list");

        assertFalse(result.isExit());
    }

    @Test
    void getCommandResult_listWithNoTasks_emptyListMessage() {
        Beemo beemo = new Beemo(tempDirectory.resolve("data/tasks.txt"));

        CommandResult result = beemo.getCommandResult("list");

        assertEquals("Your task list is empty. Add a task whenever you're ready!",
                result.response());
    }

    @Test
    void reload_tasksContainingSeparator_tasksPreserved() {
        Path filePath = tempDirectory.resolve("data/tasks.txt");
        Beemo firstSession = new Beemo(filePath);
        firstSession.getResponse("todo keep me");
        firstSession.getResponse("todo buy milk | eggs");

        Beemo secondSession = new Beemo(filePath);

        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                        + "1.[T][ ] keep me" + System.lineSeparator()
                        + "2.[T][ ] buy milk | eggs",
                secondSession.getResponse("list"));
    }
}

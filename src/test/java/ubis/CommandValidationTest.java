package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests that rejected commands preserve tasks and never attempt to save them.
 */
public class CommandValidationTest {
    @TempDir
    private Path temporaryDirectory;

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "help extra", "todo", "deadline report /by bad",
        "event trip /from 2026-10-03 /to 2026-10-01", "find", "mark", "unmark text",
        "delete 99999999999999", "mark 0", "delete 8", "unmark 8", "todo {bad}"})
    public void getResponse_invalidCommand_preservesTasksWithoutSaving(String command) {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo().initialise("read book"));
        Ubis ubis = new Ubis(tasks, temporaryDirectory.resolve("tasks.txt")) {
            @Override
            boolean saveTasks() {
                throw new AssertionError("Rejected commands must not save");
            }
        };
        String response = ubis.getResponse(command);
        assertFalse(response.isBlank());
        assertEquals("1: [T][ ] read book", ubis.getResponse("list"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"mark", "unmark", "delete"})
    public void getResponse_missingNumber_preservesCommandSpecificGuidance(String command) {
        Ubis ubis = new Ubis(temporaryDirectory.resolve("tasks.txt"));
        assertEquals("Please add the task number you want to " + command + "!\n"
                + "Example: \"" + command + " 4\" if you want to " + command + " the fourth task.",
                ubis.getResponse(command));
    }
}

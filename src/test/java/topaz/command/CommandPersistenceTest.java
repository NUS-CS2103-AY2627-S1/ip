package topaz.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import topaz.TopazException;
import topaz.storage.Storage;
import topaz.task.Task;
import topaz.task.TaskList;
import topaz.task.Todo;
import topaz.ui.Ui;

/** Tests rollback of every task mutation when persistence fails. */
class CommandPersistenceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_saveFailure_restoresOriginalTasksAndStatus() {
        Storage failingStorage = new Storage(temporaryDirectory.resolve("tasks.txt")) {
            @Override
            public void save(List<Task> tasks) throws TopazException {
                throw new TopazException("Unable to save your tasks.");
            }
        };
        Task completed = new Todo("first");
        completed.markAsDone();
        TaskList tasks = new TaskList(List.of(completed, new Todo("second")));
        List<String> original = tasks.asList().stream().map(Task::toFileString).toList();
        Command[] commands = {new AddCommand(new Todo("third")), new DeleteCommand(0),
                new MarkCommand(1), new UnmarkCommand(0), new MarkCommand(0), new UnmarkCommand(1)};
        for (Command command : commands) {
            StringBuilder response = new StringBuilder();
            assertThrows(TopazException.class, () -> command.execute(tasks, new Ui(response), failingStorage));
            assertEquals(original, tasks.asList().stream().map(Task::toFileString).toList());
            assertEquals("", response.toString());
        }
    }
}

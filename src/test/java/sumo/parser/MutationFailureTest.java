package sumo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sumo.command.Command;
import sumo.storage.Storage;
import sumo.task.Task;
import sumo.task.TaskList;
import sumo.task.Todo;
import sumo.ui.Ui;

/** Verifies that storage failures never leave a mutation half-applied or announce success. */
class MutationFailureTest {
    @Test
    void execute_accessDenied_allMutationsRestoreOriginalState() throws Exception {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        second.markAsDone();
        TaskList tasks = new TaskList(List.of(first, second));
        List<String> original = tasks.getTasks().stream().map(Task::toDataString).toList();
        List<String> responses = new ArrayList<>();
        Storage storage = new DeniedStorage();

        for (String input : List.of("todo third", "mark 1", "unmark 2", "delete 1", "delete 2")) {
            Command command = new Parser().parse(input, tasks.size());
            assertThrows(AccessDeniedException.class, () -> command.execute(tasks, new Ui(responses::add), storage));
            assertEquals(original, tasks.getTasks().stream().map(Task::toDataString).toList());
            assertTrue(responses.isEmpty());
        }
    }

    /** Simulates denied writes independently of the operating system and test user's permissions. */
    private static class DeniedStorage extends Storage {
        private DeniedStorage() {
            super(Path.of("unused-test-file"));
        }

        @Override
        public void save(List<Task> tasks) throws IOException {
            throw new AccessDeniedException("The data file is read-only.");
        }
    }
}

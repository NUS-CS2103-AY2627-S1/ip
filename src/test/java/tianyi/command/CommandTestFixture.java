package tianyi.command;

import java.util.List;

import tianyi.storage.Storage;
import tianyi.task.Task;

/**
 * Provides recording test doubles shared by command unit tests.
 */
final class CommandTestFixture {
    private CommandTestFixture() {
    }

    /**
     * Records the task snapshot passed to save without writing a file.
     */
    static class RecordingStorage extends Storage {
        protected List<Task> savedTasks;

        RecordingStorage() {
            super("unused");
        }

        @Override
        public void save(List<Task> tasks) {
            savedTasks = List.copyOf(tasks);
        }
    }
}

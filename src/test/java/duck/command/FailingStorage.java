package duck.command;

import java.util.List;

import duck.DuckException;
import duck.storage.Storage;
import duck.task.Task;

/**
 * Test storage that consistently simulates a persistence failure.
 */
final class FailingStorage extends Storage {
    FailingStorage() {
        super("unused-test-path");
    }

    @Override
    public void save(List<Task> tasks) throws DuckException {
        throw new DuckException("Simulated save failure.");
    }
}

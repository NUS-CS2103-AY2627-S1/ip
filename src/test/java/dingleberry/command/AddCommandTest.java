package dingleberry.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dingleberry.exception.DingleberryException;
import dingleberry.model.Deadlines;
import dingleberry.model.Events;
import dingleberry.model.Task;
import dingleberry.model.TaskList;
import dingleberry.model.Todo;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Tests adding each supported task type. */
class AddCommandTest {
    /** Base year used for the test dates. */
    private static final int TEST_YEAR = 2026;
    /** Base month used for the test dates. */
    private static final int TEST_MONTH = 8;
    /** Base day used for the test dates. */
    private static final int TEST_DAY = 25;
    /** Deadline hour used in the test fixture. */
    private static final int DEADLINE_HOUR = 18;
    /** Event start hour used in the test fixture. */
    private static final int EVENT_START_HOUR = 14;
    /** Event end hour used in the test fixture. */
    private static final int EVENT_END_HOUR = 15;

    /** Redirects storage writes to a temporary folder during tests. */
    @TempDir
    private Path tempDir;

    @Test
    void executeTodoAddsAndPersistsTask() throws Exception {
        assertAddedTaskIsPersisted(new Todo("read lecture notes"));
    }

    @Test
    void executeDeadlineAddsAndPersistsTask() throws Exception {
        assertAddedTaskIsPersisted(new Deadlines("submit report",
                LocalDateTime.of(TEST_YEAR, TEST_MONTH,
                        TEST_DAY, DEADLINE_HOUR, 0)));
    }

    @Test
    void executeEventAddsAndPersistsTask() throws Exception {
        assertAddedTaskIsPersisted(new Events("team meeting",
                LocalDateTime.of(TEST_YEAR, TEST_MONTH,
                        TEST_DAY, EVENT_START_HOUR, 0),
                LocalDateTime.of(TEST_YEAR, TEST_MONTH,
                        TEST_DAY, EVENT_END_HOUR, 0)));
    }

        @Test
        void executeDuplicateTaskRejectsSecondTask() throws Exception {
        TaskList tasks = new TaskList(new Todo("read lecture notes"));
        Storage storage = new Storage(tempDir.resolve("dingleberry.txt")
                .toString());

        assertThrows(DingleberryException.class,
                () -> new AddCommand(new Todo("read lecture notes"))
                        .execute(tasks, new Ui(), storage));

        assertEquals(1, tasks.size());
    }

    @Test
    void executeUnexpectedSaveFailureReportsErrorInsteadOfThrowing()
            throws Exception {
        Task failingTask = new Task("broken") {
            @Override
            public String toSaveFormat() {
                throw new IllegalStateException("serialization failed");
            }
        };
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("dingleberry.txt")
                .toString());

        assertDoesNotThrow(() -> new AddCommand(failingTask)
                .execute(tasks, new Ui(), storage));
        assertEquals(1, tasks.size());
    }

    private void assertAddedTaskIsPersisted(final Task task) throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("dingleberry.txt")
                .toString());

        new AddCommand(task).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals(task.getDescription(), tasks.get(0).getDescription());
        TaskList loadedTasks = new TaskList(storage.load());
        assertEquals(1, loadedTasks.size());
        assertInstanceOf(task.getClass(), loadedTasks.get(0));
    }
}

package discitrack.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import discitrack.exception.SaveException;
import discitrack.parser.Parser;
import discitrack.storage.Storage;
import discitrack.task.Task;
import discitrack.task.TaskList;
import discitrack.task.Todo;
import discitrack.ui.Ui;

/**
 * Verifies that a rejected save can be retried once without losing or duplicating changes.
 */
public class SaveRecoveryTest {
    @TempDir
    public Path directory;

    @TestFactory
    public Stream<DynamicTest> failedSave_retry_persistsExactlyOneChange() {
        return Stream.of("todo new", "todo new /tag home", "deadline new /by 2026-09-30",
                "event new /from 2026-09-30 /to 2026-10-01", "mark 1", "unmark 2",
                "delete 1", "delete 2", "tag 1 urgent", "untag 1 school")
                .map(input -> DynamicTest.dynamicTest("retry " + input, () -> {
                    Path file = Files.createTempDirectory(directory, "retry-").resolve("tasks.txt");
                    RetryStorage storage = new RetryStorage(file);
                    TaskList tasks = initialTasks();
                    storage.save(tasks.asList());
                    String originalFile = Files.readString(file);
                    List<String> originalTasks = descriptions(tasks.asList());
                    storage.failure = new IOException("Simulated replacement failure");
                    Command command = Parser.parse(input);
                    Ui ui = new Ui(false);

                    assertThrows(SaveException.class, () -> command.execute(tasks, ui, storage));
                    assertEquals(originalTasks, descriptions(tasks.asList()));
                    assertEquals(originalFile, Files.readString(file));
                    assertEquals("", ui.getLastResponse());

                    storage.failure = null;
                    command.execute(tasks, ui, storage);
                    verifyChange(input, tasks);
                    assertFalse(ui.getLastResponse().isBlank());
                    assertEquals(2, storage.successfulReplacements);
                    assertEquals(descriptions(tasks.asList()), descriptions(new Storage(file.toString()).load()));
                    try (Stream<Path> files = Files.list(file.getParent())) {
                        assertEquals(List.of(file), files.toList());
                    }
                }));
    }

    @TestFactory
    public Stream<DynamicTest> saveFailureTypes_showSpecificAdvice_andRestoreTask() {
        return Stream.of("permission", "atomic", "generic").map(kind ->
                DynamicTest.dynamicTest("save error: " + kind, () -> {
                    Path file = Files.createTempDirectory(directory, "advice-").resolve("tasks.txt");
                    RetryStorage storage = new RetryStorage(file);
                    TaskList tasks = initialTasks();
                    storage.save(tasks.asList());
                    String original = Files.readString(file);
                    storage.failure = switch (kind) {
                        case "permission" -> new AccessDeniedException(file.toString());
                        case "atomic" -> new AtomicMoveNotSupportedException("temporary", file.toString(), "test");
                        default -> new IOException("test");
                    };
                    String advice = switch (kind) {
                        case "permission" -> "Access was denied";
                        case "atomic" -> "supporting atomic replacement";
                        default -> "free space";
                    };
                    Command command = Parser.parse("mark 1");
                    SaveException error = assertThrows(SaveException.class, () ->
                            command.execute(tasks, new Ui(false), storage));
                    assertTrue(error.getMessage().contains(advice));
                    assertTrue(error.getMessage().contains("No changes were made"));
                    assertSame(storage.failure, error.getCause());
                    assertFalse(tasks.get(1).isDone());
                    assertEquals(original, Files.readString(file));
                }));
    }

    private TaskList initialTasks() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        first.addTag("school");
        Task second = new Todo("second");
        second.markAsDone();
        tasks.add(first);
        tasks.add(second);
        return tasks;
    }

    private List<String> descriptions(List<Task> tasks) {
        return tasks.stream().map(Task::toString).toList();
    }

    private void verifyChange(String input, TaskList tasks) throws Exception {
        String command = input.split(" ", 2)[0];
        switch (command) {
            case "todo", "deadline", "event":
                assertEquals(3, tasks.size());
                assertEquals("new", tasks.get(3).getActivity());
                assertEquals(input.contains("/tag") ? List.of("home") : List.of(), tasks.get(3).getTags());
                break;
            case "delete":
                assertEquals(1, tasks.size());
                assertEquals(input.endsWith("1") ? "second" : "first", tasks.get(1).getActivity());
                break;
            case "mark":
                assertTrue(tasks.get(1).isDone());
                assertTrue(tasks.get(2).isDone());
                break;
            case "unmark":
                assertFalse(tasks.get(1).isDone());
                assertFalse(tasks.get(2).isDone());
                break;
            case "tag":
                assertEquals(List.of("school", "urgent"), tasks.get(1).getTags());
                break;
            case "untag":
                assertEquals(List.of(), tasks.get(1).getTags());
                break;
            default:
                throw new AssertionError("Unexpected test command: " + command);
        }
    }

    private static class RetryStorage extends Storage {
        private IOException failure;
        private int successfulReplacements;

        RetryStorage(Path path) {
            super(path.toString());
        }

        @Override
        protected void replaceFile(Path temporary, Path target) throws IOException {
            if (failure != null) {
                throw failure;
            }
            super.replaceFile(temporary, target);
            successfulReplacements++;
        }
    }
}

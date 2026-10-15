package gongrilla.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gongrilla.storage.Storage;
import gongrilla.task.Deadline;
import gongrilla.task.Event;
import gongrilla.task.Task;
import gongrilla.task.TaskList;
import gongrilla.task.Todo;
import gongrilla.ui.Ui;

/** Tests that add confirmations agree with the task being added and saved. */
class AddCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_accessDenied_propagatesErrorWithoutChangingTasks() {
        TaskList tasks = new TaskList();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Storage deniedStorage = new Storage(temporaryDirectory.resolve("denied.txt")) {
            @Override
            public void appendAdd(Task task) throws IOException {
                throw new AccessDeniedException("denied.txt");
            }
        };
        assertThrows(AccessDeniedException.class, () -> new AddCommand(new Todo("new")).execute(tasks,
                new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output)), deniedStorage));
        assertEquals(0, tasks.size());
        assertEquals("", output.toString());
    }

    @Test
    void execute_saveFailure_preservesTasksAndSuppressesSuccessReply() throws Exception {
        Path parent = temporaryDirectory.resolve("blocked");
        Files.writeString(parent, "keep");
        Task original = new Todo("existing");
        TaskList tasks = new TaskList(original);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

        assertThrows(IOException.class, () -> new AddCommand(new Todo("new")).execute(
                tasks, ui, new Storage(parent.resolve("tasks.txt"))));

        assertEquals(List.of(original), tasks.asList());
        assertEquals("", output.toString());
        assertEquals("keep", Files.readString(parent));
    }

    @Test
    void execute_supportedTasks_derivesLabelsFromTasks() throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 9, 6, 12, 0);
        List<Task> examples = List.of(new Task("basic"), new Todo("read"),
                new Deadline("report", date), new Event("meeting", date, date.plusHours(1)));
        List<String> labels = List.of("todo", "todo", "deadline", "event");
        TaskList tasks = new TaskList();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

        for (int i = 0; i < examples.size(); i++) {
            output.reset();
            Task task = examples.get(i);

            new AddCommand(task).execute(tasks, ui, storage);

            assertSame(task, tasks.get(i));
            assertEquals(i + 1, tasks.size());
            assertTrue(output.toString().startsWith("higa higa click click. New " + labels.get(i) + ":"));
            Task restored = storage.load().get(i);
            assertEquals(task.toDataString(), restored.toDataString());
            assertEquals(task.getType(), restored.getType());
        }
    }
}

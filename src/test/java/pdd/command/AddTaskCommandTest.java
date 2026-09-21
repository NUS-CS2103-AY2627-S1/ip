package pdd.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pdd.storage.Storage;
import pdd.task.Deadline;
import pdd.task.Event;
import pdd.task.Task;
import pdd.task.TaskList;
import pdd.task.Todo;
import pdd.ui.Ui;

public class AddTaskCommandTest {
    @TempDir
    Path tempDir;

    private List<String> lines;
    private Ui ui;
    private TaskList tasks;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        lines = new ArrayList<>();
        ui = new Ui(lines::add);
        tasks = new TaskList(new ArrayList<>());
        storage = new Storage(tempDir.resolve("pdd.txt").toString());
    }

    @Test
    public void execute_todoCommand_addsShowsAndSavesTodo() {
        Todo todo = new Todo("read book");
        new TodoCommand(todo).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
        assertEquals("Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
                String.join("\n", lines));
        assertEquals("[T][ ] read book", storage.load().get(0).toString());
    }

    @Test
    public void execute_deadlineCommand_addsShowsAndSavesDeadline() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 1));
        new DeadlineCommand(deadline).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        Task saved = storage.load().get(0);
        assertEquals("[D][ ] return book (by: Dec 01 2019)", saved.toString());
        assertEquals(
                "Got it. I've added this task:\n  [D][ ] return book (by: Dec 01 2019)\n"
                        + "Now you have 1 tasks in the list.",
                String.join("\n", lines));
    }

    @Test
    public void execute_eventCommand_addsShowsAndSavesEvent() {
        Event event = new Event("project meeting", LocalDate.of(2019, 12, 2), "4pm");
        new EventCommand(event).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        Task saved = storage.load().get(0);
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: 4pm)", saved.toString());
        assertEquals(
                "Got it. I've added this task:\n  [E][ ] project meeting (from: Dec 02 2019 to: 4pm)\n"
                        + "Now you have 1 tasks in the list.",
                String.join("\n", lines));
    }
}

package ev.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ev.EvException;
import ev.storage.Storage;
import ev.task.Deadline;
import ev.task.Event;
import ev.task.TaskList;
import ev.task.Todo;
import ev.ui.Ui;

public class OnCommandTest {

    @TempDir
    Path workFolder;

    private TaskList threeTasks() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.add(new Event("camp",
                LocalDateTime.of(2019, 12, 1, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0)));
        return tasks;
    }

    @Test
    public void execute_dateWithADeadlineAndAnEvent_todoLeftOut() throws EvException {
        Ui ui = new Ui();
        new OnCommand(LocalDate.of(2019, 12, 2))
                .execute(threeTasks(), ui, new Storage(workFolder.resolve("ev.txt")));
        assertEquals("On Dec 2 2019:"
                + "\n2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)"
                + "\n3.[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)",
                ui.takeResponse());
    }

    @Test
    public void execute_dayTheEventEnds_stillCounted() throws EvException {
        Ui ui = new Ui();
        new OnCommand(LocalDate.of(2019, 12, 3))
                .execute(threeTasks(), ui, new Storage(workFolder.resolve("ev.txt")));
        assertEquals("On Dec 3 2019:"
                + "\n3.[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)",
                ui.takeResponse());
    }

    @Test
    public void execute_dateWithNothingOnIt_saysSo() throws EvException {
        Ui ui = new Ui();
        new OnCommand(LocalDate.of(2020, 1, 1))
                .execute(threeTasks(), ui, new Storage(workFolder.resolve("ev.txt")));
        assertEquals("Nothing on Jan 1 2020.", ui.takeResponse());
    }
}

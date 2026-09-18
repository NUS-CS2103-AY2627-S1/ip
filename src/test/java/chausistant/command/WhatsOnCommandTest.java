package chausistant.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import chausistant.storage.Storage;
import chausistant.task.DeadlineTask;
import chausistant.task.EventTask;
import chausistant.task.TaskList;
import chausistant.task.TodoTask;
import chausistant.ui.Ui;

/** Tests the schedule shown for one selected date. */
class WhatsOnCommandTest {

    @Test
    void execute_relevantTasks_ordersEventsAndDeadlinesChronologically() {
        LocalDate date = LocalDate.of(2019, 12, 2);
        TaskList tasks = new TaskList(
                new TodoTask("read book"),
                new EventTask("afternoon meeting", LocalDateTime.of(2019, 12, 2, 14, 0), true,
                        LocalDateTime.of(2019, 12, 2, 16, 0), true),
                new DeadlineTask("collect book", LocalDateTime.of(2019, 12, 2, 18, 0), true),
                new EventTask("overnight event", LocalDateTime.of(2019, 12, 1, 0, 0), false,
                        LocalDateTime.of(2019, 12, 3, 23, 59), false),
                new DeadlineTask("submit report", LocalDateTime.of(2019, 12, 2, 9, 0), true),
                new DeadlineTask("outside deadline", LocalDateTime.of(2019, 12, 3, 9, 0), true));
        StringBuilder response = new StringBuilder();

        new WhatsOnCommand(date).execute(tasks, new Ui(response), new Storage(Path.of("unused.txt")));

        assertEquals(String.join(System.lineSeparator(),
                "here's your day at a glance for Dec 2 2019:",
                "events:",
                "[E][ ] overnight event (from: Dec 1 2019 to: Dec 3 2019)",
                "[E][ ] afternoon meeting (from: Dec 2 2019 1400 to: Dec 2 2019 1600)",
                "--------------------",
                "deadlines:",
                "[D][ ] submit report (by: Dec 2 2019 0900)",
                "[D][ ] collect book (by: Dec 2 2019 1800)") + System.lineSeparator(),
                response.toString());
    }
}

package auto.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import auto.task.Deadline;
import auto.task.Event;
import auto.task.Task;
import auto.task.TaskList;
import auto.task.ToDo;

class OccurCommandTest extends CommandTestSupport {
    @TempDir
    Path tempDirectory;

    @Test
    void execute_mixedTasks_displaysOnlyTasksOccurringOnDateWithoutMutation() {
        LocalDate date = LocalDate.of(2026, 8, 22);
        List<Task> originalTasks = List.of(
                new ToDo("prepare notes"),
                new Deadline("submit report", date),
                new Event("conference", date.minusDays(1), date.plusDays(1)),
                new Deadline("return book", date.plusDays(1)));
        TaskList tasks = new TaskList(originalTasks);

        CommandResult result = new OccurCommand(date).execute(tasks, failingStorage(tempDirectory));

        assertEquals(originalTasks, tasks.asList());
        assertTrue(result.message().contains("2. [D][ ] submit report"));
        assertTrue(result.message().contains("3. [E][ ] conference"));
        assertFalse(result.message().contains("1. [T][ ] prepare notes"));
        assertFalse(result.message().contains("4. [D][ ] return book"));
    }
}

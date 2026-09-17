package glennon.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import glennon.Storage;
import glennon.Ui;
import glennon.exception.GlennonException;
import glennon.task.Deadline;
import glennon.task.Task;
import glennon.task.TaskList;
import glennon.task.Todo;

/**
 * Tests chronological sorting through the complete command boundary.
 */
class SortCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_unsortedMissions_displaysAndPersistsSortedOrder()
            throws GlennonException {
        Deadline laterDeadline = new Deadline(
                "later", LocalDateTime.of(2026, 9, 3, 18, 0));
        Deadline earlierDeadline = new Deadline(
                "earlier", LocalDateTime.of(2026, 9, 1, 9, 0));
        Todo todo = new Todo("unscheduled");
        TaskList missions = new TaskList(List.of(laterDeadline, todo, earlierDeadline));
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Storage storage = new Storage(dataPath);
        StringWriter output = new StringWriter();
        Ui ui = new Ui(new PrintWriter(output, true));

        new SortCommand().execute(missions, ui, storage);

        assertEquals(List.of(earlierDeadline, laterDeadline, todo), missions.asList());
        assertEquals("Mission log sorted chronologically:\n"
                        + "1. [D][ ] earlier (by: Sep 1 2026, 9:00 AM)\n"
                        + "2. [D][ ] later (by: Sep 3 2026, 6:00 PM)\n"
                        + "3. [T][ ] unscheduled\n",
                output.toString().replace("\r\n", "\n"));
        List<Task> reloadedMissions = storage.loadMissions();
        assertEquals("earlier", assertInstanceOf(
                Deadline.class, reloadedMissions.get(0)).getDescription());
        assertEquals("later", assertInstanceOf(
                Deadline.class, reloadedMissions.get(1)).getDescription());
        assertEquals("unscheduled", assertInstanceOf(
                Todo.class, reloadedMissions.get(2)).getDescription());
    }
}

package samantha.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import samantha.exception.TaskValidationException;
import samantha.model.Event;
import samantha.model.TaskList;
import samantha.storage.Storage;
import samantha.ui.Ui;

class AddEventCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validEvent_addsAndPersistsTask() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList tasks = new TaskList();

        new AddEventCommand("meeting", "2/12/2019 1400", "2/12/2019 1600")
                .execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertInstanceOf(Event.class, tasks.getTask(1));
        assertEquals("E | 0 | meeting | 2/12/2019 1400 to 2/12/2019 1600",
                storage.load().getFirst().toFileString());
    }

    @Test
    void execute_missingEventTime_taskValidationExceptionAndNoTaskAdded() {
        TaskList tasks = new TaskList();

        assertThrows(TaskValidationException.class, () -> new AddEventCommand("meeting", "2/12/2019", "")
                .execute(tasks, new Ui(), new Storage(temporaryDirectory.resolve("tasks.txt"))));
        assertEquals(0, tasks.size());
    }
}

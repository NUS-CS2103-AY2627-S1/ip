package samantha.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import samantha.exception.InputException;
import samantha.model.Deadline;
import samantha.model.TaskList;
import samantha.storage.Storage;
import samantha.ui.Ui;

class AddDeadlineCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validDeadline_addsAndPersistsTask() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList tasks = new TaskList();

        new AddDeadlineCommand("return book", "2/12/2019 1800").execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertInstanceOf(Deadline.class, tasks.getTask(1));
        assertEquals("D | 0 | return book | 2/12/2019 1800", storage.load().getFirst().toFileString());
    }

    @Test
    void execute_invalidDeadline_inputExceptionAndNoTaskAdded() {
        TaskList tasks = new TaskList();

        assertThrows(InputException.class, () -> new AddDeadlineCommand("return book", "tomorrow")
                .execute(tasks, new Ui(), new Storage(temporaryDirectory.resolve("tasks.txt"))));
        assertEquals(0, tasks.size());
    }
}

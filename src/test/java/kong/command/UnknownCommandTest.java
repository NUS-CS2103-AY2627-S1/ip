package kong.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.TaskList;
import kong.ui.Ui;

class UnknownCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_throwsKongExceptionWithStandardMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui(message -> { });
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        UnknownCommand command = new UnknownCommand();

        KongException exception = assertThrows(KongException.class, () ->
                command.execute(tasks, ui, storage));

        assertEquals("Confound it! I do not recognise that command in my lexicon.", exception.getMessage());
    }
}

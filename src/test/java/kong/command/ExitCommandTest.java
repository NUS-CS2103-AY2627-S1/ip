package kong.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kong.storage.Storage;
import kong.task.TaskList;
import kong.ui.Ui;

class ExitCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_displaysGoodbyeMessageViaUi() {
        TaskList tasks = new TaskList();
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ExitCommand command = new ExitCommand();

        command.execute(tasks, ui, storage);

        assertTrue(output.contains("Cheerio! Until our next scholarly consultation."));
    }

    @Test
    void isExit_returnsTrue() {
        ExitCommand command = new ExitCommand();

        assertTrue(command.isExit());
    }
}

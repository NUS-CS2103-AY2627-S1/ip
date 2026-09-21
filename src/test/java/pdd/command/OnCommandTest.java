package pdd.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pdd.storage.Storage;
import pdd.task.Deadline;
import pdd.task.TaskList;
import pdd.task.Todo;
import pdd.ui.Ui;

public class OnCommandTest {
    @TempDir
    Path tempDir;

    @Test
    public void execute_dateMatchesSomeTasks_showsOnlyMatchesExcludingUndatedTasks() {
        List<String> lines = new ArrayList<>();
        Ui ui = new Ui(lines::add);
        Deadline matching = new Deadline("return book", LocalDate.of(2019, 12, 1));
        Deadline notMatching = new Deadline("pay bills", LocalDate.of(2019, 12, 2));
        TaskList tasks = new TaskList(new ArrayList<>(List.of(matching, notMatching, new Todo("read book"))));
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        new OnCommand(LocalDate.of(2019, 12, 1)).execute(tasks, ui, storage);

        assertEquals("Here are the tasks on Dec 01 2019:\n1.[D][ ] return book (by: Dec 01 2019)",
                String.join("\n", lines));
    }

    @Test
    public void execute_dateMatchesNothing_showsHeaderOnly() {
        List<String> lines = new ArrayList<>();
        Ui ui = new Ui(lines::add);
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Deadline("pay bills", LocalDate.of(2019, 12, 2)))));
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        new OnCommand(LocalDate.of(2019, 12, 25)).execute(tasks, ui, storage);

        assertEquals("Here are the tasks on Dec 25 2019:", String.join("\n", lines));
    }
}

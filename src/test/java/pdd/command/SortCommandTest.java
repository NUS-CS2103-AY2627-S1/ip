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
import pdd.task.Task;
import pdd.task.TaskList;
import pdd.task.Todo;
import pdd.ui.Ui;

public class SortCommandTest {
    @TempDir
    Path tempDir;

    @Test
    public void execute_mixedTaskTypes_sortsShowsAndSavesInNewOrder() {
        List<String> lines = new ArrayList<>();
        Ui ui = new Ui(lines::add);
        Todo zTodo = new Todo("z todo");
        Deadline deadline = new Deadline("b deadline", LocalDate.of(2019, 12, 15));
        TaskList tasks = new TaskList(new ArrayList<>(List.of(zTodo, deadline)));
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        new SortCommand().execute(tasks, ui, storage);

        assertEquals(List.of(deadline, zTodo), tasks.getTasks());
        assertEquals(
                "Sorted! Here are your tasks, in order:\n"
                        + "1.[D][ ] b deadline (by: Dec 15 2019)\n"
                        + "2.[T][ ] z todo",
                String.join("\n", lines));
        List<Task> saved = storage.load();
        assertEquals("[D][ ] b deadline (by: Dec 15 2019)", saved.get(0).toString());
        assertEquals("[T][ ] z todo", saved.get(1).toString());
    }
}

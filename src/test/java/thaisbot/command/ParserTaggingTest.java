package thaisbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.task.TaskList;

public class ParserTaggingTest {
    @TempDir
    Path tempDir;

    @Test
    public void commands_parseTagsAndPersistThem() throws Exception {
        Parser parser = new Parser();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString(), parser);
        TaskList tasks = new TaskList();
        Ui ui = new NoOpUi();

        execute(parser, "todo read book #fun #school", tasks, ui, storage);
        execute(parser, "deadline return book /by 2026-09-10 #school", tasks, ui, storage);
        execute(parser, "event group study /from 2026-09-10 0900 /to 2026-09-10 1100 #project", tasks, ui, storage);

        TaskList loaded = storage.load();
        assertEquals("[T][ ] read book #fun #school", loaded.get(0).toString());
        assertTrue(loaded.get(0).matchesSearch("fun"));
        assertTrue(loaded.get(1).toString().endsWith("#school"));
        assertTrue(loaded.get(2).toString().endsWith("#project"));
    }

    @Test
    public void invalidTags_throwException() {
        Parser parser = new Parser();

        assertThrows(ThaisBotException.class, () -> parser.parse("todo read book #fun!"));
    }

    private void execute(Parser parser, String input, TaskList tasks, Ui ui, Storage storage)
            throws Exception {
        Command command = parser.parse(input);
        command.execute(tasks, ui, storage);
    }

    private static class NoOpUi extends Ui {
        @Override
        public void showWelcome() {
        }

        @Override
        public void showBye() {
        }

        @Override
        public void showError(String message) {
        }

        @Override
        public void showTaskList(TaskList tasks) {
        }

        @Override
        public void showTaskAdded(thaisbot.task.Task task, int taskCount) {
        }

        @Override
        public void showTaskMarkedDone(thaisbot.task.Task task) {
        }

        @Override
        public void showTaskMarkedNotDone(thaisbot.task.Task task) {
        }

        @Override
        public void showTaskRemoved(thaisbot.task.Task task, int taskCount) {
        }

        @Override
        public void showTasksOnDate(TaskList tasks, java.time.LocalDate date) {
        }

        @Override
        public void showMatchingTasks(TaskList tasks, String keyword) {
        }
    }
}

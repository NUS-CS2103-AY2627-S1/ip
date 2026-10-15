package gooble.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gooble.storage.Storage;
import gooble.task.TaskList;
import gooble.task.Todo;

/** Tests console formatting that is independent of the JavaFX GUI. */
class UiTest {
    @Test
    void welcomeAndHelp_containExpectedGuidance() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);

        ui.showWelcome();
        ui.showHelp();
        ui.showHelp("todo");

        assertTrue(output.toString().contains("Hello! I'm Gooble."));
        assertTrue(output.toString().contains("GOOBLE COMMANDS"));
        assertTrue(output.toString().contains("TODO COMMAND"));
    }

    @Test
    void taskMessages_renderStatusesAndTags(@TempDir Path tempDir) {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);
        TaskList tasks = new TaskList(new Storage(tempDir.resolve("tasks.txt")));
        Todo task = new Todo("read book");
        task.addTag("#school");
        tasks.add(task);

        ui.showTasks(tasks);
        ui.showMatchingTasks(java.util.List.of(task));
        ui.showAdded(task);
        ui.showMarkedDone(task);
        ui.showMarkedNotDone(task);
        ui.showDeleted(task, 0);
        ui.showTaskCount(1);
        ui.showBye();
        ui.showTagged(task);
        ui.showUntagged(task);

        assertTrue(output.toString().contains("1.[T] [ ] read book [#school]"));
        assertTrue(output.toString().contains("Bye. Hope to see you again soon!"));
        assertTrue(output.toString().contains("Now you have 1 tasks in the list."));
    }
}

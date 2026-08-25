package matthew.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import matthew.task.Task;
import matthew.task.Todo;

/** Tests console input and all user-interface messages. */
class UiTest {
    @Test
    void uiReadsCommandsAndDisplaysEveryMessageType() throws Exception {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui();
            assertEquals("list", ui.readCommand());

            Todo todo = new Todo("buy milk");
            ArrayList<Task> tasks = new ArrayList<>();
            tasks.add(todo);
            String output = capture(() -> {
                ui.showWelcome();
                ui.showLine();
                ui.showGoodbye();
                ui.showError("bad command");
                ui.showLoadingError();
                ui.showTaskList(tasks);
                ui.showAddedTask(todo, 1);
                ui.showMarkedTask(todo);
                ui.showUnmarkedTask(todo);
                ui.showDeletedTask(todo, 0);
                ui.showTasksOn("2026-08-22", new ArrayList<>());
                ui.showTasksOn("2026-08-22", tasks);
            });

            assertTrue(output.contains("Hello! I'm Matthew."));
            assertTrue(output.contains("Goodbye! Have a nice day!"));
            assertTrue(output.contains("OOPS!!! bad command"));
            assertTrue(output.contains("couldn't load the saved tasks"));
            assertTrue(output.contains("1.[T][ ] buy milk"));
            assertTrue(output.contains("Now you have 0 tasks"));
            assertTrue(output.contains("No deadlines or events found."));
            assertTrue(output.contains("Tasks occurring on 2026-08-22"));
        } finally {
            System.setIn(originalInput);
        }
    }

    private String capture(ThrowingRunnable action) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            action.run();
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(original);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}

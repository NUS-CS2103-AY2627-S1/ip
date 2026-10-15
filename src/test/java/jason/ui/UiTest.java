package jason.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jason.exception.InvalidCommandException;
import jason.task.TaskList;
import jason.task.ToDo;

/** Tests the command-line input and output handled by {@link Ui}. */
class UiTest {
    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;
    private ByteArrayOutputStream output;

    @BeforeEach
    void redirectOutput() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void readCommand_readsOneLineFromStandardInput() {
        System.setIn(new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertEquals("list", ui.readCommand());
    }

    @Test
    void showMessages_printExpectedConsoleOutput() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();
        ToDo task = new ToDo("read book");
        tasks.add(task);

        ui.showWelcome();
        ui.showLine();
        ui.showTaskList(tasks);
        ui.showAddedTask(task);
        ui.showMarkedComplete(task);
        ui.showMarkedIncomplete(task);
        ui.showDeletedTask(task, 0);
        ui.showMatchingTasks(List.of(task));
        ui.showMatchingTasks(List.of());
        ui.showError(new InvalidCommandException());
        ui.showGoodbye();

        String consoleOutput = output.toString();
        assertTrue(consoleOutput.contains("Hello! My name is Jason"));
        assertTrue(consoleOutput.contains("_______"));
        assertTrue(consoleOutput.contains("Send me a command, and I will keep your tasks neatly structured."));
        assertTrue(consoleOutput.contains("1. [T] [ ] read book"));
        assertTrue(consoleOutput.contains("Added to your task list: [T] [ ] read book"));
        assertTrue(consoleOutput.contains("Updated the completion flag to true:"));
        assertTrue(consoleOutput.contains("Updated the completion flag to false:"));
        assertTrue(consoleOutput.contains("Your JSON task array now contains 0 tasks."));
        assertTrue(consoleOutput.contains("Here are the matching task objects:"));
        assertTrue(consoleOutput.contains("No matching task objects found."));
        assertTrue(consoleOutput.contains("Invalid command."));
        assertTrue(consoleOutput.contains("Goodbye! Your task data is ready for the next session."));
    }

    @Test
    void outputConsumer_receivesMessagesWithoutWritingToConsole() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showLine();
        ui.showGoodbye();

        assertEquals(List.of("_______", "Goodbye! Your task data is ready for the next session."), messages);
        assertEquals("", output.toString());
    }
}

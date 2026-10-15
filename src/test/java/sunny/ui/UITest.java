package sunny.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sunny.taskboard.Taskboard;
import sunnyexception.SunnyException;
import task.Task;
import task.ToDo;

public class UITest {
    private UI ui;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        ui = UI.getInstance();

        originalOut = System.out;
        output = new ByteArrayOutputStream();

        System.setOut(new PrintStream(output));
        ui.reset();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void listDisplaysTasks() {
        Taskboard taskboard = Taskboard.getInstance();

        taskboard.clearTasks();

        Task first = new ToDo("buy milk");
        Task second = new ToDo("do homework");

        taskboard.addTask(first);
        taskboard.addTask(second);
        taskboard.updateTaskCount(2);

        ui.replyList();

        String result = output.toString();

        assertTrue(result.contains("1."));
        assertTrue(result.contains("2."));
        assertTrue(result.contains(" buy milk"));
        assertTrue(result.contains(" do homework"));
    }

    @Test
    public void taskDisplaysTaskDescription() {
        Task task = new ToDo("buy milk");

        ui.replyTask(task);

        String result = output.toString();

        assertTrue(result.contains("buy milk"));
    }

    @Test
    public void markDisplaysMarkedTask() {
        Taskboard taskboard = Taskboard.getInstance();
        taskboard.clearTasks();

        Task task = new ToDo("buy milk");
        taskboard.addTask(task);

        task.mark();

        ui.replyMark(0);

        String result = output.toString();

        assertTrue(result.contains("buy milk"));
        assertTrue(result.contains("[X]"));
    }

    @Test
    public void unmarkDisplaysUnmarkedTask() {
        Taskboard taskboard = Taskboard.getInstance();
        taskboard.clearTasks();

        Task task = new ToDo("buy milk");
        task.mark();
        taskboard.addTask(task);

        task.unmark();

        ui.replyUnmark(0);

        String result = output.toString();

        assertTrue(result.contains("buy milk"));
        assertTrue(result.contains("[ ]"));
    }

    @Test
    public void replyGoodbyeTurnsOffUI() {
        ui.isRunning = true;

        ui.replyGoodbye();

        assertFalse(ui.isRunning);
    }

    @Test
    public void runProcessesTodoCommand() throws SunnyException {
        Taskboard taskboard = Taskboard.getInstance();
        taskboard.clearTasks();

        UI ui = UI.getInstance();

        Scanner scanner = new Scanner(
                "todo buy milk\nbye\n"
        );

        ui.run(scanner);

        assertEquals(1, taskboard.getTaskCount());
        assertEquals(" buy milk", taskboard.getTask(0).getDesc());
    }

    @Test
    public void runCanProcessSeveralCommands() throws SunnyException {
        Taskboard taskboard = Taskboard.getInstance();
        taskboard.clearTasks();

        UI ui = UI.getInstance();
        ui.reset();

        Scanner scanner = new Scanner(
                "todo buy milk\n" +
                        "todo do homework\n" +
                        "mark 1\n" +
                        "unmark 1\n" +
                        "delete 2\n" +
                        "bye\n"
        );

        ui.run(scanner);

        assertEquals(1, taskboard.getTaskCount());

        assertEquals(
                " buy milk",
                taskboard.getTask(0).getDesc()
        );

        assertFalse(taskboard.getTask(0).isDone());
    }

    @Test
    void find_printsTasks() {
        // Arrange
        ArrayList<Task> foundTasks = new ArrayList<>();

        Task task1 = new Task("Buy milk");
        Task task2 = new Task("Do homework");

        foundTasks.add(task1);
        foundTasks.add(task2);

        System.setOut(new PrintStream(output));

        ui.replyFind(foundTasks);

        String printedOutput = output.toString();

        assertTrue(printedOutput.contains("Buy milk"));
        assertTrue(printedOutput.contains("Do homework"));
    }
}

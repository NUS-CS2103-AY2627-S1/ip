package sunny.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sunny.taskboard.Taskboard;
import sunny.ui.UI;
import sunnyexception.SunnyException;
import sunnyexception.TaskEmptyDescException;
import sunnyexception.InsufficientInfoException;
import sunnyexception.TaskOutOfBoundsException;
import sunnyexception.TooManyKeywordsException;
import task.Deadline;
import task.Task;
import task.ToDo;

public class UserParserTest {
    private UI ui = UI.getInstance();

    @BeforeEach
    public void resetTaskboard() {
        Taskboard taskboard = Taskboard.getInstance();
        taskboard.clearTasks();
    }

    @Test
    public void todoWithoutDescriptionThrowsException() {
        UserParser parser = UserParser.getInstance();

        assertThrows(
                TaskEmptyDescException.class,
                () -> parser.parseUserInput("todo", ui)
        );
    }

    @Test
    public void deadlineWithoutDescriptionThrowsException() {
        UserParser parser = UserParser.getInstance();

        assertThrows(
                TaskEmptyDescException.class,
                () -> parser.parseUserInput("deadline", ui)
        );
        assertThrows(
                InsufficientInfoException.class,
                () -> parser.parseUserInput("deadline submit report", ui)
        );
    }

    @Test
    public void eventWithoutDescriptionThrowsException() {
        UserParser parser = UserParser.getInstance();

        assertThrows(
                TaskEmptyDescException.class,
                () -> parser.parseUserInput("event", ui)
        );
        assertThrows(
                InsufficientInfoException.class,
                () -> parser.parseUserInput("event meeting", ui)
        );
    }

    @Test
    public void todoCommandCreatesTodo() throws SunnyException {
        UserParser parser = UserParser.getInstance();
        Taskboard taskboard = Taskboard.getInstance();

        int originalCount = taskboard.getTaskCount();

        parser.parseUserInput("todo buy milk", ui);

        assertEquals(originalCount + 1, taskboard.getTaskCount());

        Task task = taskboard.getTask(taskboard.getTaskCount() - 1);

        assertTrue(task instanceof ToDo);
        assertEquals(" buy milk", task.getDesc());
    }

    @Test
    public void markCommandMarksTask() throws SunnyException {
        UserParser parser = UserParser.getInstance();
        Taskboard taskboard = Taskboard.getInstance();

        Task task = new ToDo("buy milk");
        taskboard.addTask(task);
        taskboard.updateTaskCount(1);

        parser.parseUserInput("mark 1", ui);

        assertTrue(taskboard.getTask(0).isDone());
    }

    @Test
    public void unmarkCommandUnmarksTask() throws SunnyException {
        UserParser parser = UserParser.getInstance();
        Taskboard taskboard = Taskboard.getInstance();

        Task task = new ToDo("buy milk");
        task.mark();

        taskboard.addTask(task);
        taskboard.updateTaskCount(1);

        parser.parseUserInput("unmark 1", ui);

        assertFalse(taskboard.getTask(0).isDone());
    }

    @Test
    public void markInvalidIndexThrowsException() {
        UserParser parser = UserParser.getInstance();

        assertThrows(
                TaskOutOfBoundsException.class,
                () -> parser.parseUserInput("mark 999", ui)
        );
    }

    @Test
    public void deleteInvalidIndexThrowsException() {
        UserParser parser = UserParser.getInstance();

        assertThrows(
                TaskOutOfBoundsException.class,
                () -> parser.parseUserInput("delete 999", ui)
        );
    }

    @Test
    public void markWithNonNumberThrowsNumberFormatException() {
        UserParser parser = UserParser.getInstance();

        assertThrows(
                NumberFormatException.class,
                () -> parser.parseUserInput("mark banana", ui)
        );
    }

    @Test
    public void deadlineCommandCreatesDeadline() throws SunnyException {
        UserParser parser = UserParser.getInstance();
        Taskboard taskboard = Taskboard.getInstance();

        int originalCount = taskboard.getTaskCount();

        parser.parseUserInput(
                "deadline submit report /by 24/08/2026",
                ui
        );

        assertEquals(originalCount + 1, taskboard.getTaskCount());

        Task task = taskboard.getTask(taskboard.getTaskCount() - 1);

        assertTrue(task instanceof Deadline);
        assertEquals("submit report", task.getDesc());
    }

    @Test
    void parseUserInput_findWithoutKeyword_throwsException() {
        UserParser parser = UserParser.getInstance();

        assertThrows(InsufficientInfoException.class, () -> {
            parser.parseUserInput("find", ui);
        });
    }

    @Test
    void parseUserInput_findWithTooManyKeywords_throwsException() {
        UserParser parser = UserParser.getInstance();

        assertThrows(TooManyKeywordsException.class, () -> {
            parser.parseUserInput("find homework tomorrow", ui);
        });
    }

    @Test
    void parseUserInput_findMatchingTasks() throws SunnyException {
        UserParser parser = UserParser.getInstance();
        Taskboard taskboard = Taskboard.getInstance();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        Task task1 = new Task("Buy milk");
        Task task2 = new Task("Do homework");
        Task task3 = new Task("Buy groceries");

        taskboard.addTask(task1);
        taskboard.addTask(task2);
        taskboard.addTask(task3);

        parser.parseUserInput("find Buy", ui);

        System.setOut(originalOut);

        String result = output.toString();

        assertTrue(result.contains("Buy milk"));
        assertTrue(result.contains("Buy groceries"));
    }
}

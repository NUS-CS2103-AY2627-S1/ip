package zinc.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests adding, updating, finding, and deleting tasks in a task list. */
public class TaskListTest {
    private static final Path STORAGE_FILE = Path.of("data", "zincTasks.txt");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(STORAGE_FILE);
    }

    @Test
    public void addTask_todo_success() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("Buy bread");

        taskList.addTask(todo);

        assertEquals(todo, taskList.getTasks().get(0));
    }

    @Test
    public void addTask_whenTaskListIsFull_rejectsTask() {
        TaskList taskList = new TaskList();
        for (int i = 0; i < 100; i++) {
            taskList.addTask(new Todo("Task " + i));
        }

        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            taskList.addTask(new Todo("Read book"));
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals(100, taskList.getTaskCount());
        assertTrue(output.toString().contains("The task list is full"));
        assertFalse(output.toString().contains("Task added to list"));
    }

    @Test
    public void addTask_deadlineWithOnlyDate_addsDeadline() {
        TaskList taskList = new TaskList();

        LocalDate date = LocalDate.parse("2030-12-26", DATE_FORMAT);
        Deadline deadline = new Deadline("Buy bread", date.atStartOfDay());

        taskList.addTask(deadline);

        assertEquals(deadline, taskList.getTasks().get(0));
    }

    @Test
    public void addTask_deadlineWithDateAndTime_addsDeadline() {
        TaskList taskList = new TaskList();
        Deadline deadline = new Deadline("Buy bread", LocalDateTime.parse("2030-12-26T10:30"));

        taskList.addTask(deadline);

        assertEquals(deadline, taskList.getTasks().get(0));
    }

    @Test
    public void addTask_duplicateTodo_rejectsTask() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("Buy bread"));

        String output = captureOutput(() -> taskList.addTask(new Todo("Buy bread")));

        assertEquals(1, taskList.getTaskCount());
        assertTrue(output.contains("That task already exists in the database. Type \"list\" to see your tasks."));
        assertFalse(output.contains("Task added to list"));
    }

    @Test
    public void addTask_duplicateDeadline_rejectsTask() {
        TaskList taskList = new TaskList();
        LocalDateTime deadlineTime = LocalDateTime.parse("2030-12-26T10:30");
        taskList.addTask(new Deadline("Buy bread", deadlineTime));

        taskList.addTask(new Deadline("Buy bread", deadlineTime));

        assertEquals(1, taskList.getTaskCount());
    }

    @Test
    public void addTask_duplicateEvent_rejectsTask() {
        TaskList taskList = new TaskList();
        LocalDateTime start = LocalDateTime.parse("2030-12-26T10:30");
        LocalDateTime end = LocalDateTime.parse("2030-12-26T11:30");
        taskList.addTask(new Event("Meeting", start, end));

        taskList.addTask(new Event("Meeting", start, end));

        assertEquals(1, taskList.getTaskCount());
    }

    @Test
    public void addTask_sameDescriptionWithDifferentTimeParameters_allowsTasks() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Deadline("Buy bread", LocalDateTime.parse("2030-12-26T10:30")));

        taskList.addTask(new Deadline("Buy bread", LocalDateTime.parse("2030-12-26T11:30")));

        assertEquals(2, taskList.getTaskCount());
    }

    @Test
    public void addTask_eventWithOnlyDates_addsEvent() {
        LocalDate startDate = LocalDate.parse("2030-12-26", DATE_FORMAT);
        LocalDate endDate = LocalDate.parse("2030-12-27", DATE_FORMAT);

        Event event = new Event("Buy bread", startDate.atStartOfDay(), endDate.atStartOfDay());

        TaskList taskList = new TaskList();
        taskList.addTask(event);

        assertEquals(event, taskList.getTasks().get(0));
    }

    @Test
    public void addTask_eventWithDatesAndTimes_addsEvent() {
        TaskList taskList = new TaskList();
        Event event = new Event("Buy bread", LocalDateTime.parse("2030-12-26T10:30"),
                LocalDateTime.parse("2030-12-27T20:00"));

        taskList.addTask(event);

        assertEquals(event, taskList.getTasks().get(0));
    }

    @Test
    public void markTaskAsCompleted_validTask_marksTaskAsCompleted() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("Buy bread"));

        taskList.markTaskAsCompleted(1);

        assertTrue(taskList.getTasks().get(0).isCompleted());
    }

    @Test
    public void markTaskAsIncomplete_completedTask_marksTaskAsIncomplete() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("Buy bread"));
        taskList.markTaskAsCompleted(1);

        taskList.markTaskAsIncomplete(1);

        assertFalse(taskList.getTasks().get(0).isCompleted());
    }

    @Test
    public void delete_notLastAddedTask_success() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("First"));
        taskList.addTask(new Todo("Second"));

        taskList.deleteTask(1);

        assertEquals("Second", taskList.getTasks().get(0).getDescription());
        assertEquals(1, taskList.getTasks().size());
    }

    @Test
    public void delete_lastAddedTask_success() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("First"));
        taskList.addTask(new Todo("Second"));

        taskList.deleteTask(2);

        assertEquals("First", taskList.getTasks().get(0).getDescription());
        assertEquals(1, taskList.getTasks().size());
    }

    @Test
    public void printTasks_emptyTaskList_printsEmptyTaskListMessage() {
        TaskList taskList = new TaskList();

        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            taskList.printTasks();
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("You have no tasks and is free to jam!"));
        assertFalse(output.toString().contains("Here are your current tasks:"));
    }

    @Test
    public void printTasksEndingOn_success() {
        TaskList taskList = new TaskList();
        LocalDateTime deadline = LocalDateTime.of(2026, 8, 27, 18, 0);
        LocalDateTime deadline2 = LocalDateTime.of(2026, 8, 28, 18, 0);
        taskList.addTask(new Deadline("Submit report", deadline));
        taskList.addTask(new Deadline("Don't do report", deadline2));

        taskList.printTasksEndingOn(LocalDate.of(2026, 8, 27));

        assertEquals("Submit report", taskList.getTasks().get(0).getDescription());
    }

    @Test
    public void printTasksContaining_success() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("Buy bread"));
        taskList.addTask(new Todo("Read book"));

        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            taskList.printTasksContaining("bread");
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Buy bread"));
        assertFalse(output.toString().contains("Read book"));
    }

    /** Captures output printed while executing the supplied action. */
    private String captureOutput(Runnable action) {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString();
    }
}

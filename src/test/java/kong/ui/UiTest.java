package kong.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import kong.task.Deadline;
import kong.task.Event;
import kong.task.Task;
import kong.task.TaskList;
import kong.task.Todo;

class UiTest {
    @Test
    void showWelcome_displaysBannerAndGreeting() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showWelcome();

        assertEquals(1, output.size());
        assertTrue(output.get(0).contains("Greetings, esteemed colleague! I am Professor Kong."));
        assertTrue(output.get(0).contains(
                "What grand endeavor or academic inquiry shall we pursue today?"));
        assertTrue(output.get(0).contains("________________________________________"));
    }

    @Test
    void showLine_displaysDivider() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showLine();

        assertEquals(List.of("________________________________________"), output);
    }

    @Test
    void showError_displaysSuppliedMessage() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showError("Something went wrong");

        assertEquals(List.of("Something went wrong"), output);
    }

    @Test
    void showLoadingError_displaysDefaultDiskErrorMessage() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showLoadingError();

        assertEquals(List.of("Heavens! Unable to load tasks from disk. Starting with an empty archive."), output);
    }

    @Test
    void showTaskList_emptyList_displaysNoTasksMessage() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showTaskList(new TaskList());

        assertEquals(List.of("Your archive is pristine. There are currently no tasks in your list."), output);
    }

    @Test
    void showTaskList_populatedList_displaysHeaderAndNumberedTasks() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write code"));

        ui.showTaskList(tasks);

        assertEquals(3, output.size());
        assertEquals("Here are the tasks currently recorded in your archives:", output.get(0));
        assertEquals("1. [T][ ] read book", output.get(1));
        assertEquals("2. [T][ ] write code", output.get(2));
    }

    @Test
    void showTasksOnDate_empty_displaysNoTasksOnDateMessage() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showTasksOnDate(new ArrayList<>());

        assertEquals(List.of("The ledger is clear. There are no deadlines or events on this date."), output);
    }

    @Test
    void showTasksOnDate_populated_displaysHeaderAndNumberedTasks() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Deadline("return book", LocalDate.parse("2019-10-15")));

        ui.showTasksOnDate(tasks);

        assertEquals(2, output.size());
        assertEquals("Consulting the ledger. Here are the deadlines and events on this date:", output.get(0));
        assertEquals("1. [D][ ] return book (by: Oct 15 2019)", output.get(1));
    }

    @Test
    void showMatchingTasks_empty_displaysNoMatchesMessage() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showMatchingTasks(new ArrayList<>());

        assertEquals(List.of("Search complete. There are no matching tasks in your list."), output);
    }

    @Test
    void showMatchingTasks_populated_displaysHeaderAndNumberedTasks() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        ui.showMatchingTasks(tasks);

        assertEquals(2, output.size());
        assertEquals("Eureka! Here are the matching tasks found in your archives:", output.get(0));
        assertEquals("1. [T][ ] read book", output.get(1));
    }

    @Test
    void showTaskAdded_todo_displaysConfirmation() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Todo todo = new Todo("read book");

        ui.showTaskAdded(todo);

        assertEquals(2, output.size());
        assertEquals("Splendid addition! I have inscribed this task into your archives:", output.get(0));
        assertEquals("[T][ ] read book", output.get(1));
    }

    @Test
    void showTaskAdded_deadlineAndEvent_displaysConfirmation() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        Event event = new Event("conference", LocalDate.parse("2019-10-15"), LocalDate.parse("2019-10-16"));

        ui.showTaskAdded(deadline);
        ui.showTaskAdded(event);

        assertEquals(4, output.size());
        assertEquals("Splendid addition! I have inscribed this task into your archives:", output.get(0));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", output.get(1));
        assertEquals("Splendid addition! I have inscribed this task into your archives:", output.get(2));
        assertEquals("[E][ ] conference (from: Oct 15 2019 to: Oct 16 2019)", output.get(3));
    }

    @Test
    void showTaskMarked_displaysMarkedConfirmation() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Todo todo = new Todo("read book", true);

        ui.showTaskMarked(todo);

        assertEquals(List.of("Capital progress! I have marked this task as completed:",
                "[T][X] read book"), output);
    }

    @Test
    void showTaskUnmarked_displaysUnmarkedConfirmation() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Todo todo = new Todo("read book");

        ui.showTaskUnmarked(todo);

        assertEquals(List.of("Back to the drawing board! I have restored this task to pending status:",
                "[T][ ] read book"), output);
    }

    @Test
    void showTaskDeleted_displaysDeletedConfirmation() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Todo todo = new Todo("read book");

        ui.showTaskDeleted(todo);

        assertEquals(List.of("Expunged from the archives! The following task has been removed:",
                "[T][ ] read book"), output);
    }

    @Test
    void showGoodbye_displaysFarewell() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showGoodbye();

        assertEquals(List.of("Cheerio! Until our next scholarly consultation."), output);
    }

    @Test
    void readCommand_fromStandardInput_returnsEnteredLine() {
        InputStream originalIn = System.in;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8));
            System.setIn(in);
            Ui ui = new Ui();

            assertEquals("list", ui.readCommand());
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void readCommand_responseOnlyUi_throwsAssertionError() {
        Ui ui = new Ui(message -> { });

        assertThrows(AssertionError.class, ui::readCommand);
    }

    @Test
    void hasNextCommand_withAvailableInput_returnsTrue() {
        InputStream originalIn = System.in;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8));
            System.setIn(in);
            Ui ui = new Ui();

            assertTrue(ui.hasNextCommand());
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void hasNextCommand_withExhaustedInput_returnsFalse() {
        InputStream originalIn = System.in;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
            System.setIn(in);
            Ui ui = new Ui();

            assertFalse(ui.hasNextCommand());
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void hasNextCommand_responseOnlyUi_returnsFalse() {
        Ui ui = new Ui(message -> { });

        assertFalse(ui.hasNextCommand());
    }
}

package bro.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bro.task.Task;
import bro.task.TaskList;
import bro.task.Todo;

/**
 * Unit tests for {@link UserInterface}.
 */
public class UserInterfaceTest {
    private UserInterface ui;

    @BeforeEach
    public void setUp() {
        ui = new UserInterface();
    }

    @AfterEach
    public void tearDown() {
        ui.close();
    }

    @Test
    public void showWelcome_validCall_returnsExpectedMessage() {
        String welcome = ui.showWelcome();
        assertEquals("What's up bro, I'm Bro.\nIf you need anything, just ask bro.", welcome);
    }

    @Test
    public void showGoodbye_validCall_returnsExpectedMessage() {
        String goodbye = ui.showGoodbye();
        assertEquals("See you soon bro.", goodbye);
    }

    @Test
    public void showTaskList_withTaskList_returnsFormattedList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Todo completedTodo = new Todo("return book");
        completedTodo.markDone();
        tasks.add(completedTodo);

        String result = ui.showTaskList(tasks);
        String expected = "Here are the tasks you have bro:\n"
                + "1. [T][ ] read book\n"
                + "2. [T][X] return book";
        assertEquals(expected, result);
    }

    @Test
    public void showTaskList_withArrayList_returnsFormattedList() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("buy groceries"));

        String result = ui.showTaskList(tasks);
        String expected = "Here are the tasks you have bro:\n"
                + "1. [T][ ] buy groceries";
        assertEquals(expected, result);
    }

    @Test
    public void showTasksForDate_emptyList_returnsNoTasksMessage() {
        LocalDate date = LocalDate.of(2026, 10, 15);
        String result = ui.showTasksForDate(date, new ArrayList<>());
        assertEquals("You don't have any tasks for Oct 15 2026 bro.", result);
    }

    @Test
    public void showTasksForDate_matchingTasks_returnsFormattedTasks() {
        LocalDate date = LocalDate.of(2026, 10, 15);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("study for finals"));

        String result = ui.showTasksForDate(date, tasks);
        String expected = "Here are the tasks happening on Oct 15 2026 bro:\n"
                + "1. [T][ ] study for finals";
        assertEquals(expected, result);
    }

    @Test
    public void showTasksContainingKeywords_emptyList_returnsNotFoundMessage() {
        String result = ui.showTasksContainingKeywords("gym", new ArrayList<>());
        assertEquals("Sorry bro, I couldn't find any tasks containing 'gym', "
                + "can you check that you didn't make a typo?", result);
    }

    @Test
    public void showTasksContainingKeywords_matchingTasks_returnsFormattedTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("go to the gym"));

        String result = ui.showTasksContainingKeywords("gym", tasks);
        String expected = "No problem bro, here are the tasks containing 'gym':\n"
                + "1. [T][ ] go to the gym";
        assertEquals(expected, result);
    }

    @Test
    public void showTaskAdded_validTask_returnsAddedMessage() {
        Task task = new Todo("read book");
        String result = ui.showTaskAdded(task, 1);
        String expected = "I gotchu bro, added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 task in the list.";
        assertEquals(expected, result);
    }

    @Test
    public void showTaskDeleted_validTask_returnsDeletedMessage() {
        Task task = new Todo("clean room");
        String result = ui.showTaskDeleted(task, 0);
        String expected = "No problem bro, I've removed this task:\n"
                + "  [T][ ] clean room\n"
                + "Now you have 0 tasks in the list.";
        assertEquals(expected, result);
    }

    @Test
    public void showTaskMarked_validTask_returnsMarkedMessage() {
        Task task = new Todo("wash dishes");
        task.markDone();
        String result = ui.showTaskMarked(task);
        String expected = "Nice bro, I've marked this task as done for you:\n"
                + "  [T][X] wash dishes";
        assertEquals(expected, result);
    }

    @Test
    public void showTaskAlreadyMarked_validTask_returnsAlreadyMarkedMessage() {
        Task task = new Todo("wash dishes");
        task.markDone();
        String result = ui.showTaskAlreadyMarked(task);
        String expected = "Bro, that task is already marked as done:\n"
                + "  [T][X] wash dishes";
        assertEquals(expected, result);
    }

    @Test
    public void showTaskUnmarked_validTask_returnsUnmarkedMessage() {
        Task task = new Todo("wash dishes");
        String result = ui.showTaskUnmarked(task);
        String expected = "That's tough bro, I've marked this task as not done yet:\n"
                + "  [T][ ] wash dishes";
        assertEquals(expected, result);
    }

    @Test
    public void showTaskAlreadyUnmarked_validTask_returnsAlreadyUnmarkedMessage() {
        Task task = new Todo("wash dishes");
        String result = ui.showTaskAlreadyUnmarked(task);
        String expected = "Bro, that task isn't marked as done yet anyway:\n"
                + "  [T][ ] wash dishes";
        assertEquals(expected, result);
    }

    @Test
    public void showTaskEdited_validTask_returnsFormattedMessage() {
        Task task = new Todo("buy groceries");
        String result = ui.showTaskEdited(task);
        String expected = "Gotchu bro, I've updated this task:\n"
                + "  [T][ ] buy groceries";
        assertEquals(expected, result);
    }

    @Test
    public void showDuplicateTaskWarning_validTask_returnsFormattedMessage() {
        Task task = new Todo("buy groceries");
        String result = ui.showDuplicateTaskWarning(task);
        String expected = "Bro, you already have that exact task in your list:\n"
                + "  [T][ ] buy groceries";
        assertEquals(expected, result);
    }

    @Test
    public void showTaskCount_variousSizes_correctPluralization() {
        assertEquals("Now you have 0 tasks in the list.", ui.showTaskCount(0));
        assertEquals("Now you have 1 task in the list.", ui.showTaskCount(1));
        assertEquals("Now you have 2 tasks in the list.", ui.showTaskCount(2));
    }

    @Test
    public void showError_trimmedMessageReturned() {
        String error = ui.showError("\tSomething went wrong bro!  \n");
        assertEquals("Something went wrong bro!", error);
    }

    @Test
    public void showSpecificErrors_returnsExpectedMessages() {
        assertEquals("Bro, please enter a valid task number.", ui.showInvalidTaskNumberError());
        assertEquals("Uhh, that task doesn't exist in your list bro.", ui.showNoSuchTaskError());
        assertEquals("Bro, your list is completely empty right now, so there's no task 1.",
                ui.showNoSuchTaskError(1, 0));
        assertEquals("Bro, task 3 doesn't exist. You have 2 tasks, so pick a number between 1 and 2.",
                ui.showNoSuchTaskError(3, 2));
        assertEquals("Bro, task 2 doesn't exist. You have 1 task, so pick a number between 1 and 1.",
                ui.showNoSuchTaskError(2, 1));
        assertTrue(ui.showLoadingError("corrupted")
                .contains("Oops, couldn't load tasks from file bro: corrupted"));
        assertTrue(ui.showSavingError("permission denied")
                .contains("Oops, couldn't save your tasks to file bro: permission denied"));
    }
}


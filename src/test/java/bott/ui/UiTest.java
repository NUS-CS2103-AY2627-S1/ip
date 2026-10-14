package bott.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import bott.task.Task;
import bott.task.TaskType;
import bott.task.Todo;

/**
 * Tests {@link Ui}'s message-building methods: pure functions that turn a task (or list of
 * tasks) into the text of one of Sergeant Bott's responses. The console/GUI-driving methods
 * ({@code showWelcome}, {@code readCommand}, etc.) are not tested here - they are thin wrappers
 * around {@code System.in}/{@code System.out} whose actual behavior is exercised by
 * {@code test/ui-test-plan.md} via the {@code test-ui} skill instead.
 */
public class UiTest {

    private final Ui ui = new Ui();

    @Test
    public void getWelcomeMessage_returnsGreeting() {
        assertEquals(
                "Ten-hut! Sergeant Bott reporting for duty.\nWhat's your first order, recruit?",
                ui.getWelcomeMessage());
    }

    @Test
    public void getGoodbyeMessage_returnsFarewell() {
        assertEquals("Dismissed! Fall out, recruit.", ui.getGoodbyeMessage());
    }

    @Test
    public void getErrorMessage_prependsNegativeRecruitPrefix() {
        assertEquals("NEGATIVE, RECRUIT! A todo needs a description.",
                ui.getErrorMessage("A todo needs a description."));
    }

    @Test
    public void getTaskListMessage_emptyList_showsHeaderOnly() {
        assertEquals("Roll call! Here's your mission roster:", ui.getTaskListMessage(List.of()));
    }

    @Test
    public void getTaskListMessage_multipleTasks_numbersFromOneInOrder() {
        List<Task> tasks = List.of(new Todo("read book"), new Todo("return book"));
        assertEquals(
                "Roll call! Here's your mission roster:\n"
                        + "1.[T][ ] read book\n"
                        + "2.[T][ ] return book",
                ui.getTaskListMessage(tasks));
    }

    @Test
    public void getMatchingTasksMessage_emptyList_showsHeaderOnly() {
        assertEquals(
                "Found these missions matching your intel:",
                ui.getMatchingTasksMessage(List.of()));
    }

    @Test
    public void getMatchingTasksMessage_multipleTasks_numbersFromOne() {
        List<Task> matches = List.of(new Todo("read book"), new Todo("return book"));
        assertEquals(
                "Found these missions matching your intel:\n"
                        + "1.[T][ ] read book\n"
                        + "2.[T][ ] return book",
                ui.getMatchingTasksMessage(matches));
    }

    @Test
    public void getTaskAddedMessage_includesHeadlineTaskAndCount() {
        assertEquals(
                "Mission logged, recruit! Fall in:\n"
                        + "  [T][ ] read book\n"
                        + "You now have 3 mission(s) on the roster.",
                ui.getTaskAddedMessage(new Todo("read book"), 3));
    }

    @Test
    public void getTaskDeletedMessage_includesHeadlineTaskAndCount() {
        assertEquals(
                "Mission scrubbed, recruit! Fall out:\n"
                        + "  [T][ ] read book\n"
                        + "You now have 0 mission(s) on the roster.",
                ui.getTaskDeletedMessage(new Todo("read book"), 0));
    }

    @Test
    public void getTaskMarkedMessage_includesHeadlineAndTask() {
        Task task = new Task("read book", TaskType.TODO);
        task.markAsDone();
        assertEquals("Outstanding! Mission accomplished:\n  [T][X] read book",
                ui.getTaskMarkedMessage(task));
    }

    @Test
    public void getTaskUnmarkedMessage_includesHeadlineAndTask() {
        Task task = new Task("read book", TaskType.TODO);
        assertEquals("At ease. Mission's back on the roster:\n  [T][ ] read book",
                ui.getTaskUnmarkedMessage(task));
    }
}

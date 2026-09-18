package aster.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import aster.task.TaskStatistics;
import aster.task.Todo;

/**
 * Tests that a {@link Ui} given a line writer sends every line it shows there, that the
 * text interface's dividers still frame the greeting and the farewell, and the exact
 * wording of the task statistics.
 *
 * <p>Every case collects the lines into a list, so nothing here reads from the keyboard
 * or writes to the screen, and no standard stream is replaced.
 */
class UiTest {
    private static final String DIVIDER =
            "____________________________________________________________";

    @Test
    void constructorWithLineWriter_showAdded_sendsEachLine() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showAdded(new Todo("read book"), 1);

        assertEquals(List.of("Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 task in the list."), lines);
    }

    @Test
    void showAlreadyMarked_doneTask_sendsTwoLines() {
        List<String> lines = new ArrayList<>();
        Todo task = new Todo("read book");
        task.markAsDone();

        new Ui(lines::add).showAlreadyMarked(task);

        assertEquals(List.of("This task is already marked as done:",
                "  [T][X] read book"), lines);
    }

    @Test
    void showAlreadyUnmarked_taskNotDone_sendsTwoLines() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showAlreadyUnmarked(new Todo("read book"));

        assertEquals(List.of("This task is already marked as not done:",
                "  [T][ ] read book"), lines);
    }

    @Test
    void showWelcome_lineWriter_framesGreetingWithDividers() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showWelcome();

        assertEquals(List.of(DIVIDER,
                "Hello! I'm Aster.",
                "I'm a simple chatbot, and I'm glad you're here.",
                "What can I do for you?",
                DIVIDER), lines);
    }

    @Test
    void showGoodbye_lineWriter_framesFarewellWithDividers() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showGoodbye();

        assertEquals(List.of(DIVIDER, "Goodbye for now. Take care!", DIVIDER), lines);
    }

    @Test
    void hasNextCommand_lineWriterConstructor_readsNothing() {
        // An interface built for collecting a reply must never wait on the keyboard.
        assertFalse(new Ui(line -> { }).hasNextCommand());
    }

    @Test
    void showStatistics_mixedTasks_sendsFiveExactLines() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showStatistics(new TaskStatistics(5, 2, 2, 2, 1));

        assertEquals(List.of("Here are your task statistics:",
                "Total: 5 tasks",
                "Completed: 2 (40%)",
                "Not completed: 3",
                "Todos: 2, Deadlines: 2, Events: 1"), lines);
    }

    @Test
    void showStatistics_oneTask_usesSingularTask() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showStatistics(new TaskStatistics(1, 1, 1, 0, 0));

        assertEquals(List.of("Here are your task statistics:",
                "Total: 1 task",
                "Completed: 1 (100%)",
                "Not completed: 0",
                "Todos: 1, Deadlines: 0, Events: 0"), lines);
    }

    @Test
    void showStatistics_oneShortOfTwoHundred_showsNinetyNinePercent() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showStatistics(new TaskStatistics(200, 199, 200, 0, 0));

        assertEquals("Completed: 199 (99%)", lines.get(2));
    }

    @Test
    void showStatistics_noTasks_sendsOnlyTheNoStatisticsLine() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showStatistics(new TaskStatistics(0, 0, 0, 0, 0));

        assertEquals(List.of("You have no tasks yet, so there are no statistics to show."), lines);
    }
}

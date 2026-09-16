package arin.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import arin.task.Deadline;
import arin.task.Task;
import arin.task.Todo;

/**
 * Tests task-list output from {@link Ui}.
 */
class UiTest {
    private static final String DIVIDER = "________________________________";

    @Test
    void showTaskList_emptyList_showsHeadingWithoutTaskEntries() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(buffer));

        ui.showTaskList(List.of());

        assertEquals(DIVIDER + System.lineSeparator()
                + "Here are the tasks in your list:" + System.lineSeparator()
                + DIVIDER + System.lineSeparator(), buffer.toString());
    }

    @Test
    void showTaskList_multipleTasks_showsOneBasedNumbersAndTaskText() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(buffer));
        Task completedTask = new Todo("read book");
        completedTask.markTask();

        ui.showTaskList(List.of(new Todo("buy milk"), completedTask));

        assertEquals(DIVIDER + System.lineSeparator()
                + "Here are the tasks in your list:" + System.lineSeparator()
                + "1. [T][ ] buy milk" + System.lineSeparator()
                + "2. [T][X] read book" + System.lineSeparator()
                + DIVIDER + System.lineSeparator(), buffer.toString());
    }

    @Test
    void showMatchingTasks_multipleTasks_showsOneBasedNumbersAndTaskText() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(buffer));
        Task completedTask = new Todo("read book");
        completedTask.markTask();

        ui.showMatchingTasks(List.of(
                completedTask,
                new Deadline("return book", LocalDate.of(2026, 6, 6))
        ));

        assertEquals(DIVIDER + System.lineSeparator()
                + "Here are the matching tasks in your list:" + System.lineSeparator()
                + "1. [T][X] read book" + System.lineSeparator()
                + "2. [D][ ] return book (by: Jun 06 2026)" + System.lineSeparator()
                + DIVIDER + System.lineSeparator(), buffer.toString());
    }

    @Test
    void showMatchingTasks_noMatchingTasks_showsHeadingWithoutTaskEntries() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(buffer));

        ui.showMatchingTasks(List.of());

        assertEquals(DIVIDER + System.lineSeparator()
                + "Here are the matching tasks in your list:" + System.lineSeparator()
                + DIVIDER + System.lineSeparator(), buffer.toString());
    }
}

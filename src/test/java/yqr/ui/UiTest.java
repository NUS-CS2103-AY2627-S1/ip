package yqr.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import yqr.task.Todo;

class UiTest {
    @Test
    void readCommand_commandSurroundedByWhitespace_trimmedCommandReturned() {
        try (Scanner scanner = new Scanner("  list  \n")) {
            Ui ui = new Ui(scanner, ignored -> {
            });

            assertTrue(ui.hasNextCommand());
            assertEquals("list", ui.readCommand());
            assertFalse(ui.hasNextCommand());
        }
    }

    @Test
    void showTaskList_emptyList_emptyListMessageEmitted() {
        List<String> outputLines = new ArrayList<>();
        Ui ui = new Ui(outputLines::add);

        ui.showTaskList(List.of());

        assertIterableEquals(List.of("There are no tasks in your list."), outputLines);
    }

    @Test
    void showMatchingTasks_emptyList_noMatchesMessageEmitted() {
        List<String> outputLines = new ArrayList<>();
        Ui ui = new Ui(outputLines::add);

        ui.showMatchingTasks(List.of());

        assertIterableEquals(List.of("There are no matching tasks in your list."), outputLines);
    }

    @Test
    void showTaskList_multipleTasks_numberedWithReadableSpacing() {
        List<String> outputLines = new ArrayList<>();
        Ui ui = new Ui(outputLines::add);

        ui.showTaskList(List.of(new Todo("read book"), new Todo("write notes")));

        assertIterableEquals(List.of(
                "Here are the tasks in your list:",
                "1. [T][ ] read book",
                "2. [T][ ] write notes"), outputLines);
    }

    @Test
    void showTaskAdded_taskAndCountProvided_allLinesEmittedInOrder() {
        List<String> outputLines = new ArrayList<>();
        Ui ui = new Ui(outputLines::add);

        ui.showTaskAdded(new Todo("read book"), 1);

        assertIterableEquals(List.of(
                "Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 task in the list."), outputLines);
    }
}

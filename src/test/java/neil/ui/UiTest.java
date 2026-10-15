package neil.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import neil.task.Task;
import neil.task.ToDoTask;

/**
 * Tests for displaying messages to the user.
 */
class UiTest {
    @Test
    void showWelcome_applicationStarted_helpCommandSuggested() {
        Ui ui = new Ui();

        assertTrue(ui.showWelcome().contains("Type \"help\" to see available commands."));
    }

    @Test
    void showHelp_helpRequested_commandsAndExamplesReturned() {
        Ui ui = new Ui();

        String[] lines = ui.showHelp().split("\n");
        List<List<String>> rows = Arrays.stream(lines)
                .skip(4)
                .limit(10)
                .map(UiTest::splitHelpRow)
                .toList();

        assertEquals("AVAILABLE COMMANDS", lines[0]);
        assertEquals(List.of("COMMAND", "PURPOSE", "EXAMPLE"), splitHelpRow(lines[2]));
        assertEquals(List.of(
                List.of("todo DESCRIPTION", "Add a todo task.", "todo read book"),
                List.of(
                        "deadline DESCRIPTION /by YYYY-MM-DD",
                        "Add a deadline task.",
                        "deadline return book /by 2026-09-15"
                ),
                List.of(
                        "event DESCRIPTION /from START /to END",
                        "Add an event task.",
                        "event meeting /from 2026-09-15 14:00 /to 2026-09-15 15:00"
                ),
                List.of("list", "Show all tasks.", "list"),
                List.of("mark NUMBER", "Mark a task as done.", "mark 1"),
                List.of("unmark NUMBER", "Mark a task as not done.", "unmark 1"),
                List.of("delete NUMBER", "Delete a task.", "delete 1"),
                List.of("find KEYWORD", "Find matching tasks.", "find book"),
                List.of("help", "Show this help table.", "help"),
                List.of("bye", "Exit Neil.", "bye")
        ), rows);
        assertEquals("NUMBER is the task number shown by list.", lines[15]);
        assertEquals(
                "START and END use yyyy-MM-dd HH:mm and START must be earlier than END.",
                lines[16]);
    }

    @Test
    void showMatchingTasks_matchingTasks_tasksDisplayedWithNewNumbers() {
        Task firstTask = new ToDoTask("read book");
        Task secondTask = new ToDoTask("return book");
        secondTask.markAsDone();
        Ui ui = new Ui();

        String message = ui.showMatchingTasks(List.of(firstTask, secondTask));

        String expectedOutput = "Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[T][X] return book";
        assertEquals(expectedOutput, message);
    }

    @Test
    void showMatchingTasks_noMatchingTasks_headingDisplayed() {
        Ui ui = new Ui();

        String message = ui.showMatchingTasks(List.of());

        assertEquals("Here are the matching tasks in your list:", message);
    }

    private static List<String> splitHelpRow(String row) {
        return List.of(row.strip().split("\\s{2,}"));
    }
}

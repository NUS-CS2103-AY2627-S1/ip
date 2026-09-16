package kibo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import kibo.task.TaskList;
import kibo.task.Todo;

/**
 * Tests that Kibo's friendly confirmations preserve task details and accurate counts.
 */
public class UiTest {
    private final Ui ui = new Ui();

    @Test
    void getListMessages_emptyLists_returnsHeadingsWithoutPhantomTasks() {
        TaskList empty = new TaskList();
        assertEquals("Here's your lineup. One step at a time!", ui.getTaskListMessage(empty));
        assertEquals("Let's see what matches your search:", ui.getMatchingTasksMessage(empty));
        assertEquals("Let's plan your day! Schedule for Feb 29 2024:",
                ui.getScheduleMessage(empty, LocalDate.of(2024, 2, 29)));
    }

    @Test
    void getScheduleMessage_nonEnglishLocale_preservesEnglishDateAndNumbering() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CHINESE);
            TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));
            assertEquals("Let's plan your day! Schedule for Feb 29 2024:\n1.[T][ ] first\n2.[T][ ] second",
                    ui.getScheduleMessage(tasks, LocalDate.of(2024, 2, 29)));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void getTaskAddedMessage_singleAndMultipleTasks_usesCorrectNoun() {
        Todo task = new Todo("read book");
        String confirmation = "On the list! Let's make it happen:\n  [T][ ] read book\n";

        assertEquals(confirmation + "You've got 1 task on your list.", ui.getTaskAddedMessage(task, 1));
        assertEquals(confirmation + "You've got 2 tasks on your list.", ui.getTaskAddedMessage(task, 2));
    }

    @Test
    void getTaskDeletedMessage_lastCompletedTask_keepsStatusAndShowsZero() {
        Todo task = new Todo("read book");
        task.markAsDone();

        assertEquals("All cleared! I've removed this task:\n  [T][X] read book\n"
                + "You've got 0 tasks on your list.", ui.getTaskDeletedMessage(task, 0));
    }
}

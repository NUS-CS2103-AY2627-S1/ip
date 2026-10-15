package catgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests user-facing response formatting in {@link Ui}.
 */
class UiTest {
    private final Ui ui = new Ui();

    @Test
    void formatTaskAddedWithOneTaskUsesSingularNoun() {
        String response = ui.formatTaskAdded(new Todo("read book"), 1);

        assertEquals(
                "Purr-fect! I've added this task:" + System.lineSeparator()
                        + "[T][ ] read book" + System.lineSeparator()
                        + "Now you have 1 task in the list.",
                response);
    }

    @Test
    void formatTaskDeletedWithMultipleTasksUsesPluralNoun() {
        String response = ui.formatTaskDeleted(new Todo("read book"), 2);

        assertEquals(
                "Noted. I've removed this task:" + System.lineSeparator()
                        + "[T][ ] read book" + System.lineSeparator()
                        + "Now you have 2 tasks in the list.",
                response);
    }

    @Test
    void formatMatchingTasksWithNoMatchesReturnsHeadingOnly() {
        String response = ui.formatMatchingTasks(new TaskList(List.of()));

        assertEquals("Here are the matching tasks in your list:", response);
    }
}

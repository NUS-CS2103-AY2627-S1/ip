package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class UiTest {

    @Test
    public void formatTaskList_emptyAndPopulatedLists_formatsOneBasedNumbers() {
        assertEquals("Your orbit is clear. There are no tasks yet.",
                Ui.formatTaskList(new TaskList()));

        TaskList tasks = new TaskList(List.of(
                Task.todo("read book"),
                Task.event("consultation", "2pm", "4pm")));
        String expected = String.join(System.lineSeparator(),
                "1.[T][ ] read book",
                "2.[E][ ] consultation (from: 2pm to: 4pm)");

        assertEquals(expected, Ui.formatTaskList(tasks));
    }

    @Test
    public void formatMatchingTasks_emptyAndPopulatedLists_formatsHeaderAndNumbers() {
        String header = "Scan complete. Here are the matching tasks:";
        assertEquals(header, Ui.formatMatchingTasks(List.of()));

        String expected = String.join(System.lineSeparator(),
                header,
                "1.[T][ ] read book",
                "2.[T][ ] return book");
        assertEquals(expected, Ui.formatMatchingTasks(List.of(
                Task.todo("read book"), Task.todo("return book"))));
    }

    @Test
    public void formatTaskConfirmations_validTasks_includeDetailsAndCounts() {
        Task task = Task.todo("read book");

        assertEquals(String.join(System.lineSeparator(),
                "Task added to your orbit:",
                "[T][ ] read book",
                "You now have 2 tasks in orbit."),
                Ui.formatAddedTask(task, 2));
        assertEquals(String.join(System.lineSeparator(),
                "Task cleared from your orbit:",
                "[T][ ] read book",
                "You now have 1 task in orbit."),
                Ui.formatDeletedTask(task, 1));
        assertEquals(String.join(System.lineSeparator(),
                "Course corrected! I've updated this task:",
                "[T][ ] read book"),
                Ui.formatUpdatedTask(task));
    }

    @Test
    public void formatFixedMessages_returnsExpectedText() {
        assertEquals(String.join(System.lineSeparator(),
                "Nova online! Your tasks are ready for launch.",
                "What shall we accomplish today?"), Ui.formatWelcomeMessage());
        assertEquals("Mission accomplished! I've marked this task as done.",
                Ui.formatMarkedMessage());
        assertEquals("That command is outside my orbit. Please try another one.",
                Ui.formatUnknownCommand());
        assertEquals("Nova signing off. Keep reaching for the stars!",
                Ui.formatByeMessage());
    }
}

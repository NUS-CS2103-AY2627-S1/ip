package sophon.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import sophon.model.TaskList;
import sophon.model.Todo;

public class UiTest {
    @Test
    public void getGreeting_withAndWithoutWarning_returnsExpectedContent() {
        Ui ui = new Ui();

        assertTrue(ui.getGreeting(null).endsWith("What do you wish to communicate?"));
        assertTrue(ui.getGreeting("Could not load.").endsWith("Could not load."));
    }

    @Test
    public void messages_returnExpectedText() {
        Ui ui = new Ui();

        assertEquals("Our conversation ends here.\nUntil we meet again.", ui.getByeMessage());
        assertEquals("problem", ui.getError("problem"));
    }

    @Test
    public void taskLists_emptyAndPopulated_returnsNumberedText() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();

        assertEquals("Current tasks under observation:\n", ui.getTaskList(tasks));
        assertEquals("These signals match your search:\n", ui.getMatchingTasks(tasks));

        tasks.add(new Todo("read book"));
        tasks.add(new Todo("borrow book"));
        assertEquals("Current tasks under observation:\n"
                + String.format("1.[T][ ] read book%n")
                + String.format("2.[T][ ] borrow book%n"), ui.getTaskList(tasks));
    }
}

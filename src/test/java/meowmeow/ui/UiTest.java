package meowmeow.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import meowmeow.task.Task;
import meowmeow.task.Todo;

public class UiTest {

    private final Ui ui = new Ui();

    @Test
    public void formatList_emptyList_emptyMessageReturned() {
        assertTrue(ui.formatList(new ArrayList<>()).contains("empty"));
    }

    @Test
    public void formatList_severalTasks_numberedFromOne() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        String output = ui.formatList(tasks);

        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[T][ ] return book"));
    }

    @Test
    public void formatAdded_taskAndCount_bothShown() {
        String output = ui.formatAdded(new Todo("read book"), 3);
        assertTrue(output.contains("read book"));
        assertTrue(output.contains("3 task"));
    }

    @Test
    public void formatMatches_noMatches_messageReturned() {
        assertTrue(ui.formatMatches(new ArrayList<>()).contains("No matching"));
    }
}
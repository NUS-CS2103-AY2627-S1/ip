package walnut;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class UiTest {
    private final Ui ui = new Ui();

    @Test
    void banner_containsApplicationNameArt() {
        assertTrue(ui.showBanner().contains("_"));
    }

    @Test
    void greetingMentionsWalnut() {
        assertTrue(ui.showGreeting().contains("Walnut"));
    }

    @Test
    void taskMessages_containRelevantInformation() {
        Task task = new ToDo("buy milk");

        assertTrue(ui.showTaskAdded(task, 1).contains("buy milk"));
        assertTrue(ui.showTaskRemoved(task, 0).contains("buy milk"));
        assertTrue(ui.showTaskMarkedAsDone(task).contains("done"));
        assertTrue(ui.showTaskMarkedAsNotDone(task).contains("not done"));
    }

    @Test
    void searchMessage_containsMatchingTask() {
        ArrayList<Task> found = new ArrayList<>();
        found.add(new ToDo("buy milk"));

        assertTrue(ui.showFoundTasks(found).contains("buy milk"));
    }
}

package rudra.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import rudra.task.ToDo;

/**
 * Tests for {@link Ui} output adapters.
 */
public class UiTest {
    @Test
    public void showTaskAdded_customOutput_receivesConfirmationLines() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showTaskAdded(new ToDo("read chapter 4"), 1);

        assertEquals(List.of(
                "You got it, homie - task locked in!",
                "[T][ ] read chapter 4",
                "Your radar now has 1 task.",
                "____________________________________________________________"), messages);
    }

    @Test
    public void showWelcome_customOutput_receivesRudraGreeting() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showWelcome();

        assertEquals("Yo! Rudra's online.", messages.get(2));
        assertEquals("What are we getting done today?", messages.get(3));
    }

    @Test
    public void showError_customOutput_keepsGuidanceClearAndInCharacter() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showError("Please include a task number.");

        assertEquals(List.of(
                "Whoa! Please include a task number.",
                "____________________________________________________________"), messages);
    }
}

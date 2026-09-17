package lebron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import lebron.task.TaskList;
import lebron.ui.Ui;

public class ByeCommandTest {
    @Test
    public void isExit_alwaysReturnsTrue() {
        assertTrue(new ByeCommand().isExit());
    }

    @Test
    public void execute_showsGoodbyeMessage() {
        String response = new ByeCommand().execute(new TaskList(new ArrayList<>()), new Ui());

        assertEquals("Peace out see ya later!", response);
    }
}

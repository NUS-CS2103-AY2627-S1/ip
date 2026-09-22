package longfrog.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import longfrog.ui.Ui;

class ExitCommandTest {
    @Test
    void execute_always_displaysFarewellAndRequestsExit() {
        List<String> messages = new ArrayList<>();

        boolean shouldExit = new ExitCommand().execute(new Ui(messages::add));

        assertTrue(shouldExit);
        assertEquals(List.of("Pond secured. Rest well—ribbit."), messages);
    }
}

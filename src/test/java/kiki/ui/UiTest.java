package kiki.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

public class UiTest {

    @Test
    public void printWelcome_kikiIntroducesTaskAssistantRole() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

        ui.printWelcome();

        assertTrue(output.toString().contains("Hello! I'm Kiki."));
        assertTrue(output.toString().contains("I'll help you keep track of your tasks."));
        assertFalse(output.toString().contains("Listing Mode"));
    }

    @Test
    public void printGoodbye_usesWarmFarewell() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

        ui.printGoodbye();

        assertTrue(output.toString().contains("All set for now. See you next time."));
    }
}

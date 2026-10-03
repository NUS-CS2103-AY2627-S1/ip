package ramly.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/** Tests ordered output from the console and GUI-compatible UI adapter. */
public class UiTest {
    @Test
    public void show_multipleMessages_preservesMessageOrder() {
        ArrayList<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.show("first", "second", "third");

        assertEquals(java.util.List.of("first", "second", "third"), messages);
    }

    @Test
    public void constructor_nullOutput_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Ui(null));
    }

    @Test
    public void show_nullMessage_throwsAssertionError() {
        Ui ui = new Ui(message -> { });

        assertThrows(AssertionError.class, () -> ui.show("first", null));
    }

    @Test
    public void showWelcome_usesPipPersonality() {
        ArrayList<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showWelcome();

        assertTrue(messages.contains("Hey, I'm Pip—your pocket pathfinder."));
        assertTrue(messages.contains("What shall we tackle next?"));
        assertFalse(messages.stream().anyMatch(message -> message.contains("Ramly")));
    }

    @Test
    public void readCommand_endOfInput_returnsNull() {
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), message -> { });

        assertNull(ui.readCommand());
    }
}

package pixel.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import pixel.task.Todo;

/** Tests console input normalization and output formatting. */
class UiTest {
    private final InputStream originalInput = System.in;

    @AfterEach
    void restoreStandardInput() {
        System.setIn(originalInput);
    }

    @Test
    void readCommand_availableInput_trimsEachLineAndTracksAvailability() {
        System.setIn(new ByteArrayInputStream("  list  \nbye\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui(new ArrayList<String>()::add);

        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    void showWelcome_standardGreeting_emitsBannerAndDividerSequence() {
        ArrayList<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showWelcome();

        assertEquals(5, messages.size());
        assertEquals("____________________________________________________________", messages.get(0));
        assertTrue(messages.get(1).contains("____  _"));
        assertEquals("Hello! I'm Pixel.", messages.get(2));
        assertEquals("What can I do for you?", messages.get(3));
        assertEquals(messages.get(0), messages.get(4));
    }

    @Test
    void showTaskAdded_taskAndCount_emitsCompleteConfirmation() {
        ArrayList<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showTaskAdded(new Todo("read book"), 1);

        assertEquals(List.of("Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 tasks in the list."), messages);
    }

    @Test
    void showMessageAndLine_customOutput_emitsBothValues() {
        ArrayList<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showMessage("hello");
        ui.showLine();

        assertEquals(List.of("hello",
                "____________________________________________________________"), messages);
    }
}

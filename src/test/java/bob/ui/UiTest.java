package bob.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

/**
 * Tests command-line input handled by {@link Ui}.
 */
class UiTest {
    @Test
    void readCommand_commandAvailable_trimmedCommandReturned() {
        Ui ui = new Ui(new Scanner(new StringReader("  list  \n")));

        String command = ui.readCommand();

        assertEquals("list", command);
        ui.close();
    }

    @Test
    void readCommand_endOfInput_byeReturned() {
        Ui ui = new Ui(new Scanner(new StringReader("")));

        String command = ui.readCommand();

        assertEquals("bye", command);
        ui.close();
    }
}

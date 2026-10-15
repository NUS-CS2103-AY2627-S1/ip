package esther.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class UiTest {
    @Test
    public void showToUser_multipleMessages_printsMessagesInOrder() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            Ui ui = new Ui();

            ui.showToUser("First message", "Second message");
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals(
                "First message" + System.lineSeparator()
                        + "Second message" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }
}

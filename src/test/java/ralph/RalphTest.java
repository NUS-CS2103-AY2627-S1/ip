package ralph;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class RalphTest {

    @Test
    void run_emptyInput_doesNotThrowNoSuchElement() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        try {
            // Empty input simulates EOF (e.g., user pressed Ctrl-D/closed stdin)
            System.setIn(new ByteArrayInputStream(new byte[0]));
            // Capture output to keep test output clean
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));

            Ralph app = new Ralph("data/ralph_test.txt");
            assertDoesNotThrow(() -> app.run());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    void getResponse_todoCommand_returnsAddedTaskString() throws Exception {
        Path tempFile = Files.createTempFile("ralph-gui-", ".txt");
        Ralph app = new Ralph(tempFile.toString());

        String response = app.getResponse("todo read book");

        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("read book"));
    }
}

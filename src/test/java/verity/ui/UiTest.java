package verity.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests Ui's console output and input reading. */
class UiTest {
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void redirectOut() {
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void showWelcome_printsBannerAndGreeting() {
        new Ui().showWelcome();

        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains(Ui.GREETING));
    }

    @Test
    void showLine_printsDivider() {
        new Ui().showLine();

        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("____"));
    }

    @Test
    void showError_printsErrorPrefixAndMessage() {
        new Ui().showError("something went wrong");

        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("ERROR!!! >.<"));
        assertTrue(output.contains("something went wrong"));
    }

    @Test
    void showResponse_printsResponseVerbatim() {
        new Ui().showResponse("Obviously. Added: read book");

        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Obviously. Added: read book"));
    }

    @Test
    void showLoadWarning_printsReason() {
        new Ui().showLoadWarning("file is a directory");

        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("file is a directory"));
    }

    @Test
    void readCommand_returnsLineFromStdin() {
        System.setIn(new ByteArrayInputStream("todo read book\n".getBytes(StandardCharsets.UTF_8)));

        String command = new Ui().readCommand();

        assertEquals("todo read book", command);
    }
}

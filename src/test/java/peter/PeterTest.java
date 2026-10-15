package peter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link Peter}, focused on the error/non-error distinction its
 * responses carry for the GUI to style differently, and on the loading and
 * command-line-session behaviour that wires everything else together.
 */
public class PeterTest {
    @TempDir
    private Path tempDir;

    private InputStream originalIn;
    private PrintStream originalOut;

    @AfterEach
    public void restoreSystemStreams() {
        if (originalIn != null) {
            System.setIn(originalIn);
            originalIn = null;
        }
        if (originalOut != null) {
            System.setOut(originalOut);
            originalOut = null;
        }
    }

    @Test
    public void getResponse_validCommand_returnsNonErrorResponse() {
        Peter peter = new Peter(tempDir.resolve("data.txt").toString());

        Peter.Response response = peter.getResponse("todo read book");

        assertFalse(response.isError());
        assertTrue(response.text().contains("read book"));
    }

    @Test
    public void getResponse_unrecognisedCommand_returnsErrorResponse() {
        Peter peter = new Peter(tempDir.resolve("data.txt").toString());

        Peter.Response response = peter.getResponse("fly to the moon");

        assertTrue(response.isError());
    }

    @Test
    public void getResponse_missingArguments_returnsErrorResponse() {
        Peter peter = new Peter(tempDir.resolve("data.txt").toString());

        Peter.Response response = peter.getResponse("todo");

        assertTrue(response.isError());
    }

    @Test
    public void isLoadingError_freshDataFile_isFalse() {
        Peter peter = new Peter(tempDir.resolve("data.txt").toString());

        assertFalse(peter.isLoadingError());
    }

    @Test
    public void isLoadingError_dataFileIsActuallyADirectory_isTrueAndStartsWithEmptyList() throws Exception {
        Path dataAsDirectory = tempDir.resolve("data.txt");
        Files.createDirectory(dataAsDirectory);

        Peter peter = new Peter(dataAsDirectory.toString());

        assertTrue(peter.isLoadingError());
        // Falls back to an empty list rather than refusing to start.
        assertFalse(peter.getResponse("list").isError());
        assertTrue(peter.getResponse("list").text().contains("stuck to the web"));
    }

    @Test
    public void getResponse_byeCommand_setsIsExitTrue() {
        Peter peter = new Peter(tempDir.resolve("data.txt").toString());

        peter.getResponse("bye");

        assertTrue(peter.isExit());
    }

    @Test
    public void getResponse_byeCommand_returnsNonErrorGoodbyeMessage() {
        Peter peter = new Peter(tempDir.resolve("data.txt").toString());

        Peter.Response response = peter.getResponse("bye");

        assertFalse(response.isError());
        assertTrue(response.text().contains("web-swing"));
    }

    @Test
    public void isExit_beforeByeCommand_isFalse() {
        Peter peter = new Peter(tempDir.resolve("data.txt").toString());

        peter.getResponse("todo read book");

        assertFalse(peter.isExit());
    }

    @Test
    public void run_scriptedCliSessionEndingInBye_printsResponsesAndExits() {
        originalIn = System.in;
        originalOut = System.out;
        System.setIn(new ByteArrayInputStream(
                "todo read book\nbye\n".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));

        Peter peter = new Peter(tempDir.resolve("data.txt").toString());
        peter.run();

        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Your friendly neighbourhood Peter"));
        assertTrue(output.contains("read book"));
        assertTrue(output.contains("Catch you on the web-swing"));
        assertTrue(peter.isExit());
    }
}

package peter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PeterRunTest {
    @TempDir
    private Path tempDir;

    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    public void redirectSystemStreams() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreSystemStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void run_blankAndInvalidByeThenValidBye_onlyValidByeExits() {
        String commands = "\nbye extra\nbye\n";
        System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
        Peter peter = new Peter(tempDir.resolve("tasks.txt").toString());

        peter.run();

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("doesn't take extra parameters"));
        assertTrue(output.contains("Journey paused"));
    }

    @Test
    public void run_damagedStorage_printsWarningBeforeWelcome() throws IOException {
        Path dataFile = tempDir.resolve("damaged.txt");
        java.nio.file.Files.writeString(dataFile, "damaged entry\n");
        System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
        Peter peter = new Peter(dataFile.toString());

        peter.run();

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.indexOf("[Log warning]") < output.indexOf("Welcome aboard"));
    }
}

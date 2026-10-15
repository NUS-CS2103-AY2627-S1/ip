package orangutan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class OrangutanTest {
    private static final String WELCOME_MESSAGE =
            "Greetings, I am Orangutan. In what manner may I be of service today?";

    @TempDir
    public Path temporaryFolder;

    @Test
    public void getResponse_commandSequence_updatesStateAndPersistsList() throws IOException {
        Path filePath = temporaryFolder.resolve("orangutan.txt");
        Orangutan orangutan = new Orangutan(filePath.toString(), true);

        assertFalse(orangutan.isRun());
        assertEquals(WELCOME_MESSAGE, orangutan.getResponse("init"));
        assertTrue(orangutan.isRun());
        assertEquals("Hark! A task hath been added to your list:\n [T] [ ] read book",
                orangutan.getResponse("todo read book"));
        assertEquals("Fare thee well, and may we meet again.", orangutan.getResponse("bye"));

        assertFalse(orangutan.isRun());
        assertEquals("NONE\n[T],false,read book", Files.readString(filePath));
    }

    @Test
    public void getResponse_initWithSavedData_loadsList() throws IOException {
        Path filePath = temporaryFolder.resolve("orangutan.txt");
        Files.writeString(filePath, "NONE\n[T],true,read book\n[D],false,submit report,20260901 1700");
        Orangutan orangutan = new Orangutan(filePath.toString(), true);

        assertEquals(WELCOME_MESSAGE, orangutan.getResponse("init"));

        assertEquals("Herein lie the undertakings in your list:\n"
                        + "1. [T] [X] read book\n"
                        + "2. [D] [ ] submit report (by 2026-09-01 17:00)",
                orangutan.getResponse("list"));
    }

    @Test
    public void getResponse_unknownCommand_returnsErrorMessage() {
        Orangutan orangutan = new Orangutan(temporaryFolder.resolve("orangutan.txt").toString(), true);
        orangutan.getResponse("init");

        String response = orangutan.getResponse("dance");

        assertTrue(response.startsWith("Alas! My simian mind is unable to comprehend your instructions."));
    }

    @Test
    public void run_cliSession_processesInputUntilBye() throws IOException {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Path filePath = temporaryFolder.resolve("orangutan.txt");
        try {
            System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            Orangutan orangutan = new Orangutan(filePath.toString(), false);
            orangutan.run();

            assertFalse(orangutan.isRun());
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String printed = output.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("Greetings, I am Orangutan."));
        assertTrue(printed.contains("Fare thee well, and may we meet again."));
        assertEquals("NONE\n", Files.readString(filePath));
    }
}

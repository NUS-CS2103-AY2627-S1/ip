package matthew;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests a complete application session through the public run method. */
class MatthewTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void runProcessesCommandsUntilBye() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("todo read book\nbye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            new Matthew(temporaryDirectory.resolve("tasks.txt").toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String session = output.toString(StandardCharsets.UTF_8);
        assertTrue(session.contains("Hello! I'm Matthew."));
        assertTrue(session.contains("I've added this task"));
        assertTrue(session.contains("Goodbye! Have a nice day!"));
    }
}

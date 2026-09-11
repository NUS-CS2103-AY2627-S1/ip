package lynn;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LynnTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void emptyCommand_showsActionableErrorWithoutChangingState() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(output, true, StandardCharsets.UTF_8));
        Lynn lynn = new Lynn(ui, new Storage(temporaryDirectory.resolve("data/lynn.txt")));

        assertFalse(lynn.respondTo("   "));
        assertTrue(output.toString(StandardCharsets.UTF_8)
                .contains("Please enter a command. Try help to see what I can do."));
    }

    @Test
    void exactCommands_areCaseInsensitive() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(output, true, StandardCharsets.UTF_8));
        Lynn lynn = new Lynn(ui, new Storage(temporaryDirectory.resolve("data/lynn.txt")));

        assertFalse(lynn.respondTo("HELP"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Here are Lynn's commands:"));
    }
}

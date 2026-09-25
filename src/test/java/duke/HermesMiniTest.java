package duke;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command processing used by both the CLI and JavaFX interfaces. */
public class HermesMiniTest {

    @TempDir
    private Path temporaryDirectory;

    private HermesMini hermes;

    @BeforeEach
    public void setUp() {
        hermes = new HermesMini(new Storage(temporaryDirectory.resolve("tasks.txt")), new Parser());
    }

    @Test
    public void getResponse_taskWorkflow_updatesDisplayedList() {
        assertTrue(hermes.getResponse("todo read book").contains("read book"));
        assertTrue(hermes.getResponse("mark 1").contains("now done"));
        assertTrue(hermes.getResponse("list").contains("[T][X] read book"));
        assertTrue(hermes.getResponse("delete 1").contains("0 tasks"));
    }

    @Test
    public void getResponse_invalidCommands_reportsErrorsWithoutExiting() {
        assertTrue(hermes.getResponse("deadline invalid").startsWith("DELIVERY FAILED"));
        assertTrue(hermes.getResponse("mark abc").startsWith("DELIVERY FAILED"));
        assertTrue(hermes.getResponse("unknown").startsWith("DELIVERY FAILED"));
        assertFalse(hermes.shouldExit());
    }

    @Test
    public void getResponse_bye_requestsExit() {
        assertTrue(hermes.getResponse("bye").startsWith("Farewell"));
        assertTrue(hermes.shouldExit());
    }
}

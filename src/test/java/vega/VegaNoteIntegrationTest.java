package vega;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class VegaNoteIntegrationTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void noteCommands_addRestartListAndDelete_persistNotes() {
        String taskFile = temporaryDirectory.resolve("tasks.txt").toString();
        Vega firstSession = new Vega(taskFile);

        assertTrue(firstSession.getResponse("note room code is 1234").contains("recorded this note"));
        assertTrue(firstSession.getResponse("notes").contains("1. room code is 1234"));

        Vega restartedSession = new Vega(taskFile);
        assertTrue(restartedSession.getResponse("notes").contains("1. room code is 1234"));
        assertTrue(restartedSession.getResponse("delete-note 1").contains("removed this note"));
    }
}

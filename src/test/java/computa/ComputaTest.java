package computa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ComputaTest {
    @TempDir Path directory;

    @Test void validCommandProducesResponse() {
        Computa computa = new Computa(directory.resolve("tasks.txt").toString());
        assertTrue(computa.processCommand("todo study").contains("Added"));
        assertFalse(computa.lastCommandHadError());
    }

    @Test void invalidCommandIsReportedAsError() {
        Computa computa = new Computa(directory.resolve("tasks.txt").toString());
        assertTrue(computa.processCommand("not a command").contains("Unknown command"));
        assertTrue(computa.lastCommandHadError());
    }

    @Test void invalidDateIsReportedAsError() {
        Computa computa = new Computa(directory.resolve("tasks.txt").toString());
        computa.processCommand("deadline report / wrong");
        assertTrue(computa.lastCommandHadError());
    }
}

package chim;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ChimTest {

    @TempDir
    Path tempDir;

    @Test
    public void getResponse_validCommand_returnsNonErrorResponse() {
        Chim chim = new Chim(tempDir.resolve("chim.txt").toString());
        chim.getResponse("todo read book");
        assertFalse(chim.isLastResponseError());
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorResponse() {
        Chim chim = new Chim(tempDir.resolve("chim.txt").toString());
        chim.getResponse("gibberish");
        assertTrue(chim.isLastResponseError());
    }

    @Test
    public void isExit_byeCommand_returnsTrue() {
        Chim chim = new Chim(tempDir.resolve("chim.txt").toString());
        assertTrue(chim.isExit("bye"));
    }

    @Test
    public void isExit_otherCommand_returnsFalse() {
        Chim chim = new Chim(tempDir.resolve("chim.txt").toString());
        assertFalse(chim.isExit("list"));
    }

    @Test
    public void getResponse_dataPersistsAcrossInstances() {
        String path = tempDir.resolve("chim.txt").toString();
        Chim chim1 = new Chim(path);
        chim1.getResponse("todo read book");

        Chim chim2 = new Chim(path);
        String response = chim2.getResponse("list");
        assertTrue(response.contains("read book"));
    }
}

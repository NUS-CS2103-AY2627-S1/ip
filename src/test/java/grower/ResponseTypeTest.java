package grower;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import grower.ui.ResponseType;
import grower.ui.Ui;

/**
 * Verifies alert classification and that each command starts with a clean status.
 */
public class ResponseTypeTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_errorThenSuccess_resetsAttentionLevel() {
        Grower grower = new Grower(temporaryDirectory.resolve("tasks.txt").toString());
        grower.getResponse("dance");
        assertEquals(ResponseType.ERROR, grower.getResponseType());
        grower.getResponse("todo Error Warning No results!!!!");
        assertEquals(ResponseType.NORMAL, grower.getResponseType());
    }

    @Test
    public void getResponse_noMatchesThenMatch_clearsWarning() {
        Grower grower = new Grower(temporaryDirectory.resolve("tasks.txt").toString());
        grower.getResponse("find missing");
        assertEquals(ResponseType.WARNING, grower.getResponseType());
        grower.getResponse("todo missing book");
        grower.getResponse("find missing");
        assertEquals(ResponseType.NORMAL, grower.getResponseType());
    }

    @Test
    public void getResponse_loadFailure_blocksChangesWithError() {
        Grower grower = new Grower(temporaryDirectory.toString());
        grower.getResponse("todo new task");
        assertEquals(ResponseType.ERROR, grower.getResponseType());
    }

    @Test
    public void showWarning_existingError_preservesErrorUntilCleared() {
        Ui ui = new Ui();
        ui.showError("Unable to save");
        ui.showWarning("Check your tasks");
        assertEquals(ResponseType.ERROR, ui.getResponseType());
        ui.clearOutput();
        assertEquals(ResponseType.NORMAL, ui.getResponseType());
        assertEquals("", ui.getOutput());
        ui.showSearchResults(List.of());
        assertEquals(ResponseType.WARNING, ui.getResponseType());
    }
}

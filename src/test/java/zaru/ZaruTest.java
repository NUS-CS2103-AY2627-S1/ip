package zaru;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zaru.parser.Response;

/** Tests application-level handling of failures outside individual commands. */
public class ZaruTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies that a startup storage failure is shown and does not permit overwriting saved data. */
    @Test
    public void getResponse_invalidSavePath_returnsPersistentError() {
        Zaru zaru = new Zaru(temporaryDirectory);

        Response response = zaru.getResponse("todo should not be saved");

        assertEquals(Response.ResponseType.ERROR, response.getType());
        assertTrue(response.getText().contains("could not load the save file safely"));
        assertTrue(response.getText().contains("Fix the file or its permissions"));
    }
}

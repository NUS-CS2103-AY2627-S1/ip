package meowmeow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import meowmeow.ui.MessageStyle;
import meowmeow.ui.Ui;

/**
 * Tests for {@link Response} and {@link ResponseKind} - specifically that
 * {@link Meowmeow#getResponse(String)} tags each reply with the right kind,
 * since that is the seam the GUI uses to style errors and to know when to
 * close the window on "bye".
 */
public class ResponseTest {

    private static final long SEED = 7L;

    private Meowmeow newMeowmeow(Path tempDir) {
        Ui ui = new Ui(MessageStyle.PLAIN, new Random(SEED));
        return new Meowmeow(ui, tempDir.toString(), "tasks.txt");
    }

    @Test
    public void getResponse_byeCommand_taggedExit(@TempDir Path tempDir) {
        List<Response> responses = newMeowmeow(tempDir).getResponse("bye");

        assertEquals(1, responses.size());
        assertEquals(ResponseKind.EXIT, responses.get(0).kind());
    }

    @Test
    public void getResponse_addCommand_taggedNormal(@TempDir Path tempDir) {
        List<Response> responses = newMeowmeow(tempDir).getResponse("todo borrow book");

        assertEquals(1, responses.size());
        assertEquals(ResponseKind.NORMAL, responses.get(0).kind());
        assertFalse(responses.get(0).text().isBlank());
    }

    @Test
    public void getResponse_unknownCommand_taggedError(@TempDir Path tempDir) {
        List<Response> responses = newMeowmeow(tempDir).getResponse("sing a song");

        assertEquals(1, responses.size());
        assertEquals(ResponseKind.ERROR, responses.get(0).kind());
    }

    @Test
    public void startupMessages_freshDataFolder_isJustTheWelcome(@TempDir Path tempDir) {
        List<Response> responses = newMeowmeow(tempDir).startupMessages();

        assertEquals(1, responses.size());
        assertEquals(ResponseKind.NORMAL, responses.get(0).kind());
    }
}

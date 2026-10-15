package james.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Verifies consistent message endings across graphical display modes.
 */
public class CommandResponseTest {
    @Test
    public void createResponse_trailingLineBreaks_preservesOnlyInternalLineBreaks() {
        for (String ending : new String[]{"", "\n", "\r\n", "\n\n", "\r"}) {
            String message = "First line\nSecond line" + ending;
            assertEquals("First line\nSecond line",
                    new CommandResponse(message, CommandResponse.Type.NORMAL, false).getMessage());
            assertEquals("First line\nSecond line",
                    CommandResponse.createWithSticker(message, CommandResponse.Type.ADD, Sticker.YATTA).getMessage());
            assertEquals("First line\nSecond line",
                    CommandResponse.createStickerOnly(message, Sticker.YATTA).getMessage());
        }
    }

    @Test
    public void createResponse_emptyOrWhitespaceText_preservesNonNewlineCharacters() {
        assertEquals("", new CommandResponse("\n\r\n", CommandResponse.Type.NORMAL, false).getMessage());
        assertEquals("", new CommandResponse("", CommandResponse.Type.NORMAL, false).getMessage());
        assertEquals(" text ", new CommandResponse(" text \n", CommandResponse.Type.NORMAL, false).getMessage());
    }
}

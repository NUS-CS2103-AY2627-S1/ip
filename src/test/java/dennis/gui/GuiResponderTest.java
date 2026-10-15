package dennis.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link GuiResponder#isLastResponseError()}, which the GUI uses to
 * decide whether to render a reply as an error bubble.
 *
 * <p>Every case uses {@code list}, the one command guaranteed not to touch
 * {@code Storage#save}: {@link GuiResponder} owns a real {@link
 * dennis.storage.Storage} pointed at the actual save file, and a command
 * such as {@code todo} would overwrite it as a side effect of running this
 * test.</p>
 */
public class GuiResponderTest {

    @Test
    public void isLastResponseError_wellFormedCommand_isFalse() {
        GuiResponder responder = new GuiResponder();
        responder.getResponse("list");
        assertFalse(responder.isLastResponseError());
    }

    @Test
    public void isLastResponseError_unrecognisedCommand_isTrue() {
        GuiResponder responder = new GuiResponder();
        responder.getResponse("sing");
        assertTrue(responder.isLastResponseError());
    }

    @Test
    public void isLastResponseError_goodCommandAfterBadOne_resetsToFalse() {
        GuiResponder responder = new GuiResponder();
        responder.getResponse("sing");
        responder.getResponse("list");
        assertFalse(responder.isLastResponseError());
    }
}

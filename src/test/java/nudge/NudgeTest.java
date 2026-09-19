package nudge;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests command responses produced by {@link Nudge}.
 */
class NudgeTest {
    @Test
    void getResponse_invalidCommand_returnsErrorResponse() {
        NudgeResponse response = new Nudge().getResponse("not-a-command");

        assertTrue(response.isError());
    }

    @Test
    void getResponse_validCommand_returnsNormalResponse() {
        NudgeResponse response = new Nudge().getResponse("list");

        assertFalse(response.isError());
    }
}

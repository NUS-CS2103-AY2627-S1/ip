package axiom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AxiomReplyTest {
    @Test
    void success_withMessage_isNotError() {
        AxiomReply reply = AxiomReply.success("Got it.");
        assertEquals("Got it.", reply.getMessage());
        assertFalse(reply.isError());
    }

    @Test
    void error_withMessage_isError() {
        AxiomReply reply = AxiomReply.error("Sorry, I don't understand that command.");
        assertEquals("Sorry, I don't understand that command.", reply.getMessage());
        assertTrue(reply.isError());
    }
}

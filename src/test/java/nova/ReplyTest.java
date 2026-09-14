package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ReplyTest {

    @Test
    public void of_anyText_isNotAnError() {
        Reply reply = Reply.of("Added.");

        assertEquals("Added.", reply.text());
        assertFalse(reply.isError());
    }

    @Test
    public void error_anyText_isAnError() {
        Reply reply = Reply.error("That is not a number.");

        assertEquals("That is not a number.", reply.text());
        assertTrue(reply.isError());
    }

    @Test
    public void isEmpty_reflectsWhetherThereIsAnythingToSay() {
        assertTrue(Reply.of("").isEmpty());
        assertFalse(Reply.of("Added.").isEmpty());
    }
}

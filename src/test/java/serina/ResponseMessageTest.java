package serina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests typed response messages. */
public class ResponseMessageTest {
    @Test
    public void factories_validText_assignExpectedTypes() {
        assertEquals(ResponseType.NORMAL, ResponseMessage.normal("normal").getType());
        assertEquals(ResponseType.ERROR, ResponseMessage.error("error").getType());
        assertEquals(ResponseType.WARNING, ResponseMessage.warning("warning").getType());
        assertEquals("normal", ResponseMessage.normal("normal").getText());
    }

    @Test
    public void constructor_nullValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ResponseMessage(null, ResponseType.NORMAL));
        assertThrows(NullPointerException.class, () -> new ResponseMessage("text", null));
    }
}

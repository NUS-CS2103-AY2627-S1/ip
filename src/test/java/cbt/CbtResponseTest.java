package cbt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the immutable response passed from the application to the GUI. */
public class CbtResponseTest {
    /** Verifies record accessors preserve successful and error response values. */
    @Test
    public void accessors_successAndErrorValues_sameValuesReturned() {
        CbtResponse success = new CbtResponse("task added", false);
        CbtResponse error = new CbtResponse("bad command", true);

        assertEquals("task added", success.message());
        assertFalse(success.isError());
        assertEquals("bad command", error.message());
        assertTrue(error.isError());
    }
}

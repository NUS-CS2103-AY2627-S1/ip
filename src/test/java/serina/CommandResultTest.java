package serina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests command response aggregation and immutability. */
public class CommandResultTest {
    @Test
    public void constructors_validResponses_preserveOrderAndExitFlag() {
        CommandResult listResult = new CommandResult(new ArrayList<>(List.of("first", "second")), false);
        CommandResult varargsResult = new CommandResult(true, "done");

        assertEquals(List.of("first", "second"), listResult.getResponses());
        assertFalse(listResult.shouldExit());
        assertEquals(ResponseType.NORMAL, listResult.getMessages().get(0).getType());
        assertEquals(List.of("done"), varargsResult.getResponses());
        assertTrue(varargsResult.shouldExit());
    }

    @Test
    public void messageCollections_returnedToCaller_areUnmodifiable() {
        CommandResult result = CommandResult.ofMessages(false, ResponseMessage.warning("warning"));

        assertThrows(UnsupportedOperationException.class, () ->
                result.getMessages().add(ResponseMessage.normal("extra")));
        assertThrows(UnsupportedOperationException.class, () -> result.getResponses().add("extra"));
    }
}

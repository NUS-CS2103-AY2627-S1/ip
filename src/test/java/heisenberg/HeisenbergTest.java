package heisenberg;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests response state used by the GUI to distinguish normal replies from errors. */
public class HeisenbergTest {
    @Test
    public void getResponse_invalidCommand_marksResponseAsError() {
        Heisenberg heisenberg = new Heisenberg();

        heisenberg.getResponse("not-a-command");

        assertTrue(heisenberg.wasLastResponseAnError());
    }

    @Test
    public void getResponse_validCommand_marksResponseAsNormal() {
        Heisenberg heisenberg = new Heisenberg();

        heisenberg.getResponse("list");

        assertFalse(heisenberg.wasLastResponseAnError());
    }
}

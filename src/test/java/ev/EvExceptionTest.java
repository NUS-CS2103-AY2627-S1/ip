package ev;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EvExceptionTest {

    @Test
    public void getMessage_oneLine_returnedAsGiven() {
        assertEquals("Which task?", new EvException("Which task?").getMessage());
    }

    @Test
    public void getMessage_severalLines_joinedByLineBreaks() {
        EvException thrown = new EvException("Which task?", "e.g. mark 2");
        assertEquals("Which task?\ne.g. mark 2", thrown.getMessage());
    }

    @Test
    public void getMessage_noLines_empty() {
        assertEquals("", new EvException().getMessage());
    }
}

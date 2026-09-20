package steph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link StephException}. */
public class StephExceptionTest {

    @Test
    public void getMessage_returnsMessagePassedToConstructor() {
        assertEquals("Please try again", new StephException("Please try again").getMessage());
    }
}

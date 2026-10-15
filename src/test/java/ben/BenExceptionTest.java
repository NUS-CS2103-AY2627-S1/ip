package ben;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests for {@link BenException}'s message formatting. */
class BenExceptionTest {
    @Test
    void getMessage_prefixesGivenTextWithOops() {
        assertEquals("OOPS!!! something went wrong", new BenException("something went wrong").getMessage());
    }
}

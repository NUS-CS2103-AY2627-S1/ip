package nubish.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests command argument token values.
 */
public class ArgumentTokenTest {
    /**
     * Verifies that the deadline token text is exposed correctly.
     */
    @Test
    public void getToken_byToken_returnsByLiteral() {
        assertEquals("/by", ArgumentToken.BY.getToken());
    }

    /**
     * Verifies that the event start token text is exposed correctly.
     */
    @Test
    public void getToken_fromToken_returnsFromLiteral() {
        assertEquals("/from", ArgumentToken.FROM.getToken());
    }

    /**
     * Verifies that the event end token text is exposed correctly.
     */
    @Test
    public void getToken_toToken_returnsToLiteral() {
        assertEquals("/to", ArgumentToken.TO.getToken());
    }
}

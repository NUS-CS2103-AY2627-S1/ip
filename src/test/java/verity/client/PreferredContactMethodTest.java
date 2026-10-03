package verity.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import verity.exception.VerityException;

/**
 * Tests preferred-contact parsing and display values.
 */
class PreferredContactMethodTest {
    @Test
    void parse_emptyValue_returnsNull() throws VerityException {
        assertNull(PreferredContactMethod.parse(""));
    }

    @Test
    void parse_supportedValues_ignoresCase() throws VerityException {
        assertEquals(
                PreferredContactMethod.PHONE,
                PreferredContactMethod.parse("PhOnE"));
        assertEquals("other", PreferredContactMethod.OTHER.getValue());
    }

    @Test
    void parse_unsupportedValue_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> PreferredContactMethod.parse("post"));

        assertEquals(
                "Preferred contact must be phone, email, or other.",
                exception.getMessage());
    }
}

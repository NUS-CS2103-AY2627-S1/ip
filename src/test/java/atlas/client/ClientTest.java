package atlas.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the stored fields, the display form and the matching rule of a client. */
public class ClientTest {

    @Test
    void displaysOnlyTheFieldsThatAreKnown() {
        assertEquals("[C] Bob", new Client("Bob").toString());
        assertEquals("[C] Bob (phone: 91234567)", new Client("Bob", "91234567", "").toString());
        assertEquals("[C] Bob (email: b@x.com)", new Client("Bob", "", "b@x.com").toString());
        assertEquals("[C] Bob (phone: 91234567) (email: b@x.com)",
                new Client("Bob", "91234567", "b@x.com").toString());
    }

    @Test
    void reportsWhichOptionalFieldsAreKnown() {
        Client full = new Client("Bob", "91234567", "b@x.com");
        Client bare = new Client("Bob");

        assertTrue(full.hasPhone());
        assertTrue(full.hasEmail());
        assertFalse(bare.hasPhone());
        assertFalse(bare.hasEmail());
    }

    @Test
    void matchesCaseSensitivelyOnEveryField() {
        Client client = new Client("Bob", "91234567", "robert@example.com");

        assertTrue(client.matches("Bob"));
        assertTrue(client.matches("9123"));
        assertTrue(client.matches("example.com"));
        assertFalse(client.matches("bob"));
        assertFalse(client.matches("9124"));
        assertFalse(client.matches("Robert"));
    }
}

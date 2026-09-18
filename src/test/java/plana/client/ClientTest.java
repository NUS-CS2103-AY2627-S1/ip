package plana.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests client values, summaries, searching, and serialization.
 */
class ClientTest {
    @Test
    void client_valuesAreNormalizedAndReturned() {
        Client client = new Client(" Alice ", "ALICE@EXAMPLE.COM", " 91234567 ",
                " 12 Baker Street ", " no nuts ", " birthday cake ");

        assertEquals("Alice", client.getName());
        assertEquals("alice@example.com", client.getEmail());
        assertEquals("91234567", client.getPhone());
        assertEquals("12 Baker Street", client.getAddress());
        assertEquals("no nuts", client.getPreferences());
        assertEquals("birthday cake", client.getNotes());
    }

    @Test
    void client_summarySearchAndStorage_specialCharactersHandled() {
        Client client = new Client("Alice | Tan", "alice@example.com", "", "", "no nuts", "use \\ sign");

        assertEquals("Alice | Tan <alice@example.com>", client.getSummary());
        assertTrue(client.matchesKeyword("ALICE"));
        assertTrue(client.matchesKeyword("nuts"));
        assertEquals("C | alice@example.com | Alice \\| Tan |  |  | no nuts | use \\\\ sign",
                client.toStorageString());
    }

    @Test
    void matchesKeyword_allOptionalFieldsAndNoMatch_areHandledCaseInsensitively() {
        Client client = new Client("Alice", "alice@example.com", "91234567",
                "12 Baker Street", "no nuts", "birthday cake");

        assertTrue(client.matchesKeyword("91234567"));
        assertTrue(client.matchesKeyword("BAKER"));
        assertTrue(client.matchesKeyword("BIRTHDAY"));
        assertTrue(client.matchesKeyword(""));
        assertFalse(client.matchesKeyword("photography"));
    }

    @Test
    void nullOptionalFields_areNormalizedToEmptyStrings() {
        Client client = new Client("Alice", "alice@example.com", null, null, null, null);

        assertEquals("", client.getPhone());
        assertEquals("", client.getAddress());
        assertEquals("", client.getPreferences());
        assertEquals("", client.getNotes());
    }

    @Test
    void constructor_missingRequiredField_assertionFails() {
        assertThrows(AssertionError.class, () ->
                new Client(null, "alice@example.com", "", "", "", ""));
        assertThrows(AssertionError.class, () ->
                new Client("Alice", "", "", "", "", ""));
    }
}

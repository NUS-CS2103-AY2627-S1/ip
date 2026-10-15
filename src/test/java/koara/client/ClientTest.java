package koara.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests client display, storage, and searching behavior.
 */
public class ClientTest {

    @Test
    public void clientRepresentations_validDetails_returnExpectedFormats() {
        Client client = new Client("Alex Tan", "91234567", "Run 5 km", "Knee injury");

        assertTrue(client.containsKeyword("alex"));
        assertTrue(client.containsKeyword("KNEE"));
        assertFalse(client.containsKeyword("swimming"));
        assertEquals("Alex Tan | Phone: 91234567 | Goal: Run 5 km | Notes: Knee injury",
                client.toString());
        assertEquals("Alex Tan\t91234567\tRun 5 km\tKnee injury", client.toDataString());
    }

    @Test
    public void hasSameIdentity_matchingNameOrNormalizedPhone_returnsTrue() {
        Client client = new Client("Alex Tan", "+65 91234567", "Run", "Healthy");

        assertTrue(client.hasSameIdentity(new Client(
                "alex tan", "80000000", "Swim", "Healthy")));
        assertTrue(client.hasSameIdentity(new Client(
                "Beth Lee", "+6591234567", "Swim", "Healthy")));
        assertFalse(client.hasSameIdentity(new Client(
                "Beth Lee", "80000000", "Swim", "Healthy")));
    }

    @Test
    public void isValidPhone_variedFormats_validatesActualDigitCount() {
        assertTrue(Client.isValidPhone("91234567"));
        assertTrue(Client.isValidPhone("+65 9123 4567"));
        assertFalse(Client.isValidPhone("1 2 3 4"));
        assertFalse(Client.isValidPhone("+       "));
        assertFalse(Client.isValidPhone("9123abcd"));
        assertFalse(Client.isValidPhone("12345678901234567"));
        assertFalse(Client.isValidPhone(null));
    }

    @Test
    public void constructorAndSearch_invalidInternalArguments_throwAssertionError() {
        assertThrows(AssertionError.class, () -> new Client("", "91234567", "Run", "Healthy"));
        assertThrows(AssertionError.class, () -> new Client("Alex", "", "Run", "Healthy"));
        assertThrows(AssertionError.class, () -> new Client("Alex", "1 2 3 4", "Run", "Healthy"));
        assertThrows(AssertionError.class, () -> new Client("Alex", "91234567", "", "Healthy"));
        assertThrows(AssertionError.class, () -> new Client("Alex", "91234567", "Run", ""));
        Client client = new Client("Alex", "91234567", "Run", "Healthy");
        assertThrows(AssertionError.class, () -> client.containsKeyword(" "));
        assertThrows(AssertionError.class, () -> client.hasSameIdentity(null));
    }
}

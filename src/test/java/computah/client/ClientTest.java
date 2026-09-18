package computah.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import computah.exception.ComputahException;

/**
 * Tests client validation, display, and serialization behavior.
 */
public class ClientTest {
    @Test
    public void toString_missingContactDetails_showsPlaceholders() throws ComputahException {
        Client client = new Client("Alice Tan", "", "");

        assertEquals("Alice Tan [phone: -] [email: -]", client.toString());
    }

    @Test
    public void toFileString_allFields_returnsStorageRepresentation() throws ComputahException {
        Client client = new Client("Alice Tan", "+65 9123 4567", "alice@example.com");

        assertEquals("C | Alice Tan | +65 9123 4567 | alice@example.com", client.toFileString());
    }

    @Test
    public void update_emptyPhone_clearsPhone() throws ComputahException {
        Client client = new Client("Alice Tan", "123", "alice@example.com");

        client.update(null, "", null);

        assertEquals("Alice Tan [phone: -] [email: alice@example.com]", client.toString());
    }

    @Test
    public void constructor_blankName_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                new Client("  ", "", ""));

        assertEquals("The name of a client cannot be empty.", exception.getMessage());
    }

    @Test
    public void constructor_storageDelimiter_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                new Client("Alice | Tan", "", ""));

        assertEquals("Client details cannot contain \" | \".", exception.getMessage());
    }

    @Test
    public void update_invalidLaterField_doesNotChangeEarlierField() throws ComputahException {
        Client client = new Client("Alice Tan", "123", "alice@example.com");

        assertThrows(ComputahException.class, () -> client.update("Alice Lim", "456 | 789", null));

        assertEquals("Alice Tan [phone: 123] [email: alice@example.com]", client.toString());
    }

    @Test
    public void constructor_phoneWithLetters_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                new Client("Alice Tan", "call-me", ""));

        assertEquals("The phone of a client may contain only digits, spaces, +, -, and parentheses.",
                exception.getMessage());
    }

    @Test
    public void constructor_malformedEmail_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                new Client("Alice Tan", "", "alice-at-example.com"));

        assertEquals("The email of a client must be in the format name@example.com.", exception.getMessage());
    }

    @Test
    public void constructor_nameWithoutLetterOrNumber_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                new Client("---", "", ""));

        assertEquals("The name of a client must contain a letter or number.", exception.getMessage());
    }
}

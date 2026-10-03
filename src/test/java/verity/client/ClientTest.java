package verity.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import verity.exception.VerityException;

/**
 * Tests client validation, matching, IDs, and serialization.
 */
class ClientTest {
    @Test
    void constructor_validFields_createsClient() throws VerityException {
        Client client = new Client(
                1,
                "Alice Tan",
                "+65 9123 4567",
                "alice@example.com",
                "1 Main Street",
                "Acme",
                "Called at 9am\nRequested a quote",
                PreferredContactMethod.EMAIL);

        assertEquals("C001", client.getId());
        assertEquals(1, client.getNumericId());
        assertEquals("Alice Tan", client.getFullName());
        assertEquals("+65 9123 4567", client.getPhone());
        assertEquals("alice@example.com", client.getEmail());
        assertEquals("1 Main Street", client.getAddress());
        assertEquals("Acme", client.getCompany());
        assertEquals("Called at 9am\nRequested a quote", client.getNotes());
        assertEquals(
                PreferredContactMethod.EMAIL,
                client.getPreferredContactMethod());
        assertTrue(client.matchesName("aLi"));
        assertEquals(
                "C\tC001\tAlice Tan\t+65 9123 4567\talice@example.com"
                        + "\t1 Main Street\tAcme\tCalled at 9am\\nRequested a quote\temail",
                client.serialize());
    }

    @Test
    void constructor_nameWithPunctuation_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> createClient("Alice-Tan", "", ""));

        assertEquals(
                "Client names may contain letters and spaces only.",
                exception.getMessage());
    }

    @Test
    void constructor_invalidPhone_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> createClient("Alice Tan", "call-me", ""));
    }

    @Test
    void constructor_invalidEmail_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> createClient("Alice Tan", "", "alice.example.com"));
    }

    @Test
    void constructor_boundaryWhitespace_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> createClient("Alice Tan ", "", ""));

        assertEquals(
                "Client name cannot start or end with whitespace.",
                exception.getMessage());
    }

    @Test
    void constructor_addressWithTab_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> new Client(
                        1, "Alice Tan", "", "", "1 Main\tStreet",
                        "", "", null));
    }

    @Test
    void constructor_notesWithNewline_acceptsNotes() throws VerityException {
        Client client = new Client(
                1, "Alice Tan", "", "", "", "", "First\nSecond", null);

        assertEquals("First\nSecond", client.getNotes());
    }

    @Test
    void constructor_notesWithTab_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> new Client(
                        1, "Alice Tan", "", "", "", "",
                        "First\tSecond", null));
    }

    @Test
    void constructor_overlongName_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> createClient("A".repeat(101), "", ""));
    }

    @Test
    void parseId_mixedCase_returnsNumericId() throws VerityException {
        assertEquals(12, Client.parseId("c012"));
    }

    @Test
    void constructor_nonPositiveId_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> new Client(0, "Alice Tan", "", "", "", "", "", null));

        assertEquals("client ID must be positive.", exception.getMessage());
    }

    @Test
    void constructor_emptyName_throwsVerityException() {
        assertClientFailure("", "", "", "", "", "",
                "A client name is required.");
    }

    @Test
    void constructor_phoneOutsideLengthLimits_throwsVerityException() {
        assertClientFailure("Alice Tan", "12", "", "", "", "",
                "Phone numbers must contain 3 to 32 characters.");
        assertClientFailure("Alice Tan", "1".repeat(33), "", "", "", "",
                "Phone numbers must contain 3 to 32 characters.");
    }

    @Test
    void constructor_emailOutsideFormatLimits_throwsVerityException() {
        String expectedMessage = "The email address format is invalid.";
        assertClientFailure("Alice Tan", "", "a@", "", "", "", expectedMessage);
        assertClientFailure(
                "Alice Tan", "", "a@b " + "x".repeat(250), "", "", "",
                expectedMessage);
        assertClientFailure(
                "Alice Tan", "", "alice @example.com", "", "", "",
                expectedMessage);
        assertClientFailure(
                "Alice Tan", "", "a@b@example.com", "", "", "",
                expectedMessage);
    }

    @Test
    void constructor_overlongSingleLineFields_throwsVerityException() {
        assertClientFailure(
                "Alice Tan", "", "", "A".repeat(201), "", "",
                "Address cannot exceed 200 characters.");
        assertClientFailure(
                "Alice Tan", "", "", "", "A".repeat(101), "",
                "Company cannot exceed 100 characters.");
    }

    @Test
    void constructor_newlineInSingleLineField_throwsVerityException() {
        assertClientFailure(
                "Alice Tan", "", "", "", "Acme\nLtd", "",
                "Company cannot contain tabs or newlines.");
    }

    @Test
    void constructor_overlongNotes_throwsVerityException() {
        assertClientFailure(
                "Alice Tan", "", "", "", "", "A".repeat(2001),
                "Notes cannot exceed 2000 characters.");
    }

    @Test
    void parseId_invalidFormats_throwsVerityException() {
        String[] invalidIds = {"001", "C01", "C000", "C12A", "C999999999999"};

        for (String invalidId : invalidIds) {
            VerityException exception = assertThrows(
                    VerityException.class,
                    () -> Client.parseId(invalidId));
            assertEquals(
                    "Client IDs must use the format C001.",
                    exception.getMessage());
        }
    }

    @Test
    void serialize_noPreferredContact_writesEmptyField() throws VerityException {
        Client client = createClient("Alice Tan", "", "");

        assertNull(client.getPreferredContactMethod());
        assertTrue(client.serialize().endsWith("\t"));
    }

    private void assertClientFailure(String name, String phone, String email,
            String address, String company, String notes, String expectedMessage) {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> new Client(
                        1, name, phone, email, address, company, notes, null));

        assertEquals(expectedMessage, exception.getMessage());
    }

    private Client createClient(String name, String phone, String email)
            throws VerityException {
        return new Client(1, name, phone, email, "", "", "", null);
    }
}

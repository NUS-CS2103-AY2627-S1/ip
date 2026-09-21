package friday.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests contact field validation and display without depending on the command parser. */
class ContactTest {
    @Test
    void constructor_surroundingSpaces_trimsFieldsAndPreservesInternalText() {
        Contact contact = new Contact("  Alice  Tan  ", " 91234567 ", " Alice@Example.com ");
        assertEquals("Alice  Tan", contact.getName());
        assertEquals("91234567", contact.getPhone());
        assertEquals("Alice@Example.com", contact.getEmail());
        assertEquals("alice  tan", contact.getNameKey());
        assertEquals("Alice  Tan (phone: 91234567, email: Alice@Example.com)", contact.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"60000000", "69999999", "80000000", "89999999", "90000000", "99999999"})
    void constructor_validPhoneOnly_acceptsSingaporeBoundaries(String phone) {
        Contact contact = new Contact("Alice", phone, "");
        assertEquals("Alice (phone: " + phone + ", email: -)", contact.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678", "70000000", "9123456", "912345678", "+6591234567",
            "9123 4567", "9123-4567", "(91234567)", "abcdefgh", "９１２３４５６７"})
    void constructor_invalidPhone_rejectsValue(String phone) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Contact("Alice", phone, "alice@example.com"));
        assertEquals("Invalid phone number. Use 8 digits starting with 6, 8, or 9.", error.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"alice@example.com", "Alice+work@sub.example.com", "a@b.c"})
    void constructor_validEmailOnly_acceptsValue(String email) {
        Contact contact = new Contact("Alice", "", email);
        assertEquals("Alice (phone: -, email: " + email + ")", contact.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"alice@", "@example.com", "alice@example", "alice@@example.com",
            "alice @example.com", "alice@example..com", "alice@.example.com", "alice@example.com."})
    void constructor_invalidEmail_rejectsValue(String email) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Contact("Alice", "91234567", email));
        assertEquals("Invalid email address. Use a format like alice@example.com.", error.getMessage());
    }

    @Test
    void constructor_missingFields_rejectsBeforePhoneAndEmailValidation() {
        assertEquals(Contact.INVALID_FORMAT_MESSAGE, assertThrows(IllegalArgumentException.class,
                () -> new Contact(" ", "bad", "bad")).getMessage());
        assertThrows(IllegalArgumentException.class, () -> new Contact("Alice", " ", " "));
        assertThrows(IllegalArgumentException.class, () -> new Contact(null, "91234567", ""));
        assertThrows(IllegalArgumentException.class, () -> new Contact("Alice", null, "a@b.c"));
        assertThrows(IllegalArgumentException.class, () -> new Contact("Alice", "91234567", null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\n", "\r", "\t", "\u001b", "\u007f", "\u0085"})
    void constructor_controlCharacters_rejectsEveryField(String control) {
        assertThrows(IllegalArgumentException.class, () -> new Contact("Alice" + control, "91234567", ""));
        assertThrows(IllegalArgumentException.class, () -> new Contact("Alice", "91234567" + control, ""));
        assertThrows(IllegalArgumentException.class, () -> new Contact("Alice", "", "a@b.c" + control));
    }

    @Test
    void contains_searchesIndividualFields_matchesLiteralTextIgnoringCase() {
        Contact contact = new Contact("Alice  Tan", "91234567", "Alice+Work@Example.com");
        assertTrue(contact.contains("ALICE"));
        assertTrue(contact.contains("2345"));
        assertTrue(contact.contains("+WORK@"));
        assertFalse(contact.contains(".*"));
        assertFalse(contact.contains("Alice Tan"));
        assertFalse(contact.contains("Tan9123"));
    }
}

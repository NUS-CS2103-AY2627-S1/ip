package gary.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ContactParserTest {
    private final ContactParser parser = new ContactParser();

    @Test
    void parseContact_completeCommand_returnsContact() {
        Contact contact = parser.parseContact(
                "contact add Alice Tan /phone 91234567 /email alice@example.com");

        assertEquals("Alice Tan", contact.getName());
        assertEquals("91234567", contact.getPhone());
        assertEquals("alice@example.com", contact.getEmail());
    }

    @Test
    void parseContact_missingDetails_throwsException() {
        Executable missingName = () -> parser.parseContact(
                "contact add /phone 91234567 /email alice@example.com");
        Executable missingPhone = () -> parser.parseContact(
                "contact add Alice /phone /email alice@example.com");
        Executable missingEmailMarker = () -> parser.parseContact(
                "contact add Alice /phone 91234567");

        assertThrows(IllegalArgumentException.class, missingName);
        assertThrows(IllegalArgumentException.class, missingPhone);
        assertThrows(IllegalArgumentException.class, missingEmailMarker);
    }

    @Test
    void parseCommandArguments_variedInputs_returnsValidatedValues() {
        assertEquals(ContactCommandType.ADD, parser.parseCommandType("contact add Alice"));
        assertEquals(ContactCommandType.LIST, parser.parseCommandType("contact list"));
        assertEquals(ContactCommandType.UNKNOWN, parser.parseCommandType("contact list extra"));
        assertEquals(2, parser.parseContactNumber("contact delete 2", 3));
        assertEquals(-1, parser.parseContactNumber("contact delete 4", 3));
        assertEquals(-1, parser.parseContactNumber("contact delete two", 3));
        assertEquals("Alice", parser.parseKeyword("contact find Alice"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseKeyword("contact find"));
    }

    @Test
    void parseCommand_variedWhitespaceAndCase_returnsContact() {
        assertEquals(ContactCommandType.LIST, parser.parseCommandType("contact   LIST"));

        Contact contact = parser.parseContact(
                "CONTACT ADD Alice Tan /PHONE 91234567 /EMAIL alice@example.com");
        assertEquals("Alice Tan", contact.getName());
        assertEquals("91234567", contact.getPhone());
        assertEquals("alice@example.com", contact.getEmail());
    }
}

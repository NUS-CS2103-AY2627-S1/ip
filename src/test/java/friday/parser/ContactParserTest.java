package friday.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import friday.contact.Contact;

/** Tests contact command syntax, field boundaries, and validation precedence. */
class ContactParserTest {
    @ParameterizedTest
    @ValueSource(strings = {"contact add Alice Tan /phone 91234567 /email Alice@example.com",
            "contact   add   Alice Tan   /email Alice@example.com   /phone 91234567   "})
    void parseContact_validFieldsInEitherOrder_preservesDetails(String command) {
        Contact contact = ContactParser.parseContact(command);
        assertEquals("Alice Tan", contact.getName());
        assertEquals("91234567", contact.getPhone());
        assertEquals("Alice@example.com", contact.getEmail());
        assertEquals(Parser.CommandType.CONTACT, Parser.parseCommandType(command));
    }

    @Test
    void parseContact_singleDetail_acceptsPhoneOrEmail() {
        assertEquals("", ContactParser.parseContact("contact add Alice /phone 91234567").getEmail());
        assertEquals("", ContactParser.parseContact("contact add Bob /email bob@example.com").getPhone());
        assertEquals("Alice/Bob | \\",
                ContactParser.parseContact("contact add Alice/Bob | \\ /phone 91234567").getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"contact add", "contact add   ", "contact add Alice", "contact add /phone 91234567",
            "contact add Alice /phone", "contact add Alice /email ",
            "contact add Alice /phone /email a@b.c", "contact add Alice /phone 91234567 /email",
            "contact add Alice /phone 91234567 /phone 81234567",
            "contact add Alice /email a@b.c /email b@b.c", "contact add Alice /address home",
            "contact add Alice /Phone 91234567", "contact add Alice /phone91234567",
            "contact add Alice /phone bad /unknown value", "contact add Alice /phone 91234567 /"})
    void parseContact_invalidStructure_rejectsBeforeFieldValidation(String command) {
        assertEquals(Contact.INVALID_FORMAT_MESSAGE,
                assertThrows(IllegalArgumentException.class, () -> ContactParser.parseContact(command)).getMessage());
    }

    @Test
    void parseContact_invalidValues_checksPhoneBeforeEmail() {
        assertEquals("Invalid phone number. Use 8 digits starting with 6, 8, or 9.",
                assertThrows(IllegalArgumentException.class,
                        () -> ContactParser.parseContact("contact add Alice /email bad /phone bad")).getMessage());
        assertEquals("Invalid email address. Use a format like alice@example.com.",
                assertThrows(IllegalArgumentException.class,
                        () -> ContactParser.parseContact("contact add Alice /email bad")).getMessage());
        assertEquals(Contact.INVALID_FORMAT_MESSAGE, assertThrows(IllegalArgumentException.class,
                () -> ContactParser.parseContact("contact add Alice\tTan /phone 91234567")).getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"contact", "contact ", "contact edit Alice", "contact ADD Alice", "contacts",
            "contact list extra", "contact listed", "Contact list"})
    void parseCommandType_invalidSubcommand_rejectsWithUsage(String command) {
        assertEquals("Invalid contact command. Use: contact add, contact list, contact find, or contact delete.",
                assertThrows(IllegalArgumentException.class,
                        () -> ContactParser.parseCommandType(command)).getMessage());
    }

    @Test
    void parseFindKeyword_phrase_preservesCaseInternalSpacesAndLiteralPrefixes() {
        assertEquals("Alice  TAN", ContactParser.parseFindKeyword("contact find   Alice  TAN  "));
        assertEquals("/email", ContactParser.parseFindKeyword("contact find /email"));
        assertEquals(ContactParser.CommandType.LIST, ContactParser.parseCommandType("contact list   "));
    }

    @ParameterizedTest
    @ValueSource(strings = {"contact find", "contact find   "})
    void parseFindKeyword_blank_rejectsWithUsage(String command) {
        assertEquals("Invalid contact find format. Use: contact find KEYWORD",
                assertThrows(IllegalArgumentException.class,
                        () -> ContactParser.parseFindKeyword(command)).getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "0", "-1", "+1", "1.0", "one", "1 2", "2147483648", "９"})
    void parseContactNumber_invalidInteger_rejectsWithNumberError(String number) {
        assertEquals("The contact number is invalid.", assertThrows(IllegalArgumentException.class,
                () -> ContactParser.parseContactNumber("contact delete " + number)).getMessage());
    }

    @Test
    void parseContactNumber_positiveDecimal_acceptsLeadingZerosAndMaximumInteger() {
        assertEquals(1, ContactParser.parseContactNumber("contact delete 001 "));
        assertEquals(Integer.MAX_VALUE, ContactParser.parseContactNumber("contact delete 2147483647"));
    }

    @Test
    void parseArguments_wrongOperation_rejectsParserMisuse() {
        assertThrows(IllegalArgumentException.class, () -> ContactParser.parseContact("contact list"));
        assertThrows(IllegalArgumentException.class, () -> ContactParser.parseFindKeyword("contact delete 1"));
        assertThrows(IllegalArgumentException.class, () -> ContactParser.parseContactNumber("contact find 1"));
    }
}

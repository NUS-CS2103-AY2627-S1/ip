package ff15;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ff15.command.ContactAddCommand;
import ff15.command.ContactDeleteCommand;
import ff15.command.ContactFindCommand;
import ff15.command.ContactListCommand;
import ff15.contact.Contact;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/**
 * Tests {@link Parser#parse(String)} on the {@code contact} commands: which
 * sub-command each line becomes, how the optional fields are read, and which
 * lines are refused.
 */
public class ContactParserTest extends ParserTestBase {


    /** Runs {@code input} against a fresh contact list and returns the single contact it added. */
    private Contact contactAddedBy(String input) throws Exception {
        ContactList contacts = new ContactList();
        run(new TaskList(), contacts, input);
        assertEquals(1, contacts.size(), "the command should have added exactly one contact");
        return contacts.get(1);
    }

    @Test
    public void parse_contactSubCommands_returnTheMatchingCommand() throws FF15Exception {
        assertInstanceOf(ContactAddCommand.class, Parser.parse("contact add John"));
        assertInstanceOf(ContactListCommand.class, Parser.parse("contact list"));
        assertInstanceOf(ContactDeleteCommand.class, Parser.parse("contact delete 1"));
        assertInstanceOf(ContactFindCommand.class, Parser.parse("contact find john"));
    }

    @Test
    public void parse_contactAddWithAllFields_keepsAllThree() throws Exception {
        Contact contact = contactAddedBy("contact add John /phone 91234567 /email john@example.com");
        assertEquals("John", contact.getName());
        assertEquals("91234567", contact.getPhone());
        assertEquals("john@example.com", contact.getEmail());
    }

    @Test
    public void parse_contactAlreadyInTheList_isRefusedAndNamesIt() throws Exception {
        ContactList contacts = new ContactList();
        run(new TaskList(), contacts, "contact add John /phone 91234567");
        run(new TaskList(), contacts, "contact add Mary");

        FF15Exception thrown = assertThrows(FF15Exception.class, () ->
                run(new TaskList(), contacts, "contact add John /phone 91234567"));

        assertTrue(thrown.getMessage().contains("contact 1"), thrown.getMessage());
        assertEquals(2, contacts.size());
    }

    @Test
    public void parse_contactSharingOnlyAName_isNotARepeat() throws Exception {
        ContactList contacts = new ContactList();
        run(new TaskList(), contacts, "contact add John /phone 91234567");
        run(new TaskList(), contacts, "contact add John /phone 98765432");
        assertEquals(2, contacts.size());
    }

    @Test
    public void parse_contactAddWithMarkersReversed_stillReadsBothFields() throws Exception {
        Contact contact = contactAddedBy("contact add John /email john@example.com /phone 91234567");
        assertEquals("John", contact.getName());
        assertEquals("91234567", contact.getPhone());
        assertEquals("john@example.com", contact.getEmail());
    }

    @Test
    public void parse_contactAddWithOneField_leavesTheOtherEmpty() throws Exception {
        Contact withPhone = contactAddedBy("contact add Mary /phone 98765432");
        assertEquals("98765432", withPhone.getPhone());
        assertEquals("", withPhone.getEmail());

        Contact withEmail = contactAddedBy("contact add Sam /email sam@example.com");
        assertEquals("", withEmail.getPhone());
        assertEquals("sam@example.com", withEmail.getEmail());
    }

    @Test
    public void parse_contactAddNameOnly_keepsTheWholeName() throws Exception {
        assertEquals("Alex Tan", contactAddedBy("contact add Alex Tan").getName());
    }

    @Test
    public void parse_contactAddWithSurroundingSpaces_trimsEachField() throws Exception {
        Contact contact = contactAddedBy(
                "contact add   John   /phone   91234567   /email   john@example.com  ");
        assertEquals("John", contact.getName());
        assertEquals("91234567", contact.getPhone());
        assertEquals("john@example.com", contact.getEmail());
    }

    @Test
    public void parse_contactAddWithNoName_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add"));
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add /phone 91234567"));
    }

    @Test
    public void parse_contactAddWithEmptyMarkerValue_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John /phone"));
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John /email"));
    }

    @Test
    public void parse_contactAddWithUnusablePhone_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John /phone hello"));
    }

    @Test
    public void parse_contactAddWithOddButValidPhone_isAccepted() throws Exception {
        Contact contact = contactAddedBy("contact add John /phone +65 (912) 345-67");
        assertEquals("+65 (912) 345-67", contact.getPhone());
    }

    @Test
    public void parse_contactAddWithUnusableEmail_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John /email nope"));
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John /email @example.com"));
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John /email john@"));
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John /email a@b@c"));
    }

    @Test
    public void parse_contactWithNoSubCommand_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("contact"));
        assertThrows(FF15Exception.class, () -> Parser.parse("contact   "));
    }

    @Test
    public void parse_contactWithUnknownSubCommand_throwsException() {
        FF15Exception thrown = assertThrows(FF15Exception.class, () -> Parser.parse("contact mark 1"));
        assertTrue(thrown.getMessage().contains("contact add"), thrown.getMessage());
    }

    @Test
    public void parse_wordThatMerelyStartsWithContact_isNotAContactCommand() {
        assertThrows(FF15Exception.class, () -> Parser.parse("contacts"));
    }

    @Test
    public void parse_contactListWithAnArgument_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("contact list everything"));
    }

    @Test
    public void parse_contactDeleteWithoutOrWithABadNumber_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("contact delete"));
        assertThrows(FF15Exception.class, () -> Parser.parse("contact delete two"));
    }

    @Test
    public void parse_contactFindWithoutAKeyword_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("contact find"));
    }

    @Test
    public void parse_contactDelete_removesTheContactTheUserNumbered() throws Exception {
        ContactList contacts = new ContactList();
        contacts.add(new Contact("a", "", ""), new Contact("b", "", ""));
        run(new TaskList(), contacts, "contact delete 1");
        assertEquals(1, contacts.size());
        assertEquals("b", contacts.get(1).getName());
    }

    @Test
    public void parse_contactFind_reportsTheMatchingContacts() throws Exception {
        ContactList contacts = new ContactList();
        contacts.add(new Contact("John", "", ""), new Contact("Mary", "", ""));

        String printed = runCapturing(new TaskList(), contacts, "contact find john");

        assertTrue(printed.contains("John"), printed);
        assertFalse(printed.contains("Mary"), printed);
    }

    @Test
    public void parse_contactFindWithNoMatch_saysSo() throws Exception {
        ContactList contacts = new ContactList();
        contacts.add(new Contact("John", "", ""));

        String printed = runCapturing(new TaskList(), contacts, "contact find zzz");

        assertTrue(printed.contains("Is this someone from corporate?"), printed);
    }
}

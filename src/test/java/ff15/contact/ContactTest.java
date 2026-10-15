package ff15.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests how a {@link Contact} shows itself to the user and to the save file, and
 * which keywords it answers to.
 *
 * <p>The cases below work through the four combinations of the two optional
 * fields, since leaving one out changes both the display and the saved line.
 */
public class ContactTest {

    @Test
    public void toString_allFields_showsPhoneAndEmail() {
        Contact contact = new Contact("John", "91234567", "john@example.com");
        assertEquals("John (phone: 91234567, email: john@example.com)", contact.toString());
    }

    @Test
    public void toString_phoneOnly_leavesOutTheEmail() {
        assertEquals("Mary (phone: 98765432)", new Contact("Mary", "98765432", "").toString());
    }

    @Test
    public void toString_emailOnly_leavesOutThePhone() {
        assertEquals("Sam (email: sam@example.com)", new Contact("Sam", "", "sam@example.com").toString());
    }

    @Test
    public void toString_nameOnly_showsJustTheName() {
        assertEquals("Alex", new Contact("Alex", "", "").toString());
    }

    @Test
    public void constructor_nullFields_treatsThemAsAbsent() {
        assertEquals("Alex", new Contact("Alex", null, null).toString());
    }

    @Test
    public void toFileFormat_allFields_writesAllThree() {
        Contact contact = new Contact("John", "91234567", "john@example.com");
        assertEquals("John | 91234567 | john@example.com", contact.toFileFormat());
    }

    @Test
    public void toFileFormat_nameOnly_stillWritesBothEmptyFields() {
        assertEquals("Alex |  | ", new Contact("Alex", "", "").toFileFormat());
    }

    @Test
    public void isSameAs_everyFieldEqual_isTrue() {
        Contact a = new Contact("John", "91234567", "john@example.com");
        Contact b = new Contact("John", "91234567", "john@example.com");
        assertTrue(a.isSameAs(b));
    }

    @Test
    public void isSameAs_sameNameDifferentDetails_isFalse() {
        // Two people can share a name, so a name alone does not make a repeat.
        Contact john = new Contact("John", "91234567", "");
        assertFalse(john.isSameAs(new Contact("John", "98765432", "")));
        assertFalse(john.isSameAs(new Contact("John", "91234567", "john@example.com")));
        assertFalse(john.isSameAs(new Contact("John", "", "")));
    }

    @Test
    public void isSameAs_nameDiffersOnlyInCase_isFalse() {
        assertFalse(new Contact("John", "", "").isSameAs(new Contact("john", "", "")));
    }

    @Test
    public void hasKeyword_matchingName_returnsTrue() {
        assertTrue(new Contact("John Tan", "", "").hasKeyword("john"));
    }

    @Test
    public void hasKeyword_differentCase_stillMatches() {
        assertTrue(new Contact("john tan", "", "").hasKeyword("TAN"));
    }

    @Test
    public void hasKeyword_partOfTheName_matches() {
        assertTrue(new Contact("Jonathan", "", "").hasKeyword("nath"));
    }

    @Test
    public void hasKeyword_noMatch_returnsFalse() {
        assertFalse(new Contact("John", "", "").hasKeyword("mary"));
    }

    @Test
    public void hasKeyword_matchesPhoneOrEmail_returnsFalse() {
        Contact contact = new Contact("John", "91234567", "john@example.com");
        assertFalse(contact.hasKeyword("9123"), "only the name should be searched");
        assertFalse(contact.hasKeyword("example.com"), "only the name should be searched");
    }
}

package hu9o.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Person}'s two rendering methods: {@code compressionString}
 * (the save-file record) and {@code toString} (display). The main risk
 * covered is a blank, trailing {@code dob}/{@code notes} field being dropped
 * by a naive split when the record is later parsed.
 */
public class PersonTest {

    @Test
    public void compressionString_blankDobAndNotes_keepsTrailingEmptyFields() {
        Person person = new Person("John Tan", "91234567", "john@example.com", "", "");

        assertEquals("John Tan|91234567|john@example.com||", person.compressionString());
    }

    @Test
    public void compressionString_fullBiodata_includesAllFieldsInOrder() {
        Person person = new Person("John Tan", "91234567", "john@example.com", "1/1/90", "likes tea");

        assertEquals("John Tan|91234567|john@example.com|1/1/90|likes tea", person.compressionString());
    }

    @Test
    public void toString_minimalBiodata_omitsDobAndNotes() {
        Person person = new Person("John Tan", "91234567", "john@example.com", "", "");

        String rendered = person.toString();

        assertEquals("John Tan (Phone: 91234567, Email: john@example.com)", rendered);
    }

    @Test
    public void toString_fullBiodata_includesDobAndNotes() {
        Person person = new Person("John Tan", "91234567", "john@example.com", "1/1/90", "likes tea");

        String rendered = person.toString();

        assertTrue(rendered.contains("DOB: 1/1/90"));
        assertTrue(rendered.contains("Notes: likes tea"));
    }

    @Test
    public void getTasks_newPerson_startsEmpty() {
        Person person = new Person("John Tan", "91234567", "john@example.com", "", "");

        assertTrue(person.getTasks().isEmpty());
    }
}

package hu9o.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import hu9o.contact.errors.DuplicatePersonNameException;
import hu9o.contact.errors.InvalidDeletePersonIndexException;
import hu9o.contact.errors.PersonNotFoundException;
import hu9o.contact.errors.SelfLinkException;

/**
 * Tests {@link PersonNetwork}: name uniqueness and lookup, keyword search,
 * one-based index validation, and the undirected link set (including that
 * deleting a person cleans up any links that named them).
 */
public class PersonNetworkTest {

    private static Person personNamed(String name) {
        return new Person(name, "91234567", "person@example.com", "", "");
    }

    @Test
    public void addPerson_duplicateNameDifferentCase_throwsDuplicatePersonNameException() throws Exception {
        PersonNetwork people = new PersonNetwork();
        people.addPerson(personNamed("John Tan"));

        assertThrows(DuplicatePersonNameException.class, () -> people.addPerson(personNamed("john tan")));
    }

    @Test
    public void findPeople_keywordInSomeNames_returnsOnlyThoseInListOrder() throws Exception {
        PersonNetwork people = new PersonNetwork();
        people.addPerson(personNamed("John Tan"));
        people.addPerson(personNamed("Mary Lim"));
        people.addPerson(personNamed("Johnny Lee"));

        PersonNetwork matches = people.findPeople("john");

        assertEquals(2, matches.size());
        assertEquals("John Tan", matches.get(0).getName());
        assertEquals("Johnny Lee", matches.get(1).getName());
    }

    @Test
    public void getPersonByName_unknownName_throwsPersonNotFoundException() {
        PersonNetwork people = new PersonNetwork();

        assertThrows(PersonNotFoundException.class, () -> people.getPersonByName("Nobody"));
    }

    @Test
    public void getPersonByName_nameDiffersInCase_stillMatches() throws Exception {
        PersonNetwork people = new PersonNetwork();
        people.addPerson(personNamed("John Tan"));

        assertEquals("John Tan", people.getPersonByName("JOHN TAN").getName());
    }

    @Test
    public void deletePerson_indexOutOfRange_throwsInvalidDeletePersonIndexException() {
        PersonNetwork people = new PersonNetwork();

        assertThrows(InvalidDeletePersonIndexException.class, () -> people.deletePerson(1));
    }

    @Test
    public void deletePerson_personWithLinks_removesTheirLinksToo() throws Exception {
        PersonNetwork people = new PersonNetwork();
        Person john = personNamed("John Tan");
        Person mary = personNamed("Mary Lim");
        people.addPerson(john);
        people.addPerson(mary);
        people.link(john, mary);

        people.deletePerson(1); // removes John Tan

        assertTrue(people.getConnections(mary).isEmpty());
        assertTrue(people.getLinkKeys().isEmpty());
    }

    @Test
    public void link_samePersonTwice_throwsSelfLinkException() throws Exception {
        PersonNetwork people = new PersonNetwork();
        Person john = personNamed("John Tan");
        people.addPerson(john);

        assertThrows(SelfLinkException.class, () -> people.link(john, john));
    }

    @Test
    public void getConnections_afterLinking_returnsBothDirections() throws Exception {
        PersonNetwork people = new PersonNetwork();
        Person john = personNamed("John Tan");
        Person mary = personNamed("Mary Lim");
        people.addPerson(john);
        people.addPerson(mary);

        people.link(john, mary);

        assertEquals(List.of(mary), people.getConnections(john));
        assertEquals(List.of(john), people.getConnections(mary));
    }

    @Test
    public void link_samePairTwice_isIdempotent() throws Exception {
        PersonNetwork people = new PersonNetwork();
        Person john = personNamed("John Tan");
        Person mary = personNamed("Mary Lim");
        people.addPerson(john);
        people.addPerson(mary);

        people.link(john, mary);
        people.link(mary, john); // same pair, opposite argument order

        assertEquals(1, people.getLinkKeys().size());
    }
}

package hu9o.contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import hu9o.contact.errors.DuplicatePersonNameException;
import hu9o.contact.errors.InvalidDeletePersonIndexException;
import hu9o.contact.errors.PersonNotFoundException;
import hu9o.contact.errors.SelfLinkException;

/**
 * The in-memory network of people Hu9o knows about, plus the simple,
 * undirected connections between them.
 *
 * <p>It extends {@link ArrayList} so callers can still iterate over it and
 * read its {@code size()}, while adding network-aware operations that
 * validate a one-based position or a name before touching the list or the
 * link set. This mirrors {@link hu9o.task.TaskList}'s own design.
 */
public class PersonNetwork extends ArrayList<Person> {
    /** Identifies this class version during serialization (inherited from {@link ArrayList}). */
    private static final long serialVersionUID = 1L;

    /** Undirected links, one entry per pair, stored as "name1|name2" in case-insensitive order. */
    private final Set<String> links = new LinkedHashSet<>();

    /**
     * Adds a person to the network.
     *
     * @param person the person to add.
     * @throws DuplicatePersonNameException if someone with that name (ignoring case) is already in the network.
     */
    public void addPerson(Person person) throws DuplicatePersonNameException {
        boolean nameTaken = stream().anyMatch(existing -> existing.getName().equalsIgnoreCase(person.getName()));
        if (nameTaken) {
            throw new DuplicatePersonNameException();
        }
        add(person);
    }

    /**
     * Removes and returns the person at the given one-based position, and
     * removes any links that mentioned them.
     *
     * @param oneBasedIndex the person's number shown by {@code people} (1 = first person);
     *                      0 means the command could not be parsed.
     * @return the person that was removed.
     * @throws InvalidDeletePersonIndexException if no person has that number.
     */
    public Person deletePerson(int oneBasedIndex) throws InvalidDeletePersonIndexException {
        if (oneBasedIndex < 1 || oneBasedIndex > size()) {
            throw new InvalidDeletePersonIndexException();
        }
        Person removed = remove(oneBasedIndex - 1);
        links.removeIf(key -> mentions(key, removed.getName()));
        return removed;
    }

    /**
     * Returns the people whose name contains the given keyword, matched
     * case-insensitively, in their current list order.
     *
     * @param keyword the text to search for.
     * @return the matching people; empty if none match.
     */
    public PersonNetwork findPeople(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return stream()
                .filter(person -> person.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(PersonNetwork::new));
    }

    /**
     * Returns the person with the given name.
     *
     * @param name the name to look up, matched case-insensitively.
     * @return the matching person.
     * @throws PersonNotFoundException if no one in the network has that name.
     */
    public Person getPersonByName(String name) throws PersonNotFoundException {
        return stream()
                .filter(person -> person.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(PersonNotFoundException::new);
    }

    /**
     * Records a simple, undirected connection between two people. Linking
     * the same pair again has no further effect.
     *
     * @param first  one of the two people to connect.
     * @param second the other person to connect.
     * @throws SelfLinkException if {@code first} and {@code second} are the same person.
     */
    public void link(Person first, Person second) throws SelfLinkException {
        if (first == second) {
            throw new SelfLinkException();
        }
        links.add(linkKey(first.getName(), second.getName()));
    }

    /**
     * Returns the people linked to the given person.
     *
     * @param person the person whose connections to look up.
     * @return the connected people, in network order; empty if there are none.
     */
    public List<Person> getConnections(Person person) {
        return stream()
                .filter(other -> other != person && links.contains(linkKey(person.getName(), other.getName())))
                .collect(Collectors.toList());
    }

    /**
     * Returns every link as a {@code "name1|name2"} key, in insertion order.
     * {@link ContactStorage} uses this to persist links; nothing else should
     * need the raw key format.
     *
     * @return the link keys; empty if there are no links.
     */
    public Set<String> getLinkKeys() {
        return Collections.unmodifiableSet(links);
    }

    /** Returns the canonical, order-independent key for the link between two names. */
    private String linkKey(String name1, String name2) {
        return name1.compareToIgnoreCase(name2) <= 0
                ? name1 + "|" + name2
                : name2 + "|" + name1;
    }

    /** Returns whether the given link key names the given person, on either side. */
    private boolean mentions(String linkKey, String name) {
        String[] names = linkKey.split("\\|", 2);
        return names[0].equals(name) || names[1].equals(name);
    }
}

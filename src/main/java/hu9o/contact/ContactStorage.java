package hu9o.contact;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hu9o.errors.Hu9oException;
import hu9o.parser.Parser;

/**
 * Reads and writes the contact network on disk: the people, their biodata,
 * their links, and each person's own tasks.
 *
 * <p>Unlike {@link hu9o.storage.Storage}, this class has no {@link hu9o.ui.Ui}
 * dependency and never prints anything. It must stay silent, because every
 * existing console UI test asserts the exact bytes Hu9o prints on exit, and
 * this class's save step runs alongside {@code Storage}'s on every {@code bye}.
 */
public class ContactStorage {
    /** Location of the person records file. */
    private static final Path CONTACT_DATA_PATH = Path.of("data", "contactData.txt");
    /** Location of the link records file. */
    private static final Path CONTACT_LINKS_PATH = Path.of("data", "contactLinks.txt");
    /** Directory holding one task file per person, named by their 1-based network position. */
    private static final Path CONTACTS_DIR = Path.of("data", "contacts");

    private final Parser parser;

    /**
     * Creates a contact storage helper.
     *
     * @param parser used only for its already-public {@link Parser#parseTask}, to rebuild
     *               each person's tasks the same way {@link hu9o.storage.Storage} rebuilds the global list.
     */
    public ContactStorage(Parser parser) {
        assert parser != null : "ContactStorage needs a parser to rebuild saved tasks";
        this.parser = parser;
    }

    /**
     * Loads every saved person, their own tasks, and every link between them into the given network.
     *
     * @param people the network to populate.
     */
    public void loadAll(PersonNetwork people) {
        loadPeople(people);
        loadPersonTasks(people);
        loadLinks(people);
    }

    /**
     * Saves every person, their own tasks, and every link between them, so they can be loaded on the next run.
     *
     * @param people the network to persist.
     */
    public void dumpAll(PersonNetwork people) {
        dumpPeople(people);
        dumpPersonTasks(people);
        dumpLinks(people);
    }

    /** Loads {@code contactData.txt} into {@code people}, skipping any record that does not have 5 fields. */
    private void loadPeople(PersonNetwork people) {
        if (Files.notExists(CONTACT_DATA_PATH)) {
            // No contact data yet (e.g. first run, or the data folder went missing) --
            // create an empty file straight away and start with an empty network.
            writeQuietly(CONTACT_DATA_PATH, "");
            return;
        }
        try (Stream<String> lines = Files.lines(CONTACT_DATA_PATH)) {
            // people::add (not addPerson): this data was already validated when it
            // was saved, so the duplicate-name check is unnecessary here.
            lines.map(this::parsePersonRecord)
                    .filter(Objects::nonNull)
                    .forEach(people::add);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /** Loads each person's {@code data/contacts/<n>.txt} into their own task list, by network position. */
    private void loadPersonTasks(PersonNetwork people) {
        for (int i = 0; i < people.size(); i++) {
            Path taskPath = CONTACTS_DIR.resolve((i + 1) + ".txt");
            if (Files.notExists(taskPath)) {
                continue;
            }
            try (Stream<String> lines = Files.lines(taskPath)) {
                lines.map(parser::parseTask)
                        .filter(Objects::nonNull)
                        .forEach(people.get(i).getTasks()::addTask);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /** Loads {@code contactLinks.txt}, skipping any pair that no longer names two real people. */
    private void loadLinks(PersonNetwork people) {
        if (Files.notExists(CONTACT_LINKS_PATH)) {
            writeQuietly(CONTACT_LINKS_PATH, "");
            return;
        }
        try (Stream<String> lines = Files.lines(CONTACT_LINKS_PATH)) {
            lines.forEach(line -> linkSilently(people, line));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /** Rebuilds one person from a {@code NAME|PHONE|EMAIL|DOB|NOTES} record, or {@code null} if malformed. */
    private Person parsePersonRecord(String record) {
        // -1 keeps trailing empty fields (a blank dob and/or notes); a bare
        // split would silently drop them and misalign the remaining columns.
        String[] parts = record.split("\\|", -1);
        if (parts.length != 5) {
            return null;
        }
        return new Person(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

    /** Links two names from a saved {@code NAME1|NAME2} record, skipping it if either name no longer exists. */
    private void linkSilently(PersonNetwork people, String linkLine) {
        String[] names = linkLine.split("\\|", -1);
        if (names.length != 2) {
            return;
        }
        try {
            people.link(people.getPersonByName(names[0]), people.getPersonByName(names[1]));
        } catch (Hu9oException exception) {
            // A since-deleted person's name, or a corrupted line; skip it rather than fail the whole load.
        }
    }

    /** Writes {@code contactData.txt}, one {@link Person#compressionString()} per line. */
    private void dumpPeople(PersonNetwork people) {
        String data = people.stream()
                .map(person -> person.compressionString() + "\n")
                .collect(Collectors.joining());
        writeQuietly(CONTACT_DATA_PATH, data);
    }

    /** Writes {@code contactLinks.txt}, one link key per line. */
    private void dumpLinks(PersonNetwork people) {
        String data = people.getLinkKeys().stream()
                .map(key -> key + "\n")
                .collect(Collectors.joining());
        writeQuietly(CONTACT_LINKS_PATH, data);
    }

    /** Writes each person's own tasks to {@code data/contacts/<n>.txt}, by their current network position. */
    private void dumpPersonTasks(PersonNetwork people) {
        for (int i = 0; i < people.size(); i++) {
            String data = people.get(i).getTasks().stream()
                    .map(task -> task.compressionString() + "\n")
                    .collect(Collectors.joining());
            writeQuietly(CONTACTS_DIR.resolve((i + 1) + ".txt"), data);
        }
    }

    /** Writes {@code data} to {@code path}, creating its parent directory first; failures are logged, not thrown. */
    private void writeQuietly(Path path, String data) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, data);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

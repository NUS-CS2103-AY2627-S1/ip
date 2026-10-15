package hu9o.contact;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu9o.contact.errors.InvalidConnectionsFormatException;
import hu9o.contact.errors.InvalidFindPersonFormatException;
import hu9o.contact.errors.InvalidLinkFormatException;
import hu9o.contact.errors.InvalidPersonFormatException;
import hu9o.contact.errors.InvalidSelectFormatException;
import hu9o.errors.Hu9oException;
import hu9o.ui.Ui;

/**
 * Turns text into actions for the contact network: adding people, linking
 * them, and selecting a person so ordinary todo/list/mark commands can be
 * redirected to their own task list.
 *
 * <p>This mirrors {@link hu9o.parser.Parser}'s own shape (a command-type
 * enum, one {@code Pattern} per command, one private handler method each),
 * but is deliberately a separate class: {@link hu9o.Hu9o} decides, per
 * command, whether to route to this parser or to the task {@code Parser},
 * so neither parser needs to know the other exists.
 */
public class ContactParser {
    /** Matches a person command and captures name, phone, email, and the optional dob/notes. */
    private static final Pattern PERSON_PATTERN = Pattern.compile(
            "^person\\s+(.+?)\\s+/phone\\s+(.+?)\\s+/email\\s+(.+?)"
                    + "(?:\\s+/dob\\s+(.+?))?(?:\\s+/notes\\s+(.+))?$",
            Pattern.CASE_INSENSITIVE);
    /** Matches a findperson command and captures its search keyword. */
    private static final Pattern FINDPERSON_PATTERN =
            Pattern.compile("^findperson\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    /** Matches a link command and captures the two names it connects. */
    private static final Pattern LINK_PATTERN =
            Pattern.compile("^link\\s+(.+?)\\s+/with\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    /** Matches a connections command and captures the name to look up. */
    private static final Pattern CONNECTIONS_PATTERN =
            Pattern.compile("^connections\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    /** Matches a select command and captures the name to select. */
    private static final Pattern SELECT_PATTERN =
            Pattern.compile("^select\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    /** Every contact command name Hu9o understands. */
    private enum ContactCommandType {
        PERSON, PEOPLE, FINDPERSON, DELETEPERSON, LINK, CONNECTIONS, SELECT, DESELECT
    }

    private final PersonNetwork people;
    private final Ui ui;

    /** The person session commands (todo/list/mark/...) should currently be redirected to; {@code null} means none. */
    private Person selectedPerson;

    /**
     * Creates a contact parser that acts on the given network and reports through the given UI.
     *
     * @param people the person network to read and modify.
     * @param ui     the UI used to display results and errors.
     */
    public ContactParser(PersonNetwork people, Ui ui) {
        // Both collaborators are wired up by Hu9o, never supplied from outside;
        // a null here is a construction bug, not a user error.
        assert people != null : "ContactParser needs a person network to act on";
        assert ui != null : "ContactParser needs a UI to report through";
        this.people = people;
        this.ui = ui;
    }

    /**
     * Returns whether the given word is a known contact command. {@link hu9o.Hu9o}
     * calls this before {@link #handleCommand} to decide which parser should
     * handle a line, since an unrecognized word always produces an error block.
     *
     * @param firstWord the first word of a command, as typed by the user.
     * @return {@code true} if the word names a contact command.
     */
    public boolean isContactCommand(String firstWord) {
        try {
            ContactCommandType.valueOf(firstWord.toUpperCase());
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /**
     * Returns the person session commands are currently redirected to.
     *
     * @return the selected person, or {@code null} if no one is selected.
     */
    public Person getSelectedPerson() {
        return selectedPerson;
    }

    /**
     * Identifies the requested contact operation and delegates it to the
     * matching {@code handle...} method.
     *
     * @param command the complete command entered by the user; must name a
     *                known contact command (see {@link #isContactCommand}).
     */
    public void handleCommand(String command) {
        String[] parts = command.trim().split("\\s+");
        assert parts.length >= 1 : "splitting a trimmed string on whitespace should yield at least one element";
        // Hu9o only calls this after isContactCommand confirms parts[0] is a
        // known verb, so every value of the enum below is covered explicitly.
        assert isContactCommand(parts[0]) : "handleCommand should only be called for a recognized contact verb";
        ui.showBlockStart();
        try {
            switch (ContactCommandType.valueOf(parts[0].toUpperCase())) {
                case PERSON -> handlePerson(command);
                case PEOPLE -> handlePeople();
                case FINDPERSON -> handleFindPerson(command);
                case DELETEPERSON -> handleDeletePerson(parts);
                case LINK -> handleLink(command);
                case CONNECTIONS -> handleConnections(command);
                case SELECT -> handleSelect(command);
                case DESELECT -> handleDeselect();
                default -> throw new AssertionError("unreachable: every ContactCommandType is handled above");
            }
        } catch (Hu9oException exception) {
            ui.showError(exception.getMessage());
        }
        ui.showBlockEnd();
    }

    /**
     * Adds the person described by a {@code person} command.
     *
     * @param command the full command text, matched against {@link #PERSON_PATTERN}.
     * @throws Hu9oException if the command is missing a name, phone, or email, or the name is already taken.
     */
    private void handlePerson(String command) throws Hu9oException {
        Matcher matcher = requireMatch(PERSON_PATTERN, command, InvalidPersonFormatException::new);
        String dob = matcher.group(4) == null ? "" : matcher.group(4);
        String notes = matcher.group(5) == null ? "" : matcher.group(5);
        Person person = new Person(matcher.group(1), matcher.group(2), matcher.group(3), dob, notes);
        people.addPerson(person);
        ui.showPersonAdded(person, people.size());
    }

    /** Shows every person, in network order, for a {@code people} command. */
    private void handlePeople() {
        ui.showPersonList(people);
    }

    /**
     * Shows the people whose name contains the keyword of a {@code findperson} command.
     *
     * @param command the full command text, matched against {@link #FINDPERSON_PATTERN}.
     * @throws Hu9oException if the command carries no search keyword.
     */
    private void handleFindPerson(String command) throws Hu9oException {
        Matcher matcher = requireMatch(FINDPERSON_PATTERN, command, InvalidFindPersonFormatException::new);
        ui.showMatchingPeople(people.findPeople(matcher.group(1)));
    }

    /**
     * Removes the person addressed by a {@code deleteperson} command, deselecting them first if selected.
     *
     * @param parts the whitespace-split command, expected as {@code ["deleteperson", INDEX]}.
     * @throws Hu9oException if the command does not name an existing person.
     */
    private void handleDeletePerson(String[] parts) throws Hu9oException {
        Person removed = people.deletePerson(parseIndex(parts));
        if (removed == selectedPerson) {
            selectedPerson = null;
        }
        ui.showPersonDeleted(removed);
    }

    /**
     * Connects the two people named by a {@code link} command.
     *
     * @param command the full command text, matched against {@link #LINK_PATTERN}.
     * @throws Hu9oException if either name is unknown, or the two names are the same.
     */
    private void handleLink(String command) throws Hu9oException {
        Matcher matcher = requireMatch(LINK_PATTERN, command, InvalidLinkFormatException::new);
        Person first = people.getPersonByName(matcher.group(1));
        Person second = people.getPersonByName(matcher.group(2));
        people.link(first, second);
        ui.showLinkAdded(first, second);
    }

    /**
     * Shows the people linked to the person named by a {@code connections} command.
     *
     * @param command the full command text, matched against {@link #CONNECTIONS_PATTERN}.
     * @throws Hu9oException if the command carries no name, or the name is unknown.
     */
    private void handleConnections(String command) throws Hu9oException {
        Matcher matcher = requireMatch(CONNECTIONS_PATTERN, command, InvalidConnectionsFormatException::new);
        Person person = people.getPersonByName(matcher.group(1));
        ui.showConnections(person, people.getConnections(person));
    }

    /**
     * Redirects session commands to the person named by a {@code select} command.
     *
     * @param command the full command text, matched against {@link #SELECT_PATTERN}.
     * @throws Hu9oException if the command carries no name, or the name is unknown.
     */
    private void handleSelect(String command) throws Hu9oException {
        Matcher matcher = requireMatch(SELECT_PATTERN, command, InvalidSelectFormatException::new);
        selectedPerson = people.getPersonByName(matcher.group(1));
        ui.showSelected(selectedPerson);
    }

    /** Clears the current selection, if any, for a {@code deselect} command. */
    private void handleDeselect() {
        boolean wasSelected = selectedPerson != null;
        selectedPerson = null;
        ui.showDeselected(wasSelected);
    }

    /**
     * Returns the successful match of {@code command} against {@code pattern}, or
     * throws if the command does not fit that format.
     *
     * @param pattern    the pattern the whole command must match.
     * @param command    the raw command text.
     * @param onMismatch supplies the exception to throw when the match fails.
     * @return the matcher, positioned so its captured argument groups can be read.
     * @throws Hu9oException the supplied exception when {@code command} does not match.
     */
    private Matcher requireMatch(Pattern pattern, String command,
            Supplier<? extends Hu9oException> onMismatch) throws Hu9oException {
        Matcher matcher = pattern.matcher(command);
        if (!matcher.matches()) {
            throw onMismatch.get();
        }
        return matcher;
    }

    /**
     * Reads the person number from a two-word command such as {@code deleteperson 3}.
     *
     * @param parts the whitespace-split command.
     * @return the person number, or {@code 0} if the command is malformed (the
     *         network treats 0 as out of range).
     */
    private int parseIndex(String[] parts) {
        boolean isValid = parts.length == 2 && parts[1].matches("[1-9][0-9]{0,8}");
        int index = isValid ? Integer.parseInt(parts[1]) : 0;
        assert index >= 0 : "parseIndex should never return a negative number";
        return index;
    }
}

package ff15;

import ff15.ai.AiAssistant;
import ff15.ai.UnavailableAiHelper;
import ff15.command.AddCommand;
import ff15.command.AiAskCommand;
import ff15.command.AiDoCommand;
import ff15.command.Command;
import ff15.command.ContactAddCommand;
import ff15.command.ContactDeleteCommand;
import ff15.command.ContactFindCommand;
import ff15.command.ContactListCommand;
import ff15.command.DeleteCommand;
import ff15.command.ExitCommand;
import ff15.command.FindCommand;
import ff15.command.ListCommand;
import ff15.command.MarkCommand;
import ff15.command.OnCommand;
import ff15.command.UnmarkCommand;
import ff15.contact.Contact;
import ff15.task.DateRange;
import ff15.task.Deadline;
import ff15.task.Event;
import ff15.task.TaskTime;
import ff15.task.Todo;

/**
 * Deals with making sense of what the user typed: separating the arguments from
 * the command word, checking that the required parts are all there, and turning
 * them into the objects the rest of the program works with.
 *
 * <p>Every method throws {@link FF15Exception} carrying a message aimed at the
 * user, so a badly typed command is reported rather than reaching the task list
 * half-understood. Holds only static helpers and is never instantiated.
 */
public class Parser {
    /** Marks off the date a deadline is due, e.g. {@code deadline return book /by 2019-12-02}. */
    private static final String BY_MARKER = " /by";

    /** Marks off the date an event starts. */
    private static final String FROM_MARKER = " /from";

    /** Marks off the date an event ends. */
    private static final String TO_MARKER = " /to";

    /** Marks off a contact's phone number, e.g. {@code contact add John /phone 91234567}. */
    private static final String PHONE_MARKER = " /phone";

    /** Marks off a contact's email address. */
    private static final String EMAIL_MARKER = " /email";

    /** The word that follows {@code contact} to add one. */
    private static final String CONTACT_ADD = "add";

    /** The word that follows {@code contact} to list them all. */
    private static final String CONTACT_LIST = "list";

    /** The word that follows {@code contact} to remove one. */
    private static final String CONTACT_DELETE = "delete";

    /** The word that follows {@code contact} to search them by name. */
    private static final String CONTACT_FIND = "find";

    /** The characters a phone number may be written with. */
    private static final String PHONE_PATTERN = "[0-9 +\\-()]+";

    /**
     * The one character no user-typed text may contain. The save file separates
     * fields with it, so a description holding it would split into extra fields
     * on the way back in and lose whatever followed.
     */
    private static final String RESERVED = "|";

    /** Answers the AI commands when {@link #parse(String)} is given no assistant of its own. */
    private static final AiAssistant NO_AI = new AiAssistant(
            new UnavailableAiHelper("There's no AI here. It lives in the window version of me."));

    private Parser() { // a private constructor stops anyone writing "new Parser()"
    }

    /**
     * Works out which command {@code input} asks for, and builds it ready to run.
     * The AI commands built this way have no AI behind them, and report so when
     * run; use {@link #parse(String, AiAssistant)} to give them one.
     *
     * @throws FF15Exception if the command word is not recognised, or the rest of
     *     the line does not give the command what it needs.
     */
    public static Command parse(String rawInput) throws FF15Exception {
        return parse(rawInput, NO_AI);
    }

    /**
     * Works out which command {@code input} asks for, and builds it ready to run,
     * handing {@code assistant} to the AI commands so they can reach the AI.
     *
     * @param rawInput the line the user typed.
     * @param assistant what the {@code @ai} and {@code @do} commands ask.
     * @throws FF15Exception if the command word is not recognised, or the rest of
     *     the line does not give the command what it needs.
     */
    public static Command parse(String rawInput, AiAssistant assistant) throws FF15Exception {
        // Trim the ends and collapse runs of spaces, so a stray space never turns a
        // good command into an unknown one, and a description is stored the way it
        // reads rather than with whatever spacing happened to be typed.
        String input = rawInput.strip().replaceAll("\\s+", " ");
        if (input.isEmpty()) {
            throw new FF15Exception("You didn't say anything. I'm a good listener. Say something.");
        }
        CommandWord word = CommandWord.match(input);
        return switch (word) {
            case LIST -> new ListCommand();
            case ON -> new OnCommand(parseDateQuery(input));
            case MARK -> new MarkCommand(parseTaskNumber(input, CommandWord.MARK));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(input, CommandWord.UNMARK));
            case DELETE -> new DeleteCommand(parseTaskNumber(input, CommandWord.DELETE));
            case TODO -> new AddCommand(parseTodo(input));
            case DEADLINE -> new AddCommand(parseDeadline(input));
            case EVENT -> new AddCommand(parseEvent(input));
            case FIND -> new FindCommand(parseKeyword(input));
            case CONTACT -> parseContactCommand(input);
            case AI -> new AiAskCommand(assistant, parseAiQuestion(input));
            case DO -> new AiDoCommand(assistant, parseAiRequest(input));
            case BYE -> new ExitCommand();
            case UNKNOWN -> throw new FF15Exception(
                    "I don't know what that means. Is this a Jim thing? Is Jim doing a thing?");
        };
    }

    /**
     * Returns whatever follows the command word in {@code input} (trimmed), or an
     * empty string if the command word was typed with nothing after it.
     */
    private static String argumentAfter(String input, CommandWord command) {
        String word = command.getWord();
        // Only ever reached once CommandWord.match has matched this word, and the
        // substring arithmetic below is nonsense if that is not so.
        assert input.startsWith(word)
                : "argumentAfter needs input starting with '" + word + "', got: " + input;
        if (input.length() <= word.length()) {
            return "";
        }
        return input.substring(word.length() + 1).trim();
    }

    /**
     * Rejects a marker that appears more than once. Only one value can be kept,
     * and taking the first silently would leave the user wondering where the
     * other one went.
     */
    private static void requireOnce(String details, String marker) throws FF15Exception {
        int first = details.indexOf(marker);
        if (first != -1 && details.indexOf(marker, first + 1) != -1) {
            throw new FF15Exception("You gave " + marker.strip() + " twice. Once is plenty.");
        }
    }

    /** Rejects text that holds the character the save file reserves for itself. */
    private static void requireNoReserved(String text, String what) throws FF15Exception {
        if (text.contains(RESERVED)) {
            throw new FF15Exception("A " + what + " can't contain '" + RESERVED
                    + "'. It's the one character I use to save things.");
        }
    }

    /**
     * Parses the 1-based task number given to mark, unmark, or delete, checking
     * that it is present and numeric. Whether it is in range is checked later by
     * {@link TaskList}, which is the thing that knows how many tasks there are.
     */
    private static int parseTaskNumber(String input, CommandWord command) throws FF15Exception {
        String arg = argumentAfter(input, command);
        if (arg.isEmpty()) {
            throw new FF15Exception("Which one? Use your words. Like, a number. e.g. "
                    + command.getWord() + " 2");
        }
        int number;
        try {
            number = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new FF15Exception("'" + arg + "' is not a number. I know numbers. I run a branch.");
        }
        return number;
    }

    /** Builds the Todo described by {@code input}, which needs only a description. */
    private static Todo parseTodo(String input) throws FF15Exception {
        String description = argumentAfter(input, CommandWord.TODO);
        if (description.isEmpty()) {
            throw new FF15Exception("A todo with nothing in it. That's what she-- no. Tell me what to do.");
        }
        requireNoReserved(description, "description");
        return new Todo(description);
    }

    /** Builds the Deadline described by {@code input}, which must carry a /by. */
    private static Deadline parseDeadline(String input) throws FF15Exception {
        // Padded so a marker that opens the line is still found; without it,
        // "deadline /by ..." reads as having no /by rather than no description.
        String details = " " + argumentAfter(input, CommandWord.DEADLINE);
        requireOnce(details, BY_MARKER);
        int byIndex = details.indexOf(BY_MARKER);
        if (byIndex == -1) {
            throw new FF15Exception("When? Deadlines need a /by, "
                    + "e.g.: deadline return book /by 2019-12-02 1800");
        }
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + BY_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new FF15Exception(
                    "A deadline needs a description. I'm a manager, not a mind reader. Which I also am.");
        }
        if (by.isEmpty()) {
            throw new FF15Exception("A /by with nothing after it. When is it due? Use your words.");
        }
        requireNoReserved(description, "description");
        return new Deadline(description, TaskTime.parse(by));
    }

    /** Builds the Event described by {@code input}, which must carry a /from followed by a /to. */
    private static Event parseEvent(String input) throws FF15Exception {
        String details = " " + argumentAfter(input, CommandWord.EVENT); // padded, as for a deadline
        requireOnce(details, FROM_MARKER);
        requireOnce(details, TO_MARKER);
        int fromIndex = details.indexOf(FROM_MARKER);
        int toIndex = details.indexOf(TO_MARKER);
        boolean isFromMissing = fromIndex == -1;
        boolean isToMissing = toIndex == -1;
        boolean isToBeforeFrom = toIndex < fromIndex;
        if (isFromMissing || isToMissing || isToBeforeFrom) {
            throw new FF15Exception("An event needs a /from and a /to. "
                    + "Otherwise how do I know when to show up? "
                    + "e.g.: event project meeting /from 2019-12-05 1400 /to 2019-12-05 1600");
        }
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = details.substring(toIndex + TO_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new FF15Exception(
                    "An event needs a description. I'm a manager, not a mind reader. Which I also am.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new FF15Exception("A /from or /to with nothing after it. When do I show up?");
        }
        requireNoReserved(description, "description");
        TaskTime fromTime = TaskTime.parse(from);
        TaskTime toTime = TaskTime.parse(to);
        requireSomeDuration(fromTime, toTime);
        return new Event(description, fromTime, toTime);
    }

    /** Rejects an event that ends before it starts, or the moment it starts: neither takes any time. */
    private static void requireSomeDuration(TaskTime from, TaskTime to) throws FF15Exception {
        if (to.isBefore(from)) {
            throw new FF15Exception("It ends before it starts? That's not an event. That's a Ryan.");
        }
        if (to.isSameMomentAs(from)) {
            throw new FF15Exception("It ends when it starts? That's not an event. That's a moment.");
        }
    }

    /** Returns the keyword a {@code find} command should search for. */
    private static String parseKeyword(String input) throws FF15Exception {
        String keyword = argumentAfter(input, CommandWord.FIND);
        if (keyword.isEmpty()) {
            throw new FF15Exception("Look for what? Give me a word. e.g.: find book");
        }
        return keyword;
    }

    /**
     * Builds the contact command asked for by {@code input}, deciding from the
     * word that follows {@code contact} which of the four it is.
     */
    private static Command parseContactCommand(String input) throws FF15Exception {
        String details = argumentAfter(input, CommandWord.CONTACT);
        if (details.isEmpty()) {
            throw new FF15Exception("Tell me what to do with your contacts. "
                    + "Try: contact add, contact list, contact delete, or contact find");
        }
        String subCommand = details.split(" ", 2)[0];
        String rest = details.substring(subCommand.length()).trim();
        return switch (subCommand) {
            case CONTACT_ADD -> new ContactAddCommand(parseContact(rest));
            case CONTACT_LIST -> parseContactList(rest);
            case CONTACT_DELETE -> new ContactDeleteCommand(parseContactNumber(rest));
            case CONTACT_FIND -> new ContactFindCommand(parseContactKeyword(rest));
            default -> throw new FF15Exception("I can't '" + subCommand + "' a contact. Nobody can. "
                    + "Try: contact add, contact list, contact delete, or contact find");
        };
    }

    /**
     * Builds the Contact described by {@code details}, which starts with a name
     * and may then carry a /phone, an /email, or both, in either order.
     */
    private static Contact parseContact(String details) throws FF15Exception {
        // Padded for the same reason as a deadline or an event: a marker that opens
        // the line is a missing name, not a contact called "/phone 123".
        String padded = " " + details;
        requireOnce(padded, PHONE_MARKER);
        requireOnce(padded, EMAIL_MARKER);
        int phoneIndex = padded.indexOf(PHONE_MARKER);
        int emailIndex = padded.indexOf(EMAIL_MARKER);

        String name = padded.substring(0, firstMarkerAt(padded.length(), phoneIndex, emailIndex)).trim();
        if (name.isEmpty()) {
            throw new FF15Exception("A contact needs a name. Everyone has a name. Even Toby. "
                    + "e.g.: contact add John /phone 91234567");
        }

        requireNoReserved(name, "contact name");
        String phone = valueAfter(padded, phoneIndex, PHONE_MARKER, emailIndex);
        String email = valueAfter(padded, emailIndex, EMAIL_MARKER, phoneIndex);
        requirePhone(phone, phoneIndex);
        requireEmail(email, emailIndex);
        requireNoReserved(email, "contact email");
        return new Contact(name, phone, email);
    }

    /**
     * Returns the earliest of {@code markerIndices} that is present, or
     * {@code fallback} when none of them is, which is where the name ends.
     */
    private static int firstMarkerAt(int fallback, int... markerIndices) {
        int earliest = fallback;
        for (int index : markerIndices) {
            if (index != -1 && index < earliest) {
                earliest = index;
            }
        }
        return earliest;
    }

    /**
     * Returns the text following {@code marker}, stopping at {@code otherIndex} if
     * the other marker comes later on the line. Returns an empty string when this
     * marker was not given at all.
     */
    private static String valueAfter(String details, int markerIndex, String marker, int otherIndex) {
        if (markerIndex == -1) {
            return "";
        }
        int start = markerIndex + marker.length();
        int end = otherIndex > markerIndex ? otherIndex : details.length();
        return details.substring(start, end).trim();
    }

    /** Rejects a /phone that was given but is empty or not written like a phone number. */
    private static void requirePhone(String phone, int phoneIndex) throws FF15Exception {
        if (phoneIndex != -1 && phone.isEmpty()) {
            throw new FF15Exception("A /phone with nothing after it. What's the number?");
        }
        if (!phone.isEmpty() && !phone.matches(PHONE_PATTERN)) {
            throw new FF15Exception("'" + phone + "' is not a phone number. "
                    + "I know phones. I have a Blackberry. Digits, spaces, +, -, and brackets only.");
        }
    }

    /** Rejects an /email that was given but is empty or has no single @ inside it. */
    private static void requireEmail(String email, int emailIndex) throws FF15Exception {
        if (emailIndex != -1 && email.isEmpty()) {
            throw new FF15Exception("An /email with nothing after it. What's the email?");
        }
        int at = email.indexOf('@');
        boolean hasTextBothSides = at > 0 && at < email.length() - 1;
        boolean hasOneAt = at == email.lastIndexOf('@');
        if (!email.isEmpty() && !(hasTextBothSides && hasOneAt)) {
            throw new FF15Exception("'" + email + "' is not an email. I've sent emails. Reply-all emails. "
                    + "It needs one @ with something on both sides.");
        }
    }

    /** Builds the command that lists every contact, which takes nothing after it. */
    private static Command parseContactList(String rest) throws FF15Exception {
        if (!rest.isEmpty()) {
            throw new FF15Exception(
                    "'contact list' doesn't need anything after it. Just 'contact list'. Simple.");
        }
        return new ContactListCommand();
    }

    /** Parses the 1-based contact number given to {@code contact delete}. */
    private static int parseContactNumber(String rest) throws FF15Exception {
        if (rest.isEmpty()) {
            throw new FF15Exception("Which contact? Use your words. Like, a number. e.g. contact delete 2");
        }
        try {
            return Integer.parseInt(rest);
        } catch (NumberFormatException e) {
            throw new FF15Exception("'" + rest + "' is not a number. I know numbers. I run a branch.");
        }
    }

    /** Returns the keyword a {@code contact find} command should search names for. */
    private static String parseContactKeyword(String rest) throws FF15Exception {
        if (rest.isEmpty()) {
            throw new FF15Exception("Look for who? Give me a name. e.g.: contact find john");
        }
        return rest;
    }

    /** Returns the question an {@code @ai} command puts to the AI. */
    private static String parseAiQuestion(String input) throws FF15Exception {
        String question = argumentAfter(input, CommandWord.AI);
        if (question.isEmpty()) {
            throw new FF15Exception("Ask me what? e.g.: @ai how do I add a deadline?");
        }
        return question;
    }

    /** Returns the request an {@code @do} command asks the AI to turn into a command. */
    private static String parseAiRequest(String input) throws FF15Exception {
        String request = argumentAfter(input, CommandWord.DO);
        if (request.isEmpty()) {
            throw new FF15Exception("Do what? Say it like you'd say it to me. "
                    + "e.g.: @do remind me to call Pam");
        }
        return request;
    }

    /** Builds the span of dates asked about by an {@code on} command. */
    private static DateRange parseDateQuery(String input) throws FF15Exception {
        String query = argumentAfter(input, CommandWord.ON);
        if (query.isEmpty()) {
            throw new FF15Exception("When? Tell me when. e.g.: on 2019-12-02, on 2019-12, or on 2019");
        }
        return DateRange.parse(query);
    }
}

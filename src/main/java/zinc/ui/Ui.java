package zinc.ui;

import java.util.List;
import java.util.Locale;
import java.util.random.RandomGenerator;

/** Handles the user-facing presentation of Zinc. */
public class Ui {
    /** Personality lines used before successful command results. */
    private static final List<String> SUCCESS_INTRODUCTIONS = List.of(
            "Rock on!", "Nice riff!", "Locked in and ready to rock!");

    /** Personality lines used before lists and search results. */
    private static final List<String> LIST_INTRODUCTIONS = List.of(
            "Here comes the setlist!", "Your lineup is ready!", "Let's check the stage!");

    /** Personality lines used before invalid-command and validation details. */
    private static final List<String> ERROR_INTRODUCTIONS = List.of(
            "That note was a little off.", "Let's tune that command.", "Small soundcheck needed.");

    /** Explanatory message of the CLI's "view message history" feature. */
    private static final String MESSAGE_HISTORY_INTRODUCTION =
            "\nTip: Press the arrow keys to view previously entered commands.";

    /** Messages used to greet the user. */
    private static final List<String> GREETINGS = List.of(
            "Hello, I'm Zinc. Ready to rock your tasks?",
            "Zinc is plugged in! What shall we tackle?",
            "Hello! Let's tune up your day together.");

    /** Messages used to start a fresh GUI conversation. */
    private static final List<String> CONVERSATION_PROMPTS = List.of(
            "What's next on the setlist?", "Ready for another riff?", "What shall we rock through next?");

    /** Messages used when the user exits Zinc. */
    private static final List<String> GOODBYES = List.of(
            "Goodbye. Keep rocking!", "Goodbye. Until the next encore!", "Goodbye. The stage is yours!");

    /** Personality lines used before the command reference. */
    private static final List<String> HELP_INTRODUCTIONS = List.of(
            "Here's the command setlist:",
            "Pick your next move from this lineup:",
            "These commands are ready to rock:");

    /** All commands currently supported by Zinc, in alphabetical order. */
    private static final List<String> SUPPORTED_COMMANDS = List.of(
            "bye", "contact", "ct", "deadline", "delete", "event", "find", "help", "list", "ls",
            "mark", "todo", "ui", "unmark");

    /** The random source used to vary Zinc's responses. */
    private final RandomGenerator randomGenerator;

    /** Whether the CLI's "view message history" feature has been introduced. */
    private boolean hasIntroducedMessageHistoryFunction = false;

    /** The background selection used by the graphical interface. */
    private BackgroundType backgroundType = BackgroundType.AUTO;

    /** Creates a UI that chooses response variants using the default random generator. */
    public Ui() {
        this(RandomGenerator.getDefault());
    }

    /**
     * Creates a UI that uses a supplied random generator.
     *
     * @param randomGenerator The source used to choose response variants.
     */
    Ui(RandomGenerator randomGenerator) {
        assert randomGenerator != null : "Random generator must not be null";
        this.randomGenerator = randomGenerator;
    }

    /** Prints the Zinc banner. */
    public void printBanner() {
        String banner = " ______ _            \n"
                + "|___  /(_)           \n"
                + "   / /  _ _ __   ___ \n"
                + "  / /  | | '_ \\ / __|\n"
                + " / /___| | | | | (__ \n"
                + "/______|_|_| |_|\\___|\n";
        System.out.println(banner);
    }

    /** Prints a randomly selected opening greeting. */
    public void printGreeting() {
        System.out.println(getGreetingMessage() + "\n");
    }

    /**
     * Returns a randomly selected opening greeting.
     *
     * @return A friendly greeting.
     */
    public String getGreetingMessage() {
        return selectResponse(GREETINGS);
    }

    /**
     * Returns a randomly selected prompt for a fresh conversation.
     *
     * @return A friendly conversation prompt.
     */
    public String getConversationPrompt() {
        return selectResponse(CONVERSATION_PROMPTS);
    }

    /**
     * Returns a randomly selected closing message.
     *
     * @return A friendly goodbye message.
     */
    public String getGoodbyeMessage() {
        return selectResponse(GOODBYES);
    }

    /** Prints a compact overview of the currently supported commands. */
    public void printHelp() {
        System.out.println(selectResponse(HELP_INTRODUCTIONS) + "\n" + HelpContent.getCompactOverview());
    }

    /** Prints the usage message for an empty todo description. */
    public void printTodoUsage() {
        printError("The description of a todo cannot be empty.");
    }

    /** Prints guidance when the user submits no command. */
    public void printEmptyInput() {
        printError("No command entered. Type help to see the command setlist.");
    }

    /** Prints the maximum supported command length. */
    public void printCommandTooLong(int maximumLength) {
        printError("That command is too long. Keep it to " + String.format(Locale.ENGLISH, "%,d", maximumLength)
                + " characters or fewer.");
    }

    /** Prints the message used when input contains a non-printable control character. */
    public void printUnsupportedControlCharacter() {
        printError("That command contains an unsupported control character.");
    }

    /** Prints the message used when input contains Zinc's reserved storage separator. */
    public void printReservedStorageSeparator() {
        printError("The sequence \" | \" is reserved and cannot be used in a command.");
    }

    /** Prints a maximum-length error for a user-supplied field. */
    public void printFieldTooLong(String fieldName, int maximumLength) {
        printError(fieldName + " must be " + maximumLength + " characters or fewer.");
    }

    /** Prints the usage message for the deadline command. */
    public void printDeadlineUsage() {
        printError("Usage: deadline <description> /by <DD/MM/YY> [HHMM or HH:MM]");
    }

    /** Prints the usage message for the event command. */
    public void printEventUsage() {
        printError("Usage: event <description> /from <DD/MM/YY> [HHMM or HH:MM]"
                + " /to <DD/MM/YY> [HHMM or HH:MM]");
    }

    /** Prints the message used when an event ends before it starts. */
    public void printEventChronologyError() {
        printError("An event cannot end before it starts.");
    }

    /** Prints the message used when the task list has reached its capacity. */
    public void printTaskListFull() {
        printError("The task list is full. Delete a task before adding another.");
    }

    /** Prints the message used when an identical task is already stored. */
    public void printDuplicateTask() {
        printError("That task already exists in the database. Type \"list\" to see your tasks.");
    }

    /** Prints the invalid date/time message. */
    public void printDateTimeError() {
        printError("Date and time must use DD/MM/YY with an optional HHMM or HH:MM time.");
    }

    /** Prints the usage message for the list date filter. */
    public void printListDateError() {
        printError("Date must use DD/MM/YY. Usage: list <DD/MM/YY>");
    }

    /** Prints the usage message for the find command. */
    public void printFindUsage() {
        printError("Usage: find <keyword>");
    }

    /** Prints the usage message for contact commands. */
    public void printContactUsage() {
        printError("Usage:\n"
                + "contact add /n <name> [/p <8-digit contact number>] [/d <description>]\n"
                + "contact del /n <name>\n"
                + "contact update <current name> [/n <new name>]"
                + " [/p <8-digit contact number>] [/d <description>]\n"
                + "contact list <keyword>");
    }

    /** Prints the message used when a contact number is invalid. */
    public void printContactNumberError() {
        printError("Contact number must contain exactly 8 digits.");
    }

    /** Prints the message used when a contact name is already stored. */
    public void printDuplicateContactName(String name) {
        printError("A contact named \"" + name + "\" already exists.");
    }

    /** Prints the usage message for UI background commands. */
    public void printUiUsage() {
        printError("Usage: ui background <morning|evening|night|auto>");
    }

    /** Prints a confirmation after changing the UI background selection. */
    public void printBackgroundChanged(BackgroundType selectedBackgroundType) {
        assert selectedBackgroundType != null : "Background type must not be null";
        printSuccess("Background changed to " + selectedBackgroundType.name().toLowerCase() + ".");
    }

    /** Prints the invalid task-number message for the given command. */
    public void printTaskNumberError(String command) {
        printError("Task number must be a positive whole number. Usage: " + command + " <task number>");
    }

    /** Prints the message used when a task number is too large for an integer. */
    public void printTaskNumberTooLarge(String command) {
        printError("Task number is too large. Usage: " + command + " <task number>");
    }

    /** Prints the message used when a command does not accept trailing arguments. */
    public void printUnexpectedArguments(String usage) {
        printError("This command does not accept extra arguments. Usage: " + usage);
    }

    /** Prints a message stating that the selected task does not exist. */
    public void printTaskNotFound() {
        printError("No such task found.");
    }

    /** Prints a message stating that the named contact does not exist. */
    public void printContactNotFound(String name) {
        printError("No contact named \"" + name + "\" found.");
    }

    /** Prints either similar command names or the unknown-command message. */
    public void printUnknownCommand(List<String> suggestedCommands) {
        assert suggestedCommands != null : "Suggested commands must not be null";
        if (suggestedCommands.isEmpty()) {
            printError("I don't know that command. Type help for the command setlist.");
            return;
        }

        printError("Did you mean: " + String.join(", ", suggestedCommands)
                + "? Type help for the command setlist.");
    }

    /** Prints a newly added task and the updated task count. */
    public void printTaskAdded(String taskDescription, int taskCount) {
        printSuccess("Task added to list:\n" + taskDescription
                + "\nYou have " + taskCount + " tasks in the list");
    }

    /** Prints task entries beneath a descriptive heading. */
    public void printTaskList(String heading, List<String> taskEntries) {
        printList(heading, taskEntries);
    }

    /** Prints a task whose completion state was changed. */
    public void printTaskCompletionChanged(String actionDescription, String taskDescription) {
        printSuccess("Task " + actionDescription + ":\n" + taskDescription);
    }

    /** Prints a deleted task and the updated task count. */
    public void printTaskDeleted(String taskDescription, int taskCount) {
        printSuccess("Task deleted:\n" + taskDescription
                + "\nYou have " + taskCount + " tasks in the list");
    }

    /** Prints a newly added contact. */
    public void printContactAdded(String contactDescription) {
        printSuccess("Contact added:\n" + contactDescription);
    }

    /** Prints a deleted contact. */
    public void printContactDeleted(String contactDescription) {
        printSuccess("Contact deleted:\n" + contactDescription);
    }

    /** Prints an updated contact. */
    public void printContactUpdated(String contactDescription) {
        printSuccess("Contact updated:\n" + contactDescription);
    }

    /** Prints contact entries beneath a descriptive heading. */
    public void printContactList(String heading, List<String> contactEntries) {
        printList(heading, contactEntries);
    }

    /** Prints the closing message. */
    public void printGoodbye() {
        System.out.println("\n" + getGoodbyeMessage() + "\n");
    }

    /**
     * Returns the supported commands in alphabetical order.
     *
     * @return An immutable list of command names.
     */
    public List<String> getCommands() {
        return SUPPORTED_COMMANDS;
    }

    /** Returns the background selection currently used by the graphical interface. */
    public BackgroundType getBackgroundType() {
        return backgroundType;
    }

    /** Updates the background selection used by the graphical interface. */
    public void setBackgroundType(BackgroundType selectedBackgroundType) {
        assert selectedBackgroundType != null : "Background type must not be null";
        backgroundType = selectedBackgroundType;
    }

    /** Prints a successful result with a randomly selected introduction. */
    private void printSuccess(String message) {
        printSection(selectResponse(SUCCESS_INTRODUCTIONS) + "\n" + message);
    }

    /** Prints list content with a randomly selected introduction. */
    private void printList(String heading, List<String> entries) {
        assert heading != null && entries != null : "List output must not be null";
        StringBuilder message = new StringBuilder(selectResponse(LIST_INTRODUCTIONS))
                .append("\n")
                .append(heading);
        for (String entry : entries) {
            message.append("\n").append(entry);
        }
        printSection(message.toString());
    }

    /** Prints a validation or command error with a randomly selected introduction. */
    private void printError(String message) {
        if (!hasIntroducedMessageHistoryFunction) {
            hasIntroducedMessageHistoryFunction = true;
            printFirstError(message);
            return;
        }

        System.out.println(selectResponse(ERROR_INTRODUCTIONS) + "\n" + message + "\n");
    }

    private void printFirstError(String message) {
        System.out.println(selectResponse(ERROR_INTRODUCTIONS) + "\n" + message + "\n"
                + " " + MESSAGE_HISTORY_INTRODUCTION);
    }

    /** Prints a message followed by a blank line. */
    private void printSection(String message) {
        System.out.println(message + "\n");
    }

    /** Selects one response from a non-empty collection. */
    private String selectResponse(List<String> responses) {
        assert responses != null && !responses.isEmpty() : "Response collection must not be empty";
        return responses.get(randomGenerator.nextInt(responses.size()));
    }
}

package whimsybot;

import java.util.StringJoiner;

import whimsybot.task.Deadline;
import whimsybot.task.Event;
import whimsybot.task.Task;

/** Provides Whimsy Bot's consistent cheerful questmaster voice. */
public final class Personality {
    private Personality() {
        // Utility class.
    }

    /** Returns the first line of the console welcome message. */
    public static String welcomeGreeting() {
        return "Greetings, brave planner!";
    }

    /** Returns the second line of the console welcome message. */
    public static String welcomePrompt() {
        return "Which tiny quest shall we tackle today?";
    }

    /** Returns the first-use guide shown when the chatbot starts. */
    public static String introduction() {
        StringJoiner introduction = new StringJoiner(System.lineSeparator());
        introduction.add("I am Whimsy Bot, your cheerful questmaster for everyday tasks.");
        introduction.add("I can help you capture quests, deadlines, events, and tags.");
        introduction.add("");
        introduction.add("Try these spells to begin:");
        introduction.add("  todo DESCRIPTION - add a simple quest");
        introduction.add("  deadline DESCRIPTION /by DATE - add a time-sensitive quest");
        introduction.add("  event DESCRIPTION /from START /to END - pin an appointment");
        introduction.add("  list - view your quest board");
        introduction.add("  help - open the full spellbook");
        introduction.add("");
        introduction.add("Type a command below, and I will handle the busywork with a little magic.");
        return introduction.toString();
    }

    /** Returns the introduction to a task-list response. */
    public static String taskListIntro() {
        return "Behold, your current constellation of quests:";
    }

    /** Returns the introduction to a search response. */
    public static String findIntro() {
        return "The search sprite discovered these matching quests:";
    }

    /** Returns the response shown after adding a task. */
    public static String addedTaskIntro(Task task) {
        if (task instanceof Deadline) {
            return "A time-sensitive quest has joined the expedition:";
        }
        if (task instanceof Event) {
            return "An appointment has been pinned to the adventure map:";
        }
        return "A new side quest has been tucked into your satchel:";
    }

    /** Returns the response shown after changing a task's completion state. */
    public static String markedTaskIntro(boolean isMarking) {
        return isMarking
                ? "Victory! One chore has been banished to the realm of Done:"
                : "The quest has returned from retirement:";
    }

    /** Returns the response shown after deleting a task. */
    public static String deletedTaskIntro() {
        return "Plucked from the quest board and sent to the archives:";
    }

    /** Returns the response shown after updating a tag. */
    public static String updatedTagIntro(boolean isAdding, String tag) {
        return (isAdding ? "The tagging sprite added " : "The tagging sprite removed ")
                + "#" + tag + " from this quest.";
    }

    /** Returns the introduction to the help response. */
    public static String helpIntro() {
        return "The spellbook is open. Here are the available incantations:";
    }

    /** Returns the goodbye response. */
    public static String goodbye() {
        return "Farewell, brave planner! May your to-do list remain enchantingly short.";
    }

    /** Returns the response for an unknown command. */
    public static String unknownCommand() {
        return "OOPS!!! The command pixie could not decipher that spell."
                + System.lineSeparator()
                + "Type 'help' to open the spellbook and see the commands I understand.";
    }

    /** Returns the response for a command that received unexpected arguments. */
    public static String unexpectedArguments(String commandWord) {
        return "OOPS!!! The " + commandWord + " spell does not accept any extra ingredients.";
    }

    /** Returns the response for a duplicate task. */
    public static String duplicateTask() {
        return "OOPS!!! That quest is already in your satchel.";
    }

    /** Returns the response for a failed storage operation. */
    public static String storageFailure(String message) {
        return "OOPS!!! " + message;
    }

    /** Returns the response for a task without a description. */
    public static String emptyDescription(String taskType) {
        return "OOPS!!! Even the quest pixie needs a description for your " + taskType + ".";
    }

    /** Returns the response for a deadline without its date marker. */
    public static String missingDeadline(String description) {
        return "OOPS!!! The deadline quest needs a date, e.g. 'deadline "
                + description + " /by Sunday'.";
    }

    /** Returns the response for an event without complete time markers. */
    public static String missingEventTimes(String description) {
        return "OOPS!!! The event quest needs a start and end, e.g. 'event "
                + description + " /from Monday 2pm /to 4pm'.";
    }

    /** Returns the response for a tag command without both required values. */
    public static String missingTag() {
        return "OOPS!!! The tagging sprite needs a task number and a tag.";
    }

    /** Returns the response for a tag with invalid characters. */
    public static String invalidTag() {
        return "OOPS!!! Tags may contain only letters, numbers, underscores, and hyphens.";
    }

    /** Returns the response for a command without a task number. */
    public static String missingTaskNumber(String commandWord) {
        return "OOPS!!! The quest pixie needs to know which task number to " + commandWord + ".";
    }

    /** Returns the response for a task number with an invalid format. */
    public static String invalidTaskNumber() {
        return "OOPS!!! Task numbers must be whole numbers, brave planner.";
    }

    /** Returns the response for a task number that is not on the quest board. */
    public static String missingTaskNumber(int taskNumber) {
        return "OOPS!!! There is no quest number " + taskNumber + " on your quest board.";
    }

    /** Returns the response for a full quest board. */
    public static String fullQuestBoard() {
        return "OOPS!!! Your quest board is full; there is no room for another adventure.";
    }

    /** Returns the response for an invalid date value. */
    public static String invalidDate(String fieldName) {
        return "OOPS!!! The " + fieldName + " must be a valid date, such as 2026-09-20.";
    }

    /** Returns the response for an event with reversed dates. */
    public static String reversedEventDates() {
        return "OOPS!!! An event's start date must be before its end date.";
    }
}

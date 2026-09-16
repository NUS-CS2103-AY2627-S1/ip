package brucli.ui;

import java.util.Random;

/**
 * Provides all user-facing text produced by BruCLI.
 */
public final class Messages {
    private static final Random RANDOM = new Random();
    private static final String BANNER =
            ".@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n"
            + ".@...................@......@.............@@\n"
            + ".@..................@@......@@............@@\n"
            + ".@...................@@......@@...........@@\n"
            + ".@....................@@@.....@@@@@@......@@\n"
            + ".@.............................@@@@@@.....@@\n"
            + ".@....................@@@...........@@....@@\n"
            + ".@...................@@@.............@@@..@@\n"
            + ".@..................@@.................@@.@@\n"
            + ".@................@@@...@..........@@@.@@.@@\n"
            + ".@.......@@..@@@@@@...@.@.........@@@@@@@.@@\n"
            + ".@.....@@@@@@@@....@@@..@........@@...@@..@@\n"
            + ".@.....@......@@@@@@...@@........@........@@\n"
            + ".@.....@@@@@@@@@@....@@@.........@@@......@@\n"
            + ".@................@@@@.............@@.....@@\n"
            + ".@..............@@@@................@@@...@@\n"
            + ".@.............@@@...................@@@@.@@\n"
            + ".@...........@@@..........@@@..........@@@@@\n"
            + ".@..........@@@.........@@@@@@@@.........@@@\n"
            + ".@.........@@........@@@@@...@@@@.........@@\n"
            + ".@........@@........@@@.........@.........@@\n"
            + ".@.......@@.......@@@...........@........@@@\n"
            + ".@......@@.......@@@............@.......@@@@\n"
            + ".@......@@.....@@@.............@@......@@.@@\n"
            + ".@.....@@....@@@@..............@@..@@@@@..@@\n"
            + ".@.....@..@@@@@................@...@.@@...@@\n"
            + ".@..@@@@..@@@..................@@..@@.....@@\n"
            + ".@@@@...@@.......................@..@@....@@\n"
            + ".@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n"
            + "+------------------------------------------+\n"
            + "|   ____              ____ _     ___       |\n"
            + "|  | __ ) _ __ _   _ / ___| |   |_ _|      |\n"
            + "|  |  _ \\| '__| | | | |   | |    | |       |\n"
            + "|  | |_) | |  | |_| | |___| |___ | |       |\n"
            + "|  |____/|_|   \\__,_|\\____|_____|___|      |\n"
            + "|                 BruCLI                   |\n"
            + "+------------------------------------------+";

    private static final String[] WELCOME_MESSAGES = {
        "Empty your mind. What task shall we master today?",
        "Knowing is not enough, we must execute. Ready when you are.",
        "Adapt to the workflow. How can BruCLI assist your setup?",
        "Be formless, shapeless—like input. Type your command to begin.",
        "I fear not the user who runs 10,000 commands once, but the user "
                    + "who masters one command 10,000 times. Welcome back."
    };
    private static final String[] GOODBYE_MESSAGES = {
        "Do not pray for an easy runtime, pray for the strength to endure "
                    + "complex tasks. Farewell!",
        "Absorb what was useful, discard what was useless. Session closed.",
        "Be water, my friend... until the next execution.",
        "Task applied. Action completed. Walk on!",
        "Laser-like focus maintained to the end. See you next time.",
        "Keep practicing the fundamentals. Session closed."
    };
    private static final String[] TODO_MESSAGES = {
        "A goal is not always meant to be reached, it often serves simply "
                    + "as something to aim at. Task added.",
        "Do not turn away from the workload. Record your target.",
        "Notice that the stiffest tree is most easily cracked. Break your "
                    + "goal down into a task.",
        "To heavy minds, a task is a burden; to a warrior, it is an "
                    + "objective. Logging todo.",
        "Real living is living for others—and keeping track of your commitments."
    };
    private static final String[] DEADLINE_MESSAGES = {
        "Time waits for no process. Deadline anchored.",
        "To control time is to control oneself. Target date set.",
        "The quiet before the storm is preparation. Time limit registered.",
        "Do not let tomorrow steal the energy of today. Target set.",
        "A deadline sharpens the edge of intent. Date locked."
    };
    private static final String[] EVENT_MESSAGES = {
        "Be present in the moment, but map the ground ahead. Event scheduled.",
        "Flow into the schedule without friction. Time entry created.",
        "Preparation is the root of fluid action. Calendar updated.",
        "A warrior moves with rhythm, not chaos. Event locked.",
        "Honor the commitment of time. Marker placed on the schedule."
    };
    private static final String[] LIST_MESSAGES = {
        "Clear vision precedes effective action. Fetching your active inventory:",
        "Look closely at what remains; simplify to move forward:",
        "To know oneself is to study one's open commitments in action:",
        "Unclutter your view to sharpen your focus. Displaying tasks:",
        "Review your path without judgment, then strike again:"
    };
    private static final String[] MARK_MESSAGES = {
        "Strike complete! Task conquered.",
        "One clean move—item resolved.",
        "Offense turns into defense, intent turns into completion. Marked isDone!",
        "Shattered through the obstacle. Task marked complete.",
        "Execution without hesitation. Done!"
    };
    private static final String[] UNMARK_MESSAGES = {
        "The opponent rises again; face it with renewed energy. Task reopened.",
        "Flexibility allows a warrior to reset position. Task unmarked.",
        "Do not fear stepping back to build stronger momentum. Status restored.",
        "Flow backward, correct posture, strike again. Task reactivated.",
        "No motion is wasted if intent remains clear. Task restored."
    };
    private static final String[] DELETE_MESSAGES = {
        "Stripped away the unnecessary. Erased!",
        "Purge the dead weight to keep your form light and agile. Deleted.",
        "Severed from the record.",
        "Simplicity is the key to brilliance. Task eliminated.",
        "Cast aside what no longer serves the objective. Removed."
    };
    private static final String[] UNKNOWN_MESSAGES = {
        "Unfocused energy yields no force. Command not recognized.",
        "A strike without direction misses the target. Check your syntax.",
        "If you push against the wall, the wall pushes back. Invalid input.",
        "Refine your stance; BruCLI does not understand this motion.",
        "Do not strike blindly in the dark. Type a valid command."
    };
    private static final String[] GAME_MESSAGES = {
        "Your opponent is procrastination. Round one begins now.",
        "Secret technique unlocked: finish one small task.",
        "The dojo has no save point. Keep moving!",
        "Combo move: focus, execute, then take a snack break."
    };
    private static final String[] BRUCE_LEE_SOUNDS = {
        "*HI-YA!*",
        "*WATAAAH!*",
        "*HOOO-AAAH!*",
        "*HYAA-TCHAA!*",
        "*WHACK*"
    };

    private Messages() {
    }

    /**
     * Returns BruCLI's opening banner.
     */
    public static String banner() {
        return BANNER;
    }

    /**
     * Returns a welcome message with a short getting-started guide.
     */
    public static String welcome() {
        return "Welcome to BruCLI, your task-management dojo!\n"
                + randomMessage(WELCOME_MESSAGES) + "\n\n"
                + "Type a command and press Enter to begin. Try:\n"
                + "todo Read a chapter - add your first task\n"
                + "list - see your tasks and their numbers\n"
                + "mark 1 - complete task 1\n\n"
                + "Type help for all commands and date examples.";
    }

    /**
     * Returns a randomized goodbye message.
     */
    public static String goodbye() {
        return randomMessage(GOODBYE_MESSAGES);
    }

    /**
     * Returns a randomized todo-added confirmation.
     */
    public static String todoAdded() {
        return randomMessage(TODO_MESSAGES);
    }

    /**
     * Returns a randomized deadline-added confirmation.
     */
    public static String deadlineAdded() {
        return randomMessage(DEADLINE_MESSAGES);
    }

    /**
     * Returns a randomized event-added confirmation.
     */
    public static String eventAdded() {
        return randomMessage(EVENT_MESSAGES);
    }

    /**
     * Returns a randomized introduction to a task listing.
     */
    public static String listHeader() {
        return randomMessage(LIST_MESSAGES);
    }

    /**
     * Returns a randomized task-marked confirmation.
     */
    public static String taskMarked() {
        return randomMessage(MARK_MESSAGES);
    }

    /**
     * Returns a randomized task-unmarked confirmation.
     */
    public static String taskUnmarked() {
        return randomMessage(UNMARK_MESSAGES);
    }

    /**
     * Returns a randomized task-deleted confirmation.
     */
    public static String taskDeleted() {
        return randomMessage(DELETE_MESSAGES);
    }

    /**
     * Returns a randomized response for an unknown command.
     */
    public static String unknownCommand() {
        return randomMessage(UNKNOWN_MESSAGES);
    }

    /**
     * Returns the response for an unsupported sudo command.
     */
    public static String sudoDenied() {
        return "Power without discipline is no power at all. Sudo is unavailable.";
    }

    /**
     * Returns the response shown when saved tasks cannot be loaded.
     */
    public static String loadingError() {
        return "The stance was broken: saved tasks could not be loaded.";
    }

    /**
     * Returns the response shown when tasks cannot be saved.
     */
    public static String savingError() {
        return "The strike did not land: your tasks could not be saved.";
    }

    /**
     * Returns the response shown when no tasks match a request.
     */
    public static String noMatchingTasks() {
        return "The path is clear—no tasks match your request.";
    }

    /**
     * Returns the response shown when there are no tasks to sort.
     */
    public static String noTasksToSort() {
        return "An empty dojo needs no order—there are no tasks to sort.";
    }

    /**
     * Introduces tasks returned by a keyword search.
     *
     * @param taskList Formatted tasks or a no-matches response.
     */
    public static String findResults(String taskList) {
        return "Focus reveals the target. Here are the matching tasks:\n" + taskList;
    }

    /**
     * Returns a randomized response for the game command.
     */
    public static String gameResponse() {
        return randomMessage(GAME_MESSAGES);
    }

    /**
     * Returns the validation message for a todo without a description.
     */
    public static String todoDescriptionRequired() {
        return "A strike needs a target. Usage: todo DESCRIPTION";
    }

    /**
     * Returns the validation message for a deadline without a by marker.
     */
    public static String deadlineByRequired() {
        return "A deadline needs direction. Add /by before its date and time.";
    }

    /**
     * Returns the deadline command usage message.
     */
    public static String deadlineUsage() {
        return "Set your target clearly. Usage: deadline DESCRIPTION /by yyyy-MM-dd HHmm";
    }

    /**
     * Returns the event command usage message.
     */
    public static String eventUsage() {
        return "Move with clear timing. Usage: event DESCRIPTION "
                + "/from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm";
    }

    /**
     * Returns the date-filtered list command usage message.
     */
    public static String listUsage() {
        return "Choose one direction. Usage: list BEFORE|AFTER yyyy-MM-dd HHmm";
    }

    /**
     * Returns the validation message for an unsupported list filter.
     */
    public static String invalidListFilter() {
        return "Your direction must be precise: choose BEFORE or AFTER.";
    }

    /**
     * Returns the validation message for a command missing its task number.
     *
     * @param commandName Name of the command requiring a task number.
     */
    public static String taskNumberRequired(String commandName) {
        return "A strike needs a target. " + commandName + " needs a task number.";
    }

    /**
     * Returns the validation message for a nonnumeric task number.
     */
    public static String taskNumberMustBeNumeric() {
        return "Focus your aim: the task number must be numeric.";
    }

    /**
     * Returns the validation message for a task number below one.
     */
    public static String taskNumberMustBePositive() {
        return "Begin at the first step: the task number must be at least 1.";
    }

    /**
     * Returns the validation message for an invalid task number.
     */
    public static String taskNotFound() {
        return "That target is beyond reach: the task does not exist.";
    }

    /**
     * Returns the required user-facing date-time format.
     */
    public static String invalidDateTime() {
        return "Time demands precision. Use yyyy-MM-dd HHmm "
                + "(for example, 2026-08-26 1830).";
    }

    /**
     * Returns the diagnostic for an invalid stored date-time.
     *
     * @param text Invalid value read from storage.
     */
    public static String invalidStoredDateTime(String text) {
        return "Invalid date and time in save file: " + text;
    }

    /**
     * Returns the diagnostic for a malformed task data file.
     */
    public static String malformedTaskFile() {
        return "Task data file is malformed.";
    }

    /**
     * Returns the diagnostic for an unsupported stored task type.
     *
     * @param type Unsupported task type marker.
     */
    public static String unknownTaskType(String type) {
        return "Unknown task type: " + type;
    }

    /**
     * Formats a response using BruCLI's standard prompt layout.
     *
     * @param message Response body to format.
     */
    public static String formatResponse(String message) {
        return "BruCLI |\n     " + message.replace("\n", "\n     ");
    }

    /**
     * Returns supported commands and examples for the help response.
     */
    public static String help() {
        return "BruCLI command guide\n\n"
                + "todo DESCRIPTION - add a task\n"
                + "deadline DESCRIPTION /by yyyy-MM-dd HHmm - add a deadline\n"
                + "event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm - add an event\n"
                + "list - show all tasks and their numbers\n"
                + "list BEFORE|AFTER yyyy-MM-dd HHmm - filter dated tasks\n"
                + "find KEYWORD - search task descriptions\n"
                + "mark NUMBER - mark a task as done\n"
                + "unmark NUMBER - reopen a task\n"
                + "delete NUMBER - delete a task\n"
                + "help - show this guide\n"
                + "game - get a playful message\n"
                + "sudo - get BruCLI's response to elevated privileges\n"
                + "bye - say goodbye (exits the terminal app)\n\n"
                + "Replace uppercase placeholders with your own values.\n"
                + "Use task numbers from list, starting at 1.\n"
                + "Dates use 24-hour time, for example:\n"
                + "deadline Submit assignment /by 2026-09-30 1800\n"
                + "event Study group /from 2026-09-30 1400 /to 2026-09-30 1600";
    }

    private static String randomMessage(String[] messages) {
        return String.format(
                "%s %s",
                BRUCE_LEE_SOUNDS[RANDOM.nextInt(BRUCE_LEE_SOUNDS.length)],
                messages[RANDOM.nextInt(messages.length)]
        );
    }
}

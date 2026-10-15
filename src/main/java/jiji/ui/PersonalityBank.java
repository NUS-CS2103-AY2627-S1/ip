package jiji.ui;

import java.util.List;
import java.util.Random;

import jiji.task.Task;

/**
 * Repository of cozy, comforting feline statements and templates used by Jiji.
 * Supports randomized selection for interactive experiences and canonical selection
 * for deterministic automated testing.
 */
public final class PersonalityBank {

    private static final List<String> GREETINGS = List.of(
            "Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ\n"
                    + "What shall we gently tackle together today?",
            "A soft stretch and a cozy purr... Hello there! ₍^. .^₎☕\n"
                    + "Grab a warm drink, and tell me how I can help today.",
            "Warm head-bumps! Jiji is right here beside you ₍^._.^₎\n"
                    + "Ready whenever you are. What tasks are on your mind?",
            "Purr-fect timing! Curled up and ready to lend a paw ₍^ ᵕ ᵕ ^₎\n"
                    + "Let's take things one cozy step at a time.",
            "Meow... Good to see you! The blanket is warm and I'm all ears ₍^._.^₎ฅ\n"
                    + "What can I look after for you today?"
    );

    private static final List<String> WELCOME_LINES = List.of(
            "Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ",
            "What shall we gently tackle together today?"
    );

    private static final List<String> TASK_ADDED_TEMPLATES = List.of(
            "Tucked away safely! I've nestled this task into your list:\n  %s\n"
                    + "That makes %d tasks in our cozy bundle. ₍^. .^₎",
            "Gently noted! I've placed this task in our basket:\n  %s\n"
                    + "Now you have %d tasks to tend to. ₍^ ᵕ ᵕ ^₎",
            "Purr... I've got this one written down for you:\n  %s\n"
                    + "Your list now holds %d tasks. Take your time!",
            "Soft paws on it! Wrapped this into your checklist:\n  %s\n"
                    + "Now you have %d tasks in your list. ₍^._.^₎☕",
            "Cozy and secured! Added this to our day:\n  %s\n"
                    + "We're taking care of %d tasks together."
    );

    private static final List<String> TASK_MARKED_TEMPLATES = List.of(
            "Paws up! Marked this task as done:\n  %s\n"
                    + "Wonderful job! Time for a gentle stretch. ₍^ ᵕ ᵕ ^₎ฅ",
            "All wrapped up warm and tidy! Marked as completed:\n  %s\n"
                    + "Purr... you're doing splendidly! ₍^. .^₎",
            "Checked off with a soft paw-tap! Marked as done:\n  %s\n"
                    + "Rest your eyes for a moment, you've earned it.",
            "Finished and filed away! Marked as done:\n  %s\n"
                    + "Soft purrs of appreciation for your hard work ₍^._.^₎",
            "Another one completed! Marked as done:\n  %s\n"
                    + "Slow and steady wins the cozy race ₍^ ᵕ ᵕ ^₎"
    );

    private static final List<String> TASK_UNMARKED_TEMPLATES = List.of(
            "No hurry at all! I've marked this task as pending again:\n  %s\n"
                    + "We'll get back to it when you're ready. ₍^. .^₎",
            "Gently set back on the to-do cushion! Marked as not done yet:\n  %s\n"
                    + "Take all the time you need.",
            "Unwrapped and kept warm for later! Marked as not done:\n  %s\n"
                    + "No pressure, friend ₍^ ᵕ ᵕ ^₎",
            "Understood! Re-opened this task with care:\n  %s\n"
                    + "One little step at a time.",
            "Paws down for now! Marked this task as not done yet:\n  %s\n"
                    + "We'll curl up with it later."
    );

    private static final List<String> TASK_REMOVED_TEMPLATES = List.of(
            "Gently cleared away! I've removed this task:\n  %s\n"
                    + "That leaves %d cozy tasks in your list. ₍^. .^₎",
            "Swept off with a soft tail-flick! Removed:\n  %s\n"
                    + "Now you have %d tasks remaining. Breathe easy!",
            "All cleared! I've lifted this task off your shoulders:\n  %s\n"
                    + "Now you have %d tasks in your basket. ₍^ ᵕ ᵕ ^₎",
            "Softly let go! Removed this task:\n  %s\n"
                    + "Only %d tasks left to look after.",
            "Tidied up nicely! Removed from your list:\n  %s\n"
                    + "Your list now has %d tasks remaining."
    );

    private static final List<String> GOODBYES = List.of(
            "Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ",
            "Curling up by the warm hearth for a catnap. Goodbye for now! ₍^. .^₎☕",
            "Stay cozy and take good care of yourself! Meow for now! ₍^._.^₎ฅ",
            "May your day be filled with warm tea and peaceful moments. Farewell! ₍^ ᵕ ᵕ ^₎",
            "Soft purrs until next time! Jiji will be right here waiting. ₍^._.^₎"
    );

    private static final List<String> EMPTY_SCHEDULE_TEMPLATES = List.of(
            "No tasks scheduled for %s. A purr-fectly peaceful day to rest! ₍^ ᵕ ᵕ ^₎",
            "Your calendar is clear on %s! Time to brew some tea and bask in the sun. ₍^. .^₎☕",
            "Nothing on the agenda for %s. Enjoy a quiet, cozy day! ₍^._.^₎"
    );

    private static final Random RANDOM = new Random();
    private static boolean isRandomized = false;

    private PersonalityBank() {
    }

    /**
     * Enables or disables randomized selection from the statement banks.
     *
     * @param randomized True to enable random selections; false for canonical deterministic outputs.
     */
    public static void setRandomized(boolean randomized) {
        isRandomized = randomized;
    }

    /**
     * Returns whether statement selection is currently randomized.
     *
     * @return True if randomized; false if deterministic.
     */
    public static boolean isRandomized() {
        return isRandomized;
    }

    /**
     * Returns a greeting string from the greeting bank.
     *
     * @return A greeting message.
     */
    public static String getGreeting() {
        return isRandomized ? getRandomElement(GREETINGS) : GREETINGS.get(0);
    }

    /**
     * Returns the canonical welcome lines for CLI startup.
     *
     * @return List of welcome strings.
     */
    public static List<String> getWelcomeLines() {
        return WELCOME_LINES;
    }

    /**
     * Formats confirmation for an added task.
     *
     * @param task The task that was added.
     * @param totalTasks Total number of tasks in the list.
     * @return Formatted confirmation string.
     */
    public static String formatTaskAdded(Task task, int totalTasks) {
        String template = isRandomized ? getRandomElement(TASK_ADDED_TEMPLATES) : TASK_ADDED_TEMPLATES.get(0);
        return String.format(template, task, totalTasks);
    }

    /**
     * Formats confirmation for marking a task as done.
     *
     * @param task The marked task.
     * @return Formatted confirmation string.
     */
    public static String formatTaskMarked(Task task) {
        String template = isRandomized ? getRandomElement(TASK_MARKED_TEMPLATES) : TASK_MARKED_TEMPLATES.get(0);
        return String.format(template, task);
    }

    /**
     * Formats confirmation for marking a task as incomplete.
     *
     * @param task The unmarked task.
     * @return Formatted confirmation string.
     */
    public static String formatTaskUnmarked(Task task) {
        String template = isRandomized ? getRandomElement(TASK_UNMARKED_TEMPLATES) : TASK_UNMARKED_TEMPLATES.get(0);
        return String.format(template, task);
    }

    /**
     * Formats confirmation for removing a task.
     *
     * @param task The removed task.
     * @param totalTasks Total number of remaining tasks.
     * @return Formatted confirmation string.
     */
    public static String formatTaskRemoved(Task task, int totalTasks) {
        String template = isRandomized ? getRandomElement(TASK_REMOVED_TEMPLATES) : TASK_REMOVED_TEMPLATES.get(0);
        return String.format(template, task, totalTasks);
    }

    /**
     * Formats a farewell message.
     *
     * @return Formatted farewell string.
     */
    public static String formatGoodbye() {
        return isRandomized ? getRandomElement(GOODBYES) : GOODBYES.get(0);
    }

    /**
     * Formats notice when no tasks are scheduled for a date.
     *
     * @param dateDisplay Formatted date string.
     * @return Formatted notice string.
     */
    public static String formatEmptySchedule(String dateDisplay) {
        String template = isRandomized ? getRandomElement(EMPTY_SCHEDULE_TEMPLATES) : EMPTY_SCHEDULE_TEMPLATES.get(0);
        return String.format(template, dateDisplay);
    }

    private static <T> T getRandomElement(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }
}

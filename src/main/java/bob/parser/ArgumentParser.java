package bob.parser;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;

import bob.BobException;
import bob.command.CommandWord;
import bob.task.TaskDateTime;

/**
 * Parses the single value that follows the command word of a command taking a task
 * number, a day, a count or a keyword.
 *
 * <p>Such a value has no markers in it, so the only mistakes to catch are that
 * nothing was typed, or that what was typed is not the kind of value the command
 * needs. Whether the value makes sense against the task list is a fact about the
 * list rather than about the text, so that check belongs to the command, which is
 * given the list. Both kinds of complaint read alike to the user: {@code mark seven}
 * is refused here, while {@code mark 7} with four tasks is refused by
 * {@link bob.command.TaskNumberCommand TaskNumberCommand}.
 */
class ArgumentParser {

    /** How many tasks {@link #NEXT_EXAMPLE} asks for. */
    private static final int NEXT_EXAMPLE_COUNT = 3;

    /** Example of a well-formed {@link CommandWord#NEXT} command, shown when one is malformed. */
    private static final String NEXT_EXAMPLE =
            CommandWord.NEXT.getKeyword() + " " + NEXT_EXAMPLE_COUNT;

    /** Example of a well-formed {@link CommandWord#FIND} command, shown when one is malformed. */
    private static final String FIND_EXAMPLE = CommandWord.FIND.getKeyword() + " book";

    /**
     * Returns the task number the user typed, counting from 1 to match the
     * numbering shown by {@link CommandWord#LIST}.
     *
     * <p>Only the mistakes that are visible in the text itself are caught here:
     * nothing typed after the command word, several numbers where one was wanted,
     * a number too big to be a task number, and something typed that is not a
     * number at all. Whether a number that <em>is</em> a task number names a task
     * the user has is left to the command, which is the one holding the list.
     *
     * <p>A number too big for an {@code int} is refused rather than read as the
     * largest one that fits. No list is that long, so the user has most likely
     * typed a key too many, and telling them so is more use than "I don't have a
     * task numbered 2147483647".
     *
     * <p>The command is passed as a {@link CommandWord} rather than as its keyword, so
     * a caller cannot name a command in the message that does not exist. The
     * keyword is read off it here, where the message is written.
     *
     * @param taskNumberText the task number as the user typed it.
     * @param command        the command to name in any error message.
     * @return the number typed, which may still be larger than the list.
     * @throws BobException if no task number was given, several were, or what was
     *                      given is too big to be a task number or is not a number.
     */
    static int parseTaskNumber(String taskNumberText, CommandWord command) throws BobException {
        String word = command.getKeyword();
        String listCommand = CommandWord.LIST.getKeyword();
        if (taskNumberText.isEmpty()) {
            throw new BobException("Which task should I " + word + "?"
                    + "\nGive me its number from " + listCommand + ", for example: "
                    + word + " 2");
        }

        // Split at the same whitespace that CommandWord accepts after a keyword.
        String[] words = taskNumberText.split("\\p{javaWhitespace}+");
        if (words.length > 1 && Arrays.stream(words).allMatch(ArgumentParser::isDigitsOnly)) {
            throw BobException.withExample("I can only " + word + " one task at a time.",
                    word + " " + words[0]);
        }

        try {
            // The user is free to type anything after the command word, so a
            // number is asked for again rather than allowed to crash the chatbot.
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            String problem = isDigitsOnly(taskNumberText)
                    ? taskNumberText + " is too big to be a task number."
                    : "\"" + taskNumberText + "\" isn't a task number.";
            throw new BobException(problem
                    + "\nI need the number shown next to the task in " + listCommand
                    + ", for example: " + word + " 2");
        }
    }

    /**
     * Returns the day the user asked about, having checked that they named one and
     * that it is a day this chatbot can read.
     *
     * <p>{@link CommandWord#ON}, {@link CommandWord#BEFORE} and {@link CommandWord#AFTER} all
     * take a day and reject the same two mistakes, so the checking is written here
     * once. Only the example differs, and it is built from the command that asked,
     * so each of the three is shown its own.
     *
     * @param dayText the day as the user typed it after the command word.
     * @param command the command that asked, used to write its example.
     * @return the day that text names.
     * @throws BobException if nothing was typed after the command word, or what was
     *                      typed is not a day.
     */
    static LocalDate parseDay(String dayText, CommandWord command) throws BobException {
        if (dayText.isEmpty()) {
            throw BobException.withExample("Which day should I look at?", getDayExample(command));
        }
        return TaskDateTime.parseDay(dayText);
    }

    /**
     * Returns how many tasks {@link CommandWord#NEXT} was asked for, having checked
     * that the user named a number and that it is a number of tasks worth showing.
     *
     * <p>Zero and negative numbers are refused rather than quietly showing nothing,
     * since a user who typed one has misunderstood the command and would learn
     * nothing from an empty answer.
     *
     * <p>A count too big for an {@code int} is not refused, unlike a task number
     * that big. Asking for more tasks than the list holds already shows all of
     * them, so the largest count that fits is taken instead, and asks for the same.
     *
     * <p>The count is read as a {@link BigInteger} so that no number the user can
     * type is too long to be recognised as one. Read as an {@code int}, a number
     * past the end of the range was told it was not a number at all, which is
     * plainly false and left {@code next -99999999999} answered differently from
     * {@code next -5}.
     *
     * @param countText the count as the user typed it after {@code next}.
     * @return how many tasks to show, always one or more.
     * @throws BobException if nothing was typed after {@code next}, or what was
     *                      typed is not a whole number, or is less than one.
     */
    static int parseCount(String countText) throws BobException {
        if (countText.isEmpty()) {
            throw BobException.withExample("How many tasks should I show?", NEXT_EXAMPLE);
        }
        BigInteger count;
        try {
            // The user is free to type anything after the command word, so a
            // number is asked for again rather than allowed to crash the chatbot.
            count = new BigInteger(countText);
        } catch (NumberFormatException e) {
            throw BobException.withExample("\"" + countText + "\" isn't a number of tasks.",
                    NEXT_EXAMPLE);
        }
        if (count.signum() < 1) {
            throw BobException.withExample("I can show you one task or more, but not " + count + ".",
                    NEXT_EXAMPLE);
        }
        return count.min(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
    }

    /**
     * Returns the text {@link CommandWord#FIND} was asked to search for, having
     * checked that the user typed something to search for at all.
     *
     * <p>Everything after the command word is the keyword, spaces and all, so a
     * user may search for a phrase such as {@code find sports club}. Nothing else
     * is checked: any text at all is a search someone might mean, and one that
     * matches nothing is answered by the command rather than refused here.
     *
     * @param keywordText the text as the user typed it after {@code find}.
     * @return the text to look for in each description.
     * @throws BobException if nothing was typed after {@code find}.
     */
    static String parseKeyword(String keywordText) throws BobException {
        if (keywordText.isEmpty()) {
            throw new BobException("What should I look for?"
                    + "\nGive me a word from the task, for example: " + FIND_EXAMPLE);
        }
        return keywordText;
    }

    /**
     * Returns a well-formed use of a command that takes a day, for example
     * {@code before 2026-12-02}.
     *
     * <p>Built from the command word rather than written out once per command,
     * since the three commands that take a day are written alike. A constant
     * apiece would be three chances for one of them to be forgotten when a fourth
     * such command is added.
     */
    private static String getDayExample(CommandWord command) {
        return command.getKeyword() + " " + TaskDateTime.EXAMPLE_DATE;
    }

    /**
     * Returns whether {@code text} is made only of the digits 0 to 9.
     *
     * <p>Such text is a whole number even when {@link Integer#parseInt} refuses it,
     * which happens when the number is too big for an {@code int}. Telling that
     * apart from text that is no number at all is what lets the two be answered
     * differently. A sign is not allowed, so a hugely negative number is still
     * answered as not being a task number rather than as too big to be one.
     */
    private static boolean isDigitsOnly(String text) {
        return text.matches("[0-9]+");
    }
}

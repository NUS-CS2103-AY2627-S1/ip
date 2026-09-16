package bags.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import bags.exception.BagsException;

/**
 * Represents a task with a deadline.
 */
public class Deadline extends Task {

    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy h:mma", Locale.ENGLISH);

    private final LocalDateTime deadline;
    private final String formattedDeadline;

    /**
     * Creates a deadline task.
     *
     * @param description text describing the task
     * @param deadline deadline string in {@code yyyy-MM-dd HH:mm} format
     * @throws BagsException if the deadline format is invalid
     */
    public Deadline(String description, String deadline) throws BagsException {
        super(description, TaskType.DEADLINE);
        assert description != null : "Task description must not be null";
        assert deadline != null : "Deadline must not be null";

        try {
            this.deadline = LocalDateTime.parse(deadline, INPUT_FORMATTER);
            this.formattedDeadline = this.deadline.format(OUTPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BagsException(
                    "Oops! Invalid date. Use YYYY-MM-DD HH:MM in 24-hour time.");
        }
    }

    /**
     * Creates a deadline task from an add-task command string.
     *
     * @param command user input after {@code deadline}
     * @return the created deadline task
     * @throws BagsException if the description or deadline is missing, or the format is invalid
     */
    public static Deadline createTask(String command) throws BagsException {
        assert command != null : "Command string must not be null";

        String[] words = command.split(" ");
        if (words.length < 2) {
            throw new BagsException("Oops! Missing task description. Add some details after deadline.");
        }

        int byIndex = findByIndex(words);
        if (byIndex == -1) {
            throw new BagsException("Hmm, Missing /by. Add /by <end date> to your deadline.");
        }

        String description = buildText(words, 1, byIndex);
        if (description.isEmpty()) {
            throw new BagsException("Oops! Missing task description. Add some details after deadline.");
        }

        String deadlineInfo = buildText(words, byIndex + 1, words.length);
        if (deadlineInfo.isEmpty()) {
            throw new BagsException("Hmm, I need a deadline after /by. Add /by <deadline> after the task name.");
        }

        return new Deadline(description, deadlineInfo);
    }

    /**
     * Finds the position of the {@code /by} keyword in user command.
     *
     * @param words the command words
     * @return the index of {@code /by}, or {@code -1} if it is absent
     */
    private static int findByIndex(String[] words) {
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("/by")) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Builds a string literal from a list of words.
     *
     * @param words the referenced word list
     * @param startIndex the inclusive starting index
     * @param endIndex the exclusive ending index
     * @return the combined text
     */
    private static String buildText(String[] words, int startIndex, int endIndex) {
        StringBuilder text = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            text.append(words[i]).append(" ");
        }
        return text.toString().trim();
    }

    public LocalDateTime getDeadline() {
        return this.deadline;
    }

    /**
     * Returns the deadline task in a user-readable format.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return "[D][" + super.getStatusIcon() + "] " + super.getDescription()
                + " (by: " + formattedDeadline + ")";
    }

    /**
     * Converts the deadline task into the format used for saving to storage.
     *
     * @return the deadline task in its stored format
     */
    @Override
    public String parseEvent() {
        return "D | " + "[" + getStatusIcon() + "] | " + description + " | "
                + deadline.format(INPUT_FORMATTER);
    }
}

package odysseus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Converts user command text into task operations and task values.
 */
public class Parser {
    private static final String DEFAULT_TOPIC = "Odyssey";

    /**
     * Parses a learning card from a learn command.
     */
    public LearningCard parseLearningCard(String command) throws OdysseusException {
        int answerIndex = command.indexOf(" /answer ");
        if (answerIndex < 0) {
            throw new OdysseusException("A learning card needs /answer <answer>. Try: learn nostos"
                    + " /answer homecoming.");
        }
        int topicIndex = command.indexOf(" /topic ", answerIndex + 9);
        String prompt = command.substring("learn".length(), answerIndex).trim();
        String answer = command.substring(answerIndex + " /answer ".length(),
                topicIndex < 0 ? command.length() : topicIndex).trim();
        String topic = topicIndex < 0 ? DEFAULT_TOPIC
                : command.substring(topicIndex + " /topic ".length()).trim();
        if (prompt.isBlank() || answer.isBlank() || topic.isBlank()) {
            throw new OdysseusException("A learning card needs a prompt, answer, and non-empty topic.");
        }
        return new LearningCard(prompt, answer, topic);
    }

    /**
     * Parses an optional topic following a learning-list or review command.
     */
    public String parseOptionalTopic(String command, String commandWord) {
        String topic = command.substring(commandWord.length()).trim();
        return topic.isEmpty() ? null : topic;
    }

    /**
     * Parses the non-empty answer following an answer command.
     */
    public String parseAnswer(String command) throws OdysseusException {
        String answer = command.substring("answer".length()).trim();
        if (answer.isEmpty()) {
            throw new OdysseusException("Offer an answer after answer, or use reveal to see Athena's answer.");
        }
        return answer;
    }

    /**
     * Parses the one-based learning-card number after forget.
     */
    public int parseCardNumber(String command) throws OdysseusException {
        String numberText = command.substring("forget".length()).trim();
        if (numberText.isEmpty()) {
            throw new OdysseusException("Name the card to forget, for example: forget 2.");
        }
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new OdysseusException("Use a card number after forget, for example: forget 2.");
        }
    }

    /**
     * Parses the non-empty keyword that follows a find command.
     *
     * @param command complete user command.
     * @return keyword to search for.
     * @throws OdysseusException if the command does not contain a keyword.
     */
    public String parseFindKeyword(String command) throws OdysseusException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new OdysseusException("Provide a keyword after find, for example: find book.");
        }
        return keyword;
    }

    /**
     * Parses the one-based task number that follows an existing-task action.
     *
     * @param command complete user command.
     * @param action action that owns the command prefix.
     * @return validated one-based task number.
     * @throws OdysseusException if no valid task number follows the action.
     */
    public int parseTaskNumber(String command, TaskAction action) throws OdysseusException {
        String actionWord = action.getCommandWord();
        String numberText = command.substring(actionWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new OdysseusException("Name the task to " + actionWord + ", for example: " + actionWord + " 2.");
        }
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new OdysseusException("Use a task number after " + actionWord
                    + ", for example: " + actionWord + " 2.");
        }
    }

    /**
     * Parses the date portion of a deadline, accepting an optional trailing time.
     *
     * @param by user-provided deadline value.
     * @return date represented by the deadline.
     */
    private LocalDate parseDeadlineDate(String by) {
        assert !by.isBlank() : "Deadline text must be present after command validation";
        String trimmed = by.trim();
        String dateText = trimmed;
        if (trimmed.contains(" ")) {
            dateText = trimmed.split("\\s+")[0];
        } else if (trimmed.contains("T")) {
            dateText = trimmed.substring(0, trimmed.indexOf('T'));
        }
        return LocalDate.parse(dateText);
    }

    /**
     * Creates a task from a supported creation command.
     *
     * @param command complete user command.
     * @return task described by the command.
     * @throws OdysseusException if the command is malformed or unsupported.
     */
    public Task parseTask(String command) throws OdysseusException {
        if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.length() == 4 ? "" : command.substring(5);
            if (description.isBlank()) {
                throw new OdysseusException("A to-do needs a task to steer by. Try: todo borrow book.");
            }
            return new Todo(description);
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            int byIndex = command.indexOf(" /by ");
            if (byIndex < 0) {
                throw new OdysseusException("A deadline needs /by <date or time>. Try: deadline return"
                        + " book /by Sunday.");
            }
            String description = command.substring(9, byIndex);
            String by = command.substring(byIndex + 5);
            if (description.isBlank() || by.isBlank()) {
                throw new OdysseusException("A deadline needs a description and a date.");
            }
            try {
                return new Deadline(description, parseDeadlineDate(by));
            } catch (DateTimeParseException exception) {
                throw new OdysseusException("Deadline dates use yyyy-MM-dd, for example: 2019-10-15.");
            }
        }
        if (command.equals("event") || command.startsWith("event ")) {
            int fromIndex = command.indexOf(" /from ");
            int toIndex = command.indexOf(" /to ");
            if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
                throw new OdysseusException("An event needs /from <start> /to <end>. Try: event meeting"
                        + " /from 2pm /to 4pm.");
            }
            if (toIndex <= fromIndex + 7) {
                throw new OdysseusException("Give both an event start after /from and an end after /to.");
            }
            String description = command.substring(6, fromIndex);
            String from = command.substring(fromIndex + 7, toIndex);
            String to = command.substring(toIndex + 5);
            if (description.isBlank() || from.isBlank() || to.isBlank()) {
                throw new OdysseusException("Give an event description, start, and end.");
            }
            return new Event(description, from, to);
        }
        throw new OdysseusException("I cannot chart a course from that command. Try todo, deadline,"
                + " event, learn, review, cards, list, mark, unmark, or bye.");
    }
}

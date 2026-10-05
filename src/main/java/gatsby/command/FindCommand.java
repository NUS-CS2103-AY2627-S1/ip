package gatsby.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gatsby.exception.EmptyPayloadException;
import gatsby.exception.GatsbyException;
import gatsby.model.Task;
import gatsby.model.TaskList;
import gatsby.ui.Ui;

/**
 * Finds tasks whose descriptions contain all user-supplied query words.
 */
public class FindCommand extends Command {

    /** The keyword to search for in each task description. */
    private final String payload;

    /**
     * Creates a find command for the supplied keyword.
     *
     * @param payload the keyword entered after {@code find}
     */
    public FindCommand(String payload) {
        this.payload = payload == null ? "" : payload.strip();
    }

    /**
     * Searches the task descriptions and displays the matching tasks.
     *
     * The search uses the task description only, so task metadata such as
     * deadline dates and event times is not considered. Exact word matches are
     * displayed before partial matches, while each group keeps task-list order.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     * @throws GatsbyException when the search keyword is empty
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws GatsbyException {
        if (payload.isBlank()) {
            throw new EmptyPayloadException("OOPS! How do I even find nothing??");
        }

        List<String> queryWords = tokenize(payload);
        List<Integer> exactMatches = new ArrayList<>();
        List<Integer> partialMatches = new ArrayList<>();

        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            String normalizedDescription = normalize(task.getTaskName());
            List<String> descriptionWords = tokenize(normalizedDescription);
            if (!containsAllQueryWords(normalizedDescription, queryWords)) {
                continue;
            }
            if (containsAllExactWords(descriptionWords, queryWords)) {
                exactMatches.add(index);
            } else {
                partialMatches.add(index);
            }
        }

        List<Integer> matches = new ArrayList<>(exactMatches);
        matches.addAll(partialMatches);

        if (matches.isEmpty()) {
            ui.printLine(" No matching tasks found :(");
        } else {
            ui.printLine(" Here are the matching tasks in your list (numbers refer to your full task list):");
            for (int index : matches) {
                ui.printLine(" " + (index + 1) + ". " + tasks.get(index));
            }
        }
    }

    /**
     * Normalizes text for case-insensitive matching and consistent whitespace handling.
     *
     * @param text the text to normalize
     * @return the normalized text
     */
    private static String normalize(String text) {
        return text.strip().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    /**
     * Splits normalized text into whitespace-separated words.
     *
     * @param text the text to tokenize
     * @return the text's words
     */
    private static List<String> tokenize(String text) {
        String normalizedText = normalize(text);
        return List.of(normalizedText.split(" "));
    }

    /**
     * Checks whether every query word occurs as a substring in the description.
     *
     * @param description the normalized description
     * @param queryWords the normalized query words
     * @return true when all query words occur in the description
     */
    private static boolean containsAllQueryWords(String description, List<String> queryWords) {
        return queryWords.stream().allMatch(description::contains);
    }

    /**
     * Checks whether every query word occurs as a complete description word.
     *
     * @param descriptionWords the normalized description words
     * @param queryWords the normalized query words
     * @return true when all query words have exact word matches
     */
    private static boolean containsAllExactWords(List<String> descriptionWords,
                                                 List<String> queryWords) {
        return queryWords.stream().allMatch(descriptionWords::contains);
    }
}

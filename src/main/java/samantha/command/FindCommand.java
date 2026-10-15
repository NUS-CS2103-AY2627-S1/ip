package samantha.command;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import samantha.model.Note;
import samantha.model.Task;

/**
 * Represents the command that finds tasks and notes containing a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command for the supplied keyword.
     *
     * @param keyword keyword to search for in task descriptions or note content
     */
    public FindCommand(String keyword) {
        this.keyword = keyword.toLowerCase(Locale.ROOT);
    }

    /**
     * Displays tasks and notes whose text contains the search keyword.
     *
     * @param context current application state and persistence handlers
     * @return matching tasks and notes
     */
    @Override
    public String execute(CommandContext context) {
        List<Task> taskSnapshot = context.getTasks().asList();
        String matchingTasks = IntStream.range(0, taskSnapshot.size())
                .filter(index -> containsKeyword(taskSnapshot.get(index)))
                .mapToObj(index -> String.format("%d. %s", index + 1, taskSnapshot.get(index)))
                .collect(Collectors.joining("\n"));
        List<Note> noteSnapshot = context.getNotes().asList();
        String matchingNotes = IntStream.range(0, noteSnapshot.size())
                .filter(index -> containsKeyword(noteSnapshot.get(index)))
                .mapToObj(index -> String.format("%d. %s", index + 1, noteSnapshot.get(index)))
                .collect(Collectors.joining("\n"));
        String message = formatSection("Here are the matching tasks in your list:", matchingTasks)
                + "\n\n"
                + formatSection("Here are the matching notes in your list:", matchingNotes);
        return message.stripTrailing();
    }

    /**
     * Checks whether a task description contains this command's keyword.
     *
     * @param task task whose description should be checked
     * @return {@code true} if the description contains the keyword, ignoring case
     */
    private boolean containsKeyword(Task task) {
        return task.getTaskName().toLowerCase(Locale.ROOT).contains(keyword);
    }

    /**
     * Checks whether note content contains this command's keyword.
     *
     * @param note note whose content should be checked
     * @return {@code true} if the content contains the keyword, ignoring case
     */
    private boolean containsKeyword(Note note) {
        return note.getContent().toLowerCase(Locale.ROOT).contains(keyword);
    }

    /**
     * Formats a search result section without adding a trailing line for an empty result.
     *
     * @param heading section heading
     * @param matchingLines matching item lines
     * @return formatted search result section
     */
    private String formatSection(String heading, String matchingLines) {
        return matchingLines.isEmpty() ? heading : heading + "\n" + matchingLines;
    }
}

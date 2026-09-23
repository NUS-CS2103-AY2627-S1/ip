package sallman.command;

import java.util.List;
import java.util.Locale;

import sallman.Parser;
import sallman.SallmanException;
import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;
import sallman.task.Task;

/**
 * Attaches labels to a task, or takes them off again.
 * <p>
 * Tagging and untagging differ only in what they do with each label and in the
 * wording of the reply, so one command covers both.
 */
public class TagCommand extends Command {

    /** Whether this attaches the tags, rather than removing them. */
    private final boolean isAddingTags;

    /** The task number and tags the user gave, checked when the command runs. */
    private final String arguments;

    /**
     * Creates a command that tags or untags the task named.
     *
     * @param isAddingTags true to attach the tags, false to remove them
     * @param arguments    text the user typed after the command
     */
    public TagCommand(boolean isAddingTags, String arguments) {
        this.isAddingTags = isAddingTags;
        this.arguments = arguments;
    }

    /**
     * Changes the tags on the numbered task, reports it, and saves the list.
     * <p>
     * The list is only saved when something actually changed, so re-adding a
     * tag a task already has does not rewrite the file.
     *
     * @param tasks   the list holding the task
     * @param ui      used to confirm the change
     * @param storage used to save the changed list
     * @throws SallmanException if the number or the tags cannot be read, the
     *                          number is outside the list, or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SallmanException {
        // The keyword is quoted back in the error messages, so it has to match
        // whichever of the two commands the user actually typed.
        String keyword = isAddingTags
                ? CommandType.TAG.getKeyword()
                : CommandType.UNTAG.getKeyword();
        String[] parts = Parser.splitTaskNumberAndTags(keyword, arguments);
        int index = Parser.parseTaskNumber(keyword, parts[0], tasks.size());
        List<String> tags = Parser.parseTags(keyword, parts[1]);

        Task task = tasks.get(index);
        // Worked out before anything is changed, so that a command which turns
        // out to be a no-op neither saves the file nor leaves a state that undo
        // would restore to no visible effect.
        boolean hasChanged = tags.stream()
                .anyMatch(tag -> task.getTags().contains(tag.toLowerCase(Locale.ROOT)) != isAddingTags);

        if (hasChanged) {
            tasks.saveSnapshot();
            for (String tag : tags) {
                if (isAddingTags) {
                    task.addTag(tag);
                } else {
                    task.removeTag(tag);
                }
            }
        }

        ui.showTagged(task, isAddingTags, hasChanged);
        if (hasChanged) {
            storage.save(tasks.asList());
        }
    }
}

package bibi.command;

import bibi.Storage;
import bibi.Ui;
import bibi.task.TaskList;

/**
 * Answers a standalone greeting or thanks without changing the task list.
 *
 * <p>The parser recognizes the whole input first, so words inside a task remain
 * task text. These short replies keep the conversation on the command workflow.
 */
public class SocialCommand extends Command {
    private final boolean isThanks;

    /**
     * Creates a reply for one of the small set of supported social inputs.
     *
     * @param isThanks whether the user thanked Bibi rather than greeted it
     */
    public SocialCommand(boolean isThanks) {
        this.isThanks = isThanks;
    }

    /**
     * Replies briefly without saving or changing any tasks.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (isThanks) {
            ui.showMessage("You're welcome. You handle the doing; I'll handle the remembering.");
        } else {
            ui.showMessage("Hey. I'm here. Type help if you need the rundown.");
        }
    }
}

package ff15.command;

import java.util.List;

import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.Task;
import ff15.task.TaskList;

/** Shows the tasks whose description contains a keyword the user asked about. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that will report the tasks matching one keyword.
     *
     * @param keyword the text to look for in each task description.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Shows the tasks matching the keyword, or says there are none. */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        List<Task> matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            ui.showMessage("Nothing matching '" + keyword + "'. I looked. I looked so hard.");
        } else {
            ui.showTaskList("Found them. I'm basically a detective. Michael Scarn:", matches);
        }
    }
}

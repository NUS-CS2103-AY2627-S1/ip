package trybot.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import trybot.task.Task;
import trybot.task.TaskList;

/**
 * Formats friendly graphical replies and tracks errors independently of their wording.
 */
public class ChatUi extends Ui {
    private final ByteArrayOutputStream buffer;
    private final PrintStream output;
    private boolean hasError;
    private boolean hasWarning;

    /**
     * Creates an empty reply collector for one command.
     */
    public ChatUi() {
        this(new ByteArrayOutputStream());
    }

    private ChatUi(ByteArrayOutputStream buffer) {
        super(new PrintStream(buffer, true, StandardCharsets.UTF_8),
                new PrintStream(buffer, true, StandardCharsets.UTF_8));
        this.buffer = buffer;
        output = new PrintStream(buffer, true, StandardCharsets.UTF_8);
    }

    @Override
    public void showLine() {
        // Conversation cards provide separation without console divider characters.
    }

    @Override
    public void showError(String message) {
        hasError = true;
        output.println("No worries! " + message);
        output.println("You've got this! Let's try again, or choose Commands for an example.");
    }

    @Override
    public void showSavingError() {
        hasWarning = true;
        output.println("You made progress, but I couldn't save it just now. "
                + "Your changes are still here for this session.");
    }

    @Override
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Wonderful! I've added this to your list:");
        output.println(task);
        output.println("You now have " + taskCount + (taskCount == 1 ? " task." : " tasks."));
        output.println("You're off to a great start! Want to give it a tag? Try: tag " + taskCount
                + " school (or a tag of your own).");
    }

    @Override
    public void showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            output.println("Your list is a fresh start—what's one exciting thing you'd like to get done?");
            output.println("Try: todo read a chapter");
            return;
        }
        output.println("Here we go! Here's what we've got on your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + ". " + tasks.get(i));
        }
        output.println("Ready for a win? When you finish something, tell me with mark <number>!");
    }

    @Override
    public void showTaskMarkedDone(Task task) {
        output.println("Fantastic job! You did it—one more thing checked off:");
        output.println(task);
        output.println("You should feel proud! Keep that momentum going!");
    }

    @Override
    public void showTaskMarkedNotDone(Task task) {
        output.println("No worries—every step counts. I've put this back on your to-do list:");
        output.println(task);
        output.println("You can come back to it whenever you're ready!");
    }

    @Override
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("Great reset! I've removed this task:");
        output.println(task);
        output.println("You now have " + taskCount + (taskCount == 1 ? " task" : " tasks")
                + ". Keep going—you've got this!");
    }

    @Override
    public void showMatchingTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            output.println("No worries! Nothing matches that keyword yet. Keep trying—you'll find it!");
            return;
        }

        output.println("Great searching! Here are the matching tasks:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.println((i + 1) + ". " + matchingTasks.get(i));
        }
        output.println("You've got a clear view now—what would you like to tackle first?");
    }

    @Override
    public void showTagCreated(int taskNumber, String tagName) {
        output.println("Awesome! I've tagged task " + taskNumber + " with #" + tagName + ".");
        output.println("Your list is getting nicely organised! What would you like to try next?");
    }

    @Override
    public void showTagsDeleted(int taskNumber) {
        output.println("All set! I cleared the tags from task " + taskNumber + ".");
        output.println("A clean slate can feel great—what's your next move?");
    }

    @Override
    public void showHelp() {
        output.println("Absolutely! Here are some ways I can help you make progress:");
        output.println("help: help (shows this list of commands)");
        output.println("todo: todo <task> (adds a todo task to the list)");
        output.println("deadline: deadline <task> /by <date or time> (adds a deadline task)");
        output.println("event: event <task> /from <start> /to <end> (adds an event task)");
        output.println("list: list (shows all tasks)");
        output.println("find: find <keyword> (finds tasks containing the keyword)");
        output.println("tag: tag <number> <tag name> (attaches a tag to a task)");
        output.println("tagdel: tagdel <number> (removes all tags from a task)");
        output.println("mark: mark <number> (marks a task as done)");
        output.println("unmark: unmark <number> (marks a task as not done)");
        output.println("delete: delete <number> (deletes a task)");
        output.println("bye: bye (exits TryBot)");
        output.println("Give one a try—you might surprise yourself!");
    }

    @Override
    public void showGoodbyeMessage() {
        output.println("You did great today! Thanks for sharing your to-do list with me. See you soon!");
    }

    public String getText() {
        return buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n").strip();
    }

    public boolean hasError() {
        return hasError;
    }

    public boolean hasWarning() {
        return hasWarning;
    }
}

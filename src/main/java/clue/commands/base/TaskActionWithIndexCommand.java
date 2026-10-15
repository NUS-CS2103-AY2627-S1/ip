package clue.commands.base;

import java.util.Map;

import clue.components.Parser;
import clue.components.TaskList;
import clue.tasks.Task;
import clue.ui.Ui;

/** A command which asks the user to select a task by index and do something with that task */
public abstract class TaskActionWithIndexCommand implements Parser.Command {
    protected final TaskList tasks;
    private final String promptText;
    private final String successMessage;

    /**
     * Creates a command handler that takes a task by index and does something with it.
     *
     * @param tasks the TaskList that this command operates on
     * @param promptText the prompt text to show when index is missing
     * @param successMessage the message to show after successful action
     */
    public TaskActionWithIndexCommand(TaskList tasks, String promptText, String successMessage) {
        this.tasks = tasks;
        this.promptText = promptText;
        this.successMessage = successMessage;
    }

    /** The action to perform on the task */
    protected abstract void action(Task task, int index);

    /** What to do after a successful action, by default nothing */
    protected void after(Ui ui) {}

    /** Finds the task selected by the user, and give it to other methods */
    @Override
    public boolean run(Ui ui, Map<String, String> args) {
        if (args.isEmpty()) {
            ui.println("Please tell us " + promptText + "!");
            return true;
        }

        String indexInput = args.get(Parser.DEFAULT_ARGUMENT);
        if (indexInput == null || indexInput.trim().isEmpty()) {
            ui.println("Please tell us " + promptText + "!");
            return true;
        }

        int idx = getIndexFromInput(ui, indexInput);
        if (idx != -1) {
            Task t = tasks.get(idx);
            this.action(t, idx);
            ui.println(successMessage);
            ui.print("\t");
            ui.println(t);
            this.after(ui);
        }
        return true;
    }

    private int getIndexFromInput(Ui ui, String unparsedInt) {
        String trimmed = unparsedInt.trim();
        if (trimmed.isEmpty()) {
            ui.println("Invalid task number! Please provide a positive integer!");
            return -1;
        }

        int idx;
        try {
            idx = Integer.parseInt(trimmed) - 1;
        } catch (NumberFormatException e) {
            ui.println("Invalid task number! Please provide a positive integer!");
            return -1;
        }
        if (idx < 0) {
            ui.println("Invalid task number! Please provide a positive integer!");
        } else if (idx >= tasks.size()) {
            ui.println("Invalid task number! Not enough recorded tasks!");
        } else {
            return idx;
        }
        return -1;
    }
}

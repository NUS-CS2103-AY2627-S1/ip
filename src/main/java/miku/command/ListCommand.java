package miku.command;

/** Displays the current task list. */
public class ListCommand extends Command {
    /** Displays the current task list without changing it. */
    @Override
    public String execute(CommandContext commandContext) {
        return commandContext.responseFormatter().formatTaskList(commandContext.tasks());
    }
}

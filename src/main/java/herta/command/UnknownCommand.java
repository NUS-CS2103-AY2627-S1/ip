package herta.command;

import herta.exception.HertaException;
import herta.exception.UsageGuidanceException;
import herta.storage.Storage;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents an empty or unrecognized user command.
 */
public class UnknownCommand extends Command {
    private static final String EMPTY_COMMAND_ERROR = "Nothing? Use a command. "
            + "Try: list, find <keyword>, or todo <description>.";
    private static final String UNKNOWN_COMMAND_ERROR = "That command isn't in my vocabulary.\n"
            + "Use: todo, deadline, event, list, find, filter, upcoming, sort, "
            + "mark, unmark, delete, archive, archived, restore, and bye.";
    private final boolean isEmptyInput;

    /**
     * Creates a command that reports the appropriate error for the input.
     *
     * @param input the complete user input.
     */
    public UnknownCommand(String input) {
        isEmptyInput = input.isEmpty();
    }

    /**
     * Rejects the input with the same explanation used by the original dispatcher.
     *
     * @param tasks unused because the input is not a supported task command.
     * @param ui unused because errors are propagated to the main loop.
     * @param storage unused because the input does not change stored data.
     * @throws HertaException describing why the input was rejected.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) throws HertaException {
        if (isEmptyInput) {
            throw new HertaException(EMPTY_COMMAND_ERROR);
        }
        throw new UsageGuidanceException(UNKNOWN_COMMAND_ERROR);
    }
}

package shrekanddonkey.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import shrekanddonkey.exception.ShrekAndDonkeyException;
import shrekanddonkey.parser.Parser;
import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.Deadline;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Represents the command that adds a deadline task.
 */
public class DeadlineCommand extends Command {
    private final String arguments;

    /**
     * Creates a deadline command with its raw description and deadline argument.
     *
     * @param arguments text following the {@code deadline} command.
     */
    public DeadlineCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Adds a deadline task, or displays an error when the input is invalid.
     *
     * @param tasks application's task list.
     * @param ui application's user interface.
     * @param storage application's file storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        assert tasks != null : "TaskList collaborator cannot be null";
        assert ui != null : "Ui collaborator cannot be null";
        assert storage != null : "Storage collaborator cannot be null";
        assert arguments != null : "Command arguments cannot be null";
        int byMarkerIndex = arguments.indexOf("/by");
        if (byMarkerIndex < 0) {
            ui.showError(" Please specify a deadline using /by.");
            ui.showDivider();
            return;
        }

        try {
            String description = arguments.substring(0, byMarkerIndex).trim();
            if (description.isEmpty()) {
                throw new ShrekAndDonkeyException("deadline");
            }
            String deadlineText = arguments.substring(byMarkerIndex + "/by".length()).trim();
            LocalDateTime deadline = Parser.parseDateTime(deadlineText);
            assert deadline != null : "Parsed deadline cannot be null";
            tasks.add(new Deadline(description, deadline));
            ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
        } catch (ShrekAndDonkeyException e) {
            ui.showError("OOPS!!UWU description of a " + e.getMessage() + " cannot be empty UwU");
        } catch (DateTimeParseException e) {
            ui.showError(" Please enter a valid deadline date (yyyy-MM-dd, yyyy-MM-dd HH:mm, "
                    + "or d/M/yyyy HHmm).");
        }
        ui.showDivider();
    }

    @Override
    public boolean modifiesTasks() {
        return true;
    }
}

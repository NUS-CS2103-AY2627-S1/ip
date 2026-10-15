package shrekanddonkey.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import shrekanddonkey.exception.ShrekAndDonkeyException;
import shrekanddonkey.parser.Parser;
import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.Event;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Represents the command that adds an event task.
 */
public class EventCommand extends Command {
    private final String arguments;

    /**
     * Creates an event command with its raw description and time arguments.
     *
     * @param arguments text following the {@code event} command.
     */
    public EventCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Adds an event task, or displays an error when the input is invalid.
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
        int fromMarkerIndex = arguments.indexOf("/from");
        int toMarkerIndex = arguments.indexOf("/to", fromMarkerIndex + "/from".length());
        try {
            if (fromMarkerIndex < 0 || toMarkerIndex < 0) {
                ui.showError(" Please specify an event using /from and /to.");
            } else {
                String description = arguments.substring(0, fromMarkerIndex).trim();
                if (description.isEmpty()) {
                    throw new ShrekAndDonkeyException("event");
                }
                String startText = arguments.substring(
                        fromMarkerIndex + "/from".length(), toMarkerIndex).trim();
                String endText = arguments.substring(toMarkerIndex + "/to".length()).trim();
                LocalDateTime start = Parser.parseDateTime(startText);
                LocalDateTime end = Parser.parseDateTime(endText);
                if (end.isBefore(start)) {
                    ui.showError(" Please ensure the event end is not before its start.");
                } else {
                    assert start != null && end != null : "Parsed event dates cannot be null";
                    assert !end.isBefore(start) : "Event end time cannot be before start time";
                    tasks.add(new Event(description, start, end));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                }
            }
        } catch (ShrekAndDonkeyException e) {
            ui.showError("OOPS!!UWU description of a " + e.getMessage() + " cannot be empty UwU");
        } catch (DateTimeParseException e) {
            ui.showError(" Please enter valid event dates (yyyy-MM-dd, yyyy-MM-dd HH:mm, "
                    + "or d/M/yyyy HHmm).");
        }
        ui.showDivider();
    }

    @Override
    public boolean modifiesTasks() {
        return true;
    }
}

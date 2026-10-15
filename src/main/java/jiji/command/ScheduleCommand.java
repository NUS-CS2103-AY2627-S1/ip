package jiji.command;

import java.time.LocalDate;

import jiji.storage.Storage;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Command that displays the schedule of tasks occurring on or due by a specific date.
 */
public class ScheduleCommand extends Command {

    private final LocalDate targetDate;

    /**
     * Constructs a ScheduleCommand for the given target date.
     *
     * @param targetDate The date to view scheduled tasks for.
     */
    public ScheduleCommand(LocalDate targetDate) {
        assert targetDate != null : "Target date cannot be null";
        this.targetDate = targetDate;
    }

    /**
     * Returns the target date of this schedule command.
     *
     * @return The target date.
     */
    public LocalDate getTargetDate() {
        return targetDate;
    }

    /**
     * Executes the schedule command by formatting and displaying tasks scheduled on the target date.
     *
     * @param tasks The task list.
     * @param ui The UI handler for displaying output.
     * @param storage The storage handler.
     * @return Formatted schedule string.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        assert ui != null : "Ui instance cannot be null";
        assert tasks != null : "TaskList cannot be null";

        String scheduleText = ui.formatSchedule(tasks, targetDate);
        ui.showSchedule(scheduleText);
        return scheduleText;
    }
}

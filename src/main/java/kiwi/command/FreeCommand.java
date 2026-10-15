package kiwi.command;

import java.time.LocalDate;
import java.util.Optional;

import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.FreeSlot;
import kiwi.task.TaskList;

/**
 * Finds the nearest work day with a free slot of a given length.
 */
public class FreeCommand extends Command {
    private final int hours;
    private final LocalDate startDate;

    /**
     * Creates a command that searches for a free slot of {@code hours}.
     *
     * @param hours length of the slot in hours.
     * @param startDate first day to search, or {@code null} to use today at execute time.
     */
    public FreeCommand(int hours, LocalDate startDate) {
        assert hours >= 1 && hours <= FreeSlot.WORK_DAY_HOURS
                : "Parser already rejects hours that do not fit a work day";
        this.hours = hours;
        this.startDate = startDate;
    }

    /**
     * Shows the nearest matching free slot, or explains that none was found.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        LocalDate from = startDate != null ? startDate : LocalDate.now();
        Optional<FreeSlot> slot = tasks.findNearestFreeSlot(hours, from);
        if (slot.isEmpty()) {
            ui.showNoFreeSlot(hours);
        } else {
            ui.showFreeSlot(hours, slot.get());
        }
    }
}

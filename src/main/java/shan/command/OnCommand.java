package shan.command;

import java.time.LocalDate;

import shan.datetime.DateTimeParser;
import shan.storage.Storage;
import shan.task.Task;
import shan.task.TaskList;

/**
 * Displays deadlines and events occurring within an inclusive date range.
 */
public class OnCommand extends Command {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Constructs a command that searches the supplied inclusive date range.
     *
     * @param startDate First date in the range.
     * @param endDate   Last date in the range.
     */
    public OnCommand(LocalDate startDate, LocalDate endDate) {
        assert startDate != null : "Range start date must not be null";
        assert endDate != null : "Range end date must not be null";
        assert !endDate.isBefore(startDate) : "Range end date must not be before its start date";
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Displays dated tasks that overlap this command's inclusive date range.
     *
     * @param tasks   Task list to search.
     * @param storage Storage collaborator; unused because searching does not persist changes.
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        boolean isRange = !this.startDate.equals(this.endDate);
        String displayStartDate = DateTimeParser.formatDateForDisplay(this.startDate);
        String displayEndDate = DateTimeParser.formatDateForDisplay(this.endDate);
        String resultHeading = isRange
                ? String.format("Here are the deadlines and events from %s to %s:",
                        displayStartDate, displayEndDate)
                : String.format("Got your deadlines and events on %s:", displayStartDate);
        StringBuilder result = new StringBuilder(resultHeading);
        int matchCount = 0;
        for (int taskNumber = 1; taskNumber <= tasks.size(); taskNumber++) {
            Task task = tasks.get(taskNumber);
            if (task.occursBetween(this.startDate, this.endDate)) {
                result.append(String.format("\n%d.%s", taskNumber, task));
                matchCount++;
            }
        }

        if (matchCount == 0) {
            return isRange
                    ? String.format("There are no deadlines or events from %s to %s.",
                            displayStartDate, displayEndDate)
                    : String.format("There are no deadlines or events on %s.", displayStartDate);
        }
        return result.toString();
    }
}

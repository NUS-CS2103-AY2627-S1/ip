package luckynoslacky.luckycommand;

import java.time.LocalDateTime;

import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckytask.TaskMaster;

/**
 * Represents a command that searches tasks by description, date, or both.
 */
public class LuckyNoFindCommand extends LuckyNoCommand {
    private final String descriptionQuery;
    private final LocalDateTime dateTimeQuery;
    private final TaskMaster taskMaster;

    /**
     * Creates a date-only find command with a task master.
     *
     * @param dateTimeQuery date and time from the search query
     * @param taskMaster task master to search
     * @throws IllegalArgumentException if {@code taskMaster} is null
     */
    public LuckyNoFindCommand(
            LocalDateTime dateTimeQuery,
            TaskMaster taskMaster) {
        this(null, dateTimeQuery, taskMaster);
    }

    /**
     * Creates a find command with optional description and date filters.
     * At least one filter must be supplied.
     *
     * @param descriptionQuery optional text to search for in task descriptions
     * @param dateTimeQuery optional date and time from the search query
     * @param taskMaster task master to search
     * @throws IllegalArgumentException if the query is empty or
     *                                  {@code taskMaster} is null
     */
    public LuckyNoFindCommand(
            String descriptionQuery,
            LocalDateTime dateTimeQuery,
            TaskMaster taskMaster) {
        if ((descriptionQuery == null || descriptionQuery.isBlank())
                && dateTimeQuery == null) {
            throw new IllegalArgumentException("Find query cannot be empty.");
        }
        this.descriptionQuery = descriptionQuery;
        this.dateTimeQuery = dateTimeQuery;
        this.taskMaster = requireTaskMaster(taskMaster);
    }

    /**
     * Searches the task master and returns the matching task response.
     *
     * @return search response
     */
    @Override
    protected ResponseContent executeContent() {
        return LuckyNoTaskResponses.listed(
                taskMaster.findTasks(descriptionQuery, dateTimeQuery));
    }

    /**
     * Returns the information tone for the search response.
     *
     * @return information response tone
     */
    @Override
    protected ResponseTone responseTone() {
        return ResponseTone.INFO;
    }

}

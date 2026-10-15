package jiji.command;

import java.time.LocalDate;
import java.util.Locale;

import jiji.storage.Storage;
import jiji.task.Deadline;
import jiji.task.Event;
import jiji.task.TaskList;
import jiji.task.Todo;
import jiji.ui.Ui;

/**
 * Command that displays overall progress, completion rates, and task breakdown statistics.
 */
public class StatsCommand extends Command {

    private final LocalDate referenceDate;

    /**
     * Constructs a StatsCommand with the specified reference date for overdue calculations.
     *
     * @param referenceDate The reference date to evaluate overdue deadlines against.
     */
    public StatsCommand(LocalDate referenceDate) {
        this.referenceDate = (referenceDate != null) ? referenceDate : LocalDate.now();
    }

    /**
     * Constructs a StatsCommand using today's date as the reference date.
     */
    public StatsCommand() {
        this(LocalDate.now());
    }

    /**
     * Executes the stats command by computing statistics from the task list and displaying them via the UI.
     *
     * @param tasks The task list.
     * @param ui The UI handler for displaying output.
     * @param storage The storage handler.
     * @return The formatted statistics dashboard string.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        assert ui != null : "Ui instance cannot be null";
        assert tasks != null : "TaskList cannot be null";

        String statsText = generateStats(tasks);
        ui.showStats(statsText);
        return statsText;
    }

    /**
     * Generates the formatted statistics dashboard text.
     *
     * @param tasks The task list to compute metrics from.
     * @return Formatted statistics string.
     */
    public String generateStats(TaskList tasks) {
        if (tasks.isEmpty()) {
            return "Task Statistics & Insights:\n\n"
                    + "• No tasks in your list yet!\n"
                    + "• Use 'todo', 'deadline', or 'event' to add tasks.";
        }

        int total = tasks.size();
        int completed = tasks.countCompleted();
        int pending = tasks.countPending();
        double completionRate = (total > 0) ? ((double) completed / total) * 100.0 : 0.0;

        int totalTodos = tasks.countByType(Todo.class);
        int completedTodos = tasks.countCompletedByType(Todo.class);

        int totalDeadlines = tasks.countByType(Deadline.class);
        int completedDeadlines = tasks.countCompletedByType(Deadline.class);
        int overdueDeadlines = tasks.countOverdueDeadlines(referenceDate);

        int totalEvents = tasks.countByType(Event.class);
        int completedEvents = tasks.countCompletedByType(Event.class);

        return String.format(Locale.ENGLISH,
                "Task Statistics & Insights:\n\n"
                + "[Overall Progress]\n"
                + "• Total tasks: %d\n"
                + "• Completed: %d (%.1f%%)\n"
                + "• Pending: %d\n\n"
                + "[Breakdown by Type]\n"
                + "• ToDos: %d (%d completed)\n"
                + "• Deadlines: %d (%d completed, %d overdue)\n"
                + "• Events: %d (%d completed)\n\n"
                + "Tip: Use 'list pending' to view only incomplete tasks!",
                total, completed, completionRate, pending,
                totalTodos, completedTodos,
                totalDeadlines, completedDeadlines, overdueDeadlines,
                totalEvents, completedEvents);
    }
}

package minty.command;

import java.time.LocalDate;
import java.util.List;

import minty.storage.Storage;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Displays derived reminders without changing tasks or storage.
 */
public class RemindersCommand extends Command {
    private final LocalDate today;

    /**
     * Creates a reminder query starting on the specified date.
     *
     * @param today first date in the seven-date window.
     */
    public RemindersCommand(LocalDate today) {
        this.today = today;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        showReminders(tasks.findReminders(today), ui);
    }

    /**
     * Displays a reminder snapshot in the shared console and chat response format.
     *
     * @param reminders sorted tasks with their original numbers.
     * @param ui response destination.
     */
    public static void showReminders(List<TaskList.NumberedTask> reminders, Ui ui) {
        ui.showMessage("Heads up! Here are your reminders for today and the next 6 days:");
        for (TaskList.NumberedTask reminder : reminders) {
            ui.showNumberedTask(reminder.number(), reminder.task());
        }
        if (reminders.isEmpty()) {
            ui.showMessage("No incomplete deadlines or events in this period. Stay fresh!");
        }
    }
}

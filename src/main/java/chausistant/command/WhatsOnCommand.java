package chausistant.command;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import chausistant.storage.Storage;
import chausistant.task.DeadlineTask;
import chausistant.task.EventTask;
import chausistant.task.Task;
import chausistant.task.TaskList;
import chausistant.ui.Ui;

/** Command that shows events and deadlines relevant to one calendar date. */
public class WhatsOnCommand extends Command {
    private final LocalDate date;

    /** Creates a command that displays work scheduled on the supplied date. */
    public WhatsOnCommand(LocalDate date) {
        this.date = date;
    }

    /** Displays events and deadlines that occur on the requested date. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> taskSnapshot = tasks.getTasks();
        List<EventTask> events = taskSnapshot.stream()
                .filter(EventTask.class::isInstance)
                .map(EventTask.class::cast)
                .filter(event -> !event.getFrom().toLocalDate().isAfter(date)
                        && !event.getTo().toLocalDate().isBefore(date))
                .sorted(Comparator.comparing(EventTask::getFrom))
                .toList();
        List<DeadlineTask> deadlines = taskSnapshot.stream()
                .filter(DeadlineTask.class::isInstance)
                .map(DeadlineTask.class::cast)
                .filter(deadline -> deadline.getDeadline().toLocalDate().equals(date))
                .sorted(Comparator.comparing(DeadlineTask::getDeadline))
                .toList();

        List<String> eventDetails = events.stream().map(EventTask::printTask).toList();
        List<String> deadlineDetails = deadlines.stream().map(DeadlineTask::printTask).toList();
        ui.showSchedule(Task.formatDateForDisplay(date), eventDetails, deadlineDetails);
    }
}

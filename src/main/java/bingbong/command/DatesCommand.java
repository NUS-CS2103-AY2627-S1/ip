package bingbong.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import bingbong.exception.BingBongException;
import bingbong.storage.Storage;
import bingbong.task.Deadline;
import bingbong.task.Event;
import bingbong.task.Task;
import bingbong.task.TaskList;
import bingbong.ui.Ui;

/**
 * bingbong.command.Command to query tasks occurring on a specific date.
 */
public class DatesCommand extends Command {

    private final String in;

    /**
     * Initialises a date-matching query execution command wrapper.
     *
     * @param in The command line user input.
     */
    public DatesCommand(String in) {
        this.in = in;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingBongException {
        if (in.length() <= 6 || in.substring(5).trim().isEmpty()) {
            throw new BingBongException("BingBong only recognises 'DD/MM/YYYY'!");
        }
        try {
            LocalDate queryDate = LocalDate.parse(in.substring(6).trim(),
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            ui.print("BingBong search results for " + in.substring(6).trim() + ":");
            boolean hasMatches = false;
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                boolean isMatch = (
                        task instanceof Deadline && ((Deadline) task).getDueDate().equals(queryDate) || (
                                task instanceof Event && ((Event) task).isOccuringOn(queryDate)));
                if (isMatch) {
                    ui.print(" -" + task);
                    hasMatches = true;
                }
            }
            if (!hasMatches) {
                ui.print("BingBong does not find any task on this date!");
            }
        } catch (DateTimeParseException e) {
            throw new BingBongException("BingBong only recognises 'DD/MM/YYYY'!");
        }
    }
}
